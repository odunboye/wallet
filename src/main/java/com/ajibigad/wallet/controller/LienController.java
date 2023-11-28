package com.ajibigad.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ajibigad.wallet.services.LienService;
import com.payonus.walletlib.dto.CreateLienRequest;

import jakarta.validation.Valid;

@RestController
public class LienController {

    @Autowired
    private LienService lienService;

    @PostMapping("/lien")
    String lien(@Valid @RequestBody CreateLienRequest request) {
        return lienService.create(request);
    }

    @GetMapping("/lien/{lienReference}")
    void get(@PathVariable String lienReference) {
        lienService.get(lienReference);
    }

    @PutMapping("/lien/settle/{lienReference}")
    void unlien(@PathVariable String lienReference) {
        lienService.settle(lienReference);
    }

}
