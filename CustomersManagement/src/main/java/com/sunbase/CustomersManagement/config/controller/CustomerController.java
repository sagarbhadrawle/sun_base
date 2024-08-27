package com.sunBase.CustomersManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sunBase.CustomersManagement.model.Customer;
import com.sunBase.CustomersManagement.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = service.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }
    
    @PostMapping("/bulk-upload")
    public ResponseEntity<List<Customer>> bulkUploadCustomers(@RequestBody List<Customer> customers) {
        List<Customer> updatedOrCreatedCustomers = service.bulkUpsertCustomers(customers);
        return new ResponseEntity<>(updatedOrCreatedCustomers, HttpStatus.OK);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String uuid, @RequestBody Customer customer) {
        Customer updatedCustomer = service.updateCustomer(uuid, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String uuid) {
        Customer customer = service.getCustomerByUuid(uuid);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<Page<Customer>> listCustomers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "name") String searchBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String sort) {
    	
        // Log parameters for debugging
        System.out.println("Search: " + search);
        System.out.println("SearchBy: " + searchBy);
        System.out.println("Page: " + page);
        System.out.println("Size: " + size);
        System.out.println("Sort: " + sort);

        // Set sorting order with no default value
        Sort sortOrder = Sort.unsorted();

        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            sortOrder = Sort.by(Sort.Order.by(sortParams[0]));
            if (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])) {
                sortOrder = sortOrder.descending();
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<Customer> customers = service.listCustomers(search, searchBy, pageable);
        return ResponseEntity.ok(customers);
    }


    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String uuid) {
        service.deleteCustomer(uuid);
        return ResponseEntity.noContent().build();
    }
}
