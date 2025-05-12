package com.jocata.loansystem.service;

import com.jocata.loansystem.form.LoanProductRequestForm;
import com.jocata.loansystem.form.LoanProductResponseForm;

public interface LoanProductService {
    LoanProductResponseForm createProduct(LoanProductRequestForm loanProductRequestForm);
    LoanProductResponseForm getProduct(String id);
    LoanProductResponseForm updateProduct(LoanProductResponseForm loanProductResponseForm);
    String deleteProduct(String id);
}
