// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'metric.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$MetricImpl _$$MetricImplFromJson(Map<String, dynamic> json) => _$MetricImpl(
  id: json['id'] as String,
  name: json['name'] as String,
  type: $enumDecode(
    _$MetricTypeEnumMap,
    json['type'],
    unknownValue: MetricType.unknown,
  ),
  value: (json['value'] as num).toDouble(),
  unit: json['unit'] as String,
  timestamp: DateTime.parse(json['timestamp'] as String),
  description: json['description'] as String? ?? '',
  tags: json['tags'] as String? ?? '',
);

Map<String, dynamic> _$$MetricImplToJson(_$MetricImpl instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'type': _$MetricTypeEnumMap[instance.type]!,
      'value': instance.value,
      'unit': instance.unit,
      'timestamp': instance.timestamp.toIso8601String(),
      'description': instance.description,
      'tags': instance.tags,
    };

const _$MetricTypeEnumMap = {
  MetricType.cpuUsage: 'CPU_USAGE',
  MetricType.memoryUsage: 'MEMORY_USAGE',
  MetricType.diskUsage: 'DISK_USAGE',
  MetricType.networkUsage: 'NETWORK_USAGE',
  MetricType.databaseConnections: 'DATABASE_CONNECTIONS',
  MetricType.externalServiceLatency: 'EXTERNAL_SERVICE_LATENCY',
  MetricType.applicationHealth: 'APPLICATION_HEALTH',
  MetricType.systemLoad: 'SYSTEM_LOAD',
  MetricType.networkIo: 'NETWORK_IO',
  MetricType.responseTime: 'RESPONSE_TIME',
  MetricType.unknown: 'UNKNOWN',
};
