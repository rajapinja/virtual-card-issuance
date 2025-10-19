package com.laraid.vci.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaTopicProperties props;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "host.docker.internal:29092");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic ledgerEntryTopic() {
        return TopicBuilder.name(props.getLedgerEntryCreated())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic spendRequestedTopic() {
        return TopicBuilder.name(props.getSpendRequested())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic spendApprovedTopic() {
        return TopicBuilder.name(props.getSpendApproved())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic spendDeclinedTopic() {
        return TopicBuilder.name(props.getSpendDeclined())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic cardIssuedTopic() {
        return TopicBuilder.name(props.getCardIssued())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic merchantCreatedTopic() {
        return TopicBuilder.name(props.getMerchantCreated())
                .partitions(3)
                .replicas(1)
                .build();
    }
}

