package com.laraid.vci.wallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.laraid.vci.auth.util.CurrentUser;
import com.laraid.vci.wallet.dto.WalletFundRequest;
import com.laraid.vci.wallet.entity.WalletLedger;
import com.laraid.vci.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/fund")
    public ResponseEntity<String> fundWallet(@RequestHeader("Idempotency-Key") String key,
                                             @Valid @RequestBody WalletFundRequest request) throws JsonProcessingException {
             walletService.fundWallet(key, request);
        return ResponseEntity.ok("Wallet funded successfully");
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getBalance(walletId));
    }

    @GetMapping("/{userId}/ledger")
    public ResponseEntity<List<WalletLedger>> getLedger(@PathVariable String userId) {
        return ResponseEntity.ok(walletService.getLedger(userId));
    }
}

