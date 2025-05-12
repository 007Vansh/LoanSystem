package com.jocata.loansystem.service.serviceImpl;

import com.jocata.loansystem.dao.*;
import com.jocata.loansystem.entity.*;
import com.jocata.loansystem.form.LoanDisbursementForm;
import com.jocata.loansystem.service.LoanDisbursementService;
import com.jocata.loansystem.util.DisbursementMethods;
import com.jocata.loansystem.util.LoanStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class LoanDisbursementServiceImpl implements LoanDisbursementService {
    private final LoanDisbursementDao loanDisbursementDao;
    private final LoanDetailsDao loanDetailsDao;
    private final RiskAssessmentDao riskAssessmentDao;
    private final LoanApplicationDao loanApplicationDao;
    private final LoanProductDao loanProductDao;

    public LoanDisbursementServiceImpl(LoanDisbursementDao loanDisbursementDao, LoanDetailsDao loanDetailsDao, RiskAssessmentDao riskAssessmentDao, LoanApplicationDao loanApplicationDao, LoanProductDao loanProductDao) {
        this.loanDisbursementDao = loanDisbursementDao;
        this.loanDetailsDao = loanDetailsDao;
        this.riskAssessmentDao = riskAssessmentDao;
        this.loanApplicationDao = loanApplicationDao;
        this.loanProductDao = loanProductDao;
    }
    public LoanDisbursementForm entityToForm(LoanDisbursementDetails loanDisbursementDetails){
        LoanDisbursementForm loanDisbursementForm=new LoanDisbursementForm();
        loanDisbursementForm.setId(String.valueOf(loanDisbursementDetails.getId()));
        loanDisbursementForm.setLoanId(String.valueOf(loanDisbursementDetails.getLoan().getLoanId()));
        loanDisbursementForm.setDisbursementDate(String.valueOf(loanDisbursementDetails.getDisbursementDate()));
        loanDisbursementForm.setDisbursedAmount(String.valueOf(loanDisbursementDetails.getDisbursedAmount()));
        loanDisbursementForm.setDisbursementMethod(loanDisbursementDetails.getDisbursementMethod());
        return loanDisbursementForm;
    }
    @Override
    public LoanDisbursementForm createDisbursement(String applicationId) {
        LoanDetails loanDetails=createLoan(applicationId);
        LoanDisbursementDetails loanDisbursementDetails=new LoanDisbursementDetails();
        loanDisbursementDetails.setLoan(loanDetails);
        loanDisbursementDetails.setDisbursementDate(new Date(System.currentTimeMillis()));
        loanDisbursementDetails.setDisbursedAmount(loanDetails.getLoanAmount());
        loanDisbursementDetails.setDisbursementMethod(String.valueOf(DisbursementMethods.DIRECT_DEPOSIT));
        return entityToForm(loanDisbursementDao.create(loanDisbursementDetails));
    }
    public LoanDetails createLoan(String applicationId){
        RiskAssessmentDetails riskAssessmentDetails= riskAssessmentDao.get(applicationId);
        LoanProductDetails loanProductDetails=loanProductDao.getProductByTerm(riskAssessmentDetails.getApprovedTerm());
        LoanDetails loanDetails=new LoanDetails();
        loanDetails.setLoanApplication(loanApplicationDao.getApplicationFromId(applicationId));
        loanDetails.setDisbursementDate(new Date(System.currentTimeMillis()));
        loanDetails.setLoanBalance(riskAssessmentDetails.getApprovedAmount());
        loanDetails.setLoanAmount(riskAssessmentDetails.getApprovedAmount());
        loanDetails.setLoanTermMonths(riskAssessmentDetails.getApprovedTerm());
        loanDetails.setStatus(String.valueOf(LoanStatus.DISBURSED));
        loanDetails.setInterestRate(loanProductDetails.getInterestRate());
        return loanDetailsDao.create(loanDetails);
    }
}