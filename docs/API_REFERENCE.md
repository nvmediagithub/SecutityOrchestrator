# Security Orchestrator - API Reference

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Base URL and Headers](#base-url-and-headers)
4. [Common Response Format](#common-response-format)
5. [Error Codes](#error-codes)
6. [Health and Monitoring APIs](#health-and-monitoring-apis)
7. [Analysis Processes APIs](#analysis-processes-apis)
8. [BPMN APIs](#bpmn-apis)
9. [OpenAPI APIs](#openapi-apis)
10. [LLM Management APIs](#llm-management-apis)
11. [Test Data APIs](#test-data-apis)
12. [WebSocket APIs](#websocket-apis)
13. [Data Models](#data-models)
14. [Code Examples](#code-examples)

---

## Overview

The Security Orchestrator API provides comprehensive REST endpoints for managing security analysis workflows, BPMN processes, OpenAPI specifications, and system monitoring. All endpoints follow RESTful conventions and return consistent JSON responses.

### API Version: v1
### Base URL: `http://localhost:8080/api`

---

## Authentication

### API Key Authentication (Production)
```http
Authorization: Bearer YOUR_API_KEY
```

### Development Mode
For development, no authentication is required by default.

---

## Base URL and Headers

### Required Headers
```http
Content-Type: application/json
Accept: application/json
```

### Optional Headers
```http
Authorization: Bearer YOUR_API_KEY
X-Request-ID: unique-request-id
```

---

## Common Response Format

All APIs return responses in a standardized format:

### Success Response
```json
{
  "success": true,
  "data": {...},
  "message": "Optional success message",
  "timestamp": "2024-01-01T12:00:00.000Z"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error description",
  "errorCode": "ERROR_CODE",
  "timestamp": "2024-01-01T12:00:00.000Z",
  "requestId": "unique-request-id"
}
```

### Pagination Response
```json
{
  "success": true,
  "data": [...],
  "pagination": {
    "page": 1,
    "size": 20,
    "total": 100,
    "totalPages": 5
  },
  "timestamp": "2024-01-01T12:00:00.000Z"
}
```

---

## Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `UNAUTHORIZED` | 401 | Authentication required or failed |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `CONFLICT` | 409 | Resource conflict |
| `FILE_TOO_LARGE` | 413 | Uploaded file exceeds size limit |
| `INVALID_FORMAT` | 422 | File format or content invalid |
| `RATE_LIMITED` | 429 | Too many requests |
| `INTERNAL_ERROR` | 500 | Internal server error |
| `SERVICE_UNAVAILABLE` | 503 | Service temporarily unavailable |

---

## Health and Monitoring APIs

### Health Check

**GET /health**

Retrieve system health status.

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-01T12:00:00.000Z",
  "service": "Security Orchestrator Backend",
  "version": "1.0.0"
}
```

### System Health

**GET /monitoring/health**

Retrieve comprehensive system health information.

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "system-health-1",
    "status": "HEALTHY",
    "cpuUsage": 45.2,
    "memoryUsage": 67.8,
    "diskUsage": 23.1,
    "activeConnections": 5,
    "timestamp": "2024-01-01T12:00:00.000Z",
    "details": {
      "uptime": "2d 14h 32m",
      "lastRestart": "2024-01-01T12:00:00.000Z",
      "components": {
        "database": "HEALTHY",
        "fileSystem": "HEALTHY",
        "llmService": "HEALTHY"
      }
    }
  }
}
```

### System Metrics

**GET /monitoring/metrics**

Retrieve system performance metrics.

**Query Parameters:**
- `type` (optional): Filter by metric type (CPU, MEMORY, DISK, NETWORK, REQUESTS)
- `startTime` (optional): Start time in ISO 8601 format
- `endTime` (optional): End time in ISO 8601 format

**Example:**
```bash
GET /monitoring/metrics?type=CPU&startTime=2024-01-01T00:00:00Z&endTime=2024-01-01T23:59:59Z
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "metric-1",
      "name": "CPU Usage",
      "type": "CPU",
      "value": 45.2,
      "unit": "percent",
      "timestamp": "2024-01-01T12:00:00.000Z",
      "tags": {
        "host": "server-1",
        "source": "system"
      }
    }
  ]
}
```

### Alerts

**GET /monitoring/alerts**

Retrieve system alerts.

**Query Parameters:**
- `status` (optional): Filter by status (ACTIVE, RESOLVED, ACKNOWLEDGED)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "alert-1",
      "title": "High CPU Usage",
      "description": "CPU usage exceeded 80% for 5 minutes",
      "severity": "WARNING",
      "status": "ACTIVE",
      "source": "system",
      "createdAt": "2024-01-01T12:00:00.000Z",
      "resolvedAt": null,
      "tags": {
        "metric": "cpu",
        "threshold": "80"
      }
    }
  ]
}
```

**GET /monitoring/alerts/active**

Retrieve only active alerts requiring attention.

---

## Analysis Processes APIs

### List Analysis Processes

**GET /analysis-processes**

Retrieve all analysis processes.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "process-123",
      "name": "Credit Approval Process Analysis",
      "description": "Security analysis of credit approval workflow",
      "type": "security_analysis",
      "status": "active",
      "createdAt": "2024-01-01T12:00:00.000Z",
      "bpmnDiagramPath": "/data/analysis_processes/bpmn/process-123.bpmn",
      "bpmnDiagramName": "credit-approval.bpmn",
      "bpmnUploadedAt": "2024-01-01T12:05:00.000Z",
      "openapiSpecPath": "/data/analysis_processes/openapi/process-123-api.json",
      "openapiSpecName": "credit-api.json",
      "openapiUploadedAt": "2024-01-01T12:10:00.000Z"
    }
  ]
}
```

### Get Analysis Process

**GET /analysis-processes/{id}**

Retrieve specific analysis process by ID.

**Response:** Single AnalysisProcess object

### Create Analysis Process

**POST /analysis-processes**

Create new analysis process.

**Request Body:**
```json
{
  "name": "Process Name",
  "description": "Process description",
  "type": "security_analysis",
  "status": "active"
}
```

**Response:** Created AnalysisProcess object with generated ID

### Update Analysis Process

**PUT /analysis-processes/{id}**

Update existing analysis process.

**Request Body:** Same as create, but includes the ID

### Delete Analysis Process

**DELETE /analysis-processes/{id}**

Delete analysis process. Returns 204 No Content on success.

---

## BPMN APIs

### Upload BPMN Diagram

**POST /analysis-processes/{id}/bpmn**

Upload BPMN diagram for analysis process.

**Request:** `multipart/form-data`
```
file: @diagram.bpmn
```

**Constraints:**
- Maximum file size: 5MB
- Format: BPMN 2.0 XML
- Must be valid XML structure

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "bpmn-analysis-123",
    "processId": "process-123",
    "diagramName": "credit-approval.bpmn",
    "analysis": {
      "complexity": "MEDIUM",
      "securityIssues": [
        {
          "type": "MISSING_VALIDATION",
          "severity": "MEDIUM",
          "element": "UserTask_ValidateInput",
          "description": "Input validation missing for user task",
          "recommendation": "Add input validation to user task"
        }
      ],
      "metrics": {
        "totalElements": 15,
        "decisionPoints": 3,
        "parallelFlows": 1,
        "estimatedExecutionTime": "5m 30s"
      },
      "recommendations": [
        "Consider adding error handling for failed tasks",
        "Implement timeout mechanisms for long-running tasks"
      ]
    },
    "rawContent": "<bpmn:definitions>...</bpmn:definitions>",
    "uploadedAt": "2024-01-01T12:00:00.000Z"
  }
}
```

### Get BPMN Analysis

**GET /analysis-processes/{id}/bpmn**

Retrieve BPMN analysis results for specific process.

**Response:** BpmnAnalysisResponse object

---

## OpenAPI APIs

### Analyze OpenAPI Specification

**POST /analysis-processes/{id}/openapi**

Upload OpenAPI specification for analysis process.

**Request:** `multipart/form-data`
```
file: @api-spec.yaml
specName: (optional) Custom specification name
```

**Constraints:**
- Maximum file size: 5MB
- Format: OpenAPI 3.0+ (JSON or YAML)
- Must be valid OpenAPI specification

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "openapi-analysis-123",
    "processId": "process-123",
    "specName": "credit-api.json",
    "analysis": {
      "specification": {
        "version": "3.0.3",
        "title": "Credit API",
        "version": "1.0.0",
        "totalEndpoints": 12,
        "totalSchemas": 8
      },
      "securityAnalysis": {
        "issues": [
          {
            "type": "MISSING_AUTHENTICATION",
            "severity": "HIGH",
            "endpoint": "/api/v1/users/{id}",
            "description": "Endpoint lacks authentication",
            "recommendation": "Add authentication scheme to endpoint"
          }
        ],
        "summary": {
          "totalIssues": 3,
          "highSeverity": 1,
          "mediumSeverity": 2,
          "lowSeverity": 0
        }
      },
      "validationSummary": {
        "isValid": true,
        "totalErrors": 0,
        "totalWarnings": 2,
        "errors": [],
        "warnings": [
          "Required property 'description' missing from operation get /api/v1/health"
        ]
      },
      "recommendations": [
        "Consider implementing rate limiting",
        "Add comprehensive API documentation",
        "Implement request validation middleware"
      ]
    },
    "rawContent": "{ \"openapi\": \"3.0.3\", ... }",
    "uploadedAt": "2024-01-01T12:00:00.000Z"
  }
}
```

### Get OpenAPI Analysis

**GET /analysis-processes/{id}/openapi**

Retrieve OpenAPI analysis results for specific process.

**Response:** OpenApiAnalysisResponse object

### Load OpenAPI Examples

**GET /openapi/examples**

Load example OpenAPI specifications from dataset.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "example-1",
      "specName": "petstore-api.json",
      "analysis": {
        "specification": {
          "version": "3.0.3",
          "title": "Pet Store API",
          "totalEndpoints": 20,
          "totalSchemas": 15
        }
      }
    }
  ]
}
```

### Analyze Standalone OpenAPI

**POST /openapi/analyze**

Analyze OpenAPI specification without creating analysis process.

**Request:** `multipart/form-data`
```
file: @api-spec.yaml
specName: (optional) Custom name
```

---

## LLM Management APIs

### Get LLM Analytics

**GET /monitoring/llm**

Retrieve LLM provider analytics and performance metrics.

**Response:**
```json
{
  "success": true,
  "data": {
    "activeProvider": {
      "id": "ollama",
      "displayName": "Ollama Cloud (qwen3-coder)",
      "mode": "remote",
      "baseUrl": "http://localhost:11434",
      "model": "qwen3-coder:480b-cloud",
      "status": "ACTIVE",
      "health": "HEALTHY"
    },
    "analytics": {
      "totalRequests": 145,
      "successfulRequests": 142,
      "failedRequests": 3,
      "averageResponseTime": 1250,
      "lastUsed": "2024-01-01T12:00:00.000Z"
    },
    "availableProviders": [
      {
        "id": "ollama",
        "displayName": "Ollama Cloud (qwen3-coder)",
        "enabled": true,
        "status": "ACTIVE"
      },
      {
        "id": "openrouter",
        "displayName": "OpenRouter Community",
        "enabled": false,
        "status": "INACTIVE"
      }
    ]
  }
}
```

### Switch LLM Provider

**POST /monitoring/llm/providers/{providerId}/activate**

Switch active LLM provider.

**Parameters:**
- `providerId`: ID of provider to activate

**Response:**
```json
{
  "success": true,
  "data": {
    "activeProvider": {
      "id": "openrouter",
      "displayName": "OpenRouter Community",
      "status": "ACTIVE"
    }
  },
  "message": "Successfully switched to OpenRouter provider"
}
```

### Check LLM Connectivity

**GET /monitoring/llm/check**

Check connectivity with LLM provider.

**Response:**
```json
{
  "success": true,
  "data": {
    "provider": "ollama",
    "status": "HEALTHY",
    "responseTime": 245,
    "lastCheck": "2024-01-01T12:00:00.000Z",
    "details": {
      "connection": "SUCCESS",
      "model": "qwen3-coder:480b-cloud",
      "available": true
    }
  }
}
```

---

## Test Data APIs

### Generate Test Data

**POST /testdata/generate**

Generate intelligent test data based on specifications.

**Request Body:**
```json
{
  "specificationType": "openapi",
  "specificationContent": "{ \"openapi\": \"3.0.3\", ... }",
  "dataTypes": ["user", "payment", "order"],
  "count": 100,
  "format": "json"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "generatedAt": "2024-01-01T12:00:00.000Z",
    "totalRecords": 100,
    "dataTypes": {
      "user": 25,
      "payment": 50,
      "order": 25
    },
    "data": [
      {
        "id": "user-1",
        "name": "John Doe",
        "email": "john.doe@example.com",
        "createdAt": "2024-01-01T10:00:00.000Z"
      }
    ]
  }
}
```

---

## WebSocket APIs

### Real-time Monitoring

**WebSocket Endpoint:** `/ws/monitoring`

Connect to WebSocket for real-time monitoring updates.

**Message Types:**

#### System Health Update
```json
{
  "type": "health_update",
  "data": {
    "status": "HEALTHY",
    "cpuUsage": 45.2,
    "memoryUsage": 67.8,
    "timestamp": "2024-01-01T12:00:00.000Z"
  }
}
```

#### Alert Notification
```json
{
  "type": "alert",
  "data": {
    "id": "alert-1",
    "title": "High CPU Usage",
    "severity": "WARNING",
    "message": "CPU usage exceeded 80%",
    "timestamp": "2024-01-01T12:00:00.000Z"
  }
}
```

#### Analysis Progress Update
```json
{
  "type": "analysis_progress",
  "data": {
    "processId": "process-123",
    "status": "RUNNING",
    "currentStep": "Validating BPMN structure",
    "progress": 45,
    "estimatedTimeRemaining": "2m 30s"
  }
}
```

---

## Data Models

### AnalysisProcess
```json
{
  "id": "string (uuid)",
  "name": "string",
  "description": "string",
  "type": "string",
  "status": "string (active|inactive|completed|failed)",
  "createdAt": "string (ISO 8601 timestamp)",
  "updatedAt": "string (ISO 8601 timestamp)",
  "bpmnDiagramPath": "string (file path)",
  "bpmnDiagramName": "string",
  "bpmnUploadedAt": "string (ISO 8601 timestamp)",
  "openapiSpecPath": "string (file path)",
  "openapiSpecName": "string",
  "openapiUploadedAt": "string (ISO 8601 timestamp)"
}
```

### BpmnAnalysisResponse
```json
{
  "id": "string (uuid)",
  "processId": "string",
  "diagramName": "string",
  "analysis": {
    "complexity": "string (LOW|MEDIUM|HIGH)",
    "securityIssues": [
      {
        "type": "string",
        "severity": "string (LOW|MEDIUM|HIGH|CRITICAL)",
        "element": "string",
        "description": "string",
        "recommendation": "string"
      }
    ],
    "metrics": {
      "totalElements": "number",
      "decisionPoints": "number",
      "parallelFlows": "number",
      "estimatedExecutionTime": "string"
    },
    "recommendations": ["string"]
  },
  "rawContent": "string (BPMN XML content)",
  "uploadedAt": "string (ISO 8601 timestamp)"
}
```

### OpenApiAnalysisResponse
```json
{
  "id": "string (uuid)",
  "processId": "string",
  "specName": "string",
  "analysis": {
    "specification": {
      "version": "string",
      "title": "string",
      "version": "string",
      "totalEndpoints": "number",
      "totalSchemas": "number"
    },
    "securityAnalysis": {
      "issues": [
        {
          "type": "string",
          "severity": "string (LOW|MEDIUM|HIGH|CRITICAL)",
          "endpoint": "string",
          "description": "string",
          "recommendation": "string"
        }
      ],
      "summary": {
        "totalIssues": "number",
        "highSeverity": "number",
        "mediumSeverity": "number",
        "lowSeverity": "number"
      }
    },
    "validationSummary": {
      "isValid": "boolean",
      "totalErrors": "number",
      "totalWarnings": "number",
      "errors": [
        {
          "path": "string",
          "message": "string",
          "code": "string"
        }
      ],
      "warnings": ["string"]
    },
    "recommendations": ["string"]
  },
  "rawContent": "string (OpenAPI JSON/YAML content)",
  "uploadedAt": "string (ISO 8601 timestamp)"
}
```

### SystemHealth
```json
{
  "id": "string",
  "status": "string (HEALTHY|DEGRADED|UNHEALTHY)",
  "cpuUsage": "number (0-100)",
  "memoryUsage": "number (0-100)",
  "diskUsage": "number (0-100)",
  "activeConnections": "number",
  "timestamp": "string (ISO 8601 timestamp)",
  "details": {
    "uptime": "string",
    "lastRestart": "string (ISO 8601 timestamp)",
    "components": {
      "database": "string",
      "fileSystem": "string",
      "llmService": "string"
    }
  }
}
```

### Metric
```json
{
  "id": "string",
  "name": "string",
  "type": "string (CPU|MEMORY|DISK|NETWORK|REQUESTS|ERRORS)",
  "value": "number",
  "unit": "string",
  "timestamp": "string (ISO 8601 timestamp)",
  "tags": {
    "key": "value"
  }
}
```

### Alert
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "severity": "string (INFO|WARNING|ERROR|CRITICAL)",
  "status": "string (ACTIVE|RESOLVED|ACKNOWLEDGED)",
  "source": "string",
  "createdAt": "string (ISO 8601 timestamp)",
  "resolvedAt": "string (ISO 8601 timestamp | null)",
  "tags": {
    "key": "value"
  }
}
```

---

## Code Examples

### cURL Examples

#### Create Analysis Process
```bash
curl -X POST http://localhost:8080/api/analysis-processes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Credit Approval Analysis",
    "description": "Security analysis of credit approval process",
    "type": "security_analysis",
    "status": "active"
  }'
```

#### Upload BPMN Diagram
```bash
curl -X POST http://localhost:8080/api/analysis-processes/PROCESS_ID/bpmn \
  -F "file=@credit-approval.bpmn"
```

#### Upload OpenAPI Specification
```bash
curl -X POST http://localhost:8080/api/analysis-processes/PROCESS_ID/openapi \
  -F "file=@api-spec.yaml" \
  -F "specName=Credit API Specification"
```

#### Get System Health
```bash
curl http://localhost:8080/api/monitoring/health
```

#### Switch LLM Provider
```bash
curl -X POST http://localhost:8080/api/monitoring/llm/providers/openrouter/activate
```

### JavaScript Examples

#### Using Fetch API
```javascript
// Create analysis process
const processResponse = await fetch('http://localhost:8080/api/analysis-processes', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: 'Security Analysis',
    description: 'Analysis for security compliance',
    type: 'security_analysis',
    status: 'active'
  })
});

const processData = await processResponse.json();

// Upload BPMN diagram
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const bpmnResponse = await fetch(`http://localhost:8080/api/analysis-processes/${processData.data.id}/bpmn`, {
  method: 'POST',
  body: formData
});

