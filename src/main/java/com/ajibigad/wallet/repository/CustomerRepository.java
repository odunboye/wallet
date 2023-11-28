package com.ajibigad.wallet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ajibigad.wallet.entities.Customer;

@Component
@Service
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Customer findByEmailOrPhoneNumber(String email, String phoneNumber);
}
