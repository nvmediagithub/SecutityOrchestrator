import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/workflow_dto.dart';

class ResultsScreen extends ConsumerWidget {
  final String executionId;

  const ResultsScreen({super.key, required this.executionId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // Mock execution results
    final mockExecution = ExecutionStatusDto(
      workflowId: 'wf_123',
      executionId: executionId,
      state: ExecutionState.completed,
      progress: 1.0,
      startedAt: DateTime.now().subtract(const Duration(minutes: 15)),
      completedAt: DateTime.now().subtract(const Duration(minutes: 2)),
      steps: [
        StepExecutionDto(
          stepId: 'step1',
          stepName: 'Input Validation',
          state: ExecutionState.completed,
          startedAt: DateTime.now().subtract(const Duration(minutes: 14)),
          completedAt: DateTime.now().subtract(const Duration(minutes: 12)),
          result: 'PASSED - All inputs validated successfully',
        ),
        StepExecutionDto(
          stepId: 'step2',
          stepName: 'Authentication Check',
          state: ExecutionState.completed,
          startedAt: DateTime.now().subtract(const Duration(minutes: 12)),
          completedAt: DateTime.now().subtract(const Duration(minutes: 9)),
          result: 'PASSED - Authentication successful',
        ),
        StepExecutionDto(
          stepId: 'step3',
          stepName: 'Authorization Check',
          state: ExecutionState.completed,
          startedAt: DateTime.now().subtract(const Duration(minutes: 9)),
          completedAt: DateTime.now().subtract(const Duration(minutes: 5)),
          result: 'PASSED - User authorized for action',
        ),
        StepExecutionDto(
          stepId: 'step4',
          stepName: 'Data Processing',
          state: ExecutionState.completed,
          startedAt: DateTime.now().subtract(const Duration(minutes: 5)),
          completedAt: DateTime.now().subtract(const Duration(minutes: 2)),
          result: 'PASSED - Data processed successfully',
        ),
      ],
    );

    return Scaffold(
      appBar: AppBar(
        title: const Text('Execution Results'),
        actions: [
          IconButton(
            icon: const Icon(Icons.download),
            onPressed: () {
              // Export results
            },
          ),
          IconButton(
            icon: const Icon(Icons.share),
            onPressed: () {
              // Share results
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildExecutionSummary(mockExecution),
            const SizedBox(height: 24),
            _buildProgressSection(mockExecution),
            const SizedBox(height: 24),
            _buildStepResults(mockExecution.steps),
            const SizedBox(height: 24),
            _buildActions(context),
          ],
        ),
      ),
    );
  }

  Widget _buildExecutionSummary(ExecutionStatusDto execution) {
    final duration = execution.completedAt?.difference(execution.startedAt) ??
        DateTime.now().difference(execution.startedAt);

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
                        'Execution ${execution.executionId}',
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        'Workflow: ${execution.workflowId}',
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
            const SizedBox(height: 16),
            Row(
              children: [
                Text(
                  'Started: ${_formatDateTime(execution.startedAt)}',
                  style: TextStyle(color: Colors.grey[600]),
                ),
                if (execution.completedAt != null) ...[
                  const SizedBox(width: 16),
                  Text(
                    'Completed: ${_formatDateTime(execution.completedAt!)}',
                    style: TextStyle(color: Colors.grey[600]),
                  ),
                ],
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProgressSection(ExecutionStatusDto execution) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Progress Overview',
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

  Widget _buildStepResults(List<StepExecutionDto> steps) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Step Results',
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
                return _buildStepResultItem(step);
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStepResultItem(StepExecutionDto step) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(color: const Color(0xFFE0E0E0)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 16,
                backgroundColor: _getExecutionStateColor(step.state),
                child: Icon(
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
                      step.stepName,
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
                    ),
                  ),
                  Text(
                    _formatDuration(step.completedAt?.difference(step.startedAt) ??
                        DateTime.now().difference(step.startedAt)),
                    style: TextStyle(
                      color: Colors.grey[600],
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ],
          ),
          if (step.result != null) ...[
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                step.result!,
                style: const TextStyle(fontFamily: 'monospace'),
              ),
            ),
          ],
          if (step.error != null) ...[
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.red[50],
                border: Border.all(color: const Color(0xFFF5B7B7)),
                borderRadius: BorderRadius.circular(4),
              ),
              child: Text(
                'Error: ${step.error}',
                style: const TextStyle(
                  color: Colors.red,
                  fontFamily: 'monospace',
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildActions(BuildContext context) {
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
                Expanded(
                  child: ElevatedButton.icon(
                    icon: const Icon(Icons.replay),
                    label: const Text('Re-run Execution'),
                    onPressed: () {
                      // Re-run execution
                    },
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: OutlinedButton.icon(
                    icon: const Icon(Icons.visibility),
                    label: const Text('View Details'),
                    onPressed: () {
                      // View detailed logs
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
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