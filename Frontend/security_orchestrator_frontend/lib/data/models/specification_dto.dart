import 'package:json_annotation/json_annotation.dart';

part 'specification_dto.g.dart';

@JsonSerializable()
class SpecificationDto {
  final String id;
  final String name;
  final String? description;
  final String version;
  final OpenApiVersion openApiVersion;
  final DateTime createdAt;
  final DateTime updatedAt;
  final Map<String, dynamic> specData;
  final List<String> endpoints;

  SpecificationDto({
    required this.id,
    required this.name,
    this.description,
    required this.version,
    required this.openApiVersion,
    required this.createdAt,
    required this.updatedAt,
    required this.specData,
    required this.endpoints,
  });

  factory SpecificationDto.fromJson(Map<String, dynamic> json) =>
      _$SpecificationDtoFromJson(json);

  Map<String, dynamic> toJson() => _$SpecificationDtoToJson(this);
}

enum OpenApiVersion {
  @JsonValue('3.0.0')
  v3_0_0,
  @JsonValue('3.0.1')
  v3_0_1,
  @JsonValue('3.0.2')
  v3_0_2,
  @JsonValue('3.0.3')
  v3_0_3,
  @JsonValue('3.1.0')
  v3_1_0,
  @JsonValue('3.1.1')
  v3_1_1,
}

@JsonSerializable()
class SpecificationSummaryDto {
  final String id;
  final String name;
  final String version;
  final DateTime createdAt;
  final int endpointCount;

  SpecificationSummaryDto({
    required this.id,
    required this.name,
    required this.version,
    required this.createdAt,
    required this.endpointCount,
  });

  factory SpecificationSummaryDto.fromJson(Map<String, dynamic> json) =>
      _$SpecificationSummaryDtoFromJson(json);

  Map<String, dynamic> toJson() => _$SpecificationSummaryDtoToJson(this);
}