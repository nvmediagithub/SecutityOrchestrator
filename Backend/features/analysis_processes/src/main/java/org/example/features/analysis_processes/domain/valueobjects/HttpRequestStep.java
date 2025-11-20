package org.example.features.analysis_processes.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpRequestStep {

    @JsonProperty("name")
    private String name;

    @JsonProperty("stepId")
    private String stepId;

    @JsonProperty("method")
    private String method;

    @JsonProperty("url")
    private String url;

    @JsonProperty("headers")
    private Map<String, String> headers = Collections.emptyMap();

    @JsonProperty("body")
    private String body;

    @JsonProperty("description")
    private String description;

    public HttpRequestStep() {
        // Jackson
    }

    public HttpRequestStep(String name, String method, String url, Map<String, String> headers, String body, String description) {
        this.name = name;
        this.method = method;
        this.url = url;
        this.headers = headers == null ? Collections.emptyMap() : headers;
        this.body = body;
        this.description = description;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers == null ? Collections.emptyMap() : headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
