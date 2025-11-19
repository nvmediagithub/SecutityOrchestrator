package org.example.features.monitoring.monitoring.application.dto;

public record LlmConnectivityResponse(
    String providerId,
    String providerName,
    boolean success,
    int statusCode,
    long latencyMs,
    String endpoint,
    String message
) {}
