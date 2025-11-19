import 'package:freezed_annotation/freezed_annotation.dart';

import 'llm_provider_status.dart';

part 'llm_analytics.freezed.dart';
part 'llm_analytics.g.dart';

@freezed
class LlmAnalytics with _$LlmAnalytics {
  const factory LlmAnalytics({
    required String activeProviderId,
    String? activeProviderName,
    String? activeProviderMode,
    @Default(0) int switches,
    DateTime? lastSwitchAt,
    @Default(<LlmProviderStatus>[]) List<LlmProviderStatus> providers,
  }) = _LlmAnalytics;

  factory LlmAnalytics.fromJson(Map<String, dynamic> json) =>
      _$LlmAnalyticsFromJson(json);
}
