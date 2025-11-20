package org.example.features.analysis_processes.application.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.llm.domain.dto.ChatCompletionRequest;
import org.example.features.llm.domain.dto.ChatCompletionResponse;
import org.example.features.llm.domain.services.LLMService;
import org.example.features.analysis_processes.domain.valueobjects.HttpRequestStep;
import org.example.features.analysis_processes.domain.valueobjects.InputRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

@Component
public class ProcessAnalysisPlanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessAnalysisPlanner.class);

    private final ObjectProvider<LLMService> llmServiceProvider;
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;
    private final boolean allowFallback;

    public ProcessAnalysisPlanner(
        ObjectProvider<LLMService> llmServiceProvider,
        @Value("${analysis.planner.allow-fallback:true}") boolean allowFallback
    ) {
        this.llmServiceProvider = llmServiceProvider;
        this.allowFallback = allowFallback;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.findAndRegisterModules();
        this.jsonMapper = new ObjectMapper();
    }

    public PlanResult generatePlan(AnalysisProcess process, Map<String, Object> sessionContext) {
        Map<String, Object> userInputs = extractUserInputs(sessionContext);
        String baseUrl = valueOrDefault(userInputs.get("baseUrl"), "http://localhost:8080");
        String authToken = valueOrDefault(userInputs.get("authToken"), "token-placeholder");

        String bpmnSnippet = readSnippet(process.getBpmnDiagramPath());
        OpenApiSummary openApiSummary = summarizeOpenApi(process.getOpenapiSpecPath());
        String primaryEndpoint = openApiSummary.endpoints().isEmpty()
            ? "/actuator/health"
            : openApiSummary.endpoints().getFirst();

        Optional<PlanResult> llmPlan = tryGenerateWithLlm(
            process,
            userInputs,
            bpmnSnippet,
            openApiSummary,
            primaryEndpoint
        );
        if (llmPlan.isPresent()) {
            return llmPlan.get();
        }
        if (!allowFallback) {
            throw new IllegalStateException(
                "LLM planner is unavailable. Configure an active provider or enable analysis.planner.allow-fallback.");
        }
        LOGGER.warn("Falling back to heuristic plan because LLM provider was unavailable");
        return fallbackPlan(process, baseUrl, authToken, bpmnSnippet, openApiSummary, primaryEndpoint);
    }

    private Optional<PlanResult> tryGenerateWithLlm(
        AnalysisProcess process,
        Map<String, Object> userInputs,
        String bpmnSnippet,
        OpenApiSummary summary,
        String defaultEndpoint
    ) {
        LLMService llmService = llmServiceProvider.getIfAvailable();
        if (llmService == null || !llmService.isAvailable()) {
            return Optional.empty();
        }

        try {
            ChatCompletionRequest request = new ChatCompletionRequest();
            request.setModel("analysis-planner");
            request.setTemperature(0.2d);
            request.setMaxTokens(600);

            String prompt = buildPrompt(process, userInputs, bpmnSnippet, summary);
            request.setMessages(List.of(
                new ChatCompletionRequest.Message("system",
                    "You are a security automation expert. "
                        + "Return JSON with fields plan (markdown), httpRequests (array of HTTP steps), summary (short text)."),
                new ChatCompletionRequest.Message("user", prompt)
            ));

            ChatCompletionResponse response = llmService.chatCompletion(request).join();
            if (response.getChoices() == null || response.getChoices().isEmpty()) {
                return Optional.empty();
            }
            String content = response.getChoices().get(0).getMessage().getContent();
            Map<String, Object> parsed = parseJsonContent(content)
                .orElseGet(() -> Map.of("plan", content));
            String planText = asText(parsed.getOrDefault("plan", content));
            String summaryText = asText(parsed.getOrDefault("summary", ""));
        List<ActionItem> actions = parseActionItems(parsed.get("actions"));
        if (actions.isEmpty()) {
            actions = deriveActionItems(planText, defaultEndpoint);
        }
        List<TestAssertion> assertions = parseAssertions(parsed.get("assertions"));
        if (assertions.isEmpty()) {
            assertions = defaultAssertions(defaultEndpoint);
        }
        HttpPlan httpPlan = parseHttpRequests(parsed.get("httpRequests"), defaultEndpoint);
        boolean requiresAdditionalInput = parseRequiresAdditionalInput(parsed, httpPlan.additionalInputs());
        List<InputRequirement> rootAdditionalInputs = parseInputRequirements(parsed.get("additionalInputs"));
        if (rootAdditionalInputs.isEmpty()) {
            rootAdditionalInputs = parseInputRequirements(parsed.get("inputFields"));
        }
        List<HttpRequestStep> httpRequests = httpPlan.steps();
        return Optional.of(new PlanResult(
            planText,
            summaryText,
            actions,
            assertions,
            httpRequests,
            prompt,
            content,
            requiresAdditionalInput,
            rootAdditionalInputs,
            httpPlan.additionalInputs()
        ));
        } catch (Exception e) {
            LOGGER.warn("LLM plan generation failed, falling back to heuristics: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private PlanResult fallbackPlan(
        AnalysisProcess process,
        String baseUrl,
        String authToken,
        String bpmnSnippet,
        OpenApiSummary summary,
        String selectedEndpoint
    ) {
        StringJoiner plan = new StringJoiner("\n");
        plan.add(String.format(
            "1. Perform a static review of the BPMN diagram \"%s\" and ensure every gateway/task has a documented owner.",
            process.getName()
        ));
        if (!summary.endpoints().isEmpty()) {
            plan.add("2. Probe the most critical OpenAPI endpoints: "
                + String.join(", ", summary.endpoints()));
        } else {
            plan.add("2. Hit the primary API endpoint and verify that authentication/authorization responses are sane.");
        }
        plan.add("3. Capture the execution result and prepare remediation notes for the security team.");

        String summaryText = "BPMN snippet: " + truncate(bpmnSnippet, 200)
            + "; OpenAPI endpoints analyzed: " + summary.endpoints().size();

        List<ActionItem> actions = List.of(
            new ActionItem("BPMN consistency review", "Compare swimlanes vs. deployment reality", process.getBpmnDiagramName()),
            new ActionItem("OpenAPI surface scan", "Focus on top endpoints: "
                + (summary.endpoints().isEmpty() ? "n/a" : summary.endpoints()),
                summary.title()),
            new ActionItem("Prepare smoke payloads", "Reuse supplied auth token to hit " + selectedEndpoint, baseUrl)
        );
        List<TestAssertion> assertions = defaultAssertions(selectedEndpoint);
        List<HttpRequestStep> httpRequests = List.of(
            new HttpRequestStep(
                "Smoke test endpoint",
                "GET",
                selectedEndpoint.startsWith("http") ? selectedEndpoint : baseUrl + selectedEndpoint,
                Map.of("Authorization", "Bearer " + authToken, "Content-Type", "application/json"),
                null,
                "Check the primary endpoint reported by the analysis"
            )
        );

        List<List<InputRequirement>> inputLists = defaultHttpInputLists(httpRequests.size());
        return new PlanResult(
            plan.toString(),
            summaryText,
            actions,
            assertions,
            httpRequests,
            "",
            "",
            false,
            List.of(),
            inputLists
        );
    }

    private String buildPrompt(
        AnalysisProcess process,
        Map<String, Object> userInputs,
        String bpmnSnippet,
        OpenApiSummary summary
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("Process name: ").append(process.getName()).append("\n");
        builder.append("Description: ").append(process.getDescription()).append("\n");
        builder.append("Created at: ").append(process.getCreatedAt() != null
            ? process.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            : "n/a").append("\n\n");

        builder.append("User inputs:\n");
        userInputs.forEach((k, v) -> builder.append("- ").append(k).append(": ").append(v).append("\n"));

        builder.append("\nBPMN excerpt:\n").append(truncate(bpmnSnippet, 1500)).append("\n\n");
        builder.append("OpenAPI endpoints:\n");
        summary.endpoints().forEach(path -> builder.append("- ").append(path).append("\n"));

        builder.append("\nRequired output: JSON with keys plan (markdown list of actions), httpRequests "
            + "(array of HTTP steps containing name, method, url, headers, body, description), summary (short text). "
            + "Use user inputs to populate headers or payloads as needed.\n");
        return builder.toString();
    }

    private Map<String, Object> extractUserInputs(Map<String, Object> context) {
        if (context == null) {
            return Collections.emptyMap();
        }
        Object raw = context.get("userInputs");
        if (raw instanceof Map<?, ?> rawMap) {
            Map<String, Object> normalized = new java.util.HashMap<>();
            rawMap.forEach((key, value) -> {
                if (key != null) {
                    normalized.put(key.toString(), value);
                }
            });
            return normalized;
        }
        return Collections.emptyMap();
    }

    private String readSnippet(String path) {
        if (!StringUtils.hasText(path)) {
            return "BPMN diagram not uploaded.";
        }
        try {
            return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn("Failed to read BPMN diagram {}: {}", path, e.getMessage());
            return "Unable to read BPMN diagram.";
        }
    }

    private OpenApiSummary summarizeOpenApi(String path) {
        if (!StringUtils.hasText(path)) {
            return new OpenApiSummary(List.of(), "n/a", "n/a");
        }

        try {
            Path specPath = Paths.get(path);
            if (Files.notExists(specPath)) {
                return new OpenApiSummary(List.of(), "n/a", "n/a");
            }
            String content = Files.readString(specPath, StandardCharsets.UTF_8);
            Map<String, Object> document = yamlMapper.readValue(
                content,
                new TypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> paths = safeCast(document.get("paths"));

            List<String> endpoints = new ArrayList<>();
            if (paths != null) {
                for (Map.Entry<String, Object> entry : paths.entrySet()) {
                    endpoints.add(entry.getKey());
                    if (endpoints.size() >= 5) {
                        break;
                    }
                }
            }

            Map<String, Object> info = safeCast(document.get("info"));
            String version = info != null && info.get("version") != null ? info.get("version").toString() : "n/a";
            String title = info != null && info.get("title") != null ? info.get("title").toString() : "n/a";

            return new OpenApiSummary(endpoints, version, title);

        } catch (Exception e) {
            LOGGER.warn("Failed to parse OpenAPI spec {}: {}", path, e.getMessage());
            return new OpenApiSummary(List.of(), "n/a", "n/a");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeCast(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new java.util.HashMap<>();
            map.forEach((key, val) -> {
                if (key != null) {
                    result.put(key.toString(), val);
                }
            });
            return result;
        }
        return null;
    }

    private String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }

    public record PlanResult(
        String plan,
        String summary,
        List<ActionItem> actions,
        List<TestAssertion> assertions,
        List<HttpRequestStep> httpRequests,
        String prompt,
        String rawResponse,
        boolean requiresAdditionalInput,
        List<InputRequirement> additionalInputs,
        List<List<InputRequirement>> httpAdditionalInputs
    ) {}

    private record HttpPlan(
        List<HttpRequestStep> steps,
        List<List<InputRequirement>> additionalInputs
    ) {}

    public record ActionItem(String title, String detail, String relatedArtifact) {}

    public record TestAssertion(String check, String expected) {}

    private record OpenApiSummary(List<String> endpoints, String version, String title) {}

    private String asText(Object value) {
        return value == null ? "" : value.toString();
    }

    private String valueOrDefault(Object value, String fallback) {
        String text = value == null ? "" : value.toString();
        return text.isBlank() ? fallback : text;
    }

    private Map<String, Object> normalizeRecord(Map<?, ?> source) {
        Map<String, Object> normalized = new java.util.HashMap<>();
        source.forEach((key, val) -> {
            if (key != null) {
                normalized.put(key.toString(), val);
            }
        });
        return normalized;
    }

    private boolean parseRequiresAdditionalInput(
        Map<String, Object> parsed,
        List<List<InputRequirement>> httpInputs
    ) {
        if (Boolean.TRUE.equals(parsed.get("requires_additional_input"))
            || Boolean.TRUE.equals(parsed.get("requiresAdditionalInput"))) {
            return true;
        }
        if (!parseInputRequirements(parsed.get("additionalInputs")).isEmpty()
            || !parseInputRequirements(parsed.get("inputFields")).isEmpty()) {
            return true;
        }
        return httpInputs.stream().anyMatch(inputs -> inputs != null && !inputs.isEmpty());
    }

    private List<InputRequirement> parseInputRequirements(Object source) {
        if (!(source instanceof List<?> list)) {
            return List.of();
        }
        List<InputRequirement> requirements = new ArrayList<>();
        for (Object entry : list) {
            if (entry instanceof Map<?, ?> mapEntry) {
                Map<String, Object> normalized = normalizeRecord(mapEntry);
                String name = asText(normalized.get("name"));
                if (!StringUtils.hasText(name)) {
                    continue;
                }
                String label = asText(normalized.get("label"));
                String description = asText(normalized.get("description"));
                boolean required = Boolean.TRUE.equals(normalized.get("required"));
                requirements.add(new InputRequirement(name, label, description, required).withDefaults());
            }
        }
        return requirements;
    }

    @SuppressWarnings("unchecked")
    private HttpPlan parseHttpRequests(Object source, String defaultEndpoint) {
        if (!(source instanceof List<?> list)) {
            List<HttpRequestStep> defaultSteps = defaultHttpRequests(defaultEndpoint);
            return new HttpPlan(defaultSteps, defaultHttpInputLists(defaultSteps.size()));
        }
        List<HttpRequestStep> steps = new ArrayList<>();
        List<List<InputRequirement>> additionalInputs = new ArrayList<>();
        for (Object entry : list) {
            if (entry instanceof Map<?, ?> map) {
                Map<String, Object> normalized = normalizeRecord(map);
                String name = asText(normalized.getOrDefault("name", normalized.get("title")));
                if (!StringUtils.hasText(name)) {
                    name = "HTTP step " + (steps.size() + 1);
                }
                String method = asText(normalized.getOrDefault("method", "get")).toUpperCase();
                String url = asText(normalized.getOrDefault("url", normalized.get("endpoint")));
                if (!StringUtils.hasText(url)) {
                    url = defaultEndpoint;
                }
                Map<String, Object> headersRaw = safeCast(normalized.get("headers"));
                Map<String, String> headers = new java.util.HashMap<>();
                if (headersRaw != null) {
                    headersRaw.forEach((k, v) -> {
                        if (k != null && v != null) {
                            headers.put(k.toString(), v.toString());
                        }
                    });
                }
                String body = asText(normalized.get("body"));
                String description = asText(normalized.get("description"));
                steps.add(new HttpRequestStep(
                    name,
                    method,
                    url,
                    headers,
                    StringUtils.hasText(body) ? body : null,
                    description
                ));
                List<InputRequirement> inputs = parseInputRequirements(normalized.get("additionalInputs"));
                if (inputs.isEmpty()) {
                    inputs = parseInputRequirements(normalized.get("inputFields"));
                }
                additionalInputs.add(inputs);
            }
        }
        if (steps.isEmpty()) {
            List<HttpRequestStep> defaultSteps = defaultHttpRequests(defaultEndpoint);
            return new HttpPlan(defaultSteps, defaultHttpInputLists(defaultSteps.size()));
        }
        while (additionalInputs.size() < steps.size()) {
            additionalInputs.add(List.of());
        }
        return new HttpPlan(steps, additionalInputs);
    }

    private List<HttpRequestStep> defaultHttpRequests(String defaultEndpoint) {
        if (!StringUtils.hasText(defaultEndpoint)) {
            return List.of();
        }
        return List.of(new HttpRequestStep(
            "Default endpoint check",
            "GET",
            defaultEndpoint,
            Map.of("Content-Type", "application/json"),
            null,
            "Basic connectivity check"
        ));
    }

    private List<List<InputRequirement>> defaultHttpInputLists(int count) {
        List<List<InputRequirement>> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(List.of());
        }
        return result;
    }

    private Optional<Map<String, Object>> parseJsonContent(String content) {
        if (!StringUtils.hasText(content)) {
            return Optional.empty();
        }
        Optional<Map<String, Object>> attempt = tryParse(content);
        if (attempt.isPresent()) {
            return attempt;
        }
        int first = content.indexOf('{');
        int last = content.lastIndexOf('}');
        if (first >= 0 && last > first) {
            return tryParse(content.substring(first, last + 1));
        }
        LOGGER.warn("Unable to parse LLM response as JSON: {}", truncate(content, 256));
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<Map<String, Object>> tryParse(String text) {
        try {
            Map<String, Object> parsed = jsonMapper.readValue(
                text,
                new TypeReference<Map<String, Object>>() {}
            );
            return Optional.of(parsed);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<ActionItem> parseActionItems(Object source) {
        if (!(source instanceof List<?> list)) {
            return List.of();
        }
        List<ActionItem> actions = new ArrayList<>();
        for (Object entry : list) {
            if (entry instanceof Map<?, ?> map) {
                Map<String, Object> normalized = normalizeRecord(map);
                String title = asText(normalized.getOrDefault("title", normalized.get("name"))).trim();
                String detail = asText(normalized.getOrDefault("detail", normalized.get("description"))).trim();
                String artifact = asText(normalized.getOrDefault("artifact", normalized.get("context"))).trim();
                if (!title.isEmpty() || !detail.isEmpty()) {
                    actions.add(new ActionItem(
                        title.isEmpty() ? detail : title,
                        detail.isEmpty() ? title : detail,
                        artifact
                    ));
                }
            }
        }
        return actions;
    }

    private List<TestAssertion> parseAssertions(Object source) {
        if (!(source instanceof List<?> list)) {
            return List.of();
        }
        List<TestAssertion> assertions = new ArrayList<>();
        for (Object entry : list) {
            if (entry instanceof Map<?, ?> map) {
                Map<String, Object> normalized = normalizeRecord(map);
                String check = asText(normalized.getOrDefault("check", normalized.get("name"))).trim();
                String expected = asText(normalized.getOrDefault("expected", normalized.get("result"))).trim();
                if (!check.isEmpty() || !expected.isEmpty()) {
                    assertions.add(new TestAssertion(
                        check.isEmpty() ? "LLM validation" : check,
                        expected.isEmpty() ? "LLM expectation not provided" : expected
                    ));
                }
            }
        }
        return assertions;
    }

    private List<ActionItem> deriveActionItems(String planText, String endpoint) {
        if (!StringUtils.hasText(planText)) {
            return List.of();
        }
        List<ActionItem> actions = new ArrayList<>();
        for (String rawLine : planText.split("\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            line = line.replaceFirst("^\\d+\\.\\s*", "");
            String[] parts = line.split(":", 2);
            String title = parts[0].trim();
            String detail = parts.length > 1 ? parts[1].trim() : line;
            actions.add(new ActionItem(
                title.isEmpty() ? "Action " + (actions.size() + 1) : title,
                detail,
                endpoint
            ));
            if (actions.size() >= 5) {
                break;
            }
        }
        return actions;
    }

    private List<TestAssertion> defaultAssertions(String endpoint) {
        return List.of(
            new TestAssertion("Endpoint responds with 2xx", "Call completes successfully for " + endpoint),
            new TestAssertion("Response body is captured", "Payload is logged for triage"),
            new TestAssertion("Auth token accepted", "No HTTP 401 for configured Authorization header")
        );
    }
}
