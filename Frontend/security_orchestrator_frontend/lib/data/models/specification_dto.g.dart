// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'specification_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SpecificationDto _$SpecificationDtoFromJson(Map<String, dynamic> json) =>
    SpecificationDto(
      id: json['id'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      version: json['version'] as String,
      openApiVersion: $enumDecode(
        _$OpenApiVersionEnumMap,
        json['openApiVersion'],
      ),
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      specData: json['specData'] as Map<String, dynamic>,
      endpoints: (json['endpoints'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
    );

Map<String, dynamic> _$SpecificationDtoToJson(SpecificationDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'description': instance.description,
      'version': instance.version,
      'openApiVersion': _$OpenApiVersionEnumMap[instance.openApiVersion]!,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
      'specData': instance.specData,
      'endpoints': instance.endpoints,
    };

const _$OpenApiVersionEnumMap = {
  OpenApiVersion.v3_0_0: '3.0.0',
  OpenApiVersion.v3_0_1: '3.0.1',
  OpenApiVersion.v3_0_2: '3.0.2',
  OpenApiVersion.v3_0_3: '3.0.3',
  OpenApiVersion.v3_1_0: '3.1.0',
  OpenApiVersion.v3_1_1: '3.1.1',
};

SpecificationSummaryDto _$SpecificationSummaryDtoFromJson(
  Map<String, dynamic> json,
) => SpecificationSummaryDto(
  id: json['id'] as String,
  name: json['name'] as String,
  version: json['version'] as String,
  createdAt: DateTime.parse(json['createdAt'] as String),
  endpointCount: (json['endpointCount'] as num).toInt(),
);

Map<String, dynamic> _$SpecificationSummaryDtoToJson(
  SpecificationSummaryDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'version': instance.version,
  'createdAt': instance.createdAt.toIso8601String(),
  'endpointCount': instance.endpointCount,
};
