import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Тест генерации кода через CodeLlama
 * Проверяем способность модели генерировать безопасный код
 */
public class CodeGenerationTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== CodeLlama Code Generation Test ===");
            System.out.println();
            
            // Тестовый промпт для генерации кода
            String prompt = """
                Generate a simple Java method to validate email addresses.
                The method should:
                1. Check if email format is valid
                2. Use regex for validation
                3. Be security-conscious (no XSS vulnerabilities)
                4. Return boolean
                5. Be well-documented
                """;
            
            String generatedCode = generateCodeWithCodeLlama(prompt);
            System.out.println("Generated Code:");
            System.out.println(generatedCode);
            System.out.println();
            
            // Тест 2: Генерация SQL-безопасного кода
            String sqlPrompt = """
                Generate a Java method to safely execute SQL queries.
                The method should:
                1. Use PreparedStatement
                2. Validate input parameters
                3. Handle exceptions properly
                4. Follow OWASP best practices
                """;
            
            String sqlCode = generateCodeWithCodeLlama(sqlPrompt);
            System.out.println("Generated SQL-safe Code:");
            System.out.println(sqlCode);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateCodeWithCodeLlama(String prompt) throws Exception {
        // Создаем JSON запрос для Ollama
        String requestBody = String.format("""
            {
                "model": "codellama:7b-instruct-q4_0",
                "prompt": "%s",
                "stream": false,
                "options": {
                    "temperature": 0.1,
                    "top_k": 40,
                    "top_p": 0.9,
                    "num_predict": 500
                }
            }
            """, prompt.replace("\"", "\\\""));
        
        // Отправляем запрос в Ollama
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Читаем ответ
        if (conn.getResponseCode() == 200) {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                
                // Парсим JSON ответ и извлекаем сгенерированный код
                String jsonResponse = response.toString();
                if (jsonResponse.contains("\"response\":")) {
                    int start = jsonResponse.indexOf("\"response\":\"") + 12;
                    int end = jsonResponse.indexOf("\",\"done\"", start);
                    if (end == -1) {
                        end = jsonResponse.indexOf("\",\"load_end\"", start);
                    }
                    if (end == -1) {
                        end = jsonResponse.indexOf("\",\"model\"", start);
                    }
                    if (end != -1) {
                        return jsonResponse.substring(start, end)
                            .replace("\\n", "\n")
                            .replace("\\\"", "\"")
                            .replace("\\\\", "\\");
                    }
                }
                return "Failed to parse response";
            }
        } else {
            throw new Exception("HTTP " + conn.getResponseCode() + ": " + conn.getResponseMessage());
        }
    }
}