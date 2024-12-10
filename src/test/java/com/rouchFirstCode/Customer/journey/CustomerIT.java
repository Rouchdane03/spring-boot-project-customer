package com.rouchFirstCode.Customer.journey;

import com.github.javafaker.Faker;
import com.rouchFirstCode.Customer.Customer;
import com.rouchFirstCode.Customer.CustomerRegistrationRequest;
import com.rouchFirstCode.Customer.GenderEnum;
import com.rouchFirstCode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
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
                age,
                gender
        );
        //send a post request to our api
         webTestClient.post()
                 .uri(customerURI+"/create")
                 .accept(MediaType.APPLICATION_JSON)
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                 .exchange()     //c'est à ce niveau qu'il envoie
                 .expectStatus()
                 .isOk();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        //make sure customer is present
        Customer expectedCustomer = new Customer(name, email, age,gender);
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);



        Integer id = allCustomers.stream()
                .filter(c-> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("id non trouvé"));

expectedCustomer.setId(id);
        //get customer by id
         webTestClient.get()
                .uri(customerURI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .equals(expectedCustomer);
    }

    @Test
    void deleteCustomerById() {
        //⬇️the journey of deleting customer by id of our controller class

        //Create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress()+ "_"+UUID.randomUUID()+ "@rouch.com";
        int age = RANDOM.nextInt(1,100);
        GenderEnum gender = GenderEnum.FEMALE;
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                name,
                email,
                age,
                gender
        );
        //send a post request to our api
        webTestClient.post()
                .uri(customerURI+"/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //make sure customer we add is present
        Integer id = allCustomers.stream()
                .filter(c-> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("id non trouvé"));

        //DELETE THE CUSTOMER📌
        webTestClient.delete()
                        .uri(customerURI+"/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .expectStatus()
                        .isOk();

        //get customer by id
        webTestClient.get()
                .uri(customerURI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
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
                age,
                gender
        );
        //send a post request to our api
        webTestClient.post()
                .uri(customerURI+"/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()     //c'est à ce niveau qu'il envoie
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(customerURI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        //looking for the id of the customer we add
        Integer id = allCustomers.stream()
                .filter(c-> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("id non trouvé"));

        CustomerRegistrationRequest update = new CustomerRegistrationRequest("newName",null,null,null);
        //UPDATE customer with this id
        webTestClient.put()
                .uri(customerURI+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(update), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        //get customer by id
        Customer theUpdatedCustomer = webTestClient.get()
                .uri(customerURI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

                //making sure customer is updated
        Customer customerExpected = new Customer(id, "newName", email,age,gender);
        assertThat(theUpdatedCustomer.getId())
                .isEqualTo(customerExpected.getId());
        assertThat(theUpdatedCustomer.getName()).isEqualTo(customerExpected.getName());
        assertThat(theUpdatedCustomer.getEmail()).isEqualTo(customerExpected.getEmail());
        assertThat(theUpdatedCustomer.getAge()).isEqualTo(customerExpected.getAge());
        assertThat(theUpdatedCustomer.getGender()).isEqualTo(customerExpected.getGender());

    }
}
