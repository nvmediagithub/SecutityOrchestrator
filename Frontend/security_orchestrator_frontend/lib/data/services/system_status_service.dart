// System Status Service for SecurityOrchestrator LLM Service
// Real-time monitoring and API integration with SecurityOrchestratorLLMFinal (port 8090)

import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/system_status_model.dart';
import '../../core/constants/api_constants.dart';

class SystemStatusService {
  static const Duration _timeout = Duration(seconds: 10);
  static const Duration _refreshInterval = Duration(seconds: 30);

  Timer? _refreshTimer;
  StreamController<SystemDashboardData>? _statusController;

  // Create a stream for real-time updates
  Stream<SystemDashboardData> get statusStream {
    _statusController ??= StreamController<SystemDashboardData>.broadcast();
    return _statusController!.stream;
  }

  // Start real-time monitoring
  void startMonitoring() {
    _refreshTimer?.cancel();
    _refreshTimer = Timer.periodic(_refreshInterval, (_) {
      fetchSystemStatus();
    });
    
    // Initial fetch
    fetchSystemStatus();
  }

  // Stop monitoring
  void stopMonitoring() {
    _refreshTimer?.cancel();
    _refreshTimer = null;
    _statusController?.close();
    _statusController = null;
  }

  // Fetch complete system status
  Future<SystemDashboardData> fetchSystemStatus() async {
    try {
      final data = SystemDashboardData.initial();
      _emitStatus(data.copyWith(isLoading: true));

      // Fetch all status components concurrently
      final results = await Future.wait([
        _fetchHealthStatus(),
        _fetchLLMStatus(),
        _fetchOllamaStatus(),
        _fetchCodeLlamaStatus(),
      ], eagerError: true);

      final systemStatus = results[0] as SystemStatus;
      final llmStatus = results[1] as LLMStatus;
      final ollamaStatus = results[2] as OllamaStatus;
      final codeLlamaStatus = results[3] as CodeLlamaStatus;

      final dashboardData = SystemDashboardData(
        systemStatus: systemStatus,
        llmStatus: llmStatus,
        ollamaStatus: ollamaStatus,
        codeLlamaStatus: codeLlamaStatus,
        lastUpdated: DateTime.now(),
        isLoading: false,
        errorMessage: null,
      );

      _emitStatus(dashboardData);
      return dashboardData;
    } catch (e) {
      final errorData = SystemDashboardData.initial().copyWith(
        isLoading: false,
        errorMessage: 'Failed to fetch system status: $e',
      );
      _emitStatus(errorData);
      rethrow;
    }
  }

