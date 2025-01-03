package com.rouchFirstCode.auth;

import com.rouchFirstCode.Customer.CustomerDTO;

public record UserAuthReponse(
        String token,
        CustomerDTO customerDTO) {
}
