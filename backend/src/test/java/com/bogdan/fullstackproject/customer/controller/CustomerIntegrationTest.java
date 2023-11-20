package com.bogdan.fullstackproject.customer.controller;

import com.bogdan.fullstackproject.customer.dto.CustomerDTO;
import com.bogdan.fullstackproject.customer.model.CustomerRegistrationRequest;
import com.bogdan.fullstackproject.customer.model.CustomerUpdateRequest;
import com.bogdan.fullstackproject.customer.model.Gender;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Spring Webflux uses Project Reactor as reactive library. Spring WebFlux heavily uses two publishers:
 * Mono: Returns 0 or 1 element.
 * Flux: Returns 0…N elements.
 * ===
 * Void.class: This means that you don't expect to receive the response body, and you only care about
 * the status and/or headers.
 */

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();

    private static final String CUSTOMER_PATH = "/api/v1/customers";

    @Test
    void registerCustomer() {
        //Create customer registration request
        CustomerRegistrationRequest request = createRequest();

        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        //Get all customers
        List<CustomerDTO> customers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        //Make sure that customer os present
        int customerId = customers.stream()
                .filter(customer -> customer.email().equals(request.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerDTO expectedCustomer = new CustomerDTO(customerId, request.name(), request.email(), request.gender(),
                request.age(), List.of("ROLE_USER"), request.email());

        assertThat(customers).contains(expectedCustomer);

        //Get customer by id
        webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void deleteCustomer() {
        //Create customer registration request
        CustomerRegistrationRequest request = createRequest();

        CustomerRegistrationRequest request2 = createRequest();

        // send a post request to create customer 1
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // send a post request to create customer 2
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        //Get all customers
        List<CustomerDTO> customers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        //Customer 2 deletes customer 1
        int customerId = customers.stream()
                .filter(customer -> customer.email().equals(request.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri(CUSTOMER_PATH + "/{id}", customerId)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //Customer 2 gets customer 1 by id
        webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateCustomer() {
        //Create customer registration request
        CustomerRegistrationRequest request = createRequest();

        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        //Get all customers
        List<CustomerDTO> customers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        //Update customer
        int customerId = customers.stream()
                .filter(customer -> customer.email().equals(request.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Nick", null, null);

        webTestClient.put()
                .uri(CUSTOMER_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //Get customer by id
        CustomerDTO updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerDTO expectedCustomer = new CustomerDTO(customerId, updateRequest.name(), request.email(),
                request.gender(), request.age(), List.of("ROLE_USER"), request.email());

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }

    private CustomerRegistrationRequest createRequest() {
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        return new CustomerRegistrationRequest(name, email, "password", age, gender);
    }
}
