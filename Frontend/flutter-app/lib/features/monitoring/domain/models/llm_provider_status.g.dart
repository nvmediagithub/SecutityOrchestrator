// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'llm_provider_status.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$LlmProviderStatusImpl _$$LlmProviderStatusImplFromJson(
  Map<String, dynamic> json,
) => _$LlmProviderStatusImpl(
  id: json['id'] as String,
  displayName: json['displayName'] as String,
  mode: json['mode'] as String,
  baseUrl: json['baseUrl'] as String,
  available: json['available'] as bool,
  requiresApiKey: json['requiresApiKey'] as bool,
  active: json['active'] as bool? ?? false,
);

Map<String, dynamic> _$$LlmProviderStatusImplToJson(
  _$LlmProviderStatusImpl instance,
) => <String, dynamic>{
  'id': instance.id,
  'displayName': instance.displayName,
  'mode': instance.mode,
  'baseUrl': instance.baseUrl,
  'available': instance.available,
  'requiresApiKey': instance.requiresApiKey,
  'active': instance.active,
};
