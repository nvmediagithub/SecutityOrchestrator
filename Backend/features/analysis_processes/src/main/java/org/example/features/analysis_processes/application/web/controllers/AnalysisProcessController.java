package org.example.features.analysis_processes.application.web.controllers;

import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.services.AnalysisProcessService;
import org.example.features.bpmn.application.dto.BpmnAnalysisResponse;
import org.example.features.bpmn.application.usecases.AnalyzeBpmnProcessUseCase;
import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessParser;
import org.example.shared.common.ApiResponse;
import org.example.shared.core.valueobjects.ModelId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analysis-processes")
public class AnalysisProcessController {

    private final AnalysisProcessService analysisProcessService;
    private final ProcessParser processParser;
    private final AnalyzeBpmnProcessUseCase analyzeBpmnProcessUseCase;
    private final Path bpmnStoragePath;

    public AnalysisProcessController(
        AnalysisProcessService analysisProcessService,
        ProcessParser processParser,
        AnalyzeBpmnProcessUseCase analyzeBpmnProcessUseCase,
        @Value("${analysis.processes.bpmn-storage-path:data/analysis_processes/bpmn}") String bpmnStoragePath
    ) {
        this.analysisProcessService = analysisProcessService;
        this.processParser = processParser;
        this.analyzeBpmnProcessUseCase = analyzeBpmnProcessUseCase;
        this.bpmnStoragePath = Paths.get(bpmnStoragePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.bpmnStoragePath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize BPMN storage directory", e);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnalysisProcess>>> getAllProcesses() {
        return ResponseEntity.ok(ApiResponse.success(analysisProcessService.getAllProcesses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalysisProcess>> getProcess(@PathVariable("id") String id) {
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
            @PathVariable("id") String id,
            @RequestBody AnalysisProcessRequest request) {
        AnalysisProcess process = request.toEntity();
        return analysisProcessService.updateProcess(id, process)
            .map(updated -> ResponseEntity.ok(ApiResponse.success(updated)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Analysis process not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable("id") String id) {
        boolean deleted = analysisProcessService.deleteProcess(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping(
        value = "/{id}/bpmn",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<BpmnAnalysisResponse>> uploadBpmnDiagram(
            @PathVariable("id") String id,
            @RequestParam("file") MultipartFile file) throws IOException {

        AnalysisProcess process = requireProcess(id);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Uploaded BPMN file is empty"));
        }

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (!processParser.validateBpmnStructure(content)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File is not a valid BPMN diagram"));
        }

        BpmnDiagram diagram = processParser.parseBpmnContent(content);
        if (diagram.getId() == null) {
            diagram.setId(ModelId.generate());
        }
        if (diagram.getDiagramName() == null || diagram.getDiagramName().isBlank()) {
            diagram.setDiagramName(process.getName());
        }

        Path storedPath = bpmnStoragePath.resolve(id + ".bpmn");
        Files.createDirectories(storedPath.getParent());
        Files.writeString(storedPath, content);

        AnalysisProcess updatedProcess = process.toBuilder()
            .bpmnDiagramPath(storedPath.toString())
            .bpmnDiagramName(file.getOriginalFilename())
            .bpmnUploadedAt(LocalDateTime.now())
            .build();

        analysisProcessService.updateProcess(id, updatedProcess)
            .orElseThrow(() -> new RuntimeException("Failed to update process BPMN metadata"));

        BpmnAnalysisResponse response = buildAnalysisResponse(diagram, content);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/bpmn")
    public ResponseEntity<ApiResponse<BpmnAnalysisResponse>> getBpmnDiagram(
            @PathVariable("id") String id) {

        AnalysisProcess process = requireProcess(id);
        if (process.getBpmnDiagramPath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("BPMN diagram not uploaded for process"));
        }

        Path path = Paths.get(process.getBpmnDiagramPath());
        if (Files.notExists(path)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Stored BPMN diagram not found"));
        }

        try {
            String content = Files.readString(path);
            if (!processParser.validateBpmnStructure(content)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Stored BPMN diagram is invalid"));
            }
            BpmnDiagram diagram = processParser.parseBpmnContent(content);
            diagram.setId(ModelId.generate());
            if (diagram.getDiagramName() == null || diagram.getDiagramName().isBlank()) {
                diagram.setDiagramName(process.getName());
            }
            BpmnAnalysisResponse response = buildAnalysisResponse(diagram, content);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to read stored BPMN diagram: " + e.getMessage()));
        }
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

    private AnalysisProcess requireProcess(String id) {
        return analysisProcessService.getProcessById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                HttpStatus.NOT_FOUND, "Analysis process not found"));
    }

    private BpmnAnalysisResponse buildAnalysisResponse(BpmnDiagram diagram, String content) {
        if (diagram.getId() == null) {
            diagram.setId(ModelId.generate());
        }
        return BpmnAnalysisResponse.from(
            diagram,
            analyzeBpmnProcessUseCase.analyzeBpmnProcess(diagram.getId(), diagram),
            content
        );
    }
}
