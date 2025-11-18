import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers.dart';
import '../data/analysis_process_service.dart';
import '../data/create_analysis_process_usecase_impl.dart';
import '../domain/create_analysis_process_usecase.dart';

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
