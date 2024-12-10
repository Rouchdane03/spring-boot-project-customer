package com.rouchFirstCode.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    private CustomerRowMapper underTest;
    @Mock
    private ResultSet resultSet;
    @BeforeEach
    void setUp() {
        underTest = new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        //Given
        Mockito.when(resultSet.getInt("id")).thenReturn(1);
        Mockito.when(resultSet.getString("name")).thenReturn("John");
        Mockito.when(resultSet.getString("email")).thenReturn("john@gmail.com");
        Mockito.when(resultSet.getInt("age")).thenReturn(21);
        Mockito.when(resultSet.getString("gender")).thenReturn("MALE");
        //When
        Customer customer = underTest.mapRow(resultSet,1);
        //Then
        assertThat(customer.getId()).isEqualTo(1);
        assertThat(customer.getName()).isEqualTo("John");
        assertThat(customer.getEmail()).isEqualTo("john@gmail.com");
        assertThat(customer.getAge()).isEqualTo(21);
        assertThat(customer.getGender()).isEqualTo(GenderEnum.MALE);
    }
}