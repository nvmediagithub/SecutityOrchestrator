import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../analysis-processes/di/analysis_processes_providers.dart';
import '../../di/openapi_providers.dart';
import '../../domain/models/openapi_analysis.dart';

class OpenApiUploadSection extends ConsumerStatefulWidget {
  final String? suggestedName;
  final String? processId;

  const OpenApiUploadSection({super.key, this.suggestedName, this.processId});

  @override
  ConsumerState<OpenApiUploadSection> createState() =>
      _OpenApiUploadSectionState();
}

class _OpenApiUploadSectionState extends ConsumerState<OpenApiUploadSection> {
  OpenApiAnalysis? _analysis;
  List<OpenApiAnalysis>? _examples;
  bool _isLoading = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    if (widget.processId != null) {
      _loadExistingSpec();
    }
  }

  Future<void> _loadExistingSpec() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final service = ref.read(openApiServiceProvider);
      final analysis = await service.getProcessSpec(widget.processId!);
      setState(() => _analysis = analysis);
      if (widget.processId != null) {
        ref.invalidate(processDetailProvider(widget.processId!));
      }
    } catch (error) {
      setState(() => _error = error.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _pickAndAnalyze() async {
    final result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: const ['json', 'yaml', 'yml'],
      withData: true,
    );
    if (result == null || result.files.isEmpty) {
      return;
    }
    final file = result.files.single;
    final bytes = file.bytes;
    if (bytes == null) {
      setState(() => _error = 'Unable to read file contents');
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
      final service = ref.read(openApiServiceProvider);
      final analysis = await service.analyzeSpec(
        bytes: bytes,
        fileName: fileName,
        specName: widget.suggestedName,
      );
      setState(() => _analysis = analysis);
      if (widget.processId != null) {
        ref.invalidate(processDetailProvider(widget.processId!));
      }
    } catch (error) {
      setState(() => _error = error.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _uploadForProcess(Uint8List bytes, String fileName) async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final service = ref.read(openApiServiceProvider);
      final analysis = await service.uploadProcessSpec(
        processId: widget.processId!,
        bytes: bytes,
        fileName: fileName,
      );
      setState(() => _analysis = analysis);
    } catch (error) {
      setState(() => _error = error.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _loadExample() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final service = ref.read(openApiServiceProvider);
      _examples ??= await service.getExamples();
      if (_examples!.isEmpty) {
        setState(() => _error = 'No OpenAPI examples available');
      } else {
        setState(() => _analysis = _examples!.first);
      }
    } catch (error) {
      setState(() => _error = error.toString());
    } finally {
      setState(() => _isLoading = false);
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
                  'OpenAPI Specification',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                FilledButton.tonalIcon(
                  onPressed: _isLoading ? null : _loadExample,
                  icon: const Icon(Icons.auto_awesome),
                  label: const Text('Load example'),
                ),
                const SizedBox(width: 12),
                FilledButton.icon(
                  onPressed: _isLoading ? null : _pickAndAnalyze,
                  icon: const Icon(Icons.upload_file),
                  label: Text(
                    widget.processId != null
                        ? 'Upload OpenAPI'
                        : 'Analyze OpenAPI',
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            if (_isLoading) const LinearProgressIndicator(),
            if (_error != null)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Text(_error!, style: const TextStyle(color: Colors.red)),
              ),
            if (_analysis != null) ...[
              const SizedBox(height: 12),
              _OpenApiAnalysisSummary(analysis: _analysis!),
            ] else if (!_isLoading)
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 12),
                child: Text(
                  'Upload an OpenAPI file to run validation and security heuristics.',
                  style: TextStyle(color: Colors.grey),
                ),
              ),
          ],
        ),
      ),
    );
  }
}

class _OpenApiAnalysisSummary extends StatelessWidget {
  final OpenApiAnalysis analysis;

  const _OpenApiAnalysisSummary({required this.analysis});

