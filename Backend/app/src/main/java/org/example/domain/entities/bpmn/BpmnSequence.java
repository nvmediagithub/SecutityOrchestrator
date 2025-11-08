package org.example.domain.entities.bpmn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.entities.AbstractEntity;

import java.util.UUID;

/**
 * Domain entity representing a BPMN Sequence Flow element
 */
@Entity
@Table(name = "bpmn_sequences")
public class BpmnSequence extends AbstractEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(name = "sequence_id", nullable = false)
    private String sequenceId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private BusinessProcess process;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_task_id", nullable = false)
    private BpmnTask sourceTask;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_task_id", nullable = false)
    private BpmnTask targetTask;

    @Size(max = 255)
    @Column(name = "source_ref")
    private String sourceRef;

    @Size(max = 255)
    @Column(name = "target_ref")
    private String targetRef;

    @Size(max = 1000)
    @Column(length = 1000)
    private String name;

    @Size(max = 1000)
    @Column(name = "condition_expression", length = 1000)
    private String conditionExpression;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @Column(name = "is_catch_all")
    private boolean isCatchAll = false;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "element_index")
    private Integer elementIndex;

    // Visual properties
    @ElementCollection
    @CollectionTable(name = "bpmn_sequence_waypoints", 
                    joinColumns = @JoinColumn(name = "sequence_id"))
    @Column(name = "waypoint_data")
    private java.util.List<String> waypoints;

    @Column(name = "x_position")
    private Double xPosition;

    @Column(name = "y_position")
    private Double yPosition;

    @Column(name = "width")
    private Double width;

    @Column(name = "height")
    private Double height;

    // Color and style properties
    @Column(name = "stroke_color")
    private String strokeColor;

    @Column(name = "stroke_width")
    private Double strokeWidth;

    @Column(name = "stroke_dash_array")
    private String strokeDashArray;

    public BpmnSequence() {}

    public BpmnSequence(String sequenceId, BpmnTask sourceTask, BpmnTask targetTask, BusinessProcess process) {
        this.sequenceId = sequenceId;
        this.sourceTask = sourceTask;
        this.targetTask = targetTask;
        this.process = process;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public BusinessProcess getProcess() {
        return process;
    }

    public void setProcess(BusinessProcess process) {
        this.process = process;
    }

    public BpmnTask getSourceTask() {
        return sourceTask;
    }

    public void setSourceTask(BpmnTask sourceTask) {
        this.sourceTask = sourceTask;
    }

    public BpmnTask getTargetTask() {
        return targetTask;
    }

    public void setTargetTask(BpmnTask targetTask) {
        this.targetTask = targetTask;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isCatchAll() {
        return isCatchAll;
    }

    public void setCatchAll(boolean catchAll) {
        isCatchAll = catchAll;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getElementIndex() {
        return elementIndex;
    }

    public void setElementIndex(Integer elementIndex) {
        this.elementIndex = elementIndex;
    }

    public java.util.List<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(java.util.List<String> waypoints) {
        this.waypoints = waypoints;
    }

    public Double getxPosition() {
        return xPosition;
    }

    public void setxPosition(Double xPosition) {
        this.xPosition = xPosition;
    }

    public Double getyPosition() {
        return yPosition;
    }

    public void setyPosition(Double yPosition) {
        this.yPosition = yPosition;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(Double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getStrokeDashArray() {
        return strokeDashArray;
    }

    public void setStrokeDashArray(String strokeDashArray) {
        this.strokeDashArray = strokeDashArray;
    }
}