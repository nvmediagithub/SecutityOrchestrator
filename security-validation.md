# Security and Validation Requirements

## Security Principles

The SecurityOrchestrator follows a "secure by default" approach with comprehensive security measures designed for local execution environments where external threats are minimal but data integrity and user safety are paramount.

### Core Security Tenets

1. **Local Execution Only**: No external API calls except for testing target systems
2. **Input Validation**: All inputs are validated and sanitized before processing
3. **File Safety**: Strict validation of uploaded files with content inspection
4. **No External Dependencies**: All processing happens locally without cloud services
5. **Data Isolation**: Test data and results are isolated and not shared externally

## Input Validation and Sanitization

### File Upload Validation

#### BPMN File Validation
```java
@Component
public class BpmnFileValidator implements FileValidator {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("bpmn", "xml");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Pattern XML_PATTERN = Pattern.compile("<\\?xml.*\\?>");

    @Override
    public ValidationResult validate(MultipartFile file) {
        List<ValidationError> errors = new ArrayList<>();

        // File extension check
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            errors.add(new ValidationError("file", "Invalid file extension. Only .bpmn and .xml files are allowed"));
        }

        // File size check
        if (file.getSize() > MAX_FILE_SIZE) {
            errors.add(new ValidationError("file", "File size exceeds maximum limit of 10MB"));
        }

        // Content validation
        if (!isValidXmlContent(file)) {
            errors.add(new ValidationError("file", "File does not contain valid XML content"));
        }

        // BPMN-specific validation
        if (!isValidBpmnStructure(file)) {
            errors.add(new ValidationError("file", "File does not contain valid BPMN 2.0 structure"));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private boolean isValidXmlContent(MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            return XML_PATTERN.matcher(content).find() && isWellFormedXml(content);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidBpmnStructure(MultipartFile file) {
        try {
            BpmnModelInstance model = Bpmn.readModelFromStream(file.getInputStream());
            return model != null && !model.getModelElements().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### OpenAPI Specification Validation
```java
@Component
public class OpenApiFileValidator implements FileValidator {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("json", "yaml", "yml");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Pattern OPENAPI_PATTERN = Pattern.compile("\"?openapi\"?\\s*:\\s*\"?[0-9]+\\.[0-9]+\\.[0-9]+\"?");

