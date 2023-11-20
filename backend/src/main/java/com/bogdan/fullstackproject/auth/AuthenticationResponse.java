package com.bogdan.fullstackproject.auth;

import com.bogdan.fullstackproject.customer.dto.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
) {
}
