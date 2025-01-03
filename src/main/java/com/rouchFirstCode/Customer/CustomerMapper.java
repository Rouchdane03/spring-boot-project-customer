package com.rouchFirstCode.Customer;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CustomerMapper implements Function<Customer,CustomerDTO> {


    @Override
    public CustomerDTO apply(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getGender(),
                customer.getAge(),
                customer.getAuthorities().stream().map(r -> r.getAuthority()).toList(),
                customer.getUsername()
        );
    }
}
