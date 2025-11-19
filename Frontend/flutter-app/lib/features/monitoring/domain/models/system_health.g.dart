// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'system_health.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$SystemHealthImpl _$$SystemHealthImplFromJson(Map<String, dynamic> json) =>
    _$SystemHealthImpl(
      id: json['id'] as String,
      status: $enumDecode(
        _$HealthStatusEnumMap,
        json['status'],
        unknownValue: HealthStatus.degraded,
      ),
      cpuUsage: (json['cpuUsage'] as num).toDouble(),
      memoryUsage: (json['memoryUsage'] as num).toDouble(),
      diskUsage: (json['diskUsage'] as num).toDouble(),
      activeConnections: (json['activeConnections'] as num).toInt(),
      timestamp: DateTime.parse(json['timestamp'] as String),
      details: json['details'] as String,
    );

Map<String, dynamic> _$$SystemHealthImplToJson(_$SystemHealthImpl instance) =>
    <String, dynamic>{
      'id': instance.id,
      'status': _$HealthStatusEnumMap[instance.status]!,
      'cpuUsage': instance.cpuUsage,
      'memoryUsage': instance.memoryUsage,
      'diskUsage': instance.diskUsage,
      'activeConnections': instance.activeConnections,
      'timestamp': instance.timestamp.toIso8601String(),
      'details': instance.details,
    };

const _$HealthStatusEnumMap = {
  HealthStatus.healthy: 'HEALTHY',
  HealthStatus.degraded: 'DEGRADED',
  HealthStatus.unhealthy: 'UNHEALTHY',
};