    @Override
    public ValidationResult validate(MultipartFile file) {
        List<ValidationError> errors = new ArrayList<>();

        // Basic file validation
        errors.addAll(validateBasicFileProperties(file));

        // Content validation
        if (!isValidOpenApiContent(file)) {
            errors.add(new ValidationError("file", "File does not contain valid OpenAPI specification"));
        }

        // Schema validation
        if (!isValidOpenApiSchema(file)) {
            errors.add(new ValidationError("file", "OpenAPI specification does not conform to schema"));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private List<ValidationError> validateBasicFileProperties(MultipartFile file) {
        List<ValidationError> errors = new ArrayList<>();

        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            errors.add(new ValidationError("file", "Invalid file extension. Only .json, .yaml, and .yml files are allowed"));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            errors.add(new ValidationError("file", "File size exceeds maximum limit of 5MB"));
        }

        return errors;
    }

    private boolean isValidOpenApiContent(MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            return OPENAPI_PATTERN.matcher(content).find();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidOpenApiSchema(MultipartFile file) {
        try {
            OpenAPI openAPI = new OpenAPIV3Parser().readContents(
                IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8)
            ).getOpenAPI();
            return openAPI != null && openAPI.getOpenapi() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### AI Model File Validation
```java
@Component
public class AiModelFileValidator implements FileValidator {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("onnx", "pb", "pt", "h5");
    private static final long MAX_MODEL_SIZE = 500 * 1024 * 1024; // 500MB

    @Override
    public ValidationResult validate(MultipartFile file) {
        List<ValidationError> errors = new ArrayList<>();

        // File extension check
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            errors.add(new ValidationError("file", "Invalid model file extension"));
        }

        // File size check
        if (file.getSize() > MAX_MODEL_SIZE) {
            errors.add(new ValidationError("file", "Model file size exceeds maximum limit of 500MB"));
        }

        // Model format validation
        if (!isValidModelFormat(file, extension)) {
            errors.add(new ValidationError("file", "Invalid or corrupted model file format"));
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private boolean isValidModelFormat(MultipartFile file, String extension) {
        try {
            switch (extension.toLowerCase()) {
                case "onnx":
                    return validateOnnxModel(file);
                case "pb":
                    return validateTensorFlowModel(file);
                case "pt":
                    return validatePyTorchModel(file);
                case "h5":
                    return validateKerasModel(file);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateOnnxModel(MultipartFile file) {
        try {
            OrtSession session = OrtEnvironment.getEnvironment()
                .createSession(file.getBytes());
            session.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Input Sanitization

#### HTTP Request Sanitization
```java
@Component
public class HttpRequestSanitizer {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
    private static final List<String> DANGEROUS_HEADERS = Arrays.asList("host", "authorization", "cookie");

    public HttpRequest sanitize(HttpRequest request) {
        // Sanitize URL
        String sanitizedUrl = sanitizeUrl(request.getUrl());

        // Sanitize headers
        Map<String, String> sanitizedHeaders = sanitizeHeaders(request.getHeaders());

        // Sanitize body if present
        String sanitizedBody = request.getBody() != null ?
            sanitizeBody(request.getBody()) : null;

        return HttpRequest.builder()
            .url(sanitizedUrl)
            .method(request.getMethod())
            .headers(sanitizedHeaders)
            .body(sanitizedBody)
            .build();
    }

    private String sanitizeUrl(String url) {
        try {
            URI uri = new URI(url);
            // Ensure URL is valid and uses allowed protocols
            if (!uri.getScheme().matches("https?")) {
                throw new IllegalArgumentException("Only HTTP/HTTPS URLs are allowed");
            }
            return url;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL format", e);
        }
    }

    private Map<String, String> sanitizeHeaders(Map<String, String> headers) {
        return headers.entrySet().stream()
            .filter(entry -> !DANGEROUS_HEADERS.contains(entry.getKey().toLowerCase()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> sanitizeHeaderValue(entry.getValue())
            ));
    }

    private String sanitizeHeaderValue(String value) {
        // Remove any potentially dangerous characters
        return value.replaceAll("[\\r\\n\\t]", "");
    }

    private String sanitizeBody(String body) {
        // Remove script tags and HTML tags from request body
        String sanitized = SCRIPT_PATTERN.matcher(body).replaceAll("");
        return HTML_PATTERN.matcher(sanitized).replaceAll("");
    }
}
```

## Data Processing Security

### Test Data Isolation
```java
@Component
public class TestDataIsolationService {

    private final Path dataDirectory;

    public TestDataIsolationService(@Value("${app.data.directory}") String dataDirectory) {
        this.dataDirectory = Paths.get(dataDirectory);
    }

    public Path createIsolatedDirectory(String executionId) {
        try {
            Path executionDir = dataDirectory.resolve("executions").resolve(executionId);
            Files.createDirectories(executionDir);

            // Set restrictive permissions
            setRestrictivePermissions(executionDir);

            return executionDir;
        } catch (IOException e) {
            throw new DataIsolationException("Failed to create isolated directory", e);
        }
    }

    public void cleanupExecutionData(String executionId) {
        try {
            Path executionDir = dataDirectory.resolve("executions").resolve(executionId);
            if (Files.exists(executionDir)) {
                // Secure deletion - overwrite files before deletion
                secureDelete(executionDir);
            }
        } catch (IOException e) {
            // Log error but don't throw - cleanup failures shouldn't break execution
            logger.warn("Failed to cleanup execution data for: {}", executionId, e);
        }
    }

    private void setRestrictivePermissions(Path path) throws IOException {
        // Set permissions to owner read/write only
        Files.setPosixFilePermissions(path,
            PosixFilePermissions.fromString("rwx------"));
    }

    private void secureDelete(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> paths = Files.walk(path)) {
                paths.sorted(Comparator.reverseOrder())
                    .forEach(this::secureDeleteFile);
            }
        } else {
            secureDeleteFile(path);
        }
    }

    private void secureDeleteFile(Path file) {
        try {
            if (Files.exists(file)) {
                // Overwrite file content before deletion
                overwriteFile(file);
                Files.delete(file);
            }
        } catch (IOException e) {
            logger.warn("Failed to securely delete file: {}", file, e);
        }
    }

    private void overwriteFile(Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.WRITE)) {
            long size = channel.size();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);
            for (int i = 0; i < buffer.capacity(); i++) {
                buffer.put(i, (byte) 0);
            }
            buffer.force();
        }
    }
}
```

### Execution Sandboxing
```java
@Component
public class ExecutionSandbox {

