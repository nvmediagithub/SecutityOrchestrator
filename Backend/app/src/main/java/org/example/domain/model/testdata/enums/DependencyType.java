package org.example.domain.model.testdata.enums;

/**
 * Типы зависимостей между тестовыми данными
 */
public enum DependencyType {
    /**
     * Прямая зависимость - данные B требуют данных A для создания
     */
    DEPENDENCY("dependency"),
    
    /**
     * Предварительное условие - данные A должны быть созданы до данных B
     */
    PREREQUISITE("prerequisite"),
    
    /**
     * Ссылочная зависимость - данные B ссылаются на данные A
     */
    REFERENCE("reference"),
    
    /**
     * Каскадная зависимость - удаление данных A влияет на данные B
     */
    CASCADE("cascade"),
    
    /**
     * Валидационная зависимость - данные B валидируются данными A
     */
    VALIDATION("validation"),
    
    /**
     * Трансформационная зависимость - данные B создаются на основе данных A
     */
    TRANSFORMATION("transformation");
    
    private final String value;
    
    DependencyType(String value) {
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
     * Проверяет, является ли зависимость строгой
     */
    public boolean isStrict() {
        return this == DEPENDENCY || this == PREREQUISITE || this == CASCADE;
    }
    
    /**
     * Проверяет, является ли зависимость мягкой
     */
    public boolean isSoft() {
        return this == REFERENCE || this == VALIDATION;
    }
    
    /**
     * Проверяет, является ли зависимость трансформационной
     */
    public boolean isTransformational() {
        return this == TRANSFORMATION;
    }
}