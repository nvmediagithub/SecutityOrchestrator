import 'package:flutter/material.dart';

import '../../domain/analysis_session.dart';
import 'analysis_context_view.dart';
import 'http_request_panel.dart';
import 'input_field_spec.dart';

class AnalysisActions extends StatelessWidget {
  final AnalysisSession session;
  final AnalysisStep? currentStep;
  final Future<void> Function(Map<String, String>) onSubmitInputs;
  final Future<void> Function() onCompleteLlm;
  final Future<void> Function(Map<String, Object>) onSubmitTest;
  final Future<void> Function(String stepId, Map<String, String>) onExecuteHttpStep;
  final VoidCallback onNextStep;

  const AnalysisActions({
    required this.session,
    required this.currentStep,
    required this.onSubmitInputs,
    required this.onCompleteLlm,
    required this.onSubmitTest,
    required this.onExecuteHttpStep,
    required this.onNextStep,
  });

  @override
  Widget build(BuildContext context) {
    if (currentStep == null) {
      return const Text('Нет активного шага анализа.');
    }

    switch (currentStep!.type) {
      case AnalysisStepType.collectInputs:
        final fields = parseInputFields(currentStep!.metadata['requiredInputs']);
        if (fields.isEmpty) {
          return const Text('Нет требуемых данных для сбора.');
        }
        return _CollectInputsForm(fields: fields, onSubmit: onSubmitInputs);
      case AnalysisStepType.llmAnalysis:
        if (currentStep!.status == AnalysisStepStatus.running ||
            currentStep!.status == AnalysisStepStatus.waiting) {
          return FilledButton(
            onPressed: onCompleteLlm,
            child: const Text('Завершить шаг LLM'),
          );
        }
        break;
      case AnalysisStepType.httpRequest:
        return HttpRequestPanel(
          session: session,
          step: currentStep!,
          onExecute: (inputs) => onExecuteHttpStep(currentStep!.id, inputs),
          onNextStep: onNextStep,
        );
      case AnalysisStepType.testExecution:
        if (currentStep!.status == AnalysisStepStatus.waiting) {
          final script = session.context['testScript'] as String? ?? '';
          return _TestExecutionPanel(script: script, onSubmit: onSubmitTest);
        }
        break;
    }
    return const Text('Готово, ожидаю следующего шага...');
  }
}

class _CollectInputsForm extends StatefulWidget {
  final List<InputFieldSpec> fields;
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
      _controllers[field.name] = TextEditingController();
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
        const Text('Введите параметры для анализа (обязательные и необязательные):'),
        const SizedBox(height: 8),
        ...widget.fields.map((field) {
          final controller = _controllers[field.name]!;
          return Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      field.label,
                      style: const TextStyle(fontWeight: FontWeight.w600),
                    ),
                    if (!field.required)
                      const Padding(
                        padding: EdgeInsets.only(left: 4),
                        child: Text(
                          '(необязательно)',
                          style: TextStyle(color: Colors.grey),
                        ),
                      ),
                  ],
                ),
                if (field.description != null && field.description!.trim().isNotEmpty)
                  Padding(
                    padding: const EdgeInsets.only(top: 2, bottom: 8),
                    child: Text(
                      field.description!,
                      style: const TextStyle(color: Colors.black54),
                    ),
                  ),
                TextField(
                  controller: controller,
                  decoration: InputDecoration(
                    labelText: field.label,
                    border: const OutlineInputBorder(),
                  ),
                ),
              ],
            ),
          );
        }),
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
    setState(() {
      _submitting = true;
    });
    final values = <String, String>{};
    for (final field in widget.fields) {
      final controller = _controllers[field.name]!;
      final value = controller.text.trim();
      if (value.isNotEmpty) {
        values[field.name] = value;
      }
    }
    try {
      await widget.onSubmit(values);
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
        const Text('Тестовый скрипт:'),
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
                : '// Здесь появится скрипт LLM',
            style: const TextStyle(fontFamily: 'monospace'),
          ),
        ),
        const SizedBox(height: 12),
        SwitchListTile(
          title: const Text('Тест прошёл успешно'),
          value: _success,
          onChanged: (value) => setState(() => _success = value),
        ),
        TextField(
          controller: _notesController,
          decoration: const InputDecoration(
            labelText: 'Заметки',
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
              : const Text('Отправить результат теста'),
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
