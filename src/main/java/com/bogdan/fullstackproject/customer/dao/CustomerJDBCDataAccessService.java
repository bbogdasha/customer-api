package com.bogdan.fullstackproject.customer.dao;

import com.bogdan.fullstackproject.customer.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT id, name, email, age 
                FROM customer
                """;

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        String sql = """
                SELECT id, name, email, age 
                FROM customer WHERE id = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper, customerId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        String sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        String sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, customerId);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        String sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, customerId);
    }

    @Override
    public void updateCustomer(Customer updateCustomer) {
        if (updateCustomer.getName() != null) {
            String sql = """
                    UPDATE customer
                    SET name = ?
                    WHERE id = ?
                    """;

            jdbcTemplate.update(sql, updateCustomer.getName(), updateCustomer.getId());
        }
        if (updateCustomer.getAge() != null) {
            String sql = """
                    UPDATE customer
                    SET age = ?
                    WHERE id = ?
                    """;

            jdbcTemplate.update(sql, updateCustomer.getAge(), updateCustomer.getId());
        }
        if (updateCustomer.getEmail() != null) {
            String sql = """
                    UPDATE customer
                    SET email = ?
                    WHERE id = ?
                    """;

            jdbcTemplate.update(sql, updateCustomer.getEmail(), updateCustomer.getId());
        }
    }
}
