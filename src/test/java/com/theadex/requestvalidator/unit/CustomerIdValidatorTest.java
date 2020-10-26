package com.theadex.requestvalidator.unit;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.dto.input.RequestDto;
import com.theadex.requestvalidator.exception.CustomerNotFoundException;
import com.theadex.requestvalidator.exception.MissingCustomerIdException;
import com.theadex.requestvalidator.exception.InactiveCustomerException;
import com.theadex.requestvalidator.service.CustomerService;
import com.theadex.requestvalidator.validator.impl.CustomerIdValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CustomerIdValidatorTest {

    @Test
    void missingCustomerIdTest() {
        final CustomerService mockCustomerService = Mockito.mock(CustomerService.class);
        final CustomerIdValidator customerIdValidator = new CustomerIdValidator(mockCustomerService);

        final RequestDto requestWithNullCustomerId = new RequestDto(null, null, null, null, null);

        assertThrows(MissingCustomerIdException.class, () -> customerIdValidator.validate(requestWithNullCustomerId));
    }

    @Test
    void inactiveCustomerTest() {
        final CustomerService mockCustomerService = Mockito.mock(CustomerService.class);
        final CustomerIdValidator customerIdValidator = new CustomerIdValidator(mockCustomerService);

        final long customerID = 1L;
        final Customer customer = new Customer(customerID, "name-1", false);
        when(mockCustomerService.getCustomerById(customerID)).thenReturn(customer);

        final RequestDto request = new RequestDto(customerID, null, null, null, null);

        assertThrows(InactiveCustomerException.class, () -> customerIdValidator.validate(request));
    }

    @Test
    void invalidCustomerIdTest() {
        final CustomerService mockCustomerService = Mockito.mock(CustomerService.class);
        final CustomerIdValidator customerIdValidator = new CustomerIdValidator(mockCustomerService);

        final long customerID = 1L;
        when(mockCustomerService.getCustomerById(customerID)).thenThrow(new CustomerNotFoundException(customerID));

        final RequestDto request = new RequestDto(customerID, null, null, null, null);

        assertThrows(CustomerNotFoundException.class, () -> customerIdValidator.validate(request));
    }

    @Test
    void validCustomerIdTest() {
        final CustomerService mockCustomerService = Mockito.mock(CustomerService.class);
        final CustomerIdValidator customerIdValidator = new CustomerIdValidator(mockCustomerService);

        final long customerID = 1L;
        final Customer customer = new Customer(customerID, "name-1", true);
        when(mockCustomerService.getCustomerById(customerID)).thenReturn(customer);

        final RequestDto request = new RequestDto(customerID, null, null, null, null);

        assertDoesNotThrow(() -> customerIdValidator.validate(request));
    }

}
