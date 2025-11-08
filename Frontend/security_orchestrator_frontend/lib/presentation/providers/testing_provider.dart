import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/services/testing_service.dart';
import '../../data/models/execution_monitoring_dto.dart';
import '../../data/models/owasp_dto.dart';
import '../../data/models/vulnerability_dto.dart';
import '../../core/network/websocket_client.dart';

// Testing Service Provider
final testingServiceProvider = Provider<TestingService>((ref) {
  return TestingService();
});

// WebSocket Provider for Testing
final testingWebSocketProvider = Provider<WebSocketClient>((ref) {
  return WebSocketClient(type: WebSocketType.execution);
});

// Current Testing State
final testingStateProvider = StateNotifierProvider<TestingStateNotifier, TestingState>((ref) {
  final testingService = ref.watch(testingServiceProvider);
  final webSocket = ref.watch(testingWebSocketProvider);
  return TestingStateNotifier(testingService, webSocket);
});

// Testing State
class TestingState {
  final List<ExecutionSessionDto> activeSessions;
  final List<ExecutionSessionDto> completedSessions;
  final ExecutionSessionDto? currentSession;
  final bool isLoading;
  final String? error;
  final List<OwaspCategoryDto> owaspCategories;
  final List<VulnerabilityDto> vulnerabilities;
  final TestResultsDashboardDto? resultsDashboard;

  TestingState({
    this.activeSessions = const [],
    this.completedSessions = const [],
    this.currentSession,
    this.isLoading = false,
    this.error,
    this.owaspCategories = const [],
    this.vulnerabilities = const [],
    this.resultsDashboard,
  });

  TestingState copyWith({
    List<ExecutionSessionDto>? activeSessions,
    List<ExecutionSessionDto>? completedSessions,
    ExecutionSessionDto? currentSession,
    bool? isLoading,
    String? error,
    List<OwaspCategoryDto>? owaspCategories,
    List<VulnerabilityDto>? vulnerabilities,
    TestResultsDashboardDto? resultsDashboard,
  }) {
    return TestingState(
      activeSessions: activeSessions ?? this.activeSessions,
      completedSessions: completedSessions ?? this.completedSessions,
      currentSession: currentSession ?? this.currentSession,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      owaspCategories: owaspCategories ?? this.owaspCategories,
      vulnerabilities: vulnerabilities ?? this.vulnerabilities,
      resultsDashboard: resultsDashboard ?? this.resultsDashboard,
    );
  }
}

// Testing State Notifier
class TestingStateNotifier extends StateNotifier<TestingState> {
  final TestingService _testingService;
  final WebSocketClient _webSocket;

  TestingStateNotifier(this._testingService, this._webSocket) : super(TestingState()) {
    _initializeWebSocket();
    _loadInitialData();
  }

  void _initializeWebSocket() {
    _webSocket.onExecutionUpdate = (executionId, progress) {
      _handleExecutionUpdate(executionId, progress);
    };

    _webSocket.onVulnerabilityUpdate = (sessionId, vulnerability) {
      _handleVulnerabilityUpdate(sessionId, vulnerability);
    };

    _webSocket.connect();
  }

  void _handleExecutionUpdate(String executionId, Map<String, dynamic> progress) {
    state = state.copyWith(
      activeSessions: state.activeSessions.map((session) {
        if (session.id == executionId) {
          return session.copyWith(
            progress: ExecutionProgressDto.fromJson(progress),
          );
        }
        return session;
      }).toList(),
    );
  }

  void _handleVulnerabilityUpdate(String sessionId, Map<String, dynamic> vulnerability) {
    final newVulnerability = VulnerabilityDto.fromJson(vulnerability);
    state = state.copyWith(
      vulnerabilities: [...state.vulnerabilities, newVulnerability],
    );
  }

