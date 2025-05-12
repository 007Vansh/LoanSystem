package com.jocata.loansystem.service.serviceImpl;

import com.jocata.loansystem.dao.*;
import com.jocata.loansystem.entity.*;
import com.jocata.loansystem.form.*;
import com.jocata.loansystem.service.CreditAssessmentService;
import com.jocata.loansystem.util.LoanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CreditAssessmentServiceImpl implements CreditAssessmentService {
    @Autowired
    private CreditScoreDao creditScoreDao;
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private LoanProductDao loanProductDao;
    @Autowired
    private RiskAssessmentDao riskAssessmentDao;
    @Autowired
    private LoanApplicationDao loanApplicationDao;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getCreditAssessment(CreditAssessmentForm creditAssessmentForm) {
        if(isValidIncome(creditAssessmentForm.getIncome()) && isValidPan(creditAssessmentForm.getPanNo()) &&
        isValidRequestedAmount(creditAssessmentForm.getRequestedAmount()) && isValidRequestedTerm(creditAssessmentForm.getRequestedTenure())){
            CustomerDetails customerDetails=customerDao.getCustomer(creditAssessmentForm.getPanNo());
            CreditScoreDetails creditScoreDetails= creditScoreDao.getCustomerFromCreditScore(customerDetails.getCustomerId());
            LoanProductDetails loanProductDetails=loanProductDao.getProductByTerm(Integer.parseInt(creditAssessmentForm.getRequestedTenure()));


            BigDecimal outstandingAmount = creditScoreDetails.getTotalOutstandingBalance();
            double customerEligibleAmount = Double.parseDouble(creditAssessmentForm.getIncome()) * 0.75;
            if (outstandingAmount.doubleValue() > customerEligibleAmount) return "Not Eligible for loan";

            if ((outstandingAmount.doubleValue() < customerEligibleAmount) &&
                    Integer.valueOf(creditScoreDetails.getRecentCreditInquiries()) < 15 &&
                    creditScoreDetails.getScore() >= 750) {
                double availableEmi = customerEligibleAmount - outstandingAmount.doubleValue();

                BigDecimal interestRate = loanProductDetails.getInterestRate();
                Integer termMonths = loanProductDetails.getTermMonths();

                double monthlyInterestRate = interestRate.doubleValue() / 12 / 100;
                double powFactor = Math.pow(1 + monthlyInterestRate, termMonths);
                double principalAmount = ((availableEmi * (powFactor - 1)) / (monthlyInterestRate * powFactor));

                return checkLoanSlabEligibility(principalAmount, creditAssessmentForm);
            }
            return "Not Eligible for loan, check credit scores";
        }
        return "Given Details are Invalid";
    }

    private String checkLoanSlabEligibility(double principalAmount, CreditAssessmentForm creditAssessmentForm) {

        CustomerDetails daoCustomer = customerDao.getCustomer(creditAssessmentForm.getPanNo());
        CreditScoreDetails customerCreditScore = creditScoreDao.getCustomerFromCreditScore(daoCustomer.getCustomerId());

        List<LoanProductDetails> allLoanProducts = loanProductDao.getAll();

        double requestedAmountDouble = Double.parseDouble(creditAssessmentForm.getRequestedAmount());

        for (LoanProductDetails loanProduct : allLoanProducts) {
            BigDecimal minAmount = loanProduct.getMinAmount();
            BigDecimal maxAmount = loanProduct.getMaxAmount();

            if (principalAmount >= minAmount.doubleValue() && principalAmount <= maxAmount.doubleValue()) {
                if (requestedAmountDouble >= minAmount.doubleValue() && requestedAmountDouble <= maxAmount.doubleValue()) {

                    LoanApplicationDetails loanApplication = loanApplicationDao.getLatestLoanApplicationByCustomerId(daoCustomer.getCustomerId());
                    loanApplication.setStatus(String.valueOf(LoanStatus.APPROVED));
                    loanApplication.setProductId(loanProduct);
                    loanApplication.setLoanAmount(BigDecimal.valueOf(requestedAmountDouble));
                    loanApplication.setRequestedTerm(loanProduct.getTermMonths());
                    loanApplication.setLoanPurpose("Business Loan");

                    LoanApplicationDetails updatedLoanApplication = loanApplicationDao.updateLoan(loanApplication);

                    RiskAssessmentDetails riskAssessmentDetails = new RiskAssessmentDetails();
                    riskAssessmentDetails.setAssessmentDate(new Date(System.currentTimeMillis()));
                    riskAssessmentDetails.setCreditScore(customerCreditScore.getScore());
                    riskAssessmentDetails.setApprovedAmount(BigDecimal.valueOf(requestedAmountDouble));
                    riskAssessmentDetails.setApprovedTerm(loanProduct.getTermMonths());
                    riskAssessmentDetails.setApprovedStatus("VERIFIED");
                    riskAssessmentDetails.setIncome(new BigDecimal(creditAssessmentForm.getIncome()));
                    riskAssessmentDetails.setApplicationId(updatedLoanApplication);

                    riskAssessmentDao.save(riskAssessmentDetails);

                    CibilRequestForm cibilRequestForm=new CibilRequestForm();
                    CibilResponseForm cibilResponseForm=getCibilResponse(creditAssessmentForm.getPanNo());
                    cibilRequestForm.setPan(creditAssessmentForm.getPanNo());
                    cibilRequestForm.setTotalOutstandingBalance(String.valueOf(requestedAmountDouble));
                    cibilRequestForm.setCreditHistory(cibilResponseForm.getCreditHistory());
                    cibilRequestForm.setStatus(cibilResponseForm.getStatus());
                    cibilRequestForm.setReportDate(String.valueOf(new Date(System.currentTimeMillis())));
                    cibilRequestForm.setPaymentHistory(cibilResponseForm.getPaymentHistory());
                    cibilRequestForm.setCreditScore(cibilResponseForm.getCreditScore());
                    cibilRequestForm.setRecentCreditInquiries(cibilResponseForm.getRecentCreditEnquiries());
                    cibilRequestForm.setCreditLimit(cibilResponseForm.getCreditLimit());
                    CibilResponseForm cibilResponseForm1=saveCibil(cibilRequestForm);
                    return "You are eligible for the " + loanProduct.getProductName() + " loan slab. And Loan Application Updated Successfully";
                }
                return suggestLoanSlab(principalAmount, requestedAmountDouble, minAmount, maxAmount, loanProduct);
            }
        }

        return "Not eligible for any loan slab.";
    }

    private String suggestLoanSlab(double principalAmount, double requestedAmount, BigDecimal minAmount, BigDecimal maxAmount, LoanProductDetails loanProduct) {
        if (requestedAmount < minAmount.doubleValue()) {
            LoanProductDetails nextSlab = getNextHigherSlab(loanProduct);
            return "Please check the " + nextSlab.getProductName() + " slab for higher eligibility.";
        }

        if (requestedAmount > maxAmount.doubleValue()) {
            LoanProductDetails nextSlab = getNextLowerSlab(loanProduct);
            return "Please check the " + nextSlab.getProductName() + " slab for lower eligibility.";
        }

        if (requestedAmount > principalAmount) {
            return "Please recheck requested amount exceeds your eligible principal.";
        }

        return "Not eligible for requested loan slab.";
    }
    private LoanProductDetails getNextHigherSlab(LoanProductDetails currentSlab) {
        if (currentSlab.getProductId() == 1) {
            return loanProductDao.get(2);
        } else if (currentSlab.getProductId() == 2) {
            return loanProductDao.get(3);
        }
        return currentSlab;
    }

    private LoanProductDetails getNextLowerSlab(LoanProductDetails currentSlab) {
        if (currentSlab.getProductId() == 3) {
            return loanProductDao.get(2);
        } else if (currentSlab.getProductId() == 2) {
            return loanProductDao.get(1);
        }
        return currentSlab;
    }
    private CibilResponseForm getCibilResponse(String panNo) {

        String cibilUrl = "http://localhost:8080/externalservices/getCibilDetails";
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
            throw new IllegalArgumentException("Pan data not found for pan number: " + panNo);
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

    @Override
    public CibilResponseForm saveCibil(CibilRequestForm cibilRequestForm) {
        String cibilUrl = "http://localhost:8080/externalservices/saveCibilDetails";
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CibilRequestForm> requestFormHttpEntity=new HttpEntity<>(cibilRequestForm,headers);
        ResponseEntity<CibilResponseForm> responseEntity=restTemplate.exchange(
                cibilUrl, HttpMethod.POST, requestFormHttpEntity, new ParameterizedTypeReference<>() {
                }
        );
        return responseEntity.getBody();
    }

    private boolean isValidPan(String panNo){
        return (panNo != null && !panNo.trim().isEmpty() && panNo.matches("[A-Z]{3}[PFCHAT]{1}[A-Z]{1}[0-9]{4}[A-Z]{1}"));
    }
    private boolean isValidIncome(String income){
        return (income!=null && !income.trim().isEmpty() && Integer.parseInt(income)>0);
    }
    private boolean isValidRequestedAmount(String requestedAmount){
        return (requestedAmount!=null && !requestedAmount.trim().isEmpty() && Integer.parseInt(requestedAmount)>0);
    }
    private boolean isValidRequestedTerm(String requestedTerm){
        return (requestedTerm!=null && !requestedTerm.trim().isEmpty() && Integer.parseInt(requestedTerm)>0);
    }
}