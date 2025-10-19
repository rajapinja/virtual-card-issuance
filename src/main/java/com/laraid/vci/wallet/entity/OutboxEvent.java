package com.laraid.vci.wallet.entity;

import com.laraid.vci.wallet.dto.WalletFundRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

//CREATE TABLE outbox_event (
//    id UUID PRIMARY KEY,
//    aggregate_id VARCHAR, -- e.g., walletId
//    event_type VARCHAR,   -- e.g., WalletFundedEvent
//    payload JSONB,
//    status VARCHAR DEFAULT 'PENDING',
//    created_at TIMESTAMP
//);
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private UUID id = UUID.randomUUID();

    private String aggregateId; //walletId
    private String eventType; //WalletFundedEvent
    private String status = "PENDING";
    private LocalDateTime createdAt;

    @Lob
    private String payload; // Store as serialized JSON


}
