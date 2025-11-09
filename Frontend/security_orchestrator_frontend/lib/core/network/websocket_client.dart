import 'dart:convert';
import 'package:web_socket_channel/web_socket_channel.dart';
import '../constants/api_constants.dart';

enum WebSocketType {
  execution,
  logs,
  vulnerabilities,
  general,
}

class WebSocketClient {
  WebSocketChannel? _channel;
  final String url;
  final Duration reconnectDelay;
  bool _isConnected = false;
  WebSocketType _socketType = WebSocketType.general;
  
  // Callbacks for different types of messages
  Function(Map<String, dynamic>)? onMessage;
  Function()? onConnected;
  Function()? onDisconnected;
  Function(Object)? onError;
  
  // Specific callbacks for testing system
  Function(String executionId, Map<String, dynamic> progress)? onExecutionUpdate;
  Function(String sessionId, Map<String, dynamic> log)? onLogUpdate;
  Function(String sessionId, Map<String, dynamic> vulnerability)? onVulnerabilityUpdate;
  Function(Map<String, dynamic> status)? onSystemStatus;

  WebSocketClient({
    String? customUrl,
    WebSocketType type = WebSocketType.general,
    this.reconnectDelay = const Duration(seconds: 5),
  }) : url = customUrl ?? _getDefaultUrl(type),
       _socketType = type;

  bool get isConnected => _isConnected;

  static String _getDefaultUrl(WebSocketType type) {
    switch (type) {
      case WebSocketType.execution:
        return ApiConstants.websocketEndpoint;
      case WebSocketType.logs:
        return ApiConstants.logsWebsocketEndpoint;
      case WebSocketType.vulnerabilities:
        return ApiConstants.vulnerabilityWebsocketEndpoint;
      case WebSocketType.general:
      default:
        return ApiConstants.websocketEndpoint;
    }
  }

  void connect() {
    try {
      _channel = WebSocketChannel.connect(Uri.parse(url));
      _isConnected = true;

      _channel!.stream.listen(
        (message) {
          try {
            final data = jsonDecode(message);
            _handleMessage(data);
          } catch (e) {
            onError?.call(e);
          }
        },
        onDone: () {
          _isConnected = false;
          onDisconnected?.call();
          _scheduleReconnect();
        },
        onError: (error) {
          _isConnected = false;
          onError?.call(error);
          _scheduleReconnect();
        },
      );

      onConnected?.call();
    } catch (e) {
      onError?.call(e);
      _scheduleReconnect();
    }
  }

  void disconnect() {
    _channel?.sink.close();
    _isConnected = false;
  }

  void _scheduleReconnect() {
    Future.delayed(reconnectDelay, connect);
  }

  void send(Map<String, dynamic> message) {
    if (_isConnected && _channel != null) {
      _channel!.sink.add(jsonEncode(message));
    }
  }

  void _handleMessage(Map<String, dynamic> data) {
    final messageType = data['type'] as String?;
    
    switch (messageType) {
      case 'EXECUTION_UPDATE':
        final executionId = data['executionId'] as String?;
        final progress = data['progress'] as Map<String, dynamic>?;
        if (executionId != null && progress != null) {
          onExecutionUpdate?.call(executionId, progress);
        }
        break;
        
      case 'LOG_UPDATE':
        final sessionId = data['sessionId'] as String?;
        final log = data['log'] as Map<String, dynamic>?;
        if (sessionId != null && log != null) {
          onLogUpdate?.call(sessionId, log);
        }
        break;
        
      case 'VULNERABILITY_UPDATE':
        final sessionId = data['sessionId'] as String?;
        final vulnerability = data['vulnerability'] as Map<String, dynamic>?;
        if (sessionId != null && vulnerability != null) {
          onVulnerabilityUpdate?.call(sessionId, vulnerability);
        }
        break;
        
      case 'SYSTEM_STATUS':
        final status = data['status'] as Map<String, dynamic>?;
        if (status != null) {
          onSystemStatus?.call(status);
        }
        break;
        
      default:
        onMessage?.call(data);
        break;
    }
  }

  // Execution monitoring
  void subscribeToExecution(String executionId) {
    send({
      'type': 'SUBSCRIBE_EXECUTION',
      'executionId': executionId,
    });
  }

  void unsubscribeFromExecution(String executionId) {
    send({
      'type': 'UNSUBSCRIBE_EXECUTION',
      'executionId': executionId,
    });
  }

  void subscribeToAllExecutions() {
    send({
      'type': 'SUBSCRIBE_ALL_EXECUTIONS',
    });
  }

  // Log monitoring
  void subscribeToLogs(String sessionId) {
    send({
      'type': 'SUBSCRIBE_LOGS',
      'sessionId': sessionId,
    });
  }

  void unsubscribeFromLogs(String sessionId) {
    send({
      'type': 'UNSUBSCRIBE_LOGS',
      'sessionId': sessionId,
    });
  }

  void subscribeToAllLogs() {
    send({
      'type': 'SUBSCRIBE_ALL_LOGS',
    });
  }

  // Vulnerability monitoring
  void subscribeToVulnerabilities(String sessionId) {
    send({
      'type': 'SUBSCRIBE_VULNERABILITIES',
      'sessionId': sessionId,
    });
  }

  void unsubscribeFromVulnerabilities(String sessionId) {
    send({
      'type': 'UNSUBSCRIBE_VULNERABILITIES',
      'sessionId': sessionId,
    });
  }

  void subscribeToAllVulnerabilities() {
    send({
      'type': 'SUBSCRIBE_ALL_VULNERABILITIES',
    });
  }

  // System status
  void subscribeToSystemStatus() {
    send({
      'type': 'SUBSCRIBE_STATUS',
    });
  }

  void requestHeartbeat() {
    send({
      'type': 'HEARTBEAT',
      'timestamp': DateTime.now().toIso8601String(),
    });
  }

  // Send custom message
  void sendCustomMessage(String type, Map<String, dynamic> data) {
    send({
      'type': type,
      ...data,
    });
  }

  // Connection quality
  void ping() {
    send({
      'type': 'PING',
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    });
  }

  // Auto-reconnection management
  bool _shouldReconnect = true;
  
  void stopReconnecting() {
    _shouldReconnect = false;
  }

  void startReconnecting() {
    _shouldReconnect = true;
  }

  // Clean disconnect
  void close() {
    _shouldReconnect = false;
    disconnect();
  }
}