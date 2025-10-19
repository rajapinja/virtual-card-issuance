package com.laraid.vci.card.client;

import com.laraid.vci.card.config.CardStockProperties;
import com.laraid.vci.card.config.WebClientProperties;
import com.laraid.vci.card.dto.MockBankCardRequest;
import com.laraid.vci.mockbank.dto.MockBankCardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MockBankClient {

    private final WebClientProperties webClientProperties;
    private final WebClient.Builder webClientBuilder; // Inject the builder

    public void nullCheck(CardStockProperties cardStockProperties) {

        webClientProperties.setBaseUrl(
                Optional.ofNullable(webClientProperties.getBaseUrl())
                        .orElse("http://localhost:9792")
        );

        webClientProperties.setEndPoint(
                Optional.ofNullable(webClientProperties.getEndPoint())
                        .orElse("/api/mock-bank/provision-card")
        );
    }

    public Mono<MockBankCardResponse> provisionCard(MockBankCardRequest request, String jwtToken) {

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

