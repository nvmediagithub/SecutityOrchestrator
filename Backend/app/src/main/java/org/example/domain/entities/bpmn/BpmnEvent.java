package org.example.domain.entities.bpmn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a BPMN Event element
 */
@Entity
@Table(name = "bpmn_events")
public class BpmnEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private BusinessProcess process;

    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private EventType eventType = EventType.START_EVENT;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @Column(name = "is_interrupting")
    private boolean isInterrupting = true;

    @Column(name = "is_parallel_multiple")
    private boolean isParallelMultiple = false;

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

    // Timer events
    @Column(name = "timer_value")
    private String timerValue;

    @Column(name = "timer_format")
    private String timerFormat;

    // Message events
    @Column(name = "message_ref")
    private String messageRef;

    @Column(name = "message_name")
    private String messageName;

    // Error events
    @Column(name = "error_ref")
    private String errorRef;

    @Column(name = "error_name")
    private String errorName;

    // Escalation events
    @Column(name = "escalation_code")
    private String escalationCode;

    @Column(name = "escalation_name")
    private String escalationName;

    // Signal events
    @Column(name = "signal_ref")
    private String signalRef;

    @Column(name = "signal_name")
    private String signalName;

    // Compensation events
    @Column(name = "activity_ref")
    private String activityRef;

    @Column(name = "wait_for_completion")
    private boolean waitForCompletion = true;

    // Incoming and outgoing sequence flows
    @OneToMany(mappedBy = "targetEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnSequence> incomingSequences;

    @OneToMany(mappedBy = "sourceEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnSequence> outgoingSequences;

    public BpmnEvent() {}

    public BpmnEvent(String name, String eventId, BusinessProcess process, EventType eventType) {
        this.name = name;
        this.eventId = eventId;
        this.process = process;
        this.eventType = eventType;
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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public BusinessProcess getProcess() {
        return process;
    }

    public void setProcess(BusinessProcess process) {
        this.process = process;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInterrupting() {
        return isInterrupting;
    }

    public void setInterrupting(boolean interrupting) {
        isInterrupting = interrupting;
    }

    public boolean isParallelMultiple() {
        return isParallelMultiple;
    }

    public void setParallelMultiple(boolean parallelMultiple) {
        isParallelMultiple = parallelMultiple;
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

    public String getTimerValue() {
        return timerValue;
    }

    public void setTimerValue(String timerValue) {
        this.timerValue = timerValue;
    }

    public String getTimerFormat() {
        return timerFormat;
    }

    public void setTimerFormat(String timerFormat) {
        this.timerFormat = timerFormat;
    }

    public String getMessageRef() {
        return messageRef;
    }

    public void setMessageRef(String messageRef) {
        this.messageRef = messageRef;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getErrorRef() {
        return errorRef;
    }

    public void setErrorRef(String errorRef) {
        this.errorRef = errorRef;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getEscalationCode() {
        return escalationCode;
    }

    public void setEscalationCode(String escalationCode) {
        this.escalationCode = escalationCode;
    }

    public String getEscalationName() {
        return escalationName;
    }

    public void setEscalationName(String escalationName) {
        this.escalationName = escalationName;
    }

    public String getSignalRef() {
        return signalRef;
    }

    public void setSignalRef(String signalRef) {
        this.signalRef = signalRef;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getActivityRef() {
        return activityRef;
    }

    public void setActivityRef(String activityRef) {
        this.activityRef = activityRef;
    }

    public boolean isWaitForCompletion() {
        return waitForCompletion;
    }

    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    public List<BpmnSequence> getIncomingSequences() {
        return incomingSequences;
    }

    public void setIncomingSequences(List<BpmnSequence> incomingSequences) {
        this.incomingSequences = incomingSequences;
    }

    public List<BpmnSequence> getOutgoingSequences() {
        return outgoingSequences;
    }

    public void setOutgoingSequences(List<BpmnSequence> outgoingSequences) {
        this.outgoingSequences = outgoingSequences;
    }

    /**
     * Event type enumeration
     */
    public enum EventType {
        START_EVENT,
        END_EVENT,
        INTERMEDIATE_THROW_EVENT,
        INTERMEDIATE_CATCH_EVENT,
        BOUNDARY_EVENT,
        TIMER_EVENT,
        MESSAGE_EVENT,
        ERROR_EVENT,
        ESCALATION_EVENT,
        SIGNAL_EVENT,
        COMPENSATION_EVENT
    }
}