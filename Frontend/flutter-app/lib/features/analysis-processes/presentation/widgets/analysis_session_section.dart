import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../di/analysis_processes_providers.dart';
import '../../domain/analysis_session.dart';

class AnalysisSessionSection extends ConsumerWidget {
  final String processId;
  final String processName;
  final bool canStart;

  const AnalysisSessionSection({
    super.key,
    required this.processId,
    required this.processName,
    required this.canStart,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final sessionState = ref.watch(
      analysisSessionProvider(processId),
    );

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(
                  'LLM Analysis',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                IconButton(
                  tooltip: 'Refresh status',
                  onPressed: () =>
                      ref.invalidate(analysisSessionProvider(processId)),
                  icon: const Icon(Icons.refresh),
                ),
              ],
            ),
            const SizedBox(height: 12),
            sessionState.when(
              data: (session) => _AnalysisSessionBody(
                processId: processId,
                canStart: canStart,
                session: session,
              ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, _) => Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Failed to load analysis session: $error',
                    style: const TextStyle(color: Colors.red),
                  ),
                  const SizedBox(height: 8),
                  FilledButton(
                    onPressed: () =>
                        ref.invalidate(analysisSessionProvider(processId)),
                    child: const Text('Retry'),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _AnalysisSessionBody extends ConsumerWidget {
  final String processId;
  final bool canStart;
  final AnalysisSession? session;

  const _AnalysisSessionBody({
    required this.processId,
    required this.canStart,
    required this.session,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final messenger = ScaffoldMessenger.of(context);
    final sessionService = ref.read(analysisSessionServiceProvider);

    Future<void> runSessionAction(
      Future<void> Function() action,
      String errorMessage,
    ) async {
      try {
        await action();
        ref.invalidate(analysisSessionProvider(processId));
      } catch (error) {
        messenger.showSnackBar(
          SnackBar(content: Text('$errorMessage: $error')),
        );
      }
    }

    if (session == null) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            canStart
                ? 'Анализ ещё не запускался.'
                : 'Загрузите BPMN и OpenAPI, чтобы начать анализ.',
          ),
          const SizedBox(height: 12),
          FilledButton(
            onPressed: canStart
                ? () => runSessionAction(
                      () async => sessionService.startSession(processId),
                      'Не удалось запустить',
                    )
                : null,
            child: const Text('Начать анализ'),
          ),
        ],
      );
    }

    final currentStep = _findCurrentStep(session!);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Chip(
              avatar: Icon(
                Icons.circle,
                size: 12,
                color: _statusColor(session!.status),
              ),
              label: Text(_sessionStatusLabel(session!.status)),
            ),
            const Spacer(),
            if (session!.status == AnalysisSessionStatus.completed)
              TextButton(
                onPressed: canStart
                    ? () => runSessionAction(
                          () async => sessionService.startSession(processId),
                          'Не удалось запустить новый анализ',
                        )
                    : null,
                child: const Text('Запустить заново'),
              ),
          ],
        ),
        const SizedBox(height: 12),
        _StepsList(
          steps: session!.steps,
          currentStepId: session!.currentStepId,
        ),
        const SizedBox(height: 16),
        _AnalysisContextView(session: session!),
        const SizedBox(height: 16),
        _AnalysisActions(
          session: session!,
          currentStep: currentStep,
          onSubmitInputs: (inputs) => runSessionAction(
            () async => sessionService.provideInputs(session!.id, inputs),
            'Ошибка отправки данных',
          ),
          onCompleteLlm: () => runSessionAction(
            () async => sessionService.completeLlm(session!.id),
            'Ошибка генерации плана',
          ),
          onSubmitTest: (result) => runSessionAction(
            () async => sessionService.submitTestResult(session!.id, result),
            'Ошибка отправки результата',
          ),
        ),
      ],
    );
  }
}

class _StepsList extends StatelessWidget {
  final List<AnalysisStep> steps;
  final String? currentStepId;

  const _StepsList({required this.steps, required this.currentStepId});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: steps
          .map(
            (step) => ListTile(
              contentPadding: EdgeInsets.zero,
              leading: Icon(
                step.id == currentStepId
                    ? Icons.play_circle
                    : Icons.check_circle,
                color: _stepStatusColor(step.status),
              ),
              title: Text(step.title),
              subtitle: Text(step.description ?? ''),
              trailing: Text(_stepStatusLabel(step.status)),
            ),
          )
          .toList(),
    );
  }
}

