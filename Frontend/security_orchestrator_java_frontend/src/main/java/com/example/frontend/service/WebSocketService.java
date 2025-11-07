package com.example.frontend.service;

import com.example.frontend.dto.ExecutionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Service
public class WebSocketService {

    private final WebSocketStompClient stompClient;
    private final ObjectMapper objectMapper;

    @Value("${websocket.url}")
    private String websocketUrl;

    public WebSocketService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public void subscribeToExecutionUpdates(String executionId, Consumer<ExecutionDto> updateHandler) {
        try {
            StompSession session = stompClient.connectAsync(websocketUrl + "/execution", new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    session.subscribe("/topic/execution/" + executionId, this);
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    try {
                        ExecutionDto execution = objectMapper.convertValue(payload, ExecutionDto.class);
                        updateHandler.accept(execution);
                    } catch (Exception e) {
                        System.err.println("Error processing WebSocket message: " + e.getMessage());
                    }
                }

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Object.class;
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                          byte[] payload, Throwable exception) {
                    System.err.println("WebSocket error: " + exception.getMessage());
                }
            }).get();

            // Keep session alive (in production, you'd want to manage this better)
            Thread.sleep(1000);

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Failed to connect to WebSocket: " + e.getMessage());
        }
    }

    public void subscribeToWorkflowUpdates(String workflowId, Consumer<ExecutionDto> updateHandler) {
        subscribeToExecutionUpdates("workflow-" + workflowId, updateHandler);
    }
}