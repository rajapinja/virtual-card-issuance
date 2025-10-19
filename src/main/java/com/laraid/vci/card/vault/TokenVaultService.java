package com.laraid.vci.card.vault;

public interface TokenVaultService {
    String storeCard(String pan, String cvv, String expiry);
    CardSecret retrieveCard(String token);
}
