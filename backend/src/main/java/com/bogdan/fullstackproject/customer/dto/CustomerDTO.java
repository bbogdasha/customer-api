package com.bogdan.fullstackproject.customer.dto;

import com.bogdan.fullstackproject.customer.model.Gender;

import java.util.List;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        Gender gender,
        Integer age,
        List<String> roles,
        String username

) {
}
