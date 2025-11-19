// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'bpmn_issue.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

BpmnIssue _$BpmnIssueFromJson(Map<String, dynamic> json) {
  return _BpmnIssue.fromJson(json);
}

/// @nodoc
mixin _$BpmnIssue {
  String get type => throw _privateConstructorUsedError;
  String get severity => throw _privateConstructorUsedError;
  String get description => throw _privateConstructorUsedError;
  String? get elementId => throw _privateConstructorUsedError;
  String? get suggestion => throw _privateConstructorUsedError;

  /// Serializes this BpmnIssue to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of BpmnIssue
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $BpmnIssueCopyWith<BpmnIssue> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $BpmnIssueCopyWith<$Res> {
  factory $BpmnIssueCopyWith(BpmnIssue value, $Res Function(BpmnIssue) then) =
      _$BpmnIssueCopyWithImpl<$Res, BpmnIssue>;
  @useResult
  $Res call({
    String type,
    String severity,
    String description,
    String? elementId,
    String? suggestion,
  });
}

/// @nodoc
class _$BpmnIssueCopyWithImpl<$Res, $Val extends BpmnIssue>
    implements $BpmnIssueCopyWith<$Res> {
  _$BpmnIssueCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of BpmnIssue
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? type = null,
    Object? severity = null,
    Object? description = null,
    Object? elementId = freezed,
    Object? suggestion = freezed,
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
            elementId: freezed == elementId
                ? _value.elementId
                : elementId // ignore: cast_nullable_to_non_nullable
                      as String?,
            suggestion: freezed == suggestion
                ? _value.suggestion
                : suggestion // ignore: cast_nullable_to_non_nullable
                      as String?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$BpmnIssueImplCopyWith<$Res>
    implements $BpmnIssueCopyWith<$Res> {
  factory _$$BpmnIssueImplCopyWith(
    _$BpmnIssueImpl value,
    $Res Function(_$BpmnIssueImpl) then,
  ) = __$$BpmnIssueImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String type,
    String severity,
    String description,
    String? elementId,
    String? suggestion,
  });
}

/// @nodoc
class __$$BpmnIssueImplCopyWithImpl<$Res>
    extends _$BpmnIssueCopyWithImpl<$Res, _$BpmnIssueImpl>
    implements _$$BpmnIssueImplCopyWith<$Res> {
  __$$BpmnIssueImplCopyWithImpl(
    _$BpmnIssueImpl _value,
    $Res Function(_$BpmnIssueImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of BpmnIssue
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? type = null,
    Object? severity = null,
    Object? description = null,
    Object? elementId = freezed,
    Object? suggestion = freezed,
  }) {
    return _then(
      _$BpmnIssueImpl(
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
        elementId: freezed == elementId
            ? _value.elementId
            : elementId // ignore: cast_nullable_to_non_nullable
                  as String?,
        suggestion: freezed == suggestion
            ? _value.suggestion
            : suggestion // ignore: cast_nullable_to_non_nullable
                  as String?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$BpmnIssueImpl implements _BpmnIssue {
  const _$BpmnIssueImpl({
    required this.type,
    required this.severity,
    required this.description,
    this.elementId,
    this.suggestion,
  });

  factory _$BpmnIssueImpl.fromJson(Map<String, dynamic> json) =>
      _$$BpmnIssueImplFromJson(json);

  @override
  final String type;
  @override
  final String severity;
  @override
  final String description;
  @override
  final String? elementId;
  @override
  final String? suggestion;

  @override
  String toString() {
    return 'BpmnIssue(type: $type, severity: $severity, description: $description, elementId: $elementId, suggestion: $suggestion)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$BpmnIssueImpl &&
            (identical(other.type, type) || other.type == type) &&
            (identical(other.severity, severity) ||
                other.severity == severity) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.elementId, elementId) ||
                other.elementId == elementId) &&
            (identical(other.suggestion, suggestion) ||
                other.suggestion == suggestion));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    type,
    severity,
    description,
    elementId,
    suggestion,
  );

  /// Create a copy of BpmnIssue
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$BpmnIssueImplCopyWith<_$BpmnIssueImpl> get copyWith =>
      __$$BpmnIssueImplCopyWithImpl<_$BpmnIssueImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$BpmnIssueImplToJson(this);
  }
}

abstract class _BpmnIssue implements BpmnIssue {
  const factory _BpmnIssue({
    required final String type,
    required final String severity,
    required final String description,
    final String? elementId,
    final String? suggestion,
  }) = _$BpmnIssueImpl;

  factory _BpmnIssue.fromJson(Map<String, dynamic> json) =
      _$BpmnIssueImpl.fromJson;

  @override
  String get type;
  @override
  String get severity;
  @override
  String get description;
  @override
  String? get elementId;
  @override
  String? get suggestion;

  /// Create a copy of BpmnIssue
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$BpmnIssueImplCopyWith<_$BpmnIssueImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
