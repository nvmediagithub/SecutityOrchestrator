// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'openapi_validation_issue.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

OpenApiValidationIssue _$OpenApiValidationIssueFromJson(
  Map<String, dynamic> json,
) {
  return _OpenApiValidationIssue.fromJson(json);
}

/// @nodoc
mixin _$OpenApiValidationIssue {
  String? get path => throw _privateConstructorUsedError;
  String get message => throw _privateConstructorUsedError;
  String? get severity => throw _privateConstructorUsedError;
  String? get errorType => throw _privateConstructorUsedError;
  int? get lineNumber => throw _privateConstructorUsedError;
  int? get columnNumber => throw _privateConstructorUsedError;

  /// Serializes this OpenApiValidationIssue to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of OpenApiValidationIssue
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $OpenApiValidationIssueCopyWith<OpenApiValidationIssue> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $OpenApiValidationIssueCopyWith<$Res> {
  factory $OpenApiValidationIssueCopyWith(
    OpenApiValidationIssue value,
    $Res Function(OpenApiValidationIssue) then,
  ) = _$OpenApiValidationIssueCopyWithImpl<$Res, OpenApiValidationIssue>;
  @useResult
  $Res call({
    String? path,
    String message,
    String? severity,
    String? errorType,
    int? lineNumber,
    int? columnNumber,
  });
}

/// @nodoc
class _$OpenApiValidationIssueCopyWithImpl<
  $Res,
  $Val extends OpenApiValidationIssue
>
    implements $OpenApiValidationIssueCopyWith<$Res> {
  _$OpenApiValidationIssueCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of OpenApiValidationIssue
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? path = freezed,
    Object? message = null,
    Object? severity = freezed,
    Object? errorType = freezed,
    Object? lineNumber = freezed,
    Object? columnNumber = freezed,
  }) {
    return _then(
      _value.copyWith(
            path: freezed == path
                ? _value.path
                : path // ignore: cast_nullable_to_non_nullable
                      as String?,
            message: null == message
                ? _value.message
                : message // ignore: cast_nullable_to_non_nullable
                      as String,
            severity: freezed == severity
                ? _value.severity
                : severity // ignore: cast_nullable_to_non_nullable
                      as String?,
            errorType: freezed == errorType
                ? _value.errorType
                : errorType // ignore: cast_nullable_to_non_nullable
                      as String?,
            lineNumber: freezed == lineNumber
                ? _value.lineNumber
                : lineNumber // ignore: cast_nullable_to_non_nullable
                      as int?,
            columnNumber: freezed == columnNumber
                ? _value.columnNumber
                : columnNumber // ignore: cast_nullable_to_non_nullable
                      as int?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$OpenApiValidationIssueImplCopyWith<$Res>
    implements $OpenApiValidationIssueCopyWith<$Res> {
  factory _$$OpenApiValidationIssueImplCopyWith(
    _$OpenApiValidationIssueImpl value,
    $Res Function(_$OpenApiValidationIssueImpl) then,
  ) = __$$OpenApiValidationIssueImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String? path,
    String message,
    String? severity,
    String? errorType,
    int? lineNumber,
    int? columnNumber,
  });
}

/// @nodoc
class __$$OpenApiValidationIssueImplCopyWithImpl<$Res>
    extends
        _$OpenApiValidationIssueCopyWithImpl<$Res, _$OpenApiValidationIssueImpl>
    implements _$$OpenApiValidationIssueImplCopyWith<$Res> {
  __$$OpenApiValidationIssueImplCopyWithImpl(
    _$OpenApiValidationIssueImpl _value,
    $Res Function(_$OpenApiValidationIssueImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of OpenApiValidationIssue
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? path = freezed,
    Object? message = null,
    Object? severity = freezed,
    Object? errorType = freezed,
    Object? lineNumber = freezed,
    Object? columnNumber = freezed,
  }) {
    return _then(
      _$OpenApiValidationIssueImpl(
        path: freezed == path
            ? _value.path
            : path // ignore: cast_nullable_to_non_nullable
                  as String?,
        message: null == message
            ? _value.message
            : message // ignore: cast_nullable_to_non_nullable
                  as String,
        severity: freezed == severity
            ? _value.severity
            : severity // ignore: cast_nullable_to_non_nullable
                  as String?,
        errorType: freezed == errorType
            ? _value.errorType
            : errorType // ignore: cast_nullable_to_non_nullable
                  as String?,
        lineNumber: freezed == lineNumber
            ? _value.lineNumber
            : lineNumber // ignore: cast_nullable_to_non_nullable
                  as int?,
        columnNumber: freezed == columnNumber
            ? _value.columnNumber
            : columnNumber // ignore: cast_nullable_to_non_nullable
                  as int?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$OpenApiValidationIssueImpl implements _OpenApiValidationIssue {
  const _$OpenApiValidationIssueImpl({
    this.path,
    required this.message,
    this.severity,
    this.errorType,
    this.lineNumber,
    this.columnNumber,
  });

  factory _$OpenApiValidationIssueImpl.fromJson(Map<String, dynamic> json) =>
      _$$OpenApiValidationIssueImplFromJson(json);

  @override
  final String? path;
  @override
  final String message;
  @override
  final String? severity;
  @override
  final String? errorType;
  @override
  final int? lineNumber;
  @override
  final int? columnNumber;

  @override
  String toString() {
    return 'OpenApiValidationIssue(path: $path, message: $message, severity: $severity, errorType: $errorType, lineNumber: $lineNumber, columnNumber: $columnNumber)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$OpenApiValidationIssueImpl &&
            (identical(other.path, path) || other.path == path) &&
            (identical(other.message, message) || other.message == message) &&
            (identical(other.severity, severity) ||
                other.severity == severity) &&
            (identical(other.errorType, errorType) ||
                other.errorType == errorType) &&
            (identical(other.lineNumber, lineNumber) ||
                other.lineNumber == lineNumber) &&
            (identical(other.columnNumber, columnNumber) ||
                other.columnNumber == columnNumber));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    path,
    message,
    severity,
    errorType,
    lineNumber,
    columnNumber,
  );

  /// Create a copy of OpenApiValidationIssue
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$OpenApiValidationIssueImplCopyWith<_$OpenApiValidationIssueImpl>
  get copyWith =>
      __$$OpenApiValidationIssueImplCopyWithImpl<_$OpenApiValidationIssueImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$OpenApiValidationIssueImplToJson(this);
  }
}

abstract class _OpenApiValidationIssue implements OpenApiValidationIssue {
  const factory _OpenApiValidationIssue({
    final String? path,
    required final String message,
    final String? severity,
    final String? errorType,
    final int? lineNumber,
    final int? columnNumber,
  }) = _$OpenApiValidationIssueImpl;

  factory _OpenApiValidationIssue.fromJson(Map<String, dynamic> json) =
      _$OpenApiValidationIssueImpl.fromJson;

  @override
  String? get path;
  @override
  String get message;
  @override
  String? get severity;
  @override
  String? get errorType;
  @override
  int? get lineNumber;
  @override
  int? get columnNumber;

  /// Create a copy of OpenApiValidationIssue
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$OpenApiValidationIssueImplCopyWith<_$OpenApiValidationIssueImpl>
  get copyWith => throw _privateConstructorUsedError;
}
