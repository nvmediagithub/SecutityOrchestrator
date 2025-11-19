import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers.dart';
import '../data/analysis_process_service.dart';
import '../data/analysis_session_service.dart';
import '../data/create_analysis_process_usecase_impl.dart';
import '../domain/analysis_process.dart';
import '../domain/create_analysis_process_usecase.dart';
import '../domain/analysis_session.dart';
import '../presentation/controllers/analysis_session_controller.dart';

final analysisProcessServiceProvider = Provider<AnalysisProcessService>((ref) {
  final client = ref.watch(httpClientProvider);
  final baseUrl = ref.watch(backendBaseUrlProvider);
  return AnalysisProcessServiceImpl(client, baseUrl);
});

final createAnalysisProcessUseCaseProvider =
    Provider<CreateAnalysisProcessUseCase>((ref) {
      final service = ref.watch(analysisProcessServiceProvider);
      return CreateAnalysisProcessUseCaseImpl(service);
    });

final processDetailProvider = FutureProvider.family<AnalysisProcess, String>((
  ref,
  id,
) async {
  final service = ref.watch(analysisProcessServiceProvider);
  return service.getProcess(id);
});

final analysisSessionServiceProvider = Provider<AnalysisSessionService>((ref) {
  final client = ref.watch(httpClientProvider);
  final baseUrl = ref.watch(backendBaseUrlProvider);
  return AnalysisSessionService(client, baseUrl);
});

final analysisSessionControllerProvider =
    AutoDisposeStateNotifierProviderFamily<
      AnalysisSessionController,
      AsyncValue<AnalysisSession?>,
      String
    >((ref, processId) {
  final service = ref.watch(analysisSessionServiceProvider);
  return AnalysisSessionController(service, processId);
});
