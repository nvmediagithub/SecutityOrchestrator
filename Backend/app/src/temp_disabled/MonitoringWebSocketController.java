package org.example.web.controllers;

import org.example.infrastructure.services.monitoring.SecurityOrchestratorMetricsService;
import org.example.infrastructure.services.monitoring.SystemHealth;
import org.example.infrastructure.services.monitoring.Alert;
import org.example.infrastructure.services.monitoring.MetricType;
import org.example.infrastructure.services.monitoring.MetricPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * WebSocket controller for real-time monitoring updates
 */
@Controller
public class MonitoringWebSocketController {
    
    @Autowired
    private SecurityOrchestratorMetricsService metricsService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    // Track active subscriptions by session
    private final Map<String, Set<MetricType>> activeSubscriptions = new ConcurrentHashMap<>();
    private final AtomicReference<Instant> lastHealthUpdate = new AtomicReference<>(Instant.now());
    
    /**
     * Subscribe to real-time metric updates
     */
    @MessageMapping("/monitoring/subscribe")
    public void subscribeToMetrics(@Payload SubscribeMessage message, 
                                 SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Set<MetricType> subscribedMetrics = new HashSet<>();
        
        // Parse requested metrics from message
        if (message.getMetricTypes() != null) {
            for (String metricTypeKey : message.getMetricTypes()) {
                try {
                    MetricType metricType = MetricType.fromKey(metricTypeKey);
                    subscribedMetrics.add(metricType);
                } catch (IllegalArgumentException e) {
                    // Log invalid metric type but continue processing
                    System.err.println("Invalid metric type: " + metricTypeKey);
                }
            }
        }
        
        // Default to system health if no specific metrics requested
        if (subscribedMetrics.isEmpty()) {
            subscribedMetrics.addAll(Arrays.asList(
                MetricType.CPU_USAGE, 
                MetricType.MEMORY_USAGE, 
                MetricType.API_RESPONSE_TIME
            ));
        }
        
        activeSubscriptions.put(sessionId, subscribedMetrics);
        
        // Send initial data immediately
        sendInitialData(sessionId, subscribedMetrics);
        
        System.out.println("Client " + sessionId + " subscribed to metrics: " + subscribedMetrics);
    }
    
    /**
     * Unsubscribe from metric updates
     */
    @MessageMapping("/monitoring/unsubscribe")
    public void unsubscribeFromMetrics(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        activeSubscriptions.remove(sessionId);
        
        System.out.println("Client " + sessionId + " unsubscribed from metrics");
    }
    
    /**
     * Send initial data for subscribed metrics
     */
    private void sendInitialData(String sessionId, Set<MetricType> metricTypes) {
        try {
            SystemHealth health = metricsService.getCurrentHealth();
            
            // Send system health update
            messagingTemplate.convertAndSend(
                "/topic/monitoring/" + sessionId + "/health", 
                health
            );
            
            // Send recent metrics for each subscribed type
            for (MetricType metricType : metricTypes) {
                List<MetricPoint> recentMetrics = metricsService.getHistoricalMetrics(
                    Duration.ofMinutes(5), metricType);
                
                if (!recentMetrics.isEmpty()) {
                    messagingTemplate.convertAndSend(
                        "/topic/monitoring/" + sessionId + "/metrics/" + metricType.getKey(),
                        recentMetrics
                    );
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error sending initial data: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast periodic updates to all subscribed clients
     */
    @Async
    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void broadcastPeriodicUpdates() {
        Instant now = Instant.now();
        
        // Update system health every 30 seconds
        if (Duration.between(lastHealthUpdate.get(), now).getSeconds() >= 30) {
            try {
                SystemHealth health = metricsService.getCurrentHealth();
                
                for (String sessionId : activeSubscriptions.keySet()) {
                    messagingTemplate.convertAndSend(
                        "/topic/monitoring/" + sessionId + "/health",
                        health
                    );
                }
                
                lastHealthUpdate.set(now);
                
            } catch (Exception e) {
                System.err.println("Error broadcasting health update: " + e.getMessage());
            }
        }
        
        // Update metrics for each session based on their subscriptions
        for (Map.Entry<String, Set<MetricType>> entry : activeSubscriptions.entrySet()) {
            String sessionId = entry.getKey();
            Set<MetricType> subscribedMetrics = entry.getValue();
            
            try {
                for (MetricType metricType : subscribedMetrics) {
                    List<MetricPoint> latestMetrics = metricsService.getHistoricalMetrics(
                        Duration.ofMinutes(1), metricType);
                    
                    if (!latestMetrics.isEmpty()) {
                        // Send only the latest metric point for real-time updates
                        MetricPoint latest = latestMetrics.get(latestMetrics.size() - 1);
                        
                        messagingTemplate.convertAndSend(
                            "/topic/monitoring/" + sessionId + "/realtime/" + metricType.getKey(),
                            Map.of(
                                "value", latest.getValue(),
                                "timestamp", latest.getTimestamp(),
                                "type", metricType.getKey()
                            )
                        );
                    }
                }
                
                // Send active alerts
                List<Alert> alerts = metricsService.getRecentAlerts();
                if (!alerts.isEmpty()) {
                    messagingTemplate.convertAndSend(
                        "/topic/monitoring/" + sessionId + "/alerts",
                        alerts
                    );
                }
                
            } catch (Exception e) {
                System.err.println("Error broadcasting metrics for session " + sessionId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Handle client disconnect
     */
    public void handleClientDisconnect(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        activeSubscriptions.remove(sessionId);
        
        System.out.println("Client " + sessionId + " disconnected");
    }
    
    /**
     * Message for subscribing to metrics
     */
    public static class SubscribeMessage {
        private List<String> metricTypes;
        private String dashboardId;
        
        public List<String> getMetricTypes() {
            return metricTypes;
        }
        
        public void setMetricTypes(List<String> metricTypes) {
            this.metricTypes = metricTypes;
        }
        
        public String getDashboardId() {
            return dashboardId;
        }
        
        public void setDashboardId(String dashboardId) {
            this.dashboardId = dashboardId;
        }
    }
}