const bpmnData = await bpmnResponse.json();
console.log('BPMN Analysis:', bpmnData);
```

#### WebSocket Connection
```javascript
const socket = new WebSocket('ws://localhost:8080/ws/monitoring');

socket.onopen = function(event) {
  console.log('Connected to monitoring WebSocket');
};

socket.onmessage = function(event) {
  const message = JSON.parse(event.data);
  
  switch(message.type) {
    case 'health_update':
      updateHealthDashboard(message.data);
      break;
    case 'alert':
      showAlert(message.data);
      break;
    case 'analysis_progress':
      updateProgressBar(message.data);
      break;
  }
};

socket.onerror = function(error) {
  console.error('WebSocket error:', error);
};
```

### Python Examples

#### Using requests library
```python
import requests
import json

BASE_URL = "http://localhost:8080/api"

# Create analysis process
process_data = {
    "name": "Credit Approval Analysis",
    "description": "Security analysis of credit workflow",
    "type": "security_analysis",
    "status": "active"
}

response = requests.post(f"{BASE_URL}/analysis-processes", json=process_data)
process = response.json()

# Upload BPMN diagram
with open("credit-approval.bpmn", "rb") as file:
    files = {"file": file}
    bpmn_response = requests.post(
        f"{BASE_URL}/analysis-processes/{process['data']['id']}/bpmn",
        files=files
    )
    bpmn_analysis = bpmn_response.json()

# Check system health
health_response = requests.get(f"{BASE_URL}/monitoring/health")
health_data = health_response.json()
print(f"System Status: {health_data['data']['status']}")
```

This comprehensive API reference provides all necessary information for integrating with the Security Orchestrator API, including detailed endpoint documentation, request/response schemas, error handling, and practical code examples.