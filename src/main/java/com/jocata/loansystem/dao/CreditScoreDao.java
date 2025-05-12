package com.jocata.loansystem.dao;

import com.jocata.loansystem.entity.CreditScoreDetails;

public interface CreditScoreDao {

    CreditScoreDetails createCreditScore(CreditScoreDetails creditScoreDetails);
    CreditScoreDetails getCustomerFromCreditScore(int customerId);
    void updateCreditScore(CreditScoreDetails existingScore);

}
