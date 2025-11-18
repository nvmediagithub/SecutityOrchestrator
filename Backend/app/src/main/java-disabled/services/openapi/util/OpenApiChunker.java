package org.example.infrastructure.services.openapi.util;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for chunking large OpenAPI specifications into smaller parts
 */
@Service
public class OpenApiChunker {
    
    private static final int DEFAULT_CHUNK_SIZE = 50; // Number of paths per chunk
    private static final int MAX_CHUNK_SIZE = 100;
    private static final int MIN_CHUNK_SIZE = 10;
    
    public List<OpenApiChunk> chunkOpenApiSpecification(String openApiSpec, ChunkingOptions options) {
        if (openApiSpec == null || openApiSpec.trim().isEmpty()) {
            throw new IllegalArgumentException("OpenAPI specification cannot be null or empty");
        }
        
        ChunkingOptions opts = options != null ? options : new ChunkingOptions();
        int chunkSize = determineChunkSize(opts);
        
        try {
            Map<String, Object> spec = parseSpecification(openApiSpec);
            Map<String, Object> paths = (Map<String, Object>) spec.get("paths");
            
            if (paths == null || paths.isEmpty()) {
                return Collections.singletonList(new OpenApiChunk("chunk-1", openApiSpec, 0, 0));
            }
            
            return createChunks(spec, paths, chunkSize, opts);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to chunk OpenAPI specification: " + e.getMessage(), e);
        }
    }
    
    public List<OpenApiChunk> chunkByOperations(String openApiSpec, ChunkingOptions options) {
        ChunkingOptions opts = options != null ? options : new ChunkingOptions();
        opts.setStrategy(ChunkingStrategy.BY_OPERATIONS);
        
        return chunkOpenApiSpecification(openApiSpec, opts);
    }
    
    public List<OpenApiChunk> chunkByTags(String openApiSpec, ChunkingOptions options) {
        ChunkingOptions opts = options != null ? options : new ChunkingOptions();
        opts.setStrategy(ChunkingStrategy.BY_TAGS);
        
        return chunkOpenApiSpecification(openApiSpec, opts);
    }
    
    public List<OpenApiChunk> chunkBySchemaComplexity(String openApiSpec, ChunkingOptions options) {
        ChunkingOptions opts = options != null ? options : new ChunkingOptions();
        opts.setStrategy(ChunkingStrategy.BY_SCHEMA_COMPLEXITY);
        
        return chunkOpenApiSpecification(openApiSpec, opts);
    }
    
    private int determineChunkSize(ChunkingOptions options) {
        int requestedSize = options.getChunkSize();
        if (requestedSize > 0) {
            return Math.max(MIN_CHUNK_SIZE, Math.min(MAX_CHUNK_SIZE, requestedSize));
        }
        return DEFAULT_CHUNK_SIZE;
    }
    
