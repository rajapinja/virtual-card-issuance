package com.laraid.vci.cardstock.service;

import com.laraid.vci.cardstock.entity.CardStock;
import com.laraid.vci.cardstock.repo.CardStockRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardStockService {

    private final CardStockRepository repository;
    private final EntityManagerFactory entityManagerFactory;



    @PostConstruct
    public void preloadCardStock() {
        if (repository.count() >= 3000) return;

        List<CardStock> cards = new ArrayList<>();
        for (int i = 1; i <= 3000; i++) {
            String number = String.format("PC%010d", i);
            cards.add(new CardStock(null, number, false, null, null));
        }
        repository.saveAll(cards);
        System.out.println("✅ 3000 cards loaded into stock.");
    }

    public Optional<CardStock> getNextAvailableCard() {
        return repository.findFirstByAssignedFalseOrderByIdAsc()
                .map(card -> {
                    card.setAssigned(true);
                    card.setAssignedAt(LocalDateTime.now());
                    return repository.save(card);
                });
    }
}

