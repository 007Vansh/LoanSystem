package com.jocata.loansystem.dao;

import com.jocata.loansystem.entity.CustomerDetails;

public interface CustomerDao {
    CustomerDetails createCustomer(CustomerDetails customerDetails);
    CustomerDetails getCustomer(String panNumber);
}
