package org.example.features.analysis-processes.application.web.controllers;

import org.example.features.analysis-processes.domain.entities.AnalysisProcess;
import org.example.features.analysis-processes.domain.services.AnalysisProcessService;
import org.example.shared.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analysis-processes")
public class AnalysisProcessController {

    private final AnalysisProcessService analysisProcessService;

    public AnalysisProcessController(AnalysisProcessService analysisProcessService) {
        this.analysisProcessService = analysisProcessService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnalysisProcess>>> getAllProcesses() {
        return ResponseEntity.ok(ApiResponse.success(analysisProcessService.getAllProcesses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalysisProcess>> getProcess(@PathVariable String id) {
        return analysisProcessService.getProcessById(id)
            .map(process -> ResponseEntity.ok(ApiResponse.success(process)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Analysis process not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AnalysisProcess>> createProcess(
            @RequestBody AnalysisProcessRequest request) {
        AnalysisProcess process = request.toEntity();
        AnalysisProcess created = analysisProcessService.createProcess(process);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalysisProcess>> updateProcess(
            @PathVariable String id,
            @RequestBody AnalysisProcessRequest request) {
        AnalysisProcess process = request.toEntity();
        return analysisProcessService.updateProcess(id, process)
            .map(updated -> ResponseEntity.ok(ApiResponse.success(updated)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Analysis process not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable String id) {
        boolean deleted = analysisProcessService.deleteProcess(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public static class AnalysisProcessRequest {
        private String name;
        private String description;
        private String type;
        private String status;
        private LocalDateTime createdAt;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public AnalysisProcess toEntity() {
            return AnalysisProcess.builder()
                .name(name)
                .description(description)
                .type(type)
                .status(status)
                .createdAt(createdAt)
                .build();
        }
    }
}
