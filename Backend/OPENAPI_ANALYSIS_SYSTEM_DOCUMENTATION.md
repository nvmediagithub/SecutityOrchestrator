# OpenAPI Analysis System Documentation

## Overview

The OpenAPI Analysis System is a comprehensive solution for analyzing OpenAPI specifications using Large Language Models (LLM). The system performs automated security, validation, and consistency analysis to identify potential issues and provide recommendations for API improvements.

## Architecture

### Core Components

1. **OpenApiAnalysisService** - Main service orchestrating the analysis process
2. **LLMPromptBuilder** - Generates specialized prompts for different types of analysis
3. **IssueClassifier** - Classifies and categorizes detected issues
4. **OpenApiLLMAnalyzer** - Parses and processes LLM responses
5. **ArtifactService** - Manages OpenAPI artifacts and integrates with the analysis system
6. **Repositories** - Data access layer for storing analysis results and issues

### Analysis Flow

1. **Input**: OpenAPI specification (JSON/YAML)
2. **Parsing**: Specification is parsed and converted to internal representation
3. **LLM Analysis**: Four parallel analysis types are executed:
   - Security Analysis
   - Validation Analysis
   - Consistency Analysis
   - Comprehensive Analysis
4. **Aggregation**: Results are combined and classified
5. **Output**: Structured report with issues, recommendations, and metrics

## API Endpoints

### OpenAPI Analysis Controller

Base URL: `/api/analysis/openapi`

#### Start Analysis
```http
POST /api/analysis/openapi/{specId}
Content-Type: application/json

{
  "specificationId": "spec-001",
  "specificationTitle": "My API",
  "specificationContent": "...",
  "analysisOptions": ["security", "validation", "consistency"]
}
```

**Response:**
```json
{
  "analysisId": "analysis_spec-001_1640995200000",
  "specificationId": "spec-001",
  "specificationTitle": "My API",
  "status": "IN_PROGRESS",
  "createdAt": "2024-11-08T11:00:00Z",
  "estimatedDuration": "2-3 minutes"
}
```

#### Get Analysis Results
```http
GET /api/analysis/openapi/{specId}
```

**Response:**
```json
{
  "analysisId": "analysis_spec-001_1640995200000",
  "specificationId": "spec-001",
  "status": "COMPLETED",
  "totalIssues": 12,
  "criticalIssues": 2,
  "highIssues": 4,
  "mediumIssues": 3,
  "lowIssues": 3,
  "overallScore": 7.2,
  "grade": "B",
  "processingTimeMs": 45000,
  "securityAnalysis": { ... },
  "validationAnalysis": { ... },
  "consistencyAnalysis": { ... },
  "comprehensiveAnalysis": { ... }
}
```

#### Get Issues List
```http
GET /api/analysis/openapi/{specId}/issues
```

**Response:**
```json
[
  {
    "id": "SECURITY_001",
    "title": "Missing Authentication",
    "severity": "HIGH",
    "category": "security",
    "location": "POST /api/users",
    "description": "Endpoint does not require authentication",
    "recommendation": "Implement JWT authentication",
    "confidence": 0.92,
    "detectedAt": "2024-11-08T11:01:30Z"
  }
]
```

#### Get Security Analysis
```http
GET /api/analysis/openapi/{specId}/security
```

**Response:**
```json
{
  "securityScore": 75,
  "totalChecks": 15,
  "passedChecks": 10,
  "failedChecks": 3,
  "warningChecks": 2,
  "overallSecurityLevel": "GOOD",
  "complianceStatus": "PARTIALLY_COMPLIANT",
  "checks": [
    {
      "id": "AUTH_001",
      "name": "Authentication",
      "status": "FAILED",
      "severity": "HIGH",
      "description": "No authentication scheme defined",
      "recommendation": "Add JWT or OAuth 2.0 authentication"
    }
  ]
}
```

#### Get Inconsistencies
```http
GET /api/analysis/openapi/{specId}/inconsistencies
```

