package com.bogdan.fullstackproject.customer.dao;

import com.bogdan.fullstackproject.customer.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer customerId);

    void insertCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    boolean existsCustomerWithId(Integer customerId);

    void deleteCustomerById(Integer customerId);

    void updateCustomer(Customer updateCustomer);
}
