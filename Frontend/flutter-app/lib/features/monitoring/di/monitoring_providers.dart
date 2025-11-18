import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers.dart';
import '../data/services/monitoring_api_service.dart';
import '../domain/models/alert.dart';
import '../domain/models/metric.dart';
import '../domain/models/system_health.dart';

final monitoringApiServiceProvider = Provider<MonitoringApiService>((ref) {
  final baseUrl = ref.watch(backendBaseUrlProvider);
  final client = ref.watch(httpClientProvider);
  return MonitoringApiService(baseUrl: baseUrl, client: client);
});

final systemHealthProvider = FutureProvider<SystemHealth>((ref) async {
  final service = ref.watch(monitoringApiServiceProvider);
  return service.getSystemHealth();
});

final metricsProvider = FutureProvider<List<Metric>>((ref) async {
  final service = ref.watch(monitoringApiServiceProvider);
  return service.getMetrics();
});

final alertsProvider = FutureProvider<List<Alert>>((ref) async {
  final service = ref.watch(monitoringApiServiceProvider);
  return service.getAlerts();
});
