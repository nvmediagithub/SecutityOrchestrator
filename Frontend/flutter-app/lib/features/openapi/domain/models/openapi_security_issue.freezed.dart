// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'openapi_security_issue.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

OpenApiSecurityIssue _$OpenApiSecurityIssueFromJson(Map<String, dynamic> json) {
  return _OpenApiSecurityIssue.fromJson(json);
}

/// @nodoc
mixin _$OpenApiSecurityIssue {
  String get type => throw _privateConstructorUsedError;
  String get severity => throw _privateConstructorUsedError;
  String get description => throw _privateConstructorUsedError;
  String? get location => throw _privateConstructorUsedError;

  /// Serializes this OpenApiSecurityIssue to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of OpenApiSecurityIssue
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $OpenApiSecurityIssueCopyWith<OpenApiSecurityIssue> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $OpenApiSecurityIssueCopyWith<$Res> {
  factory $OpenApiSecurityIssueCopyWith(
    OpenApiSecurityIssue value,
    $Res Function(OpenApiSecurityIssue) then,
  ) = _$OpenApiSecurityIssueCopyWithImpl<$Res, OpenApiSecurityIssue>;
  @useResult
  $Res call({
    String type,
    String severity,
    String description,
    String? location,
  });
}

/// @nodoc
class _$OpenApiSecurityIssueCopyWithImpl<
  $Res,
  $Val extends OpenApiSecurityIssue
>
    implements $OpenApiSecurityIssueCopyWith<$Res> {
  _$OpenApiSecurityIssueCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of OpenApiSecurityIssue
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? type = null,
    Object? severity = null,
    Object? description = null,
    Object? location = freezed,
  }) {
    return _then(
      _value.copyWith(
            type: null == type
                ? _value.type
                : type // ignore: cast_nullable_to_non_nullable
                      as String,
            severity: null == severity
                ? _value.severity
                : severity // ignore: cast_nullable_to_non_nullable
                      as String,
            description: null == description
                ? _value.description
                : description // ignore: cast_nullable_to_non_nullable
                      as String,
            location: freezed == location
                ? _value.location
                : location // ignore: cast_nullable_to_non_nullable
                      as String?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$OpenApiSecurityIssueImplCopyWith<$Res>
    implements $OpenApiSecurityIssueCopyWith<$Res> {
  factory _$$OpenApiSecurityIssueImplCopyWith(
    _$OpenApiSecurityIssueImpl value,
    $Res Function(_$OpenApiSecurityIssueImpl) then,
  ) = __$$OpenApiSecurityIssueImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String type,
    String severity,
    String description,
    String? location,
  });
}

/// @nodoc
class __$$OpenApiSecurityIssueImplCopyWithImpl<$Res>
    extends _$OpenApiSecurityIssueCopyWithImpl<$Res, _$OpenApiSecurityIssueImpl>
    implements _$$OpenApiSecurityIssueImplCopyWith<$Res> {
  __$$OpenApiSecurityIssueImplCopyWithImpl(
    _$OpenApiSecurityIssueImpl _value,
    $Res Function(_$OpenApiSecurityIssueImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of OpenApiSecurityIssue
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? type = null,
    Object? severity = null,
    Object? description = null,
    Object? location = freezed,
  }) {
    return _then(
      _$OpenApiSecurityIssueImpl(
        type: null == type
            ? _value.type
            : type // ignore: cast_nullable_to_non_nullable
                  as String,
        severity: null == severity
            ? _value.severity
            : severity // ignore: cast_nullable_to_non_nullable
                  as String,
        description: null == description
            ? _value.description
            : description // ignore: cast_nullable_to_non_nullable
                  as String,
        location: freezed == location
            ? _value.location
            : location // ignore: cast_nullable_to_non_nullable
                  as String?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$OpenApiSecurityIssueImpl implements _OpenApiSecurityIssue {
  const _$OpenApiSecurityIssueImpl({
    required this.type,
    required this.severity,
    required this.description,
    this.location,
  });

  factory _$OpenApiSecurityIssueImpl.fromJson(Map<String, dynamic> json) =>
      _$$OpenApiSecurityIssueImplFromJson(json);

  @override
  final String type;
  @override
  final String severity;
  @override
  final String description;
  @override
  final String? location;

  @override
  String toString() {
    return 'OpenApiSecurityIssue(type: $type, severity: $severity, description: $description, location: $location)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$OpenApiSecurityIssueImpl &&
            (identical(other.type, type) || other.type == type) &&
            (identical(other.severity, severity) ||
                other.severity == severity) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.location, location) ||
                other.location == location));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode =>
      Object.hash(runtimeType, type, severity, description, location);

  /// Create a copy of OpenApiSecurityIssue
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$OpenApiSecurityIssueImplCopyWith<_$OpenApiSecurityIssueImpl>
  get copyWith =>
      __$$OpenApiSecurityIssueImplCopyWithImpl<_$OpenApiSecurityIssueImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$OpenApiSecurityIssueImplToJson(this);
  }
}

abstract class _OpenApiSecurityIssue implements OpenApiSecurityIssue {
  const factory _OpenApiSecurityIssue({
    required final String type,
    required final String severity,
    required final String description,
    final String? location,
  }) = _$OpenApiSecurityIssueImpl;

  factory _OpenApiSecurityIssue.fromJson(Map<String, dynamic> json) =
      _$OpenApiSecurityIssueImpl.fromJson;

  @override
  String get type;
  @override
  String get severity;
  @override
  String get description;
  @override
  String? get location;

  /// Create a copy of OpenApiSecurityIssue
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$OpenApiSecurityIssueImplCopyWith<_$OpenApiSecurityIssueImpl>
  get copyWith => throw _privateConstructorUsedError;
}
