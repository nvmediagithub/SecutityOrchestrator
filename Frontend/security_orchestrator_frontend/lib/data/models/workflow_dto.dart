import 'package:json_annotation/json_annotation.dart';

part 'workflow_dto.g.dart';

@JsonSerializable()
class WorkflowDto {
  final String id;
  final String name;
  final String? description;
  final WorkflowStatus status;
  final DateTime createdAt;
  final DateTime updatedAt;
  final String processId;
  final List<String> testCaseIds;
  final ExecutionConfigurationDto executionConfig;

  WorkflowDto({
    required this.id,
    required this.name,
    this.description,
    required this.status,
    required this.createdAt,
    required this.updatedAt,
    required this.processId,
    required this.testCaseIds,
    required this.executionConfig,
  });

  factory WorkflowDto.fromJson(Map<String, dynamic> json) =>
      _$WorkflowDtoFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowDtoToJson(this);
}

enum WorkflowStatus {
  @JsonValue('DRAFT')
  draft,
  @JsonValue('ACTIVE')
  active,
  @JsonValue('RUNNING')
  running,
  @JsonValue('COMPLETED')
  completed,
  @JsonValue('FAILED')
  failed,
}

@JsonSerializable()
class ExecutionConfigurationDto {
  final bool parallelExecution;
  final int maxRetries;
  final int timeoutSeconds;
  final Map<String, dynamic> variables;

  ExecutionConfigurationDto({
    required this.parallelExecution,
    required this.maxRetries,
    required this.timeoutSeconds,
    required this.variables,
  });

  factory ExecutionConfigurationDto.fromJson(Map<String, dynamic> json) =>
      _$ExecutionConfigurationDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ExecutionConfigurationDtoToJson(this);
}

@JsonSerializable()
class WorkflowSummaryDto {
  final String id;
  final String name;
  final WorkflowStatus status;
  final DateTime createdAt;
  final String processName;
  final int testCaseCount;
  final String? lastExecutionResult;

  WorkflowSummaryDto({
    required this.id,
    required this.name,
    required this.status,
    required this.createdAt,
    required this.processName,
    required this.testCaseCount,
    this.lastExecutionResult,
  });

  factory WorkflowSummaryDto.fromJson(Map<String, dynamic> json) =>
      _$WorkflowSummaryDtoFromJson(json);

  Map<String, dynamic> toJson() => _$WorkflowSummaryDtoToJson(this);
}

@JsonSerializable()
class ExecutionStatusDto {
  final String workflowId;
  final String executionId;
  final ExecutionState state;
  final double progress;
  final List<StepExecutionDto> steps;
  final DateTime startedAt;
  final DateTime? completedAt;
  final String? error;

  ExecutionStatusDto({
    required this.workflowId,
    required this.executionId,
    required this.state,
    required this.progress,
    required this.steps,
    required this.startedAt,
    this.completedAt,
    this.error,
  });

  factory ExecutionStatusDto.fromJson(Map<String, dynamic> json) =>
      _$ExecutionStatusDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ExecutionStatusDtoToJson(this);
}

enum ExecutionState {
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
  final String stepId;
  final String stepName;
  final ExecutionState state;
  final DateTime startedAt;
  final DateTime? completedAt;
  final String? result;
  final String? error;

  StepExecutionDto({
    required this.stepId,
    required this.stepName,
    required this.state,
    required this.startedAt,
    this.completedAt,
    this.result,
    this.error,
  });

  factory StepExecutionDto.fromJson(Map<String, dynamic> json) =>
      _$StepExecutionDtoFromJson(json);

  Map<String, dynamic> toJson() => _$StepExecutionDtoToJson(this);
}