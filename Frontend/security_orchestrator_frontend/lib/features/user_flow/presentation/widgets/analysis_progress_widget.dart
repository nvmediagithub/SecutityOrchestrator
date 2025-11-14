import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../providers/user_flow_provider.dart';

class AnalysisProgressWidget extends ConsumerWidget {
  const AnalysisProgressWidget({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userFlowState = ref.watch(userFlowProvider);

    if (userFlowState.analysisStatus == null) {
      return const SizedBox.shrink();
    }

    final status = userFlowState.analysisStatus!;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  _getStatusIcon(status.status),
                  color: _getStatusColor(status.status),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Analysis in Progress',
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      if (status.currentStep != null)
                        Text(
                          status.currentStep!,
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: Colors.grey[600],
                          ),
                        ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            LinearProgressIndicator(
              value: status.progress / 100,
              backgroundColor: Colors.grey[200],
              valueColor: AlwaysStoppedAnimation<Color>(
                _getStatusColor(status.status),
              ),
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '${status.progress.toStringAsFixed(0)}%',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: _getStatusColor(status.status),
                    fontWeight: FontWeight.w500,
                  ),
                ),
                Text(
                  _getTimeRemaining(status),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Colors.grey[600],
                  ),
                ),
              ],
            ),
            if (status.error != null) ...[
              const SizedBox(height: 12),
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.red.shade50,
                  borderRadius: BorderRadius.circular(4),
                  border: Border.all(color: Colors.red.shade200),
                ),
                child: Row(
                  children: [
                    Icon(
                      Icons.error_outline,
                      color: Colors.red.shade700,
                      size: 16,
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        status.error!,
                        style: TextStyle(
                          color: Colors.red.shade700,
                          fontSize: 12,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  IconData _getStatusIcon(String status) {
    switch (status.toLowerCase()) {
      case 'pending':
        return Icons.schedule;
      case 'processing':
        return Icons.sync;
      case 'completed':
        return Icons.check_circle;
      case 'failed':
        return Icons.error;
      default:
        return Icons.help_outline;
    }
  }

  Color _getStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'pending':
        return Colors.orange;
      case 'processing':
        return Colors.blue;
      case 'completed':
        return Colors.green;
      case 'failed':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  String _getTimeRemaining(AnalysisStatus status) {
    // Simple estimation based on progress
    if (status.progress <= 0) return 'Estimating...';

    final remainingProgress = 100 - status.progress;
    final estimatedSeconds = (remainingProgress / status.progress) * (DateTime.now().difference(status.lastUpdated).inSeconds);

    if (estimatedSeconds < 60) {
      return '${estimatedSeconds.toStringAsFixed(0)}s remaining';
    } else if (estimatedSeconds < 3600) {
      return '${(estimatedSeconds / 60).toStringAsFixed(0)}m remaining';
    } else {
      return '${(estimatedSeconds / 3600).toStringAsFixed(1)}h remaining';
    }
  }
}