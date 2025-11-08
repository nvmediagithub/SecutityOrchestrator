package org.example.domain.dto.context;

/**
 * Переменная процесса BPMN
 */
public class ProcessVariable {
    
    private String variableId;
    private String name;
    private String type;
    private Object defaultValue;
    private boolean isInput;
    private boolean isOutput;
    private String sourceTask;
    private String targetTask;
    
    public ProcessVariable() {
        this.variableId = "var_" + System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getVariableId() { return variableId; }
    public void setVariableId(String variableId) { this.variableId = variableId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Object getDefaultValue() { return defaultValue; }
    public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
    
    public boolean isInput() { return isInput; }
    public void setInput(boolean input) { isInput = input; }
    
    public boolean isOutput() { return isOutput; }
    public void setOutput(boolean output) { isOutput = output; }
    
    public String getSourceTask() { return sourceTask; }
    public void setSourceTask(String sourceTask) { this.sourceTask = sourceTask; }
    
    public String getTargetTask() { return targetTask; }
    public void setTargetTask(String targetTask) { this.targetTask = targetTask; }
}