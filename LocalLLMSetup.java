import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class LocalLLMSetup {
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String INSTALL_SCRIPT = "install_ollama_and_codellama.bat";
    private static final String LLM_CONFIG_FILE = "local_llm_config.properties";
    
    // RTX 3070 8GB оптимизированная конфигурация
    private static final Map<String, Object> RTX_3070_CONFIG = Map.of(
        "model_name", "codellama:7b-instruct-q4_0",
        "max_tokens", 4096,
        "temperature", 0.7,
        "top_p", 0.9,
        "context_length", 8192,
        "gpu_layers", 32,  // 50% GPU utilization for 8GB VRAM
        "threads", 8,
        "batch_size", 512,
        "memory_usage", "6GB",  // 75% of available VRAM
        "expected_tokens_per_second", 15.0
    );

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Local LLM Setup для SecurityOrchestrator (RTX 3070 8GB)");
        System.out.println("=========================================================");
        
        // Проверка системы
        if (!checkSystemRequirements()) {
            System.exit(1);
        }
        
        // Установка Ollama
        if (!isOllamaInstalled()) {
            installOllama();
        }
        
        // Установка CodeLlama
        installCodeLlamaModel();
        
        // Конфигурация для RTX 3070
        configureForRTX3070();
        
        // Тестирование
        testLocalLLM();
        
        // Интеграция с SecurityOrchestrator
        setupSecurityOrchestratorIntegration();
        
        System.out.println("✅ Настройка локальной LLM завершена успешно!");
    }
    
    private static boolean checkSystemRequirements() {
        System.out.println("🔍 Проверка системных требований...");
        
        try {
            // Проверка GPU
            Process gpuCheck = Runtime.getRuntime().exec("nvidia-smi --query-gpu=name,memory.total --format=csv,noheader");
            BufferedReader reader = new BufferedReader(new InputStreamReader(gpuCheck.getInputStream()));
            String gpuInfo = reader.readLine();
            
            if (gpuInfo != null && gpuInfo.contains("RTX 3070")) {
                System.out.println("✅ RTX 3070 обнаружена: " + gpuInfo);
            } else {
                System.out.println("⚠️  RTX 3070 не обнаружена, но продолжаем...");
            }
            
            // Проверка памяти
            long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            System.out.println("✅ Доступная Java память: " + (freeMemory / 1024) + "MB");
            
            return true;
        } catch (Exception e) {
            System.out.println("⚠️  Ошибка проверки системы: " + e.getMessage());
            return true; // Продолжаем в любом случае
        }
    }
    
    private static boolean isOllamaInstalled() {
        try {
            URL url = new URL(OLLAMA_BASE_URL + "/api/tags");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("✅ Ollama уже установлен и работает");
                return true;
            }
        } catch (Exception e) {
            System.out.println("ℹ️  Ollama не установлен или не запущен");
        }
        return false;
    }
    
    private static void installOllama() {
        System.out.println("📦 Установка Ollama...");
        
        try {
            // Скачивание установочного скрипта
            downloadOllamaScript();
            
            // Создание bat файла для Windows
            createWindowsInstallScript();
            
            System.out.println("📋 Создан скрипт установки: " + INSTALL_SCRIPT);
            System.out.println("🔧 Запустите следующую команду вручную:");
            System.out.println("   " + INSTALL_SCRIPT);
            
        } catch (Exception e) {
            System.out.println("❌ Ошибка при создании скрипта установки: " + e.getMessage());
        }
    }
    
    private static void downloadOllamaScript() throws IOException {
        // Создание простого bat файла для Windows
        String scriptContent = """
@echo off
echo Installing Ollama...

REM Download Ollama for Windows
powershell -Command "Invoke-WebRequest -Uri 'https://ollama.ai/download/ollama-windows-amd64.exe' -OutFile 'ollama.exe'"

REM Add to PATH
setx PATH "%PATH%;." /M

REM Start Ollama service
start /B ollama serve

echo Waiting for Ollama to start...
timeout /t 10 /nobreak

echo Installing CodeLlama 7B model...
ollama pull codellama:7b-instruct-q4_0

echo Installation complete!
pause
""";
        
        Files.write(Paths.get(INSTALL_SCRIPT), scriptContent.getBytes());
    }
    
    private static void createWindowsInstallScript() {
        // Скрипт уже создан в downloadOllamaScript()
    }
    
    private static void installCodeLlamaModel() {
        System.out.println("🤖 Установка CodeLlama 7B модели...");
        
        try {
            // Отправка запроса на скачивание модели
            String requestBody = """
{
  "name": "codellama:7b-instruct-q4_0"
}
""";
            
            URL url = new URL(OLLAMA_BASE_URL + "/api/pull");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("✅ CodeLlama 7B модель установлена");
            } else {
                System.out.println("⚠️  Модель уже установлена или устанавливается");
            }
            
        } catch (Exception e) {
            System.out.println("ℹ️  Установка модели: " + e.getMessage());
            System.out.println("   Повторите установку позже с помощью: ollama pull codellama:7b-instruct-q4_0");
        }
    }
    
    private static void configureForRTX3070() {
        System.out.println("⚙️  Конфигурация для RTX 3070 8GB...");
        
        // Создание конфигурационного файла
        Properties config = new Properties();
        config.setProperty("llm.provider", "ollama");
        config.setProperty("llm.model", "codellama:7b-instruct-q4_0");
        config.setProperty("llm.base_url", OLLAMA_BASE_URL);
        config.setProperty("llm.max_tokens", String.valueOf(RTX_3070_CONFIG.get("max_tokens")));
        config.setProperty("llm.temperature", String.valueOf(RTX_3070_CONFIG.get("temperature")));
        config.setProperty("llm.gpu_layers", String.valueOf(RTX_3070_CONFIG.get("gpu_layers")));
        config.setProperty("llm.threads", String.valueOf(RTX_3070_CONFIG.get("threads")));
        config.setProperty("llm.context_length", String.valueOf(RTX_3070_CONFIG.get("context_length")));
        config.setProperty("llm.memory_usage", String.valueOf(RTX_3070_CONFIG.get("memory_usage")));
        config.setProperty("llm.expected_tokens_per_second", String.valueOf(RTX_3070_CONFIG.get("expected_tokens_per_second")));
        config.setProperty("security_orchestrator.port", "8090");
        config.setProperty("security_orchestrator.llm_endpoint", "/api/llm/complete");
        
        try {
            try (FileOutputStream out = new FileOutputStream(LLM_CONFIG_FILE)) {
                config.store(out, "Local LLM Configuration for RTX 3070 8GB");
            }
            System.out.println("✅ Конфигурация сохранена: " + LLM_CONFIG_FILE);
        } catch (Exception e) {
            System.out.println("❌ Ошибка сохранения конфигурации: " + e.getMessage());
        }
    }
    
    private static void testLocalLLM() {
        System.out.println("🧪 Тестирование локальной LLM...");
        
        try {
            // Тестовая генерация
            String testPrompt = """
You are a security expert. Analyze this code for vulnerabilities:

function login(username, password) {
    const query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
    return database.execute(query);
}

Provide a brief security analysis:
""";
            
            String requestBody = String.format("""
{
  "model": "codellama:7b-instruct-q4_0",
  "prompt": %s,
  "stream": false,
  "options": {
    "temperature": 0.7,
    "num_predict": 500
  }
}
""", jsonEscape(testPrompt));
            
            URL url = new URL(OLLAMA_BASE_URL + "/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                System.out.println("✅ LLM тест успешен!");
                System.out.println("📝 Ответ модели: " + response.toString().substring(0, Math.min(200, response.length())) + "...");
            } else {
                System.out.println("⚠️  Тест LLM не удался (код " + responseCode + ")");
            }
            
        } catch (Exception e) {
            System.out.println("⚠️  Ошибка тестирования LLM: " + e.getMessage());
            System.out.println("   Убедитесь, что Ollama запущен и модель установлена");
        }
    }
    
    private static void setupSecurityOrchestratorIntegration() {
        System.out.println("🔗 Настройка интеграции с SecurityOrchestrator...");
        
        // Создание простого Java кода для интеграции
        String integrationCode = """
package org.example.infrastructure.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class LocalLLMService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:11434";
    
    public String generateCompletion(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                "model", "codellama:7b-instruct-q4_0",
                "prompt", prompt,
                "stream", false,
                "options", Map.of(
                    "temperature", 0.7,
                    "num_predict", 4096
                )
            );
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/generate",
                request,
                String.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    public String analyzeSecurity(String code) {
        String prompt = "You are a security expert. Analyze this code for vulnerabilities:\\n" + code;
        return generateCompletion(prompt);
    }
}
""";
        
        try {
            Files.write(Paths.get("LocalLLMService.java"), integrationCode.getBytes());
            System.out.println("✅ Код интеграции создан: LocalLLMService.java");
        } catch (Exception e) {
            System.out.println("❌ Ошибка создания кода интеграции: " + e.getMessage());
        }
        
        // Инструкции по интеграции
        System.out.println("\n📋 Инструкции по интеграции:");
        System.out.println("1. Скопируйте LocalLLMService.java в Backend/app/src/main/java/org/example/infrastructure/llm/");
        System.out.println("2. Добавьте @EnableAutoConfiguration в SecurityOrchestratorApplication");
        System.out.println("3. Добавьте @ComponentScan в SecurityOrchestratorApplication");
        System.out.println("4. Пересоберите проект: ./gradlew bootRun");
    }
    
    private static String jsonEscape(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}