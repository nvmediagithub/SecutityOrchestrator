// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'monitoring_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SystemHealth _$SystemHealthFromJson(Map<String, dynamic> json) => SystemHealth(
  cpuUsage: (json['cpuUsage'] as num).toDouble(),
  memoryUsage: (json['memoryUsage'] as num).toDouble(),
  diskUsage: (json['diskUsage'] as num).toDouble(),
  databaseConnectionStatus: json['databaseConnectionStatus'] as String,
  averageResponseTime: (json['averageResponseTime'] as num).toDouble(),
  activeModelCount: (json['activeModelCount'] as num).toInt(),
  timestamp: DateTime.parse(json['timestamp'] as String),
);

Map<String, dynamic> _$SystemHealthToJson(SystemHealth instance) =>
    <String, dynamic>{
      'cpuUsage': instance.cpuUsage,
      'memoryUsage': instance.memoryUsage,
      'diskUsage': instance.diskUsage,
      'databaseConnectionStatus': instance.databaseConnectionStatus,
      'averageResponseTime': instance.averageResponseTime,
      'activeModelCount': instance.activeModelCount,
      'timestamp': instance.timestamp.toIso8601String(),
    };

Alert _$AlertFromJson(Map<String, dynamic> json) => Alert(
  id: json['id'] as String,
  type: json['type'] as String,
  message: json['message'] as String,
  severity: $enumDecode(_$AlertSeverityEnumMap, json['severity']),
  timestamp: DateTime.parse(json['timestamp'] as String),
);

Map<String, dynamic> _$AlertToJson(Alert instance) => <String, dynamic>{
  'id': instance.id,
  'type': instance.type,
  'message': instance.message,
  'severity': _$AlertSeverityEnumMap[instance.severity]!,
  'timestamp': instance.timestamp.toIso8601String(),
};

const _$AlertSeverityEnumMap = {
  AlertSeverity.info: 'info',
  AlertSeverity.warning: 'warning',
  AlertSeverity.error: 'error',
  AlertSeverity.critical: 'critical',
};

ModelInfo _$ModelInfoFromJson(Map<String, dynamic> json) => ModelInfo(
  modelId: json['modelId'] as String,
  modelName: json['modelName'] as String,
  provider: json['provider'] as String,
  status: json['status'] as String,
  sizeGB: (json['sizeGB'] as num?)?.toDouble(),
);

Map<String, dynamic> _$ModelInfoToJson(ModelInfo instance) => <String, dynamic>{
  'modelId': instance.modelId,
  'modelName': instance.modelName,
  'provider': instance.provider,
  'status': instance.status,
  'sizeGB': instance.sizeGB,
};

SystemResourceUsage _$SystemResourceUsageFromJson(Map<String, dynamic> json) =>
    SystemResourceUsage(
      cpuUsage: (json['cpuUsage'] as num).toDouble(),
      memoryUsage: (json['memoryUsage'] as num).toDouble(),
      diskUsage: (json['diskUsage'] as num).toDouble(),
      activeConnections: (json['activeConnections'] as num).toInt(),
      threadCount: (json['threadCount'] as num).toInt(),
    );

Map<String, dynamic> _$SystemResourceUsageToJson(
  SystemResourceUsage instance,
) => <String, dynamic>{
  'cpuUsage': instance.cpuUsage,
  'memoryUsage': instance.memoryUsage,
  'diskUsage': instance.diskUsage,
  'activeConnections': instance.activeConnections,
  'threadCount': instance.threadCount,
};

MetricPoint _$MetricPointFromJson(Map<String, dynamic> json) => MetricPoint(
  type: json['type'] as String,
  value: (json['value'] as num).toDouble(),
  timestamp: DateTime.parse(json['timestamp'] as String),
);

Map<String, dynamic> _$MetricPointToJson(MetricPoint instance) =>
    <String, dynamic>{
      'type': instance.type,
      'value': instance.value,
      'timestamp': instance.timestamp.toIso8601String(),
    };

MetricsReport _$MetricsReportFromJson(Map<String, dynamic> json) =>
    MetricsReport(
      systemHealth: SystemHealth.fromJson(
        json['systemHealth'] as Map<String, dynamic>,
      ),
      llmMetrics: json['llmMetrics'] as Map<String, dynamic>,
      apiSuccessRate: (json['apiSuccessRate'] as num).toDouble(),
      totalApiRequests: (json['totalApiRequests'] as num).toInt(),
      activeUsers: (json['activeUsers'] as num).toInt(),
      uptime: Duration(microseconds: (json['uptime'] as num).toInt()),
      systemResourceUsage: SystemResourceUsage.fromJson(
        json['systemResourceUsage'] as Map<String, dynamic>,
      ),
      activeModels: (json['activeModels'] as List<dynamic>)
          .map((e) => ModelInfo.fromJson(e as Map<String, dynamic>))
          .toList(),
      recentAlerts: (json['recentAlerts'] as List<dynamic>)
          .map((e) => Alert.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$MetricsReportToJson(MetricsReport instance) =>
    <String, dynamic>{
      'systemHealth': instance.systemHealth,
      'llmMetrics': instance.llmMetrics,
      'apiSuccessRate': instance.apiSuccessRate,
      'totalApiRequests': instance.totalApiRequests,
      'activeUsers': instance.activeUsers,
      'uptime': instance.uptime.inMicroseconds,
      'systemResourceUsage': instance.systemResourceUsage,
      'activeModels': instance.activeModels,
      'recentAlerts': instance.recentAlerts,
    };
