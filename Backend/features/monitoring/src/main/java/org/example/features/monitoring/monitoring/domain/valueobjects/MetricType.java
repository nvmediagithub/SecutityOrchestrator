package org.example.features.monitoring.monitoring.domain.valueobjects;

/**
 * Value object representing the type of metric.
 */
public enum MetricType {
    CPU_USAGE,
    MEMORY_USAGE,
    DISK_USAGE,
    NETWORK_USAGE,
    DATABASE_CONNECTIONS,
    EXTERNAL_SERVICE_LATENCY,
    APPLICATION_HEALTH,
    SYSTEM_LOAD
}