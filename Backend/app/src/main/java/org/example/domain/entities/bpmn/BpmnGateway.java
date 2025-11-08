package org.example.domain.entities.bpmn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.entities.AbstractEntity;

import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a BPMN Gateway element
 */
@Entity
@Table(name = "bpmn_gateways")
public class BpmnGateway {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(name = "gateway_id", nullable = false)
    private String gatewayId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private BusinessProcess process;

    @Column(name = "gateway_type")
    @Enumerated(EnumType.STRING)
    private GatewayType gatewayType = GatewayType.EXCLUSIVE_GATEWAY;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @Column(name = "is_marker_visible")
    private boolean isMarkerVisible = true;

    @Column(name = "is_open")
    private boolean isOpen = true; // true for parallel gateways, false for exclusive

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

    // Incoming and outgoing sequence flows
    @OneToMany(mappedBy = "targetGateway", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnSequence> incomingSequences;

    @OneToMany(mappedBy = "sourceGateway", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnSequence> outgoingSequences;

    // Gateway configuration for exclusive and inclusive gateways
    @Column(name = "default_flow")
    private String defaultFlow;

    @Column(name = "activation_condition")
    private String activationCondition;

    // For parallel gateways
    @Column(name = "is_merge")
    private boolean isMerge = false;

    // For event-based gateways
    @Column(name = "is_event_based")
    private boolean isEventBased = false;

    @Column(name = "instantiate")
    private boolean instantiate = false;

    public BpmnGateway() {}

    public BpmnGateway(String name, String gatewayId, BusinessProcess process, GatewayType gatewayType) {
        this.name = name;
        this.gatewayId = gatewayId;
        this.process = process;
        this.gatewayType = gatewayType;
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

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public BusinessProcess getProcess() {
        return process;
    }

    public void setProcess(BusinessProcess process) {
        this.process = process;
    }

    public GatewayType getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(GatewayType gatewayType) {
        this.gatewayType = gatewayType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMarkerVisible() {
        return isMarkerVisible;
    }

    public void setMarkerVisible(boolean markerVisible) {
        isMarkerVisible = markerVisible;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
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

    public String getDefaultFlow() {
        return defaultFlow;
    }

    public void setDefaultFlow(String defaultFlow) {
        this.defaultFlow = defaultFlow;
    }

    public String getActivationCondition() {
        return activationCondition;
    }

    public void setActivationCondition(String activationCondition) {
        this.activationCondition = activationCondition;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }

    public boolean isEventBased() {
        return isEventBased;
    }

    public void setEventBased(boolean eventBased) {
        isEventBased = eventBased;
    }

    public boolean isInstantiate() {
        return instantiate;
    }

    public void setInstantiate(boolean instantiate) {
        this.instantiate = instantiate;
    }

    /**
     * Gateway type enumeration
     */
    public enum GatewayType {
        EXCLUSIVE_GATEWAY,
        PARALLEL_GATEWAY,
        INCLUSIVE_GATEWAY,
        EVENT_BASED_GATEWAY
    }
}