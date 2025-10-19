//package com.laraid.vci.config;
//
//import org.apache.kafka.clients.admin.*;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.*;
//
//@Configuration
//public class KafkaAdminConfig {
//
//    @Bean
//    public AdminClient kafkaAdminClient() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "host.docker.internal:29092");
//        return AdminClient.create(configs);
//    }
//
//    @Bean
//    public ApplicationRunner topicLister(AdminClient adminClient) {
//        return args -> {
//            System.out.println("🧾 Listing Kafka topics:");
//            adminClient.listTopics().names().get().forEach(System.out::println);
//        };
//    }
//}
//
