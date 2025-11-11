// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_flow_models.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UserTask _$UserTaskFromJson(Map<String, dynamic> json) => UserTask(
  id: json['id'] as String,
  title: json['title'] as String,
  description: json['description'] as String,
  attachments: (json['attachments'] as List<dynamic>)
      .map((e) => UploadedFile.fromJson(e as Map<String, dynamic>))
      .toList(),
  createdAt: DateTime.parse(json['createdAt'] as String),
  status: $enumDecode(_$TaskStatusEnumMap, json['status']),
);

Map<String, dynamic> _$UserTaskToJson(UserTask instance) => <String, dynamic>{
  'id': instance.id,
  'title': instance.title,
  'description': instance.description,
  'attachments': instance.attachments,
  'createdAt': instance.createdAt.toIso8601String(),
  'status': _$TaskStatusEnumMap[instance.status]!,
};

const _$TaskStatusEnumMap = {
  TaskStatus.pending: 'pending',
  TaskStatus.analyzing: 'analyzing',
  TaskStatus.completed: 'completed',
  TaskStatus.error: 'error',
};

UploadedFile _$UploadedFileFromJson(Map<String, dynamic> json) => UploadedFile(
  id: json['id'] as String,
  name: json['name'] as String,
  path: json['path'] as String,
  type: json['type'] as String,
  size: (json['size'] as num).toInt(),
  uploadedAt: DateTime.parse(json['uploadedAt'] as String),
);

Map<String, dynamic> _$UploadedFileToJson(UploadedFile instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'path': instance.path,
      'type': instance.type,
      'size': instance.size,
      'uploadedAt': instance.uploadedAt.toIso8601String(),
    };

AnalysisRequest _$AnalysisRequestFromJson(Map<String, dynamic> json) =>
    AnalysisRequest(
      title: json['title'] as String,
      description: json['description'] as String,
      fileIds: (json['fileIds'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
    );

Map<String, dynamic> _$AnalysisRequestToJson(AnalysisRequest instance) =>
    <String, dynamic>{
      'title': instance.title,
      'description': instance.description,
      'fileIds': instance.fileIds,
    };

AnalysisResponse _$AnalysisResponseFromJson(Map<String, dynamic> json) =>
    AnalysisResponse(
      analysisId: json['analysisId'] as String,
      status: json['status'] as String,
      message: json['message'] as String,
    );

Map<String, dynamic> _$AnalysisResponseToJson(AnalysisResponse instance) =>
    <String, dynamic>{
      'analysisId': instance.analysisId,
      'status': instance.status,
      'message': instance.message,
    };

AnalysisResult _$AnalysisResultFromJson(Map<String, dynamic> json) =>
    AnalysisResult(
      id: json['id'] as String,
      taskId: json['taskId'] as String,
      title: json['title'] as String,
      summary: json['summary'] as String,
      reportContent: json['reportContent'] as String,
      completedAt: DateTime.parse(json['completedAt'] as String),
      metadata: json['metadata'] as Map<String, dynamic>,
    );

Map<String, dynamic> _$AnalysisResultToJson(AnalysisResult instance) =>
    <String, dynamic>{
      'id': instance.id,
      'taskId': instance.taskId,
      'title': instance.title,
      'summary': instance.summary,
      'reportContent': instance.reportContent,
      'completedAt': instance.completedAt.toIso8601String(),
      'metadata': instance.metadata,
    };

WebSocketMessage _$WebSocketMessageFromJson(Map<String, dynamic> json) =>
    WebSocketMessage(
      type: json['type'] as String,
      taskId: json['taskId'] as String,
      message: json['message'] as String,
      timestamp: DateTime.parse(json['timestamp'] as String),
      data: json['data'] as Map<String, dynamic>?,
    );

Map<String, dynamic> _$WebSocketMessageToJson(WebSocketMessage instance) =>
    <String, dynamic>{
      'type': instance.type,
      'taskId': instance.taskId,
      'message': instance.message,
      'data': instance.data,
      'timestamp': instance.timestamp.toIso8601String(),
    };

ExportOptions _$ExportOptionsFromJson(Map<String, dynamic> json) =>
    ExportOptions(
      includeDetails: json['includeDetails'] as bool,
      includeMetadata: json['includeMetadata'] as bool,
      format: json['format'] as String,
    );

Map<String, dynamic> _$ExportOptionsToJson(ExportOptions instance) =>
    <String, dynamic>{
      'includeDetails': instance.includeDetails,
      'includeMetadata': instance.includeMetadata,
      'format': instance.format,
    };