**Response:**
```json
[
  {
    "id": "NAMING_001",
    "type": "naming",
    "severity": "MEDIUM",
    "description": "Inconsistent parameter naming convention",
    "affectedElements": ["parameters", "endpoints"]
  }
]
```

#### Get Analysis Summary
```http
GET /api/analysis/openapi/{specId}/summary
```

**Response:**
```json
{
  "totalIssues": 12,
  "criticalIssues": 2,
  "highIssues": 4,
  "mediumIssues": 3,
  "lowIssues": 3,
  "overallScore": 7.2,
  "grade": "B",
  "analysisTime": "45s",
  "lastAnalyzed": "2024-11-08T11:01:45Z",
  "topRecommendations": [
    "Add authentication to protected endpoints",
    "Improve input validation",
    "Standardize naming conventions"
  ]
}
```

### Artifact Management Endpoints

Base URL: `/api/artifacts/openapi`

#### Start Analysis for Artifact
```http
POST /api/artifacts/openapi/{id}/analyze
```

**Response:**
```json
{
  "analysisId": "analysis_123_1640995200000",
  "status": "STARTED",
  "message": "Analysis started successfully"
}
```

#### Get Analysis Status
```http
GET /api/artifacts/openapi/{id}/analysis/status
```

**Response:**
```json
{
  "specId": "123",
  "status": "IN_PROGRESS",
  "progressPercentage": 65,
  "currentPhase": "Security Analysis",
  "startedAt": "2024-11-08T11:00:00Z",
  "estimatedCompletionTime": "2024-11-08T11:02:00Z",
  "elapsedTimeMinutes": 1,
  "estimatedTimeRemainingMinutes": 1
}
```

#### Get Analysis Results
```http
GET /api/artifacts/openapi/{id}/analysis/results
```

**Response:**
Same format as `/api/analysis/openapi/{specId}` response.

## Analysis Types

### 1. Security Analysis
- **Purpose**: Identify security vulnerabilities and compliance issues
- **Checks**:
  - Authentication schemes
  - Authorization requirements
  - Input validation
  - Rate limiting
  - HTTPS usage
  - CORS configuration
  - Data exposure risks

### 2. Validation Analysis
- **Purpose**: Ensure API specification follows best practices
- **Checks**:
  - Required field validation
  - Data type consistency
  - Parameter validation rules
  - Response schema validation
  - Error handling consistency

### 3. Consistency Analysis
- **Purpose**: Identify inconsistencies in API design
- **Checks**:
  - Naming conventions
  - Response format consistency
  - Error code consistency
  - Versioning strategy
  - Documentation completeness

### 4. Comprehensive Analysis
- **Purpose**: Overall API quality assessment
- **Metrics**:
  - API complexity score
  - Documentation completeness
  - Design quality score
  - Best practices compliance
  - Performance considerations

## Data Models

### OpenApiAnalysis Entity
```java
@Entity
@Table(name = "openapi_analyses")
public class OpenApiAnalysis {
    private String analysisId;
    private String specificationId;
    private String specificationTitle;
    private AnalysisStatus status;
    private Integer totalIssues;
    private Integer criticalIssues;
    private Integer highIssues;
    private Integer mediumIssues;
    private Integer lowIssues;
    private Long analysisDurationMs;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    // ... other fields
}
```

### ApiIssue Entity
```java
@Entity
@Table(name = "api_issues")
public class ApiIssue {
    private String issueId;
    private String specificationId;
    private SeverityLevel severity;
    private String category;
    private String title;
    private String description;
    private String location;
    private Double confidence;
    private String status;
    // ... other fields
}
```

### ApiSecurityCheck Entity
```java
@Entity
@Table(name = "api_security_checks")
public class ApiSecurityCheck {
    private String checkId;
    private String specificationId;
    private String status; // PASSED, FAILED, WARNING
    private String severity;
    private String category;
    private String name;
    private String description;
    // ... other fields
}
```

### ApiInconsistency Entity
```java
@Entity
@Table(name = "api_inconsistencies")
public class ApiInconsistency {
    private String inconsistencyId;
    private String specificationId;
    private String type; // naming, version, documentation
    private String severity;
    private String description;
    private List<String> affectedElements;
    // ... other fields
}
```

