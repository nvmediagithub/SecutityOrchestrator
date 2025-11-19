// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'alert.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$AlertImpl _$$AlertImplFromJson(Map<String, dynamic> json) => _$AlertImpl(
  id: json['id'] as String,
  title: json['title'] as String,
  description: json['description'] as String,
  severity: $enumDecode(
    _$AlertSeverityEnumMap,
    json['severity'],
    unknownValue: AlertSeverity.medium,
  ),
  status: $enumDecode(
    _$AlertStatusEnumMap,
    json['status'],
    unknownValue: AlertStatus.active,
  ),
  source: json['source'] as String,
  createdAt: DateTime.parse(json['createdAt'] as String),
  resolvedAt: json['resolvedAt'] == null
      ? null
      : DateTime.parse(json['resolvedAt'] as String),
  tags: json['tags'] as String? ?? '',
);

Map<String, dynamic> _$$AlertImplToJson(_$AlertImpl instance) =>
    <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'description': instance.description,
      'severity': _$AlertSeverityEnumMap[instance.severity]!,
      'status': _$AlertStatusEnumMap[instance.status]!,
      'source': instance.source,
      'createdAt': instance.createdAt.toIso8601String(),
      'resolvedAt': instance.resolvedAt?.toIso8601String(),
      'tags': instance.tags,
    };

const _$AlertSeverityEnumMap = {
  AlertSeverity.low: 'LOW',
  AlertSeverity.medium: 'MEDIUM',
  AlertSeverity.high: 'HIGH',
  AlertSeverity.critical: 'CRITICAL',
};

const _$AlertStatusEnumMap = {
  AlertStatus.active: 'ACTIVE',
  AlertStatus.resolved: 'RESOLVED',
};
