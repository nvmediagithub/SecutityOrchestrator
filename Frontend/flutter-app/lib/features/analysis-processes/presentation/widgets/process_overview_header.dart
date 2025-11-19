import 'package:flutter/material.dart';

import '../../domain/analysis_process.dart';

class ProcessOverviewHeader extends StatelessWidget {
  final AnalysisProcess process;

  const ProcessOverviewHeader({super.key, required this.process});

  @override
  Widget build(BuildContext context) {
    final textTheme = Theme.of(context).textTheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(process.name, style: textTheme.headlineMedium),
        const SizedBox(height: 8),
        Text(
          '${process.type.displayName}  ${process.status.displayName}',
          style: textTheme.titleMedium?.copyWith(color: Colors.grey),
        ),
        const SizedBox(height: 12),
        Wrap(
          spacing: 8,
          runSpacing: 4,
          children: [
            _ArtifactBadge(
              label: 'BPMN',
              uploaded: process.hasBpmn,
              timestamp: process.bpmnUploadedAt,
            ),
            _ArtifactBadge(
              label: 'OpenAPI',
              uploaded: process.hasOpenApi,
              timestamp: process.openapiUploadedAt,
            ),
          ],
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
      ],
    );
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

class _ArtifactBadge extends StatelessWidget {
  final String label;
  final bool uploaded;
  final DateTime? timestamp;

  const _ArtifactBadge({
    required this.label,
    required this.uploaded,
    this.timestamp,
  });

  @override
  Widget build(BuildContext context) {
    final statusText = uploaded ? '$label uploaded' : '$label missing';
    final details =
        uploaded && timestamp != null ? timestamp!.toLocal().toString() : null;

    return Chip(
      avatar: Icon(
        uploaded ? Icons.check_circle_outline : Icons.warning_amber_outlined,
        color: uploaded ? Colors.green : Colors.orange,
      ),
      backgroundColor: uploaded
          ? Colors.green.withOpacity(0.15)
          : Colors.orange.withOpacity(0.15),
      label: Text(details != null ? '$statusText • $details' : statusText),
    );
  }
}
