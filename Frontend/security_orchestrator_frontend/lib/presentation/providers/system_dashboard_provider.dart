// System Dashboard Provider for SecurityOrchestrator LLM Service
// Riverpod provider for system status monitoring and management

import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/services/system_status_service.dart';
import '../../data/models/system_status_model.dart';

// System Dashboard Provider
final systemDashboardProvider = StateNotifierProvider<SystemDashboardNotifier, AsyncValue<SystemDashboardData>>((ref) {
  final service = SystemStatusService();
  return SystemDashboardNotifier(service);
});

class SystemDashboardNotifier extends StateNotifier<AsyncValue<SystemDashboardData>> {
  final SystemStatusService _service;
  bool _isInitialized = false;

  SystemDashboardNotifier(this._service) : super(const AsyncValue.loading()) {
    _initialize();
  }

  void _initialize() {
    if (!_isInitialized) {
      _isInitialized = true;
      _startMonitoring();
    }
  }

  void _startMonitoring() {
    _service.startMonitoring();
    _service.statusStream.listen(
      (data) {
        state = AsyncValue.data(data);
      },
      onError: (error, stackTrace) {
        state = AsyncValue.error(error, stackTrace);
      },
    );

    // Initial fetch
    _fetchSystemData();
  }

  Future<void> _fetchSystemData() async {
    state = const AsyncValue.loading();
    try {
      final data = await _service.fetchSystemStatus();
      state = AsyncValue.data(data);
    } catch (error, stackTrace) {
      state = AsyncValue.error(error, stackTrace);
    }
  }

  Future<void> refresh() async {
    await _fetchSystemData();
  }

  Future<Map<String, dynamic>> testLLMIntegration() async {
    return await _service.testLLMIntegration();
  }

  Future<Map<String, dynamic>> checkCompletionStatus() async {
    return await _service.checkCompletionStatus();
  }

  // Get system health status
  bool get isSystemHealthy {
    return state.when(
      data: (dashboard) => dashboard.isSystemHealthy,
      loading: () => false,
      error: (_, __) => false,
    );
  }

  // Get current dashboard data
  SystemDashboardData? get currentData {
    return state.when(
      data: (dashboard) => dashboard,
      loading: () => null,
      error: (_, __) => null,
    );
  }

  // Get LLM service status
  bool get isLLMAvailable {
    return state.when(
      data: (dashboard) => dashboard.llmStatus.isOperational,
      loading: () => false,
      error: (_, __) => false,
    );
  }

  // Get Ollama connection status
  bool get isOllamaConnected {
    return state.when(
      data: (dashboard) => dashboard.ollamaStatus.isAccessible,
      loading: () => false,
      error: (_, __) => false,
    );
  }

  // Get CodeLlama model status
  bool get isCodeLlamaLoaded {
    return state.when(
      data: (dashboard) => dashboard.codeLlamaStatus.loaded,
      loading: () => false,
      error: (_, __) => false,
    );
  }

  // Get system uptime percentage
  double get systemUptime {
    return state.when(
      data: (dashboard) {
        final criticalServices = dashboard.criticalServicesStatus;
        if (criticalServices.isEmpty) return 0.0;
        final healthyServices = criticalServices.where((status) => status).length;
        return (healthyServices / criticalServices.length) * 100;
      },
      loading: () => 0.0,
      error: (_, __) => 0.0,
    );
  }

  // Get last update time
  DateTime? get lastUpdateTime {
    return state.when(
      data: (dashboard) => dashboard.lastUpdated,
      loading: () => null,
      error: (_, __) => null,
    );
  }

  // Get error message if any
  String? get errorMessage {
    return state.when(
      data: (dashboard) => dashboard.errorMessage,
      loading: () => null,
      error: (error, __) => error.toString(),
    );
  }

  @override
  void dispose() {
    _service.stopMonitoring();
    super.dispose();
  }
}

// Provider for system status service
final systemStatusServiceProvider = Provider<SystemStatusService>((ref) {
  return SystemStatusService();
});

// Provider for system health check
final systemHealthProvider = Provider<bool>((ref) {
  final notifier = ref.watch(systemDashboardProvider.notifier);
  return notifier.isSystemHealthy;
});

// Provider for LLM availability
final llmAvailabilityProvider = Provider<bool>((ref) {
  final notifier = ref.watch(systemDashboardProvider.notifier);
  return notifier.isLLMAvailable;
});

// Provider for CodeLlama status
final codeLlamaStatusProvider = Provider<CodeLlamaStatus?>((ref) {
  return ref.watch(systemDashboardProvider).when(
    data: (dashboard) => dashboard.codeLlamaStatus,
    loading: () => null,
    error: (_, __) => null,
  );
});

// Provider for system metrics
final systemMetricsProvider = Provider<SystemMetrics>((ref) {
  final dashboard = ref.watch(systemDashboardProvider);
  
  return dashboard.when(
    data: (data) => SystemMetrics(
      uptime: data.isSystemHealthy ? 100.0 : 0.0,
      servicesOnline: data.criticalServicesStatus.where((s) => s).length,
      totalServices: data.criticalServicesStatus.length,
      lastUpdate: data.lastUpdated,
      errorCount: data.errorMessage != null ? 1 : 0,
    ),
    loading: () => const SystemMetrics(
      uptime: 0.0,
      servicesOnline: 0,
      totalServices: 3,
      lastUpdate: null,
      errorCount: 0,
    ),
    error: (_, __) => const SystemMetrics(
      uptime: 0.0,
      servicesOnline: 0,
      totalServices: 3,
      lastUpdate: null,
      errorCount: 1,
    ),
  );
});

class SystemMetrics {
  final double uptime;
  final int servicesOnline;
  final int totalServices;
  final DateTime? lastUpdate;
  final int errorCount;

  const SystemMetrics({
    required this.uptime,
    required this.servicesOnline,
    required this.totalServices,
    this.lastUpdate,
    required this.errorCount,
  });

  double get uptimePercentage => uptime;
  int get healthScore => ((servicesOnline / totalServices) * 100).round();
  bool get isHealthy => healthScore >= 100;
  bool get hasErrors => errorCount > 0;
  
  String get statusText {
    if (isHealthy) return 'All Systems Operational';
    if (servicesOnline > 0) return 'Partial Operation';
    return 'System Issues Detected';
  }
}