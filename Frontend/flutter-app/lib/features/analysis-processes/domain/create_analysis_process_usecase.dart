import 'analysis_process.dart';

abstract class CreateAnalysisProcessUseCase {
  Future<AnalysisProcess> execute({
    required String name,
    required String description,
    required ProcessType type,
  });
}
