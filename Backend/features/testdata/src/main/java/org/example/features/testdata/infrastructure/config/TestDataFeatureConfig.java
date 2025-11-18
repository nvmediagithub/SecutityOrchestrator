package org.example.features.testdata.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Spring configuration class for the TestData feature module.
 * This class wires together domain services, infrastructure adapters, and application use cases.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "org.example.features.testdata.infrastructure.repositories",
    entityManagerFactoryRef = "testDataEntityManagerFactory"
)
@ComponentScan(basePackages = {
    "org.example.features.testdata.application",
    "org.example.features.testdata.domain",
    "org.example.features.testdata.infrastructure",
    "org.example.features.testdata.presentation"
})
public class TestDataFeatureConfig {

    @Bean
    public DataSource testDataDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdata_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean testDataEntityManagerFactory(@Qualifier("testDataDataSource") DataSource testDataDataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(testDataDataSource);
        factoryBean.setPackagesToScan("org.example.shared.domain.entities");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProperties.setProperty("hibernate.show_sql", "true");
        factoryBean.setJpaProperties(jpaProperties);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager testDataTransactionManager(@Qualifier("testDataEntityManagerFactory") LocalContainerEntityManagerFactoryBean testDataEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(testDataEntityManagerFactory.getObject());
        return transactionManager;
    }
}