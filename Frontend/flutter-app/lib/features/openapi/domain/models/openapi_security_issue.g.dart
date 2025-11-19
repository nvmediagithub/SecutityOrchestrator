// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'openapi_security_issue.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$OpenApiSecurityIssueImpl _$$OpenApiSecurityIssueImplFromJson(
  Map<String, dynamic> json,
) => _$OpenApiSecurityIssueImpl(
  type: json['type'] as String,
  severity: json['severity'] as String,
  description: json['description'] as String,
  location: json['location'] as String?,
);

Map<String, dynamic> _$$OpenApiSecurityIssueImplToJson(
  _$OpenApiSecurityIssueImpl instance,
) => <String, dynamic>{
  'type': instance.type,
  'severity': instance.severity,
  'description': instance.description,
  'location': instance.location,
};
