package com.bogdan.fullstackproject.customer.dao;

import com.bogdan.fullstackproject.AbstractTestcontainers;
import com.bogdan.fullstackproject.customer.model.Customer;
import com.bogdan.fullstackproject.customer.model.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;

    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = getCustomer();
        underTest.insertCustomer(customer);

        //When
        List<Customer> actual = underTest.selectAllCustomers();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        //When
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
           assertThat(c.getId()).isEqualTo(customerId);
           assertThat(c.getName()).isEqualTo(customer.getName());
           assertThat(c.getEmail()).isEqualTo(customer.getEmail());
           assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void returnEmptyWhenSelectCustomerById() {
        //Given
        int customerId = -1;

        //When
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void existsCustomerWithEmail() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        //When
        boolean actual = underTest.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithEmailReturnFalseWhenDoesNotExists() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //When
        boolean actual = underTest.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        //When
        boolean actual = underTest.existsCustomerWithId(customerId);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdReturnFalseWhenIdDoesNotExists() {
        //Given
        int customerId = -1;

        //When
        boolean actual = underTest.existsCustomerWithId(customerId);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        //When
        underTest.deleteCustomerById(customerId);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(customerId);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        String newName = "Test";

        //When
        Customer update = new Customer();
        update.setId(customerId);
        update.setName(newName);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(newName); //change
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //When
        Customer update = new Customer();
        update.setId(customerId);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail); //change
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        int newAge = 99;

        //When
        Customer update = new Customer();
        update.setId(customerId);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge); //change
        });
    }

    @Test
    void updateAllPropertiesCustomer() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        //When
        Customer update = new Customer();
        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        update.setId(customerId);
        update.setName("Test");
        update.setEmail(newEmail);
        update.setAge(21);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(customerId);
        assertThat(actual).isPresent().hasValueSatisfying(updated -> {
            assertThat(updated.getId()).isEqualTo(customerId);
            assertThat(updated.getName()).isEqualTo("Test");
            assertThat(updated.getGender()).isEqualTo(Gender.UNSELECTED);
            assertThat(updated.getEmail()).isEqualTo(newEmail);
            assertThat(updated.getAge()).isEqualTo(21);
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.insertCustomer(customer);

        int customerId = getCustomerId(email);

        //When
        Customer update = new Customer();
        update.setId(customerId);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerById(customerId);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    private Customer getCustomer() {
        return new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                "password",
                25,
                Gender.UNSELECTED);
    }

    private int getCustomerId(String email) {
        return underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
    }
}