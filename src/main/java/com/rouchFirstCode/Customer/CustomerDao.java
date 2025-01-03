package com.rouchFirstCode.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer id);

    void uploadCustomer(Customer customer);

    boolean existPersonWithEmail(String email);

    void deleteCustomerById(Integer id);

    boolean existPersonWithId(Integer id);

    void updateThisCustomer(Customer customer);

    Optional<Customer> loadUserByHisUserName(String email);
}
