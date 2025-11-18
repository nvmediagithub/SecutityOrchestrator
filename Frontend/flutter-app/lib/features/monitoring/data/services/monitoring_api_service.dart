import 'dart:convert';
import 'package:http/http.dart' as http;
import '../../domain/models/system_health.dart';
import '../../domain/models/metric.dart';
import '../../domain/models/alert.dart';

class MonitoringApiService {
  final String baseUrl;
  final http.Client client;

  MonitoringApiService({
    required this.baseUrl,
    http.Client? client,
  }) : client = client ?? http.Client();

  Future<SystemHealth> getSystemHealth() async {
    final response = await client.get(
      Uri.parse('$baseUrl/api/monitoring/health'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      final json = jsonDecode(response.body) as Map<String, dynamic>;
      return SystemHealth.fromJson(json);
    } else {
      throw Exception('Failed to load system health: ${response.statusCode}');
    }
  }

  Future<List<Metric>> getMetrics() async {
    final response = await client.get(
      Uri.parse('$baseUrl/api/monitoring/metrics'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      final jsonList = jsonDecode(response.body) as List<dynamic>;
      return jsonList.map((json) => Metric.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load metrics: ${response.statusCode}');
    }
  }

  Future<List<Alert>> getAlerts() async {
    final response = await client.get(
      Uri.parse('$baseUrl/api/monitoring/alerts'),
      headers: {'Content-Type': 'application/json'},
    );

    if (response.statusCode == 200) {
      final jsonList = jsonDecode(response.body) as List<dynamic>;
      return jsonList.map((json) => Alert.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load alerts: ${response.statusCode}');
    }
  }

  void dispose() {
    client.close();
  }
}