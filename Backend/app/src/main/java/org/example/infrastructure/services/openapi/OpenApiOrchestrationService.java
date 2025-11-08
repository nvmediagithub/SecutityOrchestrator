package org.example.infrastructure.services.openapi;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.OpenApiSpec;
import org.example.domain.valueobjects.SpecificationId;
import org.example.domain.valueobjects.OpenApiVersion;
import org.example.infrastructure.repositories.OpenApiSpecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Комплексный оркестрационный сервис OpenAPI
 */
@Service
public class OpenApiOrchestrationService {
    
    @Autowired
    private OpenApiParserService parserService;
    
    @Autowired
    private OpenApiValidator validator;
    
    @Autowired
    private OpenApiDataExtractor dataExtractor;
    
    @Autowired
    private OpenApiChunker chunker;
    
    @Autowired
    private OpenApiIssueDetector issueDetector;
    
    @Autowired
    private OpenApiSpecRepository specificationRepository;
    
    /**
     * Полный анализ OpenAPI спецификации
     */
    public OpenApiAnalysisResult analyzeSpecification(String specContent, String format) {
        OpenApiAnalysisResult result = new OpenApiAnalysisResult();
        result.setStartedAt(LocalDateTime.now());
        
        try {
            OpenApiSpecification specification = parserService.parseSpecification(specContent, format);
            result.setSpecification(specification);
            
            OpenApiValidator.ValidationResult validationResult = validator.validateSpecification(specification);
            result.setValidationResult(validationResult);
            
            OpenApiIssueDetector.OpenApiIssueReport issueReport = issueDetector.detectIssues(specification);
            result.setIssueReport(issueReport);
            
            OpenApiDataExtractor.OpenApiLLMData llmData = dataExtractor.prepareForLLMAnalysis(specification);
            result.setLlmData(llmData);
            
            List<OpenApiChunker.OpenApiChunk> chunks = chunker.chunkSpecification(specification);
            result.setChunks(chunks);
            
            result.setSummary(dataExtractor.generateSummary(specification));
            
            result.setStatus(AnalysisStatus.COMPLETED);
            
        } catch (Exception e) {
            result.setStatus(AnalysisStatus.FAILED);
            result.setErrorMessage("Ошибка анализа: " + e.getMessage());
        }
        
        result.setCompletedAt(LocalDateTime.now());
        return result;
    }
    
    /**
     * Парсинг спецификации из файла
     */
    public OpenApiAnalysisResult analyzeSpecificationFromFile(MultipartFile file) {
        try {
            String content = new String(file.getBytes());
            String format = detectFormatFromFileName(file.getOriginalFilename());
            
            OpenApiAnalysisResult result = analyzeSpecification(content, format);
            result.setFileName(file.getOriginalFilename());
            result.setFileSize(file.getSize());
            
            return result;
        } catch (IOException e) {
            OpenApiAnalysisResult result = new OpenApiAnalysisResult();
            result.setStatus(AnalysisStatus.FAILED);
            result.setErrorMessage("Ошибка чтения файла: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * Анализ спецификации по URL
     */
    public OpenApiAnalysisResult analyzeSpecificationFromUrl(String specUrl) {
        try {
            OpenApiSpecification specification = parserService.parseFromUrl(specUrl);
            return analyzeSpecification(specification.getRawContent(), "auto");
        } catch (Exception e) {
            OpenApiAnalysisResult result = new OpenApiAnalysisResult();
            result.setStatus(AnalysisStatus.FAILED);
            result.setErrorMessage("Ошибка загрузки по URL: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * Сохранение спецификации в базу данных
     */
    public String saveSpecification(OpenApiSpecification specification) {
        try {
            OpenApiSpec specEntity = convertToEntity(specification);
            OpenApiSpec saved = specificationRepository.save(specEntity);
            return saved.getSpecificationId().getValue();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения спецификации: " + e.getMessage(), e);
        }
    }
    
    /**
     * Получение спецификации из базы
     */
    public Optional<OpenApiSpecification> getSpecification(String specId) {
        try {
            Optional<OpenApiSpec> specEntity = specificationRepository.findBySpecificationIdValue(specId);
            if (specEntity.isPresent()) {
                return Optional.of(convertFromEntity(specEntity.get()));
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private String detectFormatFromFileName(String fileName) {
        if (fileName != null) {
            String lowerName = fileName.toLowerCase();
            if (lowerName.endsWith(".yaml") || lowerName.endsWith(".yml")) {
                return "yaml";
            } else if (lowerName.endsWith(".json")) {
                return "json";
            }
        }
        return "auto";
    }
    
    private OpenApiSpec convertToEntity(OpenApiSpecification specification) {
        OpenApiSpec entity = new OpenApiSpec();
        entity.setSpecificationId(specification.getId());
        entity.setTitle(specification.getTitle());
        entity.setDescription(specification.getDescription());
        entity.setVersion(specification.getVersion());
        entity.setOpenApiVersion(specification.getOpenApiVersion());
        entity.setSpecificationContent(specification.getRawContent());
        entity.setIsActive(true);
        return entity;
    }
    
    private OpenApiSpecification convertFromEntity(OpenApiSpec entity) {
        OpenApiSpecification specification = new OpenApiSpecification();
        specification.setId(entity.getSpecificationId());
        specification.setTitle(entity.getTitle());
        specification.setDescription(entity.getDescription());
        specification.setVersion(entity.getVersion());
        specification.setOpenApiVersion(entity.getOpenApiVersion());
        specification.setRawContent(entity.getSpecificationContent());
        return specification;
    }
    
    public static class OpenApiAnalysisResult {
        private AnalysisStatus status;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String errorMessage;
        private String fileName;
        private Long fileSize;
        
        private OpenApiSpecification specification;
        private OpenApiValidator.ValidationResult validationResult;
        private OpenApiIssueDetector.OpenApiIssueReport issueReport;
        private OpenApiDataExtractor.OpenApiLLMData llmData;
        private List<OpenApiChunker.OpenApiChunk> chunks;
        private String summary;
        
        public AnalysisStatus getStatus() { return status; }
        public void setStatus(AnalysisStatus status) { this.status = status; }
        
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
        
        public OpenApiSpecification getSpecification() { return specification; }
        public void setSpecification(OpenApiSpecification specification) { this.specification = specification; }
        
        public OpenApiValidator.ValidationResult getValidationResult() { return validationResult; }
        public void setValidationResult(OpenApiValidator.ValidationResult validationResult) { this.validationResult = validationResult; }
        
        public OpenApiIssueDetector.OpenApiIssueReport getIssueReport() { return issueReport; }
        public void setIssueReport(OpenApiIssueDetector.OpenApiIssueReport issueReport) { this.issueReport = issueReport; }
        
        public OpenApiDataExtractor.OpenApiLLMData getLlmData() { return llmData; }
        public void setLlmData(OpenApiDataExtractor.OpenApiLLMData llmData) { this.llmData = llmData; }
        
        public List<OpenApiChunker.OpenApiChunk> getChunks() { return chunks; }
        public void setChunks(List<OpenApiChunker.OpenApiChunk> chunks) { this.chunks = chunks; }
        
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }
    
    public enum AnalysisStatus {
        STARTED,
        COMPLETED,
        FAILED
    }
}