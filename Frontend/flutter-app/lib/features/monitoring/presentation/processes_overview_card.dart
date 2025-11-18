import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../analysis-processes/di/analysis_processes_providers.dart';
import '../../analysis-processes/domain/analysis_process.dart';

final processesProvider = FutureProvider<List<AnalysisProcess>>((ref) async {
  final service = ref.watch(analysisProcessServiceProvider);
  return service.getProcesses();
});

class ProcessesOverviewCard extends ConsumerWidget {
  const ProcessesOverviewCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final processesAsync = ref.watch(processesProvider);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Analysis Processes',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                TextButton.icon(
                  onPressed: () => context.push('/processes'),
                  icon: const Icon(Icons.arrow_forward),
                  label: const Text('View All'),
                ),
              ],
            ),
            const SizedBox(height: 16),
            processesAsync.when(
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, _) => Text('Error loading processes: $error'),
              data: (processes) {
                final activeProcesses = processes
                    .where((p) => p.status.isActive)
                    .toList();
                final recentProcesses = processes
                    .where((p) => p.status.isFinished)
                    .take(3)
                    .toList();

                return Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    if (activeProcesses.isNotEmpty) ...[
                      const Text(
                        'Active Processes',
                        style: TextStyle(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      ...activeProcesses.map(
                        (process) =>
                            _ProcessListTile(process: process, ref: ref),
                      ),
                    ] else
                      const Text(
                        'No active processes',
                        style: TextStyle(color: Colors.grey),
                      ),
                    const SizedBox(height: 16),
                    if (recentProcesses.isNotEmpty) ...[
                      const Text(
                        'Recent Activity',
                        style: TextStyle(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      ...recentProcesses.map(
                        (process) =>
                            _ProcessListTile(process: process, ref: ref),
                      ),
                    ],
                  ],
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}

class _ProcessListTile extends StatelessWidget {
  final AnalysisProcess process;
  final WidgetRef ref;

  const _ProcessListTile({required this.process, required this.ref});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey.shade300),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Icon(
            _statusIcon(process.status),
            color: _statusColor(process.status),
            size: 20,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  process.name,
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
                Text(
                  '${process.type.displayName} - ${process.status.displayName}',
                  style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                ),
              ],
            ),
          ),
          if (process.status.isFinished && process.id != null)
            IconButton(
              icon: const Icon(Icons.delete, size: 20),
              onPressed: () async {
                final confirmed = await _showDeleteConfirmationDialog(
                  context,
                  process,
                );
                if (confirmed == true) {
                  await _deleteProcess(context, process);
                }
              },
            ),
        ],
      ),
    );
  }

  IconData _statusIcon(ProcessStatus status) {
    switch (status) {
      case ProcessStatus.running:
        return Icons.play_arrow;
      case ProcessStatus.completed:
        return Icons.check_circle;
      case ProcessStatus.failed:
        return Icons.error;
      case ProcessStatus.pending:
        return Icons.schedule;
    }
  }

  Color _statusColor(ProcessStatus status) {
    switch (status) {
      case ProcessStatus.running:
        return Colors.blue;
      case ProcessStatus.completed:
        return Colors.green;
      case ProcessStatus.failed:
        return Colors.red;
      case ProcessStatus.pending:
        return Colors.orange;
    }
  }

  Future<void> _deleteProcess(
    BuildContext context,
    AnalysisProcess process,
  ) async {
    final messenger = ScaffoldMessenger.of(context);
    final service = ref.read(analysisProcessServiceProvider);
    try {
      await service.deleteProcess(process.id!);
      ref.invalidate(processesProvider);
      messenger.showSnackBar(
        SnackBar(content: Text('Process "${process.name}" deleted')),
      );
    } catch (error) {
      messenger.showSnackBar(
        SnackBar(content: Text('Failed to delete process: $error')),
      );
    }
  }

  Future<bool?> _showDeleteConfirmationDialog(
    BuildContext context,
    AnalysisProcess process,
  ) {
    return showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Process'),
        content: Text('Are you sure you want to delete "${process.name}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
  }
}
