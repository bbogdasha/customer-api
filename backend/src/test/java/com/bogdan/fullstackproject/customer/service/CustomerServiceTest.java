package com.bogdan.fullstackproject.customer.service;

import com.bogdan.fullstackproject.customer.dao.CustomerDao;
import com.bogdan.fullstackproject.customer.dto.CustomerDTO;
import com.bogdan.fullstackproject.customer.mapper.CustomerMapper;
import com.bogdan.fullstackproject.customer.model.Customer;
import com.bogdan.fullstackproject.customer.model.CustomerRegistrationRequest;
import com.bogdan.fullstackproject.customer.model.CustomerUpdateRequest;
import com.bogdan.fullstackproject.customer.model.Gender;
import com.bogdan.fullstackproject.exception.DuplicateResourceException;
import com.bogdan.fullstackproject.exception.RequestValidationException;
import com.bogdan.fullstackproject.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Extension: @ExtendWith(MockitoExtension.class)
 * There is also a Mockito extension for JUnit 5 that will make the initialization even simpler.
 * Now we can apply the extension and get rid of the MockitoAnnotations.openMocks() method call.
 * Now we don't need .openMocks() and .close()
 * ---
 * Class ArgumentCaptor<Customer> in 'addCustomer()' test:
 * We can use ArgumentCaptor to intercept the results of method calls and then check their validity.
 */

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomerService underTest;

    private final CustomerMapper customerMapper = new CustomerMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, customerMapper, passwordEncoder);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomer() {
        //Given
        int customerId = 10;

        Customer customer = new Customer(customerId, "Jill", "jill@gmail.com",
                "password", 25, Gender.UNSELECTED);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerMapper.apply(customer);

        //When
        CustomerDTO actual = underTest.getCustomer(10);

        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void throwExceptionWhenGetCustomerEmpty() {
        //Given
        int customerId = 10;

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        //Then
        assertThatThrownBy(() -> underTest.getCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(customerId));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "jill@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Jill", "jill@gmail.com", "password", 25, Gender.FEMALE);

        String passwordHash = "gfsdyf7%$5";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void throwExceptionWhenEmailExistsWhileAddingCustomer() {
        //Given
        String email = "jill@gmail.com";

        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Jill", "jill@gmail.com", "password", 25, Gender.FEMALE);

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        //Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomer() {
        //Given
        int customerId = 10;

        when(customerDao.existsCustomerWithId(customerId)).thenReturn(true);

        //When
        underTest.deleteCustomer(customerId);

        //Then
        verify(customerDao).deleteCustomerById(customerId);
    }

    @Test
    void throwExceptionWhileDeletingCustomerNotExists() {
        //Given
        int customerId = 10;

        when(customerDao.existsCustomerWithId(customerId)).thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomer(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(customerId));

        //Then
        verify(customerDao, never()).deleteCustomerById(customerId);
    }

    @Test
    void updateAllCustomerProperties() {
        //Given
        int customerId = 10;

        Customer customer = new Customer(customerId, "Jill", "jill@gmail.com",
                "password", 25, Gender.FEMALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String newEmail = "alex@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Alex", newEmail, 20);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(customerId, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void updateOnlyCustomerName() {
        //Given
        int customerId = 10;

        Customer customer = new Customer(customerId, "Jill", "jill@gmail.com",
                "password", 25, Gender.FEMALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Alex", null, null);

        //When
        underTest.updateCustomer(customerId, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateOnlyCustomerEmail() {
        //Given
        int customerId = 10;

        Customer customer = new Customer(customerId, "Jill", "jill@gmail.com",
                "password", 25, Gender.FEMALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String newEmail = "alex@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(customerId, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateOnlyCustomerAge() {
        //Given
        int customerId = 10;

        Customer customer = new Customer(customerId, "Jill", "jill@gmail.com",
                "password", 25, Gender.FEMALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, 24);

        //When
        underTest.updateCustomer(customerId, updateRequest);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void throwExceptionWhileUpdatingCustomerEmailWhenAlreadyTaken() {
        //Given
        int customerId = 10;

        Customer customer = new Customer(customerId, "Jill", "jill@gmail.com",
                "password", 25, Gender.FEMALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String newEmail = "alex@gmail.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void throwExceptionWhenCustomerUpdateHasNoChanges() {
        //Given
        int customerId = 10;

        Customer customer = new Customer(customerId, "Jill", "jill@gmail.com",
                "password", 25, Gender.FEMALE);

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(customerId, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");

        //Then
        verify(customerDao, never()).updateCustomer(any());
    }

}