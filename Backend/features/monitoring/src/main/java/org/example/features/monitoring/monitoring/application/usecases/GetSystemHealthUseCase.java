package org.example.features.monitoring.monitoring.application.usecases;

import org.example.features.monitoring.monitoring.domain.entities.SystemHealth;

import java.util.concurrent.CompletableFuture;

/**
 * Use case for retrieving current system health status.
 */
public interface GetSystemHealthUseCase {

    /**
     * Gets the current system health status.
     *
     * @return CompletableFuture containing the system health information
     */
    CompletableFuture<SystemHealth> execute();
}