package org.example.infrastructure.repositories.openapi;

import org.example.domain.entities.openapi.EndpointAnalysis;
import org.example.domain.entities.openapi.OpenApiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с EndpointAnalysis
 */
@Repository
public interface EndpointAnalysisRepository extends JpaRepository<EndpointAnalysis, UUID> {
    
    /**
     * Найти анализы по сервису
     */
    List<EndpointAnalysis> findByService(OpenApiService service);
    
    /**
     * Найти анализы по ID сервиса
     */
    List<EndpointAnalysis> findByServiceId(UUID serviceId);
    
    /**
     * Найти анализ по имени и сервису
     */
    Optional<EndpointAnalysis> findByAnalysisNameAndService(String analysisName, OpenApiService service);
    
    /**
     * Найти анализы по типу
     */
    List<EndpointAnalysis> findByAnalysisType(EndpointAnalysis.AnalysisType analysisType);
    
    /**
     * Найти анализы по статусу
     */
    List<EndpointAnalysis> findByStatus(EndpointAnalysis.AnalysisStatus status);
    
    /**
     * Найти анализы по сервису и статусу
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.service = :service AND a.status = :status")
    List<EndpointAnalysis> findByServiceAndStatus(@Param("service") OpenApiService service, 
                                                  @Param("status") EndpointAnalysis.AnalysisStatus status);
    
    /**
     * Найти анализы с проблемами безопасности
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.service = :service AND a.securityIssues > 0")
    List<EndpointAnalysis> findSecurityIssuesByService(@Param("service") OpenApiService service);
    
    /**
     * Найти анализы с проблемами валидации
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.service = :service AND a.validationIssues > 0")
    List<EndpointAnalysis> findValidationIssuesByService(@Param("service") OpenApiService service);
    
    /**
     * Найти анализы с проблемами консистентности
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.service = :service AND a.consistencyIssues > 0")
    List<EndpointAnalysis> findConsistencyIssuesByService(@Param("service") OpenApiService service);
    
    /**
     * Найти анализы с проблемами производительности
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.service = :service AND a.performanceIssues > 0")
    List<EndpointAnalysis> findPerformanceIssuesByService(@Param("service") OpenApiService service);
    
    /**
     * Найти последние анализы по сервису
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.service = :service ORDER BY a.createdAt DESC")
    List<EndpointAnalysis> findLatestByService(@Param("service") OpenApiService service, Pageable pageable);
    
    /**
     * Найти завершенные анализы
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.status = 'COMPLETED' ORDER BY a.completedAt DESC")
    List<EndpointAnalysis> findCompletedAnalyses(Pageable pageable);
    
    /**
     * Найти неудачные анализы
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.status = 'FAILED' ORDER BY a.createdAt DESC")
    List<EndpointAnalysis> findFailedAnalyses(Pageable pageable);
    
    /**
     * Найти анализы с низким качеством
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.qualityScore < :threshold AND a.status = 'COMPLETED'")
    List<EndpointAnalysis> findLowQualityAnalyses(@Param("threshold") Double threshold);
    
    /**
     * Найти анализы по дате создания
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate")
    List<EndpointAnalysis> findByCreatedDateRange(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * Найти анализы по тегам
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE :tag MEMBER OF a.tags")
    List<EndpointAnalysis> findByTag(@Param("tag") String tag);
    
    /**
     * Получить статистику анализов по сервису
     */
    @Query("SELECT " +
           "COUNT(a) as total, " +
           "SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
           "SUM(CASE WHEN a.status = 'FAILED' THEN 1 ELSE 0 END) as failed, " +
           "AVG(a.qualityScore) as averageQuality, " +
           "SUM(a.issuesFound) as totalIssues " +
           "FROM EndpointAnalysis a WHERE a.service = :service")
    AnalysisStatistics getStatisticsByService(@Param("service") OpenApiService service);
    
    /**
     * Получить общую статистику анализов
     */
    @Query("SELECT " +
           "COUNT(a) as total, " +
           "SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
           "SUM(CASE WHEN a.status = 'RUNNING' THEN 1 ELSE 0 END) as running, " +
           "SUM(CASE WHEN a.status = 'FAILED' THEN 1 ELSE 0 END) as failed, " +
           "AVG(a.qualityScore) as averageQuality " +
           "FROM EndpointAnalysis a")
    AnalysisStatistics getOverallStatistics();
    
    /**
     * Найти анализы с высоким количеством проблем
     */
    @Query("SELECT a FROM EndpointAnalysis a WHERE a.issuesFound > :minIssues AND a.status = 'COMPLETED' ORDER BY a.issuesFound DESC")
    List<EndpointAnalysis> findHighIssueAnalyses(@Param("minIssues") Integer minIssues, Pageable pageable);
    
    /**
     * Проверить существование анализа с таким именем в сервисе
     */
    boolean existsByAnalysisNameAndService(String analysisName, OpenApiService service);
    
    /**
     * Найти пагинированный список анализов по сервису
     */
    Page<EndpointAnalysis> findByService(OpenApiService service, Pageable pageable);
    
    /**
     * DTO для статистики
     */
    interface AnalysisStatistics {
        Long getTotal();
        Long getCompleted();
        Long getRunning();
        Long getFailed();
        Double getAverageQuality();
        Long getTotalIssues();
    }
}