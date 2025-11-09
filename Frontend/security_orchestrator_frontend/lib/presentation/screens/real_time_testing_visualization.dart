import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:security_orchestrator_frontend/core/network/websocket_client.dart';
import 'package:security_orchestrator_frontend/data/models/api_response.dart';

class RealTimeTestingVisualization extends StatefulWidget {
  final String executionId;

  const RealTimeTestingVisualization({
    Key? key,
    required this.executionId,
  }) : super(key: key);

  @override
  State<RealTimeTestingVisualization> createState() => _RealTimeTestingVisualizationState();
}

class _RealTimeTestingVisualizationState extends State<RealTimeTestingVisualization> {
  final WebSocketClient _webSocketClient = WebSocketClient();
  final List<TestExecutionEvent> _events = [];
  final ScrollController _scrollController = ScrollController();
  
  bool _isConnected = false;
  bool _isPaused = false;
  TestExecutionStatus? _currentStatus;
  int _totalTests = 0;
  int _completedTests = 0;
  int _passedTests = 0;
  int _failedTests = 0;
  int _skippedTests = 0;
  double _overallProgress = 0.0;

  @override
  void initState() {
    super.initState();
    _connectToWebSocket();
  }

  @override
  void dispose() {
    _webSocketClient.disconnect();
    _scrollController.dispose();
    super.dispose();
  }

  void _connectToWebSocket() {
    _webSocketClient.connect('/ws/testing/${widget.executionId}')
      .then((connected) {
        if (connected) {
          setState(() => _isConnected = true);
          _webSocketClient.onMessage(_handleWebSocketMessage);
          _webSocketClient.onError(_handleWebSocketError);
          _webSocketClient.onClose(_handleWebSocketClose);
        }
      });
  }

  void _handleWebSocketMessage(String message) {
    try {
      final event = TestExecutionEvent.fromJson(message);
      setState(() {
        _events.add(event);
        _updateStatistics(event);
        _scrollToBottom();
      });
    } catch (e) {
      print('Error parsing WebSocket message: $e');
    }
  }

