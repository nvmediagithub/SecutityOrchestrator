// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'openapi_validation_issue.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$OpenApiValidationIssueImpl _$$OpenApiValidationIssueImplFromJson(
  Map<String, dynamic> json,
) => _$OpenApiValidationIssueImpl(
  path: json['path'] as String?,
  message: json['message'] as String,
  severity: json['severity'] as String?,
  errorType: json['errorType'] as String?,
  lineNumber: (json['lineNumber'] as num?)?.toInt(),
  columnNumber: (json['columnNumber'] as num?)?.toInt(),
);

Map<String, dynamic> _$$OpenApiValidationIssueImplToJson(
  _$OpenApiValidationIssueImpl instance,
) => <String, dynamic>{
  'path': instance.path,
  'message': instance.message,
  'severity': instance.severity,
  'errorType': instance.errorType,
  'lineNumber': instance.lineNumber,
  'columnNumber': instance.columnNumber,
};
