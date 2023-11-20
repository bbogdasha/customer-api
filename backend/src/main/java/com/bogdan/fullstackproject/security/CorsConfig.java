package com.bogdan.fullstackproject.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * CorsConfigurationSource is an interface in the Spring Framework that defines methods for
 * obtaining a CorsConfiguration based on a particular request. CORS (Cross-Origin Resource Sharing)
 * is a security feature implemented by web browsers that controls how web pages in one domain can
 * request and interact with resources hosted in another domain.
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // List of allowed origins for CORS.
    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    // List of allowed HTTP methods.
    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    // List of allowed HTTP headers.
    @Value("#{'${cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;

    // List of headers exposed to the client
    @Value("#{'${cors.exposed-headers}'.split(',')}")
    private List<String> expectedHeaders;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(expectedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
