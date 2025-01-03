package com.rouchFirstCode.auth.journey;

import com.github.javafaker.Faker;
import com.rouchFirstCode.Customer.CustomerDTO;
import com.rouchFirstCode.Customer.CustomerRegistrationRequest;
import com.rouchFirstCode.Customer.GenderEnum;
import com.rouchFirstCode.auth.UserAuthReponse;
import com.rouchFirstCode.auth.UserIdentifiers;
import com.rouchFirstCode.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogUserIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    private final Random RANDOM = new Random();

    private static final String customerURI = "api/v1/customer";

    private static final String logURI = "api/v1/auth";


    @Test
    void canLoginUser() {
        //⬇️the journey of login an user of our controller class

        //Create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress()+ "_"+ UUID.randomUUID()+ "@rouch.com";
        int age = RANDOM.nextInt(1,100);
        GenderEnum gender = GenderEnum.MALE;
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                name,
                email,
                "password", age,
                gender
        );
        //Getting user login credentials
        UserIdentifiers userIdentifiers = new UserIdentifiers(registrationRequest.email(),registrationRequest.password());

        //Tryng to ligin an unexisting user
        webTestClient.post()
                .uri(logURI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userIdentifiers), UserIdentifiers.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isUnauthorized();

        //send a post request to our api
       webTestClient.post()
                .uri(customerURI + "/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk();

        //📌retry LOGIN THE USER
        EntityExchangeResult<UserAuthReponse> result = webTestClient.post()
                .uri(logURI + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userIdentifiers), UserIdentifiers.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<UserAuthReponse>() {
                })
                .returnResult();

        //Verifying
        String tokenAfterLogged = result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        CustomerDTO customerDTO = result.getResponseBody().customerDTO();

        assertThat(jwtUtil.isTokenValid(tokenAfterLogged,customerDTO.username())).isTrue(); //for the token

        assertThat(customerDTO.email()).isEqualTo(email);  //for the body (UserAuthResponse(token,customerDTO))
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }
}
