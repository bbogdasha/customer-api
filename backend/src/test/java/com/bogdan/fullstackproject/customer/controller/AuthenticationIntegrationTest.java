package com.bogdan.fullstackproject.customer.controller;

import com.bogdan.fullstackproject.auth.AuthenticationRequest;
import com.bogdan.fullstackproject.auth.AuthenticationResponse;
import com.bogdan.fullstackproject.customer.dto.CustomerDTO;
import com.bogdan.fullstackproject.customer.model.CustomerRegistrationRequest;
import com.bogdan.fullstackproject.customer.model.Gender;
import com.bogdan.fullstackproject.jwt.JWTUtil;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthenticationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    private static final Random RANDOM = new Random();

    private static final String CUSTOMER_PATH = "/api/v1/customers";

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    @Test
    void loginTest() {
        //Create customer registration registrationRequest
        CustomerRegistrationRequest registrationRequest = createRequest();

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                registrationRequest.email(),
                registrationRequest.password());

        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {})
                .returnResult();

        String jwtToken = result.getResponseHeaders().get(AUTHORIZATION).get(0);

        AuthenticationResponse authenticationResponse = result.getResponseBody();
        CustomerDTO customerDTO = authenticationResponse.customerDTO();

        assertThat(jwtUtil.isTokenValid(jwtToken, customerDTO.username())).isTrue();

        assertThat(customerDTO.email()).isEqualTo(registrationRequest.email());
        assertThat(customerDTO.age()).isEqualTo(registrationRequest.age());
        assertThat(customerDTO.name()).isEqualTo(registrationRequest.name());
        assertThat(customerDTO.username()).isEqualTo(registrationRequest.email());
        assertThat(customerDTO.gender()).isEqualTo(registrationRequest.gender());
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
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
