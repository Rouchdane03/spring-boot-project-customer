package com.rouchFirstCode.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {


    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();

        // IMPORTANT: pour Vercel Preview il faut des "patterns", pas setAllowedOrigins
        configuration.setAllowedOriginPatterns(List.of(
                "https://front-react-project-customer.vercel.app", // prod
                "https://*.vercel.app",                             // toutes les previews
                "http://localhost:5173"                             // dev local (vite)
        ));

        configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type","Authorization","Accept"));

        configuration.setExposedHeaders(List.of("Location","Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

}
