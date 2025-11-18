package org.example.features.monitoring.monitoring.application.usecases.impl;

import org.example.features.monitoring.monitoring.application.usecases.GetAlertsUseCase;
import org.example.features.monitoring.monitoring.domain.entities.Alert;
import org.example.features.monitoring.monitoring.domain.valueobjects.AlertStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of GetAlertsUseCase.
 */
@Service
public class GetAlertsUseCaseImpl implements GetAlertsUseCase {

    @Override
    public CompletableFuture<List<Alert>> execute(AlertStatus status) {
        // Placeholder implementation - would integrate with alert repository/service
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<List<Alert>> executeBySource(String source) {
        // Placeholder implementation - would filter alerts by source
        return CompletableFuture.completedFuture(List.of());
    }
}