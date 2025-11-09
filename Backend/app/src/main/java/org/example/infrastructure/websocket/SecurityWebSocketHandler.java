package org.example.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.domain.entities.SecurityAnalysisResult;
import org.example.domain.entities.SecurityTest;
import org.example.domain.entities.BpmnProcessStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * WebSocket handler for real-time security analysis updates
 */
@Component
public class SecurityWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = Logger.getLogger(SecurityWebSocketHandler.class.getName());
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("New WebSocket connection established: " + session.getId());
        
        // Send initial connection confirmation
        sendMessageToSession(session, createMessage("connection", "connected", 
            createPayload("sessionId", session.getId(), "timestamp", System.currentTimeMillis())));
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("WebSocket connection closed: " + session.getId());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            // Handle incoming messages from clients
            String payload = message.getPayload();
            logger.info("Received message: " + payload);
            
            // Echo back for testing
            sendMessageToSession(session, createMessage("echo", "received", 
                createPayload("originalMessage", payload)));
                
        } catch (Exception e) {
            logger.severe("Error handling WebSocket message: " + e.getMessage());
            sendErrorMessage(session, "Error processing message: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast security vulnerability update
     */
    public void broadcastVulnerabilityUpdate(SecurityAnalysisResult vulnerability) {
        try {
            Map<String, Object> vulnMap = createPayload(
                "id", vulnerability.getId(),
                "name", vulnerability.getDescription().split("\\n")[0],
                "severity", vulnerability.getSeverity(),
                "category", vulnerability.getOwaspCategory() != null ? 
                    vulnerability.getOwaspCategory().name() : "UNKNOWN",
                "description", vulnerability.getDescription(),
                "endpoint", vulnerability.getApiEndpoint(),
                "confidence", vulnerability.getConfidenceScore(),
                "timestamp", vulnerability.getAnalyzedAt().toString()
            );
            
            Map<String, Object> payload = createPayload("vulnerability", vulnMap);
            
            broadcastMessage("vulnerability", payload);
            
        } catch (Exception e) {
            logger.severe("Error broadcasting vulnerability update: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast test result update
     */
    public void broadcastTestResult(SecurityTest test) {
        try {
            Map<String, Object> testMap = createPayload(
                "id", test.getId(),
                "testName", test.getName(),
                "passed", test.isPassed(),
                "executionTime", test.getSeverityScore(), // Using as execution time
                "payload", test.getPayload(),
                "category", test.getCategory(),
                "timestamp", System.currentTimeMillis()
            );
            
            Map<String, Object> payload = createPayload("test", testMap);
            
            broadcastMessage("test_result", payload);
            
        } catch (Exception e) {
            logger.severe("Error broadcasting test result: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast BPMN process step update
     */
    public void broadcastBpmnStep(BpmnProcessStep step) {
        try {
            Map<String, Object> stepMap = createPayload(
                "id", step.getId(),
                "name", step.getName(),
                "status", step.getStatus(),
                "executionTime", step.getExecutionTime(),
                "timestamp", step.getTimestamp().toString()
            );
            
            Map<String, Object> payload = createPayload("step", stepMap);
            
            broadcastMessage("bpmn_step", payload);
            
        } catch (Exception e) {
            logger.severe("Error broadcasting BPMN step: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast LLM analysis update
     */
    public void broadcastLLMAnalysis(String analysisId, Map<String, Object> results) {
        try {
            Map<String, Object> analysisMap = createPayload(
                "id", analysisId,
                "results", results,
                "timestamp", System.currentTimeMillis()
            );
            
            Map<String, Object> payload = createPayload("analysis", analysisMap);
            
            broadcastMessage("llm_analysis", payload);
            
        } catch (Exception e) {
            logger.severe("Error broadcasting LLM analysis: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast system status update
     */
    public void broadcastSystemStatus(String status, Map<String, Object> details) {
        try {
            Map<String, Object> statusMap = createPayload(
                "system", status,
                "details", details,
                "timestamp", System.currentTimeMillis()
            );
            
            Map<String, Object> payload = createPayload("status", statusMap);
            
            broadcastMessage("system_status", payload);
            
        } catch (Exception e) {
            logger.severe("Error broadcasting system status: " + e.getMessage());
        }
    }
    
    /**
     * Send progress update for ongoing analysis
     */
    public void broadcastProgressUpdate(String analysisId, int progress, String currentStep) {
        try {
            Map<String, Object> progressMap = createPayload(
                "analysisId", analysisId,
                "progress", progress,
                "currentStep", currentStep,
                "timestamp", System.currentTimeMillis()
            );
            
            Map<String, Object> payload = createPayload("progress", progressMap);
            
            broadcastMessage("progress_update", payload);
            
        } catch (Exception e) {
            logger.severe("Error broadcasting progress update: " + e.getMessage());
        }
    }
    
    private void broadcastMessage(String type, Map<String, Object> payload) {
        String message = createMessage(type, "update", payload);
        
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    sendMessageToSession(session, message);
                } catch (Exception e) {
                    logger.warning("Error sending message to session " + session.getId() + ": " + e.getMessage());
                }
            }
        }
    }
    
    private void sendMessageToSession(WebSocketSession session, String message) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
    
    private void sendErrorMessage(WebSocketSession session, String error) {
        try {
            String message = createMessage("error", "error", createPayload("message", error));
            sendMessageToSession(session, message);
        } catch (Exception e) {
            logger.severe("Error sending error message: " + e.getMessage());
        }
    }
    
    private String createMessage(String type, String action, Map<String, Object> payload) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", type);
            message.put("action", action);
            message.put("payload", payload);
            message.put("timestamp", System.currentTimeMillis());
            
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.severe("Error creating message: " + e.getMessage());
            return "{\"type\":\"error\",\"message\":\"Failed to create message\"}";
        }
    }
    
    private Map<String, Object> createPayload(Object... keyValuePairs) {
        Map<String, Object> payload = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            payload.put(keyValuePairs[i].toString(), keyValuePairs[i + 1]);
        }
        return payload;
    }
    
    /**
     * Get number of active connections
     */
    public int getActiveConnections() {
        return (int) sessions.stream().filter(WebSocketSession::isOpen).count();
    }
    
    /**
     * Check if any clients are connected
     */
    public boolean hasActiveConnections() {
        return sessions.stream().anyMatch(WebSocketSession::isOpen);
    }
}