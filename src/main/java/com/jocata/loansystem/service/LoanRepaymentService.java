package com.jocata.loansystem.service;

import com.jocata.loansystem.form.LoanRepaymentResponseForm;

import java.util.List;

public interface LoanRepaymentService {
    List<LoanRepaymentResponseForm> getSchedule(String applicationId);
    String rePayment(String loanId);
}
