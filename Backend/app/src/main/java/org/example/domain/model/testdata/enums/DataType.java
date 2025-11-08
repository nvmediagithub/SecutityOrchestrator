package org.example.domain.model.testdata.enums;

/**
 * Типы тестовых данных для категоризации
 */
public enum DataType {
    /**
     * Персональные данные пользователей
     */
    PERSONAL("personal"),
    
    /**
     * Финансовые данные
     */
    FINANCIAL("financial"),
    
    /**
     * Данные безопасности
     */
    SECURITY("security"),
    
    /**
     * Бизнес-данные
     */
    BUSINESS("business"),
    
    /**
     * Технические данные
     */
    TECHNICAL("technical"),
    
    /**
     * Метаданные системы
     */
    METADATA("metadata"),
    
    /**
     * Логи и события
     */
    LOGS("logs"),
    
    /**
     * Конфигурационные данные
     */
    CONFIGURATION("configuration"),
    
    /**
     * Временные данные (временные ряды)
     */
    TEMPORAL("temporal"),
    
    /**
     * Географические данные
     */
    GEOGRAPHIC("geographic"),
    
    /**
     * Медиаданные
     */
    MEDIA("media"),
    
    /**
     * Документооборот
     */
    DOCUMENT("document"),
    
    /**
     * API данные
     */
    API("api"),
    
    /**
     * База данных
     */
    DATABASE("database"),
    
    /**
     * Файловые данные
     */
    FILE("file");
    
    private final String value;
    
    DataType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    /**
     * Проверяет, является ли тип данных чувствительным
     */
    public boolean isSensitive() {
        return this == PERSONAL || this == FINANCIAL || this == SECURITY;
    }
    
    /**
     * Проверяет, является ли тип данных структурированным
     */
    public boolean isStructured() {
        return this == BUSINESS || this == TECHNICAL || this == METADATA || 
               this == CONFIGURATION || this == API || this == DATABASE;
    }
    
    /**
     * Проверяет, является ли тип данных временным
     */
    public boolean isTemporal() {
        return this == TEMPORAL || this == LOGS;
    }
    
    /**
     * Проверяет, является ли тип данных внешним
     */
    public boolean isExternal() {
        return this == API || this == DATABASE || this == FILE || this == DOCUMENT;
    }
    
    /**
     * Получает уровень риска для типа данных
     */
    public RiskLevel getRiskLevel() {
        switch (this) {
            case PERSONAL:
                return RiskLevel.HIGH;
            case FINANCIAL:
                return RiskLevel.HIGH;
            case SECURITY:
                return RiskLevel.CRITICAL;
            case BUSINESS:
                return RiskLevel.MEDIUM;
            case TECHNICAL:
                return RiskLevel.LOW;
            case METADATA:
                return RiskLevel.LOW;
            case LOGS:
                return RiskLevel.MEDIUM;
            case CONFIGURATION:
                return RiskLevel.MEDIUM;
            case TEMPORAL:
                return RiskLevel.MEDIUM;
            case GEOGRAPHIC:
                return RiskLevel.HIGH;
            case MEDIA:
                return RiskLevel.MEDIUM;
            case DOCUMENT:
                return RiskLevel.MEDIUM;
            case API:
                return RiskLevel.MEDIUM;
            case DATABASE:
                return RiskLevel.HIGH;
            case FILE:
                return RiskLevel.MEDIUM;
            default:
                return RiskLevel.LOW;
        }
    }
    
    /**
     * Уровни риска для типов данных
     */
    public enum RiskLevel {
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high"),
        CRITICAL("critical");
        
        private final String value;
        
        RiskLevel(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
}