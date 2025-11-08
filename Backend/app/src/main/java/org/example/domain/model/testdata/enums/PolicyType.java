package org.example.domain.model.testdata.enums;

/**
 * Типы политик для тестовых данных
 */
public enum PolicyType {
    /**
     * Политика конфиденциальности
     */
    PRIVACY("privacy"),
    
    /**
     * Политика безопасности
     */
    SECURITY("security"),
    
    /**
     * Политика соответствия требованиям
     */
    COMPLIANCE("compliance"),
    
    /**
     * Политика доступа к данным
     */
    ACCESS_CONTROL("access_control"),
    
    /**
     * Политика хранения данных
     */
    STORAGE("storage"),
    
    /**
     * Политика шифрования
     */
    ENCRYPTION("encryption"),
    
    /**
     * Политика маскирования данных
     */
    MASKING("masking"),
    
    /**
     * Политика очистки данных
     */
    DATA_RETENTION("data_retention"),
    
    /**
     * Политика аудита
     */
    AUDIT("audit"),
    
    /**
     * Политика бэкапа
     */
    BACKUP("backup"),
    
    /**
     * Политика управления версиями
     */
    VERSION_CONTROL("version_control"),
    
    /**
     * Политика качества данных
     */
    DATA_QUALITY("data_quality"),
    
    /**
     * Политика синхронизации
     */
    SYNCHRONIZATION("synchronization"),
    
    /**
     * Политика изоляции данных
     */
    ISOLATION("isolation");
    
    private final String value;
    
    PolicyType(String value) {
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
     * Проверяет, является ли политика критически важной
     */
    public boolean isCritical() {
        return this == SECURITY || this == COMPLIANCE || this == ENCRYPTION;
    }
    
    /**
     * Проверяет, связана ли политика с защитой данных
     */
    public boolean isDataProtection() {
        return this == PRIVACY || this == SECURITY || this == ENCRYPTION || 
               this == MASKING || this == ACCESS_CONTROL;
    }
    
    /**
     * Проверяет, связана ли политика с управлением жизненным циклом
     */
    public boolean isLifecycleManagement() {
        return this == STORAGE || this == DATA_RETENTION || this == BACKUP || 
               this == VERSION_CONTROL || this == AUDIT;
    }
    
    /**
     * Проверяет, связана ли политика с качеством данных
     */
    public boolean isDataQuality() {
        return this == DATA_QUALITY || this == COMPLIANCE;
    }
    
    /**
     * Получает приоритет политики
     */
    public PolicyPriority getPriority() {
        switch (this) {
            case SECURITY:
            case COMPLIANCE:
            case ENCRYPTION:
                return PolicyPriority.HIGHEST;
            case PRIVACY:
            case ACCESS_CONTROL:
            case AUDIT:
                return PolicyPriority.HIGH;
            case STORAGE:
            case MASKING:
            case DATA_RETENTION:
                return PolicyPriority.MEDIUM;
            case BACKUP:
            case VERSION_CONTROL:
            case DATA_QUALITY:
                return PolicyPriority.MEDIUM_LOW;
            case SYNCHRONIZATION:
            case ISOLATION:
                return PolicyPriority.LOW;
            default:
                return PolicyPriority.MEDIUM;
        }
    }
    
    /**
     * Приоритеты политик
     */
    public enum PolicyPriority {
        LOWEST("lowest"),
        LOW("low"),
        MEDIUM_LOW("medium_low"),
        MEDIUM("medium"),
        HIGH("high"),
        HIGHEST("highest");
        
        private final String value;
        
        PolicyPriority(String value) {
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