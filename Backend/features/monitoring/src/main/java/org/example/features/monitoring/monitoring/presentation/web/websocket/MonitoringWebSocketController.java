package org.example.features.monitoring.monitoring.presentation.web.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for monitoring real-time data streaming.
 */
@Controller
public class MonitoringWebSocketController {

    // Note: This is a simplified implementation. In a real application,
    // you would integrate with monitoring services and handle subscriptions.

    @MessageMapping("/monitoring/subscribe")
    @SendTo("/topic/monitoring")
    public String subscribeToMonitoring(String message) {
        // Handle subscription to monitoring updates
        return "Subscribed to monitoring updates: " + message;
    }

    @MessageMapping("/monitoring/unsubscribe")
    @SendTo("/topic/monitoring")
    public String unsubscribeFromMonitoring(String message) {
        // Handle unsubscription from monitoring updates
        return "Unsubscribed from monitoring updates: " + message;
    }
}