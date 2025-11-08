package org.example.domain.entities.bpmn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.entities.AbstractEntity;

import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a BPMN Task element
 */
@Entity
@Table(name = "bpmn_tasks")
public class BpmnTask extends AbstractEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(name = "task_id", nullable = false)
    private String taskId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private BusinessProcess process;

    @Column(name = "task_type")
    @Enumerated(EnumType.STRING)
    private TaskType taskType = TaskType.USER_TASK;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @Column(name = "implementation")
    private String implementation;

    @Column(name = "form_key")
    private String formKey;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "candidate_users", columnDefinition = "TEXT")
    private String candidateUsers;

    @Column(name = "candidate_groups", columnDefinition = "TEXT")
    private String candidateGroups;

    @Column(name = "due_date")
    private String dueDate;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "is_async")
    private boolean async = false;

    @Column(name = "is_exclusive")
    private boolean exclusive = true;

    @Column(name = "is_collection")
    private boolean collection = false;

    @Column(name = "element_index")
    private Integer elementIndex;

    @Column(name = "x_position")
    private Double xPosition;

    @Column(name = "y_position")
    private Double yPosition;

    @Column(name = "width")
    private Double width;

    @Column(name = "height")
    private Double height;

    @OneToMany(mappedBy = "sourceTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnSequence> outgoingSequences;

    @OneToMany(mappedBy = "targetTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnSequence> incomingSequences;

    public BpmnTask() {}

    public BpmnTask(String name, String taskId, BusinessProcess process) {
        this.name = name;
        this.taskId = taskId;
        this.process = process;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public BusinessProcess getProcess() {
        return process;
    }

    public void setProcess(BusinessProcess process) {
        this.process = process;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getCandidateUsers() {
        return candidateUsers;
    }

    public void setCandidateUsers(String candidateUsers) {
        this.candidateUsers = candidateUsers;
    }

    public String getCandidateGroups() {
        return candidateGroups;
    }

    public void setCandidateGroups(String candidateGroups) {
        this.candidateGroups = candidateGroups;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    public Integer getElementIndex() {
        return elementIndex;
    }

    public void setElementIndex(Integer elementIndex) {
        this.elementIndex = elementIndex;
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

    public List<BpmnSequence> getOutgoingSequences() {
        return outgoingSequences;
    }

    public void setOutgoingSequences(List<BpmnSequence> outgoingSequences) {
        this.outgoingSequences = outgoingSequences;
    }

    public List<BpmnSequence> getIncomingSequences() {
        return incomingSequences;
    }

    public void setIncomingSequences(List<BpmnSequence> incomingSequences) {
        this.incomingSequences = incomingSequences;
    }

    /**
     * Task type enumeration
     */
    public enum TaskType {
        USER_TASK,
        SERVICE_TASK,
        SCRIPT_TASK,
        BUSINESS_RULE_TASK,
        SEND_TASK,
        RECEIVE_TASK,
        SUB_PROCESS,
        CALL_ACTIVITY,
        TRANSACTION,
        PARALLEL_GATEWAY,
        EXCLUSIVE_GATEWAY,
        INCLUSIVE_GATEWAY,
        EVENT_BASED_GATEWAY
    }
}