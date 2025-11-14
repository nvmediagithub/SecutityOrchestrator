import 'package:equatable/equatable.dart';

class AnalysisResponse extends Equatable {
  final String analysisId;
  final String status;
  final DateTime createdAt;
  final DateTime? estimatedCompletion;

  const AnalysisResponse({
    required this.analysisId,
    required this.status,
    required this.createdAt,
    this.estimatedCompletion,
  });

  factory AnalysisResponse.fromJson(Map<String, dynamic> json) {
    return AnalysisResponse(
      analysisId: json['analysisId'] as String,
      status: json['status'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
      estimatedCompletion: json['estimatedCompletion'] != null
          ? DateTime.parse(json['estimatedCompletion'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'analysisId': analysisId,
      'status': status,
      'createdAt': createdAt.toIso8601String(),
      'estimatedCompletion': estimatedCompletion?.toIso8601String(),
    };
  }

  @override
  List<Object?> get props => [analysisId, status, createdAt, estimatedCompletion];
}

class AnalysisStatus extends Equatable {
  final String analysisId;
  final String status;
  final double progress;
  final String? currentStep;
  final DateTime lastUpdated;
  final String? error;

  const AnalysisStatus({
    required this.analysisId,
    required this.status,
    required this.progress,
    this.currentStep,
    required this.lastUpdated,
    this.error,
  });

  factory AnalysisStatus.fromJson(Map<String, dynamic> json) {
    return AnalysisStatus(
      analysisId: json['analysisId'] as String,
      status: json['status'] as String,
      progress: (json['progress'] as num).toDouble(),
      currentStep: json['currentStep'] as String?,
      lastUpdated: DateTime.parse(json['lastUpdated'] as String),
      error: json['error'] as String?,
    );
  }

  @override
  List<Object?> get props => [
    analysisId,
    status,
    progress,
    currentStep,
    lastUpdated,
    error,
  ];
}

class AnalysisResult extends Equatable {
  final String analysisId;
  final int securityScore;
  final List<Vulnerability> vulnerabilities;
  final List<Recommendation> recommendations;
  final Map<String, dynamic> metadata;
  final int analysisTime;
  final DateTime completedAt;

  const AnalysisResult({
    required this.analysisId,
    required this.securityScore,
    required this.vulnerabilities,
    required this.recommendations,
    required this.metadata,
    required this.analysisTime,
    required this.completedAt,
  });

  factory AnalysisResult.fromJson(Map<String, dynamic> json) {
    return AnalysisResult(
      analysisId: json['analysisId'] as String,
      securityScore: json['securityScore'] as int,
      vulnerabilities: (json['vulnerabilities'] as List)
          .map((v) => Vulnerability.fromJson(v))
          .toList(),
      recommendations: (json['recommendations'] as List)
          .map((r) => Recommendation.fromJson(r))
          .toList(),
      metadata: json['metadata'] as Map<String, dynamic>,
      analysisTime: json['analysisTime'] as int,
      completedAt: DateTime.parse(json['completedAt'] as String),
    );
  }

  @override
  List<Object?> get props => [
    analysisId,
    securityScore,
    vulnerabilities,
    recommendations,
    metadata,
    analysisTime,
    completedAt,
  ];
}

class Vulnerability extends Equatable {
  final String id;
  final String title;
  final String description;
  final String severity;
  final String category;
  final Map<String, dynamic>? details;

  const Vulnerability({
    required this.id,
    required this.title,
    required this.description,
    required this.severity,
    required this.category,
    this.details,
  });

  factory Vulnerability.fromJson(Map<String, dynamic> json) {
    return Vulnerability(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String,
      severity: json['severity'] as String,
      category: json['category'] as String,
      details: json['details'] as Map<String, dynamic>?,
    );
  }

  @override
  List<Object?> get props => [id, title, description, severity, category, details];
}

class Recommendation extends Equatable {
  final String id;
  final String title;
  final String description;
  final String priority;
  final String category;

  const Recommendation({
    required this.id,
    required this.title,
    required this.description,
    required this.priority,
    required this.category,
  });

  factory Recommendation.fromJson(Map<String, dynamic> json) {
    return Recommendation(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String,
      priority: json['priority'] as String,
      category: json['category'] as String,
    );
  }

  @override
  List<Object?> get props => [id, title, description, priority, category];
}

class AnalysisHistory extends Equatable {
  final String analysisId;
  final String documentName;
  final String documentType;
  final int securityScore;
  final String status;
  final DateTime createdAt;
  final DateTime? completedAt;

  const AnalysisHistory({
    required this.analysisId,
    required this.documentName,
    required this.documentType,
    required this.securityScore,
    required this.status,
    required this.createdAt,
    this.completedAt,
  });

  factory AnalysisHistory.fromJson(Map<String, dynamic> json) {
    return AnalysisHistory(
      analysisId: json['analysisId'] as String,
      documentName: json['documentName'] as String,
      documentType: json['documentType'] as String,
      securityScore: json['securityScore'] as int,
      status: json['status'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
      completedAt: json['completedAt'] != null
          ? DateTime.parse(json['completedAt'] as String)
          : null,
    );
  }

  @override
  List<Object?> get props => [
    analysisId,
    documentName,
    documentType,
    securityScore,
    status,
    createdAt,
    completedAt,
  ];
}