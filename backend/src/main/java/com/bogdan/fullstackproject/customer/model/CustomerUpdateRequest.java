package com.bogdan.fullstackproject.customer.model;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
