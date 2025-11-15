package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
})
@ComponentScan(basePackages = {"org.example.infrastructure.config"})
public class SecurityOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityOrchestratorApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // LLM API endpoints
                registry.addMapping("/api/llm/**")
                    .allowedOriginPatterns(
                        "http://localhost:4200",  // Flutter dev server
                        "http://localhost:*",     // Any localhost port
                        "http://127.0.0.1:4200", // Alternative localhost
                        "http://127.0.0.1:*"     // Any localhost port alternative
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);

                // General API endpoints
                registry.addMapping("/api/**")
                    .allowedOriginPatterns(
                        "http://localhost:4200",
                        "http://localhost:*",
                        "http://127.0.0.1:4200",
                        "http://127.0.0.1:*"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);

                // WebSocket endpoints
                registry.addMapping("/ws/**")
                    .allowedOriginPatterns(
                        "http://localhost:4200",
                        "http://localhost:*",
                        "http://127.0.0.1:4200",
                        "http://127.0.0.1:*"
                    )
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);

                // Actuator endpoints (for health checks)
                registry.addMapping("/actuator/**")
                    .allowedOriginPatterns(
                        "http://localhost:4200",
                        "http://localhost:*",
                        "http://127.0.0.1:4200",
                        "http://127.0.0.1:*"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}