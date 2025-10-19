package com.laraid.vci.transaction.controller;

import com.laraid.vci.card.event.SpendRequestEvent;
import com.laraid.vci.transaction.dto.SpendRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// ------------ 6. transaction-service (Spend + Kafka) ------------
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private KafkaTemplate<String, SpendRequestEvent> kafkaTemplate;

    @PostMapping("/spend")
    public ResponseEntity<String> spend(@RequestBody SpendRequest request) {
        SpendRequestEvent event = new SpendRequestEvent(request);

        kafkaTemplate.send("txn.spend_requested", event);
        return ResponseEntity.ok("Spend Initiated");
    }
}
