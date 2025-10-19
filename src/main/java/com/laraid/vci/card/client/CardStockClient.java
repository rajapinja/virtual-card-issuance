package com.laraid.vci.card.client;

import com.laraid.vci.card.config.CardStockProperties;
import com.laraid.vci.card.config.WebClientProperties;
import com.laraid.vci.cardstock.dto.CardStockResponse;
import com.laraid.vci.card.dto.MockBankCardRequest;
import com.laraid.vci.mockbank.dto.MockBankCardResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CardStockClient {

    private static final Logger log = LoggerFactory.getLogger(CardStockClient.class);

    private final CardStockProperties cardStockProperties;
    private final WebClientProperties webClientProperties;
    private final WebClient.Builder webClientBuilder; // Inject the builder

    public void nullCheck(CardStockProperties cardStockProperties) {
        cardStockProperties.setBaseUrlCardStock(
                Optional.ofNullable(cardStockProperties.getBaseUrlCardStock())
                        .orElse("http://localhost:9793")
        );
        cardStockProperties.setEndPointCardStock(
                Optional.ofNullable(cardStockProperties.getEndPointCardStock())
                        .orElse("/api/card-stock/next")
        );

        webClientProperties.setBaseUrl(
                Optional.ofNullable(webClientProperties.getBaseUrl())
                        .orElse("http://localhost:9792")
        );

        webClientProperties.setEndPoint(
                Optional.ofNullable(webClientProperties.getEndPoint())
                        .orElse("/api/mock-bank/provision-card")
        );
    }

    public Mono<String> allocateCardNumber(String jwtToken) {
        log.info("BaseURL and Stock endpoint: {} : {}",
                cardStockProperties.getBaseUrlCardStock(), cardStockProperties.getEndPointCardStock());


        return webClientBuilder
                .baseUrl(cardStockProperties.getBaseUrlCardStock())
                .build()
                .get()
                .uri(cardStockProperties.getEndPointCardStock())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(CardStockResponse.class)
                .map(CardStockResponse::getCardNumber);
    }

    public Mono<MockBankCardResponse> provisionCard(MockBankCardRequest request, String jwtToken) {
        log.info("BaseURL and Provision endpoint: {} : {}", webClientProperties.getBaseUrl(), webClientProperties.getEndPoint());

        return webClientBuilder
                .baseUrl(webClientProperties.getBaseUrl())
                .build()
                .post()
                .uri(webClientProperties.getEndPoint())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MockBankCardResponse.class);
    }
}

