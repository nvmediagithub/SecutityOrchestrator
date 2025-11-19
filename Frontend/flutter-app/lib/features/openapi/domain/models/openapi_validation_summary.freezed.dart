// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'openapi_validation_summary.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

OpenApiValidationSummary _$OpenApiValidationSummaryFromJson(
  Map<String, dynamic> json,
) {
  return _OpenApiValidationSummary.fromJson(json);
}

/// @nodoc
mixin _$OpenApiValidationSummary {
  int get totalChecks => throw _privateConstructorUsedError;
  int get passedChecks => throw _privateConstructorUsedError;
  int get failedChecks => throw _privateConstructorUsedError;
  int get warningChecks => throw _privateConstructorUsedError;
  int get validationTimeMs => throw _privateConstructorUsedError;
  double get successRate => throw _privateConstructorUsedError;

  /// Serializes this OpenApiValidationSummary to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of OpenApiValidationSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $OpenApiValidationSummaryCopyWith<OpenApiValidationSummary> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $OpenApiValidationSummaryCopyWith<$Res> {
  factory $OpenApiValidationSummaryCopyWith(
    OpenApiValidationSummary value,
    $Res Function(OpenApiValidationSummary) then,
  ) = _$OpenApiValidationSummaryCopyWithImpl<$Res, OpenApiValidationSummary>;
  @useResult
  $Res call({
    int totalChecks,
    int passedChecks,
    int failedChecks,
    int warningChecks,
    int validationTimeMs,
    double successRate,
  });
}

/// @nodoc
class _$OpenApiValidationSummaryCopyWithImpl<
  $Res,
  $Val extends OpenApiValidationSummary
