import '../domain/analysis_process.dart';
import '../domain/create_analysis_process_usecase.dart';
import 'analysis_process_service.dart';

class CreateAnalysisProcessUseCaseImpl implements CreateAnalysisProcessUseCase {
  final AnalysisProcessService _service;

  CreateAnalysisProcessUseCaseImpl(this._service);

  @override
  Future<AnalysisProcess> execute({
    required String name,
    required String description,
    required ProcessType type,
  }) async {
    final process = AnalysisProcess.create(
      name: name,
      description: description,
      type: type,
    );
    return _service.createProcess(process);
  }
}
