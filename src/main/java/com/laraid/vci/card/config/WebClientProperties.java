package com.laraid.vci.card.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mock-back")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebClientProperties {
    private String baseUrl;
    private String endPoint;
}

