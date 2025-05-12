package com.jocata.loansystem.service.serviceImpl;

import com.jocata.loansystem.dao.CreditScoreDao;
import com.jocata.loansystem.dao.CustomerDao;
import com.jocata.loansystem.dao.LoanApplicationDao;
import com.jocata.loansystem.entity.CreditScoreDetails;
import com.jocata.loansystem.entity.CustomerDetails;
import com.jocata.loansystem.entity.LoanApplicationDetails;
import com.jocata.loansystem.form.*;
import com.jocata.loansystem.service.LoanApplicationService;
import com.jocata.loansystem.util.LoanStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class LoanApplicationServiceImpl implements LoanApplicationService {
    private final RestTemplate restTemplate;
    private final CustomerDao customerDao;
    private final CreditScoreDao creditScoreDao;
    private final LoanApplicationDao loanApplicationDao;

    public LoanApplicationServiceImpl(RestTemplate restTemplate, CustomerDao customerDao, CreditScoreDao creditScoreDao, LoanApplicationDao loanApplicationDao) {
        this.restTemplate = restTemplate;
        this.customerDao = customerDao;
        this.creditScoreDao = creditScoreDao;
        this.loanApplicationDao = loanApplicationDao;
    }

    private static String aadharUrl = "http://localhost:8080/externalservices/getAadharDetails";
    private static String panUrl = "http://localhost:8080/externalservices/getPanDetails";
    private static String cibilUrl = "http://localhost:8080/externalservices/getCibilDetails";

    @Override
    public String createLoanApplication(LoanApplicationRequestForm loanApplicationRequestForm) {

        String aadharNo = loanApplicationRequestForm.getAadharNo();
        String panNo = loanApplicationRequestForm.getPanNo();
        String mobileNo = loanApplicationRequestForm.getMobileNo();

        if (isValidAadhar(aadharNo) && isValidPan(panNo) && isValidmobile(mobileNo)) {
            try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
                Future<PanResponseForm> panFuture = executorService.submit(() -> getPanResponse(panNo));
                Future<AadharResponseForm> aadharFuture = executorService.submit(() -> getAadharResponse(aadharNo));
                Future<CibilResponseForm> cibilFuture = executorService.submit(() -> getCibilResponse(panNo));

                try {
                    PanResponseForm panResponseForm = panFuture.get();
                    AadharResponseForm aadharResponseForm = aadharFuture.get();
                    CibilResponseForm cibilResponseForm = cibilFuture.get();

                    String aadharName = aadharResponseForm.getFullName();
                    if (aadharName == null || aadharName.isBlank()) {
                        throw new IllegalArgumentException("Full name is missing in Aadhar");
                    }
                    String[] parts = aadharName.split(" ");
                    String firstNameFromAadhar = parts[0];
                    String lastNameFromAadhar = parts.length > 1 ? parts[parts.length - 1] : "";

                    String panName = panResponseForm.getFullName();
                    if (panName == null || panName.isBlank()) {
                        throw new IllegalArgumentException("Full name is missing in Pan.");
                    }
                    panName = panName.trim().replaceAll("\\s+", " ");
                    String[] partsPan = panName.split(" ");
                    String firstNameFromPan = partsPan[0];
                    String lastNameFromPan = partsPan.length > 1 ? partsPan[partsPan.length - 1] : "";


                    CustomerDetails customerDetails = customerDao.getCustomer(panNo);
                    CustomerDetails loanApplicationCustomer = loanApplicationDao.getCustomerFromLoan(customerDetails.getCustomerId());
                    if (customerDetails.getCustomerId().equals(loanApplicationCustomer.getCustomerId())) {
                        CreditScoreDetails currentScore = creditScoreDao.getCustomerFromCreditScore(customerDetails.getCustomerId());
                        if (currentScore != null) {
                            currentScore.setScore(Integer.valueOf(cibilResponseForm.getCreditScore()));
                            currentScore.setScoreDate(Date.valueOf(cibilResponseForm.getReportDate()));
                            currentScore.setCreditHistory(cibilResponseForm.getCreditHistory());
                            currentScore.setTotalOutstandingBalance(new BigDecimal(cibilResponseForm.getTotalOutstandingBalance()));
                            currentScore.setRecentCreditInquiries(cibilResponseForm.getRecentCreditEnquiries());
                            currentScore.setPaymentHistory(cibilResponseForm.getPaymentHistory());
                            currentScore.setCreditLimit(new BigDecimal(cibilResponseForm.getCreditLimit()));
                            currentScore.setStatus(cibilResponseForm.getStatus());
                            creditScoreDao.updateCreditScore(currentScore);
                        } else {
                            CreditScoreDetails newScore = new CreditScoreDetails();
                            newScore.setCustomer(customerDetails);
                            newScore.setScore(Integer.valueOf(cibilResponseForm.getCreditScore()));
                            newScore.setScoreDate(Date.valueOf(cibilResponseForm.getReportDate()));
                            newScore.setCreditHistory(cibilResponseForm.getCreditHistory());
                            newScore.setTotalOutstandingBalance(new BigDecimal(cibilResponseForm.getTotalOutstandingBalance()));
                            newScore.setRecentCreditInquiries(cibilResponseForm.getRecentCreditEnquiries());
                            newScore.setPaymentHistory(cibilResponseForm.getPaymentHistory());
                            newScore.setCreditLimit(new BigDecimal(cibilResponseForm.getCreditLimit()));
                            newScore.setStatus(cibilResponseForm.getStatus());
                            creditScoreDao.createCreditScore(newScore);
                        }
                        LoanApplicationDetails newLoan = new LoanApplicationDetails();
                        newLoan.setApplicationDate(new Date(System.currentTimeMillis()));
                        newLoan.setCustomerId(customerDetails);
                        newLoan.setStatus(String.valueOf(LoanStatus.PENDING));

                        loanApplicationDao.createLoan(newLoan);
                        return "Loan Application submitted sucessfully.";
                    }
                    CustomerDetails customer = new CustomerDetails();
                    customer.setFirstName(!StringUtils.isEmpty(firstNameFromPan) ? firstNameFromPan : firstNameFromAadhar);
                    customer.setLastName(!StringUtils.isEmpty(lastNameFromPan) ? lastNameFromPan : lastNameFromAadhar);
                    customer.setEmail(aadharResponseForm.getEmail());
                    customer.setDob(Date.valueOf(aadharResponseForm.getDob()));
                    customer.setPhoneNumber(aadharResponseForm.getContactNo());
                    customer.setAddress(aadharResponseForm.getAddress());
                    customer.setIdentityNumber(!StringUtils.isEmpty(panResponseForm.getPanNo()) ? panResponseForm.getPanNo() : aadharResponseForm.getAadharNo());
                    customerDao.createCustomer(customer);

                    CreditScoreDetails scoreDetails = new CreditScoreDetails();
                    scoreDetails.setCustomer(customer);
                    scoreDetails.setScoreDate(Date.valueOf(cibilResponseForm.getReportDate()));
                    scoreDetails.setScore(Integer.valueOf(cibilResponseForm.getCreditScore()));
                    scoreDetails.setCreditHistory(cibilResponseForm.getCreditHistory());
                    scoreDetails.setTotalOutstandingBalance(new BigDecimal(cibilResponseForm.getTotalOutstandingBalance()));
                    scoreDetails.setRecentCreditInquiries(cibilResponseForm.getRecentCreditEnquiries());
                    scoreDetails.setPaymentHistory(cibilResponseForm.getPaymentHistory());
                    scoreDetails.setCreditLimit(new BigDecimal(cibilResponseForm.getCreditLimit()));
                    scoreDetails.setStatus(cibilResponseForm.getStatus());
                    creditScoreDao.createCreditScore(scoreDetails);

                    LoanApplicationDetails applicationDetails = new LoanApplicationDetails();
                    applicationDetails.setApplicationDate(new Date(System.currentTimeMillis()));
                    applicationDetails.setCustomerId(customer);
                    applicationDetails.setStatus(String.valueOf(LoanStatus.PENDING));
                    loanApplicationDao.createLoan(applicationDetails);
                } catch (Exception e) {
//                    throw new IllegalArgumentException("Error while fetching data from api.");
                    e.printStackTrace();
                } finally {
                    executorService.shutdown();
                }
            }
        }

        return "Loan application stored successfully.";
    }

    private CibilResponseForm getCibilResponse(String panNo) {
        CibilPayLoad cibilPayLoad = new CibilPayLoad();
        cibilPayLoad.setPanNo(panNo);

        ExternalServiceRequestForm externalServiceRequestForm = new ExternalServiceRequestForm();
        externalServiceRequestForm.setTxnId(UUID.randomUUID().toString());
        externalServiceRequestForm.setCibilPayload(cibilPayLoad);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExternalServiceRequestForm> requestFormHttpEntity = new HttpEntity<>(externalServiceRequestForm, headers);
        ResponseEntity<ExternalServiceResponseForm<List<CibilResponseForm>>> responseFormHttpEntity = restTemplate.exchange(
                cibilUrl, HttpMethod.POST, requestFormHttpEntity, new ParameterizedTypeReference<>() {
                }
        );
        return getCibilResponse(panNo, responseFormHttpEntity);
    }

    private CibilResponseForm getCibilResponse(String panNo, ResponseEntity<ExternalServiceResponseForm<List<CibilResponseForm>>> responseFormEntity) {
        ExternalServiceResponseForm<List<CibilResponseForm>> response = responseFormEntity.getBody();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Pan data not found for Aadhar number: " + panNo);
        }
        List<CibilResponseForm> cibilResponses = response.getData();
        CibilResponseForm latestCibilResponse = null;
        for (CibilResponseForm cibilResponse : cibilResponses) {
            if (cibilResponse.getReportDate() == null) {
                continue;
            }

            if (latestCibilResponse == null || cibilResponse.getReportDate().compareTo(latestCibilResponse.getReportDate()) > 0) {
                latestCibilResponse = cibilResponse;
            }
        }

        if (latestCibilResponse == null) {
            throw new IllegalArgumentException("No valid CIBIL report found");
        }
        return latestCibilResponse;
    }

    private AadharResponseForm getAadharResponse(String aadharNo) {
        AadharPayLoad aadharPayLoad = new AadharPayLoad();
        aadharPayLoad.setAadharNo(aadharNo);

        ExternalServiceRequestForm request = new ExternalServiceRequestForm();
        request.setAadharPayload(aadharPayLoad);
        request.setTxnId(UUID.randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExternalServiceRequestForm> requestFormHttpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<ExternalServiceResponseForm<AadharResponseForm>> responseEntity = restTemplate.exchange(
                aadharUrl, HttpMethod.POST, requestFormHttpEntity, new ParameterizedTypeReference<>() {
                }
        );

        ExternalServiceResponseForm<AadharResponseForm> response = responseEntity.getBody();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Aadhar data not found.");
        }
        return response.getData();
    }

    private PanResponseForm getPanResponse(String panNo) {
        PanPayLoad panPayload = new PanPayLoad();
        panPayload.setPanNo(panNo);

        ExternalServiceRequestForm externalServiceRequest = new ExternalServiceRequestForm();
        externalServiceRequest.setTxnId(UUID.randomUUID().toString());
        externalServiceRequest.setPanPayload(panPayload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ExternalServiceRequestForm> requestEntity = new HttpEntity<>(externalServiceRequest, headers);

        ResponseEntity<ExternalServiceResponseForm<PanResponseForm>> responseEntity = restTemplate.exchange(
                panUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
                }
        );

        ExternalServiceResponseForm<PanResponseForm> response = responseEntity.getBody();
        if (response == null || response.getData() == null) {
            throw new IllegalArgumentException("Pan data not found");
        }

        return response.getData();
    }

    private boolean isValidAadhar(String aadharNo) {
        return (aadharNo != null && !aadharNo.trim().isEmpty() && aadharNo.matches("\\d{12}"));
    }

    private boolean isValidPan(String panNo) {
        return (panNo != null && !panNo.trim().isEmpty() && panNo.matches("[A-Z]{3}[PFCHAT]{1}[A-Z]{1}[0-9]{4}[A-Z]{1}"));
    }

    private boolean isValidmobile(String mobileNo) {
        return (mobileNo != null && !mobileNo.trim().isEmpty() && mobileNo.matches("\\d{10}"));
    }
}
