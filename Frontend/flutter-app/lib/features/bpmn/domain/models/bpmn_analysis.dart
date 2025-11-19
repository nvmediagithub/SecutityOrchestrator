import 'package:freezed_annotation/freezed_annotation.dart';

import 'bpmn_issue.dart';

part 'bpmn_analysis.freezed.dart';
part 'bpmn_analysis.g.dart';

@freezed
class BpmnAnalysis with _$BpmnAnalysis {
  const factory BpmnAnalysis({
    required String diagramName,
    String? processId,
    required String overallRisk,
    required int totalIssues,
    @Default(<BpmnIssue>[]) List<BpmnIssue> structuralIssues,
    @Default(<BpmnIssue>[]) List<BpmnIssue> securityIssues,
    @Default(<BpmnIssue>[]) List<BpmnIssue> performanceIssues,
    required String bpmnContent,
    DateTime? analyzedAt,
  }) = _BpmnAnalysis;

  factory BpmnAnalysis.fromJson(Map<String, dynamic> json) =>
      _$BpmnAnalysisFromJson(json);
}
