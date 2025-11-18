enum HealthStatus {
  healthy,
  degraded,
  unhealthy,
}

class SystemHealth {
  final String id;
  final HealthStatus status;
  final double cpuUsage;
  final double memoryUsage;
  final double diskUsage;
  final int activeConnections;
  final DateTime timestamp;
  final String details;

  SystemHealth({
    required this.id,
    required this.status,
    required this.cpuUsage,
    required this.memoryUsage,
    required this.diskUsage,
    required this.activeConnections,
    required this.timestamp,
    required this.details,
  });

  factory SystemHealth.fromJson(Map<String, dynamic> json) {
    return SystemHealth(
      id: json['id'] as String,
      status: HealthStatus.values.firstWhere(
        (e) => e.name == (json['status'] as String).toLowerCase(),
      ),
      cpuUsage: (json['cpuUsage'] as num).toDouble(),
      memoryUsage: (json['memoryUsage'] as num).toDouble(),
      diskUsage: (json['diskUsage'] as num).toDouble(),
      activeConnections: json['activeConnections'] as int,
      timestamp: DateTime.parse(json['timestamp'] as String),
      details: json['details'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'status': status.name.toUpperCase(),
      'cpuUsage': cpuUsage,
      'memoryUsage': memoryUsage,
      'diskUsage': diskUsage,
      'activeConnections': activeConnections,
      'timestamp': timestamp.toIso8601String(),
      'details': details,
    };
  }

  bool get isHealthy => status == HealthStatus.healthy;
  bool get isDegraded => status == HealthStatus.degraded;
  bool get isUnhealthy => status == HealthStatus.unhealthy;
}