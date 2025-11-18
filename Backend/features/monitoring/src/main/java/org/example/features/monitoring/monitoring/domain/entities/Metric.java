package org.example.features.monitoring.monitoring.domain.entities;

import java.time.LocalDateTime;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;

/**
 * Domain entity representing a system metric.
 */
public class Metric {
    private final String id;
    private final String name;
    private final MetricType type;
    private final double value;
    private final String unit;
    private final LocalDateTime timestamp;
    private final String tags;

    public Metric(String id, String name, MetricType type, double value, String unit, LocalDateTime timestamp, String tags) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MetricType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTags() {
        return tags;
    }
}