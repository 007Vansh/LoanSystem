package com.jocata.loansystem.service.serviceImpl;

import com.jocata.loansystem.dao.LoanApplicationDao;
import com.jocata.loansystem.dao.LoanDetailsDao;
import com.jocata.loansystem.dao.LoanRepaymentDao;
import com.jocata.loansystem.entity.LoanApplicationDetails;
import com.jocata.loansystem.entity.LoanDetails;
import com.jocata.loansystem.entity.LoanPaymentDetails;
import com.jocata.loansystem.form.LoanRepaymentResponseForm;
import com.jocata.loansystem.service.LoanRepaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanRepaymentServiceImpl implements LoanRepaymentService {
    private LoanApplicationDao loanApplicationDao;
    private LoanRepaymentDao loanRepaymentDao;
    private LoanDetailsDao loanDetailsDao;

    public LoanRepaymentServiceImpl(LoanApplicationDao loanApplicationDao, LoanRepaymentDao loanRepaymentDao, LoanDetailsDao loanDetailsDao) {
        this.loanApplicationDao = loanApplicationDao;
        this.loanRepaymentDao = loanRepaymentDao;
        this.loanDetailsDao = loanDetailsDao;
    }

    @Override
    public List<LoanRepaymentResponseForm> getSchedule(String applicationId) {
        LoanApplicationDetails loanApplicationDetails=loanApplicationDao.getApplicationFromId(applicationId);
        BigDecimal loanAmount = loanApplicationDetails.getLoanAmount();
        BigDecimal interestRate = loanApplicationDetails.getProductId().getInterestRate();
        Integer termMonths = loanApplicationDetails.getProductId().getTermMonths();

        BigDecimal monthlyInterestRate = interestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal fixedInterestPerMonth = loanAmount.multiply(monthlyInterestRate).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        return getRePaymentScheduleForms(loanAmount, termMonths, fixedInterestPerMonth);
    }



    private static List<LoanRepaymentResponseForm> getRePaymentScheduleForms(BigDecimal loanAmount, Integer termMonths, BigDecimal fixedInterestPerMonth) {
        BigDecimal principalPerMonth = loanAmount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        BigDecimal remainingBalance = loanAmount;

        List<LoanRepaymentResponseForm> rePaymentScheduleForms = new ArrayList<>();

        for (int i = 1; i <= termMonths; i++) {
            remainingBalance = remainingBalance.subtract(principalPerMonth).setScale(0, RoundingMode.HALF_UP);
            LoanRepaymentResponseForm schedule = new LoanRepaymentResponseForm();
            schedule.setId(String.valueOf(i));
            schedule.setPrinciple(String.valueOf(principalPerMonth.setScale(0, RoundingMode.HALF_UP).intValue()));
            schedule.setInterest(String.valueOf(fixedInterestPerMonth.setScale(0, RoundingMode.HALF_UP).intValue()));
            schedule.setBalance(String.valueOf(remainingBalance.compareTo(BigDecimal.ZERO) > 0 ? remainingBalance.intValue() : 0));
//            schedule.setEmi(String.valueOf(principalPerMonth.setScale(0, RoundingMode.HALF_UP).intValue()
//                    +fixedInterestPerMonth.setScale(0, RoundingMode.HALF_UP).intValue()));
            rePaymentScheduleForms.add(schedule);
        }
        return rePaymentScheduleForms;
    }
    @Override
    public String rePayment(String loanId) {
        Long paidInstallment=(loanRepaymentDao.getinstllment(loanId));
        LoanDetails loanDetails= loanDetailsDao.getLoanByLoanId(loanId);
        List<LoanRepaymentResponseForm> repaymentSchedule=getSchedule(String.valueOf(loanDetails.getLoanApplication().getApplicationId()));
        LoanPaymentDetails loanPaymentDetails=new LoanPaymentDetails();
        loanPaymentDetails.setPaymentDate(new Date(System.currentTimeMillis()));
        loanPaymentDetails.setLoan(loanDetails);
        loanPaymentDetails.setPaymentAmount(new BigDecimal(repaymentSchedule.get((int) (paidInstallment+1)).getPrinciple()+repaymentSchedule.get((int) (paidInstallment+1)).getInterest()));
        loanPaymentDetails.setPaymentMethod("UPI");
        loanPaymentDetails.setRemainingBalance(new BigDecimal(repaymentSchedule.get((int) (paidInstallment+1)).getBalance()));
        return loanRepaymentDao.rePayment(loanPaymentDetails);
    }
}
