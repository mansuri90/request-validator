package com.theadex.requestvalidator.service;

import com.theadex.requestvalidator.domain.Customer;

public interface CustomerService {
    Customer getCustomerById(Long customerID);

    Customer save(Customer customer);
}
