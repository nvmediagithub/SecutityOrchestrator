package org.example.features.llm.domain.entities;

import java.time.LocalDateTime;

/**
 * Domain entity representing performance metrics for AI models.
 * This class encapsulates performance-related data and statistics.
 */
public class PerformanceMetrics {
    private final int totalRequests;
    private final int successfulRequests;
    private final double averageResponseTime;
    private final double successRate;
    private final LocalDateTime lastUpdated;
    private final int tokensProcessed;
    private final double costPerToken;

    public PerformanceMetrics(int totalRequests, int successfulRequests, double averageResponseTime,
                            LocalDateTime lastUpdated, int tokensProcessed, double costPerToken) {
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.averageResponseTime = averageResponseTime;
        this.successRate = calculateSuccessRate(totalRequests, successfulRequests);
        this.lastUpdated = lastUpdated;
        this.tokensProcessed = tokensProcessed;
        this.costPerToken = costPerToken;
    }

    private double calculateSuccessRate(int total, int successful) {
        return total > 0 ? (double) successful / total * 100.0 : 0.0;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public int getTokensProcessed() {
        return tokensProcessed;
    }

    public double getCostPerToken() {
        return costPerToken;
    }

    public double getTotalCost() {
        return tokensProcessed * costPerToken;
    }
}