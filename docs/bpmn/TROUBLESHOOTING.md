# BPMN Analysis System - Troubleshooting Guide

## Table of Contents

1. [Common Issues and Solutions](#common-issues-and-solutions)
2. [BPMN File Processing Issues](#bpmn-file-processing-issues)
3. [AI Integration Problems](#ai-integration-problems)
4. [Performance Issues](#performance-issues)
5. [Authentication and Authorization](#authentication-and-authorization)
6. [Network and Connectivity](#network-and-connectivity)
7. [Configuration Problems](#configuration-problems)
8. [Debugging and Logging](#debugging-and-logging)
9. [Performance Tuning](#performance-tuning)
10. [FAQ Section](#faq-section)

## Common Issues and Solutions

### Issue 1: BPMN File Upload Fails

**Symptoms:**
- File upload returns 400 Bad Request
- "Invalid BPMN file format" error
- XML parsing errors

**Possible Causes:**
- Invalid BPMN 2.0 XML structure
- File size exceeds limits
- Unsupported BPMN elements
- Malformed XML

**Solutions:**

#### 1. Validate BPMN File Structure
```bash
# Use XML validator to check file
xmllint --schema /path/to/bpmn.xsd your-process.bpmn

# Check basic XML structure
xmllint --noout your-process.bpmn
```

#### 2. Verify BPMN 2.0 Compliance
```java
// Java validation example
@Component
public class BPMNValidator {
    
    public ValidationResult validateBPMNFile(InputStream inputStream) {
        try {
            // Parse with strict validation
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);
            
            // Check BPMN version
            String bpmnVersion = modelInstance.getDocument().getDocumentElement()
                .getAttribute("version");
            
            if (!"2.0".equals(bpmnVersion)) {
                return ValidationResult.builder()
                    .valid(false)
                    .error("Unsupported BPMN version: " + bpmnVersion)
                    .build();
            }
            
            // Validate required elements
            Collection<StartEvent> startEvents = modelInstance.getModelInstance()
                .getModelElementsByType(StartEvent.class);
                
            if (startEvents.isEmpty()) {
                return ValidationResult.builder()
                    .valid(false)
                    .error("BPMN process must have at least one start event")
                    .build();
            }
            
            return ValidationResult.builder().valid(true).build();
            
        } catch (Exception e) {
            return ValidationResult.builder()
                .valid(false)
                .error("BPMN validation failed: " + e.getMessage())
                .build();
        }
    }
}
```

#### 3. Check File Size and Format
```yaml
# application.yml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
      enabled: true
```

### Issue 2: Analysis Takes Too Long

**Symptoms:**
- Analysis never completes
- Timeout errors
- System appears frozen

**Possible Causes:**
- Complex BPMN process
- AI service timeout
- Database performance issues
- Memory constraints

**Solutions:**

#### 1. Optimize Analysis Configuration
```yaml
# application-bpmn.yml
bpmn:
  analysis:
    timeout: 600s  # Increase timeout for complex processes
    async:
      enabled: true
      max-concurrent: 5
    cache:
      enabled: true
      ttl: 3600s
```

#### 2. Use Async Processing
```java
@Service
public class AsyncBPMNAnalysisService {
    
    @Async("taskExecutor")
    public CompletableFuture<AnalysisResult> analyzeProcessAsync(String processId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return performAnalysis(processId);
            } catch (Exception e) {
                throw new AnalysisException("Async analysis failed", e);
            }
        });
    }
}
```

#### 3. Monitor Resource Usage
```java
@Component
public class ResourceMonitor {
    
    public void checkSystemResources() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        
        double heapUsedPercent = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
        
        if (heapUsedPercent > 80) {
            log.warn("High memory usage: {}%", heapUsedPercent);
            // Trigger garbage collection
            System.gc();
        }
    }
}
```

### Issue 3: AI Service Unavailable

**Symptoms:**
- "AI_SERVICE_UNAVAILABLE" errors
- AI analysis fails to start
- Timeout connecting to AI provider

**Possible Causes:**
- Network connectivity issues
- Invalid API keys
- Rate limiting
- Provider service outage

**Solutions:**

#### 1. Test AI Provider Connectivity
```bash
# Test OpenRouter connection
curl -X POST https://openrouter.ai/api/v1/models \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json"

# Test Ollama connection
curl http://localhost:11434/api/tags
```

#### 2. Configure Fallback Provider
```java
@Configuration
public class AIProviderConfig {
    
    @Bean
    public AIProviderChain aiProviderChain() {
        return new AIProviderChain()
            .addProvider(new OpenRouterProvider())
            .addProvider(new OllamaProvider())
            .addProvider(new MockProvider()); // Fallback
    }
}
```

#### 3. Implement Retry Logic with Circuit Breaker
```java
@Service
public class ResilientAIService {
    
    private final CircuitBreaker aiCircuitBreaker;
    
    public ResilientAIService() {
        this.aiCircuitBreaker = CircuitBreaker.ofDefaults("ai-service");
    }
    
    public String generateInsight(String prompt) {
        return aiCircuitBreaker.executeSupplier(() -> {
            try {
                return primaryAIProvider.generateCompletion(prompt);
            } catch (Exception e) {
                log.warn("Primary AI provider failed, trying fallback", e);
                return fallbackAIProvider.generateCompletion(prompt);
            }
        });
    }
}
```

## BPMN File Processing Issues

### Invalid XML Structure

**Problem**: BPMN file contains malformed XML

**Solution**:
```java
@Component
public class XMLProcessor {
    
    public void validateXMLStructure(InputStream inputStream) throws XMLValidationException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            
            // Check for common XML issues
            NodeList elements = document.getElementsByTagName("*");
            for (int i = 0; i < elements.getLength(); i++) {
                Element element = (Element) elements.item(i);
                
                // Check for unclosed tags
                if (!element.hasChildNodes() && !element.hasAttributes()) {
                    // Check if this could be a self-closing tag issue
                    String tagName = element.getTagName();
                    if (isBPMNSelfClosingTag(tagName)) {
                        continue; // This is expected
                    }
                }
            }
            
        } catch (SAXException e) {
            throw new XMLValidationException("XML parsing failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new XMLValidationException("XML validation failed: " + e.getMessage(), e);
        }
    }
}
```

### Unsupported BPMN Elements

**Problem**: Process contains unsupported BPMN elements

**Solution**:
```java
@Component
public class UnsupportedElementHandler {
    
    public void handleUnsupportedElements(BpmnModelInstance modelInstance) {
        Collection<BaseElement> allElements = modelInstance.getModelElementsByType(BaseElement.class);
        
        Set<String> unsupportedElements = new HashSet<>();
        
        for (BaseElement element : allElements) {
            if (!isSupportedElement(element)) {
                unsupportedElements.add(element.getElementType().getTypeName());
            }
        }
        
        if (!unsupportedElements.isEmpty()) {
            log.warn("Found unsupported BPMN elements: {}", unsupportedElements);
            // Decide whether to reject the process or continue with warnings
        }
    }
    
    private boolean isSupportedElement(BaseElement element) {
        // Check against supported element types
        return element instanceof StartEvent ||
               element instanceof EndEvent ||
               element instanceof UserTask ||
               element instanceof ServiceTask ||
               // ... add other supported types
               false;
    }
}
```

## AI Integration Problems

### OpenRouter API Issues

**Problem**: OpenRouter API returns authentication errors

**Solution**:
```java
@Configuration
public class OpenRouterConfig {
    
    @Value("${openrouter.api.key}")
    private String apiKey;
    
    @Value("${openrouter.api.base-url}")
    private String baseUrl;
    
    @Bean
    public RestTemplate openRouterRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        
        restTemplate.setInterceptors(Collections.singletonList(
            (request, body, execution) -> {
                request.getHeaders().addAll(headers);
                return execution.execute(request, body);
            }
        ));
        
        return restTemplate;
    }
}
```

### Ollama Connection Issues

**Problem**: Cannot connect to Ollama service

**Solution**:
```java
@Service
public class OllamaHealthCheck {
    
    @Scheduled(fixedRate = 30000) // Check every 30 seconds
    public void checkOllamaHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:11434/api/tags", 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("Ollama service is healthy");
            } else {
                log.warn("Ollama service returned status: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Ollama service is not reachable: {}", e.getMessage());
            // Implement fallback logic
        }
    }
}
```

## Performance Issues

### Slow BPMN Parsing

**Problem**: BPMN file parsing takes too long

**Solution**:
```java
@Component
public class OptimizedBPMNParser {
    
    public BpmnModelInstance parseBPMNEfficiently(InputStream inputStream) {
        try {
            // Use streaming parser for large files
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);
            
            // Pre-compute element relationships
            Map<String, Set<String>> elementDependencies = 
                precomputeDependencies(modelInstance);
            
            // Cache frequently accessed data
            cacheProcessMetadata(modelInstance, elementDependencies);
            
            return modelInstance;
            
        } catch (Exception e) {
            throw new BPMNParseException("Failed to parse BPMN efficiently", e);
        }
    }
    
    private Map<String, Set<String>> precomputeDependencies(BpmnModelInstance modelInstance) {
        Map<String, Set<String>> dependencies = new HashMap<>();
        
        // Build dependency graph for faster traversal
        Collection<FlowNode> flowNodes = modelInstance.getModelElementsByType(FlowNode.class);
        for (FlowNode node : flowNodes) {
            String elementId = node.getId();
            Set<String> dependsOn = new HashSet<>();
            
            // Add incoming sequence flows
            node.getIncoming().forEach(flow -> 
                dependsOn.add(flow.getSource().getId())
            );
            
            dependencies.put(elementId, dependsOn);
        }
        
        return dependencies;
    }
}
```

### Memory Issues

**Problem**: OutOfMemoryError during analysis

**Solution**:
```java
@Configuration
public class MemoryOptimizationConfig {
    
    @Bean
    public TaskExecutor analysisTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("bpmn-analysis-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
    
    @EventListener
    public void handleMemoryPressure(ApplicationMemoryPressureEvent event) {
        // Clear caches when memory pressure is detected
        bpmnCache.clear();
        analysisCache.clear();
        
        // Force garbage collection
        System.gc();
    }
}
```

## Authentication and Authorization

### API Key Authentication Issues

**Problem**: "Unauthorized" responses when using API key

**Solution**:
```java
@Configuration
public class APIKeyAuthConfig {
    
    @Bean
    public APIKeyAuthFilter apiKeyAuthFilter() {
        return new APIKeyAuthFilter(apiKeyValidator);
    }
}

@Component
public class APIKeyValidator {
    
    public boolean isValidAPIKey(String apiKey) {
        try {
            // Check against stored API keys
            return storedAPIKeys.contains(apiKey);
        } catch (Exception e) {
            log.error("API key validation failed", e);
            return false;
        }
    }
}
```

### JWT Token Issues

**Problem**: JWT token validation fails

**Solution**:
```java
@Component
public class JWTValidator {
    
    public JWTClaims validateJWT(String token) {
        try {
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            JWSObject jwsObject = JWSObject.parse(token);
            
            if (jwsObject.verify(verifier)) {
                return JWTClaims.fromJSON(jwsObject.getPayload().toString());
            } else {
                throw new JWTValidationException("JWT signature verification failed");
            }
            
        } catch (Exception e) {
            log.error("JWT validation failed", e);
            throw new JWTValidationException("Invalid JWT token", e);
        }
    }
}
```

## Network and Connectivity

### Connection Timeouts

**Problem**: Requests timeout when connecting to external services

**Solution**:
```java
@Configuration
public class NetworkConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory();
            
        factory.setConnectTimeout(5000); // 5 seconds
        factory.setReadTimeout(30000); // 30 seconds
        
        return new RestTemplate(factory);
    }
}
```

### WebSocket Connection Issues

**Problem**: WebSocket connections drop frequently

**Solution**:
```java
@Component
public class WebSocketConnectionManager {
    
    private final Map<String, WebSocketSession> activeConnections = new ConcurrentHashMap<>();
    
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        activeConnections.remove(sessionId);
        
        log.info("WebSocket session disconnected: {}", sessionId);
        
        // Attempt to reconnect for critical sessions
        if (isCriticalSession(sessionId)) {
            scheduleReconnect(sessionId);
        }
    }
    
    private void scheduleReconnect(String sessionId) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000); // Wait 5 seconds
                attemptReconnect(sessionId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

## Configuration Problems

### Environment Variable Issues

**Problem**: Configuration values not loaded from environment variables

**Solution**:
```java
@Configuration
@ConditionalOnProperty(name = "bpmn.enabled", havingValue = "true")
@EnableConfigurationProperties(BPMNConfigProperties.class)
public class BPMNConfigValidation {
    
    @PostConstruct
    public void validateConfiguration() {
        if (StringUtils.isBlank(bpmnConfig.getBaseUrl())) {
            throw new ConfigurationException("BPMN base URL is required");
        }
        
        if (StringUtils.isBlank(bpmnConfig.getApiKey())) {
            log.warn("BPMN API key is not configured - limited functionality");
        }
    }
}
```

### Database Configuration Issues

**Problem**: Cannot connect to database

**Solution**:
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:file:./data/bpmn-analysis
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
```

## Debugging and Logging

### Enable Debug Logging

```yaml
# logback-spring.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.securityorchestrator.bpmn" level="DEBUG"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <logger name="org.camunda.bpm" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

### Custom Debug Endpoints

```java
@RestController
public class DebugController {
    
    @GetMapping("/debug/bpmn/health")
    public Map<String, Object> getBPMNSystemHealth() {
        return Map.of(
            "status", "UP",
            "bpmnParser", bpmnParser.isHealthy(),
            "aiService", aiService.isHealthy(),
            "database", database.isHealthy(),
            "memoryUsage", getMemoryUsage(),
            "activeAnalyses", analysisService.getActiveAnalysisCount()
        );
    }
    
    @GetMapping("/debug/bpmn/config")
    public Map<String, Object> getConfiguration() {
        return Map.of(
            "baseUrl", bpmnConfig.getBaseUrl(),
            "timeout", bpmnConfig.getTimeout(),
            "maxRetries", bpmnConfig.getMaxRetries(),
            "aiEnabled", bpmnConfig.getAi().isEnabled(),
            "cacheEnabled", bpmnConfig.getCache().isEnabled()
        );
    }
}
```

## Performance Tuning

### Database Optimization

```sql
-- Create indexes for better query performance
CREATE INDEX idx_process_status ON bpmn_processes(status);
CREATE INDEX idx_analysis_type ON security_analyses(analysis_type);
CREATE INDEX idx_analysis_status ON security_analyses(status);
CREATE INDEX idx_finding_severity ON security_findings(severity);
CREATE INDEX idx_process_created ON bpmn_processes(created_at);
```

### JVM Tuning

```bash
# JVM options for BPMN analysis
java -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+UseStringDeduplication \
     -jar bpmn-analysis-service.jar
```

### Application-Level Optimization

```java
@Service
public class OptimizedBPMNAnalysisService {
    
    @Cacheable(value = "bpmn-analysis", key = "#processId + '_' + #analysisType")
    public AnalysisResult analyzeProcess(String processId, AnalysisType analysisType) {
        // Analysis logic with caching
    }
    
    @Async("taskExecutor")
    public CompletableFuture<Void> performBackgroundAnalysis(String processId) {
        return CompletableFuture.runAsync(() -> {
            // Background analysis logic
        });
    }
}
```

## FAQ Section

### Q: Why is my BPMN file not being accepted?

**A**: Common issues include:
- File is not valid BPMN 2.0 XML
- File size exceeds 10MB limit
- Contains unsupported BPMN elements
- Malformed XML structure

**Solution**: Validate your file using a BPMN validator and ensure it complies with BPMN 2.0 standard.

### Q: How long should analysis take?

**A**: Analysis time depends on process complexity:
- Simple processes (< 10 elements): 1-2 minutes
- Medium complexity (10-50 elements): 2-5 minutes
- Complex processes (> 50 elements): 5-15 minutes

For very complex processes, consider using async analysis with WebSocket updates.

### Q: Why is AI analysis failing?

**A**: Common causes:
- Network connectivity issues
- Invalid API keys
- Rate limiting by AI provider
- AI provider service outage

**Solution**: Check network connectivity, verify API keys, and configure fallback providers.

### Q: Can I analyze processes with custom extensions?

**A**: Yes, but with limitations:
- Standard BPMN 2.0 elements are fully supported
- Custom extensions may be ignored or require custom rules
- Consider using extension attributes for custom metadata

### Q: How do I improve analysis performance?

**A**: Optimization strategies:
- Enable caching for repeated analyses
- Use async processing for large processes
- Configure appropriate JVM memory settings
- Optimize database queries and indexes

### Q: What compliance standards are supported?

**A**: Currently supported:
- GDPR (General Data Protection Regulation)
- PCI-DSS (Payment Card Industry Data Security Standard)
- HIPAA (Health Insurance Portability and Accountability Act)
- ISO 27001 (Information Security Management)
- SOX (Sarbanes-Oxley Act)

### Q: How do I handle rate limiting?

**A**: Implement rate limiting strategies:
- Use exponential backoff for retries
- Implement circuit breaker patterns
- Queue requests during high load
- Monitor API usage and adjust accordingly

### Q: Can I customize security rules?

**A**: Yes, through:
- Custom rule development framework
- AI-enhanced rule creation
- Plugin architecture for extensions
- Business-specific rule configurations

For additional support, please refer to the main documentation or contact the development team.