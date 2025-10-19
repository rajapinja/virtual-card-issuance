package com.laraid.vci.wallet.entity;

import com.laraid.vci.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class WalletLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType txnType;

    private LocalDateTime timestamp = LocalDateTime.now();

}

