package com.bogdan.fullstackproject.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
