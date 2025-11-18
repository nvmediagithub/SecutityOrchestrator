import 'package:flutter/material.dart';

enum AlertSeverity {
  low,
  medium,
  high,
  critical,
}

enum AlertStatus {
  active,
  resolved,
}

class Alert {
  final String id;
  final String title;
  final String description;
  final AlertSeverity severity;
  final AlertStatus status;
  final String source;
  final DateTime createdAt;
  final DateTime? resolvedAt;
  final String tags;

  Alert({
    required this.id,
    required this.title,
    required this.description,
    required this.severity,
    required this.status,
    required this.source,
    required this.createdAt,
    this.resolvedAt,
    required this.tags,
  });

  factory Alert.fromJson(Map<String, dynamic> json) {
    return Alert(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String,
      severity: AlertSeverity.values.firstWhere(
        (e) => e.name == (json['severity'] as String).toLowerCase(),
      ),
      status: AlertStatus.values.firstWhere(
        (e) => e.name == (json['status'] as String).toLowerCase(),
      ),
      source: json['source'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
      resolvedAt: json['resolvedAt'] != null
          ? DateTime.parse(json['resolvedAt'] as String)
          : null,
      tags: json['tags'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'severity': severity.name.toUpperCase(),
      'status': status.name.toUpperCase(),
      'source': source,
      'createdAt': createdAt.toIso8601String(),
      'resolvedAt': resolvedAt?.toIso8601String(),
      'tags': tags,
    };
  }

  bool get isActive => status == AlertStatus.active;
  bool get isResolved => status == AlertStatus.resolved;

  bool get isAcknowledged => status == AlertStatus.resolved;

  String get message => description;

  DateTime get timestamp => createdAt;

  Color get color => switch (severity) {
        AlertSeverity.low => Colors.yellow,
        AlertSeverity.medium => Colors.orange,
        AlertSeverity.high => Colors.red,
        AlertSeverity.critical => Colors.red.shade900,
      };

  IconData get icon => switch (severity) {
        AlertSeverity.low => Icons.warning,
        AlertSeverity.medium => Icons.warning_amber,
        AlertSeverity.high => Icons.error,
        AlertSeverity.critical => Icons.error_outline,
      };
}