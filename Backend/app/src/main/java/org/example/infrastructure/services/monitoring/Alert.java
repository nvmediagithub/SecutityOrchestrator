package org.example.infrastructure.services.monitoring;

import java.time.Instant;

/**
 * Alert information for monitoring dashboard
 */
public class Alert {
    private final String id;
    private final String type;
    private final String message;
    private final AlertSeverity severity;
    private final Instant timestamp;
    
    public Alert(String type, String message, AlertSeverity severity, Instant timestamp) {
        this.id = type + "_" + timestamp.toEpochMilli();
        this.type = type;
        this.message = message;
        this.severity = severity;
        this.timestamp = timestamp;
    }
    
    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public AlertSeverity getSeverity() {
        return severity;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "Alert{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", severity=" + severity +
                ", timestamp=" + timestamp +
                '}';
    }
}