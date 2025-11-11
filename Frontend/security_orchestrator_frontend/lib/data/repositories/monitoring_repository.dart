import 'dart:convert';
import 'dart:io';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import '../models/monitoring_models.dart';

/// Provider for the monitoring repository
final monitoringRepositoryProvider = Provider<MonitoringRepository>((ref) {
  return MonitoringRepository();
});

class MonitoringRepository {
  // Updated to use OWASP backend port 8091
  static const String baseUrl = 'http://localhost:8091/api/monitoring';
  static const String wsUrl = 'ws://localhost:8091/ws';
  
  WebSocketChannel? _channel;
  bool _isConnected = false;

  /// Get dashboard summary with all monitoring data
  Future<Map<String, dynamic>> getDashboardSummary() async {
    try {
      final response = await _makeGetRequest('/dashboard');
      return json.decode(response.body) as Map<String, dynamic>;
    } catch (e) {
      // Return mock data for development
      return _getMockDashboardData();
    }
  }

  /// Get system health information
  Future<SystemHealth> getSystemHealth() async {
    try {
      final response = await _makeGetRequest('/health');
      final data = json.decode(response.body) as Map<String, dynamic>;
      data['timestamp'] = DateTime.parse(data['timestamp']);
      return SystemHealth.fromJson(data);
    } catch (e) {
      // Return mock data for development
      return _getMockSystemHealth();
    }
  }

  /// Get active alerts
  Future<List<Alert>> getActiveAlerts() async {
    try {
      final response = await _makeGetRequest('/alerts');
      final List<dynamic> data = json.decode(response.body) as List<dynamic>;
      return data.map((item) {
        item['timestamp'] = DateTime.parse(item['timestamp']);
        return Alert.fromJson(item as Map<String, dynamic>);
      }).toList();
    } catch (e) {
      // Return mock data for development
      return _getMockAlerts();
    }
  }

  /// Get active models information
  Future<List<ModelInfo>> getActiveModels() async {
    try {
      final response = await _makeGetRequest('/models');
      final List<dynamic> data = json.decode(response.body) as List<dynamic>;
      return data.map((item) => ModelInfo.fromJson(item as Map<String, dynamic>)).toList();
    } catch (e) {
      // Return mock data for development
      return _getMockModels();
    }
  }

  /// Get system resources usage
  Future<SystemResourceUsage> getSystemResources() async {
    try {
      final response = await _makeGetRequest('/resources');
      final data = json.decode(response.body) as Map<String, dynamic>;
      return SystemResourceUsage.fromJson(data);
    } catch (e) {
      // Return mock data for development
      return _getMockSystemResourceUsage();
    }
  }

  /// Get historical metrics for a specific type and time period
  Future<List<dynamic>> getHistoricalMetrics(String metricType, String period) async {
    try {
      final response = await _makeGetRequest('/metrics/$metricType?period=$period');
      final List<dynamic> data = json.decode(response.body) as List<dynamic>;
      return data.map((item) {
        item['timestamp'] = DateTime.parse(item['timestamp']);
        return item;
      }).toList();
    } catch (e) {
      // Return mock data for development
      return _getMockMetricsData(metricType);
    }
  }

  /// Subscribe to real-time metrics via WebSocket
  Stream<Map<String, double>> subscribeToRealtimeMetrics(List<String> metricTypes) async* {
    try {
      await _connectWebSocket();
      
      // Subscribe to requested metrics
      _sendWebSocketMessage({
        'metricTypes': metricTypes,
        'dashboardId': 'main_dashboard',
      });

      yield* _channel!.stream.map((data) {
        try {
          final jsonData = json.decode(data) as Map<String, dynamic>;
          return _parseRealtimeMetrics(jsonData);
        } catch (e) {
          return <String, double>{};
        }
      });
    } catch (e) {
      // Return empty stream on error
      yield* const Stream.empty();
    }
  }

  /// Record API request for monitoring
  Future<void> recordApiRequest(bool successful, int responseTimeMs) async {
    try {
      await _makePostRequest('/api-request', {
        'successful': successful,
        'responseTimeMs': responseTimeMs,
      });
    } catch (e) {
      // Log error but don't throw
      print('Failed to record API request: $e');
    }
  }

  /// Connect to WebSocket for real-time updates
  Future<void> _connectWebSocket() async {
    if (_isConnected && _channel != null) return;

    try {
      _channel = WebSocketChannel.connect(Uri.parse(wsUrl));
      _isConnected = true;
      
      _channel!.stream.listen((data) {
        // Handle incoming WebSocket messages
        print('WebSocket message received: $data');
      }, onError: (error) {
        print('WebSocket error: $error');
        _isConnected = false;
      }, onDone: () {
        print('WebSocket connection closed');
        _isConnected = false;
      });
    } catch (e) {
      print('Failed to connect to WebSocket: $e');
      _isConnected = false;
    }
  }

