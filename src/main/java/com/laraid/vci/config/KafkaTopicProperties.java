package com.laraid.vci.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.kafka.topic")
@Data
public class KafkaTopicProperties {

    private String ledgerEntryCreated;
    private String spendRequested;
    private String spendApproved;
    private String spendDeclined;
    private String cardIssued;
    private String merchantCreated;
}

