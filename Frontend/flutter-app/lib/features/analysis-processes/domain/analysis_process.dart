enum ProcessStatus {
  pending,
  running,
  completed,
  failed,
}

enum ProcessType {
  securityAnalysis,
  performanceTest,
  integrationTest,
  custom,
}

extension ProcessStatusExtension on ProcessStatus {
  String get displayName {
    switch (this) {
      case ProcessStatus.pending:
        return 'Pending';
      case ProcessStatus.running:
        return 'Running';
      case ProcessStatus.completed:
        return 'Completed';
      case ProcessStatus.failed:
        return 'Failed';
    }
  }

  bool get isActive => this == ProcessStatus.running;
  bool get isFinished => this == ProcessStatus.completed || this == ProcessStatus.failed;
}

extension ProcessTypeExtension on ProcessType {
  String get displayName {
    switch (this) {
      case ProcessType.securityAnalysis:
        return 'Security Analysis';
      case ProcessType.performanceTest:
        return 'Performance Test';
      case ProcessType.integrationTest:
        return 'Integration Test';
      case ProcessType.custom:
        return 'Custom';
    }
  }
}

class AnalysisProcess {
  final String? id;
  final String name;
  final String description;
  final DateTime createdAt;
  final ProcessStatus status;
  final ProcessType type;

  const AnalysisProcess({
    this.id,
    required this.name,
    required this.description,
    required this.createdAt,
    required this.status,
    required this.type,
  });

  AnalysisProcess copyWith({
    String? id,
    String? name,
    String? description,
    DateTime? createdAt,
    ProcessStatus? status,
    ProcessType? type,
  }) {
    return AnalysisProcess(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      createdAt: createdAt ?? this.createdAt,
      status: status ?? this.status,
      type: type ?? this.type,
    );
  }

  factory AnalysisProcess.create({
    required String name,
    required String description,
    required ProcessType type,
  }) {
    return AnalysisProcess(
      id: null,
      name: name,
      description: description,
      createdAt: DateTime.now(),
      status: ProcessStatus.pending,
      type: type,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'createdAt': createdAt.toIso8601String(),
      'status': status.name,
      'type': type.name,
    };
  }

  factory AnalysisProcess.fromJson(Map<String, dynamic> json) {
    return AnalysisProcess(
      id: json['id'] as String?,
      name: json['name'] as String,
      description: json['description'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
      status: ProcessStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => ProcessStatus.pending,
      ),
      type: ProcessType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => ProcessType.custom,
      ),
    );
  }

  @override
  String toString() {
    return 'AnalysisProcess(id: $id, name: $name, status: $status, type: $type)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is AnalysisProcess &&
        other.id == id &&
        other.name == name &&
        other.description == description &&
        other.createdAt == createdAt &&
        other.status == status &&
        other.type == type;
  }

  @override
  int get hashCode {
    return id.hashCode ^
        name.hashCode ^
        description.hashCode ^
        createdAt.hashCode ^
        status.hashCode ^
        type.hashCode;
  }
}