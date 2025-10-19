package com.laraid.vci.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceLogger implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ CommandLineRunner running...");

        // Temporarily comment this if datasource isn't ready
        // System.out.println("JDBC URL: " + dataSource.getConnection().getMetaData().getURL());
    }
}
