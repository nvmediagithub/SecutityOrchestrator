package org.example.features.monitoring.monitoring.application.usecases;

import org.example.features.monitoring.monitoring.domain.entities.Alert;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertStatus;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Use case for retrieving system alerts.
 */
public interface GetAlertsUseCase {

    /**
     * Gets all alerts with optional status filter.
     *
     * @param status Optional status filter
     * @return CompletableFuture containing list of alerts
     */
    CompletableFuture<List<Alert>> execute(AlertStatus status);

    /**
     * Gets all active alerts.
     *
     * @return CompletableFuture containing list of active alerts
     */
    default CompletableFuture<List<Alert>> getActiveAlerts() {
        return execute(AlertStatus.ACTIVE);
    }

    /**
     * Gets alerts by source.
     *
     * @param source The source of the alerts
     * @return CompletableFuture containing list of alerts from the specified source
     */
    CompletableFuture<List<Alert>> executeBySource(String source);
}