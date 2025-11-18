package org.example.features.testdata.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain entity representing a test data template
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataTemplate {
    private Long id;
    private String templateId;
    private String name;
    private String description;
    private TemplateStatus status;
    private String dataType;
    private List<String> fieldIds;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum TemplateStatus {
        ACTIVE,
        INACTIVE,
        DEPRECATED
    }

    public boolean isActive() {
        return status == TemplateStatus.ACTIVE;
    }
}