import 'package:freezed_annotation/freezed_annotation.dart';

part 'llm_provider_status.freezed.dart';
part 'llm_provider_status.g.dart';

@freezed
class LlmProviderStatus with _$LlmProviderStatus {
  const factory LlmProviderStatus({
    required String id,
    required String displayName,
    required String mode,
    required String baseUrl,
    required bool available,
    required bool requiresApiKey,
    @Default(false) bool active,
  }) = _LlmProviderStatus;

  factory LlmProviderStatus.fromJson(Map<String, dynamic> json) =>
      _$LlmProviderStatusFromJson(json);
}
