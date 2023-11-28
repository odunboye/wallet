package com.ajibigad.wallet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajibigad.wallet.services.CustomerService;
import com.payonus.walletlib.dto.CreateWalletRequest;
import com.payonus.walletlib.dto.CreateWalletResponse;
import com.payonus.walletlib.dto.CustomerDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    private final CustomerService customerService;

    @PostMapping("/create")
    CreateWalletResponse create(@Valid @RequestBody CreateWalletRequest request) {
        return customerService.create(request);
    }

    @GetMapping("/{id}")
    CustomerDto getById(@PathVariable String id) {
        return customerService.getById(id);
    }

}
