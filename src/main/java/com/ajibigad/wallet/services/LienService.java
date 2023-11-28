package com.ajibigad.wallet.services;

import com.ajibigad.wallet.entities.Lien;
import com.ajibigad.wallet.entities.Wallet;
import com.ajibigad.wallet.repository.LienRepository;
import com.ajibigad.wallet.repository.WalletRepository;
import com.payonus.walletlib.dto.CreateLienRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * Created by ajibigad on 08/10/2023 4:11 PM
 */

@Service
public class LienService {

    public LienService(LienRepository lienRepository, WalletRepository walletRepository) {
        this.lienRepository = lienRepository;
        this.walletRepository = walletRepository;
    }

    LienRepository lienRepository;
    WalletRepository walletRepository;

    public String create(CreateLienRequest request) {
        Wallet wallet = walletRepository.findByAccountNumber(request.getAccountNumber());
        if (wallet == null) {
            throw new IllegalArgumentException("Invalid wallet number");
        }
        if (wallet.getBalance().subtract(getTotalLienAmount(wallet)).compareTo(request.getAmount()) <= 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        Lien lien = Lien.builder().wallet(wallet).amount(request.getAmount()).build();
        lienRepository.save(lien);
        return lien.getId().toString();
    }

    public Lien get(String lienReference) {
        return getLien(lienReference);
    }

    public void settle(String lienReference) {
        Lien lien = getLien(lienReference);
        lien.setSettled(true);
        lienRepository.save(lien);
    }

    private Lien getLien(String lienReference) {
        Optional<Lien> optionalLien = lienRepository.findById(UUID.fromString(lienReference));
        if (optionalLien.isEmpty()) {
            throw new IllegalArgumentException("Invalid lien reference");
        }
        return optionalLien.get();
    }

    public BigDecimal getTotalLienAmount(Wallet wallet) {
        List<Lien> liens = lienRepository.findByWalletAndIsSettled(wallet, false);
        return liens.stream()
                .map(Lien::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
