package com.bogdan.fullstackproject.customer.repository;

import com.bogdan.fullstackproject.AbstractTestcontainers;
import com.bogdan.fullstackproject.customer.model.Customer;
import com.bogdan.fullstackproject.customer.model.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The @DataJpaTest annotation autoconfigures beans for JPA-related components
 * like our repositories. It also configures beans for things like a DataSource,
 * JdbcTemplate, or TestEntityManager.
 * ---
 * The AutoConfigureTestDatabase.Replace.NONE annotation property tells Spring not
 * to replace the database with an embedded database. If we didn’t do this,
 * Spring wouldn’t be using the Postgres data source and would try to autoconfigure
 * an embedded database instead.
 * ---
 * Now our test code will be executed against a PostgreSQL instance running in Docker.
 * We have also avoided the problems related to using two different database engines.
 * ---
 * The @DataJpaTest VS @SpringBootTest
 * The @SpringBootTest loads full application context, exactly like how you start a
 * Spring container when you run your Spring Boot application.
 * The @DataJpaTest loads only configuration for JPA. It uses an embedded in-memory h2
 * if not specified otherwise.
 */

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.save(customer);

        //When
        boolean actual = underTest.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //When
        boolean actual = underTest.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        //Given
        Customer customer = getCustomer();
        String email = customer.getEmail();

        underTest.save(customer);

        int customerId = getCustomerId(email);

        //When
        boolean actual = underTest.existsCustomerById(customerId);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        //Given
        int customerId = -1;

        //When
        boolean actual = underTest.existsCustomerById(customerId);

        //Then
        assertThat(actual).isFalse();
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
        return underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
    }
}