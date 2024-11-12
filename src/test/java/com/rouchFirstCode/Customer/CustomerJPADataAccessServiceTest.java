package com.rouchFirstCode.Customer;

import com.amigoscode.AbstractTestContainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        //➡️On a juste à vérifier que les methodes ont éte invoquées
        //Given : ❌pas besoin car la méthode ne prend rien en paramètre

        //When
        underTest.selectAllCustomers();
        //Then
        Mockito.verify(customerRepository, Mockito.times(1)).findAll();
    }

    @Test
    void selectCustomerById() {
        //Given
        int id = 1;
        //When
        underTest.selectCustomerById(id);
        //Then
        Mockito.verify(customerRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void uploadCustomer() {
        //Given

        Customer customer = new Customer(
                "rouch",
                "rouch@gmail.com",
                21
        );
        //When
        underTest.uploadCustomer(customer);
        //Then
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }

    @Test
    void existPersonWithEmail() {
        //Given
        String email = "rouch@gmail.com";
        //When
        underTest.existPersonWithEmail(email);
        //Then
        Mockito.verify(customerRepository, Mockito.times(1)).existsCustomerByEmail(email);

    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 1;
        //When
        underTest.deleteCustomerById(id);
        //Then
        Mockito.verify(customerRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void existPersonWithId() {
        //Given
        int id = 1;
        //When
        underTest.existPersonWithId(id);
        //Then
        Mockito.verify(customerRepository, Mockito.times(1)).existsCustomerById(id);
    }

    @Test
    void updateThisCustomer() {
        //Given
        ;
        Customer customer = new Customer(
               "diss",
                "diss@gmail.com",
                21
        );
        //When
        underTest.uploadCustomer(customer);
        //Then
        Mockito.verify(customerRepository, Mockito.times(1)).save(customer);
    }
}