import 'package:flutter/material.dart';

import '../../domain/analysis_session.dart';
import 'input_field_spec.dart';
import 'key_value_row.dart';
import 'analysis_status_helpers.dart';

class HttpRequestPanel extends StatefulWidget {
  final AnalysisSession session;
  final AnalysisStep step;
  final Future<void> Function(Map<String, String>) onExecute;
  final VoidCallback onNextStep;

  const HttpRequestPanel({
    required this.session,
    required this.step,
    required this.onExecute,
    required this.onNextStep,
  });

  @override
  State<HttpRequestPanel> createState() => _HttpRequestPanelState();
}

class _HttpRequestPanelState extends State<HttpRequestPanel> {
  final Map<String, TextEditingController> _controllers = {};
  bool _running = false;

  List<Map<String, dynamic>> get _httpFields {
    final raw = widget.step.metadata['additionalInputs'];
    if (raw is List) {
      return raw.whereType<Map<String, dynamic>>().toList();
    }
    return const [];
  }

  List<InputFieldSpec> get _fields => _httpFields
      .map(InputFieldSpec.fromNullable)
      .whereType<InputFieldSpec>()
      .toList();

  @override
  void initState() {
    super.initState();
    for (final field in _fields) {
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
    final request = widget.step.metadata['httpRequest'];
    final requestMap =
        request is Map ? Map<String, dynamic>.from(request) : const {};
    final results = _httpResultsForStep(widget.session, widget.step.id);
    final statusLabel = stepStatusLabel(widget.step.status);

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Text(
              'HTTP-запрос: ${requestMap['name'] ?? widget.step.title}',
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
            const Spacer(),
            Chip(
              label: Text(statusLabel),
              backgroundColor: stepStatusColor(widget.step.status).withOpacity(0.15),
            ),
          ],
        ),
        const SizedBox(height: 8),
        KeyValueRow(label: 'Метод', value: requestMap['method'] ?? 'GET'),
        KeyValueRow(label: 'URL', value: requestMap['url'] ?? '-'),
        if (requestMap['description'] != null) ...[
          const SizedBox(height: 8),
          Text(requestMap['description'].toString()),
        ],
        if (_fields.isNotEmpty) ...[
          const SizedBox(height: 12),
          const Text('Дополнительные данные:'),
          const SizedBox(height: 6),
          ..._fields.map(_buildField),
        ],
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: FilledButton(
                onPressed: widget.step.status == AnalysisStepStatus.waiting && !_running
                    ? _submit
                    : null,
                child: _running
                    ? const SizedBox(
                        width: 16,
                        height: 16,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('Выполнить HTTP-запрос'),
              ),
            ),
            const SizedBox(width: 8),
            OutlinedButton(
              onPressed: widget.onNextStep,
              child: const Text('Следующий шаг'),
            ),
          ],
        ),
        if (results.isNotEmpty) ...[
          const SizedBox(height: 12),
          const Text('Результаты:'),
          const SizedBox(height: 8),
          ...results.map((result) => HttpResultCard(result: result)),
        ],
      ],
    );
  }

  Widget _buildField(InputFieldSpec field) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: TextField(
        controller: _controllers[field.name]!,
        decoration: InputDecoration(
          labelText: field.label,
          helperText: field.description,
          border: const OutlineInputBorder(),
        ),
      ),
    );
  }

  Future<void> _submit() async {
    setState(() => _running = true);
    final values = <String, String>{};
    for (final field in _fields) {
      final text = _controllers[field.name]!.text.trim();
      if (text.isNotEmpty) {
        values[field.name] = text;
      }
    }
    try {
      await widget.onExecute(values);
    } finally {
      if (mounted) {
        setState(() => _running = false);
      }
    }
  }

  List<Map<String, dynamic>> _httpResultsForStep(AnalysisSession session, String stepId) {
    final rawResults = session.context['httpResults'] as List<dynamic>? ?? [];
    return rawResults
        .whereType<Map<String, dynamic>>()
        .where((entry) => entry['stepId']?.toString() == stepId)
        .toList();
  }
}

class HttpResultCard extends StatelessWidget {
  final Map<String, dynamic> result;

  const HttpResultCard({required this.result});

  @override
  Widget build(BuildContext context) {
    return Card(
      color: Colors.grey.shade50,
      child: Padding(
        padding: const EdgeInsets.all(10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              result['name']?.toString() ?? 'HTTP шаг',
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
            const SizedBox(height: 4),
            Text('Статус: ${result['status'] ?? 'н/д'}'),
            if (result['durationMs'] != null)
              Text('Длительность: ${result['durationMs']} мс'),
            const SizedBox(height: 4),
            SelectableText(
              'Тело: ${result['body'] ?? ''}',
              style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
            ),
          ],
        ),
      ),
    );
  }
}
