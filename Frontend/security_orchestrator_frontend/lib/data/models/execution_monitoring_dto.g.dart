// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'execution_monitoring_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ExecutionSessionDto _$ExecutionSessionDtoFromJson(Map<String, dynamic> json) =>
    ExecutionSessionDto(
      id: json['id'] as String,
      name: json['name'] as String,
      status: json['status'] as String,
      startedAt: DateTime.parse(json['startedAt'] as String),
      completedAt: json['completedAt'] == null
          ? null
          : DateTime.parse(json['completedAt'] as String),
      openApiUrl: json['openApiUrl'] as String,
      bpmnFiles: (json['bpmnFiles'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      owaspCategories: (json['owaspCategories'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      progress: ExecutionProgressDto.fromJson(
        json['progress'] as Map<String, dynamic>,
      ),
      steps: (json['steps'] as List<dynamic>)
          .map((e) => StepExecutionDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      logs: (json['logs'] as List<dynamic>).map((e) => e as String).toList(),
      configuration: json['configuration'] as Map<String, dynamic>,
    );

Map<String, dynamic> _$ExecutionSessionDtoToJson(
  ExecutionSessionDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'status': instance.status,
  'startedAt': instance.startedAt.toIso8601String(),
  'completedAt': instance.completedAt?.toIso8601String(),
  'openApiUrl': instance.openApiUrl,
  'bpmnFiles': instance.bpmnFiles,
  'owaspCategories': instance.owaspCategories,
  'progress': instance.progress,
  'steps': instance.steps,
  'logs': instance.logs,
  'configuration': instance.configuration,
};

ExecutionProgressDto _$ExecutionProgressDtoFromJson(
  Map<String, dynamic> json,
) => ExecutionProgressDto(
  totalSteps: (json['totalSteps'] as num).toInt(),
  completedSteps: (json['completedSteps'] as num).toInt(),
  failedSteps: (json['failedSteps'] as num).toInt(),
  percentage: (json['percentage'] as num).toDouble(),
  currentStep: json['currentStep'] as String,
  estimatedTimeRemaining: json['estimatedTimeRemaining'] as String,
  startedAt: json['startedAt'] == null
      ? null
      : DateTime.parse(json['startedAt'] as String),
  completedAt: json['completedAt'] == null
      ? null
      : DateTime.parse(json['completedAt'] as String),
);

Map<String, dynamic> _$ExecutionProgressDtoToJson(
  ExecutionProgressDto instance,
) => <String, dynamic>{
  'totalSteps': instance.totalSteps,
  'completedSteps': instance.completedSteps,
  'failedSteps': instance.failedSteps,
  'percentage': instance.percentage,
  'currentStep': instance.currentStep,
  'estimatedTimeRemaining': instance.estimatedTimeRemaining,
  'startedAt': instance.startedAt?.toIso8601String(),
  'completedAt': instance.completedAt?.toIso8601String(),
};

StepExecutionDto _$StepExecutionDtoFromJson(Map<String, dynamic> json) =>
    StepExecutionDto(
      id: json['id'] as String,
      name: json['name'] as String,
      type: json['type'] as String,
      status: json['status'] as String,
      startedAt: DateTime.parse(json['startedAt'] as String),
      completedAt: json['completedAt'] == null
          ? null
          : DateTime.parse(json['completedAt'] as String),
      result: json['result'] as Map<String, dynamic>,
      errors: (json['errors'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      executionTimeMs: (json['executionTimeMs'] as num).toInt(),
    );

Map<String, dynamic> _$StepExecutionDtoToJson(StepExecutionDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'type': instance.type,
      'status': instance.status,
      'startedAt': instance.startedAt.toIso8601String(),
      'completedAt': instance.completedAt?.toIso8601String(),
      'result': instance.result,
      'errors': instance.errors,
      'executionTimeMs': instance.executionTimeMs,
    };
