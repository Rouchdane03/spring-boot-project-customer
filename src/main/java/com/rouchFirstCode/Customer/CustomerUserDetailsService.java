package com.rouchFirstCode.Customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerDao customerDao;

    public CustomerUserDetailsService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

         return customerDao.loadUserByHisUserName(username).
                 orElseThrow(()->new UsernameNotFoundException("Username " +username+ " not found"));
    }
}
