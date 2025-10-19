package com.laraid.vci.cardstock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cardstock.datasource")
public class CardStockDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    // getters and setters
}
