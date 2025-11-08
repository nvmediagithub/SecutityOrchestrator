package org.example.domain.model.testdata.valueobjects;

import java.util.Objects;

/**
 * Value object representing the strength of a data dependency
 */
public final class DependencyStrength {
    private final String value;
    private final int level; // 1-5 scale
    
    private DependencyStrength(String value, int level) {
        this.value = value;
        this.level = level;
    }
    
    /**
     * Creates a weak dependency strength
     */
    public static DependencyStrength weak() {
        return new DependencyStrength("weak", 1);
    }
    
    /**
     * Creates a medium-low dependency strength
     */
    public static DependencyStrength mediumLow() {
        return new DependencyStrength("medium_low", 2);
    }
    
    /**
     * Creates a medium dependency strength
     */
    public static DependencyStrength medium() {
        return new DependencyStrength("medium", 3);
    }
    
    /**
     * Creates a medium-high dependency strength
     */
    public static DependencyStrength mediumHigh() {
        return new DependencyStrength("medium_high", 4);
    }
    
    /**
     * Creates a strong dependency strength
     */
    public static DependencyStrength strong() {
        return new DependencyStrength("strong", 5);
    }
    
    /**
     * Creates a dependency strength from custom value and level
     */
    public static DependencyStrength of(String value, int level) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Level must be between 1 and 5");
        }
        return new DependencyStrength(value, level);
    }
    
    /**
     * Creates a dependency strength from string value
     */
    public static DependencyStrength fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        
        switch (value.toLowerCase().trim()) {
            case "weak":
                return weak();
            case "medium_low":
                return mediumLow();
            case "medium":
                return medium();
            case "medium_high":
                return mediumHigh();
            case "strong":
                return strong();
            default:
                return of(value, 3); // Default to medium strength
        }
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Checks if this strength is weaker than another
     */
    public boolean isWeakerThan(DependencyStrength other) {
        return this.level < other.level;
    }
    
    /**
     * Checks if this strength is stronger than another
     */
    public boolean isStrongerThan(DependencyStrength other) {
        return this.level > other.level;
    }
    
    /**
     * Checks if this strength is equal to another
     */
    public boolean isEqualTo(DependencyStrength other) {
        return this.level == other.level;
    }
    
    /**
     * Checks if this is a weak strength
     */
    public boolean isWeak() {
        return level <= 2;
    }
    
    /**
     * Checks if this is a strong strength
     */
    public boolean isStrong() {
        return level >= 4;
    }
    
    /**
     * Checks if this is a critical strength
     */
    public boolean isCritical() {
        return level == 5;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DependencyStrength that = (DependencyStrength) obj;
        return level == that.level && Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value, level);
    }
    
    @Override
    public String toString() {
        return value + " (level " + level + ")";
    }
}