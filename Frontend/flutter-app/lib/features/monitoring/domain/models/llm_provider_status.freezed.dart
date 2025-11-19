// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'llm_provider_status.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

LlmProviderStatus _$LlmProviderStatusFromJson(Map<String, dynamic> json) {
  return _LlmProviderStatus.fromJson(json);
}

/// @nodoc
mixin _$LlmProviderStatus {
  String get id => throw _privateConstructorUsedError;
  String get displayName => throw _privateConstructorUsedError;
  String get mode => throw _privateConstructorUsedError;
  String get baseUrl => throw _privateConstructorUsedError;
  bool get available => throw _privateConstructorUsedError;
  bool get requiresApiKey => throw _privateConstructorUsedError;
  bool get active => throw _privateConstructorUsedError;

  /// Serializes this LlmProviderStatus to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of LlmProviderStatus
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $LlmProviderStatusCopyWith<LlmProviderStatus> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $LlmProviderStatusCopyWith<$Res> {
  factory $LlmProviderStatusCopyWith(
    LlmProviderStatus value,
    $Res Function(LlmProviderStatus) then,
  ) = _$LlmProviderStatusCopyWithImpl<$Res, LlmProviderStatus>;
  @useResult
  $Res call({
    String id,
    String displayName,
    String mode,
    String baseUrl,
    bool available,
    bool requiresApiKey,
    bool active,
  });
}

/// @nodoc
class _$LlmProviderStatusCopyWithImpl<$Res, $Val extends LlmProviderStatus>
    implements $LlmProviderStatusCopyWith<$Res> {
  _$LlmProviderStatusCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of LlmProviderStatus
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? displayName = null,
    Object? mode = null,
    Object? baseUrl = null,
    Object? available = null,
    Object? requiresApiKey = null,
    Object? active = null,
  }) {
    return _then(
      _value.copyWith(
            id: null == id
                ? _value.id
                : id // ignore: cast_nullable_to_non_nullable
                      as String,
            displayName: null == displayName
                ? _value.displayName
                : displayName // ignore: cast_nullable_to_non_nullable
                      as String,
            mode: null == mode
                ? _value.mode
                : mode // ignore: cast_nullable_to_non_nullable
                      as String,
            baseUrl: null == baseUrl
                ? _value.baseUrl
                : baseUrl // ignore: cast_nullable_to_non_nullable
                      as String,
            available: null == available
                ? _value.available
                : available // ignore: cast_nullable_to_non_nullable
                      as bool,
            requiresApiKey: null == requiresApiKey
                ? _value.requiresApiKey
                : requiresApiKey // ignore: cast_nullable_to_non_nullable
                      as bool,
            active: null == active
                ? _value.active
                : active // ignore: cast_nullable_to_non_nullable
                      as bool,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$LlmProviderStatusImplCopyWith<$Res>
    implements $LlmProviderStatusCopyWith<$Res> {
  factory _$$LlmProviderStatusImplCopyWith(
    _$LlmProviderStatusImpl value,
    $Res Function(_$LlmProviderStatusImpl) then,
  ) = __$$LlmProviderStatusImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String id,
    String displayName,
    String mode,
    String baseUrl,
    bool available,
    bool requiresApiKey,
    bool active,
  });
}

/// @nodoc
class __$$LlmProviderStatusImplCopyWithImpl<$Res>
    extends _$LlmProviderStatusCopyWithImpl<$Res, _$LlmProviderStatusImpl>
    implements _$$LlmProviderStatusImplCopyWith<$Res> {
  __$$LlmProviderStatusImplCopyWithImpl(
    _$LlmProviderStatusImpl _value,
    $Res Function(_$LlmProviderStatusImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of LlmProviderStatus
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? displayName = null,
    Object? mode = null,
    Object? baseUrl = null,
    Object? available = null,
    Object? requiresApiKey = null,
    Object? active = null,
  }) {
    return _then(
      _$LlmProviderStatusImpl(
        id: null == id
            ? _value.id
            : id // ignore: cast_nullable_to_non_nullable
                  as String,
        displayName: null == displayName
            ? _value.displayName
            : displayName // ignore: cast_nullable_to_non_nullable
                  as String,
        mode: null == mode
            ? _value.mode
            : mode // ignore: cast_nullable_to_non_nullable
                  as String,
        baseUrl: null == baseUrl
            ? _value.baseUrl
            : baseUrl // ignore: cast_nullable_to_non_nullable
                  as String,
        available: null == available
            ? _value.available
            : available // ignore: cast_nullable_to_non_nullable
                  as bool,
        requiresApiKey: null == requiresApiKey
            ? _value.requiresApiKey
            : requiresApiKey // ignore: cast_nullable_to_non_nullable
                  as bool,
        active: null == active
            ? _value.active
            : active // ignore: cast_nullable_to_non_nullable
                  as bool,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$LlmProviderStatusImpl implements _LlmProviderStatus {
  const _$LlmProviderStatusImpl({
    required this.id,
    required this.displayName,
    required this.mode,
    required this.baseUrl,
    required this.available,
    required this.requiresApiKey,
    this.active = false,
  });

  factory _$LlmProviderStatusImpl.fromJson(Map<String, dynamic> json) =>
      _$$LlmProviderStatusImplFromJson(json);

  @override
  final String id;
  @override
  final String displayName;
  @override
  final String mode;
  @override
  final String baseUrl;
  @override
  final bool available;
  @override
  final bool requiresApiKey;
  @override
  @JsonKey()
  final bool active;

  @override
  String toString() {
    return 'LlmProviderStatus(id: $id, displayName: $displayName, mode: $mode, baseUrl: $baseUrl, available: $available, requiresApiKey: $requiresApiKey, active: $active)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$LlmProviderStatusImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.displayName, displayName) ||
                other.displayName == displayName) &&
            (identical(other.mode, mode) || other.mode == mode) &&
            (identical(other.baseUrl, baseUrl) || other.baseUrl == baseUrl) &&
            (identical(other.available, available) ||
                other.available == available) &&
            (identical(other.requiresApiKey, requiresApiKey) ||
                other.requiresApiKey == requiresApiKey) &&
            (identical(other.active, active) || other.active == active));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    id,
    displayName,
    mode,
    baseUrl,
    available,
    requiresApiKey,
    active,
  );

  /// Create a copy of LlmProviderStatus
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$LlmProviderStatusImplCopyWith<_$LlmProviderStatusImpl> get copyWith =>
      __$$LlmProviderStatusImplCopyWithImpl<_$LlmProviderStatusImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$LlmProviderStatusImplToJson(this);
  }
}

abstract class _LlmProviderStatus implements LlmProviderStatus {
  const factory _LlmProviderStatus({
    required final String id,
    required final String displayName,
    required final String mode,
    required final String baseUrl,
    required final bool available,
    required final bool requiresApiKey,
    final bool active,
  }) = _$LlmProviderStatusImpl;

  factory _LlmProviderStatus.fromJson(Map<String, dynamic> json) =
      _$LlmProviderStatusImpl.fromJson;

  @override
  String get id;
  @override
  String get displayName;
  @override
  String get mode;
  @override
  String get baseUrl;
  @override
  bool get available;
  @override
  bool get requiresApiKey;
  @override
  bool get active;

  /// Create a copy of LlmProviderStatus
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$LlmProviderStatusImplCopyWith<_$LlmProviderStatusImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
