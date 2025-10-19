package com.laraid.vci.cardstock.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.laraid.vci.cardstock.repo",
        entityManagerFactoryRef = "cardStockEntityManagerFactory",
        transactionManagerRef = "cardStockTransactionManager"
)
public class CardStockDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties cardStockDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource cardStockDataSource() {
        return cardStockDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
    // ✅ Needed for EntityManagerFactoryBuilder
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setShowSql(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        return adapter;
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            JpaVendorAdapter jpaVendorAdapter,
            ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {

        return new EntityManagerFactoryBuilder(
                jpaVendorAdapter,
                new HashMap<>(),
                persistenceUnitManager.getIfAvailable()
        );
    }


    @Bean(name = "cardStockEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean cardStockEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(cardStockDataSource())
                .packages("com.laraid.vci.cardstock.entity")
                .persistenceUnit("cardstock")
                .build();
    }

    @Bean(name = "cardStockTransactionManager")
    public PlatformTransactionManager cardStockTransactionManager(
            @Qualifier("cardStockEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

//    @PostConstruct
//    public void debugCardStockSetup() {
//        System.out.println("CardStock DataSource loaded: " + cardStockDataSource().getClass());
//    }
}


