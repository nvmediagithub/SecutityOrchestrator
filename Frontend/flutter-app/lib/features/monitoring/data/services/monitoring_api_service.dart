import 'dart:convert';
import 'package:http/http.dart' as http;
import '../../domain/models/alert.dart';
import '../../domain/models/metric.dart';
import '../../domain/models/system_health.dart';
import '../../domain/models/llm_analytics.dart';
import '../../domain/models/llm_connectivity.dart';

class MonitoringApiService {
  final String baseUrl;
  final http.Client client;

  MonitoringApiService({required this.baseUrl, http.Client? client})
    : client = client ?? http.Client();

  static const _jsonHeaders = {'Content-Type': 'application/json'};

  Future<SystemHealth> getSystemHealth() async {
    final data =
        await _getData('/api/monitoring/health') as Map<String, dynamic>;
    return SystemHealth.fromJson(data);
  }

  Future<List<Metric>> getMetrics() async {
    final data = await _getData('/api/monitoring/metrics') as List<dynamic>;
    return data
        .map((json) => Metric.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  Future<List<Alert>> getAlerts() async {
    final data = await _getData('/api/monitoring/alerts') as List<dynamic>;
    return data
        .map((json) => Alert.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  Future<LlmAnalytics> getLlmAnalytics() async {
    final data = await _getData('/api/monitoring/llm') as Map<String, dynamic>;
    return LlmAnalytics.fromJson(data);
  }

  Future<LlmAnalytics> activateProvider(String providerId) async {
    final response = await client.post(
      Uri.parse('$baseUrl/api/monitoring/llm/providers/$providerId/activate'),
      headers: _jsonHeaders,
    );
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('Failed to activate provider: ${response.statusCode}');
    }
    final decoded = jsonDecode(response.body) as Map<String, dynamic>;
    if (decoded['success'] != true) {
      final message = decoded['message'] ?? 'Unknown error';
      throw Exception(message);
    }
    return LlmAnalytics.fromJson(decoded['data'] as Map<String, dynamic>);
  }

  Future<LlmConnectivity> checkConnectivity() async {
    final response = await client.get(
      Uri.parse('$baseUrl/api/monitoring/llm/check'),
      headers: _jsonHeaders,
    );
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('Connectivity check failed: ${response.statusCode}');
    }
    final decoded = jsonDecode(response.body) as Map<String, dynamic>;
    if (decoded['success'] != true) {
      final message = decoded['message'] ?? 'Unknown error';
      throw Exception(message);
    }
    return LlmConnectivity.fromJson(decoded['data'] as Map<String, dynamic>);
  }

  Future<dynamic> _getData(String path) async {
    final response = await client.get(
      Uri.parse('$baseUrl$path'),
      headers: _jsonHeaders,
    );

    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception(
        'Request to $path failed with status ${response.statusCode}',
      );
    }

    final decoded = jsonDecode(response.body) as Map<String, dynamic>;
    if (decoded['success'] != true) {
      final message = decoded['message'] ?? 'Unknown error';
      throw Exception('Request to $path failed: $message');
    }

    return decoded['data'];
  }
}
