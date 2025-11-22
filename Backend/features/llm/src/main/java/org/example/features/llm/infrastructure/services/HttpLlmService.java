package org.example.features.llm.infrastructure.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.features.llm.domain.dto.ChatCompletionRequest;
import org.example.features.llm.domain.dto.ChatCompletionResponse;
import org.example.features.llm.domain.services.LLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP-based implementation that can talk to local Ollama or OpenRouter style providers.
 */
@Service
public class HttpLlmService implements LLMService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpLlmService.class);

    private final LlmProviderRegistry registry;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Duration requestTimeout;

    public HttpLlmService(
        LlmProviderRegistry registry,
        @Value("${llm.service.timeout-seconds:190}") long timeoutSeconds
    ) {
        this.registry = registry;
        this.requestTimeout = Duration.ofSeconds(Math.max(5, timeoutSeconds));
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    @Override
    public boolean isAvailable() {
        return registry.getActiveProvider()
            .filter(LlmProviderRegistry.ProviderDescriptor::enabled)
            .isPresent();
    }

    @Override
    public CompletableFuture<ChatCompletionResponse> chatCompletion(ChatCompletionRequest request) {
        Objects.requireNonNull(request, "Chat completion request cannot be null");
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            return CompletableFuture.failedFuture(
                new IllegalArgumentException("Chat completion request must include at least one message")
            );
        }

        LlmProviderRegistry.ProviderDescriptor descriptor = registry.getActiveProvider()
            .orElseThrow(() -> new IllegalStateException("No active LLM provider configured"));

        if (!descriptor.enabled()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("Active LLM provider is disabled")
            );
        }

        String effectiveModel = resolveModel(request.getModel(), descriptor.model());
        boolean isLocal = "local".equalsIgnoreCase(descriptor.mode());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Dispatching chat completion to {} ({}) with model {}",
                descriptor.id(), descriptor.mode(), effectiveModel);
        }

        return isLocal
            ? invokeOllama(descriptor, request, effectiveModel)
            : invokeOpenAiCompatible(descriptor, request, effectiveModel);
    }

    @Override
    public String getProviderName() {
        return registry.getActiveProvider()
            .map(LlmProviderRegistry.ProviderDescriptor::displayName)
            .orElse("n/a");
    }

    @Override
    public String getStatus() {
        return isAvailable() ? "ready" : "unavailable";
    }

    @Override
    public String[] getSupportedModels() {
        return registry.getActiveProvider()
            .map(descriptor -> new String[] { descriptor.model() })
            .orElseGet(() -> new String[0]);
    }

    private CompletableFuture<ChatCompletionResponse> invokeOpenAiCompatible(
        LlmProviderRegistry.ProviderDescriptor descriptor,
        ChatCompletionRequest request,
        String model
    ) {
        String url = normalizeBase(descriptor.baseUrl()) + "/chat/completions";
        ObjectNode payload = buildOpenAiPayload(request, model);

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
            .timeout(requestTimeout)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json");

        String apiKey = resolveApiKey(descriptor.apiKey());
        if (StringUtils.hasText(apiKey)) {
            builder.header("Authorization", "Bearer " + apiKey);
        }

        HttpRequest httpRequest = builder
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
            .build();

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenApply(response -> {
                ensureSuccess(response);
                return parseOpenAiResponse(response.body(), model);
            });
    }

    private CompletableFuture<ChatCompletionResponse> invokeOllama(
        LlmProviderRegistry.ProviderDescriptor descriptor,
        ChatCompletionRequest request,
        String model
    ) {
        String url = normalizeBase(descriptor.baseUrl()) + "/api/chat";
        ObjectNode payload = buildOllamaPayload(request, model);

        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url))
            .timeout(requestTimeout)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
            .build();

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenApply(response -> {
                ensureSuccess(response);
                return parseOllamaResponse(response.body(), model);
            });
    }

    private ObjectNode buildOpenAiPayload(ChatCompletionRequest request, String model) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("model", model);
        node.set("messages", toMessageArray(request.getMessages()));

        if (request.getTemperature() != null) {
            node.put("temperature", request.getTemperature());
        }
        if (request.getTopP() != null) {
            node.put("top_p", request.getTopP());
        }
        if (request.getMaxTokens() != null) {
            node.put("max_tokens", request.getMaxTokens());
        }
        if (request.getFrequencyPenalty() != null) {
            node.put("frequency_penalty", request.getFrequencyPenalty());
        }
        if (request.getPresencePenalty() != null) {
            node.put("presence_penalty", request.getPresencePenalty());
        }
        if (request.getStop() != null && !request.getStop().isEmpty()) {
            node.set("stop", objectMapper.valueToTree(request.getStop()));
        }
        if (StringUtils.hasText(request.getUser())) {
            node.put("user", request.getUser());
        }
        node.put("stream", false);
        return node;
    }

    private ObjectNode buildOllamaPayload(ChatCompletionRequest request, String model) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("model", model);
        node.set("messages", toMessageArray(request.getMessages()));
        node.put("stream", false);

        ObjectNode options = objectMapper.createObjectNode();
        if (request.getTemperature() != null) {
            options.put("temperature", request.getTemperature());
        }
        if (request.getTopP() != null) {
            options.put("top_p", request.getTopP());
        }
        if (request.getMaxTokens() != null) {
            options.put("num_ctx", request.getMaxTokens());
        }
        if (!options.isEmpty()) {
            node.set("options", options);
        }
        if (request.getStop() != null && !request.getStop().isEmpty()) {
            node.set("stop", objectMapper.valueToTree(request.getStop()));
        }
        node.put("keep_alive", "5m");
        return node;
    }

    private ArrayNode toMessageArray(List<ChatCompletionRequest.Message> messages) {
        ArrayNode array = objectMapper.createArrayNode();
        if (messages == null) {
            return array;
        }
        for (ChatCompletionRequest.Message message : messages) {
            if (message == null) {
                continue;
            }
            ObjectNode node = objectMapper.createObjectNode();
            node.put("role", StringUtils.hasText(message.getRole()) ? message.getRole() : "user");
            node.put("content", message.getContent());
            array.add(node);
        }
        return array;
    }

    private ChatCompletionResponse parseOpenAiResponse(String body, String defaultModel) {
        try {
            JsonNode root = objectMapper.readTree(body);
            ChatCompletionResponse response = new ChatCompletionResponse();
            response.setId(root.path("id").asText(UUID.randomUUID().toString()));
            response.setObject(root.path("object").asText("chat.completion"));
            response.setCreated(root.path("created").asLong(Instant.now().getEpochSecond()));
            response.setModel(root.path("model").asText(defaultModel));

            List<ChatCompletionResponse.Choice> choices = new ArrayList<>();
            for (JsonNode choiceNode : root.path("choices")) {
                ChatCompletionResponse.Message message = new ChatCompletionResponse.Message(
                    choiceNode.path("message").path("role").asText("assistant"),
                    choiceNode.path("message").path("content").asText("")
                );
                ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice(
                    choiceNode.path("index").asInt(choices.size()),
                    message,
                    choiceNode.path("finish_reason").asText("stop")
                );
                choices.add(choice);
            }
            response.setChoices(choices);

            JsonNode usageNode = root.path("usage");
            if (!usageNode.isMissingNode()) {
                ChatCompletionResponse.Usage usage = new ChatCompletionResponse.Usage(
                    usageNode.path("prompt_tokens").asInt(0),
                    usageNode.path("completion_tokens").asInt(0),
                    usageNode.path("total_tokens").asInt(0)
                );
                response.setUsage(usage);
            }
            return response;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse OpenAI-style response: " + e.getMessage(), e);
        }
    }

    private ChatCompletionResponse parseOllamaResponse(String body, String model) {
        try {
            JsonNode root = objectMapper.readTree(body);
            ChatCompletionResponse response = new ChatCompletionResponse();
            response.setId(root.path("id").asText(UUID.randomUUID().toString()));
            response.setObject("chat.completion");
            response.setCreated(Instant.now().getEpochSecond());
            response.setModel(root.path("model").asText(model));

            JsonNode messageNode = root.path("message");
            ChatCompletionResponse.Message message = new ChatCompletionResponse.Message(
                messageNode.path("role").asText("assistant"),
                messageNode.path("content").asText("")
            );

            ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice(
                0,
                message,
                root.path("done").asBoolean(true) ? "stop" : "incomplete"
            );
            response.setChoices(List.of(choice));

            int promptTokens = root.path("prompt_eval_count").asInt(0);
            int completionTokens = root.path("eval_count").asInt(0);
            ChatCompletionResponse.Usage usage = new ChatCompletionResponse.Usage(
                promptTokens,
                completionTokens,
                promptTokens + completionTokens
            );
            response.setUsage(usage);
            return response;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse Ollama response: " + e.getMessage(), e);
        }
    }

    private void ensureSuccess(HttpResponse<?> response) {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return;
        }
        String bodyPreview;
        if (response.body() instanceof String str) {
            bodyPreview = truncate(str, 512);
        } else {
            bodyPreview = "<no-body>";
        }
        throw new IllegalStateException("LLM call failed with HTTP " + status + ": " + bodyPreview);
    }

    private String resolveModel(String requested, String fallback) {
        if (!StringUtils.hasText(requested) || "analysis-planner".equalsIgnoreCase(requested)) {
            return fallback;
        }
        return requested;
    }

    private String normalizeBase(String baseUrl) {
        if (!StringUtils.hasText(baseUrl)) {
            return "";
        }
        return baseUrl.endsWith("/")
            ? baseUrl.substring(0, baseUrl.length() - 1)
            : baseUrl;
    }

    private String resolveApiKey(String raw) {
        if (!StringUtils.hasText(raw)) {
            return raw;
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
            String expression = trimmed.substring(2, trimmed.length() - 1);
            String[] parts = expression.split(":", 2);
            String key = parts[0];
            String defaultValue = parts.length > 1 ? parts[1] : "";
            String envValue = System.getenv(key);
            if (StringUtils.hasText(envValue)) {
                return envValue;
            }
            String systemValue = System.getProperty(key);
            if (StringUtils.hasText(systemValue)) {
                return systemValue;
            }
            return defaultValue;
        }
        return trimmed;
    }

    private String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max) + "...";
    }
}
