import 'dart:convert';
import 'package:web_socket_channel/web_socket_channel.dart';
import '../constants/api_constants.dart';

class WebSocketClient {
  WebSocketChannel? _channel;
  final String url;
  final Duration reconnectDelay;
  bool _isConnected = false;
  Function(Map<String, dynamic>)? onMessage;
  Function()? onConnected;
  Function()? onDisconnected;
  Function(Object)? onError;

  WebSocketClient({
    this.url = ApiConstants.websocketEndpoint,
    this.reconnectDelay = const Duration(seconds: 5),
  });

  bool get isConnected => _isConnected;

  void connect() {
    try {
      _channel = WebSocketChannel.connect(Uri.parse(url));
      _isConnected = true;

      _channel!.stream.listen(
        (message) {
          try {
            final data = jsonDecode(message);
            onMessage?.call(data);
          } catch (e) {
            onError?.call(e);
          }
        },
        onDone: () {
          _isConnected = false;
          onDisconnected?.call();
          // Auto-reconnect
          Future.delayed(reconnectDelay, connect);
        },
        onError: (error) {
          _isConnected = false;
          onError?.call(error);
          // Auto-reconnect
          Future.delayed(reconnectDelay, connect);
        },
      );

      onConnected?.call();
    } catch (e) {
      onError?.call(e);
      // Retry connection
      Future.delayed(reconnectDelay, connect);
    }
  }

  void disconnect() {
    _channel?.sink.close();
    _isConnected = false;
  }

  void send(Map<String, dynamic> message) {
    if (_isConnected && _channel != null) {
      _channel!.sink.add(jsonEncode(message));
    }
  }

  void subscribeToExecution(String executionId) {
    send({
      'type': 'SUBSCRIBE',
      'executionId': executionId,
    });
  }

  void unsubscribeFromExecution(String executionId) {
    send({
      'type': 'UNSUBSCRIBE',
      'executionId': executionId,
    });
  }
}