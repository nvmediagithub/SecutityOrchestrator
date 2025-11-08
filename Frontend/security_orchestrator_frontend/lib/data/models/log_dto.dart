import 'package:json_annotation/json_annotation.dart';

part 'log_dto.g.dart';

@JsonSerializable()
class LogEntryDto {
  final String id;
  final String sessionId;
  final String level; // DEBUG, INFO, WARN, ERROR
  final String message;
  final String category; // API_CALL, TEST_EXECUTION, VULNERABILITY_SCAN, BPMN_ANALYSIS
  final DateTime timestamp;
  final String source; // OPENAPI_MODULE, BPMN_MODULE, OWASP_MODULE, LLM_MODULE
  final Map<String, dynamic> metadata;
  final String? requestPayload;
  final String? responsePayload;
  final int? statusCode;
  final Duration? executionTime;
  final String? stackTrace;

  LogEntryDto({
    required this.id,
    required this.sessionId,
    required this.level,
    required this.message,
    required this.category,
    required this.timestamp,
    required this.source,
    required this.metadata,
    this.requestPayload,
    this.responsePayload,
    this.statusCode,
    this.executionTime,
    this.stackTrace,
  });

  factory LogEntryDto.fromJson(Map<String, dynamic> json) =>
      _$LogEntryDtoFromJson(json);

  Map<String, dynamic> toJson() => _$LogEntryDtoToJson(this);
}

@JsonSerializable()
class LogFilterDto {
  final String? sessionId;
  final List<String>? levels;
  final List<String>? categories;
  final List<String>? sources;
  final DateTime? startDate;
  final DateTime? endDate;
  final String? searchText;
  final int? limit;
  final int? offset;

  LogFilterDto({
    this.sessionId,
    this.levels,
    this.categories,
    this.sources,
    this.startDate,
    this.endDate,
    this.searchText,
    this.limit,
    this.offset,
  });

  factory LogFilterDto.fromJson(Map<String, dynamic> json) =>
      _$LogFilterDtoFromJson(json);

  Map<String, dynamic> toJson() => _$LogFilterDtoToJson(this);
}

@JsonSerializable()
class LogSummaryDto {
  final int totalLogs;
  final int errorCount;
  final int warningCount;
  final int infoCount;
  final int debugCount;
  final Map<String, int> logsByCategory;
  final Map<String, int> logsBySource;
  final List<String> recentErrors;

  LogSummaryDto({
    required this.totalLogs,
    required this.errorCount,
    required this.warningCount,
    required this.infoCount,
    required this.debugCount,
    required this.logsByCategory,
    required this.logsBySource,
    required this.recentErrors,
  });

  factory LogSummaryDto.fromJson(Map<String, dynamic> json) =>
      _$LogSummaryDtoFromJson(json);

  Map<String, dynamic> toJson() => _$LogSummaryDtoToJson(this);
}

enum LogLevel {
  @JsonValue('DEBUG')
  debug,
  @JsonValue('INFO')
  info,
  @JsonValue('WARN')
  warn,
  @JsonValue('ERROR')
  error,
}

enum LogCategory {
  @JsonValue('API_CALL')
  apiCall,
  @JsonValue('TEST_EXECUTION')
  testExecution,
  @JsonValue('VULNERABILITY_SCAN')
  vulnerabilityScan,
  @JsonValue('BPMN_ANALYSIS')
  bpmnAnalysis,
  @JsonValue('OPENAPI_ANALYSIS')
  openApiAnalysis,
  @JsonValue('OWASP_TEST')
  owaspTest,
  @JsonValue('LLM_ANALYSIS')
  llmAnalysis,
  @JsonValue('SYSTEM')
  system,
}