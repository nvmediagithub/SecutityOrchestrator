package org.example.domain.model.testdata.enums;

/**
 * Состояния цепочек данных
 */
public enum ChainState {
    /**
     * Активная цепочка - используется в тестах
     */
    ACTIVE("active"),
    
    /**
     * Неактивная цепочка - не используется, но доступна
     */
    INACTIVE("inactive"),
    
    /**
     * Устаревшая цепочка - заменена новой версией
     */
    DEPRECATED("deprecated"),
    
    /**
     * Архивная цепочка - сохранена для исторических целей
     */
    ARCHIVED("archived"),
    
    /**
     * Ошибка в цепочке - требует исправления
     */
    ERROR("error"),
    
    /**
     * В процессе создания/обновления
     */
    BUILDING("building"),
    
    /**
     * Приостановлена для обслуживания
     */
    MAINTENANCE("maintenance"),
    
    /**
     * В процессе валидации
     */
    VALIDATING("validating");
    
    private final String value;
    
    ChainState(String value) {
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
     * Проверяет, можно ли использовать цепочку
     */
    public boolean isUsable() {
        return this == ACTIVE;
    }
    
    /**
     * Проверяет, доступна ли цепочка для чтения
     */
    public boolean isAvailable() {
        return this == ACTIVE || this == INACTIVE;
    }
    
    /**
     * Проверяет, находится ли цепочка в процессе изменения
     */
    public boolean isTransient() {
        return this == BUILDING || this == VALIDATING;
    }
    
    /**
     * Проверяет, требует ли цепочка внимания
     */
    public boolean needsAttention() {
        return this == ERROR || this == MAINTENANCE;
    }
    
    /**
     * Получает приоритет для операций
     */
    public OperationPriority getPriority() {
        switch (this) {
            case ACTIVE:
                return OperationPriority.HIGH;
            case BUILDING:
            case VALIDATING:
                return OperationPriority.MEDIUM;
            case INACTIVE:
            case MAINTENANCE:
                return OperationPriority.LOW;
            case ERROR:
                return OperationPriority.CRITICAL;
            case DEPRECATED:
            case ARCHIVED:
                return OperationPriority.NONE;
            default:
                return OperationPriority.LOW;
        }
    }
    
    /**
     * Приоритеты операций для цепочек
     */
    public enum OperationPriority {
        NONE("none"),
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high"),
        CRITICAL("critical");
        
        private final String value;
        
        OperationPriority(String value) {
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