package com.laraid.vci.card.controller;

import com.laraid.vci.card.dto.CardActivationRequest;
import com.laraid.vci.card.dto.CardIssueRequest;
import com.laraid.vci.card.dto.CardResponse;
import com.laraid.vci.card.entity.Card;
import com.laraid.vci.card.service.CardService;
import com.laraid.vci.ledger.service.LedgerListener;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private static final Logger log
            = LoggerFactory. getLogger(CardController.class);

   private final CardService cardService;

    @PostMapping("/issue")
    public ResponseEntity<CardResponse> issueCard(@RequestBody CardIssueRequest request,  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        log.info(" Inside CardController issueCard :",request);
        // Extract the JWT token (remove "Bearer ")
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        CardResponse card = cardService.issueCard(request,jwtToken).block();
        return ResponseEntity.ok(card);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CardResponse>> allCards(){
        log.info(" Inside CardController all cards :");
        return ResponseEntity.ok(cardService.getAllCards());
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<CardResponse> activateCard(
            @PathVariable Long id,
            @RequestBody CardActivationRequest request
    ) {
        return ResponseEntity.ok(cardService.activateCard(id, request));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Card> block(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<Card> suspend(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.suspendCard(id));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Card> close(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.closeCard(id));
    }
}
