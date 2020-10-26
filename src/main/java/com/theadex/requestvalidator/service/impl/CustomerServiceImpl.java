package com.theadex.requestvalidator.service.impl;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.exception.CustomerNotFoundException;
import com.theadex.requestvalidator.repository.CustomerRepository;
import com.theadex.requestvalidator.service.CustomerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final @NonNull CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(@NonNull Long customerID) {
        return customerRepository.findById(customerID).orElseThrow(() -> new CustomerNotFoundException(customerID));
    }

    @Override
    public Customer save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }
}
