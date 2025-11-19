package org.example.features.openapi.application.web.controllers;

import org.example.features.openapi.application.dto.OpenApiAnalysisResponse;
import org.example.features.openapi.application.services.OpenApiAnalysisService;
import org.example.shared.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/openapi")
public class OpenApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiController.class);

    private final OpenApiAnalysisService openApiAnalysisService;
    private final Path datasetDirectory;

    public OpenApiController(
        OpenApiAnalysisService openApiAnalysisService,
        @Value("${openapi.dataset-path:dataset/openapi}") String datasetPath
    ) {
        this.openApiAnalysisService = openApiAnalysisService;
        this.datasetDirectory = Path.of(datasetPath).toAbsolutePath().normalize();
    }

    @PostMapping(
        value = "/analyze",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<OpenApiAnalysisResponse>> analyzeSpecification(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "specName", required = false) String specName
    ) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Uploaded OpenAPI file is empty"));
        }

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            OpenApiAnalysisService.Result result = openApiAnalysisService.analyze(
                content,
                StringUtils.hasText(specName) ? specName : file.getOriginalFilename()
            );
            return ResponseEntity.ok(ApiResponse.success(result.response()));
        } catch (OpenApiAnalysisService.InvalidOpenApiSpecificationException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping(value = "/examples", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OpenApiAnalysisResponse>>> loadExamples() {
        List<OpenApiAnalysisResponse> samples = new ArrayList<>();

        if (Files.notExists(datasetDirectory) || !Files.isDirectory(datasetDirectory)) {
            return ResponseEntity.ok(ApiResponse.success(samples));
        }

        try {
            List<Path> files = Files.list(datasetDirectory)
                .filter(path -> {
                    String fileName = path.getFileName().toString().toLowerCase();
                    return fileName.endsWith(".json") || fileName.endsWith(".yaml") || fileName.endsWith(".yml");
                })
                .sorted(Comparator.comparing(Path::getFileName))
                .limit(5)
                .collect(Collectors.toList());

            for (Path file : files) {
                String content = Files.readString(file);
                try {
                    OpenApiAnalysisService.Result result = openApiAnalysisService.analyze(
                        content,
                        file.getFileName().toString()
                    );
                    samples.add(result.response());
                } catch (OpenApiAnalysisService.InvalidOpenApiSpecificationException ex) {
                    LOGGER.warn("Skipping invalid OpenAPI example {}: {}", file, ex.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read OpenAPI dataset", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to read OpenAPI dataset: " + e.getMessage()));
        }

        return ResponseEntity.ok(ApiResponse.success(samples));
    }
}
