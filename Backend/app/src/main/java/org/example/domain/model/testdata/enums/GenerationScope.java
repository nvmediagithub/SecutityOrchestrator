package org.example.domain.model.testdata.enums;

/**
 * Области генерации тестовых данных
 */
public enum GenerationScope {
    /**
     * Глобальная область - данные доступны во всех тестах
     */
    GLOBAL("global"),
    
    /**
     * Область проекта - данные доступны в рамках проекта
     */
    PROJECT("project"),
    
    /**
     * Область модуля - данные доступны в рамках модуля
     */
    MODULE("module"),
    
    /**
     * Область компонента - данные доступны в рамках компонента
     */
    COMPONENT("component"),
    
    /**
     * Область тестового сценария - данные доступны в рамках сценария
     */
    SCENARIO("scenario"),
    
    /**
     * Область тестового случая - данные доступны в рамках тест-кейса
     */
    TEST_CASE("test_case"),
    
    /**
     * Область сессии - данные доступны в рамках тестовой сессии
     */
    SESSION("session"),
    
    /**
     * Область выполнения - данные доступны в рамках выполнения
     */
    EXECUTION("execution"),
    
    /**
     * Область контекста - данные доступны в рамках конкретного контекста
     */
    CONTEXT("context"),
    
    /**
     * Изолированная область - данные изолированы и недоступны другим
     */
    ISOLATED("isolated"),
    
    /**
     * Временная область - данные существуют только во время выполнения
     */
    TEMPORARY("temporary"),
    
    /**
     * Статическая область - данные неизменяемы
     */
    STATIC("static");
    
    private final String value;
    
    GenerationScope(String value) {
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
     * Проверяет, является ли область долговременной
     */
    public boolean isPersistent() {
        return this == GLOBAL || this == PROJECT || this == MODULE || 
               this == COMPONENT || this == SCENARIO || this == TEST_CASE;
    }
    
    /**
     * Проверяет, является ли область временной
     */
    public boolean isTemporary() {
        return this == SESSION || this == EXECUTION || this == TEMPORARY;
    }
    
    /**
     * Проверяет, является ли область изолированной
     */
    public boolean isIsolated() {
        return this == ISOLATED || this == CONTEXT || this == TEMPORARY;
    }
    
    /**
     * Проверяет, является ли область глобально доступной
     */
    public boolean isGlobal() {
        return this == GLOBAL;
    }
    
    /**
     * Проверяет, является ли область специфичной для теста
     */
    public boolean isTestSpecific() {
        return this == SCENARIO || this == TEST_CASE || this == EXECUTION;
    }
    
    /**
     * Получает иерархический уровень области
     */
    public ScopeLevel getLevel() {
        switch (this) {
            case GLOBAL:
                return ScopeLevel.SYSTEM;
            case PROJECT:
                return ScopeLevel.PROJECT;
            case MODULE:
                return ScopeLevel.MODULE;
            case COMPONENT:
                return ScopeLevel.COMPONENT;
            case SCENARIO:
            case TEST_CASE:
                return ScopeLevel.TEST;
            case SESSION:
            case EXECUTION:
                return ScopeLevel.RUNTIME;
            case CONTEXT:
            case ISOLATED:
            case TEMPORARY:
            case STATIC:
                return ScopeLevel.SPECIALIZED;
            default:
                return ScopeLevel.TEST;
        }
    }
    
    /**
     * Уровни области видимости
     */
    public enum ScopeLevel {
        SYSTEM("system"),
        PROJECT("project"),
        MODULE("module"),
        COMPONENT("component"),
        TEST("test"),
        RUNTIME("runtime"),
        SPECIALIZED("specialized");
        
        private final String value;
        
        ScopeLevel(String value) {
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