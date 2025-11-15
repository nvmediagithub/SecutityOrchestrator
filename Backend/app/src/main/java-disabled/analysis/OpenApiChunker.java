package org.example.domain.entities.analysis;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Утилитарный класс для разбиения OpenAPI спецификаций на части
 */
public class OpenApiChunker {
    
    public static class OpenApiChunk {
        private final String chunkId;
        private final String content;
        private final Integer chunkIndex;
        private final Integer totalChunks;
        private final String chunkType;
        private final List<String> references;
        private final String sourceSection;
        
        public OpenApiChunk(Builder builder) {
            this.chunkId = builder.chunkId != null ? builder.chunkId : UUID.randomUUID().toString();
            this.content = builder.content != null ? builder.content : "";
            this.chunkIndex = builder.chunkIndex != null ? builder.chunkIndex : 0;
            this.totalChunks = builder.totalChunks != null ? builder.totalChunks : 1;
            this.chunkType = builder.chunkType != null ? builder.chunkType : "GENERAL";
            this.references = builder.references != null ? builder.references : new ArrayList<>();
            this.sourceSection = builder.sourceSection;
        }
        
        // Getters
        public String getChunkId() { return chunkId; }
        public String getContent() { return content; }
        public Integer getChunkIndex() { return chunkIndex; }
        public Integer getTotalChunks() { return totalChunks; }
        public String getChunkType() { return chunkType; }
        public List<String> getReferences() { return references; }
        public String getSourceSection() { return sourceSection; }
        
        // Utility methods
        public boolean isFirstChunk() {
            return chunkIndex == 0;
        }
        
        public boolean isLastChunk() {
            return chunkIndex == totalChunks - 1;
        }
        
        public boolean hasReferences() {
            return !references.isEmpty();
        }
        
        public boolean isEndpointChunk() {
            return "ENDPOINT".equals(chunkType);
        }
        
        public boolean isSchemaChunk() {
            return "SCHEMA".equals(chunkType);
        }
        
        public static class Builder {
            private String chunkId;
            private String content;
            private Integer chunkIndex;
            private Integer totalChunks;
            private String chunkType;
            private List<String> references;
            private String sourceSection;
            
            public Builder chunkId(String chunkId) {
                this.chunkId = chunkId;
                return this;
            }
            
            public Builder content(String content) {
                this.content = content;
                return this;
            }
            
            public Builder chunkIndex(Integer chunkIndex) {
                this.chunkIndex = chunkIndex;
                return this;
            }
            
            public Builder totalChunks(Integer totalChunks) {
                this.totalChunks = totalChunks;
                return this;
            }
            
            public Builder chunkType(String chunkType) {
                this.chunkType = chunkType;
                return this;
            }
            
            public Builder references(List<String> references) {
                this.references = references;
                return this;
            }
            
            public Builder sourceSection(String sourceSection) {
                this.sourceSection = sourceSection;
                return this;
            }
            
            public OpenApiChunk build() {
                return new OpenApiChunk(this);
            }
        }
    }
    
    /**
     * Разбивает OpenAPI спецификацию на логические части
     */
    public static List<OpenApiChunk> chunkOpenApiSpec(String openApiSpec) {
        List<OpenApiChunk> chunks = new ArrayList<>();
        
        if (openApiSpec == null || openApiSpec.trim().isEmpty()) {
            return chunks;
        }
        
        // Простое разбиение по секциям
        String[] sections = openApiSpec.split("[\n\r]{2,}");
        
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i].trim();
            if (!section.isEmpty()) {
                OpenApiChunk chunk = new OpenApiChunk.Builder()
                    .content(section)
                    .chunkIndex(i)
                    .totalChunks(sections.length)
                    .chunkType(determineChunkType(section))
                    .sourceSection(extractSectionName(section))
                    .build();
                chunks.add(chunk);
            }
        }
        
        return chunks;
    }
    
    private static String determineChunkType(String content) {
        if (content.contains("\"paths\"") || content.contains("paths:")) {
            return "ENDPOINT";
        }
        if (content.contains("\"components\"") || content.contains("components:")) {
            return "SCHEMA";
        }
        if (content.contains("\"security\"") || content.contains("security:")) {
            return "SECURITY";
        }
        return "GENERAL";
    }
    
    private static String extractSectionName(String content) {
        // Простое извлечение названия секции
        if (content.startsWith("paths:")) {
            return "PATHS";
        }
        if (content.startsWith("components:")) {
            return "COMPONENTS";
        }
        if (content.startsWith("security:")) {
            return "SECURITY";
        }
        return "GENERAL";
    }
}