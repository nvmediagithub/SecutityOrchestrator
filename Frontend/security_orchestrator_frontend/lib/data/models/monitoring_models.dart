import 'package:json_annotation/json_annotation.dart';

part 'monitoring_models.g.dart';

/// System health information
@JsonSerializable()
class SystemHealth {
  final double cpuUsage;
  final double memoryUsage;
  final double diskUsage;
  final String databaseConnectionStatus;
  final double averageResponseTime;
  final int activeModelCount;
  final DateTime timestamp;

  const SystemHealth({
    required this.cpuUsage,
    required this.memoryUsage,
    required this.diskUsage,
    required this.databaseConnectionStatus,
    required this.averageResponseTime,
    required this.activeModelCount,
    required this.timestamp,
  });

  factory SystemHealth.fromJson(Map<String, dynamic> json) =>
      _$SystemHealthFromJson(json);

  Map<String, dynamic> toJson() => _$SystemHealthToJson(this);

  String get overallStatus {
    if (cpuUsage > 80 || memoryUsage > 80) {
      return 'CRITICAL';
    } else if (cpuUsage > 60 || memoryUsage > 60) {
      return 'WARNING';
    } else if (databaseConnectionStatus != 'CONNECTED') {
      return 'DEGRADED';
    } else {
      return 'HEALTHY';
    }
  }

  String get statusColor {
    switch (overallStatus) {
      case 'CRITICAL':
        return 'red';
      case 'WARNING':
        return 'orange';
      case 'DEGRADED':
        return 'yellow';
      default:
        return 'green';
    }
  }
}

/// Alert information
@JsonSerializable()
class Alert {
  final String id;
  final String type;
  final String message;
  final AlertSeverity severity;
  final DateTime timestamp;

  const Alert({
    required this.id,
    required this.type,
    required this.message,
    required this.severity,
    required this.timestamp,
  });

  factory Alert.fromJson(Map<String, dynamic> json) =>
      _$AlertFromJson(json);

  Map<String, dynamic> toJson() => _$AlertToJson(this);

  String get severityColor {
    switch (severity) {
      case AlertSeverity.critical:
        return 'red';
      case AlertSeverity.error:
        return 'red';
      case AlertSeverity.warning:
        return 'orange';
      case AlertSeverity.info:
        return 'blue';
    }
  }
}

enum AlertSeverity {
  @JsonValue('info')
  info,
  @JsonValue('warning')
  warning,
  @JsonValue('error')
  error,
  @JsonValue('critical')
  critical,
}

/// Model information
@JsonSerializable()
class ModelInfo {
  final String modelId;
  final String modelName;
  final String provider;
  final String status;
  final double? sizeGB;

  const ModelInfo({
    required this.modelId,
    required this.modelName,
    required this.provider,
    required this.status,
    this.sizeGB,
  });

  factory ModelInfo.fromJson(Map<String, dynamic> json) =>
      _$ModelInfoFromJson(json);

  Map<String, dynamic> toJson() => _$ModelInfoToJson(this);

  String get statusColor {
    switch (status.toLowerCase()) {
      case 'loaded':
        return 'green';
      case 'loading':
        return 'orange';
      case 'unloaded':
        return 'grey';
      case 'error':
        return 'red';
      default:
        return 'blue';
    }
  }
}

/// System resource usage
@JsonSerializable()
class SystemResourceUsage {
  final double cpuUsage;
  final double memoryUsage;
  final double diskUsage;
  final int activeConnections;
  final int threadCount;

  const SystemResourceUsage({
    required this.cpuUsage,
    required this.memoryUsage,
    required this.diskUsage,
    required this.activeConnections,
    required this.threadCount,
  });

  factory SystemResourceUsage.fromJson(Map<String, dynamic> json) =>
      _$SystemResourceUsageFromJson(json);

  Map<String, dynamic> toJson() => _$SystemResourceUsageToJson(this);
}

/// Metric point for time-series data
@JsonSerializable()
class MetricPoint {
  final String type;
  final double value;
  final DateTime timestamp;

  const MetricPoint({
    required this.type,
    required this.value,
    required this.timestamp,
  });

  factory MetricPoint.fromJson(Map<String, dynamic> json) =>
      _$MetricPointFromJson(json);

  Map<String, dynamic> toJson() => _$MetricPointToJson(this);
}

/// Comprehensive metrics report
@JsonSerializable()
class MetricsReport {
  final SystemHealth systemHealth;
  final Map<String, dynamic> llmMetrics;
  final double apiSuccessRate;
  final int totalApiRequests;
  final int activeUsers;
  final Duration uptime;
  final SystemResourceUsage systemResourceUsage;
  final List<ModelInfo> activeModels;
  final List<Alert> recentAlerts;

  const MetricsReport({
    required this.systemHealth,
    required this.llmMetrics,
    required this.apiSuccessRate,
    required this.totalApiRequests,
    required this.activeUsers,
    required this.uptime,
    required this.systemResourceUsage,
    required this.activeModels,
    required this.recentAlerts,
  });

  factory MetricsReport.fromJson(Map<String, dynamic> json) =>
      _$MetricsReportFromJson(json);

  Map<String, dynamic> toJson() => _$MetricsReportToJson(this);
}