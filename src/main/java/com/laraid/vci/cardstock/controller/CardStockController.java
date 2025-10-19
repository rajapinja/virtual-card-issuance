package com.laraid.vci.cardstock.controller;

import com.laraid.vci.cardstock.dto.CardStockResponse;
import com.laraid.vci.cardstock.service.CardStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card-stock")
@RequiredArgsConstructor
public class CardStockController {

    private final CardStockService cardStockService;

    @GetMapping("/next")
    public ResponseEntity<CardStockResponse> getNextCardNumber() {
        return cardStockService.getNextAvailableCard()
                .map(card -> ResponseEntity.ok(new CardStockResponse(card.getCardNumber())))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new CardStockResponse("NO-CARD")));
    }

//    @PostMapping("/provision-card")
//    public ResponseEntity<MockBankCardResponse> provision(@RequestBody MockBankCardRequest request) {
//        MockBankCardResponse response = MockBankCardResponse.builder()
//                .pan("411111" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10))
//                .cvv(String.format("%03d", new Random().nextInt(1000)))
//                .expiry(LocalDate.now().plusYears(3).toString())
//                .cardToken("TK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase())
//                .build();
//
//        return ResponseEntity.ok(response);
//    }

}
