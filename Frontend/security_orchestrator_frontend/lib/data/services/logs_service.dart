import 'package:dio/dio.dart';
import '../models/log_dto.dart';
import '../../core/constants/api_constants.dart';

class LogsService {
  late final Dio _dio;

  LogsService() {
    _dio = Dio();
    _dio.options.baseUrl = ApiConstants.baseUrl;
    _dio.options.headers['Content-Type'] ??= 'application/json';
  }

  // Get logs with filtering
  Future<List<LogEntryDto>> getLogs({
    String? sessionId,
    List<String>? levels,
    List<String>? categories,
    List<String>? sources,
    DateTime? startDate,
    DateTime? endDate,
    String? searchText,
    int page = 0,
    int size = 50,
  }) async {
    try {
      final queryParams = {
        'page': page,
        'size': size,
        if (sessionId != null) 'sessionId': sessionId,
        if (levels != null && levels.isNotEmpty) 'levels': levels.join(','),
        if (categories != null && categories.isNotEmpty) 'categories': categories.join(','),
        if (sources != null && sources.isNotEmpty) 'sources': sources.join(','),
        if (startDate != null) 'startDate': startDate.toIso8601String(),
        if (endDate != null) 'endDate': endDate.toIso8601String(),
        if (searchText != null) 'searchText': searchText,
      };

      final response = await _dio.get(
        ApiConstants.logsEndpoint,
        queryParameters: queryParams,
      );

      final list = response.data['content'] as List<dynamic>;
      return list
          .map((item) => LogEntryDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get logs: ${e.message}');
    }
  }

  // Get logs summary
  Future<LogSummaryDto> getLogsSummary({
    String? sessionId,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      final queryParams = {
        if (sessionId != null) 'sessionId': sessionId,
        if (startDate != null) 'startDate': startDate.toIso8601String(),
        if (endDate != null) 'endDate': endDate.toIso8601String(),
      };

      final response = await _dio.get(
        '${ApiConstants.logsEndpoint}/summary',
        queryParameters: queryParams,
      );

      return LogSummaryDto.fromJson(
        Map<String, dynamic>.from(response.data as Map),
      );
    } on DioException catch (e) {
      throw Exception('Failed to get logs summary: ${e.message}');
    }
  }

  // Get recent logs
  Future<List<LogEntryDto>> getRecentLogs({
    int limit = 100,
    String? level,
  }) async {
    try {
      final queryParams = {
        'limit': limit,
        if (level != null) 'level': level,
      };

      final response = await _dio.get(
        '${ApiConstants.logsEndpoint}/recent',
        queryParameters: queryParams,
      );

      final list = response.data as List<dynamic>;
      return list
          .map((item) => LogEntryDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get recent logs: ${e.message}');
    }
  }

  // Get error logs
  Future<List<LogEntryDto>> getErrorLogs({
    String? sessionId,
    int limit = 50,
  }) async {
    try {
      final queryParams = {
        'limit': limit,
        if (sessionId != null) 'sessionId': sessionId,
      };

      final response = await _dio.get(
        '${ApiConstants.logsEndpoint}/errors',
        queryParameters: queryParams,
      );

      final list = response.data as List<dynamic>;
      return list
          .map((item) => LogEntryDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get error logs: ${e.message}');
    }
  }

  // Get logs by session
  Future<List<LogEntryDto>> getLogsBySession(
    String sessionId, {
    int page = 0,
    int size = 50,
  }) async {
    try {
      final queryParams = {
        'page': page,
        'size': size,
      };

      final response = await _dio.get(
        '${ApiConstants.logsEndpoint}/session/$sessionId',
        queryParameters: queryParams,
      );

      final list = response.data['content'] as List<dynamic>;
      return list
          .map((item) => LogEntryDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get logs by session: ${e.message}');
    }
  }

  // Search logs
  Future<List<LogEntryDto>> searchLogs(
    String searchQuery, {
    List<String>? levels,
    List<String>? categories,
    DateTime? startDate,
    DateTime? endDate,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final queryParams = {
        'query': searchQuery,
        'page': page,
        'size': size,
        if (levels != null && levels.isNotEmpty) 'levels': levels.join(','),
        if (categories != null && categories.isNotEmpty) 'categories': categories.join(','),
        if (startDate != null) 'startDate': startDate.toIso8601String(),
        if (endDate != null) 'endDate': endDate.toIso8601String(),
      };

      final response = await _dio.get(
        '${ApiConstants.logsEndpoint}/search',
        queryParameters: queryParams,
      );

      final list = response.data['content'] as List<dynamic>;
      return list
          .map((item) => LogEntryDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to search logs: ${e.message}');
    }
  }

  // Export logs
  Future<String> exportLogs({
    String? sessionId,
    List<String>? levels,
    List<String>? categories,
    DateTime? startDate,
    DateTime? endDate,
    String format = 'CSV', // 'CSV', 'JSON', 'TXT'
  }) async {
    try {
      final response = await _dio.post(
        '${ApiConstants.logsEndpoint}/export',
        data: {
          'sessionId': sessionId,
          'levels': levels ?? [],
          'categories': categories ?? [],
          'startDate': startDate?.toIso8601String(),
          'endDate': endDate?.toIso8601String(),
          'format': format,
        },
      );

      return response.data['exportId'] as String;
    } on DioException catch (e) {
      throw Exception('Failed to export logs: ${e.message}');
    }
  }

  // Download exported logs
  Future<String> downloadExportedLogs(String exportId) async {
    try {
      final response = await _dio.get(
        '${ApiConstants.logsEndpoint}/export/$exportId/download',
      );

      return response.data['downloadUrl'] as String;
    } on DioException catch (e) {
      throw Exception('Failed to download exported logs: ${e.message}');
    }
  }

  // Get available log levels
  List<String> getAvailableLogLevels() {
    return ['DEBUG', 'INFO', 'WARN', 'ERROR'];
  }

  // Get available log categories
  List<String> getAvailableLogCategories() {
    return [
      'API_CALL',
      'TEST_EXECUTION',
      'VULNERABILITY_SCAN',
      'BPMN_ANALYSIS',
      'OPENAPI_ANALYSIS',
      'OWASP_TEST',
      'LLM_ANALYSIS',
      'SYSTEM',
    ];
  }

  // Get available log sources
  List<String> getAvailableLogSources() {
    return [
      'OPENAPI_MODULE',
      'BPMN_MODULE',
      'OWASP_MODULE',
      'LLM_MODULE',
      'TESTING_ORCHESTRATOR',
      'DATABASE',
    ];
  }

  // Clear logs
  Future<void> clearLogs({
    String? sessionId,
    DateTime? olderThan,
  }) async {
    try {
      final data = {
        if (sessionId != null) 'sessionId': sessionId,
        if (olderThan != null) 'olderThan': olderThan.toIso8601String(),
      };

      await _dio.delete(
        '${ApiConstants.logsEndpoint}/clear',
        data: data,
      );
    } on DioException catch (e) {
      throw Exception('Failed to clear logs: ${e.message}');
    }
  }

  // Get log statistics
  Future<Map<String, dynamic>> getLogStatistics({
    String? sessionId,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      final queryParams = {
        if (sessionId != null) 'sessionId': sessionId,
        if (startDate != null) 'startDate': startDate.toIso8601String(),
        if (endDate != null) 'endDate': endDate.toIso8601String(),
      };

      final response = await _dio.get(
        '${ApiConstants.logsEndpoint}/statistics',
        queryParameters: queryParams,
      );

      return response.data as Map<String, dynamic>;
    } on DioException catch (e) {
      throw Exception('Failed to get log statistics: ${e.message}');
    }
  }

  void dispose() {
    _dio.close();
  }
}