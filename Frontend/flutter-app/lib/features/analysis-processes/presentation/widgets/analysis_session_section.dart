import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../di/analysis_processes_providers.dart';
import 'analysis_session_body.dart';

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
    final sessionState = ref.watch(analysisSessionProvider(processId));

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(
                  'LLM-анализ',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                IconButton(
                  tooltip: 'Обновить статус',
                  onPressed: () => ref.invalidate(analysisSessionProvider(processId)),
                  icon: const Icon(Icons.refresh),
                ),
              ],
            ),
            const SizedBox(height: 12),
            sessionState.when(
              data: (session) => AnalysisSessionBody(
                processId: processId,
                canStart: canStart,
                session: session,
              ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, _) => Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                Text(
                  'Не удалось загрузить сессию анализа: $error',
                  style: const TextStyle(color: Colors.red),
                ),
                  const SizedBox(height: 8),
                  FilledButton(
                    onPressed: () => ref.invalidate(analysisSessionProvider(processId)),
                    child: const Text('Повторить'),
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
