package com.laraid.vci.config;

import com.laraid.vci.card.event.CardIssuedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@CacheConfig
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, CardIssuedEvent> consumerFactory() {
        JsonDeserializer<CardIssuedEvent> deserializer = new JsonDeserializer<>(CardIssuedEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "host.docker.internal:29092",
                ConsumerConfig.GROUP_ID_CONFIG, "audit-group",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
        ), new StringDeserializer(), deserializer);
    }


}
