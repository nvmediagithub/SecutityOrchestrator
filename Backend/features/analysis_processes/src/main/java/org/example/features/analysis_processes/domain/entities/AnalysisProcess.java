package org.example.features.analysis_processes.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisProcess {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Current execution status (pending/running/completed/failed).
     */
    @JsonProperty("status")
    private String status;

    /**
     * Type descriptor (securityAnalysis/performanceTest/etc).
     */
    @JsonProperty("type")
    private String type;

    @JsonProperty("bpmnDiagramPath")
    private String bpmnDiagramPath;

    @JsonProperty("bpmnDiagramName")
    private String bpmnDiagramName;

    @JsonProperty("bpmnUploadedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bpmnUploadedAt;
}
