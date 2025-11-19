import 'package:freezed_annotation/freezed_annotation.dart';

part 'openapi_validation_summary.freezed.dart';
part 'openapi_validation_summary.g.dart';

@freezed
class OpenApiValidationSummary with _$OpenApiValidationSummary {
  const factory OpenApiValidationSummary({
    required int totalChecks,
    required int passedChecks,
    required int failedChecks,
    required int warningChecks,
    required int validationTimeMs,
    @Default(0.0) double successRate,
  }) = _OpenApiValidationSummary;

  factory OpenApiValidationSummary.fromJson(Map<String, dynamic> json) =>
      _$OpenApiValidationSummaryFromJson(json);
}
