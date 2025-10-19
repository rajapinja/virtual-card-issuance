package com.laraid.vci.utils;

import com.laraid.vci.card.entity.Card;
import com.laraid.vci.card.repo.CardRepository;
import com.laraid.vci.enums.CardStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class CardExpiryScheduler {

    private final CardRepository cardRepository;

    public CardExpiryScheduler(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?") // daily at 2 AM
    public void expireOldCards() {
        List<Card> cards = cardRepository.findAll();

        LocalDate now = LocalDate.now();

        for (Card card : cards) {
            if (card.getStatus() == CardStatus.ACTIVE || card.getStatus() == CardStatus.ISSUED) {
                YearMonth expiryDate = parseExpiry(card.getValidThrough().toString());
                if (expiryDate.isBefore(YearMonth.now())) {
                    card.setStatus(CardStatus.EXPIRED);
                    cardRepository.save(card);
                }
            }
        }
    }

    private YearMonth parseExpiry(String expiry) {
        String[] parts = expiry.split("/");
        return YearMonth.of(2000 + Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
    }
}
