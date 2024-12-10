package com.rouchFirstCode.Customer;

import com.amigoscode.AbstractTestContainers;
import com.rouchFirstCode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainers {
    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;


    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        //Given
        String email = faker.internet().emailAddress()+"_"+ UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                21,
                GenderEnum.FEMALE
        );
        underTest.save(customer);
        //When
        var actual =  underTest.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isTrue();
    }
    @Test
    void existsCustomerByEmailFailsWhenEmailDoesNotExist() {
        //Given
        String email = faker.internet().emailAddress()+"_"+ UUID.randomUUID();
        //When
        var actual =  underTest.existsCustomerByEmail(email);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        //Given
        String email = faker.internet().emailAddress()+"_"+ UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                21,
                GenderEnum.MALE
        );
        underTest.save(customer);

        Integer id =  underTest.findAll().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("rien n'est trouvé comme id"));
        //When
        var actual =  underTest.existsCustomerById(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdWhenIdDoesNotExist() {
        //Given
       int id =-1;
        //When
        var actual =  underTest.existsCustomerById(id);
        //Then
        assertThat(actual).isFalse();
    }
}