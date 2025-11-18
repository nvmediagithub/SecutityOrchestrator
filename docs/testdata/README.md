# TestData Generation Feature

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)]()
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)

## Overview

The **TestData Generation Feature** is a comprehensive testing data management system that provides intelligent test data generation, project/session management, and multi-tenant database operations. This feature enables automated creation of realistic test data for various data types while maintaining data integrity and supporting complex field constraints.

### Key Capabilities

🎯 **Intelligent Data Generation**
- Automated generation of realistic test data based on field types and constraints
- Support for multiple data types (String, Integer, Date, Email, etc.)
- Custom validation rules and field constraints
- Random data generator with configurable parameters

📊 **Project & Session Management**
- Hierarchical project structure for organizing test scenarios
- Session-based test execution with isolated data contexts
- Test case management with detailed metadata tracking
- Comprehensive audit trails for data generation activities

🔧 **Template-Based Generation**
- Reusable data templates for consistent test data patterns
- Field-level customization with constraints and validation rules
- Template versioning and management
- Bulk data generation capabilities

🗄️ **Multi-Tenant Database Architecture**
- Isolated database contexts per tenant/client
- Separate EntityManagerFactory instances for data isolation
- Transaction management across multiple database operations
- Optimized performance with connection pooling

## Architecture Overview

The TestData feature implements clean architecture with three distinct layers:

### Domain Layer
- **Entities**: [`DataField.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/testdata/domain/DataField.java) - Core data field definitions
- **Value Objects**: [`DataType.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/testdata/domain/DataType.java), [`FieldConstraint.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/testdata/domain/FieldConstraint.java)
- **Services**: [`DataGenerationService.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/domain/services/DataGenerationService.java), [`ProjectService.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/domain/services/ProjectService.java)
- **Repositories**: [`DataFieldRepository.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/domain/repositories/DataFieldRepository.java), [`TestDataTemplateRepository.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/domain/repositories/TestDataTemplateRepository.java)

### Application Layer
- **Use Cases**: [`GenerateTestDataUseCase.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/application/GenerateTestDataUseCase.java) - Main data generation orchestration
- **Controllers**:
  - [`ProjectController.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/application/web/controllers/ProjectController.java) - Project management endpoints
  - [`SessionController.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/application/web/controllers/SessionController.java) - Session management endpoints
  - [`TestCaseController.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/application/web/controllers/TestCaseController.java) - Test case management endpoints

### Infrastructure Layer
- **Configuration**: [`TestDataFeatureConfig.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/infrastructure/config/TestDataFeatureConfig.java) - Spring configuration with multi-tenant setup
- **Adapters**:
  - [`RandomDataGeneratorAdapter.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/infrastructure/adapters/RandomDataGeneratorAdapter.java) - Random data generation implementation
  - [`SessionServiceAdapter.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/infrastructure/adapters/SessionServiceAdapter.java) - Session management adapter
- **Repositories**:
  - [`DataFieldRepositoryAdapter.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/infrastructure/repositories/DataFieldRepositoryAdapter.java)
  - [`TestDataTemplateRepositoryAdapter.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/infrastructure/repositories/TestDataTemplateRepositoryAdapter.java)
  - [`TestSessionRepositoryAdapter.java`](Backend/features/testdata/src/main/java/org/example/features/testdata/infrastructure/repositories/TestSessionRepositoryAdapter.java)

## Configuration Details

### Multi-Tenant Database Configuration

The feature implements multi-tenancy through separate EntityManagerFactory instances:

```java
@Configuration
@EnableJpaRepositories(
    basePackages = "org.example.features.testdata.infrastructure.repositories",
    entityManagerFactoryRef = "testDataEntityManagerFactory"
)
public class TestDataFeatureConfig {

    @Bean
    public DataSource testDataDataSource() {
        // In-memory H2 database for test data operations
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdata_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean testDataEntityManagerFactory(@Qualifier("testDataDataSource") DataSource testDataDataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(testDataDataSource);
        factoryBean.setPackagesToScan("org.example.shared.domain.entities");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // JPA properties for H2 dialect and DDL auto-creation
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager testDataTransactionManager(@Qualifier("testDataEntityManagerFactory") LocalContainerEntityManagerFactoryBean testDataEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(testDataEntityManagerFactory.getObject());
        return transactionManager;
    }
}
```

### Key Configuration Aspects

#### EntityManagerFactory Qualifiers
- **`@Qualifier("testDataDataSource")`**: Distinguishes TestData DataSource from other feature DataSources
- **`@Qualifier("testDataEntityManagerFactory")`**: Ensures proper EntityManagerFactory injection for TestData repositories

