package org.example.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Минимальная конфигурация для LLM тестирования
 * Отключает JPA и сложные entity для избежания конфликтов
 */
@Configuration
@Profile("minimal")
public class MinimalConfig {
    
    // Отключаем JPA и Hibernate для минимального тестирования
}