class _AnalysisActions extends StatelessWidget {
  final AnalysisSession session;
  final AnalysisStep? currentStep;
  final Future<void> Function(Map<String, String>) onSubmitInputs;
  final Future<void> Function() onCompleteLlm;
  final Future<void> Function(Map<String, Object>) onSubmitTest;

  const _AnalysisActions({
    required this.session,
    required this.currentStep,
    required this.onSubmitInputs,
    required this.onCompleteLlm,
    required this.onSubmitTest,
  });

  @override
  Widget build(BuildContext context) {
    if (currentStep == null) {
      return const Text('Текущий шаг не найден.');
    }

    switch (currentStep!.type) {
      case AnalysisStepType.collectInputs:
        final inputs = List<String>.from(
          currentStep!.metadata['requiredInputs'] ?? const <String>[],
        );
        return _CollectInputsForm(fields: inputs, onSubmit: onSubmitInputs);
      case AnalysisStepType.llmAnalysis:
        if (currentStep!.status == AnalysisStepStatus.running ||
            currentStep!.status == AnalysisStepStatus.waiting) {
          return FilledButton(
            onPressed: onCompleteLlm,
            child: const Text('Сгенерировать план'),
          );
        }
        break;
      case AnalysisStepType.testExecution:
        if (currentStep!.status == AnalysisStepStatus.waiting) {
          final script = session.context['testScript'] as String? ?? '';
          return _TestExecutionPanel(script: script, onSubmit: onSubmitTest);
        }
        break;
    }
    return const Text('Шаг выполняется автоматически...');
  }
}

class _CollectInputsForm extends StatefulWidget {
  final List<String> fields;
  final Future<void> Function(Map<String, String>) onSubmit;

  const _CollectInputsForm({required this.fields, required this.onSubmit});

  @override
  State<_CollectInputsForm> createState() => _CollectInputsFormState();
}

class _CollectInputsFormState extends State<_CollectInputsForm> {
  final Map<String, TextEditingController> _controllers = {};
  bool _submitting = false;

  @override
  void initState() {
    super.initState();
    for (final field in widget.fields) {
      _controllers[field] = TextEditingController();
    }
  }

  @override
  void dispose() {
    for (final controller in _controllers.values) {
      controller.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Введите дополнительные данные:'),
        const SizedBox(height: 8),
        ..._controllers.entries.map(
          (entry) => Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: TextField(
              controller: entry.value,
              decoration: InputDecoration(
                labelText: entry.key,
                border: const OutlineInputBorder(),
              ),
            ),
          ),
        ),
        FilledButton(
          onPressed: _submitting ? null : _submit,
          child: _submitting
              ? const SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              : const Text('Отправить'),
        ),
      ],
    );
  }

  Future<void> _submit() async {
    setState(() => _submitting = true);
    try {
      final inputs = <String, String>{};
      _controllers.forEach((key, controller) {
        inputs[key] = controller.text;
      });
      await widget.onSubmit(inputs);
    } finally {
      if (mounted) {
        setState(() => _submitting = false);
      }
    }
  }
}

class _TestExecutionPanel extends StatefulWidget {
  final String script;
  final Future<void> Function(Map<String, Object>) onSubmit;

  const _TestExecutionPanel({required this.script, required this.onSubmit});

  @override
  State<_TestExecutionPanel> createState() => _TestExecutionPanelState();
}

class _TestExecutionPanelState extends State<_TestExecutionPanel> {
  final TextEditingController _notesController = TextEditingController();
  bool _success = true;
  bool _submitting = false;