>
    implements $OpenApiValidationSummaryCopyWith<$Res> {
  _$OpenApiValidationSummaryCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of OpenApiValidationSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? totalChecks = null,
    Object? passedChecks = null,
    Object? failedChecks = null,
    Object? warningChecks = null,
    Object? validationTimeMs = null,
    Object? successRate = null,
  }) {
    return _then(
      _value.copyWith(
            totalChecks: null == totalChecks
                ? _value.totalChecks
                : totalChecks // ignore: cast_nullable_to_non_nullable
                      as int,
            passedChecks: null == passedChecks
                ? _value.passedChecks
                : passedChecks // ignore: cast_nullable_to_non_nullable
                      as int,
            failedChecks: null == failedChecks
                ? _value.failedChecks
                : failedChecks // ignore: cast_nullable_to_non_nullable
                      as int,
            warningChecks: null == warningChecks
                ? _value.warningChecks
                : warningChecks // ignore: cast_nullable_to_non_nullable
                      as int,
            validationTimeMs: null == validationTimeMs
                ? _value.validationTimeMs
                : validationTimeMs // ignore: cast_nullable_to_non_nullable
                      as int,
            successRate: null == successRate
                ? _value.successRate
                : successRate // ignore: cast_nullable_to_non_nullable
                      as double,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$OpenApiValidationSummaryImplCopyWith<$Res>
    implements $OpenApiValidationSummaryCopyWith<$Res> {
  factory _$$OpenApiValidationSummaryImplCopyWith(
    _$OpenApiValidationSummaryImpl value,
    $Res Function(_$OpenApiValidationSummaryImpl) then,
  ) = __$$OpenApiValidationSummaryImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int totalChecks,
    int passedChecks,
    int failedChecks,
    int warningChecks,
    int validationTimeMs,
    double successRate,
  });
}

/// @nodoc
class __$$OpenApiValidationSummaryImplCopyWithImpl<$Res>
    extends
        _$OpenApiValidationSummaryCopyWithImpl<
          $Res,
          _$OpenApiValidationSummaryImpl
        >
    implements _$$OpenApiValidationSummaryImplCopyWith<$Res> {
  __$$OpenApiValidationSummaryImplCopyWithImpl(
    _$OpenApiValidationSummaryImpl _value,
    $Res Function(_$OpenApiValidationSummaryImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of OpenApiValidationSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? totalChecks = null,
    Object? passedChecks = null,
    Object? failedChecks = null,
    Object? warningChecks = null,
    Object? validationTimeMs = null,
    Object? successRate = null,
  }) {
    return _then(
      _$OpenApiValidationSummaryImpl(
        totalChecks: null == totalChecks
            ? _value.totalChecks
            : totalChecks // ignore: cast_nullable_to_non_nullable
                  as int,
        passedChecks: null == passedChecks
            ? _value.passedChecks
            : passedChecks // ignore: cast_nullable_to_non_nullable
                  as int,
        failedChecks: null == failedChecks
            ? _value.failedChecks
            : failedChecks // ignore: cast_nullable_to_non_nullable
                  as int,
        warningChecks: null == warningChecks
            ? _value.warningChecks
            : warningChecks // ignore: cast_nullable_to_non_nullable
                  as int,
        validationTimeMs: null == validationTimeMs
            ? _value.validationTimeMs
            : validationTimeMs // ignore: cast_nullable_to_non_nullable
                  as int,
        successRate: null == successRate
            ? _value.successRate
            : successRate // ignore: cast_nullable_to_non_nullable
                  as double,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$OpenApiValidationSummaryImpl implements _OpenApiValidationSummary {
  const _$OpenApiValidationSummaryImpl({
    required this.totalChecks,
    required this.passedChecks,
    required this.failedChecks,
    required this.warningChecks,
    required this.validationTimeMs,
    this.successRate = 0.0,
  });

  factory _$OpenApiValidationSummaryImpl.fromJson(Map<String, dynamic> json) =>
      _$$OpenApiValidationSummaryImplFromJson(json);

  @override
  final int totalChecks;
  @override
  final int passedChecks;
  @override
  final int failedChecks;
  @override
  final int warningChecks;
  @override
  final int validationTimeMs;
  @override
  @JsonKey()
  final double successRate;

  @override
  String toString() {
    return 'OpenApiValidationSummary(totalChecks: $totalChecks, passedChecks: $passedChecks, failedChecks: $failedChecks, warningChecks: $warningChecks, validationTimeMs: $validationTimeMs, successRate: $successRate)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$OpenApiValidationSummaryImpl &&
            (identical(other.totalChecks, totalChecks) ||
                other.totalChecks == totalChecks) &&
            (identical(other.passedChecks, passedChecks) ||
                other.passedChecks == passedChecks) &&
            (identical(other.failedChecks, failedChecks) ||
                other.failedChecks == failedChecks) &&
            (identical(other.warningChecks, warningChecks) ||
                other.warningChecks == warningChecks) &&
            (identical(other.validationTimeMs, validationTimeMs) ||
                other.validationTimeMs == validationTimeMs) &&
            (identical(other.successRate, successRate) ||
                other.successRate == successRate));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    totalChecks,
    passedChecks,
    failedChecks,
    warningChecks,
    validationTimeMs,
    successRate,
  );

  /// Create a copy of OpenApiValidationSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$OpenApiValidationSummaryImplCopyWith<_$OpenApiValidationSummaryImpl>
  get copyWith =>
      __$$OpenApiValidationSummaryImplCopyWithImpl<
        _$OpenApiValidationSummaryImpl
      >(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$OpenApiValidationSummaryImplToJson(this);
  }
}

abstract class _OpenApiValidationSummary implements OpenApiValidationSummary {
  const factory _OpenApiValidationSummary({
    required final int totalChecks,
    required final int passedChecks,
    required final int failedChecks,
    required final int warningChecks,
    required final int validationTimeMs,
    final double successRate,
  }) = _$OpenApiValidationSummaryImpl;

  factory _OpenApiValidationSummary.fromJson(Map<String, dynamic> json) =
      _$OpenApiValidationSummaryImpl.fromJson;

  @override
  int get totalChecks;
  @override
  int get passedChecks;
  @override
  int get failedChecks;
  @override
  int get warningChecks;
  @override
  int get validationTimeMs;
  @override
  double get successRate;

  /// Create a copy of OpenApiValidationSummary
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$OpenApiValidationSummaryImplCopyWith<_$OpenApiValidationSummaryImpl>
  get copyWith => throw _privateConstructorUsedError;
}