  /// Send message through WebSocket
  void _sendWebSocketMessage(Map<String, dynamic> message) {
    if (_isConnected && _channel != null) {
      _channel!.sink.add(json.encode(message));
    }
  }

  /// Make GET request to the API
  Future<HttpClientResponse> _makeGetRequest(String endpoint) async {
    final client = HttpClient();
    final request = await client.getUrl(Uri.parse('$baseUrl$endpoint'));
    request.headers.set('Content-Type', 'application/json');
    
    final response = await request.close();
    return response;
  }

  /// Make POST request to the API
  Future<HttpClientResponse> _makePostRequest(String endpoint, Map<String, dynamic> data) async {
    final client = HttpClient();
    final request = await client.postUrl(Uri.parse('$baseUrl$endpoint'));
    request.headers.set('Content-Type', 'application/json');
    request.write(json.encode(data));
    
    final response = await request.close();
    return response;
  }

  /// Parse real-time metrics from WebSocket message
  Map<String, double> _parseRealtimeMetrics(Map<String, dynamic> jsonData) {
    final result = <String, double>{};
    
    jsonData.forEach((key, value) {
      if (value is Map<String, dynamic> && value.containsKey('value')) {
        result[key] = (value['value'] as num).toDouble();
      } else if (value is num) {
        result[key] = value.toDouble();
      }
    });
    
    return result;
  }

  // Mock data methods for development
  Map<String, dynamic> _getMockDashboardData() {
    return {
      'systemHealth': _getMockSystemHealth().toJson(),
      'metricsReport': _getMockMetricsReport().toJson(),
      'activeModels': _getMockModels().map((m) => m.toJson()).toList(),
      'activeAlerts': _getMockAlerts().map((a) => a.toJson()).toList(),
      'timestamp': DateTime.now().toIso8601String(),
    };
  }

  SystemHealth _getMockSystemHealth() {
    return SystemHealth(
      cpuUsage: 45.2,
      memoryUsage: 67.8,
      diskUsage: 23.1,
      databaseConnectionStatus: 'CONNECTED',
      averageResponseTime: 125.5,
      activeModelCount: 3,
      timestamp: DateTime.now(),
    );
  }

  List<Alert> _getMockAlerts() {
    return [
      Alert(
        id: 'high_cpu_usage_1',
        type: 'HIGH_CPU_USAGE',
        message: 'CPU usage above 80%',
        severity: AlertSeverity.warning,
        timestamp: DateTime.now().subtract(Duration(minutes: 5)),
      ),
    ];
  }

  List<ModelInfo> _getMockModels() {
    return [
      ModelInfo(
        modelId: 'local-llm-1',
        modelName: 'llama-2-7b',
        provider: 'LOCAL',
        status: 'LOADED',
        sizeGB: 1.2,
      ),
      ModelInfo(
        modelId: 'openrouter-1',
        modelName: 'gpt-3.5-turbo',
        provider: 'OPENROUTER',
        status: 'AVAILABLE',
      ),
      ModelInfo(
        modelId: 'onnx-1',
        modelName: 'bert-base',
        provider: 'ONNX',
        status: 'LOADED',
        sizeGB: 0.8,
      ),
    ];
  }

  SystemResourceUsage _getMockSystemResourceUsage() {
    return SystemResourceUsage(
      cpuUsage: 45.2,
      memoryUsage: 67.8,
      diskUsage: 23.1,
      activeConnections: 12,
      threadCount: 45,
    );
  }

  MetricsReport _getMockMetricsReport() {
    return MetricsReport(
      systemHealth: _getMockSystemHealth(),
      llmMetrics: {'responseTime': 120.0, 'successRate': 0.98},
      apiSuccessRate: 0.97,
      totalApiRequests: 15420,
      activeUsers: 23,
      uptime: Duration(hours: 5, minutes: 42),
      systemResourceUsage: _getMockSystemResourceUsage(),
      activeModels: _getMockModels(),
      recentAlerts: _getMockAlerts(),
    );
  }

  List<dynamic> _getMockMetricsData(String metricType) {
    final now = DateTime.now();
    final List<dynamic> metrics = [];
    
    for (int i = 0; i < 20; i++) {
      final timestamp = now.subtract(Duration(minutes: 3 * i));
      double value;
      
      switch (metricType) {
        case 'cpu_usage':
          value = 40 + (20 * (i % 5) + (i % 3) * 5);
          break;
        case 'memory_usage':
          value = 60 + (i % 4) * 10;
          break;
        case 'api_response_time':
          value = 100 + (i % 10) * 20;
          break;
        case 'llm_response_time':
          value = 120 + (i % 8) * 15;
          break;
        default:
          value = i * 5.0;
      }
      
      metrics.add({
        'type': metricType,
        'value': value,
        'timestamp': timestamp.toIso8601String(),
      });
    }
    
    return metrics;
  }
}