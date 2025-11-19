package org.example.features.bpmn.infrastructure.config;

import org.example.features.bpmn.application.usecases.*;
import org.example.features.bpmn.domain.repositories.BpmnDiagramRepository;
import org.example.features.bpmn.domain.services.ProcessAnalyzer;
import org.example.features.bpmn.domain.services.ProcessExecutor;
import org.example.features.bpmn.domain.services.ProcessParser;
import org.example.features.bpmn.infrastructure.repositories.JpaBpmnDiagramRepository;
import org.example.features.bpmn.infrastructure.repositories.SpringDataJpaBpmnDiagramRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
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
 * Configuration class for BPMN feature dependency injection
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "org.example.features.bpmn.infrastructure.repositories",
    entityManagerFactoryRef = "bpmnEntityManagerFactory"
)
public class BpmnConfiguration {

    @Bean
    public DataSource bpmnDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:bpmn_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean bpmnEntityManagerFactory(@Qualifier("bpmnDataSource") DataSource bpmnDataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(bpmnDataSource);
        factoryBean.setPackagesToScan("org.example.features.bpmn.infrastructure.repositories");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        jpaProperties.setProperty("hibernate.show_sql", "true");
        factoryBean.setJpaProperties(jpaProperties);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager bpmnTransactionManager(@Qualifier("bpmnEntityManagerFactory") LocalContainerEntityManagerFactoryBean bpmnEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(bpmnEntityManagerFactory.getObject());
        return transactionManager;
    }


    @Bean
    public ProcessExecutor processExecutor() {
        // TODO: Implement actual ProcessExecutor implementation
        return new ProcessExecutor() {
            @Override
            public java.util.concurrent.CompletableFuture<ExecutionResult> executeProcess(org.example.features.bpmn.domain.entities.BpmnDiagram diagram, java.util.Map<String, Object> executionContext) {
                return java.util.concurrent.CompletableFuture.completedFuture(
                    new ExecutionResult(diagram.getId(), "exec-123", true, "Executed successfully", null, 100L)
                );
            }

            @Override
            public ExecutionValidation validateExecution(org.example.features.bpmn.domain.entities.BpmnDiagram diagram) {
                return new ExecutionValidation(true, null, "Valid for execution");
            }

            @Override
            public ExecutionStatus getExecutionStatus(String executionId) {
                return ExecutionStatus.COMPLETED;
            }

            @Override
            public boolean cancelExecution(String executionId) {
                return true;
            }
        };
    }

    @Bean
    public AnalyzeBpmnProcessUseCase analyzeBpmnProcessUseCase(ProcessAnalyzer processAnalyzer) {
        return new AnalyzeBpmnProcessUseCase(processAnalyzer);
    }

    @Bean
    public ValidateBpmnDiagramUseCase validateBpmnDiagramUseCase(ProcessParser processParser) {
        return new ValidateBpmnDiagramUseCase(processParser);
    }

    @Bean
    public GenerateBpmnReportUseCase generateBpmnReportUseCase(ProcessAnalyzer processAnalyzer) {
        return new GenerateBpmnReportUseCase(processAnalyzer);
    }

    @Bean
    public BpmnDiagramRepository bpmnDiagramRepository(
        SpringDataJpaBpmnDiagramRepository springDataRepository) {
        return new JpaBpmnDiagramRepository(springDataRepository);
    }
}
