import 'package:freezed_annotation/freezed_annotation.dart';

part 'metric.freezed.dart';
part 'metric.g.dart';

@JsonEnum(fieldRename: FieldRename.screamingSnake)
enum MetricType {
  cpuUsage,
  memoryUsage,
  diskUsage,
  networkUsage,
  databaseConnections,
  externalServiceLatency,
  applicationHealth,
  systemLoad,
  @JsonValue('NETWORK_IO')
  networkIo,
  @JsonValue('RESPONSE_TIME')
  responseTime,
  unknown,
}

extension MetricTypeDisplay on MetricType {
  String get displayName {
    switch (this) {
      case MetricType.cpuUsage:
        return 'CPU Usage';
      case MetricType.memoryUsage:
        return 'Memory Usage';
      case MetricType.diskUsage:
        return 'Disk Usage';
      case MetricType.networkUsage:
      case MetricType.networkIo:
        return 'Network Throughput';
      case MetricType.databaseConnections:
        return 'Database Connections';
      case MetricType.externalServiceLatency:
        return 'External Service Latency';
      case MetricType.applicationHealth:
        return 'Application Health';
      case MetricType.systemLoad:
        return 'System Load';
      case MetricType.responseTime:
        return 'Response Time';
      case MetricType.unknown:
        return 'Metric';
    }
  }
}

@freezed
class Metric with _$Metric {
  const factory Metric({
    required String id,
    required String name,
    @JsonKey(unknownEnumValue: MetricType.unknown) required MetricType type,
    required double value,
    required String unit,
    required DateTime timestamp,
    @Default('') String description,
    @Default('') String tags,
  }) = _Metric;

  factory Metric.fromJson(Map<String, dynamic> json) => _$MetricFromJson(json);
}
