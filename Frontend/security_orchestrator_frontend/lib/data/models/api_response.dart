import 'package:json_annotation/json_annotation.dart';

part 'api_response.g.dart';

@JsonSerializable(genericArgumentFactories: true)
class ApiResponse<T> {
  final bool success;
  final T? data;
  final ApiError? error;
  final ApiMeta? meta;

  ApiResponse({
    required this.success,
    this.data,
    this.error,
    this.meta,
  });

  factory ApiResponse.fromJson(
    Map<String, dynamic> json,
    T Function(Object? json) fromJsonT,
  ) =>
      _$ApiResponseFromJson(json, fromJsonT);

  Map<String, dynamic> toJson(Object? Function(T value) toJsonT) =>
      _$ApiResponseToJson(this, toJsonT);
}

@JsonSerializable()
class ApiError {
  final String code;
  final String message;
  final List<ApiErrorDetail> details;

  ApiError({
    required this.code,
    required this.message,
    required this.details,
  });

  factory ApiError.fromJson(Map<String, dynamic> json) =>
      _$ApiErrorFromJson(json);

  Map<String, dynamic> toJson() => _$ApiErrorToJson(this);
}

@JsonSerializable()
class ApiErrorDetail {
  final String field;
  final String message;
  final String code;

  ApiErrorDetail({
    required this.field,
    required this.message,
    required this.code,
  });

  factory ApiErrorDetail.fromJson(Map<String, dynamic> json) =>
      _$ApiErrorDetailFromJson(json);

  Map<String, dynamic> toJson() => _$ApiErrorDetailToJson(this);
}

@JsonSerializable()
class ApiMeta {
  final String requestId;
  final DateTime timestamp;

  ApiMeta({
    required this.requestId,
    required this.timestamp,
  });

  factory ApiMeta.fromJson(Map<String, dynamic> json) =>
      _$ApiMetaFromJson(json);

  Map<String, dynamic> toJson() => _$ApiMetaToJson(this);
}