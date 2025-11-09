import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Простой тестовый сервер для проверки LLM интеграции
 * Сосредоточен только на тестировании Ollama API
 */
public class SimpleLLMTestServer {
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Health check endpoint
        server.createContext("/api/health", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "SecurityOrchestrator LLM Test Server is running!";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        
        // LLM status endpoint
        server.createContext("/api/llm/status", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = """
                    {
                        "status": "LLM Integration Ready",
                        "ollama_url": "http://localhost:11434",
                        "model": "codellama:7b-instruct-q4_0",
                        "message": "Ready for CodeLlama testing"
                    }
                    """;
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        
        // LLM test endpoint
        server.createContext("/api/llm/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    // Test direct Ollama API call
                    java.net.URL url = new java.net.URL("http://localhost:11434/api/tags");
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    
                    String ollamaResponse = "";
                    if (conn.getResponseCode() == 200) {
                        java.io.BufferedReader in = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder content = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();
                        ollamaResponse = content.toString();
                    }
                    conn.disconnect();
                    
                    String response = String.format("""
                        {
                            "ollama_status": "connected",
                            "ollama_response": %s,
                            "model_available": true,
                            "test_time": "%s"
                        }
                        """, ollamaResponse, new java.util.Date());
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    
                } catch (Exception e) {
                    String response = String.format("""
                        {
                            "ollama_status": "error",
                            "error": "%s",
                            "test_time": "%s"
                        }
                        """, e.getMessage(), new java.util.Date());
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        });
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("SecurityOrchestrator LLM Test Server started on port 8080");
        System.out.println("Available endpoints:");
        System.out.println("  GET /api/health - Health check");
        System.out.println("  GET /api/llm/status - LLM status");
        System.out.println("  GET /api/llm/test - Test Ollama integration");
        System.out.println();
        System.out.println("Ollama should be running on http://localhost:11434");
        System.out.println("Available models can be checked at http://localhost:11434/api/tags");
    }
}