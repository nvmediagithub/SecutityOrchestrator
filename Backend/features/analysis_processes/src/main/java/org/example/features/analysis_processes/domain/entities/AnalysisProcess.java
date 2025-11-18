package org.example.features.analysis_processes.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisProcess {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String status;
    private String type;
}