    @SuppressWarnings("unchecked")
    private List<OpenApiChunk> createChunks(Map<String, Object> spec, Map<String, Object> paths, 
                                          int chunkSize, ChunkingOptions options) {
        List<OpenApiChunk> chunks = new ArrayList<>();
        
        if (options.getStrategy() == ChunkingStrategy.BY_TAGS) {
            return chunkByTags(spec, paths, options);
        } else if (options.getStrategy() == ChunkingStrategy.BY_SCHEMA_COMPLEXITY) {
            return chunkBySchemaComplexity(spec, paths, options);
        } else {
            return chunkByPaths(spec, paths, chunkSize);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<OpenApiChunk> chunkByPaths(Map<String, Object> spec, Map<String, Object> paths, int chunkSize) {
        List<String> pathKeys = new ArrayList<>(paths.keySet());
        Collections.sort(pathKeys); // Ensure consistent ordering
        
        int totalPaths = pathKeys.size();
        int chunkCount = (int) Math.ceil((double) totalPaths / chunkSize);
        List<OpenApiChunk> chunks = new ArrayList<>();
        
        for (int i = 0; i < chunkCount; i++) {
            int startIdx = i * chunkSize;
            int endIdx = Math.min(startIdx + chunkSize, totalPaths);
            
            Map<String, Object> chunkPaths = new LinkedHashMap<>();
            for (int j = startIdx; j < endIdx; j++) {
                String path = pathKeys.get(j);
                chunkPaths.put(path, paths.get(path));
            }
            
            Map<String, Object> chunkSpec = createChunkSpec(spec, chunkPaths);
            String chunkContent = serializeSpec(chunkSpec);
            
            chunks.add(new OpenApiChunk(
                "chunk-" + (i + 1),
                chunkContent,
                startIdx,
                endIdx - 1
            ));
        }
        
        return chunks;
    }
    
    @SuppressWarnings("unchecked")
    private List<OpenApiChunk> chunkByTags(Map<String, Object> spec, Map<String, Object> paths, ChunkingOptions options) {
        Map<String, Map<String, Object>> pathsByTag = new HashMap<>();
        
        // Group paths by their primary tag
        for (Map.Entry<String, Object> entry : paths.entrySet()) {
            String path = entry.getKey();
            Map<String, Object> pathItem = (Map<String, Object>) entry.getValue();
            
            Set<String> tags = extractTags(pathItem);
            String primaryTag = tags.isEmpty() ? "untagged" : tags.iterator().next();
            
            pathsByTag.computeIfAbsent(primaryTag, k -> new LinkedHashMap<>())
                     .put(path, pathItem);
        }
        
        List<OpenApiChunk> chunks = new ArrayList<>();
        int chunkIndex = 1;
        
        for (Map.Entry<String, Map<String, Object>> tagGroup : pathsByTag.entrySet()) {
            String tag = tagGroup.getKey();
            Map<String, Object> tagPaths = tagGroup.getValue();
            
            Map<String, Object> chunkSpec = createChunkSpec(spec, tagPaths);
            chunkSpec.put("info", createTagSpecificInfo(spec, tag));
            
            String chunkContent = serializeSpec(chunkSpec);
            chunks.add(new OpenApiChunk(
                "chunk-" + chunkIndex + "-tag-" + tag,
                chunkContent,
                0,
                tagPaths.size() - 1
            ));
            chunkIndex++;
        }
        
        return chunks;
    }
    
    @SuppressWarnings("unchecked")
    private List<OpenApiChunk> chunkBySchemaComplexity(Map<String, Object> spec, Map<String, Object> paths, ChunkingOptions options) {
        // Simple heuristic: complex paths (with many operations and parameters) get their own chunks
        Map<String, Object> simplePaths = new LinkedHashMap<>();
        Map<String, Object> complexPaths = new LinkedHashMap<>();
        
        for (Map.Entry<String, Object> entry : paths.entrySet()) {
            String path = entry.getKey();
            Map<String, Object> pathItem = (Map<String, Object>) entry.getValue();
            
            if (isComplexPath(pathItem, options)) {
                complexPaths.put(path, pathItem);
            } else {
                simplePaths.put(path, pathItem);
            }
        }
        
        List<OpenApiChunk> chunks = new ArrayList<>();
        int chunkIndex = 1;
        
        // Add complex paths as individual chunks
        for (Map.Entry<String, Object> entry : complexPaths.entrySet()) {
            Map<String, Object> chunkSpec = createChunkSpec(spec, Map.of(entry.getKey(), entry.getValue()));
            String chunkContent = serializeSpec(chunkSpec);
            
            chunks.add(new OpenApiChunk(
                "chunk-" + chunkIndex + "-complex",
                chunkContent,
                0,
                0
            ));
            chunkIndex++;
        }
        
        // Group simple paths into chunks
        if (!simplePaths.isEmpty()) {
            List<OpenApiChunk> simpleChunks = chunkByPaths(spec, simplePaths, DEFAULT_CHUNK_SIZE);
            for (OpenApiChunk chunk : simpleChunks) {
                // Reset chunk ID for simple chunks
                chunk = new OpenApiChunk("chunk-" + chunkIndex + "-simple", chunk.getContent(), 
                                       chunk.getStartIndex(), chunk.getEndIndex());
                chunks.add(chunk);
                chunkIndex++;
            }
        }
        
        return chunks;
    }
    
    @SuppressWarnings("unchecked")
    private Set<String> extractTags(Map<String, Object> pathItem) {
        Set<String> tags = new HashSet<>();
        
        for (Object operation : pathItem.values()) {
            if (operation instanceof Map) {
                Map<String, Object> op = (Map<String, Object>) operation;
                List<String> opTags = (List<String>) op.get("tags");
                if (opTags != null) {
                    tags.addAll(opTags);
                }
            }
        }
        
        return tags;
    }
    
    private boolean isComplexPath(Map<String, Object> pathItem, ChunkingOptions options) {
        int operationCount = 0;
        int parameterCount = 0;
        
        for (Object operation : pathItem.values()) {
            if (operation instanceof Map) {
                Map<String, Object> op = (Map<String, Object>) operation;
                operationCount++;
                
                List<Map<String, Object>> parameters = (List<Map<String, Object>>) op.get("parameters");
                if (parameters != null) {
                    parameterCount += parameters.size();
                }
                
                Map<String, Object> requestBody = (Map<String, Object>) op.get("requestBody");
                if (requestBody != null) {
                    parameterCount += 2; // Request body counts as complex
                }
            }
        }
        
        return operationCount > 3 || parameterCount > 10;
    }
    
    private Map<String, Object> createChunkSpec(Map<String, Object> originalSpec, Map<String, Object> paths) {
        Map<String, Object> chunkSpec = new LinkedHashMap<>();
        
        // Copy metadata
        chunkSpec.put("openapi", originalSpec.get("openapi"));
        chunkSpec.put("info", originalSpec.get("info"));
        
        if (originalSpec.containsKey("servers")) {
            chunkSpec.put("servers", originalSpec.get("servers"));
        }
        
        if (originalSpec.containsKey("security")) {
            chunkSpec.put("security", originalSpec.get("security"));
        }
        
        // Add paths
        chunkSpec.put("paths", paths);
        
        // Include components that are referenced
        Map<String, Object> components = (Map<String, Object>) originalSpec.get("components");
        if (components != null) {
            Set<String> referencedSchemas = findReferencedSchemas(paths);
            if (!referencedSchemas.isEmpty()) {
                Map<String, Object> chunkComponents = new HashMap<>();
                Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
                if (schemas != null) {
                    Map<String, Object> chunkSchemas = new HashMap<>();
                    for (String schemaName : referencedSchemas) {
                        if (schemas.containsKey(schemaName)) {
                            chunkSchemas.put(schemaName, schemas.get(schemaName));
                        }
                    }
                    if (!chunkSchemas.isEmpty()) {
                        chunkComponents.put("schemas", chunkSchemas);
                    }
                }
                if (!chunkComponents.isEmpty()) {
                    chunkSpec.put("components", chunkComponents);
                }
            }
        }
        
        return chunkSpec;
    }
    
    private Map<String, Object> createTagSpecificInfo(Map<String, Object> originalSpec, String tag) {
        Map<String, Object> originalInfo = (Map<String, Object>) originalSpec.get("info");
        Map<String, Object> tagInfo = new HashMap<>();
        
        if (originalInfo != null) {
            tagInfo.put("title", originalInfo.get("title") + " - " + tag + " Operations");
            tagInfo.put("version", originalInfo.get("version"));
            if (originalInfo.containsKey("description")) {
                tagInfo.put("description", "Operations tagged with: " + tag);
            }
        }
        
        return tagInfo;
    }
    
    @SuppressWarnings("unchecked")
    private Set<String> findReferencedSchemas(Map<String, Object> paths) {
        Set<String> schemas = new HashSet<>();
        
        for (Object pathItem : paths.values()) {
            if (pathItem instanceof Map) {
                Map<String, Object> pathMap = (Map<String, Object>) pathItem;
                for (Object operation : pathMap.values()) {
                    if (operation instanceof Map) {
                        Map<String, Object> op = (Map<String, Object>) operation;
                        extractSchemaReferences(op, schemas);
                    }
                }
            }
        }
        
        return schemas;
    }
    
    @SuppressWarnings("unchecked")
    private void extractSchemaReferences(Map<String, Object> obj, Set<String> schemas) {
        for (Object value : obj.values()) {
            if (value instanceof String) {
                String str = (String) value;
                if (str.startsWith("#/components/schemas/")) {
                    String schemaName = str.substring("#/components/schemas/".length());
                    schemas.add(schemaName);
                }
            } else if (value instanceof Map) {
                extractSchemaReferences((Map<String, Object>) value, schemas);
            } else if (value instanceof List) {
                for (Object item : (List<?>) value) {
                    if (item instanceof Map) {
                        extractSchemaReferences((Map<String, Object>) item, schemas);
                    }
                }
            }
        }
    }
    
    private String serializeSpec(Map<String, Object> spec) {
        // Simplified serialization - in real implementation use Jackson or SnakeYAML
        return spec.toString();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseSpecification(String spec) {
        // Simplified parsing - in real implementation use Jackson or SnakeYAML
        return new HashMap<>();
    }
    
    public static class OpenApiChunk {
        private String chunkId;
        private final String content;
        private final int startIndex;
        private final int endIndex;
        
        public OpenApiChunk(String chunkId, String content, int startIndex, int endIndex) {
            this.chunkId = chunkId;
            this.content = content;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
        
        public String getChunkId() {
            return chunkId;
        }
        
        public String getContent() {
            return content;
        }
        
        public int getStartIndex() {
            return startIndex;
        }
        
        public int getEndIndex() {
            return endIndex;
        }
        
        public void setChunkId(String chunkId) {
            this.chunkId = chunkId;
        }
    }
    
    public static class ChunkingOptions {
        private int chunkSize = 0;
        private ChunkingStrategy strategy = ChunkingStrategy.BY_PATHS;
        private boolean includeComponents = true;
        private boolean preserveOperationOrder = true;
        
        public int getChunkSize() {
            return chunkSize;
        }
        
        public void setChunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
        }
        
        public ChunkingStrategy getStrategy() {
            return strategy;
        }
        
        public void setStrategy(ChunkingStrategy strategy) {
            this.strategy = strategy;
        }
        
        public boolean isIncludeComponents() {
            return includeComponents;
        }
        
        public void setIncludeComponents(boolean includeComponents) {
            this.includeComponents = includeComponents;
        }
        
        public boolean isPreserveOperationOrder() {
            return preserveOperationOrder;
        }
        
        public void setPreserveOperationOrder(boolean preserveOperationOrder) {
            this.preserveOperationOrder = preserveOperationOrder;
        }
    }
    
    public enum ChunkingStrategy {
        BY_PATHS,
        BY_OPERATIONS,
        BY_TAGS,
        BY_SCHEMA_COMPLEXITY
    }
}