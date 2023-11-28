package com.ajibigad.wallet.repository;

import com.ajibigad.wallet.entities.Wallet;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ajibigad on 08/10/2023 5:17 PM
 */

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({ @QueryHint(name = "jakarta.persistence.lock.timeout", value = "1000") })
    @Query(value = "select w from Wallet w where w.accountNumber IN :accountNumbers order by w.id")
    List<Wallet> findByAccountNumbersWithLock(Set<String> accountNumbers);

    List<Wallet> findByAccountNumberIn(Set<String> accountNumbers);

    Wallet findByAccountNumber(String accountNumber);
}