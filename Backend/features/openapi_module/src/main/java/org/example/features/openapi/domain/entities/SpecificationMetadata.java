package org.example.features.openapi.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Domain entity representing metadata for OpenAPI specifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationMetadata {
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String author;
    private Set<String> tags;
    private String checksum;
    private long size;

    /**
     * Validates if metadata is complete
     */
    public boolean isComplete() {
        return source != null && !source.isEmpty() &&
               createdAt != null &&
               checksum != null && !checksum.isEmpty();
    }

    /**
     * Updates the last modified timestamp
     */
    public void markModified() {
        this.lastModified = LocalDateTime.now();
    }
}