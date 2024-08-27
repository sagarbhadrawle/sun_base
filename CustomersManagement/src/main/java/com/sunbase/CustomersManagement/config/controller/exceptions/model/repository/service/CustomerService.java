package com.sunBase.CustomersManagement.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sunBase.CustomersManagement.exceptions.ResourceNotFoundException;
import com.sunBase.CustomersManagement.model.Customer;
import com.sunBase.CustomersManagement.repository.CustomerRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Customer createCustomer(Customer customer) {
        // Generate UUID if not provided
        if (customer.getUuid() == null) {
            customer.setUuid(generateUniqueUuid());
        }
        return repository.save(customer);
    }

    @Transactional
    public List<Customer> bulkUpsertCustomers(List<Customer> customers) {
        return customers.stream().map(customer -> {
            if (customer.getUuid() != null && repository.existsByUuid(customer.getUuid())) {
                // Update existing customer
                Customer existingCustomer = repository.findByUuid(customer.getUuid())
                        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with UUID " + customer.getUuid()));
                existingCustomer.setFirst_name(customer.getFirst_name());
                existingCustomer.setLast_name(customer.getLast_name());
                existingCustomer.setStreet(customer.getStreet());
                existingCustomer.setAddress(customer.getAddress());
                existingCustomer.setCity(customer.getCity());
                existingCustomer.setState(customer.getState());
                existingCustomer.setEmail(customer.getEmail());
                existingCustomer.setPhone(customer.getPhone());
                return repository.save(existingCustomer);
            } else {
                // Generate UUID if not provided
                if (customer.getUuid() == null) {
                    customer.setUuid(generateUniqueUuid());
                }
                return repository.save(customer);
            }
        }).collect(Collectors.toList());
    }

    public Customer updateCustomer(String uuid, Customer customer) {
        Customer existingCustomer = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with UUID " + uuid));
        existingCustomer.setFirst_name(customer.getFirst_name());
        existingCustomer.setLast_name(customer.getLast_name());
        existingCustomer.setStreet(customer.getStreet());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setCity(customer.getCity());
        existingCustomer.setState(customer.getState());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());
        return repository.save(existingCustomer);
    }

    public Customer getCustomerByUuid(String uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with UUID " + uuid));
    }

    public Page<Customer> listCustomers(String search, String searchBy, Pageable pageable) {
        switch (searchBy.toLowerCase()) {
            case "name":
                return repository.searchByFirstName(search, pageable);
            case "email":
                return repository.findByEmailContainingIgnoreCase(search, pageable);
            case "phone":
                return repository.findByPhoneContainingIgnoreCase(search, pageable);
            case "city":
                return repository.findByCityContainingIgnoreCase(search, pageable);
            default:
                // Default behavior if searchBy is invalid
                return repository.searchByFirstName(search, pageable);
        }
    }

    @Transactional
    public void deleteCustomer(String uuid) {
        Customer customer = repository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException("Customer not found with uuid " + uuid));
        repository.delete(customer);
    }

    private String generateUniqueUuid() {
        String uuid;
        do {
            uuid = UUID.randomUUID().toString();
        } while (repository.existsByUuid(uuid)); // Ensure uniqueness
        return uuid;
    }
}
