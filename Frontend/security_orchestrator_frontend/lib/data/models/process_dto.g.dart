// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'process_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ProcessDto _$ProcessDtoFromJson(Map<String, dynamic> json) => ProcessDto(
  id: json['id'] as String,
  name: json['name'] as String,
  description: json['description'] as String?,
  status: $enumDecode(_$ProcessStatusEnumMap, json['status']),
  createdAt: DateTime.parse(json['createdAt'] as String),
  updatedAt: DateTime.parse(json['updatedAt'] as String),
  bpmnXml: json['bpmnXml'] as String,
  elements: (json['elements'] as List<dynamic>)
      .map((e) => FlowElementDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$ProcessDtoToJson(ProcessDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'description': instance.description,
      'status': _$ProcessStatusEnumMap[instance.status]!,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
      'bpmnXml': instance.bpmnXml,
      'elements': instance.elements,
    };

const _$ProcessStatusEnumMap = {
  ProcessStatus.active: 'ACTIVE',
  ProcessStatus.inactive: 'INACTIVE',
  ProcessStatus.archived: 'ARCHIVED',
};

FlowElementDto _$FlowElementDtoFromJson(Map<String, dynamic> json) =>
    FlowElementDto(
      id: json['id'] as String,
      name: json['name'] as String,
      type: $enumDecode(_$ElementTypeEnumMap, json['type']),
      properties: json['properties'] as Map<String, dynamic>,
    );

Map<String, dynamic> _$FlowElementDtoToJson(FlowElementDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'type': _$ElementTypeEnumMap[instance.type]!,
      'properties': instance.properties,
    };

const _$ElementTypeEnumMap = {
  ElementType.startEvent: 'START_EVENT',
  ElementType.endEvent: 'END_EVENT',
  ElementType.task: 'TASK',
  ElementType.gateway: 'GATEWAY',
  ElementType.sequenceFlow: 'SEQUENCE_FLOW',
};

ProcessSummaryDto _$ProcessSummaryDtoFromJson(Map<String, dynamic> json) =>
    ProcessSummaryDto(
      id: json['id'] as String,
      name: json['name'] as String,
      status: $enumDecode(_$ProcessStatusEnumMap, json['status']),
      createdAt: DateTime.parse(json['createdAt'] as String),
      elementCount: (json['elementCount'] as num).toInt(),
    );

Map<String, dynamic> _$ProcessSummaryDtoToJson(ProcessSummaryDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'status': _$ProcessStatusEnumMap[instance.status]!,
      'createdAt': instance.createdAt.toIso8601String(),
      'elementCount': instance.elementCount,
    };
