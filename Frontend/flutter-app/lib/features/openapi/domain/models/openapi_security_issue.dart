import 'package:freezed_annotation/freezed_annotation.dart';

part 'openapi_security_issue.freezed.dart';
part 'openapi_security_issue.g.dart';

@freezed
class OpenApiSecurityIssue with _$OpenApiSecurityIssue {
  const factory OpenApiSecurityIssue({
    required String type,
    required String severity,
    required String description,
    String? location,
  }) = _OpenApiSecurityIssue;

  factory OpenApiSecurityIssue.fromJson(Map<String, dynamic> json) =>
      _$OpenApiSecurityIssueFromJson(json);
}
