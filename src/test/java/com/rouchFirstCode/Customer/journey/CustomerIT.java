package com.rouchFirstCode.Customer.journey;

import com.github.javafaker.Faker;
import com.rouchFirstCode.Customer.CustomerDTO;
import com.rouchFirstCode.Customer.CustomerRegistrationRequest;
import com.rouchFirstCode.Customer.GenderEnum;
import com.rouchFirstCode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIT {
    
    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();

    private static final String customerURI = "api/v1/customer";

    @Test
    void canAddACustomer() {
        //⬇️the journey of adding customer of our controller class

        //Create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress()+ "_"+UUID.randomUUID()+ "@rouch.com";
        int age = RANDOM.nextInt(1,100);
        GenderEnum gender = GenderEnum.MALE;
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                name,
                email,
                "password", age,
                gender
        );
        //send a post request to our api
        String jwtToken = webTestClient.post()
                .uri(customerURI + "/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        //making sure customer is present
        Integer id = allCustomers.stream()
                .filter(c-> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("id non trouvé"));

        CustomerDTO expectedCustomer = new CustomerDTO(
                id,
                name,
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );
        assertThat(allCustomers).contains(expectedCustomer);

        //get customer by id
         webTestClient.get()
                .uri(customerURI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                 .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .equals(expectedCustomer);
    }

    @Test
    void deleteCustomerById() {
        //⬇️the journey of deleting customer by id of our controller class

        //Create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email1 = faker.internet().emailAddress()+ "_"+UUID.randomUUID()+ "@rouch.com";
        String email2 = email1 + ".fr";

        int age = RANDOM.nextInt(1,100);
        GenderEnum gender = GenderEnum.FEMALE;

        CustomerRegistrationRequest registrationRequest1 = new CustomerRegistrationRequest(
                name,
                email1,
                "password", age,
                gender
        );

        CustomerRegistrationRequest registrationRequest2 = new CustomerRegistrationRequest(
                name,
                email2,
                "password", age,
                gender
        );



        //send a post request to our api FOR ADDING CUSTOMER 1
        webTestClient.post()
                .uri(customerURI + "/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest1), CustomerRegistrationRequest.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk();


        //send a post request to our api FOR ADDING CUSTOMER 2 (here we will extract the token)
        String jwtToken = webTestClient.post()
                .uri(customerURI + "/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest2), CustomerRegistrationRequest.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        //make sure customer we add is present
        Integer idCustomer1 = allCustomers.stream()
                .filter(c-> c.email().equals(email1))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("id non trouvé"));

        //DELETE THE CUSTOMER 1 using CUSTOMER 2 token📌
        webTestClient.delete()
                .uri(customerURI+"/{id}",  idCustomer1)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk();

        //trying to get customer 1 by id using again Customer 2 token
        webTestClient.get()
                .uri(customerURI+"/{id}",  idCustomer1)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateCustomerById() {
        //⬇️the journey ou updating customer by id of our controller class

        //Create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress()+ "_"+UUID.randomUUID()+ "@rouch.com";
        int age = RANDOM.nextInt(1,100);
        GenderEnum gender = GenderEnum.MALE;
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                name,
                email,
                "password", age,
                gender
        );
        //send a post request to our api
        String jwtToken = webTestClient.post()
                .uri(customerURI + "/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        //looking for the id of the customer we add
        Integer id = allCustomers.stream()
                .filter(c-> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("id non trouvé"));

        CustomerRegistrationRequest update = new CustomerRegistrationRequest("newName",null, "password", null,null);

        //UPDATE customer with this id
        webTestClient.put()
                .uri(customerURI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(update), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        CustomerDTO theUpdatedCustomer = webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s",jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

                //making sure customer is updated
        CustomerDTO customerExpected = new CustomerDTO(
                id,
                "newName",
                email,
                gender,
                age,
                List.of("ROLE_USER"),
                email
        );
        assertThat(theUpdatedCustomer.id())
                .isEqualTo(customerExpected.id());
        assertThat(theUpdatedCustomer.name()).isEqualTo(customerExpected.name());
        assertThat(theUpdatedCustomer.email()).isEqualTo(customerExpected.email());
        assertThat(theUpdatedCustomer.age()).isEqualTo(customerExpected.age());
        assertThat(theUpdatedCustomer.gender()).isEqualTo(customerExpected.gender());

    }
}