    private final TestDataIsolationService isolationService;
    private final ResourceLimiter resourceLimiter;

    public <T> T executeInSandbox(String executionId, Supplier<T> operation) {
        Path sandboxDir = isolationService.createIsolatedDirectory(executionId);

        try {
            return resourceLimiter.limit(() -> {
                // Set working directory to sandbox
                String originalDir = System.getProperty("user.dir");
                System.setProperty("user.dir", sandboxDir.toString());

                try {
                    // Execute operation in sandbox
                    return operation.get();
                } finally {
                    // Restore original working directory
                    System.setProperty("user.dir", originalDir);
                }
            });
        } finally {
            // Schedule cleanup
            cleanupSandbox(executionId);
        }
    }

    @Async
    public void cleanupSandbox(String executionId) {
        try {
            Thread.sleep(300000); // Wait 5 minutes before cleanup
            isolationService.cleanupExecutionData(executionId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

@Component
public class ResourceLimiter {

    private static final long MAX_HEAP_USAGE = 512 * 1024 * 1024; // 512MB
    private static final long MAX_EXECUTION_TIME = 300000; // 5 minutes

    public <T> T limit(Supplier<T> operation) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long startTime = System.currentTimeMillis();
        long threadId = Thread.currentThread().getId();

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Monitor resource usage
                monitorResources(threadId, startTime);

                T result = operation.get();

                // Check final resource usage
                validateResourceUsage(threadId, startTime);

                return result;
            } catch (Exception e) {
                throw new ExecutionException("Operation failed", e);
            }
        }).get(MAX_EXECUTION_TIME, TimeUnit.MILLISECONDS);
    }

    private void monitorResources(long threadId, long startTime) {
        // Implementation for resource monitoring
    }

    private void validateResourceUsage(long threadId, long startTime) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long usedHeap = memoryMXBean.getHeapMemoryUsage().getUsed();

        if (usedHeap > MAX_HEAP_USAGE) {
            throw new ResourceLimitExceededException("Heap usage exceeded limit");
        }

        long executionTime = System.currentTimeMillis() - startTime;
        if (executionTime > MAX_EXECUTION_TIME) {
            throw new ResourceLimitExceededException("Execution time exceeded limit");
        }
    }
}
```

## Network Security

### HTTP Client Security Configuration
```java
@Configuration
public class HttpClientSecurityConfiguration {

    @Bean
    public HttpClient secureHttpClient() {
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .sslContext(createSecureSslContext())
            .build();
    }

    private SSLContext createSecureSslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");

            // Use default trust manager for target system testing
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(null, null);

            sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                new SecureRandom()
            );

            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create secure SSL context", e);
        }
    }
}
```

### Request Rate Limiting
```java
@Component
public class RequestRateLimiter {

    private final LoadingCache<String, AtomicInteger> requestCounts;
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    public RequestRateLimiter() {
        this.requestCounts = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(key -> new AtomicInteger(0));
    }

    public boolean allowRequest(String clientId) {
        AtomicInteger count = requestCounts.get(clientId);
        int currentCount = count.incrementAndGet();

        return currentCount <= MAX_REQUESTS_PER_MINUTE;
    }

