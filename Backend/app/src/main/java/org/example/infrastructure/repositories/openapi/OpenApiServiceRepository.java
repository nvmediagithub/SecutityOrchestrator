package org.example.infrastructure.repositories.openapi;

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
 * Репозиторий для работы с OpenApiService
 */
@Repository
public interface OpenApiServiceRepository extends JpaRepository<OpenApiService, UUID> {
    
    /**
     * Найти сервис по имени
     */
    Optional<OpenApiService> findByServiceName(String serviceName);
    
    /**
     * Найти все активные сервисы
     */
    List<OpenApiService> findByIsActiveTrue();
    
    /**
     * Найти сервис по URL спецификации
     */
    Optional<OpenApiService> findBySpecificationUrl(String specificationUrl);
    
    /**
     * Найти сервисы по статусу парсинга
     */
    List<OpenApiService> findByParseStatus(OpenApiService.ParseStatus parseStatus);
    
    /**
     * Найти сервисы по версии OpenAPI
     */
    List<OpenApiService> findByOpenApiVersion(org.example.domain.valueobjects.OpenApiVersion openApiVersion);
    
    /**
     * Найти сервисы по тегам
     */
    @Query("SELECT s FROM OpenApiService s WHERE s.tags LIKE %:tag%")
    List<OpenApiService> findByTagsContaining(@Param("tag") String tag);
    
    /**
     * Найти сервисы по заголовку или описанию
     */
    @Query("SELECT s FROM OpenApiService s WHERE " +
           "LOWER(s.serviceTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<OpenApiService> findByTitleOrDescriptionContaining(@Param("query") String query);
    
    /**
     * Найти сервисы, которые нужно обновить
     */
    @Query("SELECT s FROM OpenApiService s WHERE s.lastParsedAt < :cutoffDate")
    List<OpenApiService> findServicesNeedingUpdate(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Получить статистику по сервисам
     */
    @Query("SELECT " +
           "COUNT(s) as total, " +
           "SUM(CASE WHEN s.isActive = true THEN 1 ELSE 0 END) as active, " +
           "SUM(CASE WHEN s.parseStatus = 'SUCCESS' THEN 1 ELSE 0 END) as successfullyParsed, " +
           "SUM(CASE WHEN s.parseStatus = 'ERROR' THEN 1 ELSE 0 END) as failedToParse " +
           "FROM OpenApiService s")
    ServiceStatistics getStatistics();
    
    /**
     * Найти последние обновленные сервисы
     */
    @Query("SELECT s FROM OpenApiService s ORDER BY s.updatedAt DESC")
    List<OpenApiService> findLatestUpdated(Pageable pageable);
    
    /**
     * Проверить существование сервиса с таким именем
     */
    boolean existsByServiceName(String serviceName);
    
    /**
     * Проверить существование сервиса с таким URL
     */
    boolean existsBySpecificationUrl(String specificationUrl);
    
    /**
     * Найти пагинированный список сервисов
     */
    Page<OpenApiService> findAll(Pageable pageable);
    
    /**
     * Найти пагинированный список активных сервисов
     */
    Page<OpenApiService> findByIsActiveTrue(Pageable pageable);
    
    /**
     * DTO для статистики
     */
    interface ServiceStatistics {
        Long getTotal();
        Long getActive();
        Long getSuccessfullyParsed();
        Long getFailedToParse();
    }
}