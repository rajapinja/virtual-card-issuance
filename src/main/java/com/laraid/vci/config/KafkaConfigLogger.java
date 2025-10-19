package com.laraid.vci.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaConfigLogger {
    private final KafkaTopicProperties kafkaTopicProperties;

    @Bean
    public AdminClient kafkaAdminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "host.docker.internal:29092");
        return AdminClient.create(configs);
    }

    @Bean
    public ApplicationRunner topicLister(AdminClient adminClient) {
        return args -> {
            System.out.println("🧾 Listing Kafka topics:");
            adminClient.listTopics().names().get().forEach(System.out::println);
        };
    }

    @PostConstruct
    public void logTopics() {
        System.out.println("✅ Topics:");
        System.out.println(" - SpendApproved: " + kafkaTopicProperties.getSpendApproved());
        System.out.println(" - CardIssued: " + kafkaTopicProperties.getCardIssued());
        System.out.println(" - SpendRequest: " + kafkaTopicProperties.getSpendRequested());
        System.out.println(" - LedgerEntryCreated: " + kafkaTopicProperties.getLedgerEntryCreated());
        System.out.println(" - SpendDeclined: " + kafkaTopicProperties.getSpendDeclined());
    }
}