#### Database Isolation
- **Separate Database**: Dedicated H2 in-memory database (`testdata_db`)
- **DDL Strategy**: `create-drop` for clean test data lifecycle
- **Transaction Management**: Isolated transaction manager for TestData operations

## Database Configuration and Multi-Tenancy

### Multi-Tenant Architecture

The system supports multi-tenancy through:

1. **Database Isolation**: Each tenant operates in a separate database context
2. **EntityManagerFactory Separation**: Dedicated JPA contexts prevent data leakage
3. **Transaction Boundaries**: Isolated transaction management per tenant
4. **Connection Pooling**: Optimized database connections per tenant context

### Supported Data Types

| Data Type | Description | Examples |
|-----------|-------------|----------|
| STRING | Text data with length constraints | Names, addresses, descriptions |
| INTEGER | Whole numbers with range validation | IDs, counts, ages |
| DECIMAL | Floating-point numbers | Prices, percentages, measurements |
| DATE | Date values with format validation | Birth dates, timestamps |
| EMAIL | Email address validation | User emails, contact addresses |
| PHONE | Phone number formatting | Contact numbers |
| BOOLEAN | True/false values | Flags, status indicators |

### Field Constraints

```java
public enum FieldConstraint {
    NOT_NULL,           // Field cannot be null
    UNIQUE,            // Field values must be unique
    LENGTH(min, max),  // String length constraints
    RANGE(min, max),   // Numeric range constraints
    PATTERN(regex),    // Regular expression validation
    FORMAT(format)     // Specific format requirements
}
```

## Recent Updates

### Dependency Injection Fixes
- **Issue**: Ambiguous EntityManagerFactory beans causing Spring context failures
- **Solution**: Implemented `@Qualifier` annotations for EntityManagerFactory and DataSource beans
- **Impact**: Resolved dependency injection conflicts, ensured proper bean wiring across features

### EntityManagerFactory Qualifiers
- **Change**: Added `@Qualifier("testDataEntityManagerFactory")` to transaction manager configuration
- **Change**: Added `@Qualifier("testDataDataSource")` to EntityManagerFactory bean parameter
- **Benefit**: Clear separation between TestData and other feature database contexts

### Duplicate Consolidation
- **Issue**: Redundant repository adapter patterns across features
- **Solution**: Standardized repository adapter implementations
- **Impact**: Reduced code duplication, improved consistency, easier maintenance

## API Endpoints

### Project Management
- `POST /api/v1/testdata/projects` - Create new test project
- `GET /api/v1/testdata/projects` - List all projects
- `GET /api/v1/testdata/projects/{id}` - Get project details
- `PUT /api/v1/testdata/projects/{id}` - Update project
- `DELETE /api/v1/testdata/projects/{id}` - Delete project

### Session Management
- `POST /api/v1/testdata/sessions` - Create test session
- `GET /api/v1/testdata/sessions` - List active sessions
- `GET /api/v1/testdata/sessions/{id}` - Get session details
- `POST /api/v1/testdata/sessions/{id}/generate` - Generate test data for session

### Test Case Management
- `POST /api/v1/testdata/testcases` - Create test case
- `GET /api/v1/testdata/testcases` - List test cases
- `GET /api/v1/testdata/testcases/{id}` - Get test case details
- `PUT /api/v1/testdata/testcases/{id}` - Update test case

## Usage Examples

### Creating a Test Project

```bash
curl -X POST http://localhost:8080/api/v1/testdata/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "E-commerce Testing",
    "description": "Test data for online shopping platform",
    "tenantId": "tenant-123"
  }'
```

### Generating Test Data

```bash
curl -X POST http://localhost:8080/api/v1/testdata/sessions/123/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": "user-template",
    "count": 100,
    "constraints": {
      "fields": [
        {
          "name": "email",
          "type": "EMAIL",
          "constraints": ["NOT_NULL", "UNIQUE"]
        },
        {
          "name": "age",
          "type": "INTEGER",
          "constraints": ["RANGE(18,99)"]
        }
      ]
    }
  }'
```

## Integration with Other Features

### BPMN Integration
- TestData can be used to generate test data for BPMN process validation
- Session-based data generation for process simulation scenarios

### OpenAPI Integration
- Generate test data that conforms to OpenAPI schema definitions
- Automated population of API request/response payloads

### Monitoring Integration
- Track test data generation metrics and performance
- Audit trails for data generation activities

## Performance Considerations

- **Memory Management**: In-memory H2 database optimized for test scenarios
- **Concurrent Access**: Thread-safe data generation with isolated sessions
- **Bulk Operations**: Efficient bulk data generation for large test datasets
- **Resource Cleanup**: Automatic cleanup of temporary test data

---

**TestData Generation Feature** - Intelligent test data management for comprehensive testing scenarios.