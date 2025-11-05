package org.example.domain.entities;

import java.nio.file.Path;

public class AiModel {
    private org.example.domain.valueobjects.ModelId id;
    private String name;
    private org.example.domain.valueobjects.ModelType type;
    private Path modelPath;
    private Object metadata;
    private org.example.domain.valueobjects.ModelStatus status;

    // Mock data for testing
    public static AiModel createMockModel() {
        AiModel model = new AiModel();
        model.id = new org.example.domain.valueobjects.ModelId("mock-model-1");
        model.name = "Mock AI Model";
        model.type = org.example.domain.valueobjects.ModelType.ONNX;
        model.status = org.example.domain.valueobjects.ModelStatus.LOADED;
        return model;
    }
}