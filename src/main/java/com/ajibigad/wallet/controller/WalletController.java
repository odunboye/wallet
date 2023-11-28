package com.ajibigad.wallet.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ajibigad.wallet.entities.WalletTransaction;
import com.ajibigad.wallet.services.WalletService;
import com.payonus.walletlib.dto.WalletDto;

@RestController
public class WalletController {

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    private final WalletService walletService;

    @PostMapping("/process-transactions")
    void processTransactions(@RequestBody List<WalletTransaction> transactions) {
        walletService.processTransactions(transactions);
    }

    @GetMapping("/wallet/{accountNumber}")
    WalletDto getById(@PathVariable String accountNumber) {
        return walletService.getWalletByAccountNumber(accountNumber);
    }

}
