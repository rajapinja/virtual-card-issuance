package com.laraid.vci.card.cardlimit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraid.vci.card.cardlimit.LimitCalculatorService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/card-limits")
@RequiredArgsConstructor
public class CardLimitController {

    private final LimitCalculatorService limitCalculatorService;

    @PostMapping("/calculate")
    public ResponseEntity<CardLimitResponse> calculateLimit(@RequestBody CardLimitRequest request) {
        BigDecimal limit = limitCalculatorService.calculateLimit(request.monthlySalary());
        return ResponseEntity.ok(new CardLimitResponse(limit));
    }
}

