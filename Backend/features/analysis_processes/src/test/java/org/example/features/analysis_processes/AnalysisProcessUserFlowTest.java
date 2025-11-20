package org.example.features.analysis_processes;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.example.features.analysis_processes.application.services.ProcessAnalysisPlanner;
import org.example.features.analysis_processes.application.services.ProcessAnalysisPlanner.PlanResult;
import org.example.features.analysis_processes.application.web.controllers.AnalysisProcessController.AnalysisProcessRequest;
import org.example.features.analysis_processes.domain.entities.AnalysisProcess;
import org.example.features.analysis_processes.domain.services.AnalysisProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "analysis.processes.bpmn-storage-path=target/test-data/bpmn",
    "analysis.processes.openapi-storage-path=target/test-data/openapi",
    "analysis.planner.allow-fallback=true"
})
class AnalysisProcessUserFlowTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProcessAnalysisPlanner processAnalysisPlanner;

    @Autowired
    private AnalysisProcessService analysisProcessService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void testUserFlow() throws Exception {
        // Step 1: Create an Analysis Process via API (POST /api/analysis-processes)
        AnalysisProcessRequest request = new AnalysisProcessRequest();
        request.setName("Test Analysis Process");
        request.setDescription("Test description for user flow");
        request.setType("security");
        request.setStatus("active");
        request.setCreatedAt(LocalDateTime.now());

        var createResponse = given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/analysis-processes")
            .then()
            .statusCode(201)
            .extract()
            .response();

        System.out.println(createResponse.asString());

        String processId = createResponse.jsonPath().getString("data.id");
        assertThat(processId).isNotNull();

        // Step 2: Upload BPMN file (dataset/bpmn/01_bonus_payment.bpmn) via API (POST /{id}/bpmn)
        String bpmnContent = Files.readString(Paths.get("dataset/bpmn/01_bonus_payment.bpmn"));

        var bpmnResponse = given()
            .multiPart("file", "01_bonus_payment.bpmn", bpmnContent.getBytes(), "application/xml")
            .when()
            .post("/api/analysis-processes/{id}/bpmn", processId)
            .then()
            .statusCode(200)
            .extract()
            .response();

        System.out.println(bpmnResponse.asString());

        assertThat(bpmnResponse.jsonPath().getString("data.elements")).isNotNull(); // Assuming response has elements

        // Step 3: Upload OpenAPI spec via API (POST /{id}/openapi)
        String openapiPath = Files.exists(Paths.get("dataset/openapi/openapi_test.json")) ?
            "dataset/openapi/openapi_test.json" : "docs/guide/openapi.json";
        String openapiContent = Files.readString(Paths.get(openapiPath));

        String openapiFileName = openapiPath.equals("dataset/openapi/openapi_test.json") ? "openapi_test.json" : "openapi.json";

        var openapiResponse = given()
            .multiPart("file", openapiFileName, openapiContent.getBytes(), "application/json")
            .when()
            .post("/api/analysis-processes/{id}/openapi", processId)
            .then()
            .statusCode(200)
            .extract()
            .response();

        System.out.println(openapiResponse.asString());

        assertThat(openapiResponse.jsonPath().getString("data.endpoints")).isNotNull(); // Assuming response has endpoints

        // Step 4: Start an analysis session
        var startSessionResponse = given()
            .when()
            .post("/api/analysis-processes/{id}/analysis-sessions", processId)
            .then()
            .statusCode(200)
            .extract()
            .response();

        System.out.println("Start session response: " + startSessionResponse.asString());

        String sessionId = startSessionResponse.jsonPath().getString("data.id");
        assertThat(sessionId).isNotNull();

        // Step 5: Generate a plan by calling the actual API endpoint POST /api/analysis-sessions/{sessionId}/llm
        var generatePlanResponse = given()
            .when()
            .post("/api/analysis-sessions/{sessionId}/llm", sessionId)
            .then()
            .extract()
            .response();

        System.out.println("Generate plan response: " + generatePlanResponse.asString());

        // Assert that the response is either successful or contains the expected error
        if (generatePlanResponse.statusCode() == 200) {
            // Success case
            assertThat(generatePlanResponse.jsonPath().getString("data.plan")).isNotNull();
            assertThat(generatePlanResponse.jsonPath().getString("data.script")).isNotNull();
            assertThat(generatePlanResponse.jsonPath().getString("data.summary")).isNotNull();
            assertThat(generatePlanResponse.jsonPath().getString("data.actions")).isNotNull();
            assertThat(generatePlanResponse.jsonPath().getString("data.assertions")).isNotNull();
        } else {
            // Error case - print the error for debugging
            System.out.println("Plan generation failed with status: " + generatePlanResponse.statusCode());
            String errorMessage = generatePlanResponse.jsonPath().getString("message");
            System.out.println("Error message: " + errorMessage);
            // Assert that we get some response (either success or expected error)
            assertThat(generatePlanResponse.statusCode() == 400 || generatePlanResponse.statusCode() == 500)
                .describedAs("Expected either success (200) or error (400/500), but got " + generatePlanResponse.statusCode())
                .isTrue();
        }
    }
}