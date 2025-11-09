package org.example.domain.entities.bpmn;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.domain.entities.AbstractEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a BPMN business process
 */
@Entity
@Table(name = "business_processes")
public class BusinessProcess {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(length = 1000)
    private String description;

    @NotBlank
    @Size(max = 255)
    @Column(name = "process_id", nullable = false, unique = true)
    private String processId;

    @NotBlank
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "version")
    private String version;

    @Column(name = "target_namespace")
    private String targetNamespace;

    @Column(name = "is_executable")
    private boolean executable;

    @Column(name = "last_parsed_at")
    private LocalDateTime lastParsedAt;

    @Column(name = "parse_status")
    @Enumerated(EnumType.STRING)
    private ParseStatus parseStatus = ParseStatus.PENDING;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnTask> tasks;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnSequence> sequences;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnGateway> gateways;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BpmnEvent> events;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProcessAnalysis> analyses;

    public BusinessProcess() {}

    public BusinessProcess(String name, String description, String processId, String filePath) {
        this.name = name;
        this.description = description;
        this.processId = processId;
        this.filePath = filePath;
        this.parseStatus = ParseStatus.PENDING;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public LocalDateTime getLastParsedAt() {
        return lastParsedAt;
    }

    public void setLastParsedAt(LocalDateTime lastParsedAt) {
        this.lastParsedAt = lastParsedAt;
    }

    public ParseStatus getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(ParseStatus parseStatus) {
        this.parseStatus = parseStatus;
    }

    public List<BpmnTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<BpmnTask> tasks) {
        this.tasks = tasks;
    }

    public List<BpmnSequence> getSequences() {
        return sequences;
    }

    public void setSequences(List<BpmnSequence> sequences) {
        this.sequences = sequences;
    }

    public List<BpmnGateway> getGateways() {
        return gateways;
    }

    public void setGateways(List<BpmnGateway> gateways) {
        this.gateways = gateways;
    }

    public List<BpmnEvent> getEvents() {
        return events;
    }

    public void setEvents(List<BpmnEvent> events) {
        this.events = events;
    }

    public List<ProcessAnalysis> getAnalyses() {
        return analyses;
    }

    public void setAnalyses(List<ProcessAnalysis> analyses) {
        this.analyses = analyses;
    }

    /**
     * Parse status enumeration
     */
    public enum ParseStatus {
        PENDING,
        PARSING,
        SUCCESS,
        FAILED,
        WARNING
    }
}