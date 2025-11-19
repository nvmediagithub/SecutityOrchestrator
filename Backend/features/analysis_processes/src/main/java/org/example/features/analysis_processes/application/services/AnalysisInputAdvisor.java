package org.example.features.analysis_processes.application.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.valueobjects.InputRequirement;
import org.example.features.llm.domain.dto.ChatCompletionRequest;
import org.example.features.llm.domain.dto.ChatCompletionResponse;
import org.example.features.llm.domain.services.LLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class AnalysisInputAdvisor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisInputAdvisor.class);

    private final ObjectProvider<LLMService> llmServiceProvider;
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;

    public AnalysisInputAdvisor(ObjectProvider<LLMService> llmServiceProvider) {
        this.llmServiceProvider = llmServiceProvider;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        this.yamlMapper.findAndRegisterModules();
        this.jsonMapper = new ObjectMapper();
    }

    public List<InputRequirement> determineInputs(AnalysisProcess process) {
        List<InputRequirement> inferred = inferFromSpecification(process.getOpenapiSpecPath());
        List<InputRequirement> defaults = defaultInputs();
        List<InputRequirement> merged = merge(defaults, inferred);

        LLMService llmService = llmServiceProvider.getIfAvailable();
        if (llmService == null || !llmService.isAvailable()) {
            return merged;
        }
        List<InputRequirement> llmSuggestions = askLlmForInputs(process, llmService);
        if (llmSuggestions.isEmpty()) {
            return merged;
        }
        return merge(merged, llmSuggestions);
    }

    private List<InputRequirement> merge(List<InputRequirement> base, List<InputRequirement> additions) {
        if (additions == null || additions.isEmpty()) {
            return base;
        }
        Map<String, InputRequirement> combined = new LinkedHashMap<>();
        if (base != null) {
            for (InputRequirement requirement : base) {
                combined.put(requirement.getName(), requirement);
            }
        }
        for (InputRequirement requirement : additions) {
            if (requirement.getName() == null) {
                continue;
            }
            combined.putIfAbsent(requirement.getName(), requirement);
        }
        return new ArrayList<>(combined.values());
    }

    private List<InputRequirement> askLlmForInputs(AnalysisProcess process, LLMService llmService) {
        if (!StringUtils.hasText(process.getOpenapiSpecPath())) {
            return Collections.emptyList();
        }
        try {
            String snippet = readSnippet(process.getOpenapiSpecPath());
            if (!StringUtils.hasText(snippet)) {
                return Collections.emptyList();
            }

            ChatCompletionRequest request = new ChatCompletionRequest();
            request.setModel("analysis-inputs");
            request.setTemperature(0.2d);
            request.setMaxTokens(400);

            String prompt = """
                We are preparing to run automated security tests for process "%s".
                Review the following OpenAPI excerpt and decide which user-provided credentials, tokens or environment parameters are required before the LLM can analyse or execute tests.
                Return JSON array of objects with fields: name (snake_case), label (short title), description, required (true/false).
                Respond with JSON only, no markdown or prose.

                OpenAPI excerpt:
                %s
                """.formatted(process.getName(), snippet);

            request.setMessages(List.of(
                new ChatCompletionRequest.Message("system",
                    "You help an automation engine ask users for the minimum necessary inputs. " +
                        "If nothing is required, return an empty JSON array. " +
                        "When tokens or credentials are needed, describe them precisely."),
                new ChatCompletionRequest.Message("user", prompt)
            ));

            CompletableFuture<ChatCompletionResponse> future = llmService.chatCompletion(request);
            ChatCompletionResponse response = future.join();
            if (response.getChoices() == null || response.getChoices().isEmpty()) {
                return Collections.emptyList();
            }
            String content = response.getChoices().getFirst().getMessage().getContent();
            return jsonMapper.readValue(
                content,
                new TypeReference<List<InputRequirement>>() {}
            );
        } catch (Exception ex) {
            LOGGER.warn("Failed to get input suggestions from LLM: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<InputRequirement> inferFromSpecification(String specPath) {
        if (!StringUtils.hasText(specPath)) {
            return Collections.emptyList();
        }
        try {
            String content = Files.readString(Paths.get(specPath), StandardCharsets.UTF_8);
            Map<String, Object> document = yamlMapper.readValue(
                content,
                new TypeReference<Map<String, Object>>() {}
            );
            Set<InputRequirement> requirements = new LinkedHashSet<>();
            Map<String, Object> paths = safeMap(document.get("paths"));
            if (paths != null) {
                for (Object pathValue : paths.values()) {
                    Map<String, Object> operations = safeMap(pathValue);
                    if (operations == null) {
                        continue;
                    }
                    for (Object operationValue : operations.values()) {
                        Map<String, Object> operation = safeMap(operationValue);
                        if (operation == null) {
                            continue;
                        }
                        List<Object> parameters = safeList(operation.get("parameters"));
                        for (Object paramValue : parameters) {
                            Map<String, Object> parameter = safeMap(paramValue);
                            if (parameter == null) {
                                continue;
                            }
                            boolean required = Boolean.TRUE.equals(parameter.get("required"));
                            if (!required) {
                                continue;
                            }
                            String name = asText(parameter.get("name"));
                            if (!StringUtils.hasText(name)) {
                                continue;
                            }
                            String description = asText(parameter.get("description"));
                            Map<String, Object> schema = safeMap(parameter.get("schema"));
                            if (!StringUtils.hasText(description) && schema != null) {
                                description = asText(schema.get("description"));
                            }
                            InputRequirement requirement = new InputRequirement(
                                name,
                                humanize(name),
                                description,
                                true
                            );
                            requirements.add(requirement);
                        }
                    }
                }
            }

            Map<String, Object> components = safeMap(document.get("components"));
            if (components != null) {
                Map<String, Object> securitySchemes = safeMap(components.get("securitySchemes"));
                if (securitySchemes != null) {
                    for (Map.Entry<String, Object> entry : securitySchemes.entrySet()) {
                        Map<String, Object> scheme = safeMap(entry.getValue());
                        if (scheme == null) {
                            continue;
                        }
                        String type = asText(scheme.get("type"));
                        String name = asText(scheme.get("name"));
                        if ("apiKey".equalsIgnoreCase(type) && StringUtils.hasText(name)) {
                            requirements.add(new InputRequirement(
                                name,
                                humanize(name),
                                "API key required for " + entry.getKey(),
                                true
                            ));
                        }
                        String schemeType = asText(scheme.get("scheme"));
                        if ("http".equalsIgnoreCase(type) && "bearer".equalsIgnoreCase(schemeType)) {
                            requirements.add(new InputRequirement(
                                "bearer_token",
                                "Bearer token",
                                "Token used for Authorization: Bearer ... header",
                                true
                            ));
                        }
                    }
                }
            }

            return requirements.stream().map(InputRequirement::withDefaults).toList();
        } catch (Exception ex) {
            LOGGER.warn("Failed to infer inputs from OpenAPI: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private List<InputRequirement> defaultInputs() {
        return List.of(
            new InputRequirement(
                "base_url",
                "Base URL",
                "Target environment URL, e.g. https://service.example.com",
                true
            )
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> normalized = new LinkedHashMap<>();
            map.forEach((key, val) -> {
                if (key != null) {
                    normalized.put(key.toString(), val);
                }
            });
            return normalized;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Object> safeList(Object value) {
        if (value instanceof List<?> list) {
            return (List<Object>) list;
        }
        return Collections.emptyList();
    }

    private String asText(Object value) {
        return value == null ? "" : value.toString();
    }

    private String humanize(String value) {
        if (!StringUtils.hasText(value)) {
            return "Input";
        }
        String normalized = value.replace('_', ' ').replace('-', ' ');
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String readSnippet(String path) {
        try {
            String content = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
            return content.length() <= 3000 ? content : content.substring(0, 3000);
        } catch (Exception ex) {
            LOGGER.warn("Failed to read OpenAPI snippet {}: {}", path, ex.getMessage());
            return "";
        }
    }
}
