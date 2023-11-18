package com.bogdan.fullstackproject.customer.model;

public record CustomerRegistrationRequest(
        String name,
        String email,
        String password, Integer age,
        Gender gender
) {
}
