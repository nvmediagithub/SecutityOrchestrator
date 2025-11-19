// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'openapi_validation_summary.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$OpenApiValidationSummaryImpl _$$OpenApiValidationSummaryImplFromJson(
  Map<String, dynamic> json,
) => _$OpenApiValidationSummaryImpl(
  totalChecks: (json['totalChecks'] as num).toInt(),
  passedChecks: (json['passedChecks'] as num).toInt(),
  failedChecks: (json['failedChecks'] as num).toInt(),
  warningChecks: (json['warningChecks'] as num).toInt(),
  validationTimeMs: (json['validationTimeMs'] as num).toInt(),
  successRate: (json['successRate'] as num?)?.toDouble() ?? 0.0,
);

Map<String, dynamic> _$$OpenApiValidationSummaryImplToJson(
  _$OpenApiValidationSummaryImpl instance,
) => <String, dynamic>{
  'totalChecks': instance.totalChecks,
  'passedChecks': instance.passedChecks,
  'failedChecks': instance.failedChecks,
  'warningChecks': instance.warningChecks,
  'validationTimeMs': instance.validationTimeMs,
  'successRate': instance.successRate,
};
