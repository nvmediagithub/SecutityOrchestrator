package org.example.features.llm.domain.dto;

import java.util.List;

/**
 * Request DTO for chat completions
 */
public class ChatCompletionRequest {

    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer maxTokens;
    private Double topP;
    private Integer frequencyPenalty;
    private Integer presencePenalty;
    private List<String> stop;
    private String user;

    // Default constructor
    public ChatCompletionRequest() {}

    // Constructor with essential fields
    public ChatCompletionRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    // Getters and setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Integer frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Integer getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Integer presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public List<String> getStop() {
        return stop;
    }

    public void setStop(List<String> stop) {
        this.stop = stop;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Inner class representing a message in the chat
     */
    public static class Message {
        private String role;
        private String content;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}