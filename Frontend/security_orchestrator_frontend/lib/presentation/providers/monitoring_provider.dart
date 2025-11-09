import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/repositories/monitoring_repository.dart';
import '../../data/models/monitoring_models.dart';

/// Provider for the main monitoring dashboard
final monitoringDashboardProvider = FutureProvider<Map<String, dynamic>>((ref) async {
  final repository = ref.watch(monitoringRepositoryProvider);
  return await repository.getDashboardSummary();
});

/// Provider for system health
final systemHealthProvider = FutureProvider<SystemHealth>((ref) async {
  final repository = ref.watch(monitoringRepositoryProvider);
  return await repository.getSystemHealth();
});

/// Provider for active alerts
final alertsProvider = FutureProvider<List<Alert>>((ref) async {
  final repository = ref.watch(monitoringRepositoryProvider);
  return await repository.getActiveAlerts();
});

/// Provider for active models
final activeModelsProvider = FutureProvider<List<ModelInfo>>((ref) async {
  final repository = ref.watch(monitoringRepositoryProvider);
  return await repository.getActiveModels();
});

/// Provider for real-time metrics
final realtimeMetricsProvider = StreamProvider.family<Map<String, double>, String>((ref, metricType) {
  final repository = ref.watch(monitoringRepositoryProvider);
  return repository.subscribeToRealtimeMetrics([metricType]);
});

/// Provider for historical metrics
final historicalMetricsProvider = FutureProvider.family<List<MetricPoint>, Map<String, dynamic>>((ref, params) async {
  final repository = ref.watch(monitoringRepositoryProvider);
  final String metricType = params['metricType'] as String;
  final String period = params['period'] as String;
  return await repository.getHistoricalMetrics(metricType, period);
});

/// Provider for system resources
final systemResourcesProvider = FutureProvider<SystemResourceUsage>((ref) async {
  final repository = ref.watch(monitoringRepositoryProvider);
  return await repository.getSystemResources();
});

/// Provider for WebSocket connection status
final websocketConnectionProvider = StateProvider<bool>((ref) => false);

/// Provider for auto-refresh toggle
final autoRefreshProvider = StateProvider<bool>((ref) => true);