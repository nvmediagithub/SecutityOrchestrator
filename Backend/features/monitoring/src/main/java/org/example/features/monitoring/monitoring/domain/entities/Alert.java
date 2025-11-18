package org.example.features.monitoring.monitoring.domain.entities;

import java.time.LocalDateTime;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertSeverity;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertStatus;

/**
 * Domain entity representing a system alert.
 */
public class Alert {
    private final String id;
    private final String title;
    private final String description;
    private final AlertSeverity severity;
    private final AlertStatus status;
    private final String source;
    private final LocalDateTime createdAt;
    private final LocalDateTime resolvedAt;
    private final String tags;

    public Alert(String id, String title, String description, AlertSeverity severity, AlertStatus status,
                String source, LocalDateTime createdAt, LocalDateTime resolvedAt, String tags) {
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