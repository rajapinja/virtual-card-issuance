package com.laraid.vci.card.vault;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RequiredArgsConstructor
public class ProcessStore {


    private static final Logger log
            = LoggerFactory. getLogger(ProcessStore.class);

    private final InMemoryTokenVaultService inMemoryTokenVaultService;

//    public void processCard(String token) {
//        inMemoryTokenVaultService.retrieveCard(token).ifPresent(cardSecret -> {
//            String cardNumber = cardSecret.cardNumber();
//            String cvv = cardSecret.cvv();
//            String expiry = cardSecret.expiry();
//
//            // Use the details for something secure
//            log.info("Retrieved PAN: {}, CVV: {}, Expiry: {}", cardNumber, cvv, expiry);
//        });
//    }
}
