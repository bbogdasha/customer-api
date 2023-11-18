package com.bogdan.fullstackproject.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * OncePerRequestFilter is a class in the Spring Security framework that ensures a
 * particular filter is only executed once per request.
 * ===
 * Override doFilterInternal method: is called by the Spring Security filter chain for each
 * incoming HTTP request. The primary purpose is to extract the JWT from the request's "Authorization"
 * header, validate it, and set up the Authentication object in the SecurityContextHolder. If the request
 * doesn't contain a valid JWT, it allows the request to proceed down the filter chain.
 * JWT Processing:
 * It checks if the request header contains a valid JWT.
 * If no JWT or it doesn't start with "Bearer ", the filter proceeds with the filter chain.
 * If a valid JWT is found, it extracts the subject (presumably the username) from the JWT.
 * It then checks if there is no existing authentication in the SecurityContextHolder for the current request.
 * If the subject is not null, it loads the user details using the UserDetailsService.
 * If the JWT is valid for the user, it constructs an UsernamePasswordAuthenticationToken and sets it in the
 * SecurityContextHolder.
 */

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String subject = jwtUtil.getSubject(jwt);

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