  @override
  Widget build(BuildContext context) {
    final endpoints = analysis.endpoints.entries.take(4);
    final securityIssues = analysis.securityIssues;
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            boxShadow: [
              BoxShadow(
                color: Colors.grey.withOpacity(0.15),
                blurRadius: 12,
                offset: const Offset(0, 6),
              ),
            ],
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                '${analysis.specificationName} (${analysis.version ?? 'v?.?'})',
                style: theme.textTheme.titleLarge,
              ),
              const SizedBox(height: 8),
              Text(
                analysis.description ?? 'OpenAPI specification overview',
                style: theme.textTheme.bodyMedium?.copyWith(color: Colors.grey[600]),
              ),
              const SizedBox(height: 12),
              Wrap(
                spacing: 8,
                runSpacing: 4,
                children: [
                  _StatusChip(
                    label: analysis.valid ? 'Valid spec' : 'Validation issues',
                    icon: analysis.valid ? Icons.check_circle : Icons.error_outline,
                    color: analysis.valid ? Colors.green : Colors.red,
                  ),
                  _StatusChip(
                    label: '${analysis.endpointCount} endpoints',
                    icon: Icons.alt_route,
                    color: Colors.blueGrey,
                  ),
                  _StatusChip(
                    label: '${analysis.schemaCount} schemas',
                    icon: Icons.account_tree,
                    color: Colors.deepPurple,
                  ),
                ],
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        if (analysis.validationSummary != null)
          Text(
            'Validation: ${analysis.validationSummary!.passedChecks}/${analysis.validationSummary!.totalChecks} checks passed',
            style: theme.textTheme.bodyMedium,
          ),
        if (analysis.recommendations.isNotEmpty) ...[
          const SizedBox(height: 12),
          Text(
            'Recommendations',
            style: theme.textTheme.titleSmall,
          ),
          const SizedBox(height: 8),
          ...analysis.recommendations
              .map(
                (rec) => Container(
                  width: double.infinity,
                  margin: const EdgeInsets.only(bottom: 6),
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.orange.withOpacity(0.08),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(rec),
                ),
              )
              .toList(),
        ],
        if (securityIssues.isNotEmpty) ...[
          const SizedBox(height: 12),
          Text(
            'Security Issues',
            style: theme.textTheme.titleSmall,
          ),
          const SizedBox(height: 8),
          ...securityIssues.map(
            (issue) => ListTile(
              dense: true,
              contentPadding: EdgeInsets.zero,
              leading: const Icon(Icons.security, color: Colors.orange),
              title: Text('${issue.type} (${issue.severity})'),
              subtitle: Text(issue.description),
            ),
          ),
        ],
        if (endpoints.isNotEmpty) ...[
          const SizedBox(height: 12),
          Text(
            'Endpoints preview',
            style: theme.textTheme.titleSmall,
          ),
          const SizedBox(height: 8),
          ...endpoints.map((entry) {
            final operations = entry.value as Map<String, dynamic>? ?? {};
            final methods = operations.keys.map((m) => m.toUpperCase()).join(', ');
            return ListTile(
              dense: true,
              contentPadding: EdgeInsets.zero,
              leading: const Icon(Icons.chevron_right),
              title: Text(entry.key),
              subtitle: Text(
                methods.isEmpty ? 'No operations documented' : methods,
              ),
            );
          }),
        ],
        const SizedBox(height: 16),
        _RawSpecViewer(content: analysis.openapiContent),
      ],
    );
  }
}


class _StatusChip extends StatelessWidget {
  final String label;
  final IconData icon;
  final Color color;

  const _StatusChip({
    required this.label,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Chip(
      avatar: Icon(icon, color: color),
      label: Text(label),
      backgroundColor: color.withOpacity(0.1),
    );
  }
}


class _RawSpecViewer extends StatefulWidget {
  final String content;

  const _RawSpecViewer({required this.content});

  @override
  State<_RawSpecViewer> createState() => _RawSpecViewerState();
}


class _RawSpecViewerState extends State<_RawSpecViewer> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    final preview = widget.content.split('\n').take(6).join('\n');
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Raw specification',
          style: Theme.of(context).textTheme.titleSmall,
        ),
        const SizedBox(height: 8),
        AnimatedCrossFade(
          duration: const Duration(milliseconds: 200),
          crossFadeState: _expanded ? CrossFadeState.showSecond : CrossFadeState.showFirst,
          firstChild: Container(
            width: double.infinity,
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.grey.shade100,
              borderRadius: BorderRadius.circular(10),
              border: Border.all(color: Colors.grey.shade300),
            ),
            child: Text(
              '$preview\n...',
              style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
            ),
          ),
          secondChild: Container(
            width: double.infinity,
            constraints: const BoxConstraints(maxHeight: 220),
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.grey.shade50,
              borderRadius: BorderRadius.circular(10),
              border: Border.all(color: Colors.grey.shade300),
            ),
            child: SingleChildScrollView(
              child: SelectableText(
                widget.content,
                style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
              ),
            ),
          ),
        ),
        Align(
          alignment: Alignment.centerRight,
          child: TextButton(
            onPressed: () => setState(() => _expanded = !_expanded),
            child: Text(_expanded ? 'Collapse JSON' : 'Show raw JSON'),
          ),
        ),
      ],
    );
  }
}
