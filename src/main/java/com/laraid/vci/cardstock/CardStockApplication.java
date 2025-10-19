package com.laraid.vci.cardstock;

import com.laraid.vci.card.config.CardStockProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableConfigurationProperties({CardStockProperties.class})
public class CardStockApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CardStockApplication.class);
        app.setAdditionalProfiles("cardstock");
        app.setBanner((environment, sourceClass, out) -> {
            out.println(":: PHYSICAL CARD STOCK SERVICE ::");
        });
        app.run(args);
    }
}
