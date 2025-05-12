package com.jocata.loansystem.dao;

import com.jocata.loansystem.entity.LoanPaymentDetails;

public interface LoanRepaymentDao {
    String rePayment(LoanPaymentDetails loanPaymentDetails);
    Long getinstllment(String loanId);
}
