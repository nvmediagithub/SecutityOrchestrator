package org.example.domain.model.consistency;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Базовый интерфейс для проверки согласованности данных
 */
public interface ConsistencyChecker {
    
    /**
     * Выполняет проверку согласованности
     */
    CompletableFuture<ConsistencyResult> checkConsistency(ConsistencyCheckRequest request);
    
    /**
     * Возвращает статус проверки
     */
    CheckStatus getCheckStatus(String checkId);
    
    /**
     * Возвращает результаты проверки
     */
    ConsistencyResult getCheckResults(String checkId);
    
    /**
     * Очищает кэш результатов
     */
    void clearCache();
    
    /**
     * Получает статистику проверок
     */
    ConsistencyStatistics getStatistics();
    
    /**
     * Базовые константы для консистентности
     */
    interface Constants {
        int MAX_CONCURRENT_CHECKS = 2;
        long CACHE_TTL_HOURS = 24;
        long CHECK_TIMEOUT_MINUTES = 60;
        double DEFAULT_CONSISTENCY_SCORE = 85.0;
    }
}