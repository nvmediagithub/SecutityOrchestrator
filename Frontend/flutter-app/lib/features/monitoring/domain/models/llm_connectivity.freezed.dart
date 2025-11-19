// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'llm_connectivity.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

LlmConnectivity _$LlmConnectivityFromJson(Map<String, dynamic> json) {
  return _LlmConnectivity.fromJson(json);
}

/// @nodoc
mixin _$LlmConnectivity {
  String get providerId => throw _privateConstructorUsedError;
  String get providerName => throw _privateConstructorUsedError;
  bool get success => throw _privateConstructorUsedError;
  int get statusCode => throw _privateConstructorUsedError;
  int get latencyMs => throw _privateConstructorUsedError;
  String get endpoint => throw _privateConstructorUsedError;
  String get message => throw _privateConstructorUsedError;

  /// Serializes this LlmConnectivity to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of LlmConnectivity
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $LlmConnectivityCopyWith<LlmConnectivity> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $LlmConnectivityCopyWith<$Res> {
  factory $LlmConnectivityCopyWith(
    LlmConnectivity value,
    $Res Function(LlmConnectivity) then,
  ) = _$LlmConnectivityCopyWithImpl<$Res, LlmConnectivity>;
  @useResult
  $Res call({
    String providerId,
    String providerName,
    bool success,
    int statusCode,
    int latencyMs,
    String endpoint,
    String message,
  });
}

/// @nodoc
class _$LlmConnectivityCopyWithImpl<$Res, $Val extends LlmConnectivity>
    implements $LlmConnectivityCopyWith<$Res> {
  _$LlmConnectivityCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of LlmConnectivity
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? providerId = null,
    Object? providerName = null,
    Object? success = null,
    Object? statusCode = null,
    Object? latencyMs = null,
    Object? endpoint = null,
    Object? message = null,
  }) {
    return _then(
      _value.copyWith(
            providerId: null == providerId
                ? _value.providerId
                : providerId // ignore: cast_nullable_to_non_nullable
                      as String,
            providerName: null == providerName
                ? _value.providerName
                : providerName // ignore: cast_nullable_to_non_nullable
                      as String,
            success: null == success
                ? _value.success
                : success // ignore: cast_nullable_to_non_nullable
                      as bool,
            statusCode: null == statusCode
                ? _value.statusCode
                : statusCode // ignore: cast_nullable_to_non_nullable
                      as int,
            latencyMs: null == latencyMs
                ? _value.latencyMs
                : latencyMs // ignore: cast_nullable_to_non_nullable
                      as int,
            endpoint: null == endpoint
                ? _value.endpoint
                : endpoint // ignore: cast_nullable_to_non_nullable
                      as String,
            message: null == message
                ? _value.message
                : message // ignore: cast_nullable_to_non_nullable
                      as String,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$LlmConnectivityImplCopyWith<$Res>
    implements $LlmConnectivityCopyWith<$Res> {
  factory _$$LlmConnectivityImplCopyWith(
    _$LlmConnectivityImpl value,
    $Res Function(_$LlmConnectivityImpl) then,
  ) = __$$LlmConnectivityImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String providerId,
    String providerName,
    bool success,
    int statusCode,
    int latencyMs,
    String endpoint,
    String message,
  });
}

/// @nodoc
class __$$LlmConnectivityImplCopyWithImpl<$Res>
    extends _$LlmConnectivityCopyWithImpl<$Res, _$LlmConnectivityImpl>
    implements _$$LlmConnectivityImplCopyWith<$Res> {
  __$$LlmConnectivityImplCopyWithImpl(
    _$LlmConnectivityImpl _value,
    $Res Function(_$LlmConnectivityImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of LlmConnectivity
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? providerId = null,
    Object? providerName = null,
    Object? success = null,
    Object? statusCode = null,
    Object? latencyMs = null,
    Object? endpoint = null,
    Object? message = null,
  }) {
    return _then(
      _$LlmConnectivityImpl(
        providerId: null == providerId
            ? _value.providerId
            : providerId // ignore: cast_nullable_to_non_nullable
                  as String,
        providerName: null == providerName
            ? _value.providerName
            : providerName // ignore: cast_nullable_to_non_nullable
                  as String,
        success: null == success
            ? _value.success
            : success // ignore: cast_nullable_to_non_nullable
                  as bool,
        statusCode: null == statusCode
            ? _value.statusCode
            : statusCode // ignore: cast_nullable_to_non_nullable
                  as int,
        latencyMs: null == latencyMs
            ? _value.latencyMs
            : latencyMs // ignore: cast_nullable_to_non_nullable
                  as int,
        endpoint: null == endpoint
            ? _value.endpoint
            : endpoint // ignore: cast_nullable_to_non_nullable
                  as String,
        message: null == message
            ? _value.message
            : message // ignore: cast_nullable_to_non_nullable
                  as String,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$LlmConnectivityImpl implements _LlmConnectivity {
  const _$LlmConnectivityImpl({
    required this.providerId,
    required this.providerName,
    required this.success,
    required this.statusCode,
    required this.latencyMs,
    required this.endpoint,
    required this.message,
  });

  factory _$LlmConnectivityImpl.fromJson(Map<String, dynamic> json) =>
      _$$LlmConnectivityImplFromJson(json);

  @override
  final String providerId;
  @override
  final String providerName;
  @override
  final bool success;
  @override
  final int statusCode;
  @override
  final int latencyMs;
  @override
  final String endpoint;
  @override
  final String message;

  @override
  String toString() {
    return 'LlmConnectivity(providerId: $providerId, providerName: $providerName, success: $success, statusCode: $statusCode, latencyMs: $latencyMs, endpoint: $endpoint, message: $message)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$LlmConnectivityImpl &&
            (identical(other.providerId, providerId) ||
                other.providerId == providerId) &&
            (identical(other.providerName, providerName) ||
                other.providerName == providerName) &&
            (identical(other.success, success) || other.success == success) &&
            (identical(other.statusCode, statusCode) ||
                other.statusCode == statusCode) &&
            (identical(other.latencyMs, latencyMs) ||
                other.latencyMs == latencyMs) &&
            (identical(other.endpoint, endpoint) ||
                other.endpoint == endpoint) &&
            (identical(other.message, message) || other.message == message));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    providerId,
    providerName,
    success,
    statusCode,
    latencyMs,
    endpoint,
    message,
  );

  /// Create a copy of LlmConnectivity
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$LlmConnectivityImplCopyWith<_$LlmConnectivityImpl> get copyWith =>
      __$$LlmConnectivityImplCopyWithImpl<_$LlmConnectivityImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$LlmConnectivityImplToJson(this);
  }
}

abstract class _LlmConnectivity implements LlmConnectivity {
  const factory _LlmConnectivity({
    required final String providerId,
    required final String providerName,
    required final bool success,
    required final int statusCode,
    required final int latencyMs,
    required final String endpoint,
    required final String message,
  }) = _$LlmConnectivityImpl;

  factory _LlmConnectivity.fromJson(Map<String, dynamic> json) =
      _$LlmConnectivityImpl.fromJson;

  @override
  String get providerId;
  @override
  String get providerName;
  @override
  bool get success;
  @override
  int get statusCode;
  @override
  int get latencyMs;
  @override
  String get endpoint;
  @override
  String get message;

  /// Create a copy of LlmConnectivity
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$LlmConnectivityImplCopyWith<_$LlmConnectivityImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
