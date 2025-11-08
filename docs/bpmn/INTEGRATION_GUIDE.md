# BPMN Analysis System - Integration Guide

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Basic Integration](#basic-integration)
4. [Advanced Integration](#advanced-integration)
5. [Frontend Integration](#frontend-integration)
6. [Custom Implementation](#custom-implementation)
7. [Testing Your Integration](#testing-your-integration)
8. [Best Practices](#best-practices)
9. [Security Considerations](#security-considerations)
10. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements
- **Java**: 21+ for backend integration
- **Spring Boot**: 3.x compatible application
- **Network Access**: HTTP/HTTPS connectivity to SecurityOrchestrator
- **Authentication**: API key or JWT token for secure access
- **BPMN Files**: BPMN 2.0 compliant XML files for processing

### Knowledge Requirements
- Basic understanding of REST APIs
- Familiarity with JSON data formats
- Experience with HTTP client libraries
- Understanding of BPMN 2.0 concepts (recommended)

## Environment Setup

### 1. Java Spring Boot Integration

#### Add Dependencies
```xml
<!-- Maven -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

```kotlin
// Gradle (Kotlin DSL)
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("org.springframework.boot:spring-boot-starter-webflux")
implementation("org.springframework.boot:spring-boot-starter-websocket")
```

#### Application Configuration
```yaml
# application.yml
security-orchestrator:
  bpmn:
    base-url: "http://localhost:8080/api/v1/bpmn"
    api-key: "${BPMN_API_KEY:your-default-api-key}"
    timeout: 30000
    max-retries: 3
    websocket:
      enabled: true
      heartbeat-interval: 30000
```

#### Configuration Properties Class
```java
@ConfigurationProperties(prefix = "security-orchestrator.bpmn")
@Data
public class BPMNConfigProperties {
    
    private String baseUrl;
    private String apiKey;
    private int timeout = 30000;
    private int maxRetries = 3;
    private WebSocket websocket = new WebSocket();
    
    @Data
    public static class WebSocket {
        private boolean enabled = true;
        private int heartbeatInterval = 30000;
    }
}
```

### 2. Environment Variables Setup
```bash
# .env file or system environment
export BPMN_API_KEY="your-secure-api-key"
export BPMN_BASE_URL="http://localhost:8080/api/v1/bpmn"
export BPMN_TIMEOUT="30000"
```

## Basic Integration

### 1. Create BPMN Client Service

```java
@Service
@Slf4j
public class BPMNAnalysisClient {
    
    private final RestTemplate restTemplate;
    private final BPMNConfigProperties config;
    private final ObjectMapper objectMapper;
    
    public BPMNAnalysisClient(RestTemplate restTemplate, 
                            BPMNConfigProperties config) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Upload and analyze a BPMN process
     */
    public ProcessAnalysisResult uploadAndAnalyzeProcess(
            String processName, 
            String processDescription,
            InputStream bpmnFile) {
        
        try {
            // Prepare multipart request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new InputStreamResource(bpmnFile));
            body.add("name", processName);
            body.add("description", processDescription);
            
            HttpEntity<MultiValueMap<String, Object>> request = 
                new HttpEntity<>(body, headers);
            
            // Upload process
            ProcessUploadResponse uploadResponse = restTemplate.postForObject(
                config.getBaseUrl() + "/processes",
                request,
                ProcessUploadResponse.class
            );
            
            log.info("Process uploaded successfully: {}", uploadResponse.getProcessId());
            
            // Start analysis
            AnalysisRequest analysisRequest = AnalysisRequest.builder()
                .processId(uploadResponse.getProcessId())
                .analysisType(AnalysisType.COMPREHENSIVE)
                .complianceStandards(Arrays.asList("GDPR", "PCI-DSS"))
                .build();
            
            AnalysisResponse analysisResponse = restTemplate.postForObject(
                config.getBaseUrl() + "/analysis",
                analysisRequest,
                AnalysisResponse.class
            );
            
            // Wait for analysis completion
            return waitForAnalysisCompletion(analysisResponse.getAnalysisId());
            
        } catch (Exception e) {
            log.error("Failed to upload and analyze process", e);
            throw new BPMNAnalysisException("Process analysis failed", e);
        }
    }
    
    /**
     * Get analysis results
     */
    public AnalysisResult getAnalysisResult(String analysisId) {
        try {
            return restTemplate.getForObject(
                config.getBaseUrl() + "/analysis/" + analysisId,
                AnalysisResult.class
            );
        } catch (Exception e) {
            log.error("Failed to get analysis results: {}", analysisId, e);
            throw new BPMNAnalysisException("Failed to retrieve analysis", e);
        }
    }
    
    private ProcessAnalysisResult waitForAnalysisCompletion(String analysisId) {
        int maxWaitTime = 600; // 10 minutes
        int waitedTime = 0;
        
        while (waitedTime < maxWaitTime) {
            try {
                Thread.sleep(5000); // Wait 5 seconds
                waitedTime += 5;
                
                AnalysisResult result = getAnalysisResult(analysisId);
                if (result.getStatus() == AnalysisStatus.COMPLETED) {
                    return ProcessAnalysisResult.builder()
                        .analysisId(analysisId)
                        .processId(result.getProcessId())
                        .securityScore(result.getResults().getSecurityScore())
                        .findings(result.getResults().getFindings())
                        .recommendations(result.getResults().getRecommendations())
                        .complianceResults(result.getResults().getComplianceResults())
                        .build();
                } else if (result.getStatus() == AnalysisStatus.FAILED) {
                    throw new BPMNAnalysisException("Analysis failed: " + result.getErrorMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BPMNAnalysisException("Analysis wait interrupted", e);
            }
        }
        
        throw new BPMNAnalysisException("Analysis timeout - analysis took longer than expected");
    }
}
```

### 2. Exception Handling

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class BPMNAnalysisException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> details;
    
    public BPMNAnalysisException(String message) {
        super(message);
        this.errorCode = "BPMN_ANALYSIS_ERROR";
        this.details = Collections.emptyMap();
    }
    
    public BPMNAnalysisException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BPMN_ANALYSIS_ERROR";
        this.details = Collections.emptyMap();
    }
    
    public BPMNAnalysisException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = Collections.emptyMap();
    }
}
```

### 3. REST Controller Example

```java
@RestController
@RequestMapping("/api/v1/processes")
@Validated
public class ProcessAnalysisController {
    
    private final BPMNAnalysisClient bpmnClient;
    
    public ProcessAnalysisController(BPMNAnalysisClient bpmnClient) {
        this.bpmnClient = bpmnClient;
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<ProcessAnalysisResult> analyzeProcess(
            @RequestParam("file") @NotNull @NotEmpty MultipartFile file,
            @RequestParam("name") @NotBlank String name,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            ProcessAnalysisResult result = bpmnClient.uploadAndAnalyzeProcess(
                name, 
                description != null ? description : "",
                file.getInputStream()
            );
            
            return ResponseEntity.ok(result);
            
        } catch (BPMNAnalysisException e) {
            log.error("Process analysis failed", e);
            return ResponseEntity.badRequest()
                .body(ProcessAnalysisResult.builder()
                    .error(e.getMessage())
                    .errorCode(e.getErrorCode())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during process analysis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProcessAnalysisResult.builder()
                    .error("Internal server error")
                    .errorCode("INTERNAL_ERROR")
                    .build());
        }
    }
    
    @GetMapping("/analysis/{analysisId}")
    public ResponseEntity<AnalysisResult> getAnalysisResult(
            @PathVariable @NotBlank String analysisId) {
        
        try {
            AnalysisResult result = bpmnClient.getAnalysisResult(analysisId);
            return ResponseEntity.ok(result);
        } catch (BPMNAnalysisException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
```

## Advanced Integration

### 1. WebSocket Integration for Real-time Updates

```java
@Component
@Slf4j
public class BPMNAnalysisWebSocketClient {
    
    private final BPMNConfigProperties config;
    private WebSocketSession session;
    private volatile boolean connected = false;
    private final Set<String> subscribedAnalysisIds = new ConcurrentHashMap.newKeySet();
    
    public BPMNAnalysisWebSocketClient(BPMNConfigProperties config) {
        this.config = config;
        initializeWebSocket();
    }
    
    private void initializeWebSocket() {
        if (!config.getWebSocket().isEnabled()) {
            log.info("WebSocket integration disabled");
            return;
        }
        
        WebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                log.info("Connected to BPMN analysis WebSocket");
                connected = true;
                this.session = session;
            }
            
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                try {
                    AnalysisUpdate update = objectMapper.readValue(
                        message.getPayload(), 
                        AnalysisUpdate.class
                    );
                    handleAnalysisUpdate(update);
                } catch (Exception e) {
                    log.error("Failed to handle WebSocket message", e);
                }
            }
            
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                log.info("WebSocket connection closed: {}", status);
                connected = false;
                scheduleReconnect();
            }
        };
        
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        String wsUrl = config.getBaseUrl().replace("http", "ws") + "/ws/analysis";
        
        client.execute(URI.create(wsUrl), handler);
    }
    
    public void subscribeToAnalysis(String analysisId) {
        if (!connected) {
            log.warn("WebSocket not connected, analysis update will not be received");
            return;
        }
        
        subscribedAnalysisIds.add(analysisId);
        
        try {
            SubscribeMessage subscribeMessage = SubscribeMessage.builder()
                .type("SUBSCRIBE_ANALYSIS")
                .analysisId(analysisId)
                .build();
            
            session.sendMessage(new TextMessage(
                objectMapper.writeValueAsString(subscribeMessage)
            ));
            
            log.info("Subscribed to analysis updates: {}", analysisId);
        } catch (Exception e) {
            log.error("Failed to subscribe to analysis: {}", analysisId, e);
        }
    }
    
    private void handleAnalysisUpdate(AnalysisUpdate update) {
        switch (update.getType()) {
            case "ANALYSIS_PROGRESS":
                handleProgressUpdate(update);
                break;
            case "ANALYSIS_COMPLETE":
                handleCompleteUpdate(update);
                break;
            case "ANALYSIS_ERROR":
                handleErrorUpdate(update);
                break;
            default:
                log.debug("Unknown update type: {}", update.getType());
        }
    }
    
    private void handleProgressUpdate(AnalysisUpdate update) {
        // Update UI or notify subscribers
        log.info("Analysis {} progress: {}%", 
            update.getAnalysisId(), 
            update.getProgress()
        );
        
        // Send to interested parties
        notifyProgressSubscribers(update);
    }
    
    private void scheduleReconnect() {
        // Implement exponential backoff reconnection logic
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
                initializeWebSocket();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

### 2. Async Processing with CompletableFuture

```java
@Service
public class AsyncBPMNAnalysisService {
    
    private final BPMNAnalysisClient client;
    private final TaskExecutor taskExecutor;
    
    public AsyncBPMNAnalysisService(BPMNAnalysisClient client, TaskExecutor taskExecutor) {
        this.client = client;
        this.taskExecutor = taskExecutor;
    }
    
    public CompletableFuture<ProcessAnalysisResult> analyzeProcessAsync(
            String processName, 
            String processDescription,
            InputStream bpmnFile) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return client.uploadAndAnalyzeProcess(
                    processName, 
                    processDescription, 
                    bpmnFile
                );
            } catch (Exception e) {
                throw new BPMNAnalysisException("Async analysis failed", e);
            }
        }, taskExecutor);
    }
    
    public CompletableFuture<List<AnalysisResult>> analyzeProcessesBatch(
            List<ProcessDefinition> processes) {
        
        return CompletableFuture.allOf(
            processes.stream()
                .map(process -> analyzeProcessAsync(
                    process.getName(),
                    process.getDescription(),
                    process.getFileInputStream()
                ))
                .toArray(CompletableFuture[]::new)
        ).thenApply(v -> 
            processes.stream()
                .map(process -> {
                    try {
                        return client.uploadAndAnalyzeProcess(
                            process.getName(),
                            process.getDescription(),
                            process.getFileInputStream()
                        );
                    } catch (Exception e) {
                        log.error("Failed to analyze process: {}", process.getName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        );
    }
}
```

### 3. Circuit Breaker Pattern

```java
@Component
public class BPMNCircuitBreaker {
    
    private final CircuitBreaker circuitBreaker;
    private final BPMNAnalysisClient client;
    
    public BPMNCircuitBreaker(BPMNAnalysisClient client) {
        this.client = client;
        this.circuitBreaker = CircuitBreaker.ofDefaults("bpmn-analysis");
    }
    
    public ProcessAnalysisResult analyzeWithProtection(
            String processName, 
            String description, 
            InputStream file) {
        
        return circuitBreaker.executeSupplier(() -> {
            try {
                return client.uploadAndAnalyzeProcess(processName, description, file);
            } catch (Exception e) {
                throw new BPMNAnalysisException("Protected analysis failed", e);
            }
        });
    }
    
    public void configureCircuitBreaker() {
        circuitBreaker.getCircuitBreakerConfig()
            .failureRateThreshold(50) // 50% failure rate
            .waitDurationInOpenState(Duration.ofSeconds(30)) // 30 seconds in open state
            .slidingWindowSize(10) // 10 calls in sliding window
            .minimumNumberOfCalls(5); // Minimum 5 calls before evaluating
    }
}
```

## Frontend Integration

### 1. JavaScript/TypeScript Client

```typescript
export interface BPMNAnalysisClientConfig {
  baseUrl: string;
  apiKey?: string;
  timeout?: number;
  websocketUrl?: string;
}

export interface ProcessAnalysisRequest {
  name: string;
  description?: string;
  file: File;
}

export interface AnalysisResult {
  analysisId: string;
  processId: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  securityScore?: number;
  findings?: SecurityFinding[];
  recommendations?: SecurityRecommendation[];
  complianceResults?: Record<string, ComplianceResult>;
}

export class BPMNAnalysisClient {
  private config: BPMNAnalysisClientConfig;
  private websocket?: WebSocket;
  private progressCallbacks: Map<string, (progress: number) => void> = new Map();

  constructor(config: BPMNAnalysisClientConfig) {
    this.config = {
      timeout: 30000,
      websocketUrl: config.baseUrl.replace('http', 'ws') + '/ws/analysis',
      ...config
    };
  }

  async uploadAndAnalyzeProcess(request: ProcessAnalysisRequest): Promise<AnalysisResult> {
    const formData = new FormData();
    formData.append('file', request.file);
    formData.append('name', request.name);
    if (request.description) {
      formData.append('description', request.description);
    }

    const headers: Record<string, string> = {};
    if (this.config.apiKey) {
      headers['X-API-Key'] = this.config.apiKey;
    }

    try {
      // Upload process
      const uploadResponse = await fetch(`${this.config.baseUrl}/processes`, {
        method: 'POST',
        headers,
        body: formData
      });

      if (!uploadResponse.ok) {
        throw new Error(`Upload failed: ${uploadResponse.statusText}`);
      }

      const uploadData = await uploadResponse.json();

      // Start analysis
      const analysisRequest = {
        processId: uploadData.processId,
        analysisType: 'COMPREHENSIVE',
        complianceStandards: ['GDPR', 'PCI-DSS']
      };

      const analysisResponse = await fetch(`${this.config.baseUrl}/analysis`, {
        method: 'POST',
        headers: {
          ...headers,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(analysisRequest)
      });

      if (!analysisResponse.ok) {
        throw new Error(`Analysis start failed: ${analysisResponse.statusText}`);
      }

      const analysisData = await analysisResponse.json();

      // Setup WebSocket for progress updates
      this.setupWebSocketConnection(analysisData.analysisId);

      return {
        analysisId: analysisData.analysisId,
        processId: uploadData.processId,
        status: 'IN_PROGRESS'
      };

    } catch (error) {
      throw new BPMNAnalysisError('Process analysis failed', error);
    }
  }

  private setupWebSocketConnection(analysisId: string): void {
    if (!this.config.websocketUrl) return;

    try {
      this.websocket = new WebSocket(this.config.websocketUrl);

      this.websocket.onopen = () => {
        console.log('WebSocket connected');
        this.subscribeToAnalysis(analysisId);
      };

      this.websocket.onmessage = (event) => {
        const update = JSON.parse(event.data);
        this.handleAnalysisUpdate(analysisId, update);
      };

      this.websocket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };

      this.websocket.onclose = () => {
        console.log('WebSocket closed');
      };

    } catch (error) {
      console.error('Failed to setup WebSocket:', error);
    }
  }

  private subscribeToAnalysis(analysisId: string): void {
    if (!this.websocket || this.websocket.readyState !== WebSocket.OPEN) return;

    const subscription = {
      type: 'SUBSCRIBE_ANALYSIS',
      analysisId
    };

    this.websocket.send(JSON.stringify(subscription));
  }

  private handleAnalysisUpdate(analysisId: string, update: any): void {
    switch (update.type) {
      case 'ANALYSIS_PROGRESS':
        const progressCallback = this.progressCallbacks.get(analysisId);
        if (progressCallback) {
          progressCallback(update.progress);
        }
        break;
      case 'ANALYSIS_COMPLETE':
        // Notify completion
        break;
      case 'ANALYSIS_ERROR':
        // Handle error
        break;
    }
  }

  onProgress(analysisId: string, callback: (progress: number) => void): void {
    this.progressCallbacks.set(analysisId, callback);
  }

  async getAnalysisResult(analysisId: string): Promise<AnalysisResult> {
    const headers: Record<string, string> = {};
    if (this.config.apiKey) {
      headers['X-API-Key'] = this.config.apiKey;
    }

    const response = await fetch(`${this.config.baseUrl}/analysis/${analysisId}`, {
      headers
    });

    if (!response.ok) {
      throw new Error(`Failed to get analysis result: ${response.statusText}`);
    }

    return await response.json();
  }

  disconnect(): void {
    if (this.websocket) {
      this.websocket.close();
      this.websocket = undefined;
    }
  }
}

export class BPMNAnalysisError extends Error {
  constructor(message: string, public cause?: any) {
    super(message);
  }
}
```

### 2. React Integration Hook

```typescript
import { useState, useEffect, useCallback } from 'react';
import { BPMNAnalysisClient, AnalysisResult } from './BPMNAnalysisClient';

export interface UseBPMNAnalysisReturn {
  analysisResult: AnalysisResult | null;
  isAnalyzing: boolean;
  progress: number;
  error: string | null;
  analyzeProcess: (file: File, name: string, description?: string) => Promise<void>;
  clearError: () => void;
}

export function useBPMNAnalysis(client: BPMNAnalysisClient): UseBPMNAnalysisReturn {
  const [analysisResult, setAnalysisResult] = useState<AnalysisResult | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const analyzeProcess = useCallback(async (file: File, name: string, description?: string) => {
    setIsAnalyzing(true);
    setProgress(0);
    setError(null);
    setAnalysisResult(null);

    try {
      const result = await client.uploadAndAnalyzeProcess({ file, name, description });
      setAnalysisResult(result);

      // Setup progress tracking
      client.onProgress(result.analysisId, (progress: number) => {
        setProgress(progress);
      });

      // Poll for final result
      const checkResult = async () => {
        const finalResult = await client.getAnalysisResult(result.analysisId);
        if (finalResult.status === 'COMPLETED' || finalResult.status === 'FAILED') {
          setAnalysisResult(finalResult);
          setIsAnalyzing(false);
          if (finalResult.status === 'FAILED') {
            setError('Analysis failed');
          }
        } else {
          setTimeout(checkResult, 2000); // Check every 2 seconds
        }
      };

      setTimeout(checkResult, 5000); // Start checking after 5 seconds

    } catch (err) {
      setError(err instanceof Error ? err.message : 'Analysis failed');
      setIsAnalyzing(false);
    }
  }, [client]);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    analysisResult,
    isAnalyzing,
    progress,
    error,
    analyzeProcess,
    clearError
  };
}
```

### 3. Vue.js Integration Component

```vue
<template>
  <div class="bpmn-analysis">
    <div class="upload-section">
      <h3>Upload BPMN Process</h3>
      <input 
        type="file" 
        @change="onFileChange" 
        accept=".bpmn,.xml"
        ref="fileInput"
      />
      <input 
        v-model="processName" 
        placeholder="Process Name"
        class="process-name-input"
      />
      <textarea 
        v-model="processDescription" 
        placeholder="Process Description"
        class="process-description-input"
      />
      <button 
        @click="startAnalysis" 
        :disabled="!canStartAnalysis || isAnalyzing"
      >
        {{ isAnalyzing ? 'Analyzing...' : 'Start Analysis' }}
      </button>
    </div>

    <div v-if="isAnalyzing" class="progress-section">
      <h4>Analysis Progress</h4>
      <div class="progress-bar">
        <div 
          class="progress-fill" 
          :style="{ width: `${progress}%` }"
        ></div>
      </div>
      <p>{{ progress }}% Complete</p>
    </div>

    <div v-if="analysisResult" class="results-section">
      <h4>Analysis Results</h4>
      <div class="security-score">
        <h5>Security Score: {{ analysisResult.securityScore }}%</h5>
      </div>
      
      <div v-if="analysisResult.findings?.length" class="findings">
        <h5>Security Findings ({{ analysisResult.findings.length }})</h5>
        <div 
          v-for="finding in analysisResult.findings" 
          :key="finding.id"
          class="finding-item"
          :class="`severity-${finding.severity.toLowerCase()}`"
        >
          <h6>{{ finding.title }}</h6>
          <p>{{ finding.description }}</p>
          <p><strong>Recommendation:</strong> {{ finding.recommendation }}</p>
        </div>
      </div>
    </div>

    <div v-if="error" class="error-section">
      <h4>Error</h4>
      <p class="error-message">{{ error }}</p>
      <button @click="clearError">Clear Error</button>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { BPMNAnalysisClient } from './BPMNAnalysisClient';

export default {
  name: 'BPMNAnalysis',
  setup() {
    const fileInput = ref(null);
    const selectedFile = ref(null);
    const processName = ref('');
    const processDescription = ref('');
    const analysisResult = ref(null);
    const isAnalyzing = ref(false);
    const progress = ref(0);
    const error = ref(null);

    const client = new BPMNAnalysisClient({
      baseUrl: 'http://localhost:8080/api/v1/bpmn',
      apiKey: 'your-api-key' // Load from environment
    });

    const canStartAnalysis = computed(() => {
      return selectedFile.value && processName.value.trim();
    });

    const onFileChange = (event) => {
      const file = event.target.files[0];
      if (file) {
        selectedFile.value = file;
        if (!processName.value) {
          processName.value = file.name.replace(/\.[^/.]+$/, "");
        }
      }
    };

    const startAnalysis = async () => {
      if (!canStartAnalysis.value) return;

      isAnalyzing.value = true;
      progress.value = 0;
      error.value = null;
      analysisResult.value = null;

      try {
        const result = await client.uploadAndAnalyzeProcess({
          file: selectedFile.value,
          name: processName.value,
          description: processDescription.value
        });

        analysisResult.value = result;

        // Setup progress tracking
        client.onProgress(result.analysisId, (progressValue) => {
          progress.value = progressValue;
        });

      } catch (err) {
        error.value = err.message || 'Analysis failed';
        isAnalyzing.value = false;
      }
    };

    const clearError = () => {
      error.value = null;
    };

    return {
      fileInput,
      selectedFile,
      processName,
      processDescription,
      analysisResult,
      isAnalyzing,
      progress,
      error,
      canStartAnalysis,
      onFileChange,
      startAnalysis,
      clearError
    };
  }
};
</script>

<style scoped>
.bpmn-analysis {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.upload-section {
  margin-bottom: 30px;
}

.progress-bar {
  width: 100%;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
  margin: 10px 0;
}

.progress-fill {
  height: 100%;
  background-color: #007bff;
  transition: width 0.3s ease;
}

.finding-item {
  padding: 15px;
  margin: 10px 0;
  border-radius: 5px;
  border-left: 4px solid #ccc;
}

.finding-item.severity-critical {
  background-color: #f8d7da;
  border-left-color: #dc3545;
}

.finding-item.severity-high {
  background-color: #fff3cd;
  border-left-color: #ffc107;
}

.finding-item.severity-medium {
  background-color: #d4edda;
  border-left-color: #28a745;
}

.finding-item.severity-low {
  background-color: #d1ecf1;
  border-left-color: #17a2b8;
}
</style>
```

This comprehensive integration guide provides everything needed to integrate the BPMN Analysis System into your applications, from basic REST API usage to advanced real-time WebSocket integration and multiple frontend framework examples.