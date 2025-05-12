package com.jocata.loansystem.dao;

import com.jocata.loansystem.entity.LoanDetails;

public interface LoanDetailsDao {
    LoanDetails create(LoanDetails loanDetails);
    LoanDetails getLoanByLoanId(String applicationId);
}
