package com.sunBase.CustomersManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunBase.CustomersManagement.model.Customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
    @Query("SELECT c FROM Customer c WHERE LOWER(c.first_name) LIKE LOWER(CONCAT('%', :first_name, '%'))")
    Page<Customer> searchByFirstName(@Param("first_name") String first_name, Pageable pageable);
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Customer> findByPhoneContainingIgnoreCase(String phone, Pageable pageable);
    Page<Customer> findByCityContainingIgnoreCase(String city, Pageable pageable);
    boolean existsByEmail(String email);
    Optional<Customer> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
    void deleteByUuid(String uuid);
}