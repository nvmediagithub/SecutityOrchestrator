import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../analysis-processes/data/analysis_process_service.dart';
import '../../analysis-processes/domain/analysis_process.dart';

// Provider for processes - assuming AnalysisProcessService is properly implemented
final processesProvider = FutureProvider<List<AnalysisProcess>>((ref) async {
  final service = ref.read(analysisProcessServiceProvider);
  return service.getProcesses();
});

// Placeholder for analysisProcessServiceProvider - should be defined in analysis-processes feature
final analysisProcessServiceProvider = Provider<AnalysisProcessService>((ref) {
  throw UnimplementedError('AnalysisProcessService provider not implemented');
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
                  onPressed: () {
                    // TODO: Navigate to full processes page
                  },
                  icon: const Icon(Icons.arrow_forward),
                  label: const Text('View All'),
                ),
              ],
            ),
            const SizedBox(height: 16),
            processesAsync.when(
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, stack) => Center(
                child: Text('Error loading processes: $error'),
              ),
              data: (processes) {
                final activeProcesses = processes.where((p) => p.status.isActive).toList();
                final recentProcesses = processes
                    .where((p) => p.status.isFinished)
                    .take(3)
                    .toList();

                return Column(
                  children: [
                    // Active Processes Section
                    if (activeProcesses.isNotEmpty) ...[
                      const Text(
                        'Active Processes',
                        style: TextStyle(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      ...activeProcesses.map((process) => _buildProcessItem(process, context)),
                    ] else ...[
                      const Text(
                        'No active processes',
                        style: TextStyle(color: Colors.grey),
                      ),
                    ],
                    const SizedBox(height: 16),

                    // Recent Activity Section
                    if (recentProcesses.isNotEmpty) ...[
                      const Text(
                        'Recent Activity',
                        style: TextStyle(fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      ...recentProcesses.map((process) => _buildProcessItem(process, context)),
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

  Widget _buildProcessItem(AnalysisProcess process, BuildContext context) {
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
            process.status == ProcessStatus.running
                ? Icons.play_arrow
                : process.status == ProcessStatus.completed
                    ? Icons.check_circle
                    : Icons.error,
            color: process.status == ProcessStatus.running
                ? Colors.blue
                : process.status == ProcessStatus.completed
                    ? Colors.green
                    : Colors.red,
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
                  '${process.type.displayName} • ${process.status.displayName}',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey.shade600,
                  ),
                ),
              ],
            ),
          ),
          if (process.status.isFinished)
            IconButton(
              icon: const Icon(Icons.delete, size: 20),
              onPressed: () async {
                final confirmed = await _showDeleteConfirmationDialog(context, process);
                if (confirmed == true) {
                  // TODO: Implement delete functionality
                }
              },
            ),
        ],
      ),
    );
  }

  Future<bool?> _showDeleteConfirmationDialog(BuildContext context, AnalysisProcess process) {
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