package com.laraid.vci.auth.service;

import com.laraid.vci.config.KeycloakProperties;
import com.laraid.vci.merchant.event.MerchantCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log
            = LoggerFactory. getLogger(AuthService.class);

   private final WebClient keycloakWebClient;
   private final KeycloakProperties keycloakProperties;

    public void printAdminDetails() {
        System.out.println("Admin Client ID: " + keycloakProperties.getClientId());
    }

    public Mono<Void> createUserInKeycloak(MerchantCreatedEvent event) {
        log.info("Keycloak user creation process has been triggered for email: {}", event.getEmail());

        return getAdminAccessToken()
                .flatMap(token -> {
                    // 1. Prepare user payload
                    Map<String, Object> userPayload = new HashMap<>();
                    userPayload.put("username", event.getEmail());
                    userPayload.put("email", event.getEmail());
                    userPayload.put("enabled", true);
                    userPayload.put("firstName", event.getFirstName());
                    userPayload.put("lastName", event.getLastName());
                    userPayload.put("emailVerified", true);

                    // 2. Set password credentials
                    Map<String, Object> credentials = new HashMap<>();
                    credentials.put("type", "password");
                    credentials.put("value", event.getPassword()); // plaintext only, do not encode
                    credentials.put("temporary", false);
                    userPayload.put("credentials", List.of(credentials));

                    // 3. Clear any required actions
                    userPayload.put("requiredActions", Collections.emptyList());

                    log.debug("Prepared Keycloak user payload: {}", userPayload);

                    return keycloakWebClient.post()
                            .uri("/admin/realms/{realm}/users", keycloakProperties.getRealm())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .bodyValue(userPayload)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, response ->
                                    response.bodyToMono(String.class)
                                            .flatMap(body -> {
                                                log.error("❌ Keycloak user creation failed. Status: {}, Body: {}", response.statusCode(), body);
                                                return Mono.error(new RuntimeException("Keycloak error: " + body));
                                            })
                            )
                            .toBodilessEntity()
                            .doOnSuccess(x -> log.info("✅ Keycloak user created successfully: {}", event.getEmail()))
                            .then();
                })
                .onErrorResume(ex -> {
                    log.error("⚠️ Error during Keycloak user creation for {}: {}", event.getEmail(), ex.getMessage(), ex);
                    return Mono.empty(); // Optional: change to Mono.error(ex) if failure should propagate
                });
    }




    public Mono<Boolean> keycloakUserExists(String email) {
        return keycloakWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8384)
                        .path("/admin/realms/{realm}/users")
                        .queryParam("email", email)
                        .build(keycloakProperties.getRealm()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAdminAccessToken())
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> !body.equals("[]")) // Keycloak returns [] if no user found
                .onErrorResume(e -> {
                    log.error("Failed to check user existence in Keycloak: {}", e.getMessage());
                    return Mono.just(false); // fail-safe
                });
    }

    public Mono<Void> deleteUserInKeycloakByEmail(String email) {
        return getAdminAccessToken()
                .flatMap(token -> findUserIdByEmail(email, token)
                        .flatMap(userId -> {
                            if (userId != null) {
                                return deleteUserById(userId, token);
                            } else {
                                return Mono.empty(); // No user to delete
                            }
                        }));
    }

    private Mono<Void> deleteUserById(String userId, String token) {
        String url = keycloakProperties.getServerUrl() + "/admin/realms/" + keycloakProperties.getRealm() + "/users/" + userId;

        return keycloakWebClient
                .delete()
                .uri(url)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<String> findUserIdByEmail(String email, String token) {
        String url = keycloakProperties.getServerUrl() + "/admin/realms/" + keycloakProperties.getRealm() + "/users?email=" + email;

        return keycloakWebClient.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(users -> users.isEmpty() ? null : (String) users.get(0).get("id"));
    }

    private Mono<String> getAdminAccessToken() {
        return keycloakWebClient.post()
                .uri("realms/vci-realm/protocol/openid-connect/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("grant_type=client_credentials&client_id=" + keycloakProperties.getClientId() +
                        "&client_secret=" + keycloakProperties.getClientSecret())
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> log.info("Token response: {}", response))
                .map(response -> (String) response.get("access_token"))
                .doOnNext(token -> log.info("Extracted token: {}", token))
                .onErrorResume(ex -> {
                    log.error("Failed to fetch access token", ex);
                    return Mono.error(ex);
                });
    }

    // ✅ Added for merchant login via Keycloak
    public Mono<String> loginToKeycloak(String email, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", keycloakProperties.getClientId());
        formData.add("client_secret", keycloakProperties.getClientSecret());
        formData.add("username", email);
        formData.add("password", password);

        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.getRealm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> {
                                    log.error("Keycloak error response: {}", error);
                                    return Mono.error(new RuntimeException("Keycloak login failed: " + error));
                                })
                )
                .bodyToMono(Map.class)
                .map(tokenResponse -> {
                    String accessToken = (String) tokenResponse.get("access_token");
                    if (accessToken == null) {
                        throw new RuntimeException("No access token in response");
                    }
                    return accessToken;
                });
    }

}