  Future<void> _loadInitialData() async {
    state = state.copyWith(isLoading: true);
    try {
      // Load OWASP categories
      final categories = await _testingService.getOwaspCategories();
      
      // Load active sessions
      final activeSessions = await _testingService.getExecutionSessions(
        status: 'RUNNING',
        size: 20,
      );

      state = state.copyWith(
        isLoading: false,
        owaspCategories: categories,
        activeSessions: activeSessions,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  // Start End-to-End Testing
  Future<ExecutionSessionDto> startEndToEndTesting({
    required String openApiUrl,
    required List<String> bpmnFiles,
    required List<String> owaspCategories,
    required String sessionName,
    Map<String, dynamic>? configuration,
  }) async {
    try {
      state = state.copyWith(isLoading: true);
      
      final session = await _testingService.startEndToEndTesting(
        openApiUrl: openApiUrl,
        bpmnFiles: bpmnFiles,
        owaspCategories: owaspCategories,
        sessionName: sessionName,
        configuration: configuration,
      );

      // Subscribe to this session for real-time updates
      _webSocket.subscribeToExecution(session.id);

      state = state.copyWith(
        isLoading: false,
        currentSession: session,
        activeSessions: [session, ...state.activeSessions],
      );

      return session;
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
      rethrow;
    }
  }

  // Get Execution Session Details
  Future<ExecutionSessionDto> getExecutionSession(String sessionId) async {
    try {
      final session = await _testingService.getExecutionSession(sessionId);
      
      // Update in active or completed sessions list
      final updatedActiveSessions = state.activeSessions.map((s) {
        return s.id == sessionId ? session : s;
      }).toList();

      final updatedCompletedSessions = state.completedSessions.map((s) {
        return s.id == sessionId ? session : s;
      }).toList();

      state = state.copyWith(
        activeSessions: updatedActiveSessions,
        completedSessions: updatedCompletedSessions,
        currentSession: state.currentSession?.id == sessionId ? session : state.currentSession,
      );

      return session;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  // Cancel Execution
  Future<void> cancelExecution(String sessionId) async {
    try {
      await _testingService.cancelExecution(sessionId);
      
      // Remove from active sessions
      state = state.copyWith(
        activeSessions: state.activeSessions.where((s) => s.id != sessionId).toList(),
      );
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  // Load Test Results Dashboard
  Future<TestResultsDashboardDto> loadTestResultsDashboard(String sessionId) async {
    try {
      final dashboard = await _testingService.getTestResultsDashboard(sessionId);
      state = state.copyWith(resultsDashboard: dashboard);
      return dashboard;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  // Get Vulnerabilities
  Future<List<VulnerabilityDto>> getVulnerabilities({
    String? sessionId,
    String? severity,
    String? category,
  }) async {
    try {
      final vulnerabilities = await _testingService.getVulnerabilities(
        sessionId: sessionId,
        severity: severity,
        category: category,
      );
      
      state = state.copyWith(vulnerabilities: vulnerabilities);
      return vulnerabilities;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  // Update Vulnerability Status
  Future<void> updateVulnerabilityStatus(String vulnerabilityId, String status) async {
    try {
      final updatedVulnerability = await _testingService.updateVulnerabilityStatus(vulnerabilityId, status);
      
      state = state.copyWith(
        vulnerabilities: state.vulnerabilities.map((v) {
          return v.id == vulnerabilityId ? updatedVulnerability : v;
        }).toList(),
      );
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  // Load OWASP Categories
  Future<List<OwaspCategoryDto>> loadOwaspCategories() async {
    try {
      final categories = await _testingService.getOwaspCategories();
      state = state.copyWith(owaspCategories: categories);
      return categories;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  // Generate Report
  Future<String> generateReport(String sessionId, String format) async {
    try {
      final response = await _testingService.generateReport(sessionId, format);
      return response['reportId'] as String;
    } catch (e) {
      state = state.copyWith(error: e.toString());
      rethrow;
    }
  }

  // Clear Error
  void clearError() {
    state = state.copyWith(error: null);
  }

  // Set Current Session
  void setCurrentSession(ExecutionSessionDto? session) {
    state = state.copyWith(currentSession: session);
  }

  // Subscribe to Execution Updates
  void subscribeToExecution(String sessionId) {
    _webSocket.subscribeToExecution(sessionId);
  }

  // Unsubscribe from Execution Updates
  void unsubscribeFromExecution(String sessionId) {
    _webSocket.unsubscribeFromExecution(sessionId);
  }

  // Dispose
  @override
  void dispose() {
    _webSocket.dispose();
    super.dispose();
  }
}

// Extension for ExecutionSessionDto
extension ExecutionSessionExtension on ExecutionSessionDto {
  ExecutionSessionDto copyWith({
    ExecutionProgressDto? progress,
  }) {
    return ExecutionSessionDto(
      id: id,
      name: name,
      status: status,
      startedAt: startedAt,
      completedAt: completedAt,
      openApiUrl: openApiUrl,
      bpmnFiles: bpmnFiles,
      owaspCategories: owaspCategories,
      progress: progress ?? this.progress,
      steps: steps,
      logs: logs,
      configuration: configuration,
    );
  }
}