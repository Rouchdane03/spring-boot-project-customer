package com.rouchFirstCode.auth;

import com.rouchFirstCode.Customer.Customer;
import com.rouchFirstCode.Customer.CustomerDTO;
import com.rouchFirstCode.Customer.CustomerMapper;
import com.rouchFirstCode.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class LogUserService {

    private final AuthenticationManager authenticationManager; //will allow us to perform Authentication
    private final CustomerMapper customerMapper;
    private final JWTUtil jwtUtil;

    public LogUserService(AuthenticationManager authenticationManager, CustomerMapper customerMapper, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerMapper = customerMapper;
        this.jwtUtil = jwtUtil;
    }

    public UserAuthReponse loginUser(UserIdentifiers userIdentifiers) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userIdentifiers.username(),
                        userIdentifiers.password()
                )
        );
        Customer principal = (Customer)authenticate.getPrincipal(); //cast du type String vers le type Customer
        CustomerDTO customerDTO = customerMapper.apply(principal);

        String myToken = jwtUtil.issueToken(customerDTO.username(), customerDTO.roles());

        return new UserAuthReponse(myToken,customerDTO);
    }
}
