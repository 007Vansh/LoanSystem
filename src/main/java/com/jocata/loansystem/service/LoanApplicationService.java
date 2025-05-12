package com.jocata.loansystem.service;


import com.jocata.loansystem.form.LoanApplicationRequestForm;

public interface LoanApplicationService {

    String createLoanApplication(LoanApplicationRequestForm loanApplicationRequestForm);
}