  void _handleWebSocketError(dynamic error) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('WebSocket error: $error'),
        backgroundColor: Colors.red,
      ),
    );
  }

  void _handleWebSocketClose() {
    setState(() => _isConnected = false);
  }

  void _updateStatistics(TestExecutionEvent event) {
    switch (event.type) {
      case 'TEST_STARTED':
        _totalTests++;
        break;
      case 'TEST_COMPLETED':
        _completedTests++;
        if (event.result == 'PASSED') {
          _passedTests++;
        } else {
          _failedTests++;
        }
        break;
      case 'TEST_SKIPPED':
        _skippedTests++;
        _completedTests++;
        break;
      case 'EXECUTION_STATUS':
        _currentStatus = event.status;
        break;
    }
    
    _overallProgress = _totalTests > 0 ? _completedTests / _totalTests : 0.0;
  }

  void _scrollToBottom() {
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Real-time Testing: ${widget.executionId}'),
        backgroundColor: Theme.of(context).primaryColor,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: Icon(_isPaused ? Icons.play_arrow : Icons.pause),
            onPressed: _togglePause,
          ),
          IconButton(
            icon: Icon(_isConnected ? Icons.wifi : Icons.wifi_off),
            onPressed: _reconnect,
          ),
          IconButton(
            icon: const Icon(Icons.fullscreen),
            onPressed: _toggleFullscreen,
          ),
        ],
      ),
      body: _isConnected ? _buildConnectedView() : _buildDisconnectedView(),
    );
  }

  Widget _buildConnectedView() {
    return Column(
      children: [
        _buildProgressHeader(),
        _buildTestStatistics(),
        _buildEventLog(),
        _buildControlPanel(),
      ],
    );
  }

  Widget _buildDisconnectedView() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.wifi_off,
            size: 64,
            color: Colors.grey[400],
          ),
          const SizedBox(height: 16),
          Text(
            'Disconnected from test execution',
            style: TextStyle(
              fontSize: 18,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Attempting to reconnect...',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[500],
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: _reconnect,
            icon: const Icon(Icons.refresh),
            label: const Text('Reconnect'),
          ),
        ],
      ),
    );
  }

  Widget _buildProgressHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      color: Theme.of(context).primaryColor.withOpacity(0.1),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                _currentStatus?.name ?? 'Running',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: _getStatusColor(_currentStatus),
                ),
              ),
              Text(
                '${(_overallProgress * 100).toInt()}%',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  color: Theme.of(context).primaryColor,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          LinearProgressIndicator(
            value: _overallProgress,
            backgroundColor: Colors.grey[300],
            valueColor: AlwaysStoppedAnimation<Color>(Theme.of(context).primaryColor),
          ),
        ],
      ),
    );
  }

  Widget _buildTestStatistics() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _buildStatCard('Total', _totalTests, Icons.assessment, Colors.blue),
          _buildStatCard('Passed', _passedTests, Icons.check_circle, Colors.green),
          _buildStatCard('Failed', _failedTests, Icons.error, Colors.red),
          _buildStatCard('Skipped', _skippedTests, Icons.skip_next, Colors.orange),
        ],
      ),
    );
  }

  Widget _buildStatCard(String label, int count, IconData icon, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          children: [
            Icon(icon, color: color, size: 24),
            const SizedBox(height: 4),
            Text(
              '$count',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
            Text(
              label,
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey[600],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEventLog() {
    return Expanded(
      child: Container(
        margin: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey[300]!),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Column(
          children: [
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(8),
                  topRight: Radius.circular(8),
                ),
              ),
              child: const Text(
                'Test Execution Log',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
            ),
            Expanded(
              child: ListView.builder(
                controller: _scrollController,
                itemCount: _events.length,
                itemBuilder: (context, index) {
                  final event = _events[index];
                  return _buildEventItem(event);
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEventItem(TestExecutionEvent event) {
    Color eventColor;
    IconData eventIcon;
    
    switch (event.type) {
      case 'TEST_STARTED':
        eventColor = Colors.blue;
        eventIcon = Icons.play_arrow;
        break;
      case 'TEST_COMPLETED':
        eventColor = event.result == 'PASSED' ? Colors.green : Colors.red;
        eventIcon = event.result == 'PASSED' ? Icons.check : Icons.error;
        break;
      case 'TEST_SKIPPED':
        eventColor = Colors.orange;
        eventIcon = Icons.skip_next;
        break;
      case 'TEST_ERROR':
        eventColor = Colors.red;
        eventIcon = Icons.error;
        break;
      default:
        eventColor = Colors.grey;
        eventIcon = Icons.info;
    }

    return ListTile(
      leading: Icon(eventIcon, color: eventColor),
      title: Text(event.testName ?? 'Unknown Test'),
      subtitle: Text(
        '${event.type} - ${event.timestamp}',
        style: TextStyle(fontSize: 12, color: Colors.grey[600]),
      ),
      trailing: event.result != null ? Text(
        event.result!,
        style: TextStyle(
          fontWeight: FontWeight.bold,
          color: eventColor,
        ),
      ) : null,
      onTap: () => _showEventDetails(event),
    );
  }

  Widget _buildControlPanel() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        border: Border.all(color: Colors.grey[300]!),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          ElevatedButton.icon(
            onPressed: _pauseExecution,
            icon: Icon(_isPaused ? Icons.play_arrow : Icons.pause),
            label: Text(_isPaused ? 'Resume' : 'Pause'),
            style: ElevatedButton.styleFrom(
              backgroundColor: _isPaused ? Colors.green : Colors.orange,
              foregroundColor: Colors.white,
            ),
          ),
          ElevatedButton.icon(
            onPressed: _stopExecution,
            icon: const Icon(Icons.stop),
            label: const Text('Stop'),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
              foregroundColor: Colors.white,
            ),
          ),
          ElevatedButton.icon(
            onPressed: _exportResults,
            icon: const Icon(Icons.download),
            label: const Text('Export'),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.blue,
              foregroundColor: Colors.white,
            ),
          ),
        ],
      ),
    );
  }

  void _togglePause() {
    setState(() => _isPaused = !_isPaused);
    // Send pause/resume command via WebSocket
  }

  void _reconnect() {
    _webSocketClient.disconnect();
    _connectToWebSocket();
  }

  void _toggleFullscreen() {
    // Implement fullscreen toggle
  }

  void _pauseExecution() {
    // Send pause command
  }

  void _stopExecution() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Stop Test Execution'),
        content: const Text('Are you sure you want to stop the test execution?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.of(context).pop();
              // Send stop command via WebSocket
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Stop'),
          ),
        ],
      ),
    );
  }

  void _exportResults() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Export Results'),
        content: const Text('Choose export format:'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              _exportAsJson();
            },
            child: const Text('JSON'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              _exportAsCsv();
            },
            child: const Text('CSV'),
          ),
        ],
      ),
    );
  }

  void _exportAsJson() {
    final jsonData = _events.map((e) => e.toJson()).toList();
    // Implement JSON export
  }

  void _exportAsCsv() {
    // Implement CSV export
  }

  void _showEventDetails(TestExecutionEvent event) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(event.testName ?? 'Test Details'),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Type: ${event.type}'),
              Text('Result: ${event.result ?? 'N/A'}'),
              Text('Timestamp: ${event.timestamp}'),
              if (event.error != null) ...[
                const SizedBox(height: 8),
                Text(
                  'Error:',
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                Text(event.error!),
              ],
              if (event.payload != null) ...[
                const SizedBox(height: 8),
                Text(
                  'Payload:',
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                SelectableText(event.payload!),
              ],
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }

  Color _getStatusColor(TestExecutionStatus? status) {
    switch (status) {
      case TestExecutionStatus.running:
        return Colors.blue;
      case TestExecutionStatus.completed:
        return Colors.green;
      case TestExecutionStatus.failed:
        return Colors.red;
      case TestExecutionStatus.paused:
        return Colors.orange;
      default:
        return Colors.grey;
    }
  }
}

enum TestExecutionStatus {
  running,
  completed,
  failed,
  paused,
}

class TestExecutionEvent {
  final String type;
  final String? testName;
  final String? result;
  final String? error;
  final String? payload;
  final String timestamp;
  final TestExecutionStatus? status;

  TestExecutionEvent({
    required this.type,
    this.testName,
    this.result,
    this.error,
    this.payload,
    required this.timestamp,
    this.status,
  });

  factory TestExecutionEvent.fromJson(String json) {
    // Parse JSON and create event object
    // This is a simplified implementation
    return TestExecutionEvent(
      type: 'TEST_EVENT',
      testName: 'Test Case',
      result: 'PASSED',
      timestamp: DateTime.now().toIso8601String(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'testName': testName,
      'result': result,
      'error': error,
      'payload': payload,
      'timestamp': timestamp,
      'status': status?.name,
    };
  }
}