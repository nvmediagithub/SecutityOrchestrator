package org.example.web.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ExecutionWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public ExecutionWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/subscribe")
    public void handleSubscription(@Payload SubscriptionMessage message) {
        // Mock implementation - in real implementation, this would register the client
        // for updates on the specific execution

        // Send a mock execution started message
        ExecutionUpdate update = new ExecutionUpdate(
            "EXECUTION_STARTED",
            message.getExecutionId(),
            "Execution started",
            null
        );

        messagingTemplate.convertAndSend("/topic/executions/" + message.getExecutionId(), update);

        // Simulate some progress updates
        simulateExecutionProgress(message.getExecutionId());
    }

    private void simulateExecutionProgress(String executionId) {
        // Mock progress updates - in real implementation, this would be triggered by actual execution
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                sendUpdate(executionId, "STEP_COMPLETED", "step-1", "Process validation completed");

                Thread.sleep(1000);
                sendUpdate(executionId, "STEP_COMPLETED", "step-2", "Test case generation completed");

                Thread.sleep(1000);
                sendUpdate(executionId, "EXECUTION_COMPLETED", null, "All steps completed successfully");

            } catch (InterruptedException e) {
                sendUpdate(executionId, "EXECUTION_FAILED", null, "Execution interrupted");
            }
        }).start();
    }

    private void sendUpdate(String executionId, String type, String stepId, String message) {
        ExecutionUpdate update = new ExecutionUpdate(type, executionId, message, stepId);
        messagingTemplate.convertAndSend("/topic/executions/" + executionId, update);
    }

    // Inner DTOs
    public static class SubscriptionMessage {
        private String executionId;

        public String getExecutionId() {
            return executionId;
        }

        public void setExecutionId(String executionId) {
            this.executionId = executionId;
        }
    }

    public static class ExecutionUpdate {
        private String type;
        private String executionId;
        private String message;
        private String stepId;

        public ExecutionUpdate(String type, String executionId, String message, String stepId) {
            this.type = type;
            this.executionId = executionId;
            this.message = message;
            this.stepId = stepId;
        }

        // Getters
        public String getType() { return type; }
        public String getExecutionId() { return executionId; }
        public String getMessage() { return message; }
        public String getStepId() { return stepId; }
    }
}