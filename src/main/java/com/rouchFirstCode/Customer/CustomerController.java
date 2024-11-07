package com.rouchFirstCode.Customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public List<Customer> getlistOfCustomers(){
        return customerService.getListofAllCustomers();
    }
    @GetMapping("/{id}")
    public Customer findCustomerById(@PathVariable("id") Integer id){
        return customerService.getCustomerById(id);
    }
    @PostMapping("/create")
    public void addCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest){
        customerService.sendCustomer(customerRegistrationRequest);
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
