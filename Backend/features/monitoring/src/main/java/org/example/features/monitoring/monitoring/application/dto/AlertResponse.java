package org.example.features.monitoring.monitoring.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertSeverity;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertStatus;

import java.time.LocalDateTime;

/**
 * DTO for alert data response.
 */
public class AlertResponse {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("severity")
    private final AlertSeverity severity;

    @JsonProperty("status")
    private final AlertStatus status;

    @JsonProperty("source")
    private final String source;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonProperty("resolvedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime resolvedAt;

    @JsonProperty("tags")
    private final String tags;

    public AlertResponse(String id, String title, String description, AlertSeverity severity,
                        AlertStatus status, String source, LocalDateTime createdAt,
                        LocalDateTime resolvedAt, String tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.source = source;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public String getTags() {
        return tags;
    }

    public boolean isActive() {
        return status == AlertStatus.ACTIVE;
    }

    public boolean isResolved() {
        return status == AlertStatus.RESOLVED;
    }
}