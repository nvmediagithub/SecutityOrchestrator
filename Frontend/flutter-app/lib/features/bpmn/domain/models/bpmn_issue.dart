import 'package:freezed_annotation/freezed_annotation.dart';

part 'bpmn_issue.freezed.dart';
part 'bpmn_issue.g.dart';

@freezed
class BpmnIssue with _$BpmnIssue {
  const factory BpmnIssue({
    required String type,
    required String severity,
    required String description,
    String? elementId,
    String? suggestion,
  }) = _BpmnIssue;

  factory BpmnIssue.fromJson(Map<String, dynamic> json) =>
      _$BpmnIssueFromJson(json);
}
