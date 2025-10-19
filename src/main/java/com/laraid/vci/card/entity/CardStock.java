package com.laraid.vci.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pan;

    private String cvv;

    private String expiry;

    private boolean used;
}

