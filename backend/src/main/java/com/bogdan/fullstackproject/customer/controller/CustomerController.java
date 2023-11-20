package com.bogdan.fullstackproject.customer.controller;

import com.bogdan.fullstackproject.customer.dto.CustomerDTO;
import com.bogdan.fullstackproject.customer.model.Customer;
import com.bogdan.fullstackproject.customer.model.CustomerRegistrationRequest;
import com.bogdan.fullstackproject.customer.model.CustomerUpdateRequest;
import com.bogdan.fullstackproject.customer.service.CustomerService;
import com.bogdan.fullstackproject.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public CustomerDTO getCustomer(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.generateAccessToken(request.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer customerId,
                               @RequestBody CustomerUpdateRequest updateRequest) {
        customerService.updateCustomer(customerId, updateRequest);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer customerId) {
        customerService.deleteCustomer(customerId);
    }
}
