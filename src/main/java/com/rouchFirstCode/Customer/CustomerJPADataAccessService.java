package com.rouchFirstCode.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao{
    private final CustomerRepository customerRepository;

    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    @Override
    public void uploadCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    @Override
    public void deleteCustomerById(Integer id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean existPersonWithId(Integer id) {
        return customerRepository.existsCustomerById(id);
    }

    @Override
    public void updateThisCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> loadUserByHisUserName(String email) {
        return customerRepository.findCustomerByEmail(email);
    }
}
