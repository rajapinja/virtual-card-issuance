package com.laraid.vci.config;

import com.laraid.vci.ledger.service.LedgerListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
@RequiredArgsConstructor
public class WebClientConfig {

    private static final Logger log
            = LoggerFactory. getLogger(WebClientConfig.class);

    private final KeycloakProperties keycloakProperties;

    @Bean
    public WebClient keycloakWebClient() {

        log.info("Loaded keycloak admin props: {}", keycloakProperties); // Should not be null
        log.info("Keycloak URL: {}", keycloakProperties.getServerUrl()); // Should print full URL

        return WebClient.builder()
                .baseUrl(keycloakProperties.getServerUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
