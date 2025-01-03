package com.rouchFirstCode.Customer;

import java.util.List;

public record CustomerDTO(
        Integer id,
        String name,
        String email,
        GenderEnum gender,
        Integer age,
        List<String> roles,
        String username) {
}
