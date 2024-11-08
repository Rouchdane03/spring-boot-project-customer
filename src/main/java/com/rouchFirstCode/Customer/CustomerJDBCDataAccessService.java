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
                SELECT id, name, email, age from customer;
                """;
       return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age from customer WHERE id = ?
                """;
        return jdbcTemplate.query(sql,customerRowMapper,id).stream().findFirst();
    }

    @Override
    public void uploadCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email,age) VALUES (?,?,?)
                """;
       int result= jdbcTemplate.update(sql,customer.getName(),customer.getEmail(),customer.getAge());
      System.out.println("jdbcTemplate.update.insert = "+result);
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        var sql = """
                SELECT * from customer WHERE email = ?
                """;
        return jdbcTemplate.query(sql,customerRowMapper,email)
                .stream()
                .anyMatch(customer -> customer.getEmail() ==email);
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
                .anyMatch(customer -> customer.getId() ==id);
    }

    @Override
    public void updateThisCustomer(Customer customer) {
        var sql = """
               UPDATE customer
               SET name = ?,
                 email = ?,
                 age = ?
               WHERE id = ?
                """;
        int result= jdbcTemplate.update(sql,customer.getName(),customer.getEmail(),customer.getAge(),customer.getId());
        System.out.println("jdbcTemplate.update.mÀJ = "+result);
    }
}
