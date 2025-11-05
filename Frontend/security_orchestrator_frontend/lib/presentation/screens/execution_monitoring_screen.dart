import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/workflow_dto.dart';
import '../../core/network/websocket_client.dart';

class ExecutionMonitoringScreen extends ConsumerStatefulWidget {
  final String workflowId;
  final String executionId;

  const ExecutionMonitoringScreen({
    super.key,
    required this.workflowId,
    required this.executionId,
  });

  @override
  ConsumerState<ExecutionMonitoringScreen> createState() => _ExecutionMonitoringScreenState();
}

class _ExecutionMonitoringScreenState extends ConsumerState<ExecutionMonitoringScreen> {
  ExecutionStatusDto? _executionStatus;
  bool _isConnected = false;

  @override
  void initState() {
    super.initState();
    _initializeExecutionStatus();
    _connectToWebSocket();
  }

  @override
  void dispose() {
    _disconnectWebSocket();
    super.dispose();
  }

  void _initializeExecutionStatus() {
    // Mock initial execution status
    _executionStatus = ExecutionStatusDto(
      workflowId: widget.workflowId,
      executionId: widget.executionId,
      state: ExecutionState.running,
      progress: 0.4,
      startedAt: DateTime.now().subtract(const Duration(minutes: 2)),
      steps: [
        StepExecutionDto(
          stepId: 'step1',
          stepName: 'Input Validation',
          state: ExecutionState.completed,
          startedAt: DateTime.now().subtract(const Duration(minutes: 2)),
          completedAt: DateTime.now().subtract(const Duration(minutes: 1, seconds: 45)),
          result: 'PASSED - All inputs validated successfully',
        ),
        StepExecutionDto(
          stepId: 'step2',
          stepName: 'Authentication Check',
          state: ExecutionState.running,
          startedAt: DateTime.now().subtract(const Duration(minutes: 1, seconds: 45)),
        ),
        StepExecutionDto(
          stepId: 'step3',
          stepName: 'Authorization Check',
          state: ExecutionState.pending,
          startedAt: DateTime.now(), // Will start soon
        ),
        StepExecutionDto(
          stepId: 'step4',
          stepName: 'Data Processing',
          state: ExecutionState.pending,
          startedAt: DateTime.now(),
        ),
        StepExecutionDto(
          stepId: 'step5',
          stepName: 'Output Validation',
          state: ExecutionState.pending,
          startedAt: DateTime.now(),
        ),
      ],
    );
  }

  void _connectToWebSocket() {
    final webSocketClient = ref.read(webSocketClientProvider);

    webSocketClient.connect();

    webSocketClient.onConnected = () {
      if (mounted) {
        setState(() {
          _isConnected = true;
        });
      }
    };

    webSocketClient.onDisconnected = () {
      if (mounted) {
        setState(() {
          _isConnected = false;
        });
      }
    };

    webSocketClient.onMessage = (message) {
      if (mounted && message['executionId'] == widget.executionId) {
        _handleExecutionUpdate(message);
      }
    };

    webSocketClient.onError = (error) {
      print('WebSocket error: $error');
    };
  }

  void _disconnectWebSocket() {
    final webSocketClient = ref.read(webSocketClientProvider);
    webSocketClient.disconnect();
  }

