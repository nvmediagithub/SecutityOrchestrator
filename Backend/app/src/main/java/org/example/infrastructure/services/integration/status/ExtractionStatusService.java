package org.example.infrastructure.services.integration.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Service for managing extraction status and monitoring
 * Tracks the progress and state of BPMN context extraction processes
 */
@Service
public class ExtractionStatusService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExtractionStatusService.class);
    
    private final Map<String, ExtractionStatus> activeExtractions = new ConcurrentHashMap<>();
    
    /**
     * Starts a new extraction process
     */
    public void startExtraction(String extractionId, String diagramId) {
        activeExtractions.put(extractionId, new ExtractionStatus(extractionId, "STARTED", LocalDateTime.now()));
        logger.info("Started BPMN context extraction - ID: {}, Diagram: {}", extractionId, diagramId);
    }
    
    /**
     * Updates the status of an extraction process
     */
    public void updateExtractionStatus(String extractionId, String status) {
        ExtractionStatus currentStatus = activeExtractions.get(extractionId);
        if (currentStatus != null) {
            activeExtractions.put(extractionId, new ExtractionStatus(extractionId, status, LocalDateTime.now()));
            logger.debug("Updated extraction status - ID: {}, Status: {}", extractionId, status);
        } else {
            logger.warn("Attempted to update non-existent extraction - ID: {}", extractionId);
        }
    }
    
    /**
     * Marks an extraction as completed
     */
    public void completeExtraction(String extractionId) {
        activeExtractions.put(extractionId, new ExtractionStatus(extractionId, "COMPLETED", LocalDateTime.now()));
        logger.info("Completed BPMN context extraction - ID: {}", extractionId);
    }
    
    /**
     * Marks an extraction as failed
     */
    public void failExtraction(String extractionId, String errorMessage) {
        activeExtractions.put(extractionId, new ExtractionStatus(extractionId, "FAILED", LocalDateTime.now(), errorMessage));
        logger.error("Failed BPMN context extraction - ID: {}, Error: {}", extractionId, errorMessage);
    }
    
    /**
     * Gets the current status of an extraction
     */
    public ExtractionStatus getExtractionStatus(String extractionId) {
        return activeExtractions.get(extractionId);
    }
    
    /**
     * Clears all completed/failed extractions from the tracking map
     */
    public void cleanupOldExtractions() {
        activeExtractions.entrySet().removeIf(entry -> {
            String status = entry.getValue().getStatus();
            return "COMPLETED".equals(status) || "FAILED".equals(status);
        });
        logger.info("Cleaned up old extraction statuses");
    }
    
    /**
     * Gets all active extractions
     */
    public Map<String, ExtractionStatus> getAllActiveExtractions() {
        return new HashMap<>(activeExtractions);
    }
    
    /**
     * Class to represent the status of an extraction process
     */
    public static class ExtractionStatus {
        private final String extractionId;
        private final String status;
        private final LocalDateTime timestamp;
        private final String errorMessage;
        
        public ExtractionStatus(String extractionId, String status, LocalDateTime timestamp) {
            this(extractionId, status, timestamp, null);
        }
        
        public ExtractionStatus(String extractionId, String status, LocalDateTime timestamp, String errorMessage) {
            this.extractionId = extractionId;
            this.status = status;
            this.timestamp = timestamp;
            this.errorMessage = errorMessage;
        }
        
        // Getters
        public String getExtractionId() { return extractionId; }
        public String getStatus() { return status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getErrorMessage() { return errorMessage; }
        
        @Override
        public String toString() {
            return String.format("ExtractionStatus{id='%s', status='%s', time=%s}", 
                               extractionId, status, timestamp);
        }
    }
}