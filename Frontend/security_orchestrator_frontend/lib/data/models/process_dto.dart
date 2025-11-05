import 'package:json_annotation/json_annotation.dart';

part 'process_dto.g.dart';

@JsonSerializable()
class ProcessDto {
  final String id;
  final String name;
  final String? description;
  final ProcessStatus status;
  final DateTime createdAt;
  final DateTime updatedAt;
  final String bpmnXml;
  final List<FlowElementDto> elements;

  ProcessDto({
    required this.id,
    required this.name,
    this.description,
    required this.status,
    required this.createdAt,
    required this.updatedAt,
    required this.bpmnXml,
    required this.elements,
  });

  factory ProcessDto.fromJson(Map<String, dynamic> json) =>
      _$ProcessDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ProcessDtoToJson(this);
}

enum ProcessStatus {
  @JsonValue('ACTIVE')
  active,
  @JsonValue('INACTIVE')
  inactive,
  @JsonValue('ARCHIVED')
  archived,
}

@JsonSerializable()
class FlowElementDto {
  final String id;
  final String name;
  final ElementType type;
  final Map<String, dynamic> properties;

  FlowElementDto({
    required this.id,
    required this.name,
    required this.type,
    required this.properties,
  });

  factory FlowElementDto.fromJson(Map<String, dynamic> json) =>
      _$FlowElementDtoFromJson(json);

  Map<String, dynamic> toJson() => _$FlowElementDtoToJson(this);
}

enum ElementType {
  @JsonValue('START_EVENT')
  startEvent,
  @JsonValue('END_EVENT')
  endEvent,
  @JsonValue('TASK')
  task,
  @JsonValue('GATEWAY')
  gateway,
  @JsonValue('SEQUENCE_FLOW')
  sequenceFlow,
}

@JsonSerializable()
class ProcessSummaryDto {
  final String id;
  final String name;
  final ProcessStatus status;
  final DateTime createdAt;
  final int elementCount;

  ProcessSummaryDto({
    required this.id,
    required this.name,
    required this.status,
    required this.createdAt,
    required this.elementCount,
  });

  factory ProcessSummaryDto.fromJson(Map<String, dynamic> json) =>
      _$ProcessSummaryDtoFromJson(json);

  Map<String, dynamic> toJson() => _$ProcessSummaryDtoToJson(this);
}