  void _handleExecutionUpdate(Map<String, dynamic> message) {
    // Simulate real-time updates
    if (message['type'] == 'EXECUTION_UPDATE') {
      setState(() {
        _executionStatus = ExecutionStatusDto.fromJson(message['data']);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_executionStatus == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Execution Monitoring'),
        actions: [
          Container(
            margin: const EdgeInsets.only(right: 16),
            child: Row(
              children: [
                Icon(
                  _isConnected ? Icons.wifi : Icons.wifi_off,
                  color: _isConnected ? Colors.green : Colors.red,
                  size: 20,
                ),
                const SizedBox(width: 4),
                Text(
                  _isConnected ? 'Live' : 'Offline',
                  style: TextStyle(
                    color: _isConnected ? Colors.green : Colors.red,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildExecutionHeader(),
            const SizedBox(height: 24),
            _buildProgressSection(),
            const SizedBox(height: 24),
            _buildStepsList(),
            const SizedBox(height: 24),
            _buildActions(),
          ],
        ),
      ),
    );
  }

  Widget _buildExecutionHeader() {
    final execution = _executionStatus!;
    final duration = DateTime.now().difference(execution.startedAt);

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  backgroundColor: _getExecutionStateColor(execution.state),
                  child: Icon(
                    _getExecutionStateIcon(execution.state),
                    color: Colors.white,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Workflow ${execution.workflowId}',
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        'Execution: ${execution.executionId}',
                        style: TextStyle(color: Colors.grey[600]),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                _buildInfoChip('Status', execution.state.name.toUpperCase()),
                const SizedBox(width: 8),
                _buildInfoChip('Progress', '${(execution.progress * 100).round()}%'),
                const SizedBox(width: 8),
                _buildInfoChip('Duration', _formatDuration(duration)),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              'Started: ${_formatDateTime(execution.startedAt)}',
              style: TextStyle(color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProgressSection() {
    final execution = _executionStatus!;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Overall Progress',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            LinearProgressIndicator(
              value: execution.progress,
              backgroundColor: Colors.grey[300],
              valueColor: AlwaysStoppedAnimation<Color>(
                _getExecutionStateColor(execution.state),
              ),
            ),
            const SizedBox(height: 8),
            Text(
              '${execution.steps.where((step) => step.state == ExecutionState.completed).length} of ${execution.steps.length} steps completed',
              style: TextStyle(color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStepsList() {
    final steps = _executionStatus!.steps;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Execution Steps',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: steps.length,
              itemBuilder: (context, index) {
                final step = steps[index];
                return _buildStepItem(step, index + 1);
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStepItem(StepExecutionDto step, int stepNumber) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey[300]!),
        borderRadius: BorderRadius.circular(8),
        color: step.state == ExecutionState.running ? Colors.blue[50] : null,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 16,
                backgroundColor: _getExecutionStateColor(step.state),
                child: step.state == ExecutionState.running
                    ? const SizedBox(
                        width: 16,
                        height: 16,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                        ),
                      )
                    : Icon(
                        _getExecutionStateIcon(step.state),
                        color: Colors.white,
                        size: 16,
                      ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '$stepNumber. ${step.stepName}',
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Text(
                      'Step ID: ${step.stepId}',
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),
              Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    step.state.name.toUpperCase(),
                    style: TextStyle(
                      color: _getExecutionStateColor(step.state),
                      fontWeight: FontWeight.bold,
                      fontSize: 12,
                    ),
                  ),
                  if (step.startedAt != null && step.state != ExecutionState.pending)
                    Text(
                      _formatDuration(DateTime.now().difference(step.startedAt!)),
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontSize: 10,
                      ),
                    ),
                ],
              ),
            ],
          ),
          if (step.result != null && step.state == ExecutionState.completed) ...[
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.green[50],
                border: Border.all(color: Colors.green[200]!),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                step.result!,
                style: const TextStyle(
                  color: Colors.green[800],
                  fontFamily: 'monospace',
                  fontSize: 12,
                ),
              ),
            ),
          ],
          if (step.error != null) ...[
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.red[50],
                border: Border.all(color: Colors.red[200]!),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                'Error: ${step.error}',
                style: const TextStyle(
                  color: Colors.red[800],
                  fontFamily: 'monospace',
                  fontSize: 12,
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildActions() {
    final execution = _executionStatus!;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Actions',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                if (execution.state == ExecutionState.running) ...[
                  Expanded(
                    child: ElevatedButton.icon(
                      icon: const Icon(Icons.stop),
                      label: const Text('Stop Execution'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.red,
                      ),
                      onPressed: _stopExecution,
                    ),
                  ),
                  const SizedBox(width: 16),
                ],
                Expanded(
                  child: OutlinedButton.icon(
                    icon: const Icon(Icons.visibility),
                    label: const Text('View Results'),
                    onPressed: execution.state == ExecutionState.completed ? _viewResults : null,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  void _stopExecution() {
    // Implement stop execution logic
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Stopping execution...')),
    );
  }

  void _viewResults() {
    // Navigate to results screen
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => ResultsScreen(executionId: widget.executionId),
      ),
    );
  }

  Widget _buildInfoChip(String label, String value) {
    return Chip(
      label: Text('$label: $value'),
      backgroundColor: Colors.grey[100],
    );
  }

  Color _getExecutionStateColor(ExecutionState state) {
    switch (state) {
      case ExecutionState.pending:
        return Colors.grey;
      case ExecutionState.running:
        return Colors.blue;
      case ExecutionState.completed:
        return Colors.green;
      case ExecutionState.failed:
        return Colors.red;
      case ExecutionState.cancelled:
        return Colors.orange;
    }
  }

  IconData _getExecutionStateIcon(ExecutionState state) {
    switch (state) {
      case ExecutionState.pending:
        return Icons.schedule;
      case ExecutionState.running:
        return Icons.play_arrow;
      case ExecutionState.completed:
        return Icons.check_circle;
      case ExecutionState.failed:
        return Icons.error;
      case ExecutionState.cancelled:
        return Icons.cancel;
    }
  }

  String _formatDuration(Duration duration) {
    if (duration.inMinutes > 0) {
      return '${duration.inMinutes}m ${duration.inSeconds % 60}s';
    } else {
      return '${duration.inSeconds}s';
    }
  }

  String _formatDateTime(DateTime dateTime) {
    return '${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')} ${dateTime.day}/${dateTime.month}/${dateTime.year}';
  }
}