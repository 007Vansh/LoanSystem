package com.jocata.loansystem.dao;

import com.jocata.loansystem.entity.LoanProductDetails;

import java.util.List;

public interface LoanProductDao {

    LoanProductDetails create(LoanProductDetails loanProductDetails);
    LoanProductDetails get(int id);
    LoanProductDetails getProductByTerm(int term);
    LoanProductDetails update(LoanProductDetails loanProductDetails);
    String delete(int id);
    List<LoanProductDetails> getAll();
}
