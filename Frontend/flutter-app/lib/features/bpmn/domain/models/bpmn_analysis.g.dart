// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bpmn_analysis.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$BpmnAnalysisImpl _$$BpmnAnalysisImplFromJson(Map<String, dynamic> json) =>
    _$BpmnAnalysisImpl(
      diagramName: json['diagramName'] as String,
      processId: json['processId'] as String?,
      overallRisk: json['overallRisk'] as String,
      totalIssues: (json['totalIssues'] as num).toInt(),
      structuralIssues:
          (json['structuralIssues'] as List<dynamic>?)
              ?.map((e) => BpmnIssue.fromJson(e as Map<String, dynamic>))
              .toList() ??
          const <BpmnIssue>[],
      securityIssues:
          (json['securityIssues'] as List<dynamic>?)
              ?.map((e) => BpmnIssue.fromJson(e as Map<String, dynamic>))
              .toList() ??
          const <BpmnIssue>[],
      performanceIssues:
          (json['performanceIssues'] as List<dynamic>?)
              ?.map((e) => BpmnIssue.fromJson(e as Map<String, dynamic>))
              .toList() ??
          const <BpmnIssue>[],
      bpmnContent: json['bpmnContent'] as String,
      analyzedAt: json['analyzedAt'] == null
          ? null
          : DateTime.parse(json['analyzedAt'] as String),
    );

Map<String, dynamic> _$$BpmnAnalysisImplToJson(_$BpmnAnalysisImpl instance) =>
    <String, dynamic>{
      'diagramName': instance.diagramName,
      'processId': instance.processId,
      'overallRisk': instance.overallRisk,
      'totalIssues': instance.totalIssues,
      'structuralIssues': instance.structuralIssues,
      'securityIssues': instance.securityIssues,
      'performanceIssues': instance.performanceIssues,
      'bpmnContent': instance.bpmnContent,
      'analyzedAt': instance.analyzedAt?.toIso8601String(),
    };
