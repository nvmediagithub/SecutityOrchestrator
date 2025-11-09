package org.example.infrastructure.services.monitoring;

import java.time.Instant;

/**
 * Represents a single metric data point with timestamp
 */
public class MetricPoint {
    private final MetricType type;
    private final double value;
    private final Instant timestamp;
    
    public MetricPoint(MetricType type, double value, Instant timestamp) {
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }
    
    public MetricType getType() {
        return type;
    }
    
    public double getValue() {
        return value;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "MetricPoint{" +
                "type=" + type +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}