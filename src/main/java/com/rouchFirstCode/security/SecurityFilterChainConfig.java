package com.rouchFirstCode.security;

import com.rouchFirstCode.exception.DelegatedAuthEntryPoint;
import com.rouchFirstCode.jwt.JWtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilterChainConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JWtAuthenticationFilter jwtAuthenticationFilter;

    private final DelegatedAuthEntryPoint delegatedAuthEntryPoint;

    public SecurityFilterChainConfig(AuthenticationProvider authenticationProvider, JWtAuthenticationFilter jwtAuthenticationFilter, DelegatedAuthEntryPoint delegatedAuthEntryPoint) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.delegatedAuthEntryPoint = delegatedAuthEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf->csrf.disable())// Désactiver CSRF avec la nouvelle syntaxe
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/customer/create", "/api/v1/auth/login", "/ping")
                        .permitAll()// Autoriser le POST sur cette route
                        .requestMatchers(HttpMethod.GET,"/ping")
                        .permitAll()
                        .anyRequest() //any other request
                        .authenticated()) // Tout le reste(other requests) nécessite une authentification
                        .sessionManagement(
                                session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .exceptionHandling(c -> c.authenticationEntryPoint(delegatedAuthEntryPoint));

       return http.build();
    }


}
