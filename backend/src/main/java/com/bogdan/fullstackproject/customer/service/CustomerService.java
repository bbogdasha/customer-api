package com.bogdan.fullstackproject.customer.service;

import com.bogdan.fullstackproject.customer.dao.CustomerDao;
import com.bogdan.fullstackproject.customer.dto.CustomerDTO;
import com.bogdan.fullstackproject.customer.mapper.CustomerMapper;
import com.bogdan.fullstackproject.customer.model.Customer;
import com.bogdan.fullstackproject.customer.model.CustomerRegistrationRequest;
import com.bogdan.fullstackproject.customer.model.CustomerUpdateRequest;
import com.bogdan.fullstackproject.exception.DuplicateResourceException;
import com.bogdan.fullstackproject.exception.RequestValidationException;
import com.bogdan.fullstackproject.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    private final CustomerMapper customerMapper;

    private final PasswordEncoder passwordEncoder;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao,
                           CustomerMapper customerMapper, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomers()
                .stream()
                .map(customerMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer customerId) {
        return customerDao.selectCustomerById(customerId)
                .map(customerMapper)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {

        String email = customerRegistrationRequest.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("Email already taken");
        }

        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());

        customerDao.insertCustomer(customer);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest updateCustomer) {

        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId)));

        boolean changes = false;

        if (updateCustomer.name() != null && !updateCustomer.name().equals(customer.getName())) {
            customer.setName(updateCustomer.name());
            changes = true;
        }

        if (updateCustomer.age() != null && !updateCustomer.age().equals(customer.getAge())) {
            customer.setAge(updateCustomer.age());
            changes = true;
        }

        if (updateCustomer.email() != null && !updateCustomer.email().equals(customer.getEmail())) {
            if (customerDao.existsCustomerWithEmail(updateCustomer.email())) {
                throw new DuplicateResourceException("Email already taken");
            }
            customer.setEmail(updateCustomer.email());
            changes = true;
        }

        if (!changes) {
            throw new RequestValidationException("No data changes found");
        }

        customerDao.updateCustomer(customer);
    }

    public void deleteCustomer(Integer customerId) {

        if (!customerDao.existsCustomerWithId(customerId)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId));
        }
        customerDao.deleteCustomerById(customerId);
    }
}
