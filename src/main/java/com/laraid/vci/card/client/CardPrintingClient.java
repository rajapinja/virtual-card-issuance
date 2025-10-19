package com.laraid.vci.card.client;

import com.laraid.vci.card.dto.CardIssueRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CardPrintingClient {

    private static final Logger log
            = LoggerFactory. getLogger(CardPrintingClient.class);


    public void sendPrintRequest(CardIssueRequest request, String cardNumber) {
        log.info("Print request sent for physical card [{}] for {}", cardNumber, request.getCardholderName());
    }
}

