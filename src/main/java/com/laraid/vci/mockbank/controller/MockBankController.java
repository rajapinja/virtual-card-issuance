package com.laraid.vci.mockbank.controller;

import com.laraid.vci.card.dto.MockBankCardRequest;
import com.laraid.vci.mockbank.dto.MockBankCardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/mock-bank")
public class MockBankController {

    @PostMapping("/provision-card")
    public ResponseEntity<MockBankCardResponse> provision(@RequestBody MockBankCardRequest request) {
        MockBankCardResponse response = MockBankCardResponse.builder()
                .pan("411111" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10))
                .cvv(String.format("%03d", new Random().nextInt(1000)))
                .expiry(LocalDate.now().plusYears(3).toString())
                .cardToken("TK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase())
                .build();

        return ResponseEntity.ok(response);
    }
}

