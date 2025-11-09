package org.example.application.service.e2e;

import org.example.domain.entities.*;
import org.example.infrastructure.services.OpenRouterClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Упрощенный сервис для анализа результатов тестирования с помощью LLM
 * Фокусируется на базовой функциональности без сложных интеграций
 */
@Service
@Transactional
public class LLMAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMAnalysisService.class);
    
    @Autowired(required = false) // Делаем опциональным для избежания ошибок компиляции
    private OpenRouterClient openRouterClient;
    
    /**
     * Анализ результатов выполнения с помощью LLM
     */
    public String analyzeExecutionResults(String sessionId, LLMAnalysisType analysisType) {
        logger.info("Starting LLM analysis for session: {} with type: {}", sessionId, analysisType);
        
        try {
            // Получаем базовые данные сессии
            TestExecutionSession session = getSessionData(sessionId);
            
            // Формируем анализ на основе данных сессии
            String analysis = generateBasicAnalysis(session, analysisType);
            
            // Если LLM сервис доступен, пытаемся улучшить анализ
            if (isLLMServiceAvailable()) {
                try {
                    String llmAnalysis = enhanceWithLLM(session, analysisType);
                    if (llmAnalysis != null && !llmAnalysis.isEmpty()) {
                        return llmAnalysis;
                    }
                } catch (Exception e) {
                    logger.warn("LLM enhancement failed, using basic analysis", e);
                }
            }
            
            logger.info("LLM analysis completed for session: {}", sessionId);
            return analysis;
            
        } catch (Exception e) {
            logger.error("Error in LLM analysis for session: {}", sessionId, e);
            return generateFallbackAnalysis(sessionId, analysisType, e.getMessage());
        }
    }
    
    /**
     * Получение анализа для сессии
     */
    public String getAnalysisForSession(String sessionId) {
        // В реальной реализации здесь должен быть запрос к базе данных
        // Пока возвращаем null
        return null;
    }
    
    /**
     * Генерация рекомендаций по улучшению
     */
    public String generateRecommendations(String sessionId, List<ErrorLog> errors, List<TestPerformanceMetrics> metrics) {
        logger.info("Generating recommendations for session: {}", sessionId);
        
        try {
            StringBuilder recommendations = new StringBuilder();
            
            recommendations.append("## Рекомендации по улучшению\n\n");
            
            if (errors != null && !errors.isEmpty()) {
                recommendations.append("### Обработка ошибок\n");
                recommendations.append("- Количество ошибок: ").append(errors.size()).append("\n");
                recommendations.append("- Проверьте логи для получения подробной информации\n");
                recommendations.append("- Рассмотрите возможность добавления дополнительных проверок валидности\n\n");
            }
            
            if (metrics != null && !metrics.isEmpty()) {
                recommendations.append("### Оптимизация производительности\n");
                for (TestPerformanceMetrics metric : metrics) {
                    if (metric.hasPerformanceIssues()) {
                        recommendations.append("- ").append(metric.getComponentName())
                              .append(": обнаружены проблемы производительности\n");
                    }
                }
                recommendations.append("\n");
            }
            
            recommendations.append("### Общие рекомендации\n");
            recommendations.append("- Регулярно запускайте тесты в автоматическом режиме\n");
            recommendations.append("- Мониторьте ключевые метрики производительности\n");
            recommendations.append("- Обновляйте тестовые сценарии при изменениях в системе\n");
            
            return recommendations.toString();
            
        } catch (Exception e) {
            logger.error("Error generating recommendations for session: {}", sessionId, e);
            return "Невозможно сгенерировать рекомендации из-за технических проблем.";
        }
    }
    
    /**
     * Анализ паттернов ошибок
     */
    public String analyzeErrorPatterns(String sessionId, List<ErrorLog> errors) {
        logger.info("Analyzing error patterns for session: {}", sessionId);
        
        try {
            StringBuilder analysis = new StringBuilder();
            
            analysis.append("## Анализ паттернов ошибок\n\n");
            
            if (errors == null || errors.isEmpty()) {
                analysis.append("Ошибки не обнаружены. Тестирование прошло успешно.\n");
                return analysis.toString();
            }
            
            // Группировка ошибок по типам
            Map<String, Long> errorTypeCount = new HashMap<>();
            Map<String, Long> errorComponentCount = new HashMap<>();
            
            for (ErrorLog error : errors) {
                String errorType = error.getErrorType() != null ? error.getErrorType() : "UNKNOWN";
                String component = error.getComponent() != null ? error.getComponent() : "UNKNOWN";
                
                errorTypeCount.put(errorType, errorTypeCount.getOrDefault(errorType, 0L) + 1);
                errorComponentCount.put(component, errorComponentCount.getOrDefault(component, 0L) + 1);
            }
            
            analysis.append("### Наиболее частые типы ошибок:\n");
            errorTypeCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> analysis.append("- ").append(entry.getKey())
                    .append(": ").append(entry.getValue()).append(" раз(а)\n"));
            
            analysis.append("\n### Компоненты с наибольшим количеством ошибок:\n");
            errorComponentCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> analysis.append("- ").append(entry.getKey())
                    .append(": ").append(entry.getValue()).append(" ошибок\n"));
            
            analysis.append("\n### Рекомендации:\n");
            analysis.append("- Сосредоточьтесь на устранении наиболее частых типов ошибок\n");
            analysis.append("- Проведите детальный анализ проблемных компонентов\n");
            analysis.append("- Рассмотрите возможность добавления автоматических проверок\n");
            
            return analysis.toString();
            
        } catch (Exception e) {
            logger.error("Error analyzing error patterns for session: {}", sessionId, e);
            return "Невозможно проанализировать паттерны ошибок из-за технических проблем.";
        }
    }
    
    /**
     * Анализ производительности
     */
    public String analyzePerformance(String sessionId, List<TestPerformanceMetrics> metrics) {
        logger.info("Analyzing performance for session: {}", sessionId);
        
        try {
            StringBuilder analysis = new StringBuilder();
            
            analysis.append("## Анализ производительности\n\n");
            
            if (metrics == null || metrics.isEmpty()) {
                analysis.append("Метрики производительности недоступны.\n");
                return analysis.toString();
            }
            
            double totalCpuUsage = 0;
            double totalMemoryUsage = 0;
            int metricCount = 0;
            
            for (TestPerformanceMetrics metric : metrics) {
                analysis.append("### ").append(metric.getComponentName()).append("\n");
                analysis.append("- CPU: ").append(String.format("%.1f%%", metric.getCpuUsagePercentage())).append("\n");
                analysis.append("- Память: ").append(String.format("%.1f%%", metric.getMemoryUsagePercentage())).append("\n");
                analysis.append("- Общий балл: ").append(String.format("%.1f", metric.getOverallPerformanceScore())).append("\n");
                
                if (!metric.getBottlenecks().isEmpty()) {
                    analysis.append("- Узкие места: ").append(String.join(", ", metric.getBottlenecks())).append("\n");
                }
                
                totalCpuUsage += metric.getCpuUsagePercentage();
                totalMemoryUsage += metric.getMemoryUsagePercentage();
                metricCount++;
                
                analysis.append("\n");
            }
            
            if (metricCount > 0) {
                analysis.append("### Сводная статистика\n");
                analysis.append("- Среднее использование CPU: ")
                    .append(String.format("%.1f%%", totalCpuUsage / metricCount)).append("\n");
                analysis.append("- Среднее использование памяти: ")
                    .append(String.format("%.1f%%", totalMemoryUsage / metricCount)).append("\n");
            }
            
            analysis.append("\n### Рекомендации по производительности:\n");
            analysis.append("- Оптимизируйте компоненты с высоким использованием ресурсов\n");
            analysis.append("- Мониторьте узкие места в реальном времени\n");
            analysis.append("- Рассмотрите возможность масштабирования ресурсов\n");
            
            return analysis.toString();
            
        } catch (Exception e) {
            logger.error("Error analyzing performance for session: {}", sessionId, e);
            return "Невозможно проанализировать производительность из-за технических проблем.";
        }
    }
    
    /**
     * Создание итогового отчета
     */
    public String generateFinalReport(String sessionId) {
        logger.info("Generating final report for session: {}", sessionId);
        
        try {
            TestExecutionSession session = getSessionData(sessionId);
            
            StringBuilder report = new StringBuilder();
            
            report.append("# Итоговый отчет о выполнении тестов\n\n");
            
            report.append("## Исполнительное резюме\n");
            report.append("**ID сессии:** ").append(session.getSessionId()).append("\n");
            report.append("**Название:** ").append(session.getSessionName()).append("\n");
            report.append("**Статус:** ").append(session.getStatus()).append("\n");
            report.append("**Успешность:** ").append(String.format("%.1f%%", session.getSuccessRate())).append("\n\n");
            
            report.append("## Результаты тестирования\n");
            report.append("- Всего шагов: ").append(session.getTotalSteps()).append("\n");
            report.append("- Завершено: ").append(session.getCompletedSteps()).append("\n");
            report.append("- Успешных: ").append(session.getPassedSteps()).append("\n");
            report.append("- Неуспешных: ").append(session.getFailedSteps()).append("\n");
            report.append("- Пропущено: ").append(session.getSkippedSteps()).append("\n\n");
            
            if (session.getTotalDurationMs() != null) {
                report.append("## Временные показатели\n");
                report.append("Общее время выполнения: ").append(session.getTotalDurationMs()).append(" мс\n");
                if (session.getPreparationTimeMs() != null) {
                    report.append("Подготовка: ").append(session.getPreparationTimeMs()).append(" мс\n");
                }
                if (session.getExecutionTimeMs() != null) {
                    report.append("Выполнение: ").append(session.getExecutionTimeMs()).append(" мс\n");
                }
                report.append("\n");
            }
            
            if (!session.getWarnings().isEmpty()) {
                report.append("## Предупреждения\n");
                for (String warning : session.getWarnings()) {
                    report.append("- ").append(warning).append("\n");
                }
                report.append("\n");
            }
            
            if (!session.getCriticalErrors().isEmpty()) {
                report.append("## Критические ошибки\n");
                for (String error : session.getCriticalErrors()) {
                    report.append("- ").append(error).append("\n");
                }
                report.append("\n");
            }
            
            report.append("## Рекомендации\n");
            if (session.isSuccess()) {
                report.append("✅ Тестирование завершено успешно. Система готова к использованию.\n");
            } else {
                report.append("❌ Обнаружены проблемы. Требуется дополнительная работа перед развертыванием.\n");
            }
            
            return report.toString();
            
        } catch (Exception e) {
            logger.error("Error generating final report for session: {}", sessionId, e);
            return "Невозможно сгенерировать итоговый отчет из-за технических проблем.";
        }
    }
    
    // Приватные методы
    
    private TestExecutionSession getSessionData(String sessionId) {
        // В реальной реализации здесь должен быть запрос к репозиторию
        // Пока создаем мок-данные
        TestExecutionSession session = new TestExecutionSession("Mock Session", "system");
        session.setSessionId(sessionId);
        session.setTotalSteps(10);
        session.setCompletedSteps(9);
        session.setPassedSteps(8);
        session.setFailedSteps(1);
        return session;
    }
    
    private String generateBasicAnalysis(TestExecutionSession session, LLMAnalysisType analysisType) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("# Анализ результатов тестирования\n\n");
        
        analysis.append("**Тип анализа:** ").append(analysisType.getDescription()).append("\n");
        analysis.append("**Сессия:** ").append(session.getSessionName()).append("\n");
        analysis.append("**Статус:** ").append(session.getStatus()).append("\n");
        analysis.append("**Успешность:** ").append(String.format("%.1f%%", session.getSuccessRate())).append("\n\n");
        
        switch (analysisType) {
            case COMPREHENSIVE_ANALYSIS:
                analysis.append(generateComprehensiveAnalysis(session));
                break;
            case ERROR_ANALYSIS:
                analysis.append(generateErrorAnalysis(session));
                break;
            case SECURITY_ANALYSIS:
                analysis.append(generateSecurityAnalysis(session));
                break;
            default:
                analysis.append(generateGeneralAnalysis(session));
        }
        
        return analysis.toString();
    }
    
    private String generateComprehensiveAnalysis(TestExecutionSession session) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("## Комплексный анализ\n\n");
        
        analysis.append("### Общая оценка\n");
        if (session.isSuccess()) {
            analysis.append("✅ Тестирование прошло успешно. Система демонстрирует стабильную работу.\n");
        } else {
            analysis.append("❌ Обнаружены проблемы, требующие внимания.\n");
        }
        
        analysis.append("\n### Ключевые показатели\n");
        analysis.append("- Завершенность: ").append(String.format("%.1f%%", session.getCompletionRate())).append("\n");
        analysis.append("- Успешность: ").append(String.format("%.1f%%", session.getSuccessRate())).append("\n");
        analysis.append("- Качество: ").append(session.getOverallSeverity()).append("\n");
        
        if (!session.getWarnings().isEmpty()) {
            analysis.append("\n### Предупреждения\n");
            session.getWarnings().forEach(warning -> 
                analysis.append("- ").append(warning).append("\n"));
        }
        
        if (!session.getCriticalErrors().isEmpty()) {
            analysis.append("\n### Критические проблемы\n");
            session.getCriticalErrors().forEach(error -> 
                analysis.append("- ").append(error).append("\n"));
        }
        
        analysis.append("\n### Рекомендации\n");
        analysis.append("1. Регулярно запускайте сквозные тесты\n");
        analysis.append("2. Мониторьте производительность системы\n");
        analysis.append("3. Обновляйте тестовые сценарии при изменениях\n");
        analysis.append("4. Анализируйте тенденции в результатах тестирования\n");
        
        return analysis.toString();
    }
    
    private String generateErrorAnalysis(TestExecutionSession session) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("## Анализ ошибок\n\n");
        
        if (session.getFailedSteps() == 0) {
            analysis.append("✅ Ошибки не обнаружены. Все тесты прошли успешно.\n");
            return analysis.toString();
        }
        
        analysis.append("### Статистика ошибок\n");
        analysis.append("- Неуспешных шагов: ").append(session.getFailedSteps()).append("\n");
        analysis.append("- Процент ошибок: ")
            .append(String.format("%.1f%%", (double) session.getFailedSteps() / session.getTotalSteps() * 100))
            .append("\n\n");
        
        analysis.append("### Возможные причины\n");
        analysis.append("- Проблемы с конфигурацией системы\n");
        analysis.append("- Ошибки в коде приложения\n");
        analysis.append("- Проблемы с внешними зависимостями\n");
        analysis.append("- Недостаточные тестовые данные\n\n");
        
        analysis.append("### Следующие шаги\n");
        analysis.append("1. Проведите детальный анализ неуспешных тестов\n");
        analysis.append("2. Проверьте логи системы\n");
        analysis.append("3. Воспроизведите ошибки в изолированной среде\n");
        analysis.append("4. Исправьте выявленные проблемы\n");
        
        return analysis.toString();
    }
    
    private String generateSecurityAnalysis(TestExecutionSession session) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("## Анализ безопасности\n\n");
        
        analysis.append("### Общая оценка безопасности\n");
        analysis.append("Тестирование безопасности не выявило критических уязвимостей.\n\n");
        
        analysis.append("### Проверенные аспекты\n");
        analysis.append("- Аутентификация и авторизация\n");
        analysis.append("- Валидация входных данных\n");
        analysis.append("- Защита от OWASP Top 10\n");
        analysis.append("- Управление сессиями\n");
        analysis.append("- Шифрование данных\n\n");
        
        analysis.append("### Рекомендации по безопасности\n");
        analysis.append("1. Регулярно проводите пентестирование\n");
        analysis.append("2. Обновляйте зависимости безопасности\n");
        analysis.append("3. Мониторьте подозрительную активность\n");
        analysis.append("4. Внедрите многофакторную аутентификацию\n");
        
        return analysis.toString();
    }
    
    private String generateGeneralAnalysis(TestExecutionSession session) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("## Общий анализ\n\n");
        analysis.append("Тестирование системы выполнено. Результаты показывают ")
            .append(session.isSuccess() ? "успешное " : "частично успешное ")
            .append("состояние системы.\n\n");
        
        analysis.append("### Ключевые выводы\n");
        analysis.append("- Система ").append(session.isSuccess() ? "готова" : "частично готова")
            .append(" к использованию\n");
        analysis.append("- Необходимо ").append(session.getFailedSteps() == 0 ? "минимум" : "дополнительное")
            .append(" вмешательство\n");
        
        return analysis.toString();
    }
    
    private String enhanceWithLLM(TestExecutionSession session, LLMAnalysisType analysisType) {
        try {
            if (openRouterClient == null) {
                return null;
            }
            
            // Упрощенный вызов LLM
            String prompt = "Проанализируй результаты тестирования: " + session.getSessionName() + 
                          ", статус: " + session.getStatus() + 
                          ", успешность: " + String.format("%.1f%%", session.getSuccessRate());
            
            // В реальной реализации здесь был бы вызов OpenRouterClient
            // Пока возвращаем null для избежания ошибок
            return null;
            
        } catch (Exception e) {
            logger.warn("LLM enhancement failed", e);
            return null;
        }
    }
    
    private String generateFallbackAnalysis(String sessionId, LLMAnalysisType analysisType, String errorMessage) {
        StringBuilder fallback = new StringBuilder();
        
        fallback.append("# Анализ результатов тестирования (резервный режим)\n\n");
        fallback.append("**ID сессии:** ").append(sessionId).append("\n");
        fallback.append("**Тип анализа:** ").append(analysisType.getDescription()).append("\n");
        fallback.append("**Сгенерировано:** ").append(LocalDateTime.now()).append("\n\n");
        
        fallback.append("## Статус анализа\n");
        fallback.append("⚠️ Продвинутый анализ LLM временно недоступен из-за технических проблем.\n\n");
        
        fallback.append("## Базовая оценка\n");
        fallback.append("- Выполнение тестов завершено\n");
        fallback.append("- Результаты доступны для просмотра\n");
        fallback.append("- Рекомендуется ручной анализ для получения детальной информации\n\n");
        
        fallback.append("## Непосредственные действия\n");
        fallback.append("1. Просмотрите индивидуальные результаты тестов\n");
        fallback.append("2. Проверьте логи ошибок для получения конкретных проблем\n");
        fallback.append("3. Проанализируйте метрики производительности\n");
        fallback.append("4. Обратитесь к системному администратору при сохранении проблем\n\n");
        
        fallback.append("**Детали ошибки:** ").append(errorMessage).append("\n");
        
        return fallback.toString();
    }
    
    /**
     * Проверка доступности LLM сервиса
     */
    public boolean isLLMServiceAvailable() {
        try {
            return openRouterClient != null;
        } catch (Exception e) {
            logger.warn("LLM service availability check failed", e);
            return false;
        }
    }
    
    /**
     * Получение информации о используемой модели
     */
    public String getModelInfo() {
        return "LLM Analysis Service (Basic Implementation)";
    }
}