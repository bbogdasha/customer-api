package com.bogdan.fullstackproject.customer.repository;

import com.bogdan.fullstackproject.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsCustomerByEmail(String email);

    boolean existsCustomerById(Integer customerId);

    Optional<Customer> findCustomerByEmail(String email);

}
