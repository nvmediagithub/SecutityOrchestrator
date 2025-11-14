package org.example.features.llm.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalLLMService {

    // Placeholder implementation
    public void loadModel(String modelName) {
        log.info("Loading local LLM model: {}", modelName);
        // Implementation will be added as needed
    }
}