import 'package:flutter/material.dart';

import '../../domain/analysis_session.dart';
import 'http_request_panel.dart';

class AnalysisContextView extends StatelessWidget {
  final AnalysisSession session;

  const AnalysisContextView({required this.session});

  @override
  Widget build(BuildContext context) {
    final llmPlan = session.context['llmPlan'] as String?;
    final httpResults = session.context['httpResults'] as List<dynamic>? ?? [];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (llmPlan != null) ...[
          const Text('LLM план:'),
          const SizedBox(height: 4),
          Text(llmPlan),
          const SizedBox(height: 12),
        ],
        if (httpResults.isNotEmpty) ...[
          const Text('Результаты HTTP-запросов:'),
          const SizedBox(height: 8),
          ...httpResults.map((result) => HttpResultCard(result: Map<String, dynamic>.from(result))),
        ],
      ],
    );
  }
}
