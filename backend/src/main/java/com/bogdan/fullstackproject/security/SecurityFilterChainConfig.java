package com.bogdan.fullstackproject.security;

import com.bogdan.fullstackproject.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *  1. `http.csrf(AbstractHttpConfigurer::disable);`: This disables protection against CSRF attacks.
 *  ===
 *  2. `http.authorizeHttpRequests(...);`: This is the method for configuring the access policy.
 *  In this case, I allow all POST requests to the path "/api/v1/customers", and for all other
 *  requests you need to authenticate.
 *  ===
 *  3. `http.sessionManagement(...)`: This line configures the session management strategy.
 *  There are specified to use an STATELESS session strategy, which means that each request should
 *  contain all the necessary information for service, and the server should not store session state.
 *  ===
 *  4. `http.authenticationProvider(...)`: This line sets the `AuthenticationProvider` you provide to
 *  handle authentication.
 *  ===
 *  5.  `http.addFilterBefore(...)`:
 *  This line adds my custom filter before the standard Spring Security filter that handles username and password.
 */

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider,
                                     JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/customers").permitAll()
                        .anyRequest().authenticated());
        http.sessionManagement(sessionAuthenticationStrategy ->
                sessionAuthenticationStrategy.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
