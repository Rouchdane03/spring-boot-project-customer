package com.rouchFirstCode.Customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{
    //db
    private static List<Customer> customers;
    static{
        customers = new ArrayList<>();
        customers.add(new Customer(1,"rouch","adissa",21,GenderEnum.MALE));
        customers.add(new Customer(2,"amakpe","hanane",19,GenderEnum.FEMALE));

    }
    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return  customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();
    }

    @Override
    public void uploadCustomer(Customer customer) {
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        return false;
    }

    @Override
    public void deleteCustomerById(Integer id) {

    }

    @Override
    public boolean existPersonWithId(Integer id) {
        return false;
    }

    @Override
    public void updateThisCustomer(Customer customer) {

    }
}
