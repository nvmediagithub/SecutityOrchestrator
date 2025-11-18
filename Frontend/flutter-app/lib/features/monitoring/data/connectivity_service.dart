import 'dart:async';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:http/http.dart' as http;
import '../domain/connection_status.dart';

abstract class ConnectivityService {
  Stream<ConnectionStatus> get connectivityStream;
  Future<ConnectionStatus> checkBackendConnectivity();
}

class ConnectivityServiceImpl implements ConnectivityService {
  final Connectivity _connectivity;
  final String _backendUrl;
  StreamSubscription<List<ConnectivityResult>>? _subscription;
  final StreamController<ConnectionStatus> _statusController =
      StreamController<ConnectionStatus>.broadcast();

  ConnectivityServiceImpl(this._connectivity, this._backendUrl) {
    print('ConnectivityServiceImpl initialized with $_backendUrl');
    _initializeConnectivity();
  }

  void _initializeConnectivity() {
    _statusController.add(ConnectionStatus.checking);

    _subscription = _connectivity.onConnectivityChanged.listen((results) {
      if (results.isNotEmpty) {
        _updateConnectionStatus(results.first);
      }
    });

    _connectivity.checkConnectivity().then((results) {
      if (results.isNotEmpty) {
        _updateConnectionStatus(results.first);
      }
    });
  }

  void _updateConnectionStatus(ConnectivityResult result) async {
    if (result == ConnectivityResult.none) {
      _statusController.add(ConnectionStatus.disconnected);
      return;
    }

    _statusController.add(ConnectionStatus.checking);
    final status = await checkBackendConnectivity();
    _statusController.add(status);
  }

  @override
  Future<ConnectionStatus> checkBackendConnectivity() async {
    try {
      // Connect to backend service (port 8080)
      final response = await http.get(Uri.parse('$_backendUrl/api/health')).timeout(
            const Duration(seconds: 10),
          );

      if (response.statusCode == 200) {
        return ConnectionStatus.connected;
      } else {
        return ConnectionStatus.error;
      }
    } catch (e) {
      return ConnectionStatus.error;
    }
  }

  @override
  Stream<ConnectionStatus> get connectivityStream => _statusController.stream;

  void dispose() {
    _subscription?.cancel();
    _statusController.close();
  }
}