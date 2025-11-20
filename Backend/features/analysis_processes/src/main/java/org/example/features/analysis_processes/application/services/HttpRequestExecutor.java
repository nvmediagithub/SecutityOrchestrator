package org.example.features.analysis_processes.application.services;

import org.example.features.analysis_processes.domain.valueobjects.HttpRequestStep;

import java.util.List;
import java.util.Map;

public interface HttpRequestExecutor {
    List<Map<String, Object>> execute(List<HttpRequestStep> steps, String baseUrl);
}
