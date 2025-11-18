package org.example.features.monitoring.monitoring.infrastructure.websocket;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket handler for real-time monitoring data streaming.
 */
public class MonitoringWebSocketHandler extends TextWebSocketHandler {

    // Note: This is a basic implementation. In a real application,
    // you would inject services and handle authentication, rate limiting, etc.

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Handle new WebSocket connection
        System.out.println("Monitoring WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // Handle WebSocket connection closure
        System.out.println("Monitoring WebSocket connection closed: " + session.getId());
    }
}