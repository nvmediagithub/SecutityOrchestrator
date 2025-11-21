enum ProcessStatus { pending, running, completed, failed }

enum ProcessType { securityAnalysis, performanceTest, integrationTest, custom }

extension ProcessStatusExtension on ProcessStatus {
  String get displayName {
    switch (this) {
      case ProcessStatus.pending:
        return 'Ожидает';
      case ProcessStatus.running:
        return 'Выполняется';
      case ProcessStatus.completed:
        return 'Завершён';
      case ProcessStatus.failed:
        return 'Сбой';
    }
  }

  bool get isActive => this == ProcessStatus.running;
  bool get isFinished =>
      this == ProcessStatus.completed || this == ProcessStatus.failed;
}

extension ProcessTypeExtension on ProcessType {
  String get displayName {
    switch (this) {
      case ProcessType.securityAnalysis:
        return 'Анализ безопасности';
      case ProcessType.performanceTest:
        return 'Тест производительности';
      case ProcessType.integrationTest:
        return 'Интеграционный тест';
      case ProcessType.custom:
        return 'Пользовательский';
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
  final String? bpmnDiagramName;
  final DateTime? bpmnUploadedAt;
  final String? openapiSpecName;
  final DateTime? openapiUploadedAt;

  const AnalysisProcess({
    this.id,
    required this.name,
    required this.description,
    required this.createdAt,
    required this.status,
    required this.type,
    this.bpmnDiagramName,
    this.bpmnUploadedAt,
    this.openapiSpecName,
    this.openapiUploadedAt,
  });

  AnalysisProcess copyWith({
    String? id,
    String? name,
    String? description,
    DateTime? createdAt,
    ProcessStatus? status,
    ProcessType? type,
    String? bpmnDiagramName,
    DateTime? bpmnUploadedAt,
    String? openapiSpecName,
    DateTime? openapiUploadedAt,
  }) {
    return AnalysisProcess(
      id: id ?? this.id,
      name: name ?? this.name,
      description: description ?? this.description,
      createdAt: createdAt ?? this.createdAt,
      status: status ?? this.status,
      type: type ?? this.type,
      bpmnDiagramName: bpmnDiagramName ?? this.bpmnDiagramName,
      bpmnUploadedAt: bpmnUploadedAt ?? this.bpmnUploadedAt,
      openapiSpecName: openapiSpecName ?? this.openapiSpecName,
      openapiUploadedAt: openapiUploadedAt ?? this.openapiUploadedAt,
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
      'bpmnDiagramName': bpmnDiagramName,
      'bpmnUploadedAt': bpmnUploadedAt?.toIso8601String(),
      'openapiSpecName': openapiSpecName,
      'openapiUploadedAt': openapiUploadedAt?.toIso8601String(),
    };
  }

  factory AnalysisProcess.fromJson(Map<String, dynamic> json) {
    return AnalysisProcess(
      id: json['id'] as String?,
      name: json['name'] as String,
      description: (json['description'] ?? '') as String,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : DateTime.now(),
      status: ProcessStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => ProcessStatus.pending,
      ),
      type: ProcessType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => ProcessType.custom,
      ),
      bpmnDiagramName: json['bpmnDiagramName'] as String?,
      bpmnUploadedAt: json['bpmnUploadedAt'] != null
          ? DateTime.parse(json['bpmnUploadedAt'] as String)
          : null,
      openapiSpecName: json['openapiSpecName'] as String?,
      openapiUploadedAt: json['openapiUploadedAt'] != null
          ? DateTime.parse(json['openapiUploadedAt'] as String)
          : null,
    );
  }

  bool get hasBpmn => bpmnDiagramName != null && bpmnUploadedAt != null;

  bool get hasOpenApi => openapiSpecName != null && openapiUploadedAt != null;

  @override
  String toString() {
    return 'AnalysisProcess(id: $id, name: $name, status: $status, type: $type, '
        'bpmnDiagramName: $bpmnDiagramName, openapiSpecName: $openapiSpecName)';
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
        other.type == type &&
        other.bpmnDiagramName == bpmnDiagramName &&
        other.bpmnUploadedAt == bpmnUploadedAt &&
        other.openapiSpecName == openapiSpecName &&
        other.openapiUploadedAt == openapiUploadedAt;
  }

  @override
  int get hashCode {
    return id.hashCode ^
        name.hashCode ^
        description.hashCode ^
        createdAt.hashCode ^
        status.hashCode ^
        type.hashCode ^
        (bpmnDiagramName?.hashCode ?? 0) ^
        (bpmnUploadedAt?.hashCode ?? 0) ^
        (openapiSpecName?.hashCode ?? 0) ^
        (openapiUploadedAt?.hashCode ?? 0);
  }
}
