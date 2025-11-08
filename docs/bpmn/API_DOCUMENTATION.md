# BPMN Analysis System - API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Error Handling](#error-handling)
4. [Rate Limits](#rate-limits)
5. [BPMN Process Management](#bpmn-process-management)
6. [Security Analysis](#security-analysis)
7. [AI Integration](#ai-integration)
8. [Test Generation](#test-generation)
9. [WebSocket Endpoints](#websocket-endpoints)
10. [Data Models](#data-models)
11. [OpenAPI Specification](#openapi-specification)

## Overview

The BPMN Analysis System provides a comprehensive REST API for managing BPMN processes, performing security analysis, and integrating with external systems. All API endpoints are versioned using the `/api/v1/bpmn` prefix and return JSON responses.

### Base URL
```
http://localhost:8080/api/v1/bpmn
```

### Content Types
- **Request**: `application/json`, `multipart/form-data`, `text/xml`
- **Response**: `application/json`
- **WebSocket**: `text/json`

### API Versioning
- Current version: `v1`
- URL pattern: `/api/v1/bpmn/{endpoint}`
- Deprecation notice: 6 months before sunset

## Authentication

### API Key Authentication
```bash
# Include API key in header
curl -H "X-API-Key: your-api-key" \
  http://localhost:8080/api/v1/bpmn/processes
```

### JWT Token Authentication
```bash
# Include JWT token in header
curl -H "Authorization: Bearer your-jwt-token" \
  http://localhost:8080/api/v1/bpmn/processes
```

### Session-Based Authentication
```bash
# Cookie-based authentication
curl -b cookies.txt \
  http://localhost:8080/api/v1/bpmn/processes
```

## Error Handling

### Standard Error Response Format
```json
{
  "timestamp": "2025-11-08T13:25:22.175Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid BPMN file format",
  "path": "/api/v1/bpmn/processes",
  "details": {
    "field": "file",
    "code": "INVALID_FORMAT",
    "reason": "File must be valid BPMN 2.0 XML"
  }
}
```

### HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid request format |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 422 | Unprocessable Entity | Validation failed |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Server error |
| 503 | Service Unavailable | Service temporarily unavailable |

### Error Codes

| Code | Description |
|------|-------------|
| `BPMN_INVALID_FORMAT` | Invalid BPMN 2.0 XML format |
| `BPMN_VALIDATION_ERROR` | BPMN schema validation failed |
| `ANALYSIS_IN_PROGRESS` | Analysis already in progress |
| `AI_SERVICE_UNAVAILABLE` | AI service temporarily unavailable |
| `RATE_LIMIT_EXCEEDED` | Request rate limit exceeded |
| `FILE_TOO_LARGE` | File size exceeds limit |
| `COMPLIANCE_STANDARD_UNSUPPORTED` | Unsupported compliance standard |
| `INSUFFICIENT_PERMISSIONS` | User lacks required permissions |

## Rate Limits

### Default Limits
- **Anonymous**: 10 requests per minute
- **Authenticated**: 100 requests per minute
- **Premium**: 1000 requests per minute

### Rate Limit Headers
```bash
curl -i http://localhost:8080/api/v1/bpmn/processes
```

```http
HTTP/1.1 200 OK
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1636454400
```

### Rate Limit Error Response
```json
{
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again in 60 seconds",
  "retryAfter": 60
}
```

## BPMN Process Management

### 1. Upload BPMN Process

**Endpoint**: `POST /api/v1/bpmn/processes`

**Description**: Upload and parse a BPMN 2.0 process file

**Content-Type**: `multipart/form-data`

**Request Parameters**:
- `file` (file, required): BPMN 2.0 XML file (max 10MB)
- `name` (string, optional): Process name (defaults to filename)
- `description` (string, optional): Process description
- `tags` (array, optional): Array of tags for categorization

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/v1/bpmn/processes \
  -F "file=@loan-approval.bpmn" \
  -F "name=Loan Approval Process" \
  -F "description=Customer loan approval workflow" \
  -F "tags=finance,approval,loan"
```

**Success Response** (201):
```json
{
  "processId": "proc_1234567890abcdef",
  "name": "Loan Approval Process",
  "description": "Customer loan approval workflow",
  "status": "PARSED",
  "elements": {
    "tasks": 15,
    "gateways": 3,
    "events": 4,
    "subProcesses": 2
  },
  "validation": {
    "isValid": true,
    "warnings": [],
    "errors": []
  },
  "uploadedAt": "2025-11-08T13:25:22.175Z"
}
```

**Error Response** (400):
```json
{
  "error": "Bad Request",
  "message": "Invalid BPMN file format",
  "details": {
    "field": "file",
    "code": "BPMN_INVALID_FORMAT",
    "reason": "XML parsing failed at line 42: Element 'startEvent' is not valid"
  }
}
```

### 2. Get Process Details

**Endpoint**: `GET /api/v1/bpmn/processes/{processId}`

**Description**: Retrieve detailed information about a specific process

**Path Parameters**:
- `processId` (string, required): Unique process identifier

**Example Request**:
```bash
curl http://localhost:8080/api/v1/bpmn/processes/proc_1234567890abcdef
```

**Success Response** (200):
```json
{
  "processId": "proc_1234567890abcdef",
  "name": "Loan Approval Process",
  "description": "Customer loan approval workflow",
  "version": "1.0.0",
  "status": "PARSED",
  "bpmnVersion": "2.0",
  "elements": {
    "startEvents": 1,
    "endEvents": 2,
    "userTasks": 8,
    "serviceTasks": 5,
    "exclusiveGateways": 2,
    "parallelGateways": 1,
    "subProcesses": 2
  },
  "validation": {
    "isValid": true,
    "warnings": [
      "Process contains deprecated element 'businessRuleTask' - consider using 'serviceTask'"
    ],
    "errors": []
  },
  "security": {
    "authenticationRequired": 8,
    "authorizationRequired": 12,
    "dataElements": 6,
    "externalSystems": 3
  },
  "metadata": {
    "createdAt": "2025-11-08T13:25:22.175Z",
    "updatedAt": "2025-11-08T13:25:22.175Z",
    "createdBy": "user123",
    "fileSize": 245760,
    "tags": ["finance", "approval", "loan"]
  }
}
```

### 3. List Processes

**Endpoint**: `GET /api/v1/bpmn/processes`

**Description**: List all processes with optional filtering and pagination

**Query Parameters**:
- `page` (integer, optional): Page number (default: 1)
- `size` (integer, optional): Page size (default: 20, max: 100)
- `status` (string, optional): Filter by status (PARSED, ANALYZING, READY, FAILED)
- `tag` (string, optional): Filter by tag
- `search` (string, optional): Search in name and description
- `sort` (string, optional): Sort field (name, createdAt, updatedAt)
- `order` (string, optional): Sort order (asc, desc)

**Example Request**:
```bash
curl "http://localhost:8080/api/v1/bpmn/processes?page=1&size=10&status=PARSED&sort=createdAt&order=desc"
```

**Success Response** (200):
```json
{
  "processes": [
    {
      "processId": "proc_1234567890abcdef",
      "name": "Loan Approval Process",
      "description": "Customer loan approval workflow",
      "status": "PARSED",
      "elementCount": 26,
      "securityScore": null,
      "tags": ["finance", "approval", "loan"],
      "createdAt": "2025-11-08T13:25:22.175Z",
      "updatedAt": "2025-11-08T13:25:22.175Z"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 5,
    "totalElements": 47,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 4. Update Process

**Endpoint**: `PUT /api/v1/bpmn/processes/{processId}`

**Description**: Update process metadata and settings

**Path Parameters**:
- `processId` (string, required): Unique process identifier

**Request Body**:
```json
{
  "name": "Updated Loan Approval Process",
  "description": "Enhanced loan approval workflow with new security controls",
  "tags": ["finance", "approval", "loan", "security-enhanced"],
  "settings": {
    "autoAnalyze": true,
    "complianceStandards": ["GDPR", "PCI-DSS"],
    "securityLevel": "HIGH"
  }
}
```

**Example Request**:
```bash
curl -X PUT http://localhost:8080/api/v1/bpmn/processes/proc_1234567890abcdef \
  -H "Content-Type: application/json" \
  -d @update-request.json
```

### 5. Delete Process

**Endpoint**: `DELETE /api/v1/bpmn/processes/{processId}`

**Description**: Delete a process and all associated analyses

**Path Parameters**:
- `processId` (string, required): Unique process identifier

**Example Request**:
```bash
curl -X DELETE http://localhost:8080/api/v1/bpmn/processes/proc_1234567890abcdef
```

## Security Analysis

### 1. Start Security Analysis

**Endpoint**: `POST /api/v1/bpmn/analysis`

**Description**: Initiate security analysis of a BPMN process

**Request Body**:
```json
{
  "processId": "proc_1234567890abcdef",
  "analysisType": "COMPREHENSIVE",
  "complianceStandards": ["GDPR", "PCI-DSS", "ISO27001"],
  "configuration": {
    "includePerformance": true,
    "includeCompliance": true,
    "includeThreatModeling": true,
    "aiAssisted": true,
    "customRules": ["custom-auth-rule", "custom-data-rule"]
  },
  "priorities": {
    "focusAreas": ["AUTHENTICATION", "DATA_PROTECTION", "COMPLIANCE"],
    "excludeElements": ["Element_1", "Element_2"]
  }
}
```

**Analysis Types**:
- `QUICK`: Basic security scan (2-5 minutes)
- `STANDARD`: Standard analysis with common security checks (5-15 minutes)
- `COMPREHENSIVE`: Full analysis with AI assistance and compliance checking (15-30 minutes)
- `COMPLIANCE`: Focused compliance analysis for specific regulations (10-20 minutes)

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/v1/bpmn/analysis \
  -H "Content-Type: application/json" \
  -d @analysis-request.json
```

**Success Response** (202):
```json
{
  "analysisId": "analysis_abcdef1234567890",
  "processId": "proc_1234567890abcdef",
  "status": "STARTED",
  "analysisType": "COMPREHENSIVE",
  "estimatedDuration": 1200,
  "startedAt": "2025-11-08T13:25:22.175Z",
  "progress": 0,
  "message": "Analysis initiated successfully"
}
```

### 2. Get Analysis Results

**Endpoint**: `GET /api/v1/bpmn/analysis/{analysisId}`

**Description**: Retrieve analysis results and current status

**Path Parameters**:
- `analysisId` (string, required): Unique analysis identifier

**Example Request**:
```bash
curl http://localhost:8080/api/v1/bpmn/analysis/analysis_abcdef1234567890
```

**Success Response** (200):
```json
{
  "analysisId": "analysis_abcdef1234567890",
  "processId": "proc_1234567890abcdef",
  "status": "COMPLETED",
  "analysisType": "COMPREHENSIVE",
  "startedAt": "2025-11-08T13:25:22.175Z",
  "completedAt": "2025-11-08T13:25:52.175Z",
  "duration": 30000,
  "results": {
    "securityScore": 78.5,
    "riskLevel": "MEDIUM",
    "totalFindings": 12,
    "findings": [
      {
        "id": "finding_001",
        "type": "AUTHENTICATION_GAP",
        "severity": "HIGH",
        "title": "Missing Authentication for User Task",
        "description": "The 'Approve Loan' user task does not require proper authentication",
        "location": {
          "elementId": "UserTask_ApproveLoan",
          "elementType": "userTask",
          "xpath": "/definitions/process/userTask[@id='UserTask_ApproveLoan']"
        },
        "impact": {
          "likelihood": "HIGH",
          "impact": "MEDIUM",
          "riskScore": 0.6
        },
        "recommendation": "Add authentication gateway before user task execution",
        "evidence": [
          "User task has no security annotation",
          "Process flow allows unauthenticated access"
        ],
        "compliance": {
          "GDPR": {
            "status": "NON_COMPLIANT",
            "article": "Article 32 - Security of processing"
          }
        },
        "aiGenerated": false
      }
    ],
    "recommendations": [
      {
        "id": "rec_001",
        "type": "ENHANCEMENT",
        "priority": "HIGH",
        "title": "Implement Multi-Factor Authentication",
        "description": "Add MFA requirement for high-risk user tasks",
        "implementation": {
          "effort": "MEDIUM",
          "cost": "LOW",
          "timeframe": "2-4 weeks"
        },
        "compliance": {
          "GDPR": "RECOMMENDED",
          "PCI-DSS": "REQUIRED"
        }
      }
    ],
    "complianceResults": {
      "GDPR": {
        "score": 85,
        "status": "MOSTLY_COMPLIANT",
        "gaps": ["Data retention policy not specified"],
        "requirements": [
          {
            "article": "Article 32",
            "status": "COMPLIANT",
            "description": "Security of processing"
          }
        ]
      },
      "PCI-DSS": {
        "score": 72,
        "status": "PARTIALLY_COMPLIANT",
        "gaps": ["Authentication not enforced for cardholder data access"],
        "requirements": [
          {
            "requirement": "8.2.3",
            "status": "NON_COMPLIANT",
            "description": "Multi-factor authentication"
          }
        ]
      }
    },
    "metrics": {
      "elementsAnalyzed": 26,
      "securityControls": 15,
      "vulnerabilities": {
        "critical": 0,
        "high": 3,
        "medium": 6,
        "low": 3
      },
      "aiRecommendations": 8
    }
  },
  "aiInsights": {
    "summary": "The loan approval process shows moderate security risks primarily in authentication and data protection areas.",
    "keyFindings": [
      "Authentication gaps in user-facing tasks",
      "Missing data classification in service tasks",
      "Compliance requirements not fully addressed"
    ],
    "businessContext": "Financial services process handling sensitive customer data"
  }
}
```

### 3. Get Analysis History

**Endpoint**: `GET /api/v1/bpmn/processes/{processId}/analysis/history`

**Description**: Retrieve analysis history for a specific process

**Example Request**:
```bash
curl "http://localhost:8080/api/v1/bpmn/processes/proc_1234567890abcdef/analysis/history"
```

**Response**:
```json
{
  "processId": "proc_1234567890abcdef",
  "analyses": [
    {
      "analysisId": "analysis_abcdef1234567890",
      "analysisType": "COMPREHENSIVE",
      "status": "COMPLETED",
      "securityScore": 78.5,
      "startedAt": "2025-11-08T13:25:22.175Z",
      "completedAt": "2025-11-08T13:25:52.175Z",
      "findingsCount": 12
    },
    {
      "analysisId": "analysis_1234567890abcdef",
      "analysisType": "QUICK",
      "status": "COMPLETED",
      "securityScore": 65.2,
      "startedAt": "2025-11-07T10:15:30.000Z",
      "completedAt": "2025-11-07T10:15:35.000Z",
      "findingsCount": 8
    }
  ],
  "trend": {
    "averageScore": 71.85,
    "trend": "IMPROVING",
    "lastAnalysis": "2025-11-08T13:25:52.175Z"
  }
}
```

## AI Integration

### 1. Test AI Provider

**Endpoint**: `POST /api/v1/bpmn/ai/test`

**Description**: Test AI provider connectivity and configuration

**Request Body**:
```json
{
  "provider": "OPENROUTER",
  "model": "anthropic/claude-3-haiku",
  "testPrompt": "Analyze this business process for security vulnerabilities",
  "timeout": 30
}
```

**Providers**: `OPENROUTER`, `OLLAMA`, `LOCAL`

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/v1/bpmn/ai/test \
  -H "Content-Type: application/json" \
  -d @ai-test-request.json
```

**Success Response** (200):
```json
{
  "status": "SUCCESS",
  "provider": "OPENROUTER",
  "model": "anthropic/claude-3-haiku",
  "responseTime": 1250,
  "response": {
    "summary": "The loan approval process has several security areas that need attention...",
    "confidence": 0.85,
    "recommendations": [
      "Implement MFA for user tasks",
      "Add data encryption for sensitive information"
    ]
  }
}
```

### 2. Get AI Configuration

**Endpoint**: `GET /api/v1/bpmn/ai/configuration`

**Description**: Retrieve current AI provider configuration

**Example Request**:
```bash
curl http://localhost:8080/api/v1/bpmn/ai/configuration
```

**Response**:
```json
{
  "defaultProvider": "OPENROUTER",
  "providers": {
    "OPENROUTER": {
      "enabled": true,
      "defaultModel": "anthropic/claude-3-haiku",
      "maxTokens": 2048,
      "temperature": 0.1,
      "timeout": 30,
      "health": "HEALTHY"
    },
    "OLLAMA": {
      "enabled": true,
      "defaultModel": "llama2",
      "baseUrl": "http://localhost:11434",
      "health": "HEALTHY"
    }
  },
  "usage": {
    "requestsToday": 45,
    "tokensUsed": 128500,
    "remaining": 871500
  }
}
```

### 3. Generate AI Recommendations

**Endpoint**: `POST /api/v1/bpmn/ai/recommendations`

**Description**: Generate AI-powered security recommendations

**Request Body**:
```json
{
  "processId": "proc_1234567890abcdef",
  "context": {
    "businessDomain": "FINANCIAL_SERVICES",
    "dataClassification": "SENSITIVE",
    "complianceRequirements": ["GDPR", "PCI-DSS"]
  },
  "focus": ["AUTHENTICATION", "DATA_PROTECTION", "COMPLIANCE"],
  "detailLevel": "DETAILED"
}
```

## Test Generation

### 1. Generate Security Test Cases

**Endpoint**: `POST /api/v1/bpmn/test-generation`

**Description**: Generate automated security test cases based on process analysis

**Request Body**:
```json
{
  "processId": "proc_1234567890abcdef",
  "analysisId": "analysis_abcdef1234567890",
  "testTypes": [
    "AUTHENTICATION",
    "AUTHORIZATION", 
    "DATA_VALIDATION",
    "ERROR_HANDLING"
  ],
  "coverage": "COMPREHENSIVE",
  "testFramework": "JUNIT",
  "outputFormat": "JAVA",
  "configuration": {
    "includeEdgeCases": true,
    "generateMockData": true,
    "includeNegativeTests": true
  }
}
```

**Test Types**:
- `AUTHENTICATION`: Tests for authentication bypass and session management
- `AUTHORIZATION`: Tests for authorization and access control
- `DATA_VALIDATION`: Tests for input validation and data integrity
- `ERROR_HANDLING`: Tests for error handling and exception scenarios
- `PERFORMANCE`: Tests for performance and load handling
- `COMPLIANCE`: Tests for regulatory compliance requirements

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/v1/bpmn/test-generation \
  -H "Content-Type: application/json" \
  -d @test-generation-request.json
```

**Success Response** (202):
```json
{
  "generationId": "gen_1234567890abcdef",
  "status": "IN_PROGRESS",
  "estimatedTime": 180,
  "testSummary": {
    "totalTests": 24,
    "testTypes": {
      "AUTHENTICATION": 8,
      "AUTHORIZATION": 6,
      "DATA_VALIDATION": 7,
      "ERROR_HANDLING": 3
    },
    "coverage": "COMPREHENSIVE"
  },
  "downloadUrl": null
}
```

### 2. Get Generated Test Results

**Endpoint**: `GET /api/v1/bpmn/test-generation/{generationId}`

**Description**: Retrieve generated test cases and download

**Example Request**:
```bash
curl http://localhost:8080/api/v1/bpmn/test-generation/gen_1234567890abcdef
```

**Success Response** (200):
```json
{
  "generationId": "gen_1234567890abcdef",
  "status": "COMPLETED",
  "completedAt": "2025-11-08T13:30:22.175Z",
  "downloadUrl": "/api/v1/bpmn/test-generation/gen_1234567890abcdef/download",
  "testSuite": {
    "name": "LoanApprovalSecurityTest",
    "framework": "JUNIT",
    "language": "JAVA",
    "totalTests": 24,
    "passingTests": 0,
    "failingTests": 0,
    "tests": [
      {
        "name": "testUserAuthenticationRequired",
        "testType": "AUTHENTICATION",
        "priority": "HIGH",
        "description": "Verify that user authentication is required for loan approval task",
        "elementId": "UserTask_ApproveLoan",
        "testCode": "@Test\npublic void testUserAuthenticationRequired() {\n    // Generated test implementation\n}",
        "mockData": {
          "userId": "test-user-123",
          "loanAmount": 50000,
          "creditScore": 750
        },
        "expectedResults": [
          "Authentication required",
          "Access denied without valid token"
        ]
      }
    ]
  },
  "executionTime": 45,
  "aiGenerated": true,
  "confidence": 0.89
}
```

## WebSocket Endpoints

### Connection

**URL**: `ws://localhost:8080/ws/bpmn`

**Authentication**: Include token in connection query parameter or header

**Example Connection**:
```javascript
const ws = new WebSocket('ws://localhost:8080/ws/bpmn?token=your-jwt-token');
```

### Message Types

#### 1. Subscribe to Analysis Updates
```json
{
  "type": "SUBSCRIBE_ANALYSIS",
  "analysisId": "analysis_abcdef1234567890"
}
```

#### 2. Analysis Progress Update
```json
{
  "type": "ANALYSIS_PROGRESS",
  "analysisId": "analysis_abcdef1234567890",
  "progress": 45,
  "currentStep": "Performing authorization analysis",
  "completedSteps": [
    "BPMN parsing",
    "Initial security scan",
    "Authentication analysis"
  ],
  "estimatedTimeRemaining": 15
}
```

#### 3. Analysis Complete
```json
{
  "type": "ANALYSIS_COMPLETE",
  "analysisId": "analysis_abcdef1234567890",
  "status": "SUCCESS",
  "summary": {
    "securityScore": 78.5,
    "totalFindings": 12,
    "highSeverityFindings": 3
  }
}
```

#### 4. Error Notification
```json
{
  "type": "ANALYSIS_ERROR",
  "analysisId": "analysis_abcdef1234567890",
  "error": {
    "code": "AI_SERVICE_UNAVAILABLE",
    "message": "AI service temporarily unavailable",
    "retryable": true
  }
}
```

#### 5. Heartbeat
```json
{
  "type": "HEARTBEAT",
  "timestamp": "2025-11-08T13:25:22.175Z"
}
```

## Data Models

### Process Element

```typescript
interface ProcessElement {
  id: string;
  type: ElementType;
  name: string;
  description?: string;
  properties: Record<string, any>;
  securityAnnotations?: SecurityAnnotation[];
  dataElements?: DataElement[];
  dependencies?: string[];
  position: {
    x: number;
    y: number;
    width: number;
    height: number;
  };
}

type ElementType = 
  | 'startEvent'
  | 'endEvent'
  | 'userTask'
  | 'serviceTask'
  | 'exclusiveGateway'
  | 'parallelGateway'
  | 'inclusiveGateway'
  | 'subProcess'
  | 'callActivity'
  | 'timerEvent'
  | 'messageEvent'
  | 'errorEvent'
  | 'signalEvent'
  | 'boundaryEvent';
```

### Security Finding

```typescript
interface SecurityFinding {
  id: string;
  type: FindingType;
  severity: Severity;
  title: string;
  description: string;
  location: {
    elementId: string;
    elementType: ElementType;
    xpath?: string;
  };
  impact: {
    likelihood: 'LOW' | 'MEDIUM' | 'HIGH';
    impact: 'LOW' | 'MEDIUM' | 'HIGH';
    riskScore: number;
  };
  recommendation: string;
  evidence: string[];
  compliance?: Record<ComplianceStandard, ComplianceResult>;
  aiGenerated: boolean;
  confidence?: number;
}

type FindingType = 
  | 'AUTHENTICATION_GAP'
  | 'AUTHORIZATION_WEAKNESS'
  | 'SENSITIVE_DATA_EXPOSURE'
  | 'INPUT_VALIDATION_MISSING'
  | 'ERROR_HANDLING_INSUFFICIENT'
  | 'COMPLIANCE_VIOLATION'
  | 'SECURITY_CONTROL_MISSING';

type Severity = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | 'INFO';
```

### Analysis Result

```typescript
interface AnalysisResult {
  analysisId: string;
  processId: string;
  status: AnalysisStatus;
  analysisType: AnalysisType;
  startedAt: string;
  completedAt?: string;
  results: {
    securityScore: number;
    riskLevel: RiskLevel;
    totalFindings: number;
    findings: SecurityFinding[];
    recommendations: SecurityRecommendation[];
    complianceResults: Record<ComplianceStandard, ComplianceResult>;
    metrics: AnalysisMetrics;
  };
  aiInsights?: AIInsights;
}

type AnalysisStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
type AnalysisType = 'QUICK' | 'STANDARD' | 'COMPREHENSIVE' | 'COMPLIANCE';
type RiskLevel = 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | 'MINIMAL';
```

## OpenAPI Specification

The complete OpenAPI 3.0 specification is available at:

**Endpoint**: `GET /api/v1/bpmn/openapi.json`

**Interactive Documentation**: `GET /api/v1/bpmn/swagger-ui.html`

**Example Response**:
```bash
curl http://localhost:8080/api/v1/bpmn/openapi.json
```

This documentation provides a complete reference for all available endpoints, request/response schemas, and examples. The interactive Swagger UI allows you to test endpoints directly from your browser.

## SDKs and Client Libraries

### Java Client
```java
// Maven dependency
<dependency>
    <groupId>com.securityorchestrator</groupId>
    <artifactId>bpmn-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### JavaScript/TypeScript Client
```bash
npm install @securityorchestrator/bpmn-client
```

### Python Client
```bash
pip install securityorchestrator-bpmn
```

### curl Examples

All endpoints can be tested using the provided curl examples. For complex scenarios, use the Postman collection provided in the `/examples/postman` directory.

This comprehensive API documentation provides everything needed to integrate with the BPMN Analysis System, from basic process management to advanced AI-powered security analysis.