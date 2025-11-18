package org.example.features.monitoring.monitoring.infrastructure.config;

import org.example.features.monitoring.monitoring.infrastructure.websocket.MonitoringWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration for monitoring real-time updates.
 */
@Configuration
@EnableWebSocket
public class MonitoringWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MonitoringWebSocketHandler(), "/ws/monitoring")
                .setAllowedOrigins("*"); // In production, specify allowed origins
    }
}