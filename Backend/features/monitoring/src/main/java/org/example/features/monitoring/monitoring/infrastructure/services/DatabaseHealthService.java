package org.example.features.monitoring.monitoring.infrastructure.services;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for database health monitoring.
 */
public interface DatabaseHealthService {

    /**
     * Checks if the database is accessible and healthy.
     *
     * @return CompletableFuture containing health check result
     */
    CompletableFuture<Boolean> isDatabaseHealthy();

    /**
     * Gets the number of active database connections.
     *
     * @return CompletableFuture containing number of active connections
     */
    CompletableFuture<Integer> getActiveConnections();

    /**
     * Gets database connection pool statistics.
     *
     * @return CompletableFuture containing connection pool stats as JSON string
     */
    CompletableFuture<String> getConnectionPoolStats();

    /**
     * Performs a simple database query to test connectivity.
     *
     * @return CompletableFuture containing true if query succeeds
     */
    CompletableFuture<Boolean> testDatabaseConnectivity();

    /**
     * Gets database performance metrics.
     *
     * @return CompletableFuture containing performance metrics as JSON string
     */
    CompletableFuture<String> getDatabasePerformanceMetrics();

    /**
     * Checks if database is under high load.
     *
     * @return CompletableFuture containing true if database is under high load
     */
    CompletableFuture<Boolean> isDatabaseUnderHighLoad();
}