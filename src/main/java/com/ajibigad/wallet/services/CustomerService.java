package com.ajibigad.wallet.services;

import com.ajibigad.wallet.entities.Customer;
import com.ajibigad.wallet.entities.Wallet;
import com.ajibigad.wallet.repository.CustomerRepository;
import com.payonus.walletlib.dto.CreateWalletRequest;
import com.payonus.walletlib.dto.CreateWalletResponse;
import com.payonus.walletlib.dto.CustomerDto;
import com.payonus.walletlib.dto.WalletDto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class CustomerService {
        private final CustomerRepository customerRepository;
        private final WalletService walletService;
        private final LienService lienService;

        public CustomerService(CustomerRepository customerRepository, WalletService walletService,
                        LienService lienService) {
                this.customerRepository = customerRepository;
                this.walletService = walletService;
                this.lienService = lienService;
        }

        public CreateWalletResponse create(CreateWalletRequest request) {
                Customer existingCustomer = customerRepository.findByEmailOrPhoneNumber(request.getEmail(),
                                request.getPhoneNumber());
                if (existingCustomer != null) {
                        Wallet wallet = walletService.createWalletNoPersist(request.getWalletNumber());
                        wallet.setCustomer(existingCustomer);
                        existingCustomer.getWallets().add(wallet);
                        customerRepository.save(existingCustomer);
                        return CreateWalletResponse.builder().customerId(existingCustomer.getId().toString())
                                        .walletAccountNumber(wallet.getAccountNumber()).build();
                } else {
                        Customer customer = Customer.builder().email(request.getEmail())
                                        .firstName(request.getFirstName())
                                        .lastName(request.getLastName()).phoneNumber(request.getPhoneNumber())
                                        .build();
                        Wallet wallet = walletService.createWalletNoPersist(request.getWalletNumber());
                        wallet.setCustomer(customer);
                        Set<Wallet> wallets = new HashSet<>();
                        wallets.add(wallet);
                        customer.setWallets(wallets);
                        customerRepository.save(customer);
                        return CreateWalletResponse.builder().customerId(customer.getId().toString())
                                        .walletAccountNumber(wallet.getAccountNumber()).build();
                }

        }

        public CustomerDto getById(String id) {

                Customer customer = customerRepository.findById(UUID.fromString(id))
                                .orElseThrow(() -> new RuntimeException("Customer not found"));
                Set<WalletDto> walletDtos = new HashSet<>();
                customer.getWallets()
                                .forEach(wallet -> walletDtos.add(WalletDto.builder()
                                                .accountNumber(wallet.getAccountNumber())
                                                .ledgerBalance(wallet.getBalance())
                                                .availableBalance(wallet.getBalance()
                                                                .subtract(lienService.getTotalLienAmount(wallet)))
                                                .currency(wallet.getCurrency())
                                                .status(wallet.getStatus()).created(wallet.getCreated()).build()));
                return CustomerDto.builder().email(customer.getEmail()).firstName(customer.getFirstName())
                                .lastName(customer.getLastName()).wallets(walletDtos)
                                .phoneNumber(customer.getPhoneNumber())
                                .created(customer.getCreated()).build();
        }

}
