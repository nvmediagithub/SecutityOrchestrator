package org.example.infrastructure.services.bpmn.analyzers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Анализатор структуры BPMN диаграмм
 * Обеспечивает comprehensive анализ структуры и элементов BPMN
 */
public class StructureAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(StructureAnalyzer.class);
    
    private final Builder builder;
    
    private StructureAnalyzer(Builder builder) {
        this.builder = builder;
    }
    
    public static class Builder {
        private boolean validateBpmnStandard = true;
        private boolean analyzeComplexity = true;
        private boolean detectAntiPatterns = true;
        private boolean extractMetrics = true;
        private boolean validateNamingConventions = true;
        private int maxComplexityScore = 100;
        private List<String> allowedElements = Arrays.asList(
            "startEvent", "endEvent", "task", "userTask", "serviceTask", "exclusiveGateway", 
            "parallelGateway", "inclusiveGateway", "sequenceFlow", "messageFlow", "subProcess"
        );
        private List<String> antiPatterns = Arrays.asList(
            "spaghetti_process", "too_many_gateways", "deeply_nested_subprocesses", 
            "unnamed_elements", "complex_conditions", "missing_error_handling"
        );
        
        public Builder setValidateBpmnStandard(boolean validateBpmnStandard) {
            this.validateBpmnStandard = validateBpmnStandard;
            return this;
        }
        
        public Builder setAnalyzeComplexity(boolean analyzeComplexity) {
            this.analyzeComplexity = analyzeComplexity;
            return this;
        }
        
        public Builder setDetectAntiPatterns(boolean detectAntiPatterns) {
            this.detectAntiPatterns = detectAntiPatterns;
            return this;
        }
        
        public Builder setExtractMetrics(boolean extractMetrics) {
            this.extractMetrics = extractMetrics;
            return this;
        }
        
        public Builder setValidateNamingConventions(boolean validateNamingConventions) {
            this.validateNamingConventions = validateNamingConventions;
            return this;
        }
        
        public Builder setMaxComplexityScore(int maxComplexityScore) {
            if (maxComplexityScore <= 0) throw new IllegalArgumentException("maxComplexityScore must be positive");
            this.maxComplexityScore = maxComplexityScore;
            return this;
        }
        
        public Builder setAllowedElements(List<String> allowedElements) {
            if (allowedElements == null || allowedElements.isEmpty()) 
                throw new IllegalArgumentException("allowedElements cannot be null or empty");
            this.allowedElements = new ArrayList<>(allowedElements);
            return this;
        }
        
        public Builder setAntiPatterns(List<String> antiPatterns) {
            if (antiPatterns == null || antiPatterns.isEmpty()) 
                throw new IllegalArgumentException("antiPatterns cannot be null or empty");
            this.antiPatterns = new ArrayList<>(antiPatterns);
            return this;
        }
        
        public StructureAnalyzer build() {
            return new StructureAnalyzer(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Выполняет comprehensive анализ структуры BPMN
     */
    public CompletableFuture<StructureAnalysisResult> analyzeStructure(String bpmnXml) {
        logger.info("Starting structure analysis of BPMN");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateBpmnInput(bpmnXml);
                
                Document document = parseBpmnXml(bpmnXml);
                StructureAnalysisResult result = new StructureAnalysisResult();
                
                // Основной анализ
                extractBasicElements(document, result);
                
                if (builder.validateBpmnStandard) {
                    validateBpmnStandard(document, result);
                }
                
                if (builder.analyzeComplexity) {
                    analyzeComplexity(document, result);
                }
                
                if (builder.detectAntiPatterns) {
                    detectAntiPatterns(document, result);
                }
                
                if (builder.extractMetrics) {
                    extractMetrics(document, result);
                }
                
                if (builder.validateNamingConventions) {
                    validateNamingConventions(document, result);
                }
                
                // Финальная оценка
                result.setOverallScore(calculateOverallScore(result));
                result.setRecommendations(generateRecommendations(result));
                
                logger.info("Structure analysis completed with score: {}", result.getOverallScore());
                return result;
                
            } catch (Exception e) {
                logger.error("Failed to analyze BPMN structure", e);
                return createErrorResult(e);
            }
        });
    }
    
    /**
     * Анализирует complexity отдельного процесса
     */
    public CompletableFuture<ComplexityAnalysis> analyzeComplexity(String bpmnXml) {
        logger.info("Starting complexity analysis of BPMN");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateBpmnInput(bpmnXml);
                Document document = parseBpmnXml(bpmnXml);
                
                ComplexityAnalysis analysis = new ComplexityAnalysis();
                
                // Cyclomatic complexity
                analysis.setCyclomaticComplexity(calculateCyclomaticComplexity(document));
                
                // Nesting depth
                analysis.setMaxNestingDepth(calculateMaxNestingDepth(document));
                
                // Fan-out (количество исходящих связей)
                analysis.setMaxFanOut(calculateMaxFanOut(document));
                
                // Количество элементов
                analysis.setElementCount(countElements(document));
                
                // Weighted complexity
                analysis.setWeightedComplexity(calculateWeightedComplexity(document));
                
                // Complexity score
                analysis.setComplexityScore(calculateComplexityScore(analysis));
                
                logger.info("Complexity analysis completed: {}", analysis.getComplexityScore());
                return analysis;
                
            } catch (Exception e) {
                logger.error("Failed to analyze complexity", e);
                return new ComplexityAnalysis(e.getMessage());
            }
        });
    }
    
    /**
     * Обнаруживает anti-patterns в BPMN
     */
    public CompletableFuture<List<AntiPattern>> detectAntiPatterns(String bpmnXml) {
        logger.info("Starting anti-pattern detection in BPMN");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateBpmnInput(bpmnXml);
                Document document = parseBpmnXml(bpmnXml);
                
                List<AntiPattern> antiPatterns = new ArrayList<>();
                
                // Spaghetti process
                if (isSpaghettiProcess(document)) {
                    antiPatterns.add(new AntiPattern(
                        "spaghetti_process",
                        "Spaghetti Process",
                        "HIGH",
                        "Process has too many unstructured connections and lacks clear flow",
                        Arrays.asList("Restructure process flow", "Add sub-processes", "Improve sequence flow")
                    ));
                }
                
                // Too many gateways
                int gatewayCount = countElementsByType(document, "gateway");
                if (gatewayCount > 10) {
                    antiPatterns.add(new AntiPattern(
                        "too_many_gateways",
                        "Too Many Gateways",
                        "MEDIUM",
                        "Process contains " + gatewayCount + " gateways which may indicate over-complexity",
                        Arrays.asList("Simplify decision logic", "Use sub-processes", "Consider business rules engine")
                    ));
                }
                
                // Deeply nested subprocesses
                int maxNesting = calculateMaxNestingDepth(document);
                if (maxNesting > 3) {
                    antiPatterns.add(new AntiPattern(
                        "deeply_nested_subprocesses",
                        "Deeply Nested Subprocesses",
                        "MEDIUM",
                        "Process has nesting depth of " + maxNesting + " which may impact maintainability",
                        Arrays.asList("Flatten process hierarchy", "Use service calls instead of sub-processes", "Add process documentation")
                    ));
                }
                
                // Unnamed elements
                List<Element> unnamedElements = findUnnamedElements(document);
                if (!unnamedElements.isEmpty()) {
                    antiPatterns.add(new AntiPattern(
                        "unnamed_elements",
                        "Unnamed Elements",
                        "LOW",
                        "Found " + unnamedElements.size() + " elements without proper names",
                        Arrays.asList("Add meaningful names to all elements", "Follow naming conventions")
                    ));
                }
                
                logger.info("Anti-pattern detection completed, found {} patterns", antiPatterns.size());
                return antiPatterns;
                
            } catch (Exception e) {
                logger.error("Failed to detect anti-patterns", e);
                return Arrays.asList(new AntiPattern(
                    "error",
                    "Analysis Error",
                    "HIGH",
                    "Failed to analyze BPMN: " + e.getMessage(),
                    Arrays.asList("Validate BPMN XML syntax", "Check for parsing errors")
                ));
            }
        });
    }
    
    /**
     * Извлекает метрики из BPMN
     */
    public CompletableFuture<BpmnMetrics> extractMetrics(String bpmnXml) {
        logger.info("Starting metrics extraction from BPMN");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateBpmnInput(bpmnXml);
                Document document = parseBpmnXml(bpmnXml);
                
                BpmnMetrics metrics = new BpmnMetrics();
                
                // Basic counts
                metrics.setTaskCount(countElementsByType(document, "task"));
                metrics.setGatewayCount(countElementsByType(document, "gateway"));
                metrics.setEventCount(countElementsByType(document, "startEvent") + 
                                    countElementsByType(document, "endEvent"));
                metrics.setSequenceFlowCount(countElementsByType(document, "sequenceFlow"));
                
                // Process dimensions
                NodeList processes = document.getElementsByTagName("process");
                if (processes.getLength() > 0) {
                    Element process = (Element) processes.item(0);
                    metrics.setProcessName(process.getAttribute("name"));
                    metrics.setIsExecutable("true".equals(process.getAttribute("isExecutable")));
                }
                
                // Complexity metrics
                ComplexityAnalysis complexity = analyzeComplexity(bpmnXml).join();
                metrics.setCyclomaticComplexity(complexity.getCyclomaticComplexity());
                metrics.setNestingDepth(complexity.getMaxNestingDepth());
                metrics.setMaxFanOut(complexity.getMaxFanOut());
                
                // Quality metrics
                metrics.setNamingCompliance(calculateNamingCompliance(document));
                metrics.setStandardCompliance(calculateStandardCompliance(document));
                
                logger.info("Metrics extraction completed for process: {}", metrics.getProcessName());
                return metrics;
                
            } catch (Exception e) {
                logger.error("Failed to extract metrics", e);
                return new BpmnMetrics(e.getMessage());
            }
        });
    }
    
    private void validateBpmnInput(String bpmnXml) {
        if (bpmnXml == null || bpmnXml.trim().isEmpty()) {
            throw new IllegalArgumentException("BPMN XML cannot be null or empty");
        }
        
        if (!bpmnXml.contains("bpmn:definitions") && !bpmnXml.contains("definitions")) {
            throw new IllegalArgumentException("Invalid BPMN XML: missing definitions element");
        }
    }
    
    private Document parseBpmnXml(String bpmnXml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(bpmnXml)));
    }
    
    private void extractBasicElements(Document document, StructureAnalysisResult result) {
        Map<String, Integer> elementCounts = new HashMap<>();
        Map<String, List<String>> elementNames = new HashMap<>();
        
        // Подсчет всех элементов
        String[] elementTypes = {"startEvent", "endEvent", "task", "userTask", "serviceTask", 
                                "exclusiveGateway", "parallelGateway", "inclusiveGateway", 
                                "sequenceFlow", "messageFlow", "subProcess"};
        
        for (String elementType : elementTypes) {
            int count = countElementsByType(document, elementType);
            if (count > 0) {
                elementCounts.put(elementType, count);
                elementNames.put(elementType, getElementNames(document, elementType));
            }
        }
        
        result.setElementCounts(elementCounts);
        result.setElementNames(elementNames);
    }
    
    private void validateBpmnStandard(Document document, StructureAnalysisResult result) {
        List<String> violations = new ArrayList<>();
        
        // Проверка наличия start event
        if (countElementsByType(document, "startEvent") == 0) {
            violations.add("Process must have at least one start event");
        }
        
        // Проверка наличия end event
        if (countElementsByType(document, "endEvent") == 0) {
            violations.add("Process must have at least one end event");
        }
        
        // Проверка правильности gateway использования
        int gatewayCount = countElementsByType(document, "gateway");
        if (gatewayCount > 0) {
            // Проверка, что gateways имеют outgoing sequence flows
            NodeList gateways = document.getElementsByTagName("gateway");
            for (int i = 0; i < gateways.getLength(); i++) {
                Element gateway = (Element) gateways.item(i);
                if (!hasOutgoingFlows(gateway)) {
                    violations.add("Gateway '" + getElementName(gateway) + "' has no outgoing sequence flows");
                }
            }
        }
        
        result.setStandardViolations(violations);
    }
    
    private void analyzeComplexity(Document document, StructureAnalysisResult result) {
        ComplexityAnalysis complexity = analyzeComplexity(document.toString()).join();
        
        result.setComplexityAnalysis(complexity);
        result.setComplexityScore(complexity.getComplexityScore());
        result.setComplexityLevel(determineComplexityLevel(complexity.getComplexityScore()));
    }
    
    private void detectAntiPatterns(Document document, StructureAnalysisResult result) {
        List<AntiPattern> patterns = detectAntiPatterns(document.toString()).join();
        result.setAntiPatterns(patterns);
    }
    
    private void extractMetrics(Document document, StructureAnalysisResult result) {
        BpmnMetrics metrics = extractMetrics(document.toString()).join();
        result.setMetrics(metrics);
    }
    
    private void validateNamingConventions(Document document, StructureAnalysisResult result) {
        List<String> namingIssues = new ArrayList<>();
        
        NodeList allElements = document.getElementsByTagName("*");
        for (int i = 0; i < allElements.getLength(); i++) {
            Element element = (Element) allElements.item(i);
            String name = element.getAttribute("name");
            
            // Проверка на пустые имена
            if (name == null || name.trim().isEmpty()) {
                namingIssues.add("Element '" + element.getTagName() + "' has no name");
            }
            // Проверка на слишком длинные имена
            else if (name.length() > 100) {
                namingIssues.add("Element name too long: " + name);
            }
            // Проверка на специальные символы
            else if (!name.matches("^[a-zA-Z0-9\\s_\\-]+$")) {
                namingIssues.add("Element name contains invalid characters: " + name);
            }
        }
        
        result.setNamingIssues(namingIssues);
        result.setNamingCompliance(calculateNamingCompliance(document));
    }
    
    private int countElementsByType(Document document, String elementType) {
        return document.getElementsByTagName(elementType).getLength();
    }
    
    private List<String> getElementNames(Document document, String elementType) {
        List<String> names = new ArrayList<>();
        NodeList elements = document.getElementsByTagName(elementType);
        
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            String name = getElementName(element);
            if (name != null && !name.trim().isEmpty()) {
                names.add(name);
            }
        }
        
        return names;
    }
    
    private String getElementName(Element element) {
        return element.getAttribute("name");
    }
    
    private boolean hasOutgoingFlows(Element element) {
        NodeList outgoing = element.getElementsByTagName("outgoing");
        return outgoing.getLength() > 0;
    }
    
    private List<Element> findUnnamedElements(Document document) {
        List<Element> unnamed = new ArrayList<>();
        NodeList allElements = document.getElementsByTagName("*");
        
        for (int i = 0; i < allElements.getLength(); i++) {
            Element element = (Element) allElements.item(i);
            String name = element.getAttribute("name");
            
            // Проверяем только значимые элементы
            String tagName = element.getTagName();
            if (tagName.contains(":") ? 
                Arrays.asList("bpmn:task", "bpmn:gateway", "bpmn:event").contains(tagName) :
                Arrays.asList("task", "gateway", "startEvent", "endEvent", "subProcess").contains(tagName)) {
                
                if (name == null || name.trim().isEmpty()) {
                    unnamed.add(element);
                }
            }
        }
        
        return unnamed;
    }
    
    // Complexity calculation methods
    private int calculateCyclomaticComplexity(Document document) {
        int decisionPoints = countElementsByType(document, "exclusiveGateway") +
                           countElementsByType(document, "inclusiveGateway") +
                           countElementsByType(document, "parallelGateway");
        
        return decisionPoints + 1;
    }
    
    private int calculateMaxNestingDepth(Document document) {
        // Simplified nesting depth calculation
        NodeList subProcesses = document.getElementsByTagName("subProcess");
        return subProcesses.getLength() > 0 ? 2 : 1; // Placeholder implementation
    }
    
    private int calculateMaxFanOut(Document document) {
        int maxFanOut = 0;
        NodeList gateways = document.getElementsByTagName("gateway");
        
        for (int i = 0; i < gateways.getLength(); i++) {
            Element gateway = (Element) gateways.item(i);
            NodeList outgoing = gateway.getElementsByTagName("outgoing");
            maxFanOut = Math.max(maxFanOut, outgoing.getLength());
        }
        
        return maxFanOut;
    }
    
    private int countElements(Document document) {
        NodeList allElements = document.getElementsByTagName("*");
        return allElements.getLength();
    }
    
    private double calculateWeightedComplexity(Document document) {
        // Weighted complexity based on element types
        Map<String, Integer> weights = Map.of(
            "task", 1, "userTask", 2, "serviceTask", 2,
            "exclusiveGateway", 2, "parallelGateway", 3, "inclusiveGateway", 3,
            "subProcess", 2
        );
        
        double weightedSum = 0;
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            int count = countElementsByType(document, entry.getKey());
            weightedSum += count * entry.getValue();
        }
        
        return weightedSum;
    }
    
    private double calculateComplexityScore(ComplexityAnalysis analysis) {
        double score = 0;
        score += analysis.getCyclomaticComplexity() * 10;
        score += analysis.getMaxNestingDepth() * 15;
        score += analysis.getMaxFanOut() * 5;
        score += analysis.getElementCount() * 0.1;
        
        return Math.min(score, builder.maxComplexityScore);
    }
    
    private String determineComplexityLevel(double score) {
        if (score < 30) return "LOW";
        if (score < 60) return "MEDIUM";
        if (score < 80) return "HIGH";
        return "VERY_HIGH";
    }
    
    private double calculateNamingCompliance(Document document) {
        NodeList allElements = document.getElementsByTagName("*");
        int totalElements = 0;
        int namedElements = 0;
        
        for (int i = 0; i < allElements.getLength(); i++) {
            Element element = (Element) allElements.item(i);
            String tagName = element.getTagName();
            
            // Считаем только значимые элементы
            if (tagName.contains(":") ? 
                Arrays.asList("bpmn:task", "bpmn:gateway", "bpmn:event", "bpmn:subProcess").contains(tagName) :
                Arrays.asList("task", "gateway", "startEvent", "endEvent", "subProcess").contains(tagName)) {
                
                totalElements++;
                String name = element.getAttribute("name");
                if (name != null && !name.trim().isEmpty()) {
                    namedElements++;
                }
            }
        }
        
        return totalElements > 0 ? (double) namedElements / totalElements * 100 : 100;
    }
    
    private double calculateStandardCompliance(Document document) {
        // Simplified compliance calculation
        double compliance = 100.0;
        
        if (countElementsByType(document, "startEvent") == 0) compliance -= 20;
        if (countElementsByType(document, "endEvent") == 0) compliance -= 20;
        
        return Math.max(compliance, 0);
    }
    
    private boolean isSpaghettiProcess(Document document) {
        int sequenceFlows = countElementsByType(document, "sequenceFlow");
        int tasks = countElementsByType(document, "task");
        
        // Если слишком много связей на задачу, это может быть spaghetti
        return tasks > 0 && (double) sequenceFlows / tasks > 3.0;
    }
    
    private double calculateOverallScore(StructureAnalysisResult result) {
        double score = 100.0;
        
        // Штрафы за violations
        score -= result.getStandardViolations().size() * 10;
        score -= result.getNamingIssues().size() * 5;
        
        // Штрафы за anti-patterns
        for (AntiPattern pattern : result.getAntiPatterns()) {
            switch (pattern.getSeverity()) {
                case "HIGH": score -= 15; break;
                case "MEDIUM": score -= 10; break;
                case "LOW": score -= 5; break;
            }
        }
        
        // Бонус за хорошие метрики
        if (result.getMetrics() != null) {
            score += result.getMetrics().getNamingCompliance() * 0.2;
            score += result.getMetrics().getStandardCompliance() * 0.1;
        }
        
        return Math.max(Math.min(score, 100.0), 0.0);
    }
    
    private List<String> generateRecommendations(StructureAnalysisResult result) {
        List<String> recommendations = new ArrayList<>();
        
        // Рекомендации на основе violations
        for (String violation : result.getStandardViolations()) {
            if (violation.contains("start event")) {
                recommendations.add("Add start event to define process entry point");
            }
            if (violation.contains("end event")) {
                recommendations.add("Add end event to define process completion");
            }
        }
        
        // Рекомендации на основе anti-patterns
        for (AntiPattern pattern : result.getAntiPatterns()) {
            recommendations.addAll(pattern.getRecommendations());
        }
        
        // Рекомендации на основе naming issues
        if (!result.getNamingIssues().isEmpty()) {
            recommendations.add("Improve naming conventions for better maintainability");
        }
        
        // Рекомендации на основе complexity
        if (result.getComplexityLevel().equals("HIGH") || result.getComplexityLevel().equals("VERY_HIGH")) {
            recommendations.add("Consider process refactoring to reduce complexity");
            recommendations.add("Break down complex processes into smaller sub-processes");
        }
        
        return recommendations;
    }
    
    private StructureAnalysisResult createErrorResult(Exception e) {
        StructureAnalysisResult result = new StructureAnalysisResult();
        result.setOverallScore(0.0);
        result.setErrorMessage("Analysis failed: " + e.getMessage());
        result.setRecommendations(Arrays.asList("Fix BPMN XML syntax errors", "Validate against BPMN 2.0 standard"));
        return result;
    }
    
    // Inner classes for results
    public static class StructureAnalysisResult {
        private Map<String, Integer> elementCounts;
        private Map<String, List<String>> elementNames;
        private List<String> standardViolations;
        private List<String> namingIssues;
        private List<AntiPattern> antiPatterns;
        private ComplexityAnalysis complexityAnalysis;
        private BpmnMetrics metrics;
        private double overallScore;
        private String complexityLevel;
        private List<String> recommendations;
        private String errorMessage;
        
        // Constructors
        public StructureAnalysisResult() {
            this.elementCounts = new HashMap<>();
            this.elementNames = new HashMap<>();
            this.standardViolations = new ArrayList<>();
            this.namingIssues = new ArrayList<>();
            this.antiPatterns = new ArrayList<>();
            this.recommendations = new ArrayList<>();
        }
        
        // Getters and setters
        public Map<String, Integer> getElementCounts() { return elementCounts; }
        public void setElementCounts(Map<String, Integer> elementCounts) { this.elementCounts = elementCounts; }
        
        public Map<String, List<String>> getElementNames() { return elementNames; }
        public void setElementNames(Map<String, List<String>> elementNames) { this.elementNames = elementNames; }
        
        public List<String> getStandardViolations() { return standardViolations; }
        public void setStandardViolations(List<String> standardViolations) { this.standardViolations = standardViolations; }
        
        public List<String> getNamingIssues() { return namingIssues; }
        public void setNamingIssues(List<String> namingIssues) { this.namingIssues = namingIssues; }
        
        public List<AntiPattern> getAntiPatterns() { return antiPatterns; }
        public void setAntiPatterns(List<AntiPattern> antiPatterns) { this.antiPatterns = antiPatterns; }
        
        public ComplexityAnalysis getComplexityAnalysis() { return complexityAnalysis; }
        public void setComplexityAnalysis(ComplexityAnalysis complexityAnalysis) { this.complexityAnalysis = complexityAnalysis; }
        
        public BpmnMetrics getMetrics() { return metrics; }
        public void setMetrics(BpmnMetrics metrics) { this.metrics = metrics; }
        
        public double getOverallScore() { return overallScore; }
        public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
        
        public String getComplexityLevel() { return complexityLevel; }
        public void setComplexityLevel(String complexityLevel) { this.complexityLevel = complexityLevel; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
        
        public double getComplexityScore() { return overallScore; }
        public void setComplexityScore(double complexityScore) { this.overallScore = complexityScore; }
        
        public double getNamingCompliance() { return getMetrics() != null ? getMetrics().getNamingCompliance() : 0.0; }
        public void setNamingCompliance(double namingCompliance) {
            if (metrics == null) metrics = new BpmnMetrics();
            metrics.setNamingCompliance(namingCompliance);
        }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class ComplexityAnalysis {
        private int cyclomaticComplexity;
        private int maxNestingDepth;
        private int maxFanOut;
        private int elementCount;
        private double weightedComplexity;
        private double complexityScore;
        private String errorMessage;
        
        public ComplexityAnalysis() {}
        
        public ComplexityAnalysis(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        // Getters and setters
        public int getCyclomaticComplexity() { return cyclomaticComplexity; }
        public void setCyclomaticComplexity(int cyclomaticComplexity) { this.cyclomaticComplexity = cyclomaticComplexity; }
        
        public int getMaxNestingDepth() { return maxNestingDepth; }
        public void setMaxNestingDepth(int maxNestingDepth) { this.maxNestingDepth = maxNestingDepth; }
        
        public int getMaxFanOut() { return maxFanOut; }
        public void setMaxFanOut(int maxFanOut) { this.maxFanOut = maxFanOut; }
        
        public int getElementCount() { return elementCount; }
        public void setElementCount(int elementCount) { this.elementCount = elementCount; }
        
        public double getWeightedComplexity() { return weightedComplexity; }
        public void setWeightedComplexity(double weightedComplexity) { this.weightedComplexity = weightedComplexity; }
        
        public double getComplexityScore() { return complexityScore; }
        public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    public static class AntiPattern {
        private String id;
        private String name;
        private String severity;
        private String description;
        private List<String> recommendations;
        
        public AntiPattern(String id, String name, String severity, String description, List<String> recommendations) {
            this.id = id;
            this.name = name;
            this.severity = severity;
            this.description = description;
            this.recommendations = recommendations;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getSeverity() { return severity; }
        public String getDescription() { return description; }
        public List<String> getRecommendations() { return recommendations; }
    }
    
    public static class BpmnMetrics {
        private String processName;
        private boolean isExecutable;
        private int taskCount;
        private int gatewayCount;
        private int eventCount;
        private int sequenceFlowCount;
        private int cyclomaticComplexity;
        private int nestingDepth;
        private int maxFanOut;
        private double namingCompliance;
        private double standardCompliance;
        private String errorMessage;
        
        public BpmnMetrics() {}
        
        public BpmnMetrics(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        // Getters and setters
        public String getProcessName() { return processName; }
        public void setProcessName(String processName) { this.processName = processName; }
        
        public boolean isExecutable() { return isExecutable; }
        public void setIsExecutable(boolean isExecutable) { this.isExecutable = isExecutable; }
        
        public int getTaskCount() { return taskCount; }
        public void setTaskCount(int taskCount) { this.taskCount = taskCount; }
        
        public int getGatewayCount() { return gatewayCount; }
        public void setGatewayCount(int gatewayCount) { this.gatewayCount = gatewayCount; }
        
        public int getEventCount() { return eventCount; }
        public void setEventCount(int eventCount) { this.eventCount = eventCount; }
        
        public int getSequenceFlowCount() { return sequenceFlowCount; }
        public void setSequenceFlowCount(int sequenceFlowCount) { this.sequenceFlowCount = sequenceFlowCount; }
        
        public int getCyclomaticComplexity() { return cyclomaticComplexity; }
        public void setCyclomaticComplexity(int cyclomaticComplexity) { this.cyclomaticComplexity = cyclomaticComplexity; }
        
        public int getNestingDepth() { return nestingDepth; }
        public void setNestingDepth(int nestingDepth) { this.nestingDepth = nestingDepth; }
        
        public int getMaxFanOut() { return maxFanOut; }
        public void setMaxFanOut(int maxFanOut) { this.maxFanOut = maxFanOut; }
        
        public double getNamingCompliance() { return namingCompliance; }
        public void setNamingCompliance(double namingCompliance) { this.namingCompliance = namingCompliance; }
        
        public double getStandardCompliance() { return standardCompliance; }
        public void setStandardCompliance(double standardCompliance) { this.standardCompliance = standardCompliance; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}