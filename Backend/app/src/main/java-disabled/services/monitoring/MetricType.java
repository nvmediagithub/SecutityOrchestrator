package org.example.infrastructure.services.monitoring;

/**
 * Types of metrics that can be collected and monitored
 */
public enum MetricType {
    // System metrics
    CPU_USAGE("cpu_usage", "CPU Usage Percentage"),
    MEMORY_USAGE("memory_usage", "Memory Usage Percentage"),
    DISK_USAGE("disk_usage", "Disk Usage Percentage"),
    
    // API metrics
    API_RESPONSE_TIME("api_response_time", "API Response Time (ms)"),
    API_REQUEST_COUNT("api_request_count", "API Request Count"),
    API_SUCCESS_RATE("api_success_rate", "API Success Rate"),
    
    // LLM metrics
    LLM_RESPONSE_TIME("llm_response_time", "LLM Response Time (ms)"),
    LLM_SUCCESS_RATE("llm_success_rate", "LLM Success Rate"),
    LLM_TOKEN_USAGE("llm_token_usage", "LLM Token Usage"),
    LLM_ERROR_RATE("llm_error_rate", "LLM Error Rate"),
    
    // Business metrics
    ACTIVE_USERS("active_users", "Active Users Count"),
    BPMN_PROCESS_COUNT("bpmn_process_count", "BPMN Process Count"),
    ACTIVE_CONNECTIONS("active_connections", "Active Connections"),
    
    // Performance metrics
    THREAD_COUNT("thread_count", "Thread Count"),
    DATABASE_CONNECTIONS("database_connections", "Database Connection Count");
    
    private final String key;
    private final String description;
    
    MetricType(String key, String description) {
        this.key = key;
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static MetricType fromKey(String key) {
        for (MetricType type : values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown metric type: " + key);
    }
}