## Configuration

### LLM Configuration
```properties
# OpenRouter Configuration
openrouter.api.key=${OPENROUTER_API_KEY}
openrouter.base.url=https://openrouter.ai/api/v1
openrouter.model=anthropic/claude-3-sonnet

# Local LLM Configuration (Ollama)
ollama.base.url=http://localhost:11434
ollama.model=llama3.1:8b
```

### Analysis Configuration
```properties
# Analysis Settings
analysis.max.concurrent=3
analysis.cache.ttl.hours=24
analysis.timeout.minutes=30
analysis.retry.attempts=3
```

## Database Schema

### Tables
- `openapi_analyses` - Main analysis results
- `api_issues` - Individual issues found
- `api_security_checks` - Security check results
- `api_inconsistencies` - Consistency issues
- `openapi_specifications` - Stored OpenAPI specs
- `test_artifacts` - Artifact management

## Error Handling

### Common Error Responses

#### 400 Bad Request
```json
{
  "error": "INVALID_REQUEST",
  "message": "Specification ID is required",
  "timestamp": "2024-11-08T11:00:00Z"
}
```

#### 404 Not Found
```json
{
  "error": "ANALYSIS_NOT_FOUND",
  "message": "Analysis with ID analysis_123 not found",
  "timestamp": "2024-11-08T11:00:00Z"
}
```

#### 500 Internal Server Error
```json
{
  "error": "ANALYSIS_FAILED",
  "message": "Analysis failed due to LLM service unavailable",
  "timestamp": "2024-11-08T11:00:00Z"
}
```

## Rate Limiting

- **Analysis requests**: 5 per minute per user
- **Status queries**: 60 per minute per user
- **Result retrieval**: 100 per minute per user

## Monitoring and Metrics

### Key Metrics
- Analysis success rate
- Average analysis time
- Issues found per specification
- Security score distribution
- LLM service availability

### Logging
- Analysis start/completion events
- LLM service calls
- Error occurrences
- Performance metrics

## Deployment

### Prerequisites
- Java 17+
- Spring Boot 3.x
- PostgreSQL/MySQL
- Redis (for caching)
- OpenRouter API key or Ollama instance

### Environment Variables
```bash
export OPENROUTER_API_KEY=your_api_key
export OLLAMA_URL=http://localhost:11434
export DB_URL=jdbc:postgresql://localhost:5432/security_orchestrator
export REDIS_URL=redis://localhost:6379
```

### Build and Run
```bash
# Build the application
./gradlew build

# Run with profiles
./gradlew bootRun --args='--spring.profiles.active=production'
```

## Troubleshooting

### Common Issues

1. **LLM Service Unavailable**
   - Check API key configuration
   - Verify network connectivity
   - Check rate limits

2. **Analysis Timeout**
   - Increase timeout configuration
   - Check specification complexity
   - Verify LLM service performance

3. **Memory Issues**
   - Increase JVM heap size
   - Optimize caching strategy
   - Review concurrent analysis limits

### Debug Mode
```properties
logging.level.org.example.infrastructure.services.llm=DEBUG
logging.level.org.example.infrastructure.services.openapi=DEBUG
```

## Security Considerations

1. **API Key Security**: Store LLM API keys securely
2. **Input Validation**: Sanitize OpenAPI specification content
3. **Rate Limiting**: Prevent abuse of analysis endpoints
4. **Data Privacy**: Ensure sensitive data in specs is handled properly
5. **Access Control**: Implement proper authentication for analysis endpoints

## Future Enhancements

1. **Real-time Analysis**: WebSocket support for live analysis updates
2. **Custom Rules**: Allow users to define custom analysis rules
3. **Integration**: CI/CD pipeline integration
4. **Reporting**: Advanced reporting and dashboard features
5. **Machine Learning**: Enhanced issue detection using ML models

## Support

For issues and questions:
- GitHub Issues: [Repository Issues]
- Documentation: [Internal Wiki]
- API Reference: [OpenAPI Specification]

---

*Last updated: 2024-11-08*
*Version: 1.0.0*