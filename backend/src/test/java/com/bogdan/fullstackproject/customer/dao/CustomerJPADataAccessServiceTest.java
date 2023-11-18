package com.bogdan.fullstackproject.customer.dao;

import com.bogdan.fullstackproject.customer.model.Customer;
import com.bogdan.fullstackproject.customer.model.Gender;
import com.bogdan.fullstackproject.customer.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * A declarative alternative to calling the Mockito.mock() method is to annotate
 * a field as a mock with the @Mock annotation. We have to call a particular method
 * to initialize the annotated objects.
 * ---
 * In Mockito 2 there is a MockitoAnnotations.initMocks() method, which is deprecated
 * and replaced with MockitoAnnotations.openMocks() in Mockito 3. The MockitoAnnotations.openMocks()
 * method returns an instance of AutoClosable which can be used to close the resource after the test.
 * ---
 * The MockitoAnnotations.openMocks(this) call tells Mockito to scan this test class instance for any
 * fields annotated with the @Mock annotation and initialize those fields as mocks.
 */

class CustomerJPADataAccessServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private AutoCloseable autoCloseable;

    private CustomerJPADataAccessService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //When
        underTest.selectAllCustomers();

        //Then
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        //Given
        int customerId = 1;

        //When
        underTest.selectCustomerById(customerId);

        //Then
        verify(customerRepository).findById(customerId);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer(1, "Jill", "jill@gmail.com",
                "password", 25, Gender.UNSELECTED);

        //When
        underTest.insertCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        //Given
        String email = "test@gmail.com";

        //When
        underTest.existsCustomerWithEmail(email);

        //Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existsCustomerWithId() {
        //Given
        int customerId = 1;

        //When
        underTest.existsCustomerWithId(customerId);

        //Then
        verify(customerRepository).existsCustomerById(customerId);
    }

    @Test
    void updateCustomer() {
        //Given
        Customer customer = new Customer(1, "Jill", "jill@gmail.com",
                "password", 25, Gender.UNSELECTED);

        //When
        underTest.updateCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void deleteCustomerById() {
        //Given
        int customerId = 1;

        //When
        underTest.deleteCustomerById(customerId);

        //Then
        verify(customerRepository).deleteById(customerId);
    }
}