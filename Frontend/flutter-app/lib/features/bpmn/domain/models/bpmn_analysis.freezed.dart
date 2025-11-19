// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'bpmn_analysis.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

BpmnAnalysis _$BpmnAnalysisFromJson(Map<String, dynamic> json) {
  return _BpmnAnalysis.fromJson(json);
}

/// @nodoc
mixin _$BpmnAnalysis {
  String get diagramName => throw _privateConstructorUsedError;
  String? get processId => throw _privateConstructorUsedError;
  String get overallRisk => throw _privateConstructorUsedError;
  int get totalIssues => throw _privateConstructorUsedError;
  List<BpmnIssue> get structuralIssues => throw _privateConstructorUsedError;
  List<BpmnIssue> get securityIssues => throw _privateConstructorUsedError;
  List<BpmnIssue> get performanceIssues => throw _privateConstructorUsedError;
  String get bpmnContent => throw _privateConstructorUsedError;
  DateTime? get analyzedAt => throw _privateConstructorUsedError;

  /// Serializes this BpmnAnalysis to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of BpmnAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $BpmnAnalysisCopyWith<BpmnAnalysis> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $BpmnAnalysisCopyWith<$Res> {
  factory $BpmnAnalysisCopyWith(
    BpmnAnalysis value,
    $Res Function(BpmnAnalysis) then,
  ) = _$BpmnAnalysisCopyWithImpl<$Res, BpmnAnalysis>;
  @useResult
  $Res call({
    String diagramName,
    String? processId,
    String overallRisk,
    int totalIssues,
    List<BpmnIssue> structuralIssues,
    List<BpmnIssue> securityIssues,
    List<BpmnIssue> performanceIssues,
    String bpmnContent,
    DateTime? analyzedAt,
  });
}

