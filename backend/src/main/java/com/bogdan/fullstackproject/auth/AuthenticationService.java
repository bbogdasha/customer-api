package com.bogdan.fullstackproject.auth;

import com.bogdan.fullstackproject.customer.dto.CustomerDTO;
import com.bogdan.fullstackproject.customer.mapper.CustomerMapper;
import com.bogdan.fullstackproject.customer.model.Customer;
import com.bogdan.fullstackproject.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final CustomerMapper customerMapper;

    private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 CustomerMapper customerMapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerMapper = customerMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        Customer principal = (Customer) authentication.getPrincipal();
        CustomerDTO customerDTO = customerMapper.apply(principal);
        String token = jwtUtil.generateAccessToken(customerDTO.username(), customerDTO.roles());

        return new AuthenticationResponse(token, customerDTO);
    }
}
