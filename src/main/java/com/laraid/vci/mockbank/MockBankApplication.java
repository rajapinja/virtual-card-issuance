package com.laraid.vci.mockbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class MockBankApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MockBankApplication.class);
        app.setAdditionalProfiles("mockbank");
        app.setBanner((environment, sourceClass, out) -> {
            out.println(":: MOCK BANK SERVICE ::");
        });
        app.run(args);
    }
}

