package com.ajibigad.wallet.repository;

import com.ajibigad.wallet.entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by ajibigad on 08/10/2023 6:57 PM
 */

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    @Query("select SUM(wt.signedAmount) from WalletTransaction wt where wt.accountNumber = :accountNumber")
    BigDecimal getRolledUpBalance(String accountNumber);
}
