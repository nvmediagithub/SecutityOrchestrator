package org.example.infrastructure.services.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entities.BpmnDiagram;
import org.example.domain.entities.analysis.AnalysisResult;
import org.example.domain.valueobjects.BpmnProcessId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for integrating BPMN processes with API testing
 * Handles parsing, analysis, and integration of BPMN diagrams for test generation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BpmnTestingIntegrationService {

    private static final String BPMN_GUIDE_PATH = "SecutityOrchestrator/guide/bpmn";

    /**
     * Parse and analyze BPMN process diagram
     */
    public AnalysisResult parseAndAnalyzeBpmn(MultipartFile bpmnFile) {
        log.info("Parsing BPMN file: {}", bpmnFile.getOriginalFilename());
        
        try {
            // Save uploaded file temporarily
            Path tempFile = Files.createTempFile("bpmn_", ".bpmn");
            bpmnFile.transferTo(tempFile);
            
            // Parse BPMN XML
            BpmnProcess parsedProcess = parseBpmnXml(tempFile.toFile());
            
            // Analyze process for API testing
            AnalysisResult result = analyzeBpmnForTesting(parsedProcess);
            
            // Clean up temporary file
            Files.deleteIfExists(tempFile);
            
            log.info("BPMN analysis completed for process: {}", parsedProcess.getProcessId());
            return result;
            
        } catch (Exception e) {
            log.error("Error analyzing BPMN process", e);
            throw new RuntimeException("Failed to analyze BPMN process", e);
        }
    }

    /**
     * Get list of available BPMN processes from guide/bpmn directory
     */
    public List<BpmnDiagram> getAvailableBpmnProcesses() {
        log.info("Loading available BPMN processes from guide directory");
        
        List<BpmnDiagram> processes = new ArrayList<>();
        
        try {
            Path bpmnDir = Paths.get(BPMN_GUIDE_PATH);
            if (Files.exists(bpmnDir) && Files.isDirectory(bpmnDir)) {
                Files.list(bpmnDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".bpmn"))
                    .forEach(bpmnFile -> {
                        try {
                            BpmnProcess process = parseBpmnXml(bpmnFile.toFile());
                            
                            BpmnDiagram diagram = new BpmnDiagram();
                            diagram.setId(process.getProcessId());
                            diagram.setDiagramName(extractProcessName(process.getProcessId()));
                            diagram.setVersion("1.0");
                            diagram.setFileName(bpmnFile.getFileName().toString());
                            diagram.setProcessSteps(process.getSteps());
                            diagram.setSequenceFlows(process.getSequenceFlows());
                            diagram.setTotalSteps(process.getSteps().size());
                            
                            processes.add(diagram);
                            
                        } catch (Exception e) {
                            log.warn("Failed to parse BPMN file: {}", bpmnFile, e);
                        }
                    });
            }
        } catch (IOException e) {
            log.error("Error reading BPMN directory", e);
        }
        
        return processes;
    }

    /**
     * Get process steps for a specific BPMN process
     */
    public List<String> getProcessSteps(String processId) {
        return getAvailableBpmnProcesses().stream()
            .filter(diagram -> diagram.getId().equals(processId))
            .findFirst()
            .map(BpmnDiagram::getProcessSteps)
            .orElse(Collections.emptyList());
    }

    /**
     * Get sequence flows for a specific BPMN process
     */
    public List<String> getSequenceFlows(String processId) {
        return getAvailableBpmnProcesses().stream()
            .filter(diagram -> diagram.getId().equals(processId))
            .findFirst()
            .map(BpmnDiagram::getSequenceFlows)
            .orElse(Collections.emptyList());
    }

    /**
     * Parse BPMN XML file and extract process information
     */
    private BpmnProcess parseBpmnXml(File bpmnFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(bpmnFile);
        
        document.getDocumentElement().normalize();
        
        // Extract process information
        String processId = extractProcessId(document);
        String processName = extractProcessName(document);
        
        // Extract tasks and sequence flows
        List<String> steps = extractTasks(document);
        List<String> sequenceFlows = extractSequenceFlows(document);
        
        return new BpmnProcess(processId, processName, steps, sequenceFlows);
    }

    /**
     * Extract process ID from BPMN XML
     */
    private String extractProcessId(Document document) {
        NodeList processNodes = document.getElementsByTagName("bpmn:process");
        if (processNodes.getLength() > 0) {
            Element processElement = (Element) processNodes.item(0);
            return processElement.getAttribute("id");
        }
        return "unknown_process";
    }

    /**
     * Extract process name from BPMN XML
     */
    private String extractProcessName(Document document) {
        NodeList processNodes = document.getElementsByTagName("bpmn:process");
        if (processNodes.getLength() > 0) {
            Element processElement = (Element) processNodes.item(0);
            return processElement.getAttribute("name");
        }
        return "Unknown Process";
    }

    /**
     * Extract tasks from BPMN XML
     */
    private List<String> extractTasks(Document document) {
        List<String> tasks = new ArrayList<>();
        NodeList taskNodes = document.getElementsByTagName("bpmn:task");
        
        for (int i = 0; i < taskNodes.getLength(); i++) {
            Element taskElement = (Element) taskNodes.item(i);
            String taskName = taskElement.getAttribute("name");
            if (!taskName.isEmpty()) {
                tasks.add(taskName);
            }
        }
        
        return tasks;
    }

    /**
     * Extract sequence flows from BPMN XML
     */
    private List<String> extractSequenceFlows(Document document) {
        List<String> flows = new ArrayList<>();
        NodeList flowNodes = document.getElementsByTagName("bpmn:sequenceFlow");
        
        for (int i = 0; i < flowNodes.getLength(); i++) {
            Element flowElement = (Element) flowNodes.item(i);
            String sourceRef = flowElement.getAttribute("sourceRef");
            String targetRef = flowElement.getAttribute("targetRef");
            String flow = sourceRef + " -> " + targetRef;
            flows.add(flow);
        }
        
        return flows;
    }

    /**
     * Extract process name from process ID
     */
    private String extractProcessName(String processId) {
        // Convert process ID to human-readable name
        String name = processId.replace("Process_", "").replace("_", " ");
        return Arrays.stream(name.split(" "))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
    }

    /**
     * Analyze BPMN process for API testing opportunities
     */
    private AnalysisResult analyzeBpmnForTesting(BpmnProcess process) {
        AnalysisResult result = new AnalysisResult();
        result.setId(UUID.randomUUID().toString());
        result.setStatus("COMPLETED");
        result.setAnalysisType("BPMN_TESTING_INTEGRATION");
        result.setCreatedAt(java.time.LocalDateTime.now());
        
        Map<String, Object> findings = new HashMap<>();
        
        // Analyze process for testing opportunities
        findings.put("totalSteps", process.getSteps().size());
        findings.put("totalFlows", process.getSequenceFlows().size());
        findings.put("processComplexity", calculateComplexity(process));
        findings.put("apiEndpoints", extractApiEndpoints(process));
        findings.put("testOpportunities", identifyTestOpportunities(process));
        findings.put("dataFlows", analyzeDataFlows(process));
        
        result.setFindings(findings);
        result.setRecommendations(generateTestingRecommendations(process));
        
        return result;
    }

    /**
     * Calculate process complexity score
     */
    private Map<String, Object> calculateComplexity(BpmnProcess process) {
        int steps = process.getSteps().size();
        int flows = process.getSequenceFlows().size();
        int complexity = steps + flows;
        
        String complexityLevel;
        if (complexity < 10) {
            complexityLevel = "LOW";
        } else if (complexity < 20) {
            complexityLevel = "MEDIUM";
        } else {
            complexityLevel = "HIGH";
        }
        
        return Map.of(
            "score", complexity,
            "level", complexityLevel,
            "steps", steps,
            "flows", flows
        );
    }

    /**
     * Extract potential API endpoints from BPMN tasks
     */
    private List<String> extractApiEndpoints(BpmnProcess process) {
        return process.getSteps().stream()
            .filter(step -> step.contains("POST") || step.contains("GET") || step.contains("PUT") || step.contains("DELETE"))
            .collect(Collectors.toList());
    }

    /**
     * Identify testing opportunities
     */
    private List<String> identifyTestOpportunities(BpmnProcess process) {
        List<String> opportunities = new ArrayList<>();
        
        // Check for authentication steps
        if (process.getSteps().stream().anyMatch(step -> step.toLowerCase().contains("auth"))) {
            opportunities.add("Authentication flow testing");
        }
        
        // Check for payment/business logic
        if (process.getSteps().stream().anyMatch(step -> step.toLowerCase().contains("pay") || step.toLowerCase().contains("payment"))) {
            opportunities.add("Payment flow security testing");
        }
        
        // Check for data validation
        if (process.getSteps().stream().anyMatch(step -> step.toLowerCase().contains("valid") || step.toLowerCase().contains("check"))) {
            opportunities.add("Data validation testing");
        }
        
        // Check for error handling
        if (process.getSequenceFlows().size() > process.getSteps().size()) {
            opportunities.add("Error handling and edge case testing");
        }
        
        return opportunities;
    }

    /**
     * Analyze data flow between steps
     */
    private Map<String, Object> analyzeDataFlows(BpmnProcess process) {
        Map<String, Object> dataFlows = new HashMap<>();
        
        // Identify potential data dependencies
        List<String> dataDependencies = new ArrayList<>();
        for (int i = 0; i < process.getSteps().size() - 1; i++) {
            String currentStep = process.getSteps().get(i);
            String nextStep = process.getSteps().get(i + 1);
            dataDependencies.add(currentStep + " -> " + nextStep);
        }
        
        dataFlows.put("dependencies", dataDependencies);
        dataFlows.put("totalDependencies", dataDependencies.size());
        dataFlows.put("complexity", dataDependencies.size() > 5 ? "HIGH" : "LOW");
        
        return dataFlows;
    }

    /**
     * Generate testing recommendations based on BPMN analysis
     */
    private Map<String, Object> generateTestingRecommendations(BpmnProcess process) {
        Map<String, Object> recommendations = new HashMap<>();
        
        recommendations.put("security", List.of(
            "Test authentication in each step",
            "Validate input data at each decision point",
            "Test error flows and edge cases"
        ));
        
        recommendations.put("functional", List.of(
            "Test end-to-end process execution",
            "Validate business rules at each step",
            "Test data persistence between steps"
        ));
        
        recommendations.put("performance", List.of(
            "Test process execution time",
            "Validate concurrent process execution",
            "Test system resource usage"
        ));
        
        return recommendations;
    }

    /**
     * Internal class to represent parsed BPMN process
     */
    private static class BpmnProcess {
        private final String processId;
        private final String processName;
        private final List<String> steps;
        private final List<String> sequenceFlows;

        public BpmnProcess(String processId, String processName, List<String> steps, List<String> sequenceFlows) {
            this.processId = processId;
            this.processName = processName;
            this.steps = steps;
            this.sequenceFlows = sequenceFlows;
        }

        public String getProcessId() { return processId; }
        public String getProcessName() { return processName; }
        public List<String> getSteps() { return steps; }
        public List<String> getSequenceFlows() { return sequenceFlows; }
    }
}