    public void recordRequest(String clientId) {
        // Additional tracking if needed
    }
}
```

## Security Monitoring and Auditing

### Security Event Logging
```java
@Component
public class SecurityEventLogger {

    private static final Logger logger = LoggerFactory.getLogger("SECURITY_AUDIT");

    @EventListener
    public void logFileUpload(FileUploadEvent event) {
        logger.info("FILE_UPLOAD | user: {} | filename: {} | size: {} | hash: {}",
            event.getUserId(),
            event.getFilename(),
            event.getFileSize(),
            calculateFileHash(event.getFile())
        );
    }

    @EventListener
    public void logExecutionStart(ExecutionStartedEvent event) {
        logger.info("EXECUTION_START | executionId: {} | type: {} | user: {}",
            event.getExecutionId(),
            event.getExecutionType(),
            event.getUserId()
        );
    }

    @EventListener
    public void logSecurityViolation(SecurityViolationEvent event) {
        logger.warn("SECURITY_VIOLATION | type: {} | details: {} | user: {} | ip: {}",
            event.getViolationType(),
            event.getDetails(),
            event.getUserId(),
            event.getClientIp()
        );
    }

    private String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(
                digest.digest(file.getBytes())
            );
        } catch (Exception e) {
            return "HASH_CALCULATION_FAILED";
        }
    }
}
```

### Integrity Checks
```java
@Component
public class DataIntegrityChecker {

    private final Path dataDirectory;

    @Scheduled(fixedDelay = 3600000) // Run every hour
    public void checkDataIntegrity() {
        try {
            checkFileIntegrity();
            checkDatabaseIntegrity();
        } catch (Exception e) {
            logger.error("Data integrity check failed", e);
        }
    }

    private void checkFileIntegrity() throws IOException {
        try (Stream<Path> files = Files.walk(dataDirectory)) {
            files.filter(Files::isRegularFile)
                .forEach(this::verifyFileIntegrity);
        }
    }

    private void verifyFileIntegrity(Path file) {
        try {
            // Check file permissions
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file);
            if (permissions.contains(PosixFilePermission.OTHERS_READ) ||
                permissions.contains(PosixFilePermission.OTHERS_WRITE)) {
                logger.warn("Insecure file permissions detected: {}", file);
            }

            // Check file size hasn't changed unexpectedly
            long size = Files.size(file);
            Long expectedSize = getExpectedSize(file);
            if (expectedSize != null && size != expectedSize) {
                logger.warn("File size mismatch for: {} (expected: {}, actual: {})",
                    file, expectedSize, size);
            }

        } catch (IOException e) {
            logger.error("Failed to verify file integrity: {}", file, e);
        }
    }

    private void checkDatabaseIntegrity() {
        // Implementation for database integrity checks
    }
}
```

## Security Headers and CORS

### Web Security Configuration
```java
@Configuration
public class WebSecurityConfiguration {

    @Bean
    public FilterRegistrationBean<HeaderFilter> headerFilter() {
        FilterRegistrationBean<HeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HeaderFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }

    public static class HeaderFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Security headers
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            httpResponse.setHeader("Content-Security-Policy", "default-src 'self'");

            // Prevent caching of sensitive data
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");

            chain.doFilter(request, response);
        }
    }
}
```

## Compliance and Best Practices

### OWASP Compliance
- **Input Validation**: All inputs are validated against strict schemas
- **Output Encoding**: Responses are properly encoded to prevent injection
- **Authentication**: Local application with no authentication requirements
- **Session Management**: Not applicable for local application
- **Access Control**: File system permissions and data isolation
- **Cryptographic Practices**: Secure random generation and hashing
- **Error Handling**: Generic error messages to prevent information disclosure
- **Logging**: Comprehensive security event logging

### Data Protection
- **Encryption at Rest**: Sensitive data encrypted using local keys
- **Secure Deletion**: Files overwritten before deletion
- **Access Logging**: All file and data access logged with timestamps
- **Backup Security**: Encrypted backups with integrity verification

This comprehensive security framework ensures the SecurityOrchestrator operates safely in local environments while providing robust protection against potential threats and data integrity issues.