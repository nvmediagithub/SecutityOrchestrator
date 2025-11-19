import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../bpmn/presentation/widgets/bpmn_upload_section.dart';
import '../di/analysis_processes_providers.dart';
import '../domain/analysis_process.dart';

class ProcessDetailPage extends ConsumerWidget {
  final String processId;

  const ProcessDetailPage({super.key, required this.processId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final processAsync = ref.watch(processDetailProvider(processId));

    return Scaffold(
      appBar: AppBar(title: const Text('Process Details')),
      body: processAsync.when(
        data: (process) => _ProcessDetailView(process: process),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, _) => Center(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.error, color: Colors.red, size: 48),
                const SizedBox(height: 16),
                Text(
                  'Failed to load process: $error',
                  textAlign: TextAlign.center,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _ProcessDetailView extends ConsumerWidget {
  final AnalysisProcess process;

  const _ProcessDetailView({required this.process});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final textTheme = Theme.of(context).textTheme;

    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(process.name, style: textTheme.headlineMedium),
          const SizedBox(height: 8),
          Text(
            '${process.type.displayName} • ${process.status.displayName}',
            style: textTheme.titleMedium?.copyWith(color: Colors.grey),
          ),
          const SizedBox(height: 24),
          _DetailSection(
            title: 'Description',
            child: Text(process.description),
          ),
          const SizedBox(height: 16),
          _DetailSection(
            title: 'Created',
            child: Text(process.createdAt.toLocal().toString()),
          ),
          const SizedBox(height: 16),
          BpmnUploadSection(suggestedName: process.name),
          const SizedBox(height: 16),
          if (process.id != null)
            FilledButton.icon(
              icon: const Icon(Icons.delete_outline),
              style: FilledButton.styleFrom(backgroundColor: Colors.red),
              onPressed: () => _confirmDelete(context, ref, process),
              label: const Text('Delete Process'),
            ),
        ],
      ),
    );
  }

  Future<void> _confirmDelete(
    BuildContext context,
    WidgetRef ref,
    AnalysisProcess process,
  ) async {
    final service = ref.read(analysisProcessServiceProvider);
    final navigator = Navigator.of(context);
    final messenger = ScaffoldMessenger.of(context);
    final shouldDelete = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Process'),
        content: Text('Delete "${process.name}"?'),
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

    if (shouldDelete == true && process.id != null) {
      try {
        await service.deleteProcess(process.id!);
        messenger.showSnackBar(
          SnackBar(content: Text('Process "${process.name}" deleted')),
        );
        navigator.pop(true);
      } catch (error) {
        messenger.showSnackBar(
          SnackBar(content: Text('Failed to delete process: $error')),
        );
      }
    }
  }
}

class _DetailSection extends StatelessWidget {
  final String title;
  final Widget child;

  const _DetailSection({required this.title, required this.child});

  @override
  Widget build(BuildContext context) {
    final textTheme = Theme.of(context).textTheme;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(title, style: textTheme.titleMedium),
        const SizedBox(height: 8),
        child,
      ],
    );
  }
}
