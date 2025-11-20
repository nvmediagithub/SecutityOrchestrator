import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../analysis-processes/di/analysis_processes_providers.dart';
import '../../di/bpmn_providers.dart';
import '../../domain/models/bpmn_analysis.dart';
import 'bpmn_viewer.dart';

class BpmnUploadSection extends ConsumerStatefulWidget {
  final String? suggestedName;
  final String? processId;

  const BpmnUploadSection({super.key, this.suggestedName, this.processId});

  @override
  ConsumerState<BpmnUploadSection> createState() => _BpmnUploadSectionState();
}

class _BpmnUploadSectionState extends ConsumerState<BpmnUploadSection> {
  BpmnAnalysis? _analysis;
  List<BpmnAnalysis>? _examples;
  bool _isLoading = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    if (widget.processId != null) {
      _loadExistingDiagram();
    }
  }

  Future<void> _loadExistingDiagram() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final service = ref.read(bpmnApiServiceProvider);
      final analysis = await service.getProcessDiagram(widget.processId!);
      setState(() {
        _analysis = analysis;
      });
      if (widget.processId != null) {
        ref.invalidate(processDetailProvider(widget.processId!));
      }
    } catch (error) {
      setState(() {
        _error = error.toString();
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _pickAndAnalyze() async {
    setState(() {
      _error = null;
    });
    final selection = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: const ['bpmn', 'xml'],
      withData: true,
    );

    if (selection == null || selection.files.isEmpty) {
      return;
    }

    final file = selection.files.single;
    final bytes = file.bytes;
    if (bytes == null) {
      setState(() {
        _error = 'Unable to read file contents';
      });
      return;
    }

    if (widget.processId != null) {
      await _uploadForProcess(bytes, file.name);
    } else {
      await _analyze(bytes, file.name);
    }
  }

  Future<void> _analyze(Uint8List bytes, String fileName) async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final service = ref.read(bpmnApiServiceProvider);
      final analysis = await service.analyzeDiagram(
        bytes: bytes,
        fileName: fileName,
        diagramName: widget.suggestedName,
      );
      setState(() {
        _analysis = analysis;
      });
      if (widget.processId != null) {
        ref.invalidate(processDetailProvider(widget.processId!));
      }
    } catch (error) {
      setState(() {
        _error = error.toString();
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _uploadForProcess(Uint8List bytes, String fileName) async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final service = ref.read(bpmnApiServiceProvider);
      final analysis = await service.uploadProcessDiagram(
        processId: widget.processId!,
        bytes: bytes,
        fileName: fileName,
      );
      setState(() {
        _analysis = analysis;
      });
    } catch (error) {
      setState(() {
        _error = error.toString();
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _loadExample() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final service = ref.read(bpmnApiServiceProvider);
      _examples ??= await service.getExamples();
      if (_examples!.isEmpty) {
        setState(() {
          _error = 'Нет доступных примеров диаграмм.';
        });
      } else {
        setState(() {
          _analysis = _examples!.first;
        });
      }
    } catch (error) {
      setState(() {
        _error = error.toString();
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 24),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(
                  'BPMN анализ',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                FilledButton.tonalIcon(
                  onPressed: _isLoading ? null : _loadExample,
                  icon: const Icon(Icons.auto_awesome),
                  label: const Text('Пример из датасета'),
                ),
                const SizedBox(width: 12),
                FilledButton.icon(
                  onPressed: _isLoading ? null : _pickAndAnalyze,
                  icon: const Icon(Icons.upload_file),
                  label: const Text('Загрузить BPMN'),
                ),
              ],
            ),
            const SizedBox(height: 12),
            if (_isLoading) const LinearProgressIndicator(),
            if (_error != null) ...[
              const SizedBox(height: 8),
              Text(_error!, style: const TextStyle(color: Colors.red)),
            ],
            if (_analysis != null) ...[
              const SizedBox(height: 16),
              _BpmnAnalysisSummary(analysis: _analysis!),
              const SizedBox(height: 16),
              Text('Диаграмма', style: Theme.of(context).textTheme.titleMedium),
              const SizedBox(height: 8),
              BpmnViewer(bpmnXml: _analysis!.bpmnContent),
            ] else if (!_isLoading)
              const Text(
                'Загрузите BPMN диаграмму, чтобы получить предварительный анализ или используйте готовый пример.',
                style: TextStyle(color: Colors.grey),
              ),
          ],
        ),
      ),
    );
  }
}

class _BpmnAnalysisSummary extends StatelessWidget {
  final BpmnAnalysis analysis;

  const _BpmnAnalysisSummary({required this.analysis});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          analysis.diagramName,
          style: Theme.of(context).textTheme.titleMedium,
        ),
        const SizedBox(height: 4),
        Text(
          'Общий риск: ${analysis.overallRisk} · Найдено ${analysis.totalIssues} проблем',
        ),
        const SizedBox(height: 12),
        _buildIssuesSection('Структурные проблемы', analysis.structuralIssues),
        _buildIssuesSection('Проблемы безопасности', analysis.securityIssues),
        _buildIssuesSection(
          'Проблемы производительности',
          analysis.performanceIssues,
        ),
      ],
    );
  }

  Widget _buildIssuesSection(String title, List issues) {
    if (issues.isEmpty) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 4),
        child: Text('$title: не обнаружено'),
      );
    }
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 4),
          ...issues.map(
            (issue) => ListTile(
              dense: true,
              contentPadding: EdgeInsets.zero,
              leading: const Icon(Icons.bug_report, size: 18),
              title: Text('${issue.type} · ${issue.severity}'),
              subtitle: Text(issue.description),
            ),
          ),
        ],
      ),
    );
  }
}
