package com.bogdan.fullstackproject.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * The ExceptionTranslationFilter implements an interface that serves as the initial filter
 * for Spring security . It acts as the entry point to verify if a user is authenticated and
 * either logs them in or throws an unauthorized exception.
 */

@Component
public class DelegatedAuthEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public DelegatedAuthEntryPoint(@Qualifier("handlerExceptionResolver")
                                   HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        handlerExceptionResolver.resolveException(request, response, null, authException);
    }
}
