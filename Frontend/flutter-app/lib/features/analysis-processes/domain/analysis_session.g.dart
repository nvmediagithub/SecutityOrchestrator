// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'analysis_session.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$AnalysisSessionImpl _$$AnalysisSessionImplFromJson(
  Map<String, dynamic> json,
) => _$AnalysisSessionImpl(
  id: json['id'] as String,
  processId: json['processId'] as String,
  status: $enumDecode(_$AnalysisSessionStatusEnumMap, json['status']),
  currentStepId: json['currentStepId'] as String?,
  steps:
      (json['steps'] as List<dynamic>?)
          ?.map((e) => AnalysisStep.fromJson(e as Map<String, dynamic>))
          .toList() ??
      const <AnalysisStep>[],
  context:
      json['context'] as Map<String, dynamic>? ?? const <String, dynamic>{},
  createdAt: DateTime.parse(json['createdAt'] as String),
  updatedAt: DateTime.parse(json['updatedAt'] as String),
);

Map<String, dynamic> _$$AnalysisSessionImplToJson(
  _$AnalysisSessionImpl instance,
) => <String, dynamic>{
  'id': instance.id,
  'processId': instance.processId,
  'status': _$AnalysisSessionStatusEnumMap[instance.status]!,
  'currentStepId': instance.currentStepId,
  'steps': instance.steps,
  'context': instance.context,
  'createdAt': instance.createdAt.toIso8601String(),
  'updatedAt': instance.updatedAt.toIso8601String(),
};

const _$AnalysisSessionStatusEnumMap = {
  AnalysisSessionStatus.waitingForInput: 'waiting_for_input',
  AnalysisSessionStatus.running: 'running',
  AnalysisSessionStatus.waitingForTest: 'waiting_for_test',
  AnalysisSessionStatus.completed: 'completed',
  AnalysisSessionStatus.failed: 'failed',
};

_$AnalysisStepImpl _$$AnalysisStepImplFromJson(Map<String, dynamic> json) =>
    _$AnalysisStepImpl(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String?,
      type: $enumDecode(_$AnalysisStepTypeEnumMap, json['type']),
      status: $enumDecode(_$AnalysisStepStatusEnumMap, json['status']),
      metadata:
          json['metadata'] as Map<String, dynamic>? ??
          const <String, dynamic>{},
    );

Map<String, dynamic> _$$AnalysisStepImplToJson(_$AnalysisStepImpl instance) =>
    <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'description': instance.description,
      'type': _$AnalysisStepTypeEnumMap[instance.type]!,
      'status': _$AnalysisStepStatusEnumMap[instance.status]!,
      'metadata': instance.metadata,
    };

const _$AnalysisStepTypeEnumMap = {
  AnalysisStepType.collectInputs: 'collect_inputs',
  AnalysisStepType.llmAnalysis: 'llm_analysis',
  AnalysisStepType.testExecution: 'test_execution',
};

const _$AnalysisStepStatusEnumMap = {
  AnalysisStepStatus.pending: 'pending',
  AnalysisStepStatus.waiting: 'waiting',
  AnalysisStepStatus.running: 'running',
  AnalysisStepStatus.completed: 'completed',
  AnalysisStepStatus.failed: 'failed',
};
