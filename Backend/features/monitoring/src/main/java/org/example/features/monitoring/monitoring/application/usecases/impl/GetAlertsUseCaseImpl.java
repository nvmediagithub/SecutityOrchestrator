package org.example.features.monitoring.monitoring.application.usecases.impl;

import org.example.features.monitoring.monitoring.application.usecases.GetAlertsUseCase;
import org.example.features.monitoring.monitoring.domain.entities.Alert;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertSeverity;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementation of GetAlertsUseCase.
 */
@Service
public class GetAlertsUseCaseImpl implements GetAlertsUseCase {

    @Override
    public CompletableFuture<List<Alert>> execute(AlertStatus status) {
        List<Alert> alerts = buildSampleAlerts();
        if (status == null) {
            return CompletableFuture.completedFuture(alerts);
        }
        return CompletableFuture.completedFuture(
            alerts.stream()
                .filter(alert -> alert.getStatus() == status)
                .collect(Collectors.toList())
        );
    }

    @Override
    public CompletableFuture<List<Alert>> executeBySource(String source) {
        List<Alert> alerts = buildSampleAlerts();
        if (source == null || source.isBlank()) {
            return CompletableFuture.completedFuture(alerts);
        }
        String normalizedSource = source.trim().toLowerCase();
        return CompletableFuture.completedFuture(
            alerts.stream()
                .filter(alert -> alert.getSource().toLowerCase().contains(normalizedSource))
                .collect(Collectors.toList())
        );
    }

    private List<Alert> buildSampleAlerts() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
            new Alert(
                UUID.randomUUID().toString(),
                "High CPU Usage",
                "CPU usage exceeded 80% threshold",
                AlertSeverity.HIGH,
                AlertStatus.ACTIVE,
                "System Monitor",
                now.minusMinutes(5),
                null,
                "performance,cpu"
            ),
            new Alert(
                UUID.randomUUID().toString(),
                "Database latency",
                "Database response time is above expected SLA",
                AlertSeverity.MEDIUM,
                AlertStatus.ACTIVE,
                "Database Monitor",
                now.minusMinutes(15),
                null,
                "database,latency"
            ),
            new Alert(
                UUID.randomUUID().toString(),
                "External service recovered",
                "External LLM provider is reachable again",
                AlertSeverity.LOW,
                AlertStatus.RESOLVED,
                "Integration Monitor",
                now.minusHours(2),
                now.minusMinutes(30),
                "integration,external"
            )
        );
    }
}
