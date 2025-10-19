package com.laraid.vci.card.vault;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryTokenVaultService implements TokenVaultService {

    private final Map<String, CardSecret> vault = new ConcurrentHashMap<>();

    @Override
    public String storeCard(String pan, String cvv, String expiry) {
        String token = UUID.randomUUID().toString();
        vault.put(token, new CardSecret(pan, cvv, expiry));
        return token;
    }

    @Override
    public CardSecret retrieveCard(String token) {
       return vault.get(token);

    }


    public void deleteToken(String token) {
        vault.remove(token);
    }
}
