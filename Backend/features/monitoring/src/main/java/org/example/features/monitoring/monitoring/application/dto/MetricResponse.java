package org.example.features.monitoring.monitoring.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.features.monitoring.monitoring.domain.valueobjects.MetricType;

import java.time.LocalDateTime;

/**
 * DTO for metric data response.
 */
public class MetricResponse {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("type")
    private final MetricType type;

    @JsonProperty("value")
    private final double value;

    @JsonProperty("unit")
    private final String unit;

    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    @JsonProperty("tags")
    private final String tags;

    public MetricResponse(String id, String name, MetricType type, double value, String unit,
                         LocalDateTime timestamp, String tags) {
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