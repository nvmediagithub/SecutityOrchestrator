// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'workflow_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

WorkflowDto _$WorkflowDtoFromJson(Map<String, dynamic> json) => WorkflowDto(
  id: json['id'] as String,
  name: json['name'] as String,
  description: json['description'] as String?,
  status: $enumDecode(_$WorkflowStatusEnumMap, json['status']),
  createdAt: DateTime.parse(json['createdAt'] as String),
  updatedAt: DateTime.parse(json['updatedAt'] as String),
  processId: json['processId'] as String,
  testCaseIds: (json['testCaseIds'] as List<dynamic>)
      .map((e) => e as String)
      .toList(),
  executionConfig: ExecutionConfigurationDto.fromJson(
    json['executionConfig'] as Map<String, dynamic>,
  ),
);

Map<String, dynamic> _$WorkflowDtoToJson(WorkflowDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'description': instance.description,
      'status': _$WorkflowStatusEnumMap[instance.status]!,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
      'processId': instance.processId,
      'testCaseIds': instance.testCaseIds,
      'executionConfig': instance.executionConfig,
    };

const _$WorkflowStatusEnumMap = {
  WorkflowStatus.draft: 'DRAFT',
  WorkflowStatus.active: 'ACTIVE',
  WorkflowStatus.running: 'RUNNING',
  WorkflowStatus.completed: 'COMPLETED',
  WorkflowStatus.failed: 'FAILED',
};

ExecutionConfigurationDto _$ExecutionConfigurationDtoFromJson(
  Map<String, dynamic> json,
) => ExecutionConfigurationDto(
  parallelExecution: json['parallelExecution'] as bool,
  maxRetries: (json['maxRetries'] as num).toInt(),
  timeoutSeconds: (json['timeoutSeconds'] as num).toInt(),
  variables: json['variables'] as Map<String, dynamic>,
);

Map<String, dynamic> _$ExecutionConfigurationDtoToJson(
  ExecutionConfigurationDto instance,
) => <String, dynamic>{
  'parallelExecution': instance.parallelExecution,
  'maxRetries': instance.maxRetries,
  'timeoutSeconds': instance.timeoutSeconds,
  'variables': instance.variables,
};

WorkflowSummaryDto _$WorkflowSummaryDtoFromJson(Map<String, dynamic> json) =>
    WorkflowSummaryDto(
      id: json['id'] as String,
      name: json['name'] as String,
      status: $enumDecode(_$WorkflowStatusEnumMap, json['status']),
      createdAt: DateTime.parse(json['createdAt'] as String),
      processName: json['processName'] as String,
      testCaseCount: (json['testCaseCount'] as num).toInt(),
      lastExecutionResult: json['lastExecutionResult'] as String?,
    );

Map<String, dynamic> _$WorkflowSummaryDtoToJson(WorkflowSummaryDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'status': _$WorkflowStatusEnumMap[instance.status]!,
      'createdAt': instance.createdAt.toIso8601String(),
      'processName': instance.processName,
      'testCaseCount': instance.testCaseCount,
      'lastExecutionResult': instance.lastExecutionResult,
    };

ExecutionStatusDto _$ExecutionStatusDtoFromJson(Map<String, dynamic> json) =>
    ExecutionStatusDto(
      workflowId: json['workflowId'] as String,
      executionId: json['executionId'] as String,
      state: $enumDecode(_$ExecutionStateEnumMap, json['state']),
      progress: (json['progress'] as num).toDouble(),
      steps: (json['steps'] as List<dynamic>)
          .map((e) => StepExecutionDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      startedAt: DateTime.parse(json['startedAt'] as String),
      completedAt: json['completedAt'] == null
          ? null
          : DateTime.parse(json['completedAt'] as String),
      error: json['error'] as String?,
    );

Map<String, dynamic> _$ExecutionStatusDtoToJson(ExecutionStatusDto instance) =>
    <String, dynamic>{
      'workflowId': instance.workflowId,
      'executionId': instance.executionId,
      'state': _$ExecutionStateEnumMap[instance.state]!,
      'progress': instance.progress,
      'steps': instance.steps,
      'startedAt': instance.startedAt.toIso8601String(),
      'completedAt': instance.completedAt?.toIso8601String(),
      'error': instance.error,
    };

const _$ExecutionStateEnumMap = {
  ExecutionState.pending: 'PENDING',
  ExecutionState.running: 'RUNNING',
  ExecutionState.completed: 'COMPLETED',
  ExecutionState.failed: 'FAILED',
  ExecutionState.cancelled: 'CANCELLED',
};

StepExecutionDto _$StepExecutionDtoFromJson(Map<String, dynamic> json) =>
    StepExecutionDto(
      stepId: json['stepId'] as String,
      stepName: json['stepName'] as String,
      state: $enumDecode(_$ExecutionStateEnumMap, json['state']),
      startedAt: DateTime.parse(json['startedAt'] as String),
      completedAt: json['completedAt'] == null
          ? null
          : DateTime.parse(json['completedAt'] as String),
      result: json['result'] as String?,
      error: json['error'] as String?,
    );

Map<String, dynamic> _$StepExecutionDtoToJson(StepExecutionDto instance) =>
    <String, dynamic>{
      'stepId': instance.stepId,
      'stepName': instance.stepName,
      'state': _$ExecutionStateEnumMap[instance.state]!,
      'startedAt': instance.startedAt.toIso8601String(),
      'completedAt': instance.completedAt?.toIso8601String(),
      'result': instance.result,
      'error': instance.error,
    };
