package com.jocata.loansystem.service;

import com.jocata.loansystem.form.LoanDisbursementForm;

public interface LoanDisbursementService {
    LoanDisbursementForm createDisbursement(String applicationId);
}
