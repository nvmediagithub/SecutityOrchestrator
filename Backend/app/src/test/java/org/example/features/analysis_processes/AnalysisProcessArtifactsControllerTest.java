package org.example.features.analysis_processes;

import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.services.AnalysisProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class AnalysisProcessArtifactsControllerTest {

    private static final String SAMPLE_BPMN = """
        <?xml version="1.0" encoding="UTF-8"?>
        <bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                          xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                          xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                          xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                          targetNamespace="http://example.com/bpmn">
            <bpmn:process id="Process_1" name="Test Process" isExecutable="true">
                <bpmn:startEvent id="StartEvent_1">
                    <bpmn:outgoing>Flow_1</bpmn:outgoing>
                </bpmn:startEvent>
                <bpmn:endEvent id="EndEvent_1">
                    <bpmn:incoming>Flow_1</bpmn:incoming>
                </bpmn:endEvent>
                <bpmn:sequenceFlow id="Flow_1" sourceRef="StartEvent_1" targetRef="EndEvent_1"/>
            </bpmn:process>
            <bpmndi:BPMNDiagram id="BpmnDiagram_1">
                <bpmndi:BPMNPlane id="BpmnPlane_1" bpmnElement="Process_1">
                    <bpmndi:BPMNShape id="StartEventShape" bpmnElement="StartEvent_1">
                        <dc:Bounds x="150" y="150" width="36" height="36"/>
                    </bpmndi:BPMNShape>
                    <bpmndi:BPMNShape id="EndEventShape" bpmnElement="EndEvent_1">
                        <dc:Bounds x="250" y="150" width="36" height="36"/>
                    </bpmndi:BPMNShape>
                    <bpmndi:BPMNEdge id="Flow_1_di" bpmnElement="Flow_1">
                        <di:waypoint x="186" y="168"/>
                        <di:waypoint x="250" y="168"/>
                    </bpmndi:BPMNEdge>
                </bpmndi:BPMNPlane>
            </bpmndi:BPMNDiagram>
        </bpmn:definitions>
        """;

    private static final String SAMPLE_OPENAPI = """
        openapi: 3.0.0
        info:
          title: Demo API
          version: \"1.0.0\"
        paths:
          /ping:
            get:
              summary: Ping
              responses:
                '200':
                  description: ok
        """;

    @TempDir
    static Path tempDir;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalysisProcessService analysisProcessService;

    private AnalysisProcess process;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws IOException {
        Path storageFile = tempDir.resolve("analysis_processes.json");
        Files.createDirectories(storageFile.getParent());
        if (Files.notExists(storageFile)) {
            Files.writeString(storageFile, "[]", StandardCharsets.UTF_8);
        }
        registry.add("analysis.processes.storage-path", () -> storageFile.toString());
        registry.add("analysis.processes.bpmn-storage-path",
            () -> tempDir.resolve("bpmn-artifacts").toString());
        registry.add("analysis.processes.openapi-storage-path",
            () -> tempDir.resolve("openapi-artifacts").toString());
    }

    @BeforeEach
    void setUp() {
        process = analysisProcessService.createProcess(
            AnalysisProcess.builder()
                .name("Integration Process " + UUID.randomUUID())
                .description("integration test")
                .status("pending")
                .type("securityAnalysis")
                .build()
        );
    }

    @Test
    void shouldUploadAndFetchBpmnDiagram() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "diagram.bpmn",
            "application/xml",
            SAMPLE_BPMN.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart(
                "/api/analysis-processes/{id}/bpmn", process.getId())
            .file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.diagramName").exists());

        mockMvc.perform(MockMvcRequestBuilders.get(
                "/api/analysis-processes/{id}/bpmn", process.getId()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalIssues").isNumber());
    }

    @Test
    void shouldUploadAndFetchOpenApiSpecification() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "spec.yaml",
            "application/x-yaml",
            SAMPLE_OPENAPI.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart(
                "/api/analysis-processes/{id}/openapi", process.getId())
            .file(file))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.specificationName").isNotEmpty());

        mockMvc.perform(MockMvcRequestBuilders.get(
                "/api/analysis-processes/{id}/openapi", process.getId()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.endpointCount").isNumber());
    }
}
