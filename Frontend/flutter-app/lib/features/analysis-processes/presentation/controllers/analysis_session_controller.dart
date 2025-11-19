import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../data/analysis_session_service.dart';
import '../../domain/analysis_session.dart';

class AnalysisSessionController
    extends Notifier<AsyncValue<AnalysisSession?>> {
  final String _processId;
  final AnalysisSessionService _service;

  AnalysisSessionController(String processId, AnalysisSessionService service)
      : _processId = processId,
        _service = service;

  @override
  AsyncValue<AnalysisSession?> build() {
    loadLatest();
    return const AsyncValue.loading();
  }

  Future<void> loadLatest() async {
    try {
      final session = await _service.getLatestSession(_processId);
      state = AsyncValue.data(session);
    } catch (error, stack) {
      state = AsyncValue.error(error, stack);
    }
  }

  Future<void> startAnalysis() async {
    await _runAction(() => _service.startSession(_processId));
  }

  Future<void> submitInputs(Map<String, String> inputs) async {
    final session = state.value;
    if (session == null) return;
    await _runAction(() => _service.provideInputs(session.id, inputs));
  }

  Future<void> completeLlmStep() async {
    final session = state.value;
    if (session == null) return;
    await _runAction(() => _service.completeLlm(session.id));
  }

  Future<void> submitTest(Map<String, Object> result) async {
    final session = state.value;
    if (session == null) return;
    await _runAction(() => _service.submitTestResult(session.id, result));
  }

  Future<void> _runAction(Future<AnalysisSession> Function() action) async {
    state = const AsyncValue.loading();
    try {
      final updated = await action();
      state = AsyncValue.data(updated);
    } catch (error, stack) {
      state = AsyncValue.error(error, stack);
      rethrow;
    }
  }
}
