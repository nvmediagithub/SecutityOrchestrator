enum MetricType {
  cpuUsage,
  memoryUsage,
  diskUsage,
  networkIo,
  responseTime,
}

class Metric {
  final String id;
  final String name;
  final MetricType type;
  final double value;
  final String unit;
  final DateTime timestamp;
  final String description;

  Metric({
    required this.id,
    required this.name,
    required this.type,
    required this.value,
    required this.unit,
    required this.timestamp,
    required this.description,
  });

  factory Metric.fromJson(Map<String, dynamic> json) {
    return Metric(
      id: json['id'] as String,
      name: json['name'] as String,
      type: MetricType.values.firstWhere(
        (e) => e.name == (json['type'] as String).toLowerCase().replaceAll('_', ''),
      ),
      value: (json['value'] as num).toDouble(),
      unit: json['unit'] as String,
      timestamp: DateTime.parse(json['timestamp'] as String),
      description: json['description'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'type': type.name.toUpperCase().replaceAllMapped(
            RegExp(r'([a-z])([A-Z])'),
            (match) => '${match.group(1)}_${match.group(2)}',
          ),
      'value': value,
      'unit': unit,
      'timestamp': timestamp.toIso8601String(),
      'description': description,
    };
  }
}