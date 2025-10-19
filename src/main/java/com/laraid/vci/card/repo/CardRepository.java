package com.laraid.vci.card.repo;

import com.laraid.vci.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByWalletId(Long walletId);
}

