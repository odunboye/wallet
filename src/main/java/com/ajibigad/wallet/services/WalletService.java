package com.ajibigad.wallet.services;

import com.ajibigad.wallet.entities.Wallet;
import com.ajibigad.wallet.entities.WalletTransaction;
import com.ajibigad.wallet.repository.WalletRepository;
import com.ajibigad.wallet.repository.WalletTransactionRepository;
import com.ajibigad.wallet.utils.BigDecimalUtils;
import com.payonus.walletlib.dto.WalletDto;
import com.payonus.walletlib.enums.Currency;
import com.payonus.walletlib.enums.TransactionType;
import com.payonus.walletlib.enums.WalletStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ajibigad on 08/10/2023 4:11 PM
 */

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    WalletTransactionRepository walletTransactionRepository;

    @Autowired
    LienService lienService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTransactions(List<WalletTransaction> transactions) {
        // Begin session
        // Lock all wallets in these transactions
        // Get the balance on each wallet
        // Create the wallet transactions with the right balance before and after
        // Commit the session
        // Catch LockTimeoutException and raise another exception so active transaction
        // rolls back

        Set<String> accountNumbers = transactions.stream().map(WalletTransaction::getAccountNumber)
                .collect(Collectors.toSet());

        Map<String, List<WalletTransaction>> walletToTransactionMap = new HashMap<>();
        transactions.forEach(walletTransaction -> {
            var txns = walletToTransactionMap.computeIfAbsent(walletTransaction.getAccountNumber(),
                    k -> new ArrayList<>());
            txns.add(walletTransaction);
        });

        // SELECT wallets for update; Pessimistic write lock
        List<Wallet> wallets = walletRepository.findByAccountNumbersWithLock(accountNumbers);
        if (wallets.size() != accountNumbers.size()) {
            throw new IllegalStateException("Invalid accounts numbers. All account numbers must exist");
        }

        for (Wallet wallet : wallets) {
            for (WalletTransaction walletTransaction : walletToTransactionMap.get(wallet.getAccountNumber())) {
                BigDecimal newBalance = wallet.getBalance().add(getSignAmount(walletTransaction)).setScale(2,
                        BigDecimalUtils.ROUNDING_MODE);
                walletTransaction.setBalanceBefore(wallet.getBalance());
                walletTransaction.setBalanceAfter(newBalance);
                walletTransaction.setSignedAmount(getSignAmount(walletTransaction));
                walletTransaction.setWallet(wallet);

                wallet.setBalance(newBalance);
            }
        }

        walletTransactionRepository.saveAll(transactions);
    }

    private BigDecimal getSignAmount(WalletTransaction walletTransaction) {
        return walletTransaction.getTransactionType() == TransactionType.CREDIT ? walletTransaction.getAmount()
                : walletTransaction.getAmount().negate();
    }

    Wallet createWalletNoPersist(String walletNumber) {
        return Wallet.builder().accountNumber(walletNumber).balance(new BigDecimal(0)).currency(Currency.NGN)
                .status(WalletStatus.ACTIVE)
                .build();
    }

    public WalletDto getWalletByAccountNumber(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber);
        return WalletDto.builder().accountNumber(wallet.getAccountNumber())
                .availableBalance(getAvailableBalance(wallet))
                .ledgerBalance(wallet.getBalance()).currency(wallet.getCurrency()).status(wallet.getStatus())
                .created(wallet.getCreated()).build();
    }

    public BigDecimal getAvailableBalance(Wallet wallet) {
        return wallet.getBalance().subtract(lienService.getTotalLienAmount(wallet));
    }
}