  // Fetch health endpoint
  Future<SystemStatus> _fetchHealthStatus() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.llmBaseUrl}${ApiConstants.healthEndpoint}'),
      ).timeout(_timeout);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body);
        return SystemStatus.fromJson(json);
      } else {
        throw HttpException('Health check failed: ${response.statusCode}');
      }
    } catch (e) {
      return SystemStatus(
        service: 'SecurityOrchestrator LLM',
        status: 'error',
        message: 'Health check failed: $e',
        timestamp: DateTime.now(),
        isHealthy: false,
        additionalData: {'error': e.toString()},
      );
    }
  }

  // Fetch LLM status endpoint
  Future<LLMStatus> _fetchLLMStatus() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.llmBaseUrl}${ApiConstants.llmStatusEndpoint}'),
      ).timeout(_timeout);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body);
        return LLMStatus.fromJson(json);
      } else {
        throw HttpException('LLM status failed: ${response.statusCode}');
      }
    } catch (e) {
      return LLMStatus(
        service: 'SecurityOrchestrator LLM',
        status: 'error',
        port: '8090',
        integration: 'Unknown',
        ready: false,
        version: 'unknown',
        completion: '0%',
        timestamp: DateTime.now(),
        isOperational: false,
      );
    }
  }

  // Fetch Ollama status endpoint
  Future<OllamaStatus> _fetchOllamaStatus() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.llmBaseUrl}${ApiConstants.ollamaStatusEndpoint}'),
      ).timeout(_timeout);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body);
        return OllamaStatus.fromJson(json);
      } else {
        throw HttpException('Ollama status failed: ${response.statusCode}');
      }
    } catch (e) {
      return OllamaStatus(
        status: 'connection_failed',
        ollamaUrl: 'http://localhost:11434',
        message: 'Ollama connection failed',
        connected: false,
        response: null,
        error: e.toString(),
        timestamp: DateTime.now(),
        isAccessible: false,
      );
    }
  }

  // Fetch CodeLlama status (simulated from Ollama data)
  Future<CodeLlamaStatus> _fetchCodeLlamaStatus() async {
    try {
      // Check if CodeLlama 7B is available in Ollama
      final ollamaResponse = await http.get(
        Uri.parse('http://localhost:11434/api/tags'),
      ).timeout(_timeout);

      bool isLoaded = false;
      String? modelStatus;

      if (ollamaResponse.statusCode == 200) {
        final ollamaJson = jsonDecode(ollamaResponse.body);
        final models = ollamaJson['models'] as List?;
        
        if (models != null) {
          final codellamaModel = models.firstWhere(
            (model) => model['name'] == 'codellama:7b',
            orElse: () => null,
          );
          
          if (codellamaModel != null) {
            isLoaded = true;
            modelStatus = 'available';
          } else {
            modelStatus = 'not_installed';
          }
        }
      }

      return CodeLlamaStatus(
        modelName: 'codellama:7b',
        modelType: 'Code Generation',
        loaded: isLoaded,
        sizeGb: 3.8,
        contextWindow: 4096,
        maxTokens: 2048,
        status: modelStatus,
        lastChecked: DateTime.now().toIso8601String(),
        isActive: isLoaded,
        timestamp: DateTime.now(),
      );
    } catch (e) {
      return CodeLlamaStatus(
        modelName: 'codellama:7b',
        modelType: 'Code Generation',
        loaded: false,
        sizeGb: 3.8,
        contextWindow: 4096,
        maxTokens: 2048,
        status: 'error',
        lastChecked: DateTime.now().toIso8601String(),
        isActive: false,
        timestamp: DateTime.now(),
      );
    }
  }

  // LLM test endpoint
  Future<Map<String, dynamic>> testLLMIntegration() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.llmBaseUrl}${ApiConstants.llmTestEndpoint}'),
      ).timeout(_timeout);

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw HttpException('LLM test failed: ${response.statusCode}');
      }
    } catch (e) {
      return {
        'test': 'LLM Integration Test',
        'status': 'failed',
        'message': 'Test failed: $e',
        'error': e.toString(),
      };
    }
  }

  // Completion status endpoint
  Future<Map<String, dynamic>> checkCompletionStatus() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.llmBaseUrl}${ApiConstants.llmCompleteEndpoint}'),
      ).timeout(_timeout);

      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw HttpException('Completion check failed: ${response.statusCode}');
      }
    } catch (e) {
      return {
        'completion': '0%',
        'status': 'error',
        'message': 'Failed to check completion: $e',
        'error': e.toString(),
      };
    }
  }

  // Emit status update to stream
  void _emitStatus(SystemDashboardData data) {
    _statusController?.add(data);
  }

  // Dispose resources
  void dispose() {
    stopMonitoring();
  }
}

// Riverpod Provider
final systemStatusServiceProvider = Provider<SystemStatusService>((ref) {
  return SystemStatusService();
});

// State provider for system dashboard data
final systemDashboardProvider = StateNotifierProvider<SystemDashboardNotifier, AsyncValue<SystemDashboardData>>((ref) {
  final service = ref.read(systemStatusServiceProvider);
  return SystemDashboardNotifier(service);
});

class SystemDashboardNotifier extends StateNotifier<AsyncValue<SystemDashboardData>> {
  final SystemStatusService _service;
  StreamSubscription? _subscription;

  SystemDashboardNotifier(this._service) : super(const AsyncValue.loading()) {
    _initialize();
  }

  void _initialize() {
    // Start monitoring and listen to updates
    _service.startMonitoring();
    _subscription = _service.statusStream.listen((data) {
      state = AsyncValue.data(data);
    }, onError: (error) {
      state = AsyncValue.error(error, StackTrace.current);
    });
  }

  Future<void> refresh() async {
    state = const AsyncValue.loading();
    try {
      final data = await _service.fetchSystemStatus();
      state = AsyncValue.data(data);
    } catch (error, stackTrace) {
      state = AsyncValue.error(error, stackTrace);
    }
  }

  Future<Map<String, dynamic>> testLLM() async {
    return await _service.testLLMIntegration();
  }

  Future<Map<String, dynamic>> checkCompletion() async {
    return await _service.checkCompletionStatus();
  }

  @override
  void dispose() {
    _subscription?.cancel();
    _service.dispose();
    super.dispose();
  }
}