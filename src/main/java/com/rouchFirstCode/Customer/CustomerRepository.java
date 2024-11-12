package com.rouchFirstCode.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {

//ceci c'est du jpql
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Integer id);
}
