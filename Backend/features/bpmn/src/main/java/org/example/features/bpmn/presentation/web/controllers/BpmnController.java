package org.example.features.bpmn.presentation.web.controllers;

import org.example.features.bpmn.application.dto.BpmnAnalysisResponse;
import org.example.features.bpmn.application.dto.BpmnIssueResponse;
import org.example.features.bpmn.application.usecases.AnalyzeBpmnProcessUseCase;
import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.features.bpmn.domain.services.ProcessAnalyzer;
import org.example.features.bpmn.domain.services.ProcessParser;
import org.example.shared.common.ApiResponse;
import org.example.shared.core.valueobjects.ModelId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller exposing BPMN analysis capabilities.
 */
@RestController
@RequestMapping("/api/bpmn")
public class BpmnController {

    private final ProcessParser processParser;
    private final AnalyzeBpmnProcessUseCase analyzeUseCase;
    private final String datasetPath;

    public BpmnController(
        ProcessParser processParser,
        AnalyzeBpmnProcessUseCase analyzeUseCase,
        @Value("${bpmn.dataset-path:dataset/bpmn}") String datasetPath
    ) {
        this.processParser = processParser;
        this.analyzeUseCase = analyzeUseCase;
        this.datasetPath = datasetPath;
    }

    @PostMapping(
        value = "/analyze",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<BpmnAnalysisResponse>> analyzeDiagram(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "diagramName", required = false) String diagramName
    ) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Uploaded BPMN file is empty"));
        }

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (!processParser.validateBpmnStructure(content)) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("File is not a valid BPMN diagram"));
        }

        BpmnDiagram diagram = processParser.parseBpmnContent(content);
        if (StringUtils.hasText(diagramName)) {
            diagram.setDiagramName(diagramName);
        }
        if (diagram.getId() == null) {
            diagram.setId(ModelId.generate());
        }

        ProcessAnalyzer.AnalysisResult analysisResult =
            analyzeUseCase.analyzeBpmnProcess(diagram.getId(), diagram);

        BpmnAnalysisResponse response = BpmnAnalysisResponse.from(diagram, analysisResult, content);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping(value = "/examples", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<BpmnAnalysisResponse>>> getSampleDiagrams() {
        List<BpmnAnalysisResponse> samples = new ArrayList<>();
        try {
            Path datasetDir = Path.of(datasetPath);
            if (Files.notExists(datasetDir) || !Files.isDirectory(datasetDir)) {
                return ResponseEntity.ok(ApiResponse.success(samples));
            }
            List<Path> files = Files.list(datasetDir)
                .filter(path -> path.toString().endsWith(".bpmn"))
                .limit(5)
                .collect(Collectors.toList());
            for (Path file : files) {
                String content = Files.readString(file);
                if (!processParser.validateBpmnStructure(content)) {
                    continue;
                }
                BpmnDiagram diagram = processParser.parseBpmnContent(content);
                if (diagram.getId() == null) {
                    diagram.setId(ModelId.generate());
                }
                if (!StringUtils.hasText(diagram.getDiagramName())) {
                    diagram.setDiagramName(file.getFileName().toString());
                }
                ProcessAnalyzer.AnalysisResult analysisResult =
                    analyzeUseCase.analyzeBpmnProcess(diagram.getId(), diagram);
                samples.add(BpmnAnalysisResponse.from(diagram, analysisResult, content));
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to read BPMN dataset: " + e.getMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success(samples));
    }
}
