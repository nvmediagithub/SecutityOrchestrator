import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../bpmn/presentation/widgets/bpmn_upload_section.dart';
import '../../openapi/presentation/widgets/openapi_upload_section.dart';
import '../di/analysis_processes_providers.dart';
import '../domain/analysis_process.dart';
import 'widgets/analysis_session_section.dart';
import 'widgets/process_overview_header.dart';

class ProcessDetailPage extends ConsumerWidget {
  final String processId;

  const ProcessDetailPage({super.key, required this.processId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final processAsync = ref.watch(processDetailProvider(processId));

    return Scaffold(
      appBar: AppBar(title: const Text('Детали процесса')),
      body: processAsync.when(
        data: (process) => ProcessDetailBody(process: process),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, _) => _ProcessError(message: '$error'),
      ),
    );
  }
}

class ProcessDetailBody extends ConsumerWidget {
  final AnalysisProcess process;

  const ProcessDetailBody({super.key, required this.process});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ProcessOverviewHeader(process: process),
          const SizedBox(height: 16),
          _buildUploadsSection(),
          const SizedBox(height: 16),
          if (process.id != null)
            AnalysisSessionSection(
              processId: process.id!,
              processName: process.name,
              canStart: process.hasBpmn && process.hasOpenApi,
            ),
        ],
      ),
    );
  }

  Widget _buildUploadsSection() {
    return Column(
      children: [
        if (process.id != null)
          BpmnUploadSection(
            processId: process.id,
            suggestedName: process.name,
          )
        else
          BpmnUploadSection(suggestedName: process.name),
        const SizedBox(height: 16),
        if (process.id != null)
          OpenApiUploadSection(
            processId: process.id,
            suggestedName: process.name,
          )
        else
          OpenApiUploadSection(suggestedName: process.name),
        const SizedBox(height: 16),
        if (process.id != null) _DeleteProcessButton(process: process),
      ],
    );
  }
}

class _ProcessError extends StatelessWidget {
  final String message;

  const _ProcessError({required this.message});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error, color: Colors.red, size: 48),
            const SizedBox(height: 16),
            Text(
              'Не удалось загрузить процесс: $message',
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}

class _DeleteProcessButton extends ConsumerWidget {
  final AnalysisProcess process;

  const _DeleteProcessButton({required this.process});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return FilledButton.icon(
      icon: const Icon(Icons.delete_outline),
      style: FilledButton.styleFrom(backgroundColor: Colors.red),
      onPressed: () => _confirmDelete(context, ref),
      label: const Text('Удалить процесс'),
    );
  }

  Future<void> _confirmDelete(BuildContext context, WidgetRef ref) async {
    final service = ref.read(analysisProcessServiceProvider);
    final navigator = Navigator.of(context);
    final messenger = ScaffoldMessenger.of(context);
    final shouldDelete = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Удалить процесс'),
        content: Text('Удалить "${process.name}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Отмена'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Удалить'),
          ),
        ],
      ),
    );

    if (shouldDelete != true) {
      return;
    }

    try {
      await service.deleteProcess(process.id!);
      if (!context.mounted) return;
      navigator.pop(true);
    } catch (error) {
      if (!context.mounted) return;
      messenger.showSnackBar(
        SnackBar(content: Text('Не удалось удалить процесс: $error')),
      );
    }
  }
}
