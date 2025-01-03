package com.rouchFirstCode.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("#{'*'.split(',')}")
    private List<String> allowedOrigins;

    @Value("#{'*'.split(',')}")
    private List<String> allowedMethods;

    @Value("#{'*'.split(',')}")
    private List<String> allowedHeaders;

    @Value("#{'*'.split(',')}")
    private List<String> exposedHeaders;


    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        //configuration.addAllowedHeader(HttpHeaders.AUTHORIZATION);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(exposedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    /*  When we don't need implement spring security
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/api/**");
        allowedOrigins.forEach(corsRegistration::allowedOrigins);
        allowedMethods.forEach(corsRegistration::allowedMethods);
    }
*/
}
