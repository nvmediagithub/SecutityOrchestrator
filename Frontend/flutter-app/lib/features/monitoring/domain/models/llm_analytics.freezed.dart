// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'llm_analytics.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

LlmAnalytics _$LlmAnalyticsFromJson(Map<String, dynamic> json) {
  return _LlmAnalytics.fromJson(json);
}

/// @nodoc
mixin _$LlmAnalytics {
  String get activeProviderId => throw _privateConstructorUsedError;
  String? get activeProviderName => throw _privateConstructorUsedError;
  String? get activeProviderMode => throw _privateConstructorUsedError;
  int get switches => throw _privateConstructorUsedError;
  DateTime? get lastSwitchAt => throw _privateConstructorUsedError;
  List<LlmProviderStatus> get providers => throw _privateConstructorUsedError;

  /// Serializes this LlmAnalytics to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of LlmAnalytics
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $LlmAnalyticsCopyWith<LlmAnalytics> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $LlmAnalyticsCopyWith<$Res> {
  factory $LlmAnalyticsCopyWith(
    LlmAnalytics value,
    $Res Function(LlmAnalytics) then,
  ) = _$LlmAnalyticsCopyWithImpl<$Res, LlmAnalytics>;
  @useResult
  $Res call({
    String activeProviderId,
    String? activeProviderName,
    String? activeProviderMode,
    int switches,
    DateTime? lastSwitchAt,
    List<LlmProviderStatus> providers,
  });
}

/// @nodoc
class _$LlmAnalyticsCopyWithImpl<$Res, $Val extends LlmAnalytics>
    implements $LlmAnalyticsCopyWith<$Res> {
  _$LlmAnalyticsCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of LlmAnalytics
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? activeProviderId = null,
    Object? activeProviderName = freezed,
    Object? activeProviderMode = freezed,
    Object? switches = null,
    Object? lastSwitchAt = freezed,
    Object? providers = null,
  }) {
    return _then(
      _value.copyWith(
            activeProviderId: null == activeProviderId
                ? _value.activeProviderId
                : activeProviderId // ignore: cast_nullable_to_non_nullable
                      as String,
            activeProviderName: freezed == activeProviderName
                ? _value.activeProviderName
                : activeProviderName // ignore: cast_nullable_to_non_nullable
                      as String?,
            activeProviderMode: freezed == activeProviderMode
                ? _value.activeProviderMode
                : activeProviderMode // ignore: cast_nullable_to_non_nullable
                      as String?,
            switches: null == switches
                ? _value.switches
                : switches // ignore: cast_nullable_to_non_nullable
                      as int,
            lastSwitchAt: freezed == lastSwitchAt
                ? _value.lastSwitchAt
                : lastSwitchAt // ignore: cast_nullable_to_non_nullable
                      as DateTime?,
            providers: null == providers
                ? _value.providers
                : providers // ignore: cast_nullable_to_non_nullable
                      as List<LlmProviderStatus>,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$LlmAnalyticsImplCopyWith<$Res>
    implements $LlmAnalyticsCopyWith<$Res> {
  factory _$$LlmAnalyticsImplCopyWith(
    _$LlmAnalyticsImpl value,
    $Res Function(_$LlmAnalyticsImpl) then,
  ) = __$$LlmAnalyticsImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String activeProviderId,
    String? activeProviderName,
    String? activeProviderMode,
    int switches,
    DateTime? lastSwitchAt,
    List<LlmProviderStatus> providers,
  });
}

/// @nodoc
class __$$LlmAnalyticsImplCopyWithImpl<$Res>
    extends _$LlmAnalyticsCopyWithImpl<$Res, _$LlmAnalyticsImpl>
    implements _$$LlmAnalyticsImplCopyWith<$Res> {
  __$$LlmAnalyticsImplCopyWithImpl(
    _$LlmAnalyticsImpl _value,
    $Res Function(_$LlmAnalyticsImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of LlmAnalytics
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? activeProviderId = null,
    Object? activeProviderName = freezed,
    Object? activeProviderMode = freezed,
    Object? switches = null,
    Object? lastSwitchAt = freezed,
    Object? providers = null,
  }) {
    return _then(
      _$LlmAnalyticsImpl(
        activeProviderId: null == activeProviderId
            ? _value.activeProviderId
            : activeProviderId // ignore: cast_nullable_to_non_nullable
                  as String,
        activeProviderName: freezed == activeProviderName
            ? _value.activeProviderName
            : activeProviderName // ignore: cast_nullable_to_non_nullable
                  as String?,
        activeProviderMode: freezed == activeProviderMode
            ? _value.activeProviderMode
            : activeProviderMode // ignore: cast_nullable_to_non_nullable
                  as String?,
        switches: null == switches
            ? _value.switches
            : switches // ignore: cast_nullable_to_non_nullable
                  as int,
        lastSwitchAt: freezed == lastSwitchAt
            ? _value.lastSwitchAt
            : lastSwitchAt // ignore: cast_nullable_to_non_nullable
                  as DateTime?,
        providers: null == providers
            ? _value._providers
            : providers // ignore: cast_nullable_to_non_nullable
                  as List<LlmProviderStatus>,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$LlmAnalyticsImpl implements _LlmAnalytics {
  const _$LlmAnalyticsImpl({
    required this.activeProviderId,
    this.activeProviderName,
    this.activeProviderMode,
    this.switches = 0,
    this.lastSwitchAt,
    final List<LlmProviderStatus> providers = const <LlmProviderStatus>[],
  }) : _providers = providers;

  factory _$LlmAnalyticsImpl.fromJson(Map<String, dynamic> json) =>
      _$$LlmAnalyticsImplFromJson(json);

  @override
  final String activeProviderId;
  @override
  final String? activeProviderName;
  @override
  final String? activeProviderMode;
  @override
  @JsonKey()
  final int switches;
  @override
  final DateTime? lastSwitchAt;
  final List<LlmProviderStatus> _providers;
  @override
  @JsonKey()
  List<LlmProviderStatus> get providers {
    if (_providers is EqualUnmodifiableListView) return _providers;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_providers);
  }

  @override
  String toString() {
    return 'LlmAnalytics(activeProviderId: $activeProviderId, activeProviderName: $activeProviderName, activeProviderMode: $activeProviderMode, switches: $switches, lastSwitchAt: $lastSwitchAt, providers: $providers)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$LlmAnalyticsImpl &&
            (identical(other.activeProviderId, activeProviderId) ||
                other.activeProviderId == activeProviderId) &&
            (identical(other.activeProviderName, activeProviderName) ||
                other.activeProviderName == activeProviderName) &&
            (identical(other.activeProviderMode, activeProviderMode) ||
                other.activeProviderMode == activeProviderMode) &&
            (identical(other.switches, switches) ||
                other.switches == switches) &&
            (identical(other.lastSwitchAt, lastSwitchAt) ||
                other.lastSwitchAt == lastSwitchAt) &&
            const DeepCollectionEquality().equals(
              other._providers,
              _providers,
            ));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    activeProviderId,
    activeProviderName,
    activeProviderMode,
    switches,
    lastSwitchAt,
    const DeepCollectionEquality().hash(_providers),
  );

  /// Create a copy of LlmAnalytics
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$LlmAnalyticsImplCopyWith<_$LlmAnalyticsImpl> get copyWith =>
      __$$LlmAnalyticsImplCopyWithImpl<_$LlmAnalyticsImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$LlmAnalyticsImplToJson(this);
  }
}

abstract class _LlmAnalytics implements LlmAnalytics {
  const factory _LlmAnalytics({
    required final String activeProviderId,
    final String? activeProviderName,
    final String? activeProviderMode,
    final int switches,
    final DateTime? lastSwitchAt,
    final List<LlmProviderStatus> providers,
  }) = _$LlmAnalyticsImpl;

  factory _LlmAnalytics.fromJson(Map<String, dynamic> json) =
      _$LlmAnalyticsImpl.fromJson;

  @override
  String get activeProviderId;
  @override
  String? get activeProviderName;
  @override
  String? get activeProviderMode;
  @override
  int get switches;
  @override
  DateTime? get lastSwitchAt;
  @override
  List<LlmProviderStatus> get providers;

  /// Create a copy of LlmAnalytics
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$LlmAnalyticsImplCopyWith<_$LlmAnalyticsImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
