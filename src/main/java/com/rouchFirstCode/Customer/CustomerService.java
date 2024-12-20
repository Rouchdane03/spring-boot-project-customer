package com.rouchFirstCode.Customer;

import com.rouchFirstCode.exception.NothingChangeException;
import com.rouchFirstCode.exception.ResourceAlreadyExistsException;
import com.rouchFirstCode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomerService{
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getListofAllCustomers(){
       return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id){
        return customerDao.selectCustomerById(id).
                orElseThrow(
                        ()-> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));
    }
    public void sendCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        //check if email exists
        if(customerDao.existPersonWithEmail(customerRegistrationRequest.email())){
            throw new ResourceAlreadyExistsException("email existe déjà");
        }
        customerDao.uploadCustomer(new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender()
        ));
    }

    public void cutCustomerWithThisId(Integer id) {
        //check if id exists
        if(customerDao.existPersonWithId(id)){
            customerDao.deleteCustomerById(id);
        }
        else throw new ResourceNotFoundException("customer with id [%s] not found".formatted(id));

    }

    public void updateCustomer(Integer id, CustomerRegistrationRequest customerRegistrationRequest) {
        Customer customer = getCustomerById(id);
        boolean changes = false;

        if(customerRegistrationRequest.name()!=null && !customerRegistrationRequest.name().equals(customer.getName())){
            customer.setName(customerRegistrationRequest.name());
            changes=true;
        }
        if(customerRegistrationRequest.email()!=null && !customerRegistrationRequest.email().equals(customer.getEmail())){
            if(customerDao.existPersonWithEmail(customerRegistrationRequest.email())){
                throw new ResourceAlreadyExistsException("email existe déjà");
            }
            customer.setEmail(customerRegistrationRequest.email());
            changes=true;
        }
        if(customerRegistrationRequest.age()!=null && !customerRegistrationRequest.age().equals(customer.getAge())){
            customer.setAge(customerRegistrationRequest.age());
            changes=true;
        }
        if(customerRegistrationRequest.gender()!=null && !customerRegistrationRequest.gender().equals(customer.getGender())){
            customer.setGender(customerRegistrationRequest.gender());
            changes=true;
        }

        if(!changes){
            throw new NothingChangeException("pas de changement");
        }
        customerDao.updateThisCustomer(customer);


    }
}
