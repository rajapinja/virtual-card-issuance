package com.laraid.vci;

import com.laraid.vci.card.config.CardStockProperties;
import com.laraid.vci.config.KeycloakProperties;
import com.laraid.vci.card.config.WebClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication(
		scanBasePackages = "com.laraid.vci",
		exclude = {
				DataSourceAutoConfiguration.class,
				DataSourceTransactionManagerAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class,
				SecurityAutoConfiguration.class
		}
)
@EnableConfigurationProperties({KeycloakProperties.class, CardStockProperties.class, WebClientProperties.class})
public class VirtualCardIssuanceApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(VirtualCardIssuanceApplication.class);
		app.setBanner((environment, sourceClass, out) -> {
			out.println(":: VIRTUAL CARD ISSUANCE ::");
		});
		app.run(args);
	}

}
