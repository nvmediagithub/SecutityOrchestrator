import 'package:freezed_annotation/freezed_annotation.dart';

import 'openapi_security_issue.dart';
import 'openapi_validation_issue.dart';
import 'openapi_validation_summary.dart';

part 'openapi_analysis.freezed.dart';
part 'openapi_analysis.g.dart';

@freezed
class OpenApiAnalysis with _$OpenApiAnalysis {
  const factory OpenApiAnalysis({
    required String specificationName,
    String? version,
    required bool valid,
    String? description,
    @Default(<String, dynamic>{}) Map<String, dynamic> metadata,
    OpenApiValidationSummary? validationSummary,
    @Default(<OpenApiValidationIssue>[]) List<OpenApiValidationIssue> validationErrors,
    required int endpointCount,
    required int schemaCount,
    @Default(<String, int>{}) Map<String, int> operationsByMethod,
    @Default(<String, dynamic>{}) Map<String, dynamic> endpoints,
    @Default(<String, dynamic>{}) Map<String, dynamic> schemas,
    @Default(<OpenApiSecurityIssue>[]) List<OpenApiSecurityIssue> securityIssues,
    @Default(<String>[]) List<String> recommendations,
    required String openapiContent,
    DateTime? analyzedAt,
  }) = _OpenApiAnalysis;

  factory OpenApiAnalysis.fromJson(Map<String, dynamic> json) => _$OpenApiAnalysisFromJson(json);
}
