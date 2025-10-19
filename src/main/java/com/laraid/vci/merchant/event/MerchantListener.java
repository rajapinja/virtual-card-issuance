package com.laraid.vci.merchant.event;

import com.laraid.vci.enums.Status;
import com.laraid.vci.merchant.repo.MerchantRepository;
import com.laraid.vci.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MerchantListener {

    private static final Logger log
            = LoggerFactory. getLogger(MerchantListener.class);

    private final AuthService authService;
    private final MerchantRepository repo;

    @KafkaListener(
            topics = "#{kafkaTopicProperties.merchantCreated}",
            containerFactory = "keycloakKafkaListenerContainerFactory"
    )
    public void handleMerchantCreated(MerchantCreatedEvent event) {
        try {
            authService.createUserInKeycloak(event).subscribe();  // WebClient call

            // 🟢 Update status to ACTIVE on success
            repo.updateStatus(event.getMerchantId(), Status.ACTIVE);
        } catch (Exception ex) {
            // 🔴 Mark as FAILED or trigger compensation
            repo.updateStatus(event.getMerchantId(), Status.FAILED);
            log.error("Keycloak user creation failed for merchant: {}", event.getMerchantId(), ex);
        }
    }
}
