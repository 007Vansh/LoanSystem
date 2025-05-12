package com.jocata.loansystem.dao;

import com.jocata.loansystem.entity.CustomerDetails;
import com.jocata.loansystem.entity.LoanApplicationDetails;

public interface LoanApplicationDao {
    LoanApplicationDetails createLoan(LoanApplicationDetails loanApplicationDetails);
    CustomerDetails getCustomerFromLoan(int id);
    LoanApplicationDetails updateLoan(LoanApplicationDetails loanApplicationDetails);
    LoanApplicationDetails getLatestLoanApplicationByCustomerId(Integer customerId);
    LoanApplicationDetails getApplicationFromId(String applicationId);
}