  @override
  void dispose() {
    _notesController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Скрипт для теста:'),
        const SizedBox(height: 8),
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey.shade400),
            borderRadius: BorderRadius.circular(8),
            color: Colors.black.withOpacity(0.03),
          ),
          child: SelectableText(
            widget.script.isNotEmpty
                ? widget.script
                : '// LLM script will appear here',
            style: const TextStyle(fontFamily: 'monospace'),
          ),
        ),
        const SizedBox(height: 12),
        SwitchListTile(
          title: const Text('Тест выполнен успешно'),
          value: _success,
          onChanged: (value) => setState(() => _success = value),
        ),
        TextField(
          controller: _notesController,
          decoration: const InputDecoration(
            labelText: 'Комментарий',
            border: OutlineInputBorder(),
          ),
          maxLines: 4,
        ),
        const SizedBox(height: 12),
        FilledButton(
          onPressed: _submitting ? null : _submit,
          child: _submitting
              ? const SizedBox(
                  width: 16,
                  height: 16,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              : const Text('Отправить результат'),
        ),
      ],
    );
  }

  Future<void> _submit() async {
    setState(() => _submitting = true);
    try {
      await widget.onSubmit({
        'success': _success,
        'notes': _notesController.text,
        'timestamp': DateTime.now().toIso8601String(),
      });
    } finally {
      if (mounted) {
        setState(() => _submitting = false);
      }
    }
  }
}

class _AnalysisContextView extends StatelessWidget {
  final AnalysisSession session;

  const _AnalysisContextView({required this.session});

  @override
  Widget build(BuildContext context) {
    final llmPlan = session.context['llmPlan'] as String?;
    final testScript = session.context['testScript'] as String?;
    final testResult = session.context['testResult'];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (llmPlan != null) ...[
          const Text('LLM план:'),
          const SizedBox(height: 4),
          Text(llmPlan),
          const SizedBox(height: 12),
        ],
        if (testScript != null) ...[
          const Text('Сгенерированный скрипт:'),
          const SizedBox(height: 4),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              border: Border.all(color: Colors.grey.shade300),
              borderRadius: BorderRadius.circular(8),
            ),
            child: SelectableText(
              testScript,
              style: const TextStyle(fontFamily: 'monospace'),
            ),
          ),
          const SizedBox(height: 12),
        ],
        if (testResult != null) ...[
          const Text('Результат теста:'),
          const SizedBox(height: 4),
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              border: Border.all(color: Colors.grey.shade300),
              borderRadius: BorderRadius.circular(8),
            ),
            child: SelectableText(
              const JsonEncoder.withIndent('  ').convert(testResult),
              style: const TextStyle(fontFamily: 'monospace'),
            ),
          ),
        ],
      ],
    );
  }
}

AnalysisStep? _findCurrentStep(AnalysisSession session) {
  final id = session.currentStepId;
  if (id == null) return null;
  try {
    return session.steps.firstWhere((step) => step.id == id);
  } catch (_) {
    return null;
  }
}

String _sessionStatusLabel(AnalysisSessionStatus status) {
  switch (status) {
    case AnalysisSessionStatus.waitingForInput:
      return 'Ожидаем данные';
    case AnalysisSessionStatus.running:
      return 'Запущено';
    case AnalysisSessionStatus.waitingForTest:
      return 'Ожидаем результат теста';
    case AnalysisSessionStatus.completed:
      return 'Завершено';
    case AnalysisSessionStatus.failed:
      return 'Ошибка';
  }
}

String _stepStatusLabel(AnalysisStepStatus status) {
  switch (status) {
    case AnalysisStepStatus.pending:
      return 'В очереди';
    case AnalysisStepStatus.waiting:
      return 'Ожидание';
    case AnalysisStepStatus.running:
      return 'Выполняется';
    case AnalysisStepStatus.completed:
      return 'Готово';
    case AnalysisStepStatus.failed:
      return 'Ошибка';
  }
}

Color _statusColor(AnalysisSessionStatus status) {
  switch (status) {
    case AnalysisSessionStatus.waitingForInput:
      return Colors.orange;
    case AnalysisSessionStatus.running:
    case AnalysisSessionStatus.waitingForTest:
      return Colors.blue;
    case AnalysisSessionStatus.completed:
      return Colors.green;
    case AnalysisSessionStatus.failed:
      return Colors.red;
  }
}

Color _stepStatusColor(AnalysisStepStatus status) {
  switch (status) {
    case AnalysisStepStatus.completed:
      return Colors.green;
    case AnalysisStepStatus.failed:
      return Colors.red;
    case AnalysisStepStatus.running:
      return Colors.blue;
    default:
      return Colors.orange;
  }
}
