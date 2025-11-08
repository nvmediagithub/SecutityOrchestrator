# BPMN Analysis System - Examples and Tutorials

## Table of Contents

1. [Quick Start Tutorial](#quick-start-tutorial)
2. [Basic BPMN Analysis Example](#basic-bpmn-analysis-example)
3. [Advanced Security Analysis](#advanced-security-analysis)
4. [AI-Enhanced Analysis Tutorial](#ai-enhanced-analysis-tutorial)
5. [Compliance Testing Example](#compliance-testing-example)
6. [End-to-End Workflow Example](#end-to-end-workflow-example)
7. [Performance Analysis Tutorial](#performance-analysis-tutorial)
8. [Custom Rule Development](#custom-rule-development)
9. [Integration with External Systems](#integration-with-external-systems)
10. [Real-world Use Cases](#real-world-use-cases)

## Quick Start Tutorial

### Prerequisites
- BPMN 2.0 file ready for analysis
- SecurityOrchestrator running on `http://localhost:8080`
- API key or authentication configured

### Step 1: Upload Your First BPMN Process

```bash
# Upload a simple BPMN process
curl -X POST http://localhost:8080/api/v1/bpmn/processes \
  -F "file=@simple-approval.bpmn" \
  -F "name=Simple Approval Process" \
  -F "description=A basic approval workflow for testing"
```

**Expected Response:**
```json
{
  "processId": "proc_1234567890abcdef",
  "name": "Simple Approval Process",
  "status": "PARSED",
  "elements": {
    "tasks": 3,
    "gateways": 1,
    "events": 2
  },
  "validation": {
    "isValid": true,
    "warnings": [],
    "errors": []
  }
}
```

### Step 2: Start a Quick Security Analysis

```bash
# Start a quick security analysis
curl -X POST http://localhost:8080/api/v1/bpmn/analysis \
  -H "Content-Type: application/json" \
  -d '{
    "processId": "proc_1234567890abcdef",
    "analysisType": "QUICK"
  }'
```

**Response:**
```json
{
  "analysisId": "analysis_abcdef1234567890",
  "status": "STARTED",
  "estimatedDuration": 120
}
```

### Step 3: Check Analysis Results

```bash
# Get the analysis results
curl http://localhost:8080/api/v1/bpmn/analysis/analysis_abcdef1234567890
```

## Basic BPMN Analysis Example

### Java Implementation

```java
@Component
public class BasicBPMNExample {
    
    private final BPMNAnalysisClient client;
    
    public BasicBPMNExample(BPMNAnalysisClient client) {
        this.client = client;
    }
    
    /**
     * Complete example: Upload, analyze, and process results
     */
    public SecurityReport analyzeBPMNProcess(InputStream bpmnFile) throws Exception {
        
        // Step 1: Upload the process
        ProcessUploadResponse uploadResponse = client.uploadProcess(
            "E-commerce Order Processing",
            "Customer order fulfillment workflow",
            bpmnFile
        );
        
        System.out.println("Process uploaded: " + uploadResponse.getProcessId());
        
        // Step 2: Start comprehensive analysis
        AnalysisRequest analysisRequest = AnalysisRequest.builder()
            .processId(uploadResponse.getProcessId())
            .analysisType(AnalysisType.COMPREHENSIVE)
            .complianceStandards(Arrays.asList("GDPR", "PCI-DSS"))
            .configuration(AnalysisConfiguration.builder()
                .includePerformance(true)
                .includeCompliance(true)
                .aiAssisted(true)
                .build())
            .build();
        
        AnalysisResponse analysisResponse = client.startAnalysis(analysisRequest);
        System.out.println("Analysis started: " + analysisResponse.getAnalysisId());
        
        // Step 3: Wait for completion and get results
        AnalysisResult result = waitForAnalysis(analysisResponse.getAnalysisId());
        
        // Step 4: Process and display results
        return processAnalysisResults(result);
    }
    
    private AnalysisResult waitForAnalysis(String analysisId) {
        int attempts = 0;
        int maxAttempts = 60; // 5 minutes max
        
        while (attempts < maxAttempts) {
            try {
                Thread.sleep(5000); // Wait 5 seconds
                AnalysisResult result = client.getAnalysisResult(analysisId);
                
                if (result.getStatus() == AnalysisStatus.COMPLETED) {
                    return result;
                } else if (result.getStatus() == AnalysisStatus.FAILED) {
                    throw new RuntimeException("Analysis failed: " + result.getErrorMessage());
                }
                
                System.out.println("Analysis in progress... (" + attempts * 5 + "s)");
                attempts++;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Analysis interrupted", e);
            }
        }
        
        throw new RuntimeException("Analysis timeout - took longer than expected");
    }
    
    private SecurityReport processAnalysisResults(AnalysisResult result) {
        System.out.println("\n=== SECURITY ANALYSIS REPORT ===");
        System.out.println("Process: " + result.getProcessId());
        System.out.println("Security Score: " + result.getResults().getSecurityScore() + "%");
        System.out.println("Risk Level: " + result.getResults().getRiskLevel());
        System.out.println("Total Findings: " + result.getResults().getTotalFindings());
        
        // Display findings by severity
        Map<Severity, List<SecurityFinding>> findingsBySeverity = 
            result.getResults().getFindings().stream()
                .collect(Collectors.groupingBy(SecurityFinding::getSeverity));
        
        for (Severity severity : Arrays.asList(Severity.CRITICAL, Severity.HIGH, 
                                             Severity.MEDIUM, Severity.LOW)) {
            List<SecurityFinding> findings = findingsBySeverity.get(severity);
            if (findings != null && !findings.isEmpty()) {
                System.out.println("\n" + severity + " Severity Findings (" + findings.size() + "):");
                for (SecurityFinding finding : findings) {
                    System.out.println("- " + finding.getTitle());
                    System.out.println("  " + finding.getDescription());
                    System.out.println("  Recommendation: " + finding.getRecommendation());
                }
            }
        }
        
        // Display compliance status
        System.out.println("\n=== COMPLIANCE STATUS ===");
        Map<String, ComplianceResult> complianceResults = result.getResults().getComplianceResults();
        for (Map.Entry<String, ComplianceResult> entry : complianceResults.entrySet()) {
            ComplianceResult compliance = entry.getValue();
            System.out.println(entry.getKey() + ": " + compliance.getScore() + "% (" + 
                             compliance.getStatus() + ")");
        }
        
        // Display AI insights
        if (result.getAiInsights() != null) {
            System.out.println("\n=== AI INSIGHTS ===");
            System.out.println("Summary: " + result.getAiInsights().getSummary());
            for (String finding : result.getAiInsights().getKeyFindings()) {
                System.out.println("- " + finding);
            }
        }
        
        return SecurityReport.builder()
            .securityScore(result.getResults().getSecurityScore())
            .riskLevel(result.getResults().getRiskLevel())
            .findings(result.getResults().getFindings())
            .complianceResults(complianceResults)
            .build();
    }
}

// Usage example
@SpringBootApplication
public class BPMNAnalysisExampleApplication implements CommandLineRunner {
    
    @Autowired
    private BasicBPMNExample bpmnExample;
    
    public static void main(String[] args) {
        SpringApplication.run(BPMNAnalysisExampleApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Load BPMN file from resources
        InputStream bpmnFile = getClass().getResourceAsStream("/bpmn/ecommerce-order.bpmn");
        
        if (bpmnFile == null) {
            System.err.println("BPMN file not found");
            return;
        }
        
        try {
            SecurityReport report = bpmnExample.analyzeBPMNProcess(bpmnFile);
            System.out.println("\nAnalysis completed successfully!");
            
            // Save report
            saveReport(report);
            
        } catch (Exception e) {
            System.err.println("Analysis failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## Advanced Security Analysis

### Comprehensive Security Assessment

```java
@Service
public class AdvancedSecurityAnalysisService {
    
    private final BPMNAnalysisClient client;
    private final List<CustomSecurityRule> customRules;
    
    public AdvancedSecurityAnalysisService(BPMNAnalysisClient client, 
                                         List<CustomSecurityRule> customRules) {
        this.client = client;
        this.customRules = customRules;
    }
    
    /**
     * Perform advanced security analysis with custom rules
     */
    public AdvancedSecurityReport performAdvancedAnalysis(String processId) {
        
        // Step 1: Run comprehensive analysis
        AnalysisRequest request = AnalysisRequest.builder()
            .processId(processId)
            .analysisType(AnalysisType.COMPREHENSIVE)
            .complianceStandards(Arrays.asList(
                "GDPR", "PCI-DSS", "HIPAA", "SOX", "ISO27001"
            ))
            .configuration(AnalysisConfiguration.builder()
                .includePerformance(true)
                .includeCompliance(true)
                .includeThreatModeling(true)
                .aiAssisted(true)
                .customRules(customRules.stream()
                    .map(CustomSecurityRule::getRuleId)
                    .collect(Collectors.toList()))
                .build())
            .build();
        
        AnalysisResponse response = client.startAnalysis(request);
        
        // Step 2: Get detailed results
        AnalysisResult result = waitForCompletion(response.getAnalysisId());
        
        // Step 3: Perform additional custom analysis
        CustomSecurityAnalysis customAnalysis = performCustomSecurityAnalysis(processId);
        
        // Step 4: Generate threat model
        ThreatModel threatModel = generateThreatModel(processId);
        
        // Step 5: Create comprehensive report
        return createAdvancedSecurityReport(result, customAnalysis, threatModel);
    }
    
    private CustomSecurityAnalysis performCustomSecurityAnalysis(String processId) {
        // Get process details
        ProcessDetails process = client.getProcessDetails(processId);
        
        // Custom analysis based on process characteristics
        List<CustomSecurityFinding> customFindings = customRules.stream()
            .map(rule -> rule.analyze(process))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        
        return CustomSecurityAnalysis.builder()
            .processId(processId)
            .customFindings(customFindings)
            .analysisTimestamp(Instant.now())
            .build();
    }
    
    private ThreatModel generateThreatModel(String processId) {
        // STRIDE analysis
        List<Threat> threats = new ArrayList<>();
        
        // Spoofing threats
        threats.addAll(identifySpoofingThreats(processId));
        
        // Tampering threats
        threats.addAll(identifyTamperingThreats(processId));
        
        // Repudiation threats
        threats.addAll(identifyRepudiationThreats(processId));
        
        // Information disclosure threats
        threats.addAll(identifyInformationDisclosureThreats(processId));
        
        // Denial of service threats
        threats.addAll(identifyDenialOfServiceThreats(processId));
        
        // Elevation of privilege threats
        threats.addAll(identifyElevationOfPrivilegeThreats(processId));
        
        return ThreatModel.builder()
            .processId(processId)
            .threats(threats)
            .riskScore(calculateOverallThreatRisk(threats))
            .mitigationStrategy(generateMitigationStrategy(threats))
            .build();
    }
    
    private List<Threat> identifySpoofingThreats(String processId) {
        List<Threat> spoofingThreats = new ArrayList<>();
        
        ProcessDetails process = client.getProcessDetails(processId);
        
        // Check for tasks that could be spoofed
        for (UserTask userTask : process.getUserTasks()) {
            if (!userTask.hasStrongAuthentication()) {
                spoofingThreats.add(Threat.builder()
                    .id("spoofing_" + userTask.getId())
                    .type(ThreatType.SPOOFING)
                    .targetElement(userTask.getId())
                    .description("User task '" + userTask.getName() + 
                               "' may be vulnerable to user impersonation")
                    .likelihood(Likelihood.MEDIUM)
                    .impact(Impact.HIGH)
                    .build());
            }
        }
        
        return spoofingThreats;
    }
    
    private List<Threat> identifyInformationDisclosureThreats(String processId) {
        List<Threat> disclosureThreats = new ArrayList<>();
        
        ProcessDetails process = client.getProcessDetails(processId);
        
        // Check for data elements that could be exposed
        for (DataObject dataObject : process.getDataObjects()) {
            if (dataObject.containsSensitiveData() && !dataObject.isEncrypted()) {
                disclosureThreats.add(Threat.builder()
                    .id("disclosure_" + dataObject.getId())
                    .type(ThreatType.INFORMATION_DISCLOSURE)
                    .targetElement(dataObject.getId())
                    .description("Sensitive data in '" + dataObject.getName() + 
                               "' may be exposed without encryption")
                    .likelihood(Likelihood.MEDIUM)
                    .impact(Impact.CRITICAL)
                    .build());
            }
        }
        
        return disclosureThreats;
    }
    
    private AdvancedSecurityReport createAdvancedSecurityReport(
            AnalysisResult result,
            CustomSecurityAnalysis customAnalysis,
            ThreatModel threatModel) {
        
        return AdvancedSecurityReport.builder()
            .processId(result.getProcessId())
            .basicAnalysis(result)
            .customAnalysis(customAnalysis)
            .threatModel(threatModel)
            .overallRiskScore(calculateOverallRisk(result, customAnalysis, threatModel))
            .executiveSummary(generateExecutiveSummary(result, customAnalysis, threatModel))
            .actionableRecommendations(generateActionableRecommendations(
                result, customAnalysis, threatModel))
            .complianceMapping(createComplianceMapping(result))
            .implementationPlan(generateImplementationPlan(result, threatModel))
            .build();
    }
}
```

## AI-Enhanced Analysis Tutorial

### Setting Up AI-Enhanced Analysis

```java
@Configuration
@EnableConfigurationProperties(AIConfigProperties.class)
public class AIAnalysisConfig {
    
    @Bean
    public AIAnalysisService aiAnalysisService(
            BPMNAnalysisClient client,
            AIConfigProperties aiConfig,
            LLMProvider primaryProvider,
            LLMProvider fallbackProvider) {
        
        return new AIAnalysisService(client, aiConfig, primaryProvider, fallbackProvider);
    }
}

@Service
public class AIAnalysisService {
    
    private final BPMNAnalysisClient client;
    private final AIConfigProperties config;
    private final LLMProvider primaryProvider;
    private final LLMProvider fallbackProvider;
    
    public AIAnalysisService(BPMNAnalysisClient client,
                           AIConfigProperties config,
                           LLMProvider primaryProvider,
                           LLMProvider fallbackProvider) {
        this.client = client;
        this.config = config;
        this.primaryProvider = primaryProvider;
        this.fallbackProvider = fallbackProvider;
    }
    
    /**
     * Enhanced analysis with AI assistance
     */
    public AIEnhancedAnalysisResult performAIEnhancedAnalysis(String processId) {
        
        // Step 1: Get basic analysis
        AnalysisResult basicResult = client.getBasicAnalysis(processId);
        
        // Step 2: Extract business context
        BusinessContext context = extractBusinessContext(processId);
        
        // Step 3: Generate AI-powered insights
        List<AIInsight> insights = generateAIInsights(processId, context);
        
        // Step 4: Enhance findings with AI
        List<EnhancedSecurityFinding> enhancedFindings = enhanceFindingsWithAI(
            basicResult.getFindings(), context);
        
        // Step 5: Generate AI recommendations
        List<AIRecommendation> recommendations = generateAIRecommendations(
            processId, context, insights);
        
        return AIEnhancedAnalysisResult.builder()
            .processId(processId)
            .basicResult(basicResult)
            .businessContext(context)
            .aiInsights(insights)
            .enhancedFindings(enhancedFindings)
            .aiRecommendations(recommendations)
            .confidenceScore(calculateOverallConfidence(insights, recommendations))
            .build();
    }
    
    private BusinessContext extractBusinessContext(String processId) {
        ProcessDetails process = client.getProcessDetails(processId);
        
        return BusinessContext.builder()
            .processName(process.getName())
            .domain(identifyBusinessDomain(process))
            .dataTypes(identifyDataTypes(process))
            .stakeholders(identifyStakeholders(process))
            .complianceRequirements(identifyComplianceRequirements(process))
            .riskProfile(assessRiskProfile(process))
            .build();
    }
    
    private List<AIInsight> generateAIInsights(String processId, BusinessContext context) {
        List<AIInsight> insights = new ArrayList<>();
        
        // Generate process complexity insight
        insights.add(generateProcessComplexityInsight(context));
        
        // Generate security risk insight
        insights.add(generateSecurityRiskInsight(context));
        
        // Generate compliance gap insight
        insights.add(generateComplianceGapInsight(context));
        
        // Generate efficiency improvement insight
        insights.add(generateEfficiencyInsight(context));
        
        return insights;
    }
    
    private AIInsight generateProcessComplexityInsight(BusinessContext context) {
        try {
            String prompt = buildComplexityAnalysisPrompt(context);
            
            String aiResponse = callAIWithRetry(prompt);
            
            return AIInsight.builder()
                .id("complexity_insight")
                .type(InsightType.PROCESS_COMPLEXITY)
                .title("Process Complexity Analysis")
                .description(extractInsightDescription(aiResponse))
                .confidence(extractConfidence(aiResponse))
                .recommendations(extractRecommendations(aiResponse))
                .build();
                
        } catch (Exception e) {
            log.warn("Failed to generate complexity insight", e);
            return createFallbackInsight(InsightType.PROCESS_COMPLEXITY);
        }
    }
    
    private String buildComplexityAnalysisPrompt(BusinessContext context) {
        return String.format("""
            Analyze the following business process for complexity and provide insights.
            
            Process Name: %s
            Business Domain: %s
            Data Types: %s
            Number of User Tasks: %d
            Number of Service Tasks: %d
            Number of Gateways: %d
            Compliance Requirements: %s
            
            Please provide:
            1. Overall complexity assessment (Simple/Medium/Complex/Very Complex)
            2. Main complexity drivers
            3. Potential simplification opportunities
            4. Risk implications of current complexity
            
            Format your response as JSON with the following structure:
            {
              "complexityLevel": "Simple|Medium|Complex|Very Complex",
              "complexityDrivers": ["driver1", "driver2", ...],
              "simplificationOpportunities": ["opportunity1", "opportunity2", ...],
              "riskImplications": ["risk1", "risk2", ...],
              "overallAssessment": "Detailed assessment text",
              "confidence": 0.85
            }
            """,
            context.getProcessName(),
            context.getDomain(),
            String.join(", ", context.getDataTypes()),
            context.getUserTaskCount(),
            context.getServiceTaskCount(),
            context.getGatewayCount(),
            String.join(", ", context.getComplianceRequirements())
        );
    }
    
    private String callAIWithRetry(String prompt) {
        int maxRetries = config.getMaxRetries();
        Exception lastException = null;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return primaryProvider.generateCompletion(
                    CompletionRequest.builder()
                        .prompt(prompt)
                        .model(config.getModel())
                        .maxTokens(config.getMaxTokens())
                        .temperature(config.getTemperature())
                        .build()
                ).getContent();
                
            } catch (Exception e) {
                lastException = e;
                log.warn("AI call attempt {} failed: {}", attempt + 1, e.getMessage());
                
                if (attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(1000 * (attempt + 1)); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during AI retry", ie);
                    }
                }
            }
        }
        
        // Try fallback provider if primary fails
        try {
            log.info("Trying fallback AI provider");
            return fallbackProvider.generateCompletion(
                CompletionRequest.builder()
                    .prompt(prompt)
                    .model(config.getFallbackModel())
                    .build()
            ).getContent();
            
        } catch (Exception e) {
            log.error("Both AI providers failed", e);
            throw new AIAnalysisException("AI analysis failed after all retries", lastException);
        }
    }
    
    private List<EnhancedSecurityFinding> enhanceFindingsWithAI(
            List<SecurityFinding> findings, 
            BusinessContext context) {
        
        return findings.stream()
            .map(finding -> enhanceFindingWithAI(finding, context))
            .collect(Collectors.toList());
    }
    
    private EnhancedSecurityFinding enhanceFindingWithAI(
            SecurityFinding finding, 
            BusinessContext context) {
        
        try {
            String prompt = buildFindingEnhancementPrompt(finding, context);
            String aiResponse = callAIWithRetry(prompt);
            
            EnhancedSecurityEnhancement enhancement = parseEnhancementResponse(aiResponse);
            
            return EnhancedSecurityFinding.builder()
                .baseFinding(finding)
                .aiEnhancement(enhancement)
                .businessImpact(enhancement.getBusinessImpact())
                .technicalComplexity(enhancement.getTechnicalComplexity())
                .implementationGuidance(enhancement.getImplementationGuidance())
                .build();
                
        } catch (Exception e) {
            log.warn("Failed to enhance finding with AI: {}", finding.getId(), e);
            
            return EnhancedSecurityFinding.builder()
                .baseFinding(finding)
                .aiEnhancement(EnhancedSecurityEnhancement.builder()
                    .businessImpact("Unable to determine with AI")
                    .technicalComplexity("Analysis unavailable")
                    .implementationGuidance("Manual analysis required")
                    .build())
                .build();
        }
    }
}
```

## Compliance Testing Example

### GDPR Compliance Analysis

```java
@Service
public class GDPRComplianceService {
    
    private final BPMNAnalysisClient client;
    private final GDPRComplianceRules gdprRules;
    
    public GDPRComplianceService(BPMNAnalysisClient client) {
        this.client = client;
        this.gdprRules = new GDPRComplianceRules();
    }
    
    /**
     * Perform GDPR compliance analysis
     */
    public GDPRComplianceReport analyzeGDPRCompliance(String processId) {
        
        // Step 1: Get process details
        ProcessDetails process = client.getProcessDetails(processId);
        
        // Step 2: Analyze data processing activities
        List<DataProcessingActivity> activities = identifyDataProcessingActivities(process);
        
        // Step 3: Check GDPR compliance requirements
        Map<String, List<GDPRRequirement>> complianceResults = new HashMap<>();
        
        for (DataProcessingActivity activity : activities) {
            List<GDPRRequirement> requirements = gdprRules.checkRequirements(activity);
            complianceResults.put(activity.getActivityId(), requirements);
        }
        
        // Step 4: Identify data subject rights
        List<DataSubjectRightAnalysis> rightsAnalysis = analyzeDataSubjectRights(process);
        
        // Step 5: Check privacy by design
        List<PrivacyByDesignCheck> designChecks = performPrivacyByDesignChecks(process);
        
        // Step 6: Assess data breach risks
        List<DataBreachRisk> breachRisks = assessDataBreachRisks(process);
        
        return GDPRComplianceReport.builder()
            .processId(processId)
            .complianceScore(calculateComplianceScore(complianceResults))
            .dataProcessingActivities(activities)
            .complianceResults(complianceResults)
            .dataSubjectRightsAnalysis(rightsAnalysis)
            .privacyByDesignChecks(designChecks)
            .dataBreachRisks(breachRisks)
            .recommendations(generateGDPRRecommendations(
                activities, rightsAnalysis, designChecks, breachRisks))
            .build();
    }
    
    private List<DataProcessingActivity> identifyDataProcessingActivities(ProcessDetails process) {
        List<DataProcessingActivity> activities = new ArrayList<>();
        
        for (UserTask userTask : process.getUserTasks()) {
            DataProcessingActivity activity = DataProcessingActivity.builder()
                .activityId(userTask.getId())
                .name(userTask.getName())
                .type(ProcessingType.COLLECTION)
                .dataTypes(extractDataTypes(userTask))
                .dataSubjects(identifyDataSubjects(userTask))
                .processingPurpose(userTask.getBusinessPurpose())
                .legalBasis(determineLegalBasis(userTask))
                .build();
            
            activities.add(activity);
        }
        
        for (ServiceTask serviceTask : process.getServiceTasks()) {
            if (serviceTask.processesPersonalData()) {
                DataProcessingActivity activity = DataProcessingActivity.builder()
                    .activityId(serviceTask.getId())
                    .name(serviceTask.getName())
                    .type(ProcessingType.PROCESSING)
                    .dataTypes(extractDataTypes(serviceTask))
                    .dataSubjects(identifyDataSubjects(serviceTask))
                    .processingPurpose(serviceTask.getBusinessPurpose())
                    .legalBasis(determineLegalBasis(serviceTask))
                    .thirdPartyInvolved(serviceTask.involvesThirdParty())
                    .build();
                
                activities.add(activity);
            }
        }
        
        return activities;
    }
    
    private GDPRRequirement checkArticle6Lawfulness(UserTask userTask) {
        List<LegalBasis> applicableBases = new ArrayList<>();
        
        if (userTask.hasConsent()) {
            applicableBases.add(LegalBasis.CONSENT);
        }
        
        if (userTask.isContractualNecessity()) {
            applicableBases.add(LegalBasis.CONTRACT);
        }
        
        if (userTask.hasLegitimateInterest()) {
            applicableBases.add(LegalBasis.LEGITIMATE_INTEREST);
        }
        
        return GDPRRequirement.builder()
            .article("Article 6")
            .title("Lawfulness of processing")
            .requirement("Processing must have a legal basis under Article 6(1)")
            .status(applicableBases.isEmpty() ? ComplianceStatus.NON_COMPLIANT : ComplianceStatus.COMPLIANT)
            .applicableBases(applicableBases)
            .evidence(userTask.getLegalBasisEvidence())
            .build();
    }
    
    private GDPRRequirement checkArticle13Information(UserTask userTask) {
        boolean hasInformationDuty = userTask.hasInformationDuty();
        
        return GDPRRequirement.builder()
            .article("Article 13")
            .title("Information to be provided")
            .requirement("Data subjects must be informed about processing at collection")
            .status(hasInformationDuty ? ComplianceStatus.COMPLIANT : ComplianceStatus.NON_COMPLIANT)
            .missingElements(hasInformationDuty ? List.of() : 
                List.of("Privacy notice", "Contact information", "Data retention period"))
            .build();
    }
    
    private DataSubjectRightAnalysis analyzeDataSubjectRights(ProcessDetails process) {
        List<DataSubjectRight> rights = Arrays.asList(
            DataSubjectRight.ACCESS,
            DataSubjectRight.RECTIFICATION,
            DataSubjectRight.ERASURE,
            DataSubjectRight.PORTABILITY,
            DataSubjectRight.OBJECT,
            DataSubjectRight.RESTRICTION
        );
        
        Map<DataSubjectRight, RightImplementation> implementations = new HashMap<>();
        
        for (DataSubjectRight right : rights) {
            RightImplementation implementation = analyzeRightImplementation(process, right);
            implementations.put(right, implementation);
        }
        
        return DataSubjectRightAnalysis.builder()
            .processId(process.getId())
            .rightsImplementation(implementations)
            .overallComplianceScore(calculateRightsComplianceScore(implementations))
            .gaps(identifyRightsGaps(implementations))
            .build();
    }
    
    private List<GDPRRecommendation> generateGDPRRecommendations(
            List<DataProcessingActivity> activities,
            List<DataSubjectRightAnalysis> rightsAnalysis,
            List<PrivacyByDesignCheck> designChecks,
            List<DataBreachRisk> breachRisks) {
        
        List<GDPRRecommendation> recommendations = new ArrayList<>();
        
        // Analyze processing activities
        for (DataProcessingActivity activity : activities) {
            if (activity.getLegalBasis().isEmpty()) {
                recommendations.add(GDPRRecommendation.builder()
                    .category("Legal Basis")
                    .priority("HIGH")
                    .title("Establish legal basis for " + activity.getName())
                    .description("Define and document the legal basis for processing personal data")
                    .article("Article 6")
                    .action("Document legal basis and update privacy notice")
                    .build());
            }
        }
        
        // Analyze data subject rights
        for (DataSubjectRightAnalysis analysis : rightsAnalysis) {
            for (Map.Entry<DataSubjectRight, RightImplementation> entry : 
                 analysis.getRightsImplementation().entrySet()) {
                
                if (entry.getValue().getStatus() == ComplianceStatus.NON_COMPLIANT) {
                    recommendations.add(GDPRRecommendation.builder()
                        .category("Data Subject Rights")
                        .priority("HIGH")
                        .title("Implement " + entry.getKey() + " right")
                        .description("Add mechanism for data subjects to exercise their " + 
                                   entry.getKey() + " right")
                        .article("Articles 15-22")
                        .action("Implement data subject request handling process")
                        .build());
                }
            }
        }
        
        // Analyze privacy by design
        for (PrivacyByDesignCheck check : designChecks) {
            if (check.getStatus() == ComplianceStatus.NON_COMPLIANT) {
                recommendations.add(GDPRRecommendation.builder()
                    .category("Privacy by Design")
                    .priority("MEDIUM")
                    .title("Implement " + check.getPrinciple() + " principle")
                    .description(check.getDescription())
                    .article("Article 25")
                    .action("Redesign process to incorporate " + check.getPrinciple() + 
                           " principle")
                    .build());
            }
        }
        
        return recommendations;
    }
}
```

This comprehensive examples and tutorials guide provides practical, real-world examples of how to use the BPMN Analysis System effectively. The examples progress from basic usage to advanced AI-enhanced analysis and compliance testing, providing a complete learning path for users at all levels.