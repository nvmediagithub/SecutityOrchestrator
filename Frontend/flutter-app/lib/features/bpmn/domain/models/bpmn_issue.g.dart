// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bpmn_issue.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$BpmnIssueImpl _$$BpmnIssueImplFromJson(Map<String, dynamic> json) =>
    _$BpmnIssueImpl(
      type: json['type'] as String,
      severity: json['severity'] as String,
      description: json['description'] as String,
      elementId: json['elementId'] as String?,
      suggestion: json['suggestion'] as String?,
    );

Map<String, dynamic> _$$BpmnIssueImplToJson(_$BpmnIssueImpl instance) =>
    <String, dynamic>{
      'type': instance.type,
      'severity': instance.severity,
      'description': instance.description,
      'elementId': instance.elementId,
      'suggestion': instance.suggestion,
    };
