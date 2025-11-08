package org.example.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Properties;

/**
 * Test configuration for Spring Boot tests
 */
@Configuration
@EnableTransactionManagement
@ActiveProfiles("test")
public class TestConfig {
    
    /**
     * Data source configuration for tests
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        
        // Set additional properties for tests
        Properties properties = new Properties();
        properties.setProperty("spring.profiles.active", "test");
        
        return dataSource;
    }
}