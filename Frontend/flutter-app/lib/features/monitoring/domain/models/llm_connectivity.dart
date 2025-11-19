import 'package:freezed_annotation/freezed_annotation.dart';

part 'llm_connectivity.freezed.dart';
part 'llm_connectivity.g.dart';

@freezed
class LlmConnectivity with _$LlmConnectivity {
  const factory LlmConnectivity({
    required String providerId,
    required String providerName,
    required bool success,
    required int statusCode,
    required int latencyMs,
    required String endpoint,
    required String message,
  }) = _LlmConnectivity;

  factory LlmConnectivity.fromJson(Map<String, dynamic> json) =>
      _$LlmConnectivityFromJson(json);
}
