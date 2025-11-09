import 'package:json_annotation/json_annotation.dart';

part 'execution_monitoring_dto.g.dart';

@JsonSerializable()
class ExecutionSessionDto {
  final String id;
  final String name;
  final String status; // PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
  final DateTime startedAt;
  final DateTime? completedAt;
  final String openApiUrl;
  final List<String> bpmnFiles;
  final List<String> owaspCategories;
  final ExecutionProgressDto progress;
  final List<StepExecutionDto> steps;
  final List<String> logs;
  final Map<String, dynamic> configuration;

  ExecutionSessionDto({
    required this.id,
    required this.name,
    required this.status,
    required this.startedAt,
    this.completedAt,
    required this.openApiUrl,
    required this.bpmnFiles,
    required this.owaspCategories,
    required this.progress,
    required this.steps,
    required this.logs,
    required this.configuration,
  });

  factory ExecutionSessionDto.fromJson(Map<String, dynamic> json) =>
      _$ExecutionSessionDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ExecutionSessionDtoToJson(this);
}

@JsonSerializable()
class ExecutionProgressDto {
  final int totalSteps;
  final int completedSteps;
  final int failedSteps;
  final double percentage;
  final String currentStep;
  final String estimatedTimeRemaining;
  final DateTime? startedAt;
  final DateTime? completedAt;

  ExecutionProgressDto({
    required this.totalSteps,
    required this.completedSteps,
    required this.failedSteps,
    required this.percentage,
    required this.currentStep,
    required this.estimatedTimeRemaining,
    this.startedAt,
    this.completedAt,
  });

  factory ExecutionProgressDto.fromJson(Map<String, dynamic> json) =>
      _$ExecutionProgressDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ExecutionProgressDtoToJson(this);
}

enum ExecutionStatus {
  @JsonValue('PENDING')
  pending,
  @JsonValue('RUNNING')
  running,
  @JsonValue('COMPLETED')
  completed,
  @JsonValue('FAILED')
  failed,
  @JsonValue('CANCELLED')
  cancelled,
}

@JsonSerializable()
class StepExecutionDto {
  final String id;
  final String name;
  final String type; // OPENAPI_ANALYSIS, BPMN_ANALYSIS, OWASP_TEST, VULNERABILITY_SCAN
  final String status; // PENDING, RUNNING, COMPLETED, FAILED
  final DateTime startedAt;
  final DateTime? completedAt;
  final Map<String, dynamic> result;
  final List<String> errors;
  final int executionTimeMs;

  StepExecutionDto({
    required this.id,
    required this.name,
    required this.type,
    required this.status,
    required this.startedAt,
    this.completedAt,
    required this.result,
    required this.errors,
    required this.executionTimeMs,
  });

  factory StepExecutionDto.fromJson(Map<String, dynamic> json) =>
      _$StepExecutionDtoFromJson(json);

  Map<String, dynamic> toJson() => _$StepExecutionDtoToJson(this);
}

enum StepType {
  @JsonValue('OPENAPI_ANALYSIS')
  openApiAnalysis,
  @JsonValue('BPMN_ANALYSIS')
  bpmnAnalysis,
  @JsonValue('OWASP_TEST')
  owaspTest,
  @JsonValue('VULNERABILITY_SCAN')
  vulnerabilityScan,
  @JsonValue('LLM_ANALYSIS')
  llmAnalysis,
}

enum StepStatus {
  @JsonValue('PENDING')
  pending,
  @JsonValue('RUNNING')
  running,
  @JsonValue('COMPLETED')
  completed,
  @JsonValue('FAILED')
  failed,
  @JsonValue('SKIPPED')
  skipped,
}