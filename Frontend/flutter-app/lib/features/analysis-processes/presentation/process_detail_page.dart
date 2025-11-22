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
      appBar: AppBar(
        title: const Text('Детали процесса'),
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black87,
      ),
      backgroundColor: Colors.grey[100],
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
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _ProcessHero(process: process),
          const SizedBox(height: 24),
          _buildUploadsSection(),
          const SizedBox(height: 24),
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
    return LayoutBuilder(
      builder: (context, constraints) {
        final isWide = constraints.maxWidth > 900;
        final children = [
          Expanded(
            flex: 1,
            child: _UploadCard(
              title: 'BPMN диаграмма',
              child: process.id != null
                  ? BpmnUploadSection(processId: process.id, suggestedName: process.name)
                  : BpmnUploadSection(suggestedName: process.name),
            ),
          ),
          const SizedBox(width: 16, height: 16),
          Expanded(
            flex: 1,
            child: _UploadCard(
              title: 'OpenAPI спецификация',
              child: process.id != null
                  ? OpenApiUploadSection(processId: process.id, suggestedName: process.name)
                  : OpenApiUploadSection(suggestedName: process.name),
            ),
          ),
        ];
        return Column(
          children: [
            if (isWide)
              Row(crossAxisAlignment: CrossAxisAlignment.start, children: children)
            else
              Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  ...children.map((child) => SizedBox(height: 250, child: child)).toList(),
                ],
              ),
            const SizedBox(height: 16),
            if (process.id != null) _DeleteProcessButton(process: process),
          ],
        );
      },
    );
  }
}

class _ProcessHero extends StatelessWidget {
  final AnalysisProcess process;

  const _ProcessHero({required this.process});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
        gradient: const LinearGradient(
          colors: [Color(0xFFEDF3FF), Color(0xFFFFFFFF)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        boxShadow: [
          BoxShadow(
            color: Colors.blueGrey.withOpacity(0.1),
            blurRadius: 20,
            offset: const Offset(0, 10),
          ),
        ],
        border: Border.all(color: Colors.blue.withOpacity(0.2)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            process.name,
            style: theme.textTheme.headlineSmall?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 4),
          Text(
            process.description,
            style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey[700]),
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Chip(
                label: Text(process.status.displayName),
                backgroundColor: Colors.blue.withOpacity(0.15),
                avatar: Icon(Icons.timeline, color: Colors.blue),
              ),
              const SizedBox(width: 8),
              Chip(
                label: Text(process.type.displayName),
                backgroundColor: Colors.green.withOpacity(0.15),
                avatar: Icon(Icons.category, color: Colors.green),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _UploadCard extends StatelessWidget {
  final String title;
  final Widget child;

  const _UploadCard({required this.title, required this.child});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.grey.withOpacity(0.2)),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.08),
            blurRadius: 16,
            offset: const Offset(0, 8),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(title, style: Theme.of(context).textTheme.titleMedium),
              const Spacer(),
              Icon(Icons.cloud_upload_outlined, color: Colors.grey),
            ],
          ),
          const SizedBox(height: 12),
          ConstrainedBox(constraints: const BoxConstraints(minHeight: 220), child: child),
        ],
      ),
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
            Text('Не удалось загрузить процесс: $message', textAlign: TextAlign.center),
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
      messenger.showSnackBar(SnackBar(content: Text('Не удалось удалить процесс: $error')));
    }
  }
}
