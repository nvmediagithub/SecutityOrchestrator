// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'log_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LogEntryDto _$LogEntryDtoFromJson(Map<String, dynamic> json) => LogEntryDto(
  id: json['id'] as String,
  sessionId: json['sessionId'] as String,
  level: json['level'] as String,
  message: json['message'] as String,
  category: json['category'] as String,
  timestamp: DateTime.parse(json['timestamp'] as String),
  source: json['source'] as String,
  metadata: json['metadata'] as Map<String, dynamic>,
  requestPayload: json['requestPayload'] as String?,
  responsePayload: json['responsePayload'] as String?,
  statusCode: (json['statusCode'] as num?)?.toInt(),
  executionTime: json['executionTime'] == null
      ? null
      : Duration(microseconds: (json['executionTime'] as num).toInt()),
  stackTrace: json['stackTrace'] as String?,
);

Map<String, dynamic> _$LogEntryDtoToJson(LogEntryDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'sessionId': instance.sessionId,
      'level': instance.level,
      'message': instance.message,
      'category': instance.category,
      'timestamp': instance.timestamp.toIso8601String(),
      'source': instance.source,
      'metadata': instance.metadata,
      'requestPayload': instance.requestPayload,
      'responsePayload': instance.responsePayload,
      'statusCode': instance.statusCode,
      'executionTime': instance.executionTime?.inMicroseconds,
      'stackTrace': instance.stackTrace,
    };

LogFilterDto _$LogFilterDtoFromJson(Map<String, dynamic> json) => LogFilterDto(
  sessionId: json['sessionId'] as String?,
  levels: (json['levels'] as List<dynamic>?)?.map((e) => e as String).toList(),
  categories: (json['categories'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList(),
  sources: (json['sources'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList(),
  startDate: json['startDate'] == null
      ? null
      : DateTime.parse(json['startDate'] as String),
  endDate: json['endDate'] == null
      ? null
      : DateTime.parse(json['endDate'] as String),
  searchText: json['searchText'] as String?,
  limit: (json['limit'] as num?)?.toInt(),
  offset: (json['offset'] as num?)?.toInt(),
);

Map<String, dynamic> _$LogFilterDtoToJson(LogFilterDto instance) =>
    <String, dynamic>{
      'sessionId': instance.sessionId,
      'levels': instance.levels,
      'categories': instance.categories,
      'sources': instance.sources,
      'startDate': instance.startDate?.toIso8601String(),
      'endDate': instance.endDate?.toIso8601String(),
      'searchText': instance.searchText,
      'limit': instance.limit,
      'offset': instance.offset,
    };

LogSummaryDto _$LogSummaryDtoFromJson(Map<String, dynamic> json) =>
    LogSummaryDto(
      totalLogs: (json['totalLogs'] as num).toInt(),
      errorCount: (json['errorCount'] as num).toInt(),
      warningCount: (json['warningCount'] as num).toInt(),
      infoCount: (json['infoCount'] as num).toInt(),
      debugCount: (json['debugCount'] as num).toInt(),
      logsByCategory: Map<String, int>.from(json['logsByCategory'] as Map),
      logsBySource: Map<String, int>.from(json['logsBySource'] as Map),
      recentErrors: (json['recentErrors'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
    );

Map<String, dynamic> _$LogSummaryDtoToJson(LogSummaryDto instance) =>
    <String, dynamic>{
      'totalLogs': instance.totalLogs,
      'errorCount': instance.errorCount,
      'warningCount': instance.warningCount,
      'infoCount': instance.infoCount,
      'debugCount': instance.debugCount,
      'logsByCategory': instance.logsByCategory,
      'logsBySource': instance.logsBySource,
      'recentErrors': instance.recentErrors,
    };
