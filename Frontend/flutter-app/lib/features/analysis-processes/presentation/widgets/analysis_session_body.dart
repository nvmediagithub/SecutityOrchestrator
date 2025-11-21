import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../di/analysis_processes_providers.dart';
import '../../domain/analysis_session.dart';
import '../../data/analysis_session_service.dart';
import 'analysis_actions.dart';
import 'analysis_context_view.dart';
import 'analysis_status_helpers.dart';

class AnalysisSessionBody extends ConsumerWidget {
  final String processId;
  final bool canStart;
  final AnalysisSession? session;

  const AnalysisSessionBody({
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
                ? 'Анализ готов к запуску.'
                : 'Загрузите BPMN-диаграмму и OpenAPI-спецификацию перед запуском анализа.',
          ),
          const SizedBox(height: 12),
          FilledButton(
            onPressed: canStart
                ? () => runSessionAction(
                () async => sessionService.startSession(processId),
                'Не удалось запустить сеанс анализа',
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
                color: statusColor(session!.status),
              ),
              label: Text(sessionStatusLabel(session!.status)),
            ),
            const Spacer(),
            if (session!.status == AnalysisSessionStatus.completed)
              TextButton(
                onPressed: canStart
                    ? () => runSessionAction(
                          () async => sessionService.startSession(processId),
                          'Не удалось перезапустить сеанс анализа',
                        )
                    : null,
                child: const Text('Перезапустить анализ'),
              ),
          ],
        ),
        const SizedBox(height: 12),
        _StepsList(
          steps: session!.steps,
          currentStepId: session!.currentStepId,
        ),
        const SizedBox(height: 16),
        AnalysisContextView(session: session!),
        const SizedBox(height: 16),
        AnalysisActions(
          session: session!,
          currentStep: currentStep,
          onSubmitInputs: (inputs) => runSessionAction(
            () async => sessionService.provideInputs(session!.id, inputs),
            'Не удалось передать требуемые данные',
          ),
          onCompleteLlm: () => runSessionAction(
            () async => sessionService.completeLlm(session!.id),
            'Не удалось завершить шаг LLM',
          ),
          onSubmitTest: (result) => runSessionAction(
            () async => sessionService.submitTestResult(session!.id, result),
            'Не удалось отправить результат теста',
          ),
          onExecuteHttpStep: (stepId, inputs) => runSessionAction(
            () async => sessionService.executeHttpStep(session!.id, stepId, inputs),
            'Не удалось выполнить HTTP-запрос',
          ),
          onNextStep: () => ref.invalidate(analysisSessionProvider(processId)),
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
                step.id == currentStepId ? Icons.play_circle : Icons.check_circle,
                color: stepStatusColor(step.status),
              ),
              title: Text(step.title),
              subtitle: Text(step.description ?? ''),
              trailing: Text(stepStatusLabel(step.status)),
            ),
          )
          .toList(),
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
