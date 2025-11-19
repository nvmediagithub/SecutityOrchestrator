package org.example.features.analysis_processes.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.features.analysis_processes.domain.valueobjects.AnalysisSessionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisSession {

    @JsonProperty("id")
    private String id;

    @JsonProperty("processId")
    private String processId;

    @JsonProperty("status")
    private AnalysisSessionStatus status;

    @JsonProperty("currentStepId")
    private String currentStepId;

    @JsonProperty("steps")
    @Builder.Default
    private List<AnalysisStep> steps = new ArrayList<>();

    @JsonProperty("context")
    @Builder.Default
    private Map<String, Object> context = new HashMap<>();

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
