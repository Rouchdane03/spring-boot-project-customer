package com.rouchFirstCode.Customer;

import com.rouchFirstCode.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil=jwtUtil;
    }

    @GetMapping()
    public List<CustomerDTO> getlistOfCustomers(){
        return customerService.getListofAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO findCustomerById(@PathVariable("id") Integer id){
        return customerService.getCustomerById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> addCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        customerService.sendCustomer(customerRegistrationRequest);
        String myToken = jwtUtil.issueToken(customerRegistrationRequest.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, myToken)
                .build();
    }

    @DeleteMapping("/{id}")
    public void deleteCustomerById(@PathVariable("id") Integer id){
        customerService.cutCustomerWithThisId(id);
    }

    @PutMapping("/{id}")
    public void updateCustomerById(@PathVariable("id") Integer id, @RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        customerService.updateCustomer(id,customerRegistrationRequest);
    }

}