/// @nodoc
class _$BpmnAnalysisCopyWithImpl<$Res, $Val extends BpmnAnalysis>
    implements $BpmnAnalysisCopyWith<$Res> {
  _$BpmnAnalysisCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of BpmnAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? diagramName = null,
    Object? processId = freezed,
    Object? overallRisk = null,
    Object? totalIssues = null,
    Object? structuralIssues = null,
    Object? securityIssues = null,
    Object? performanceIssues = null,
    Object? bpmnContent = null,
    Object? analyzedAt = freezed,
  }) {
    return _then(
      _value.copyWith(
            diagramName: null == diagramName
                ? _value.diagramName
                : diagramName // ignore: cast_nullable_to_non_nullable
                      as String,
            processId: freezed == processId
                ? _value.processId
                : processId // ignore: cast_nullable_to_non_nullable
                      as String?,
            overallRisk: null == overallRisk
                ? _value.overallRisk
                : overallRisk // ignore: cast_nullable_to_non_nullable
                      as String,
            totalIssues: null == totalIssues
                ? _value.totalIssues
                : totalIssues // ignore: cast_nullable_to_non_nullable
                      as int,
            structuralIssues: null == structuralIssues
                ? _value.structuralIssues
                : structuralIssues // ignore: cast_nullable_to_non_nullable
                      as List<BpmnIssue>,
            securityIssues: null == securityIssues
                ? _value.securityIssues
                : securityIssues // ignore: cast_nullable_to_non_nullable
                      as List<BpmnIssue>,
            performanceIssues: null == performanceIssues
                ? _value.performanceIssues
                : performanceIssues // ignore: cast_nullable_to_non_nullable
                      as List<BpmnIssue>,
            bpmnContent: null == bpmnContent
                ? _value.bpmnContent
                : bpmnContent // ignore: cast_nullable_to_non_nullable
                      as String,
            analyzedAt: freezed == analyzedAt
                ? _value.analyzedAt
                : analyzedAt // ignore: cast_nullable_to_non_nullable
                      as DateTime?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$BpmnAnalysisImplCopyWith<$Res>
    implements $BpmnAnalysisCopyWith<$Res> {
  factory _$$BpmnAnalysisImplCopyWith(
    _$BpmnAnalysisImpl value,
    $Res Function(_$BpmnAnalysisImpl) then,
  ) = __$$BpmnAnalysisImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String diagramName,
    String? processId,
    String overallRisk,
    int totalIssues,
    List<BpmnIssue> structuralIssues,
    List<BpmnIssue> securityIssues,
    List<BpmnIssue> performanceIssues,
    String bpmnContent,
    DateTime? analyzedAt,
  });
}

/// @nodoc
class __$$BpmnAnalysisImplCopyWithImpl<$Res>
    extends _$BpmnAnalysisCopyWithImpl<$Res, _$BpmnAnalysisImpl>
    implements _$$BpmnAnalysisImplCopyWith<$Res> {
  __$$BpmnAnalysisImplCopyWithImpl(
    _$BpmnAnalysisImpl _value,
    $Res Function(_$BpmnAnalysisImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of BpmnAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? diagramName = null,
    Object? processId = freezed,
    Object? overallRisk = null,
    Object? totalIssues = null,
    Object? structuralIssues = null,
    Object? securityIssues = null,
    Object? performanceIssues = null,
    Object? bpmnContent = null,
    Object? analyzedAt = freezed,
  }) {
    return _then(
      _$BpmnAnalysisImpl(
        diagramName: null == diagramName
            ? _value.diagramName
            : diagramName // ignore: cast_nullable_to_non_nullable
                  as String,
        processId: freezed == processId
            ? _value.processId
            : processId // ignore: cast_nullable_to_non_nullable
                  as String?,
        overallRisk: null == overallRisk
            ? _value.overallRisk
            : overallRisk // ignore: cast_nullable_to_non_nullable
                  as String,
        totalIssues: null == totalIssues
            ? _value.totalIssues
            : totalIssues // ignore: cast_nullable_to_non_nullable
                  as int,
        structuralIssues: null == structuralIssues
            ? _value._structuralIssues
            : structuralIssues // ignore: cast_nullable_to_non_nullable
                  as List<BpmnIssue>,
        securityIssues: null == securityIssues
            ? _value._securityIssues
            : securityIssues // ignore: cast_nullable_to_non_nullable
                  as List<BpmnIssue>,
        performanceIssues: null == performanceIssues
            ? _value._performanceIssues
            : performanceIssues // ignore: cast_nullable_to_non_nullable
                  as List<BpmnIssue>,
        bpmnContent: null == bpmnContent
            ? _value.bpmnContent
            : bpmnContent // ignore: cast_nullable_to_non_nullable
                  as String,
        analyzedAt: freezed == analyzedAt
            ? _value.analyzedAt
            : analyzedAt // ignore: cast_nullable_to_non_nullable
                  as DateTime?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$BpmnAnalysisImpl implements _BpmnAnalysis {
  const _$BpmnAnalysisImpl({
    required this.diagramName,
    this.processId,
    required this.overallRisk,
    required this.totalIssues,
    final List<BpmnIssue> structuralIssues = const <BpmnIssue>[],
    final List<BpmnIssue> securityIssues = const <BpmnIssue>[],
    final List<BpmnIssue> performanceIssues = const <BpmnIssue>[],
    required this.bpmnContent,
    this.analyzedAt,
  }) : _structuralIssues = structuralIssues,
       _securityIssues = securityIssues,
       _performanceIssues = performanceIssues;

  factory _$BpmnAnalysisImpl.fromJson(Map<String, dynamic> json) =>
      _$$BpmnAnalysisImplFromJson(json);

  @override
  final String diagramName;
  @override
  final String? processId;
  @override
  final String overallRisk;
  @override
  final int totalIssues;
  final List<BpmnIssue> _structuralIssues;
  @override
  @JsonKey()
  List<BpmnIssue> get structuralIssues {
    if (_structuralIssues is EqualUnmodifiableListView)
      return _structuralIssues;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_structuralIssues);
  }

  final List<BpmnIssue> _securityIssues;
  @override
  @JsonKey()
  List<BpmnIssue> get securityIssues {
    if (_securityIssues is EqualUnmodifiableListView) return _securityIssues;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_securityIssues);
  }

  final List<BpmnIssue> _performanceIssues;
  @override
  @JsonKey()
  List<BpmnIssue> get performanceIssues {
    if (_performanceIssues is EqualUnmodifiableListView)
      return _performanceIssues;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_performanceIssues);
  }

  @override
  final String bpmnContent;
  @override
  final DateTime? analyzedAt;

  @override
  String toString() {
    return 'BpmnAnalysis(diagramName: $diagramName, processId: $processId, overallRisk: $overallRisk, totalIssues: $totalIssues, structuralIssues: $structuralIssues, securityIssues: $securityIssues, performanceIssues: $performanceIssues, bpmnContent: $bpmnContent, analyzedAt: $analyzedAt)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$BpmnAnalysisImpl &&
            (identical(other.diagramName, diagramName) ||
                other.diagramName == diagramName) &&
            (identical(other.processId, processId) ||
                other.processId == processId) &&
            (identical(other.overallRisk, overallRisk) ||
                other.overallRisk == overallRisk) &&
            (identical(other.totalIssues, totalIssues) ||
                other.totalIssues == totalIssues) &&
            const DeepCollectionEquality().equals(
              other._structuralIssues,
              _structuralIssues,
            ) &&
            const DeepCollectionEquality().equals(
              other._securityIssues,
              _securityIssues,
            ) &&
            const DeepCollectionEquality().equals(
              other._performanceIssues,
              _performanceIssues,
            ) &&
            (identical(other.bpmnContent, bpmnContent) ||
                other.bpmnContent == bpmnContent) &&
            (identical(other.analyzedAt, analyzedAt) ||
                other.analyzedAt == analyzedAt));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    diagramName,
    processId,
    overallRisk,
    totalIssues,
    const DeepCollectionEquality().hash(_structuralIssues),
    const DeepCollectionEquality().hash(_securityIssues),
    const DeepCollectionEquality().hash(_performanceIssues),
    bpmnContent,
    analyzedAt,
  );

  /// Create a copy of BpmnAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$BpmnAnalysisImplCopyWith<_$BpmnAnalysisImpl> get copyWith =>
      __$$BpmnAnalysisImplCopyWithImpl<_$BpmnAnalysisImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$BpmnAnalysisImplToJson(this);
  }
}

abstract class _BpmnAnalysis implements BpmnAnalysis {
  const factory _BpmnAnalysis({
    required final String diagramName,
    final String? processId,
    required final String overallRisk,
    required final int totalIssues,
    final List<BpmnIssue> structuralIssues,
    final List<BpmnIssue> securityIssues,
    final List<BpmnIssue> performanceIssues,
    required final String bpmnContent,
    final DateTime? analyzedAt,
  }) = _$BpmnAnalysisImpl;

  factory _BpmnAnalysis.fromJson(Map<String, dynamic> json) =
      _$BpmnAnalysisImpl.fromJson;

  @override
  String get diagramName;
  @override
  String? get processId;
  @override
  String get overallRisk;
  @override
  int get totalIssues;
  @override
  List<BpmnIssue> get structuralIssues;
  @override
  List<BpmnIssue> get securityIssues;
  @override
  List<BpmnIssue> get performanceIssues;
  @override
  String get bpmnContent;
  @override
  DateTime? get analyzedAt;

  /// Create a copy of BpmnAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$BpmnAnalysisImplCopyWith<_$BpmnAnalysisImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
