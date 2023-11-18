package com.bogdan.fullstackproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * passwordEncoder: This method creates and returns an instance of PasswordEncoder.
 * In this case, BCryptPasswordEncoder is used, which is one of the PasswordEncoder
 * implementations for password hashing.
 * ===
 * authenticationManager: This method creates and returns an AuthenticationManager.
 * The AuthenticationManager is used to authenticate users. The method uses AuthenticationConfiguration
 * to get the AuthenticationManager.
 * ===
 * authenticationProvider: an Authentication request is processed by an AuthenticationProvider,
 * and a fully authenticated object with full credentials is returned.
 * The standard and most common implementation is the DaoAuthenticationProvider, which retrieves
 * the user details from a simple, read-only user DAO, the UserDetailsService. This User Details Service
 * only has access to the username in order to retrieve the full user entity
 * ===
 * DaoAuthenticationProvider is an AuthenticationProvider implementation that uses a
 * UserDetailsService and PasswordEncoder to authenticate a username and password.
 */

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
}
