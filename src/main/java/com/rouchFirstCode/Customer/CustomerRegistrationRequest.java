package com.rouchFirstCode.Customer;

public record CustomerRegistrationRequest(String name,
                                          String email,
                                          String password,
                                          Integer age,
                                          GenderEnum gender) {}
