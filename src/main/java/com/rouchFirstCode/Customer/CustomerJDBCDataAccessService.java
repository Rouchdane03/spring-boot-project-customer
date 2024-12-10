package com.rouchFirstCode.Customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{

     private final JdbcTemplate jdbcTemplate;
     private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age, gender from customer;
                """;
       return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age, gender from customer WHERE id = ?
                """;
        return jdbcTemplate.query(sql,customerRowMapper,id).stream().findFirst();
    }

    @Override
    public void uploadCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email,age,gender) VALUES (?,?,?,?)
                """;
       int result= jdbcTemplate.update(sql,customer.getName(),customer.getEmail(),customer.getAge(),customer.getGender().name());
      System.out.println("jdbcTemplate.update.insert = "+result);
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        var sql = """
                SELECT * from customer WHERE email = ?
                """;
        return jdbcTemplate.query(sql,customerRowMapper,email)
                .stream()
                .anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE FROM customer WHERE id = ?
                """;
        int result= jdbcTemplate.update(sql,id);
        System.out.println("jdbcTemplate.delete = "+result);
    }

    @Override
    public boolean existPersonWithId(Integer id) {
        var sql = """
                SELECT * from customer WHERE id = ?
                """;
        return jdbcTemplate.query(sql,customerRowMapper,id)
                .stream()
                .anyMatch(customer -> customer.getId()==id);
    }

    @Override
    public void updateThisCustomer(Customer customer) {
        if (customer.getName() != null) {
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getName(),
                    customer.getId()
            );
            System.out.println("update customer name result = " + result);
        }
        if (customer.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getAge(),
                    customer.getId()
            );
            System.out.println("update customer age result = " + result);
        }
        if (customer.getEmail() != null) {
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getEmail(),
                    customer.getId());
            System.out.println("update customer email result = " + result);
        }
        if (customer.getGender() != null) {
            String sql = "UPDATE customer SET gender = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    customer.getGender().name(),
                    customer.getId());
            System.out.println("update customer gender result = " + result);
        }
    }
}
