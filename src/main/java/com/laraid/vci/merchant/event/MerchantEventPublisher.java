package com.laraid.vci.merchant.event;

import com.laraid.vci.merchant.dto.MerchantDTO;
import com.laraid.vci.merchant.entity.Merchant;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MerchantEventPublisher {

    private final KafkaTemplate<String, MerchantCreatedEvent> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final String TOPIC = "txn.merchant_created";

    public void publishMerchantCreated(Merchant merchant, MerchantDTO dto) {
        MerchantCreatedEvent event = MerchantCreatedEvent.builder()
                .merchantId(merchant.getId())
                .name(merchant.getName())
                .email(merchant.getEmail())
                .password(dto.getPassword())
                .status(merchant.getStatus().toString()) // ✅ set here
                .createdAt(Instant.now())
                .build();

        kafkaTemplate.send(TOPIC, event.getEmail(), event); // Using email as the key
    }
}

