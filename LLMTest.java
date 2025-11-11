import java.io.*;
import java.net.*;
import java.nio.file.*;

public class LLMTest {
    private static final String BASE_URL = "http://localhost:8090";
    private static final String OLLAMA_URL = "http://localhost:11434";
    
    public static void main(String[] args) throws Exception {
        System.out.println("🧪 Тестирование интеграции SecurityOrchestrator с локальной LLM");
        System.out.println("============================================================");
        
        // Тест 1: Health check
        testHealthCheck();
        
        // Тест 2: LLM статус
        testLLMStatus();
        
        // Тест 3: Прямое подключение к Ollama
        testOllamaDirect();
        
        // Тест 4: Генерация текста через SecurityOrchestrator
        testTextGeneration();
        
        // Тест 5: Анализ безопасности кода
        testSecurityAnalysis();
        
        System.out.println("\n✅ Тестирование завершено!");
    }
    
    private static void testHealthCheck() {
        System.out.println("\n1. 🏥 Тест Health Check");
        try {
            String response = makeGetRequest(BASE_URL + "/api/health");
            System.out.println("   ✅ Успех: " + response);
        } catch (Exception e) {
            System.out.println("   ❌ Ошибка: " + e.getMessage());
        }
    }
    
    private static void testLLMStatus() {
        System.out.println("\n2. 🤖 Тест LLM Status");
        try {
            String response = makeGetRequest(BASE_URL + "/api/llm/status");
            System.out.println("   ✅ Статус LLM: " + response);
        } catch (Exception e) {
            System.out.println("   ❌ Ошибка: " + e.getMessage());
        }
    }
    
    private static void testOllamaDirect() {
        System.out.println("\n3. 🔗 Прямое подключение к Ollama");
        try {
            String response = makeGetRequest(OLLAMA_URL + "/api/tags");
            if (response.contains("codellama")) {
                System.out.println("   ✅ CodeLlama 7B модель обнаружена");
            } else {
                System.out.println("   ⚠️  Модель не найдена: " + response);
            }
        } catch (Exception e) {
            System.out.println("   ❌ Ошибка: " + e.getMessage());
        }
    }
    
    private static void testTextGeneration() {
        System.out.println("\n4. 📝 Тест генерации текста");
        try {
            String requestBody = """
            {
                "prompt": "What is a SQL injection attack?",
                "model": "codellama:7b-instruct-q4_0",
                "maxTokens": 200,
                "temperature": 0.7
            }
            """;
            
            String response = makePostRequest(BASE_URL + "/api/llm/complete", requestBody);
            System.out.println("   ✅ Генерация: " + response.substring(0, Math.min(200, response.length())) + "...");
        } catch (Exception e) {
            System.out.println("   ❌ Ошибка: " + e.getMessage());
        }
    }
    
    private static void testSecurityAnalysis() {
        System.out.println("\n5. 🔒 Тест анализа безопасности");
        try {
            String code = """
            String query = "SELECT * FROM users WHERE id = " + userId;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            """;
            
            String requestBody = String.format("""
            {
                "code": %s,
                "language": "java",
                "analysisType": "security"
            }
            """, jsonEscape(code));
            
            String response = makePostRequest(BASE_URL + "/api/llm/analyze", requestBody);
            System.out.println("   ✅ Анализ безопасности: " + response.substring(0, Math.min(300, response.length())) + "...");
        } catch (Exception e) {
            System.out.println("   ❌ Ошибка: " + e.getMessage());
        }
    }
    
    private static String makeGetRequest(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            return readResponse(conn.getInputStream());
        } else {
            throw new Exception("HTTP " + responseCode);
        }
    }
    
    private static String makePostRequest(String url, String requestBody) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            return readResponse(conn.getInputStream());
        } else {
            throw new Exception("HTTP " + responseCode);
        }
    }
    
    private static String readResponse(InputStream inputStream) throws Exception {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
    
    private static String jsonEscape(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}