import 'package:freezed_annotation/freezed_annotation.dart';

part 'analysis_session.freezed.dart';
part 'analysis_session.g.dart';

@JsonEnum(fieldRename: FieldRename.snake)
enum AnalysisSessionStatus {
  waitingForInput,
  running,
  waitingForTest,
  completed,
  failed,
}

@JsonEnum(fieldRename: FieldRename.snake)
enum AnalysisStepType { collectInputs, llmAnalysis, testExecution }

@JsonEnum(fieldRename: FieldRename.snake)
enum AnalysisStepStatus { pending, waiting, running, completed, failed }

@freezed
class AnalysisSession with _$AnalysisSession {
  const factory AnalysisSession({
    required String id,
    required String processId,
    required AnalysisSessionStatus status,
    String? currentStepId,
    @Default(<AnalysisStep>[]) List<AnalysisStep> steps,
    @Default(<String, dynamic>{}) Map<String, dynamic> context,
    required DateTime createdAt,
    required DateTime updatedAt,
  }) = _AnalysisSession;

  factory AnalysisSession.fromJson(Map<String, dynamic> json) =>
      _$AnalysisSessionFromJson(json);
}

@freezed
class AnalysisStep with _$AnalysisStep {
  const factory AnalysisStep({
    required String id,
    required String title,
    String? description,
    required AnalysisStepType type,
    required AnalysisStepStatus status,
    @Default(<String, dynamic>{}) Map<String, dynamic> metadata,
  }) = _AnalysisStep;

  factory AnalysisStep.fromJson(Map<String, dynamic> json) =>
      _$AnalysisStepFromJson(json);
}
