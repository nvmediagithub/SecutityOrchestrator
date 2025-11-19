import 'package:freezed_annotation/freezed_annotation.dart';

part 'openapi_validation_issue.freezed.dart';
part 'openapi_validation_issue.g.dart';

@freezed
class OpenApiValidationIssue with _$OpenApiValidationIssue {
  const factory OpenApiValidationIssue({
    String? path,
    required String message,
    String? severity,
    String? errorType,
    int? lineNumber,
    int? columnNumber,
  }) = _OpenApiValidationIssue;

  factory OpenApiValidationIssue.fromJson(Map<String, dynamic> json) =>
      _$OpenApiValidationIssueFromJson(json);
}
