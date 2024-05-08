package com.example.accounts.repositories;

import com.example.accounts.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
