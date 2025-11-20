package org.example.features.analysis_processes.application.services;

import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.llm.domain.dto.ChatCompletionResponse;
import org.example.features.llm.domain.services.LLMService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProcessAnalysisPlannerTest {

    @Test
    void fallbackPlanIncludesStructuredActions(@TempDir Path tempDir) throws IOException {
        AnalysisProcess process = sampleProcess(tempDir);
        ProcessAnalysisPlanner planner = new ProcessAnalysisPlanner(new StaticProvider(null), true);

        ProcessAnalysisPlanner.PlanResult result = planner.generatePlan(
            process,
            Map.of("userInputs", Map.of("baseUrl", "https://api.example.local", "authToken", "token"))
        );

        assertFalse(result.actions().isEmpty(), "Fallback plan should create action items");
        assertFalse(result.assertions().isEmpty(), "Fallback plan should create assertions");
        assertFalse(result.httpRequests().isEmpty(), "Fallback plan should include HTTP steps");
        assertTrue(result.plan().contains(process.getName()));
    }

    @Test
    void llmHappyPathReturnsStructuredPayload(@TempDir Path tempDir) throws IOException {
        AnalysisProcess process = sampleProcess(tempDir);
        LLMService service = new StubLlmService("""
            {
              "plan": "1. Check login flow",
              "script": "console.log('ok')",
              "summary": "LLM produced plan",
              "actions": [
                {"title": "Check login", "detail": "Verify /login", "artifact": "/login"}
              ],
              "assertions": [
                {"check": "HTTP 200", "expected": "Gateway returns success"}
              ]
            }
            """);
        ProcessAnalysisPlanner planner = new ProcessAnalysisPlanner(new StaticProvider(service), false);

        ProcessAnalysisPlanner.PlanResult result = planner.generatePlan(
            process,
            Map.of("userInputs", Collections.emptyMap())
        );

        assertEquals("LLM produced plan", result.summary());
        assertEquals(1, result.actions().size());
        assertEquals("Check login", result.actions().getFirst().title());
        assertEquals(1, result.assertions().size());
        assertEquals("HTTP 200", result.assertions().getFirst().check());
        assertEquals(1, result.httpRequests().size());
    }

    private AnalysisProcess sampleProcess(Path tempDir) throws IOException {
        Path bpmn = Files.createTempFile(tempDir, "process", ".bpmn");
        Files.writeString(bpmn, "<xml>demo</xml>");
        Path openapi = Files.createTempFile(tempDir, "spec", ".yml");
        Files.writeString(openapi, """
            openapi: 3.0.0
            info:
              title: Test API
              version: 1.0.0
            paths:
              /health: {}
            """);

        return AnalysisProcess.builder()
            .id("process-1")
            .name("Process One")
            .description("Unit test process")
            .createdAt(LocalDateTime.now())
            .bpmnDiagramPath(bpmn.toString())
            .openapiSpecPath(openapi.toString())
            .build();
    }

    private record StaticProvider(LLMService value) implements ObjectProvider<LLMService> {
        @Override
        public LLMService getObject(Object... args) {
            return value;
        }

        @Override
        public LLMService getObject() {
            return value;
        }

        @Override
        public LLMService getIfAvailable() {
            return value;
        }

        @Override
        public LLMService getIfUnique() {
            return value;
        }

        @Override
        public void forEach(Consumer<? super LLMService> action) {
            if (value != null) {
                action.accept(value);
            }
        }

        @Override
        public Stream<LLMService> stream() {
            return value == null ? Stream.empty() : Stream.of(value);
        }
    }

    private static final class StubLlmService implements LLMService {

        private final String payload;

        private StubLlmService(String payload) {
            this.payload = payload;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public CompletableFuture<ChatCompletionResponse> chatCompletion(org.example.features.llm.domain.dto.ChatCompletionRequest request) {
            ChatCompletionResponse.Message message = new ChatCompletionResponse.Message("assistant", payload);
            ChatCompletionResponse.Choice choice = new ChatCompletionResponse.Choice(0, message, "stop");
            ChatCompletionResponse response = new ChatCompletionResponse();
            response.setChoices(List.of(choice));
            return CompletableFuture.completedFuture(response);
        }

        @Override
        public String getProviderName() {
            return "stub";
        }

        @Override
        public String getStatus() {
            return "ready";
        }

        @Override
        public String[] getSupportedModels() {
            return new String[] {"stub"};
        }
    }
}
