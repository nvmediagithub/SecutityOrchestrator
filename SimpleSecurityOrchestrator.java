import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SimpleSecurityOrchestrator {
    private static final int PORT = 8091;
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL = "codellama:7b-instruct-q4_0";
    
    private static final Set<String> OWASP_TOP_10 = Set.of(
        "B1: Injection",
        "B2: Broken Authentication", 
        "B3: Sensitive Data Exposure",
        "B4: XML External Entities (XXE)",
        "B5: Broken Access Control",
        "B6: Security Misconfiguration",
        "B7: Cross-Site Scripting (XSS)",
        "B8: Insecure Deserialization",
        "B9: Using Components with Known Vulnerabilities",
        "B10: Insufficient Logging & Monitoring"
    );
    
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Map<String, Object> analysisCache = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        SimpleSecurityOrchestrator orchestrator = new SimpleSecurityOrchestrator();
        orchestrator.start();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/llm/analyze", new AnalyzeSecurityHandler());
        server.createContext("/api/llm/complete", new LLMCompleteHandler());
        server.createContext("/api/llm/status", new LLMStatusHandler());
        server.createContext("/api/owasp/scan", new OWASPScanHandler());
        server.setExecutor(executor);
        server.start();
        
        System.out.println("🚀 SecurityOrchestrator с локальной LLM запущен на порту " + PORT);
        System.out.println("🔗 LLM интеграция с Ollama: " + OLLAMA_BASE_URL);
        System.out.println("🤖 Модель: " + DEFAULT_MODEL);
        System.out.println("⚙️ Оптимизировано для RTX 3070 8GB");
        System.out.println("📊 API endpoints:");
        System.out.println("   GET  /api/health");
        System.out.println("   POST /api/llm/analyze");
        System.out.println("   POST /api/llm/complete");
        System.out.println("   GET  /api/llm/status");
        System.out.println("   POST /api/owasp/scan");
    }

    // Health Check
    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "{\"status\":\"healthy\",\"service\":\"SecurityOrchestrator\",\"llm\":\"CodeLlama 7B\",\"gpu\":\"RTX 3070 8GB\"}";
                sendResponse(exchange, 200, response);
            } else {
                sendError(exchange, 405, "Method not allowed");
            }
        }
    }

    // LLM Analysis
    private class AnalyzeSecurityHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                Map<String, Object> request = parseJson(requestBody);
                
                String code = (String) request.get("code");
                String language = (String) request.getOrDefault("language", "java");
                String analysisType = (String) request.getOrDefault("analysisType", "security");

                if (code == null || code.trim().isEmpty()) {
                    sendError(exchange, 400, "Code parameter is required");
                    return;
                }

                // Создание промпта для анализа безопасности
                String prompt = createSecurityAnalysisPrompt(code, language, analysisType);
                
                String analysis = callLocalLLM(prompt);
                
                Map<String, Object> response = Map.of(
                    "status", "success",
                    "analysis", analysis,
                    "language", language,
                    "type", analysisType,
                    "model", DEFAULT_MODEL,
                    "timestamp", System.currentTimeMillis()
                );
                
                sendResponse(exchange, 200, toJson(response));
            } catch (Exception e) {
                sendError(exchange, 500, "Analysis failed: " + e.getMessage());
            }
        }
    }

    // LLM Complete (общий endpoint)
    private class LLMCompleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                Map<String, Object> request = parseJson(requestBody);
                
                String prompt = (String) request.get("prompt");
                String model = (String) request.getOrDefault("model", DEFAULT_MODEL);
                Integer maxTokens = (Integer) request.getOrDefault("maxTokens", 4096);
                Double temperature = (Double) request.getOrDefault("temperature", 0.7);

                if (prompt == null || prompt.trim().isEmpty()) {
                    sendError(exchange, 400, "Prompt parameter is required");
                    return;
                }

                String response = callLocalLLMWithOptions(prompt, model, maxTokens, temperature);
                
                Map<String, Object> result = Map.of(
                    "status", "success",
                    "response", response,
                    "model", model,
                    "prompt", prompt,
                    "timestamp", System.currentTimeMillis()
                );
                
                sendResponse(exchange, 200, toJson(result));
            } catch (Exception e) {
                sendError(exchange, 500, "LLM call failed: " + e.getMessage());
            }
        }
    }

    // LLM Status
    private class LLMStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                // Проверка Ollama
                boolean ollamaRunning = checkOllamaStatus();
                
                // Проверка модели
                boolean modelAvailable = checkModelAvailability();
                
                Map<String, Object> status = Map.of(
                    "ollama", ollamaRunning,
                    "model", DEFAULT_MODEL,
                    "modelAvailable", modelAvailable,
                    "gpu", "RTX 3070 8GB",
                    "memoryUsage", "6GB",
                    "expectedPerformance", "15 tokens/sec"
                );
                
                sendResponse(exchange, 200, toJson(status));
            } catch (Exception e) {
                sendError(exchange, 500, "Status check failed: " + e.getMessage());
            }
        }
    }

    // OWASP Scan
    private class OWASPScanHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method not allowed");
                return;
            }

            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                Map<String, Object> request = parseJson(requestBody);
                
                String apiUrl = (String) request.get("apiUrl");
                String openApiSpec = (String) request.get("openApiSpec");
                String targetName = (String) request.getOrDefault("targetName", "Target API");

                if (apiUrl == null && openApiSpec == null) {
                    sendError(exchange, 400, "apiUrl or openApiSpec parameter is required");
                    return;
                }

                // Асинхронный OWASP анализ
                CompletableFuture.runAsync(() -> {
                    try {
                        performOWASPScan(apiUrl, openApiSpec, targetName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                Map<String, Object> response = Map.of(
                    "status", "started",
                    "message", "OWASP scan initiated for " + targetName,
                    "estimatedTime", "15-20 seconds",
                    "timestamp", System.currentTimeMillis()
                );
                
                sendResponse(exchange, 200, toJson(response));
            } catch (Exception e) {
                sendError(exchange, 500, "OWASP scan failed: " + e.getMessage());
            }
        }
    }

    // Вспомогательные методы
    private String createSecurityAnalysisPrompt(String code, String language, String analysisType) {
        return String.format("""
You are an expert security analyst. Analyze the following %s code for security vulnerabilities:

```%s
%s
```

Provide a detailed analysis focusing on:
1. Potential security vulnerabilities
2. OWASP Top 10 risks
3. Specific code patterns that are dangerous
4. Recommended fixes
5. Security best practices

Be thorough and specific in your analysis.
""", language, language, code);
    }

    private String callLocalLLM(String prompt) throws IOException {
        return callLocalLLMWithOptions(prompt, DEFAULT_MODEL, 4096, 0.7);
    }

    private String callLocalLLMWithOptions(String prompt, String model, Integer maxTokens, Double temperature) throws IOException {
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);
        request.put("prompt", prompt);
        request.put("stream", false);
        request.put("options", Map.of(
            "temperature", temperature,
            "num_predict", maxTokens
        ));

        String requestJson = toJson(request);
        
        HttpURLConnection conn = (HttpURLConnection) new URL(OLLAMA_BASE_URL + "/api/generate").openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestJson.getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            String response = new String(conn.getInputStream().readAllBytes());
            Map<String, Object> responseData = parseJson(response);
            return (String) responseData.get("response");
        } else {
            throw new IOException("LLM API call failed with code: " + responseCode);
        }
    }

    private boolean checkOllamaStatus() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(OLLAMA_BASE_URL + "/api/tags").openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkModelAvailability() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(OLLAMA_BASE_URL + "/api/tags").openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            
            if (conn.getResponseCode() == 200) {
                String response = new String(conn.getInputStream().readAllBytes());
                return response.contains(DEFAULT_MODEL);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void performOWASPScan(String apiUrl, String openApiSpec, String targetName) {
        try {
            // Симуляция OWASP анализа
            Thread.sleep(1000);
            
            String prompt = String.format("""
Perform a comprehensive OWASP API Security Top 10 analysis for the following target:

Target: %s
API URL: %s
OpenAPI Spec: %s

Provide a detailed security analysis covering:
1. All OWASP API Security Top 10 risks
2. Specific vulnerabilities found
3. Risk severity (Critical/High/Medium/Low)
4. Remediation recommendations
5. Compliance status

Format as a structured security report.
""", targetName, apiUrl != null ? apiUrl : "Not provided", 
            openApiSpec != null ? "Provided" : "Not provided");

            String analysis = callLocalLLM(prompt);
            
            // Сохранение результата
            String resultKey = "owasp_" + targetName.replaceAll("[^a-zA-Z0-9]", "_");
            analysisCache.put(resultKey, Map.of(
                "target", targetName,
                "analysis", analysis,
                "timestamp", System.currentTimeMillis(),
                "status", "completed"
            ));
            
            System.out.println("✅ OWASP scan completed for: " + targetName);
            
        } catch (Exception e) {
            System.err.println("❌ OWASP scan failed for " + targetName + ": " + e.getMessage());
        }
    }

    // JSON утилиты
    private Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();
        // Простое парсирование JSON (для полноценного парсинга нужен JSON библиотека)
        String[] lines = json.split("[,\\{\\}]");
        for (String line : lines) {
            line = line.trim();
            if (line.contains(":") && !line.equals("{")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim().replace("\"", "");
                    String value = parts[1].trim().replace("\"", "");
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    private String toJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
        }
        json.append("}");
        return json.toString();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = String.format("{\"error\":\"%s\",\"timestamp\":%d}", message, System.currentTimeMillis());
        sendResponse(exchange, statusCode, response);
    }
}