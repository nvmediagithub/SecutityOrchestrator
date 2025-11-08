import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/services/logs_service.dart';
import '../../data/models/log_dto.dart';

// Logs Service Provider
final logsServiceProvider = Provider<LogsService>((ref) {
  return LogsService();
});

// Logs State
class LogsState {
  final List<LogEntryDto> logs;
  final LogSummaryDto? summary;
  final bool isLoading;
  final String? error;
  final String? currentSessionId;
  final bool isRealTimeEnabled;

  LogsState({
    this.logs = const [],
    this.summary,
    this.isLoading = false,
    this.error,
    this.currentSessionId,
    this.isRealTimeEnabled = false,
  });

  LogsState copyWith({
    List<LogEntryDto>? logs,
    LogSummaryDto? summary,
    bool? isLoading,
    String? error,
    String? currentSessionId,
    bool? isRealTimeEnabled,
  }) {
    return LogsState(
      logs: logs ?? this.logs,
      summary: summary ?? this.summary,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      currentSessionId: currentSessionId ?? this.currentSessionId,
      isRealTimeEnabled: isRealTimeEnabled ?? this.isRealTimeEnabled,
    );
  }
}

// Logs State Notifier
class LogsStateNotifier extends StateNotifier<LogsState> {
  final LogsService _logsService;

  LogsStateNotifier(this._logsService) : super(LogsState()) {
    _loadInitialData();
  }

  Future<void> _loadInitialData() async {
    state = state.copyWith(isLoading: true);
    try {
      final logs = await _logsService.getLogs(size: 50);
      final summary = await _logsService.getLogsSummary();
      
      state = state.copyWith(
        isLoading: false,
        logs: logs,
        summary: summary,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

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
      final logs = await _logsService.getLogs(
        sessionId: sessionId,
        levels: levels,
        categories: categories,
        sources: sources,
        startDate: startDate,
        endDate: endDate,
        searchText: searchText,
        page: page,
        size: size,
      );
      
      if (page == 0) {
        state = state.copyWith(logs: logs);
      } else {
        state = state.copyWith(logs: [...state.logs, ...logs]);
      }
      
      return logs;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  Future<LogSummaryDto> getLogsSummary({
    String? sessionId,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      final summary = await _logsService.getLogsSummary(
        sessionId: sessionId,
        startDate: startDate,
        endDate: endDate,
      );
      
      state = state.copyWith(summary: summary);
      return summary;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  Future<List<LogEntryDto>> getRecentLogs({int limit = 100, String? level}) async {
    try {
      final logs = await _logsService.getRecentLogs(limit: limit, level: level);
      state = state.copyWith(logs: logs);
      return logs;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  Future<List<LogEntryDto>> getErrorLogs({String? sessionId, int limit = 50}) async {
    try {
      final logs = await _logsService.getErrorLogs(sessionId: sessionId, limit: limit);
      state = state.copyWith(logs: logs);
      return logs;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  Future<List<LogEntryDto>> getLogsBySession(
    String sessionId, {
    int page = 0,
    int size = 50,
  }) async {
    try {
      final logs = await _logsService.getLogsBySession(sessionId, page: page, size: size);
      state = state.copyWith(
        logs: page == 0 ? logs : [...state.logs, ...logs],
        currentSessionId: sessionId,
      );
      return logs;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

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
      final logs = await _logsService.searchLogs(
        searchQuery,
        levels: levels,
        categories: categories,
        startDate: startDate,
        endDate: endDate,
        page: page,
        size: size,
      );
      
      if (page == 0) {
        state = state.copyWith(logs: logs);
      } else {
        state = state.copyWith(logs: [...state.logs, ...logs]);
      }
      
      return logs;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  Future<String> exportLogs({
    String? sessionId,
    List<String>? levels,
    List<String>? categories,
    DateTime? startDate,
    DateTime? endDate,
    String format = 'CSV',
  }) async {
    try {
      final exportId = await _logsService.exportLogs(
        sessionId: sessionId,
        levels: levels,
        categories: categories,
        startDate: startDate,
        endDate: endDate,
        format: format,
      );
      return exportId;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  Future<String> downloadExportedLogs(String exportId) async {
    try {
      final downloadUrl = await _logsService.downloadExportedLogs(exportId);
      return downloadUrl;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  Future<void> clearLogs({String? sessionId, DateTime? olderThan}) async {
    try {
      await _logsService.clearLogs(sessionId: sessionId, olderThan: olderThan);
      await _loadInitialData();
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  void clearError() {
    state = state.copyWith(error: null);
  }

  void setRealTimeEnabled(bool enabled) {
    state = state.copyWith(isRealTimeEnabled: enabled);
  }

  void setCurrentSession(String? sessionId) {
    state = state.copyWith(currentSessionId: sessionId);
  }

  void addLogEntry(LogEntryDto log) {
    state = state.copyWith(logs: [log, ...state.logs]);
  }
}

// Logs Provider
final logsStateProvider = StateNotifierProvider<LogsStateNotifier, LogsState>((ref) {
  final logsService = ref.watch(logsServiceProvider);
  return LogsStateNotifier(logsService);
});