// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'llm_connectivity.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$LlmConnectivityImpl _$$LlmConnectivityImplFromJson(
  Map<String, dynamic> json,
) => _$LlmConnectivityImpl(
  providerId: json['providerId'] as String,
  providerName: json['providerName'] as String,
  success: json['success'] as bool,
  statusCode: (json['statusCode'] as num).toInt(),
  latencyMs: (json['latencyMs'] as num).toInt(),
  endpoint: json['endpoint'] as String,
  message: json['message'] as String,
);

Map<String, dynamic> _$$LlmConnectivityImplToJson(
  _$LlmConnectivityImpl instance,
) => <String, dynamic>{
  'providerId': instance.providerId,
  'providerName': instance.providerName,
  'success': instance.success,
  'statusCode': instance.statusCode,
  'latencyMs': instance.latencyMs,
  'endpoint': instance.endpoint,
  'message': instance.message,
};
