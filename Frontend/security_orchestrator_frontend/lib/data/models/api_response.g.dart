// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'api_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ApiResponse<T> _$ApiResponseFromJson<T>(
  Map<String, dynamic> json,
  T Function(Object? json) fromJsonT,
) => ApiResponse<T>(
  success: json['success'] as bool,
  data: _$nullableGenericFromJson(json['data'], fromJsonT),
  error: json['error'] == null
      ? null
      : ApiError.fromJson(json['error'] as Map<String, dynamic>),
  meta: json['meta'] == null
      ? null
      : ApiMeta.fromJson(json['meta'] as Map<String, dynamic>),
);

Map<String, dynamic> _$ApiResponseToJson<T>(
  ApiResponse<T> instance,
  Object? Function(T value) toJsonT,
) => <String, dynamic>{
  'success': instance.success,
  'data': _$nullableGenericToJson(instance.data, toJsonT),
  'error': instance.error,
  'meta': instance.meta,
};

T? _$nullableGenericFromJson<T>(
  Object? input,
  T Function(Object? json) fromJson,
) => input == null ? null : fromJson(input);

Object? _$nullableGenericToJson<T>(
  T? input,
  Object? Function(T value) toJson,
) => input == null ? null : toJson(input);

ApiError _$ApiErrorFromJson(Map<String, dynamic> json) => ApiError(
  code: json['code'] as String,
  message: json['message'] as String,
  details: (json['details'] as List<dynamic>)
      .map((e) => ApiErrorDetail.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$ApiErrorToJson(ApiError instance) => <String, dynamic>{
  'code': instance.code,
  'message': instance.message,
  'details': instance.details,
};

ApiErrorDetail _$ApiErrorDetailFromJson(Map<String, dynamic> json) =>
    ApiErrorDetail(
      field: json['field'] as String,
      message: json['message'] as String,
      code: json['code'] as String,
    );

Map<String, dynamic> _$ApiErrorDetailToJson(ApiErrorDetail instance) =>
    <String, dynamic>{
      'field': instance.field,
      'message': instance.message,
      'code': instance.code,
    };

ApiMeta _$ApiMetaFromJson(Map<String, dynamic> json) => ApiMeta(
  requestId: json['requestId'] as String,
  timestamp: DateTime.parse(json['timestamp'] as String),
);

Map<String, dynamic> _$ApiMetaToJson(ApiMeta instance) => <String, dynamic>{
  'requestId': instance.requestId,
  'timestamp': instance.timestamp.toIso8601String(),
};
