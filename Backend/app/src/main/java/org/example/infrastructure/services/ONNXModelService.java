package org.example.infrastructure.services;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.example.domain.entities.ONNXModel;
import org.example.domain.valueobjects.ModelId;
import org.example.domain.valueobjects.ModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service for managing ONNX models
 * Handles model loading, unloading, memory management, and metadata extraction
 */
@Service
public class ONNXModelService {
    
    private static final Logger logger = LoggerFactory.getLogger(ONNXModelService.class);
    
    private final Map<String, ONNXModel> availableModels = new ConcurrentHashMap<>();
    private final Map<String, LoadedModel> loadedModels = new ConcurrentHashMap<>();
    private final Map<String, ModelMetadata> modelMetadata = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Executor taskExecutor;
    private final String modelsDirectory;
    
    public ONNXModelService(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.modelsDirectory = System.getProperty("user.home") + "/.security-orchestrator/models";
        initializeModelsDirectory();
        scanForAvailableModels();
    }
    
    public ONNXModelService(Executor taskExecutor, String modelsDirectory) {
        this.taskExecutor = taskExecutor;
        this.modelsDirectory = modelsDirectory;
        initializeModelsDirectory();
        scanForAvailableModels();
    }
    
    /**
     * Load ONNX model asynchronously
     */
    public CompletableFuture<ONNXModel> loadModel(String modelPath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String modelId = extractModelIdFromPath(modelPath);
                
                lock.writeLock().lock();
                try {
                    // Check if model is already loaded
                    if (loadedModels.containsKey(modelId)) {
                        logger.info("Model {} is already loaded", modelId);
                        return loadedModels.get(modelId).getModel();
                    }
                    
                    // Check if model is available
                    if (!availableModels.containsKey(modelId)) {
                        throw new ONNXModelException("Model not found: " + modelId);
                    }
                    
                    ONNXModel model = availableModels.get(modelId).markAsLoading();
                    availableModels.put(modelId, model);
                    
                } finally {
                    lock.writeLock().unlock();
                }
                
                // Load the actual ONNX model
                LoadedModel loadedModel = performModelLoad(modelPath, modelId);
                
                lock.writeLock().lock();
                try {
                    ONNXModel loadedONNXModel = loadedModel.getModel().markAsLoaded();
                    loadedModels.put(modelId, loadedModel);
                    availableModels.put(modelId, loadedONNXModel);
                    
                    logger.info("Model {} loaded successfully", modelId);
                    return loadedONNXModel;
                    
                } finally {
                    lock.writeLock().unlock();
                }
                
            } catch (Exception e) {
                logger.error("Failed to load model: {}", modelPath, e);
                throw new ONNXModelException("Failed to load model: " + e.getMessage(), e);
            }
        }, taskExecutor);
    }
    
    /**
     * Unload ONNX model
     */
    public CompletableFuture<Boolean> unloadModel(String modelId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                lock.writeLock().lock();
                try {
                    LoadedModel loadedModel = loadedModels.remove(modelId);
                    if (loadedModel != null) {
                        // Close ONNX session
                        closeModelSession(loadedModel);
                        
                        // Update model status
                        ONNXModel model = availableModels.get(modelId);
                        if (model != null) {
                            availableModels.put(modelId, model.markAsUnloaded());
                        }
                        
                        logger.info("Model {} unloaded successfully", modelId);
                        return true;
                    } else {
                        logger.warn("Model {} was not loaded", modelId);
                        return false;
                    }
                } finally {
                    lock.writeLock().unlock();
                }
            } catch (Exception e) {
                logger.error("Failed to unload model: {}", modelId, e);
                throw new ONNXModelException("Failed to unload model: " + e.getMessage(), e);
            }
        }, taskExecutor);
    }
    
    /**
     * Get model metadata
     */
    public ModelMetadata getModelMetadata(String modelId) {
        lock.readLock().lock();
        try {
            return modelMetadata.get(modelId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get all available models
     */
    public List<ONNXModel> getAvailableModels() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(availableModels.values());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get loaded models
     */
    public List<ONNXModel> getLoadedModels() {
        lock.readLock().lock();
        try {
            return loadedModels.values().stream()
                .map(LoadedModel::getModel)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get model status
     */
    public ModelStatus getModelStatus(String modelId) {
        lock.readLock().lock();
        try {
            ONNXModel model = availableModels.get(modelId);
            return model != null ? model.getStatus() : ModelStatus.AVAILABLE;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get memory usage statistics
     */
    public MemoryStats getMemoryStats() {
        lock.readLock().lock();
        try {
            long totalMemory = loadedModels.values().stream()
                .mapToLong(LoadedModel::getMemoryUsage)
                .sum();
            long totalModels = loadedModels.size();
            
            return new MemoryStats(totalMemory, (int) totalModels, getEstimatedMaxMemory());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Clean up unused models based on memory pressure
     */
    public CompletableFuture<Integer> cleanupUnusedModels() {
        return CompletableFuture.supplyAsync(() -> {
            int cleanedCount = 0;
            MemoryStats stats = getMemoryStats();
            
            if (stats.getUsagePercent() < 80) {
                logger.debug("No memory cleanup needed, usage: {}%", stats.getUsagePercent());
                return 0;
            }
            
            List<String> modelsToUnload = new ArrayList<>();
            
            lock.readLock().lock();
            try {
                // Find least recently used models
                loadedModels.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().getLastUsed()))
                    .forEach(entry -> modelsToUnload.add(entry.getKey()));
            } finally {
                lock.readLock().unlock();
            }
            
            // Unload models until memory usage is acceptable
            for (String modelId : modelsToUnload) {
                if (stats.getUsagePercent() < 60) break;
                
                try {
                    unloadModel(modelId).get();
                    cleanedCount++;
                    
                    stats = getMemoryStats(); // Recalculate after unload
                } catch (Exception e) {
                    logger.warn("Failed to cleanup model {}: {}", modelId, e.getMessage());
                }
            }
            
            logger.info("Cleaned up {} models, memory usage now: {}%", 
                cleanedCount, stats.getUsagePercent());
            
            return cleanedCount;
        }, taskExecutor);
    }
    
    /**
     * Register new model
     */
    public CompletableFuture<Boolean> registerModel(String name, String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path path = Paths.get(filePath);
                if (!Files.exists(path)) {
                    throw new ONNXModelException("Model file not found: " + filePath);
                }
                
                ONNXModel model = ONNXModel.create(name, filePath);
                ModelMetadata metadata = extractModelMetadata(path);
                
                lock.writeLock().lock();
                try {
                    availableModels.put(model.getModelId().toString(), model);
                    modelMetadata.put(model.getModelId().toString(), metadata);
                    
                    logger.info("Registered model: {} at {}", name, filePath);
                    return true;
                } finally {
                    lock.writeLock().unlock();
                }
            } catch (Exception e) {
                logger.error("Failed to register model: {}", filePath, e);
                throw new ONNXModelException("Failed to register model: " + e.getMessage(), e);
            }
        }, taskExecutor);
    }
    
    // Private methods
    
    private void initializeModelsDirectory() {
        try {
            Files.createDirectories(Paths.get(modelsDirectory));
            logger.info("Models directory initialized: {}", modelsDirectory);
        } catch (Exception e) {
            logger.error("Failed to create models directory: {}", modelsDirectory, e);
        }
    }
    
    private void scanForAvailableModels() {
        try {
            Path modelsDir = Paths.get(modelsDirectory);
            if (Files.exists(modelsDir)) {
                Files.walk(modelsDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".onnx"))
                    .forEach(this::registerModelFromFile);
            }
        } catch (Exception e) {
            logger.error("Failed to scan for available models", e);
        }
    }
    
    private void registerModelFromFile(Path modelPath) {
        try {
            String fileName = modelPath.getFileName().toString();
            String modelName = fileName.replace(".onnx", "");
            String modelId = modelName.toLowerCase().replace(" ", "_");
            
            ONNXModel model = ONNXModel.create(modelName, modelPath.toString());
            ModelMetadata metadata = extractModelMetadata(modelPath);
            
            availableModels.put(modelId, model);
            modelMetadata.put(modelId, metadata);
            
            logger.debug("Found ONNX model: {} at {}", modelName, modelPath);
        } catch (Exception e) {
            logger.warn("Failed to register model from file: {}", modelPath, e);
        }
    }
    
    private LoadedModel performModelLoad(String modelPath, String modelId) throws Exception {
        long startTime = System.currentTimeMillis();
        
        // Initialize ONNX environment
        OrtEnvironment environment = OrtEnvironment.getEnvironment();
        
        // Create session options for optimization
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        
        // ONNX Runtime for Java doesn't have setCPU method, so we skip this optimization
        // In a real implementation, you would configure thread settings differently
        
        // Load the model
        OrtSession session = environment.createSession(modelPath, options);
        
        // Extract metadata
        ModelMetadata metadata = extractModelMetadata(Paths.get(modelPath));
        
        long loadTime = System.currentTimeMillis() - startTime;
        long estimatedMemory = estimateMemoryUsage(session, metadata);
        
        ONNXModel model = ONNXModel.create(metadata.getName(), modelPath);
        
        LoadedModel loadedModel = new LoadedModel(
            model,
            session,
            environment,
            estimatedMemory,
            LocalDateTime.now()
        );
        
        logger.info("Model {} loaded in {}ms, estimated memory: {}MB", 
            modelId, loadTime, estimatedMemory / (1024 * 1024));
        
        return loadedModel;
    }
    
    private void closeModelSession(LoadedModel loadedModel) {
        try {
            if (loadedModel.getSession() != null) {
                loadedModel.getSession().close();
            }
            // OrtEnvironment is shared, don't close it here
            logger.debug("Closed ONNX session for model: {}", 
                loadedModel.getModel().getName());
        } catch (Exception e) {
            logger.warn("Error closing ONNX session: {}", e.getMessage());
        }
    }
    
    private ModelMetadata extractModelMetadata(Path modelPath) {
        try {
            File file = modelPath.toFile();
            long fileSize = file.length();
            
            String fileName = modelPath.getFileName().toString();
            String modelName = fileName.replace(".onnx", "");
            
            // Extract model info from filename if possible
            String version = "1.0.0";
            String modelType = "text-generation";
            
            if (fileName.toLowerCase().contains("phi")) {
                modelType = "text-generation";
            } else if (fileName.toLowerCase().contains("embedding")) {
                modelType = "embeddings";
            }
            
            return new ModelMetadata(
                modelName,
                version,
                modelType,
                fileSize,
                4096, // Default context window
                2048, // Default max tokens
                "CPU", // Default hardware
                false // Default quantized
            );
        } catch (Exception e) {
            logger.warn("Failed to extract metadata for model: {}", modelPath, e);
            return ModelMetadata.defaultMetadata(modelPath.getFileName().toString());
        }
    }
    
    private String extractModelIdFromPath(String modelPath) {
        Path path = Paths.get(modelPath);
        String fileName = path.getFileName().toString();
        return fileName.replace(".onnx", "").toLowerCase().replace(" ", "_");
    }
    
    private long estimateMemoryUsage(OrtSession session, ModelMetadata metadata) {
        // Rough estimation based on model size and type
        long baseMemory = metadata.getFileSize();
        
        // Add overhead for intermediate tensors
        long overhead = (long) (baseMemory * 0.5);
        
        return baseMemory + overhead;
    }
    
    private long getEstimatedMaxMemory() {
        // Get available memory or set a reasonable limit
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        
        // Use 50% of max memory for models
        return (long) (maxMemory * 0.5);
    }
    
    // Inner classes
    
    /**
     * Represents a loaded ONNX model with its session and metadata
     */
    private static class LoadedModel {
        private final ONNXModel model;
        private final OrtSession session;
        private final OrtEnvironment environment;
        private final long memoryUsage;
        private final LocalDateTime lastUsed;
        
        public LoadedModel(ONNXModel model, OrtSession session, OrtEnvironment environment,
                          long memoryUsage, LocalDateTime lastUsed) {
            this.model = model;
            this.session = session;
            this.environment = environment;
            this.memoryUsage = memoryUsage;
            this.lastUsed = lastUsed;
        }
        
        public ONNXModel getModel() { return model; }
        public OrtSession getSession() { return session; }
        public OrtEnvironment getEnvironment() { return environment; }
        public long getMemoryUsage() { return memoryUsage; }
        public LocalDateTime getLastUsed() { return lastUsed; }
    }
    
    /**
     * Model metadata extracted from file and model info
     */
    public static class ModelMetadata {
        private final String name;
        private final String version;
        private final String modelType;
        private final long fileSize;
        private final int contextWindow;
        private final int maxTokens;
        private final String hardwareAcceleration;
        private final boolean isQuantized;
        
        public ModelMetadata(String name, String version, String modelType, long fileSize,
                           int contextWindow, int maxTokens, String hardwareAcceleration,
                           boolean isQuantized) {
            this.name = name;
            this.version = version;
            this.modelType = modelType;
            this.fileSize = fileSize;
            this.contextWindow = contextWindow;
            this.maxTokens = maxTokens;
            this.hardwareAcceleration = hardwareAcceleration;
            this.isQuantized = isQuantized;
        }
        
        public static ModelMetadata defaultMetadata(String name) {
            return new ModelMetadata(name, "1.0.0", "text-generation", 0L, 4096, 2048, "CPU", false);
        }
        
        // Getters
        public String getName() { return name; }
        public String getVersion() { return version; }
        public String getModelType() { return modelType; }
        public long getFileSize() { return fileSize; }
        public int getContextWindow() { return contextWindow; }
        public int getMaxTokens() { return maxTokens; }
        public String getHardwareAcceleration() { return hardwareAcceleration; }
        public boolean isQuantized() { return isQuantized; }
    }
    
    /**
     * Memory usage statistics
     */
    public static class MemoryStats {
        private final long usedMemory;
        private final int loadedModels;
        private final long maxMemory;
        
        public MemoryStats(long usedMemory, int loadedModels, long maxMemory) {
            this.usedMemory = usedMemory;
            this.loadedModels = loadedModels;
            this.maxMemory = maxMemory;
        }
        
        public double getUsagePercent() {
            return maxMemory > 0 ? (double) usedMemory / maxMemory * 100 : 0;
        }
        
        public long getUsedMemory() { return usedMemory; }
        public int getLoadedModels() { return loadedModels; }
        public long getMaxMemory() { return maxMemory; }
    }
    
    /**
     * Exception for ONNX model operations
     */
    public static class ONNXModelException extends RuntimeException {
        public ONNXModelException(String message) { super(message); }
        public ONNXModelException(String message, Throwable cause) { super(message, cause); }
    }
}