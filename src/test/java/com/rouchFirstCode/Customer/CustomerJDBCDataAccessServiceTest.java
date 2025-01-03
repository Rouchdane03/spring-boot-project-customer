package com.rouchFirstCode.Customer;

import com.amigoscode.AbstractTestContainers;
import com.rouchFirstCode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
               getJdbcTemplate(),
                customerRowMapper);
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().emailAddress()+"_"+ UUID.randomUUID(),
                "password", 21,
                GenderEnum.FEMALE
        );
        underTest.uploadCustomer(customer);
        //When
        List<Customer> actual = underTest.selectAllCustomers();
        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Given
        String email = faker.internet().emailAddress()+"_"+UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                GenderEnum.FEMALE
        );
        underTest.uploadCustomer(customer);

    Integer id =  underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow(()-> new ResourceNotFoundException("rien n'est trouvé comme id"));
        //When
        Optional<Customer> actual =  underTest.selectCustomerById(id);

        //Then
         assertThat(actual).isPresent().hasValueSatisfying(c -> {
           assertThat(c.getId()).isEqualTo(id);
           assertThat(c.getName()).isEqualTo(customer.getName());
             assertThat(c.getEmail()).isEqualTo(customer.getEmail());
             assertThat(c.getAge()).isEqualTo(customer.getAge());
         });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        //Given
        Integer id = -1;
        //When
        var actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void uploadCustomer() {
        //Given
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().emailAddress()+"_"+UUID.randomUUID(),
                "password", 21,
                GenderEnum.FEMALE
        );
        //When
        underTest.uploadCustomer(customer);
        //Then
        List<Customer> customers = underTest.selectAllCustomers();
        assertThat(customers).isNotEmpty();
    }

    @Test
    void existPersonWithEmail() {
        //Given
        String email = faker.internet().emailAddress()+"_"+UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 21,
                GenderEnum.FEMALE
        );
        underTest.uploadCustomer(customer);
        //When
        var actual = underTest.existPersonWithEmail(email);
        //Then
        assertThat(actual).isTrue();
    }
    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        // When
        boolean actual = underTest.existPersonWithEmail(email);
        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                GenderEnum.FEMALE
        );
        underTest.uploadCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        underTest.deleteCustomerById(id);
        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void existPersonWithId() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                GenderEnum.FEMALE
        );
        underTest.uploadCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var actual = underTest.existPersonWithId(id);
        // Then
        assertThat(actual).isTrue();
    }
    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        int id = -1;
        // When
        var actual = underTest.existPersonWithId(id);
        // Then
        assertThat(actual).isFalse();
    }
    @Test
    void updateThisCustomer() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                GenderEnum.MALE
        );
        underTest.uploadCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When update with new name, age and email
        Customer update = new Customer();
        update.setId(id);
        update.setName("foo");
        update.setEmail(UUID.randomUUID().toString());
        update.setAge(22);
        underTest.updateThisCustomer(update);
        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(
                c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(update.getName());
                    assertThat(c.getEmail()).isEqualTo(update.getEmail());
                    assertThat(c.getAge()).isEqualTo(update.getAge());
                }
        );
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                "password", 20,
                GenderEnum.MALE
        );
        underTest.uploadCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When update without no changes
        Customer update = new Customer();
        update.setId(id);
        underTest.updateThisCustomer(update);
        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });
    }
}