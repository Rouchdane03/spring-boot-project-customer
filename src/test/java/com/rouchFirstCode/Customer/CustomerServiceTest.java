package com.rouchFirstCode.Customer;

import com.rouchFirstCode.exception.NothingChangeException;
import com.rouchFirstCode.exception.ResourceAlreadyExistsException;
import com.rouchFirstCode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final CustomerMapper customerMapper = new CustomerMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao,passwordEncoder,customerMapper);
    }

    @Test
    void getListofAllCustomers() {
        //Given ❌No need , il prend rien en pramètre
        //When
        underTest.getListofAllCustomers();
        //Then
        Mockito.verify(customerDao,Mockito.times(1)).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"rouch","rouch@gmail.com", "password", 21,GenderEnum.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerMapper.apply(customer);

        //When
       underTest.getCustomerById(id);
       CustomerDTO value = underTest.getCustomerById(id);
        //Then
        assertThat(value).isEqualTo(expected);
    }

    @Test
    void canNotGetCustomerById_WillThrowException() {
        //Given
        int id = -1;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //When

        //Then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void sendCustomer() {
        //Given
        String email = "rouch@gmail.com";
        Mockito.when(customerDao.existPersonWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                "rouch",
                email,
                "password", 18,
                GenderEnum.MALE);
        String passwordHashed = "cxdefgvefrg";
        Mockito.when(passwordEncoder.encode(registrationRequest.password())).thenReturn(passwordHashed);

        //When
         underTest.sendCustomer(registrationRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao,Mockito.times(1)).uploadCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(registrationRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(registrationRequest.email());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHashed);
        assertThat(capturedCustomer.getAge()).isEqualTo(registrationRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(registrationRequest.gender());
    }

    @Test
    void sendCustomer_thows_ResourceAlreadyExistsException() {
        //Given
        String email = "rouch@gmail.com";
        Mockito.when(customerDao.existPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                "rouch",
                email,
                "password", 18,
                GenderEnum.MALE);
        //When
        assertThatThrownBy(() -> underTest.sendCustomer(registrationRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("email existe déjà");
        //Then
        Mockito.verify(customerDao,Mockito.never()).uploadCustomer(Mockito.any());
    }

    @Test
    void cutCustomerWithThisId() {
        //Given
        int id = 10;
        Mockito.when(customerDao.existPersonWithId(id)).thenReturn(true);
        //When
        underTest.cutCustomerWithThisId(id);
        //Then
        Mockito.verify(customerDao,Mockito.times(1)).deleteCustomerById(id);
    }

    @Test
    void cutCustomerWithThisId_Throws_ResourceNotFoundException() {
        //Given
        int id = -1;
        Mockito.when(customerDao.existPersonWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(() -> underTest.cutCustomerWithThisId(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
        //Then
       Mockito.verify(customerDao,Mockito.never()).deleteCustomerById(id);
    }

    @Test
    void updateCustomer_All_Properties_Changed() {
        //Given
        Integer id = 10;
        String email = "rouch@gmail.com";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                "rouch_fx",
                email+".fr",
                "password", 22,
                GenderEnum.MALE
        );
        Customer customer = new Customer(id,"rouch",email, "password", 21,GenderEnum.FEMALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDao.existPersonWithEmail(registrationRequest.email())).thenReturn(false);
        //When
        underTest.updateCustomer(id,registrationRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao,Mockito.times(1)).updateThisCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNotNull();
        assertThat(capturedCustomer.getName()).isEqualTo(registrationRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(registrationRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(registrationRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(registrationRequest.gender());

    }

    @Test
    void updateCustomer_Nothing_Changed() {
        //Given
        Integer id = 10;
        String email = "rouch@gmail.com";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                "rouch",
                email,
                "password", 21,
                GenderEnum.FEMALE
        );
        Customer customer = new Customer(id,"rouch",email, "password", 21,GenderEnum.FEMALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
             assertThatThrownBy(() -> underTest.updateCustomer(id, registrationRequest))
                     .isInstanceOf(NothingChangeException.class)
                     .hasMessage("pas de changement");
        //Then
        Mockito.verify(customerDao,Mockito.never()).updateThisCustomer(Mockito.any());
    }

    @Test
    void updateCustomer_Only_Name_Changed() {
        //Given
        Integer id = 10;
        String email = "rouch@gmail.com";
        String name = "rouch";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                name+"_Moudj",
                null,
                "password", null,
                null
        );
        Customer customer = new Customer(id,name,email, "password", 21,GenderEnum.FEMALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        underTest.updateCustomer(id,registrationRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao,Mockito.times(1)).updateThisCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNotNull();
        assertThat(capturedCustomer.getName()).isEqualTo(registrationRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateCustomer_Only_Email_Changed() {
        //Given
        Integer id = 10;
        String email = "rouch@gmail.com";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                null,
                email+".fr",
                "password", null,
                null
        );
        Customer customer = new Customer(id,"rouch",email, "password", 21,GenderEnum.FEMALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDao.existPersonWithEmail(email+".fr")).thenReturn(false);
        //When
        underTest.updateCustomer(id,registrationRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao,Mockito.times(1)).updateThisCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNotNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(registrationRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateCustomer_Only_Age_Changed() {
        //Given
        Integer id = 10;
        String email = "rouch@gmail.com";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                null,
                null,
                "password", 22,
                null
        );
        Customer customer = new Customer(id,"rouch",email, "password", 21,GenderEnum.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        underTest.updateCustomer(id,registrationRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao,Mockito.times(1)).updateThisCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNotNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(registrationRequest.age());
        assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void updateCustomer_Only_Gender_Changed() {
        //Given
        Integer id = 10;
        String email = "rouch@gmail.com";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                null,
                null,
                "password", null,
                GenderEnum.FEMALE
        );
        Customer customer = new Customer(id,"rouch",email, "password", 21,GenderEnum.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        underTest.updateCustomer(id,registrationRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao,Mockito.times(1)).updateThisCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNotNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getGender()).isEqualTo(registrationRequest.gender());
    }

    @Test
    void updateCustomer_Email_Already_Exists_Throws_ResourceAlreadyExistsException() {
        //Given
        Integer id = 10;
        String email = "rouch@gmail.com";
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                null,
                email+".fr",
                "password", null,
                null
        );
        Customer customer = new Customer(id,"rouch",email, "password", 21,GenderEnum.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Mockito.when(customerDao.existPersonWithEmail(email+".fr")).thenReturn(true);
        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id,registrationRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("email existe déjà");
        //Then
        Mockito.verify(customerDao,Mockito.never()).updateThisCustomer(Mockito.any());
    }

}