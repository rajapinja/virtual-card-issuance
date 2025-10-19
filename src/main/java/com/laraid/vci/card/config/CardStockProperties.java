package com.laraid.vci.card.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "card-stock")
@Component
public class CardStockProperties {
    private String baseUrlCardStock;
    private String endPointCardStock;
//    private String baseUrlMockBank;
//    private String endPointProvision;
}

