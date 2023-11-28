package com.ajibigad.wallet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ajibigad.wallet.entities.Lien;
import com.ajibigad.wallet.entities.Wallet;

import java.util.List;

@Component
@Service
public interface LienRepository extends JpaRepository<Lien, UUID> {

    List<Lien> findByWallet(Wallet wallet);

    List<Lien> findByWalletAndIsSettled(Wallet wallet, boolean isSettled);

}
