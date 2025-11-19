// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'openapi_analysis.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$OpenApiAnalysisImpl _$$OpenApiAnalysisImplFromJson(
  Map<String, dynamic> json,
) => _$OpenApiAnalysisImpl(
  specificationName: json['specificationName'] as String,
  version: json['version'] as String?,
  valid: json['valid'] as bool,
  metadata:
      json['metadata'] as Map<String, dynamic>? ?? const <String, dynamic>{},
  validationSummary: json['validationSummary'] == null
      ? null
      : OpenApiValidationSummary.fromJson(
          json['validationSummary'] as Map<String, dynamic>,
        ),
  validationErrors:
      (json['validationErrors'] as List<dynamic>?)
          ?.map(
            (e) => OpenApiValidationIssue.fromJson(e as Map<String, dynamic>),
          )
          .toList() ??
      const <OpenApiValidationIssue>[],
  endpointCount: (json['endpointCount'] as num).toInt(),
  schemaCount: (json['schemaCount'] as num).toInt(),
  operationsByMethod:
      (json['operationsByMethod'] as Map<String, dynamic>?)?.map(
        (k, e) => MapEntry(k, (e as num).toInt()),
      ) ??
      const <String, int>{},
  endpoints:
      json['endpoints'] as Map<String, dynamic>? ?? const <String, dynamic>{},
  schemas:
      json['schemas'] as Map<String, dynamic>? ?? const <String, dynamic>{},
  securityIssues:
      (json['securityIssues'] as List<dynamic>?)
          ?.map((e) => OpenApiSecurityIssue.fromJson(e as Map<String, dynamic>))
          .toList() ??
      const <OpenApiSecurityIssue>[],
  recommendations:
      (json['recommendations'] as List<dynamic>?)
          ?.map((e) => e as String)
          .toList() ??
      const <String>[],
  openapiContent: json['openapiContent'] as String,
  analyzedAt: json['analyzedAt'] == null
      ? null
      : DateTime.parse(json['analyzedAt'] as String),
);

Map<String, dynamic> _$$OpenApiAnalysisImplToJson(
  _$OpenApiAnalysisImpl instance,
) => <String, dynamic>{
  'specificationName': instance.specificationName,
  'version': instance.version,
  'valid': instance.valid,
  'metadata': instance.metadata,
  'validationSummary': instance.validationSummary,
  'validationErrors': instance.validationErrors,
  'endpointCount': instance.endpointCount,
  'schemaCount': instance.schemaCount,
  'operationsByMethod': instance.operationsByMethod,
  'endpoints': instance.endpoints,
  'schemas': instance.schemas,
  'securityIssues': instance.securityIssues,
  'recommendations': instance.recommendations,
  'openapiContent': instance.openapiContent,
  'analyzedAt': instance.analyzedAt?.toIso8601String(),
};
