import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../data/analysis_process_service.dart';
import '../di/analysis_processes_providers.dart';
import '../domain/analysis_process.dart';
import '../domain/create_analysis_process_usecase.dart';
import 'create_process_dialog.dart';

class AnalysisProcessesPage extends ConsumerStatefulWidget {
  const AnalysisProcessesPage({super.key});

  @override
  ConsumerState<AnalysisProcessesPage> createState() =>
      _AnalysisProcessesPageState();
}

class _AnalysisProcessesPageState extends ConsumerState<AnalysisProcessesPage> {
  late Future<List<AnalysisProcess>> _processesFuture;

  @override
  void initState() {
    super.initState();
    _loadProcesses();
  }

  void _loadProcesses() {
    final service = ref.read(analysisProcessServiceProvider);
    _processesFuture = service.getProcesses();
  }

  Future<void> _deleteProcess(AnalysisProcess process) async {
    final id = process.id;
    if (id == null) return;
    final messenger = ScaffoldMessenger.of(context);
    try {
      await ref.read(analysisProcessServiceProvider).deleteProcess(id);
      if (!mounted) return;
      setState(_loadProcesses);
      messenger.showSnackBar(
        SnackBar(content: Text('Процесс "${process.name}" удалён')),
      );
    } catch (e) {
      if (!mounted) return;
      messenger.showSnackBar(
        SnackBar(content: Text('Не удалось удалить процесс: $e')),
      );
    }
  }

  Future<void> _confirmDeleteProcess(AnalysisProcess process) async {
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

    if (shouldDelete == true) {
      await _deleteProcess(process);
    }
  }

  Future<void> _showCreateProcessDialog() async {
    final createUseCase = ref.read(createAnalysisProcessUseCaseProvider);

    final result = await showDialog<AnalysisProcess>(
      context: context,
      builder: (context) =>
          CreateProcessDialog(createProcessUseCase: createUseCase),
    );

    if (result != null) {
      setState(_loadProcesses);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.white,
        foregroundColor: Colors.black87,
        title: const Text('Процессы анализа'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadProcesses,
            tooltip: 'Обновить',
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        child: Column(
          children: [
            _PageHeader(onCreate: _showCreateProcessDialog),
            const SizedBox(height: 16),
            Expanded(
              child: FutureBuilder<List<AnalysisProcess>>(
                future: _processesFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  } else if (snapshot.hasError) {
                    return _InfoPlaceholder(
                      title: 'Ошибка загрузки процессов',
                      message: '${snapshot.error}',
                      icon: Icons.error,
                      actionLabel: 'Повторить',
                      onAction: _loadProcesses,
                    );
                  } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                    return _InfoPlaceholder(
                      title: 'Аналитические процессы не найдены',
                      message: 'Создайте первый анализ, загрузив BPMN и OpenAPI.',
                      icon: Icons.list_alt,
                      actionLabel: 'Создать процесс',
                      onAction: _showCreateProcessDialog,
                    );
                  }
                  final processes = snapshot.data!;
                  return LayoutBuilder(
                    builder: (context, constraints) {
                      final crossAxis = constraints.maxWidth > 900 ? 2 : 1;
                      return GridView.builder(
                        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: crossAxis,
                          crossAxisSpacing: 16,
                          mainAxisSpacing: 16,
                          childAspectRatio: 3,
                        ),
                        itemCount: processes.length,
                        itemBuilder: (context, index) {
                          final process = processes[index];
                          return _ProcessTile(
                            process: process,
                            onDelete: () => _confirmDeleteProcess(process),
                            onTap: process.id == null
                                ? null
                                : () async {
                                    final deleted = await context.push<bool>(
                                      '/processes/${process.id}',
                                    );
                                    if (deleted == true) {
                                      if (!mounted) return;
                                      setState(_loadProcesses);
                                    }
                                  },
                          );
                        },
                      );
                    },
                  );
                },
              ),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showCreateProcessDialog,
        tooltip: 'Создать процесс',
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildArtifactChip({required String label, required bool available}) {
    return Chip(
      avatar: Icon(
        available ? Icons.check_circle : Icons.radio_button_unchecked,
        color: available ? Colors.green : Colors.grey,
        size: 18,
      ),
      backgroundColor: available
          ? Colors.green.withOpacity(0.1)
          : Colors.grey.withOpacity(0.1),
      label: Text('$label ${available ? '✓' : '✗'}'),
    );
  }
}

class _PageHeader extends StatelessWidget {
  final VoidCallback onCreate;

  const _PageHeader({required this.onCreate});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 24),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.grey.withOpacity(0.2)),
        boxShadow: [
          BoxShadow(
            blurRadius: 20,
            color: Colors.grey.withOpacity(0.12),
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: Row(
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Аналитические процессы',
                style: Theme.of(context).textTheme.titleLarge,
              ),
              const SizedBox(height: 4),
              Text(
                'Управляйте BPMN/ OpenAPI артефактами и сопровождайте анализ.',
                style:
                    Theme.of(context).textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
              ),
            ],
          ),
          const Spacer(),
          FilledButton.icon(
            icon: const Icon(Icons.add),
            label: const Text('Новый процесс'),
            onPressed: onCreate,
          ),
        ],
      ),
    );
  }
}

class _ProcessTile extends StatelessWidget {
  final AnalysisProcess process;
  final VoidCallback onDelete;
  final VoidCallback? onTap;

  const _ProcessTile({
    required this.process,
    required this.onDelete,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Material(
      borderRadius: BorderRadius.circular(20),
      elevation: 4,
      shadowColor: Colors.black.withOpacity(0.1),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(20),
        child: Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(20),
            color: Colors.white,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Text(
                      process.name,
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.delete_outline),
                    color: Colors.red.shade400,
                    tooltip: 'Удалить',
                    onPressed: onDelete,
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                '${process.type.displayName} · ${process.status.displayName}',
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              const SizedBox(height: 12),
              Wrap(
                spacing: 6,
                runSpacing: 4,
                children: [
                  _buildArtifactBadge(
                    label: 'BPMN',
                    available: process.hasBpmn,
                  ),
                  _buildArtifactBadge(
                    label: 'OpenAPI',
                    available: process.hasOpenApi,
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildArtifactBadge({required String label, required bool available}) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: available ? Colors.green.withOpacity(0.1) : Colors.grey.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        '$label ${available ? '✓' : '✗'}',
        style: TextStyle(
          color: available ? Colors.green[800] : Colors.grey[600],
          fontSize: 13,
        ),
      ),
    );
  }
}

class _InfoPlaceholder extends StatelessWidget {
  final String title;
  final String message;
  final IconData icon;
  final String actionLabel;
  final VoidCallback onAction;

  const _InfoPlaceholder({
    required this.title,
    required this.message,
    required this.icon,
    required this.actionLabel,
    required this.onAction,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Container(
        padding: const EdgeInsets.all(24),
        constraints: const BoxConstraints(maxWidth: 420),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(18),
          border: Border.all(color: Colors.grey.withOpacity(0.2)),
          boxShadow: [
            BoxShadow(
              blurRadius: 20,
              color: Colors.grey.withOpacity(0.1),
              offset: const Offset(0, 8),
            ),
          ],
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 48, color: Colors.grey),
            const SizedBox(height: 12),
            Text(title, style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 8),
            Text(message, textAlign: TextAlign.center),
            const SizedBox(height: 16),
            FilledButton(
              onPressed: onAction,
              child: Text(actionLabel),
            ),
          ],
        ),
      ),
    );
  }
}
