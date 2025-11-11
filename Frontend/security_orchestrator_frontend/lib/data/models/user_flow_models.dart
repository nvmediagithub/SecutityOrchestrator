import 'package:equatable/equatable.dart';
import 'package:json_annotation/json_annotation.dart';

part 'user_flow_models.g.dart';

@JsonSerializable()
class UserTask extends Equatable {
  final String id;
  final String title;
  final String description;
  final List<UploadedFile> attachments;
  final DateTime createdAt;
  final TaskStatus status;

  const UserTask({
    required this.id,
    required this.title,
    required this.description,
    required this.attachments,
    required this.createdAt,
    required this.status,
  });

  factory UserTask.fromJson(Map<String, dynamic> json) =>
      _$UserTaskFromJson(json);

  Map<String, dynamic> toJson() => _$UserTaskToJson(this);

  UserTask copyWith({
    String? id,
    String? title,
    String? description,
    List<UploadedFile>? attachments,
    DateTime? createdAt,
    TaskStatus? status,
  }) {
    return UserTask(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      attachments: attachments ?? this.attachments,
      createdAt: createdAt ?? this.createdAt,
      status: status ?? this.status,
    );
  }

  @override
  List<Object?> get props => [id, title, description, attachments, createdAt, status];
}

@JsonSerializable()
class UploadedFile extends Equatable {
  final String id;
  final String name;
  final String path;
  final String type; // docx, txt, pdf
  final int size;
  final DateTime uploadedAt;

  const UploadedFile({
    required this.id,
    required this.name,
    required this.path,
    required this.type,
    required this.size,
    required this.uploadedAt,
  });

  factory UploadedFile.fromJson(Map<String, dynamic> json) =>
      _$UploadedFileFromJson(json);

  Map<String, dynamic> toJson() => _$UploadedFileToJson(this);

  UploadedFile copyWith({
    String? id,
    String? name,
    String? path,
    String? type,
    int? size,
    DateTime? uploadedAt,
  }) {
    return UploadedFile(
      id: id ?? this.id,
      name: name ?? this.name,
      path: path ?? this.path,
      type: type ?? this.type,
      size: size ?? this.size,
      uploadedAt: uploadedAt ?? this.uploadedAt,
    );
  }

  @override
  List<Object?> get props => [id, name, path, type, size, uploadedAt];
}

enum TaskStatus {
  @JsonValue('pending')
  pending,
  @JsonValue('analyzing')
  analyzing,
  @JsonValue('completed')
  completed,
  @JsonValue('error')
  error,
}

@JsonSerializable()
class AnalysisRequest extends Equatable {
  final String title;
  final String description;
  final List<String> fileIds;

  const AnalysisRequest({
    required this.title,
    required this.description,
    required this.fileIds,
  });

  factory AnalysisRequest.fromJson(Map<String, dynamic> json) =>
      _$AnalysisRequestFromJson(json);

  Map<String, dynamic> toJson() => _$AnalysisRequestToJson(this);

  @override
  List<Object?> get props => [title, description, fileIds];
}

@JsonSerializable()
class AnalysisResponse extends Equatable {
  final String analysisId;
  final String status;
  final String message;

  const AnalysisResponse({
    required this.analysisId,
    required this.status,
    required this.message,
  });

  factory AnalysisResponse.fromJson(Map<String, dynamic> json) =>
      _$AnalysisResponseFromJson(json);

  Map<String, dynamic> toJson() => _$AnalysisResponseToJson(this);

  @override
  List<Object?> get props => [analysisId, status, message];
}

@JsonSerializable()
class AnalysisResult extends Equatable {
  final String id;
  final String taskId;
  final String title;
  final String summary;
  final String reportContent;
  final DateTime completedAt;
  final Map<String, dynamic> metadata;

  const AnalysisResult({
    required this.id,
    required this.taskId,
    required this.title,
    required this.summary,
    required this.reportContent,
    required this.completedAt,
    required this.metadata,
  });

  factory AnalysisResult.fromJson(Map<String, dynamic> json) =>
      _$AnalysisResultFromJson(json);

  Map<String, dynamic> toJson() => _$AnalysisResultToJson(this);

  @override
  List<Object?> get props => [id, taskId, title, summary, reportContent, completedAt, metadata];
}

@JsonSerializable()
class WebSocketMessage extends Equatable {
  final String type;
  final String taskId;
  final String message;
  final Map<String, dynamic>? data;
  final DateTime timestamp;

  const WebSocketMessage({
    required this.type,
    required this.taskId,
    required this.message,
    required this.timestamp,
    this.data,
  });

  factory WebSocketMessage.fromJson(Map<String, dynamic> json) =>
      _$WebSocketMessageFromJson(json);

  Map<String, dynamic> toJson() => _$WebSocketMessageToJson(this);

  @override
  List<Object?> get props => [type, taskId, message, data, timestamp];
}

@JsonSerializable()
class ExportOptions extends Equatable {
  final bool includeDetails;
  final bool includeMetadata;
  final String format; // pdf, docx, html

  const ExportOptions({
    required this.includeDetails,
    required this.includeMetadata,
    required this.format,
  });

  factory ExportOptions.fromJson(Map<String, dynamic> json) =>
      _$ExportOptionsFromJson(json);

  Map<String, dynamic> toJson() => _$ExportOptionsToJson(this);

  @override
  List<Object?> get props => [includeDetails, includeMetadata, format];
}