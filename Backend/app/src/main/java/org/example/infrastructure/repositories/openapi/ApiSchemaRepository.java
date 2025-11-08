package org.example.infrastructure.repositories.openapi;

import org.example.domain.entities.openapi.ApiSchema;
import org.example.domain.entities.openapi.OpenApiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с ApiSchema
 */
@Repository
public interface ApiSchemaRepository extends JpaRepository<ApiSchema, UUID> {
    
    /**
     * Найти схемы по сервису
     */
    List<ApiSchema> findByService(OpenApiService service);
    
    /**
     * Найти схемы по ID сервиса
     */
    List<ApiSchema> findByServiceId(UUID serviceId);
    
    /**
     * Найти схему по имени и сервису
     */
    Optional<ApiSchema> findByNameAndService(String name, OpenApiService service);
    
    /**
     * Найти схемы по типу
     */
    List<ApiSchema> findByType(String type);
    
    /**
     * Найти схемы по формату
     */
    List<ApiSchema> findByFormat(String format);
    
    /**
     * Найти схемы по сервису и типу
     */
    @Query("SELECT s FROM ApiSchema s WHERE s.service = :service AND s.type = :type")
    List<ApiSchema> findByServiceAndType(@Param("service") OpenApiService service, @Param("type") String type);
    
    /**
     * Найти схемы с определенным свойством
     */
    @Query("SELECT s FROM ApiSchema s WHERE s.properties LIKE %:propertyName%")
    List<ApiSchema> findByPropertyName(@Param("propertyName") String propertyName);
    
    /**
     * Найти схемы с определенным обязательным полем
     */
    @Query("SELECT s FROM ApiSchema s WHERE :field MEMBER OF s.requiredFields")
    List<ApiSchema> findByRequiredField(@Param("field") String field);
    
    /**
     * Поиск схем по имени или описанию
     */
    @Query("SELECT s FROM ApiSchema s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<ApiSchema> findByNameOrDescriptionContaining(@Param("query") String query);
    
    /**
     * Найти схемы, которые используются в эндпоинтах сервиса
     */
    @Query("SELECT DISTINCT s FROM ApiSchema s WHERE s.service = :service")
    List<ApiSchema> findByServiceWithEndpoints(@Param("service") OpenApiService service);
    
    /**
     * Получить статистику схем по сервису
     */
    @Query("SELECT " +
           "COUNT(s) as total, " +
           "COUNT(DISTINCT s.type) as uniqueTypes, " +
           "SUM(CASE WHEN s.isDeprecated = true THEN 1 ELSE 0 END) as deprecated " +
           "FROM ApiSchema s WHERE s.service = :service")
    SchemaStatistics getStatisticsByService(@Param("service") OpenApiService service);
    
    /**
     * Найти часто используемые схемы
     */
    @Query("SELECT s FROM ApiSchema s WHERE s.service = :service ORDER BY s.properties.size DESC")
    List<ApiSchema> findMostComplexSchemas(@Param("service") OpenApiService service, Pageable pageable);
    
    /**
     * Найти устаревшие схемы
     */
    List<ApiSchema> findByIsDeprecatedTrue();
    
    /**
     * Найти абстрактные схемы
     */
    List<ApiSchema> findByIsAbstractTrue();
    
    /**
     * Проверить существование схемы с таким именем в сервисе
     */
    boolean existsByNameAndService(String name, OpenApiService service);
    
    /**
     * Найти пагинированный список схем по сервису
     */
    Page<ApiSchema> findByService(OpenApiService service, Pageable pageable);
    
    /**
     * DTO для статистики
     */
    interface SchemaStatistics {
        Long getTotal();
        Long getUniqueTypes();
        Long getDeprecated();
    }
}