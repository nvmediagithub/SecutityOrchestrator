// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'llm_analytics.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$LlmAnalyticsImpl _$$LlmAnalyticsImplFromJson(Map<String, dynamic> json) =>
    _$LlmAnalyticsImpl(
      activeProviderId: json['activeProviderId'] as String,
      activeProviderName: json['activeProviderName'] as String?,
      activeProviderMode: json['activeProviderMode'] as String?,
      switches: (json['switches'] as num?)?.toInt() ?? 0,
      lastSwitchAt: json['lastSwitchAt'] == null
          ? null
          : DateTime.parse(json['lastSwitchAt'] as String),
      providers:
          (json['providers'] as List<dynamic>?)
              ?.map(
                (e) => LlmProviderStatus.fromJson(e as Map<String, dynamic>),
              )
              .toList() ??
          const <LlmProviderStatus>[],
    );

Map<String, dynamic> _$$LlmAnalyticsImplToJson(_$LlmAnalyticsImpl instance) =>
    <String, dynamic>{
      'activeProviderId': instance.activeProviderId,
      'activeProviderName': instance.activeProviderName,
      'activeProviderMode': instance.activeProviderMode,
      'switches': instance.switches,
      'lastSwitchAt': instance.lastSwitchAt?.toIso8601String(),
      'providers': instance.providers,
    };
