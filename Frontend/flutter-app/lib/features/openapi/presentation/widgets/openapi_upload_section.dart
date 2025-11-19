import 'dart:typed_data';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

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
    final endpoints = analysis.endpoints.entries.take(5);
    final securityIssues = analysis.securityIssues;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          '${analysis.specificationName} (${analysis.version ?? 'v?.?'})',
          style: Theme.of(context).textTheme.titleMedium,
        ),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          runSpacing: 4,
          children: [
            Chip(
              avatar: Icon(
                analysis.valid ? Icons.check_circle : Icons.error_outline,
                color: analysis.valid ? Colors.green : Colors.red,
              ),
              label: Text(analysis.valid ? 'Valid spec' : 'Validation errors'),
              backgroundColor: analysis.valid
                  ? Colors.green.withOpacity(0.1)
                  : Colors.red.withOpacity(0.1),
            ),
            Chip(
              avatar: const Icon(Icons.alt_route),
              label: Text('${analysis.endpointCount} endpoints'),
            ),
            Chip(
              avatar: const Icon(Icons.account_tree),
              label: Text('${analysis.schemaCount} schemas'),
            ),
          ],
        ),
        if (analysis.validationSummary != null) ...[
          const SizedBox(height: 8),
          Text(
            'Validation: ${analysis.validationSummary!.passedChecks}/'
            '${analysis.validationSummary!.totalChecks} passed',
          ),
        ],
        if (analysis.recommendations.isNotEmpty) ...[
          const SizedBox(height: 12),
          Text(
            'Recommendations',
            style: Theme.of(context).textTheme.titleSmall,
          ),
          const SizedBox(height: 4),
          ...analysis.recommendations
              .map(
                (rec) => ListTile(
                  dense: true,
                  contentPadding: EdgeInsets.zero,
                  leading: const Icon(Icons.arrow_right),
                  title: Text(rec),
                ),
              )
              .toList(),
        ],
        if (securityIssues.isNotEmpty) ...[
          const SizedBox(height: 12),
          Text(
            'Security Issues',
            style: Theme.of(context).textTheme.titleSmall,
          ),
          const SizedBox(height: 4),
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
            style: Theme.of(context).textTheme.titleSmall,
          ),
          const SizedBox(height: 4),
          ...endpoints.map((entry) {
            final operations = entry.value as Map<String, dynamic>? ?? {};
            final methods = operations.keys
                .map((m) => m.toUpperCase())
                .join(', ');
            return ListTile(
              dense: true,
              contentPadding: EdgeInsets.zero,
              title: Text(entry.key),
              subtitle: Text(
                methods.isEmpty ? 'No operations documented' : methods,
              ),
            );
          }),
        ],
        const SizedBox(height: 12),
        Text(
          'Raw specification',
          style: Theme.of(context).textTheme.titleSmall,
        ),
        const SizedBox(height: 4),
        Container(
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            border: Border.all(color: Colors.grey.shade300),
            borderRadius: BorderRadius.circular(8),
          ),
          constraints: const BoxConstraints(maxHeight: 200),
          child: SingleChildScrollView(
            child: SelectableText(
              analysis.openapiContent,
              style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
            ),
          ),
        ),
      ],
    );
  }
}
