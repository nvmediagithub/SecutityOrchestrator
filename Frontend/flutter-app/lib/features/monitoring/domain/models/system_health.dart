import 'package:freezed_annotation/freezed_annotation.dart';

part 'system_health.freezed.dart';
part 'system_health.g.dart';

@JsonEnum(fieldRename: FieldRename.screamingSnake)
enum HealthStatus { healthy, degraded, unhealthy }

@freezed
class SystemHealth with _$SystemHealth {
  const factory SystemHealth({
    required String id,
    @JsonKey(unknownEnumValue: HealthStatus.degraded)
    required HealthStatus status,
    required double cpuUsage,
    required double memoryUsage,
    required double diskUsage,
    required int activeConnections,
    required DateTime timestamp,
    required String details,
  }) = _SystemHealth;

  factory SystemHealth.fromJson(Map<String, dynamic> json) =>
      _$SystemHealthFromJson(json);
}
