// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'openapi_analysis.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

OpenApiAnalysis _$OpenApiAnalysisFromJson(Map<String, dynamic> json) {
  return _OpenApiAnalysis.fromJson(json);
}

/// @nodoc
mixin _$OpenApiAnalysis {
  String get specificationName => throw _privateConstructorUsedError;
  String? get version => throw _privateConstructorUsedError;
  bool get valid => throw _privateConstructorUsedError;
  String? get description => throw _privateConstructorUsedError;
  Map<String, dynamic> get metadata => throw _privateConstructorUsedError;
  OpenApiValidationSummary? get validationSummary =>
      throw _privateConstructorUsedError;
  List<OpenApiValidationIssue> get validationErrors =>
      throw _privateConstructorUsedError;
  int get endpointCount => throw _privateConstructorUsedError;
  int get schemaCount => throw _privateConstructorUsedError;
  Map<String, int> get operationsByMethod => throw _privateConstructorUsedError;
  Map<String, dynamic> get endpoints => throw _privateConstructorUsedError;
  Map<String, dynamic> get schemas => throw _privateConstructorUsedError;
  List<OpenApiSecurityIssue> get securityIssues =>
      throw _privateConstructorUsedError;
  List<String> get recommendations => throw _privateConstructorUsedError;
  String get openapiContent => throw _privateConstructorUsedError;
  DateTime? get analyzedAt => throw _privateConstructorUsedError;

  /// Serializes this OpenApiAnalysis to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of OpenApiAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $OpenApiAnalysisCopyWith<OpenApiAnalysis> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $OpenApiAnalysisCopyWith<$Res> {
  factory $OpenApiAnalysisCopyWith(
    OpenApiAnalysis value,
    $Res Function(OpenApiAnalysis) then,
  ) = _$OpenApiAnalysisCopyWithImpl<$Res, OpenApiAnalysis>;
  @useResult
  $Res call({
    String specificationName,
    String? version,
    bool valid,
    String? description,
    Map<String, dynamic> metadata,
    OpenApiValidationSummary? validationSummary,
    List<OpenApiValidationIssue> validationErrors,
    int endpointCount,
    int schemaCount,
    Map<String, int> operationsByMethod,
    Map<String, dynamic> endpoints,
    Map<String, dynamic> schemas,
    List<OpenApiSecurityIssue> securityIssues,
    List<String> recommendations,
    String openapiContent,
    DateTime? analyzedAt,
  });

  $OpenApiValidationSummaryCopyWith<$Res>? get validationSummary;
}

/// @nodoc
class _$OpenApiAnalysisCopyWithImpl<$Res, $Val extends OpenApiAnalysis>
    implements $OpenApiAnalysisCopyWith<$Res> {
  _$OpenApiAnalysisCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of OpenApiAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? specificationName = null,
    Object? version = freezed,
    Object? valid = null,
    Object? description = freezed,
    Object? metadata = null,
    Object? validationSummary = freezed,
    Object? validationErrors = null,
    Object? endpointCount = null,
    Object? schemaCount = null,
    Object? operationsByMethod = null,
    Object? endpoints = null,
    Object? schemas = null,
    Object? securityIssues = null,
    Object? recommendations = null,
    Object? openapiContent = null,
    Object? analyzedAt = freezed,
  }) {
    return _then(
      _value.copyWith(
            specificationName: null == specificationName
                ? _value.specificationName
                : specificationName // ignore: cast_nullable_to_non_nullable
                      as String,
            version: freezed == version
                ? _value.version
                : version // ignore: cast_nullable_to_non_nullable
                      as String?,
            valid: null == valid
                ? _value.valid
                : valid // ignore: cast_nullable_to_non_nullable
                      as bool,
            description: freezed == description
                ? _value.description
                : description // ignore: cast_nullable_to_non_nullable
                      as String?,
            metadata: null == metadata
                ? _value.metadata
                : metadata // ignore: cast_nullable_to_non_nullable
                      as Map<String, dynamic>,
            validationSummary: freezed == validationSummary
                ? _value.validationSummary
                : validationSummary // ignore: cast_nullable_to_non_nullable
                      as OpenApiValidationSummary?,
            validationErrors: null == validationErrors
                ? _value.validationErrors
                : validationErrors // ignore: cast_nullable_to_non_nullable
                      as List<OpenApiValidationIssue>,
            endpointCount: null == endpointCount
                ? _value.endpointCount
                : endpointCount // ignore: cast_nullable_to_non_nullable
                      as int,
            schemaCount: null == schemaCount
                ? _value.schemaCount
                : schemaCount // ignore: cast_nullable_to_non_nullable
                      as int,
            operationsByMethod: null == operationsByMethod
                ? _value.operationsByMethod
                : operationsByMethod // ignore: cast_nullable_to_non_nullable
                      as Map<String, int>,
            endpoints: null == endpoints
                ? _value.endpoints
                : endpoints // ignore: cast_nullable_to_non_nullable
                      as Map<String, dynamic>,
            schemas: null == schemas
                ? _value.schemas
                : schemas // ignore: cast_nullable_to_non_nullable
                      as Map<String, dynamic>,
            securityIssues: null == securityIssues
                ? _value.securityIssues
                : securityIssues // ignore: cast_nullable_to_non_nullable
                      as List<OpenApiSecurityIssue>,
            recommendations: null == recommendations
                ? _value.recommendations
                : recommendations // ignore: cast_nullable_to_non_nullable
                      as List<String>,
            openapiContent: null == openapiContent
                ? _value.openapiContent
                : openapiContent // ignore: cast_nullable_to_non_nullable
                      as String,
            analyzedAt: freezed == analyzedAt
                ? _value.analyzedAt
                : analyzedAt // ignore: cast_nullable_to_non_nullable
                      as DateTime?,
          )
          as $Val,
    );
  }

  /// Create a copy of OpenApiAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @override
  @pragma('vm:prefer-inline')
  $OpenApiValidationSummaryCopyWith<$Res>? get validationSummary {
    if (_value.validationSummary == null) {
      return null;
    }

    return $OpenApiValidationSummaryCopyWith<$Res>(_value.validationSummary!, (
      value,
    ) {
      return _then(_value.copyWith(validationSummary: value) as $Val);
    });
  }
}

/// @nodoc
abstract class _$$OpenApiAnalysisImplCopyWith<$Res>
    implements $OpenApiAnalysisCopyWith<$Res> {
  factory _$$OpenApiAnalysisImplCopyWith(
    _$OpenApiAnalysisImpl value,
    $Res Function(_$OpenApiAnalysisImpl) then,
  ) = __$$OpenApiAnalysisImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String specificationName,
    String? version,
    bool valid,
    String? description,
    Map<String, dynamic> metadata,
    OpenApiValidationSummary? validationSummary,
    List<OpenApiValidationIssue> validationErrors,
    int endpointCount,
    int schemaCount,
    Map<String, int> operationsByMethod,
    Map<String, dynamic> endpoints,
    Map<String, dynamic> schemas,
    List<OpenApiSecurityIssue> securityIssues,
    List<String> recommendations,
    String openapiContent,
    DateTime? analyzedAt,
  });

  @override
  $OpenApiValidationSummaryCopyWith<$Res>? get validationSummary;
}

/// @nodoc
class __$$OpenApiAnalysisImplCopyWithImpl<$Res>
    extends _$OpenApiAnalysisCopyWithImpl<$Res, _$OpenApiAnalysisImpl>
    implements _$$OpenApiAnalysisImplCopyWith<$Res> {
  __$$OpenApiAnalysisImplCopyWithImpl(
    _$OpenApiAnalysisImpl _value,
    $Res Function(_$OpenApiAnalysisImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of OpenApiAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? specificationName = null,
    Object? version = freezed,
    Object? valid = null,
    Object? description = freezed,
    Object? metadata = null,
    Object? validationSummary = freezed,
    Object? validationErrors = null,
    Object? endpointCount = null,
    Object? schemaCount = null,
    Object? operationsByMethod = null,
    Object? endpoints = null,
    Object? schemas = null,
    Object? securityIssues = null,
    Object? recommendations = null,
    Object? openapiContent = null,
    Object? analyzedAt = freezed,
  }) {
    return _then(
      _$OpenApiAnalysisImpl(
        specificationName: null == specificationName
            ? _value.specificationName
            : specificationName // ignore: cast_nullable_to_non_nullable
                  as String,
        version: freezed == version
            ? _value.version
            : version // ignore: cast_nullable_to_non_nullable
                  as String?,
        valid: null == valid
            ? _value.valid
            : valid // ignore: cast_nullable_to_non_nullable
                  as bool,
        description: freezed == description
            ? _value.description
            : description // ignore: cast_nullable_to_non_nullable
                  as String?,
        metadata: null == metadata
            ? _value._metadata
            : metadata // ignore: cast_nullable_to_non_nullable
                  as Map<String, dynamic>,
        validationSummary: freezed == validationSummary
            ? _value.validationSummary
            : validationSummary // ignore: cast_nullable_to_non_nullable
                  as OpenApiValidationSummary?,
        validationErrors: null == validationErrors
            ? _value._validationErrors
            : validationErrors // ignore: cast_nullable_to_non_nullable
                  as List<OpenApiValidationIssue>,
        endpointCount: null == endpointCount
            ? _value.endpointCount
            : endpointCount // ignore: cast_nullable_to_non_nullable
                  as int,
        schemaCount: null == schemaCount
            ? _value.schemaCount
            : schemaCount // ignore: cast_nullable_to_non_nullable
                  as int,
        operationsByMethod: null == operationsByMethod
            ? _value._operationsByMethod
            : operationsByMethod // ignore: cast_nullable_to_non_nullable
                  as Map<String, int>,
        endpoints: null == endpoints
            ? _value._endpoints
            : endpoints // ignore: cast_nullable_to_non_nullable
                  as Map<String, dynamic>,
        schemas: null == schemas
            ? _value._schemas
            : schemas // ignore: cast_nullable_to_non_nullable
                  as Map<String, dynamic>,
        securityIssues: null == securityIssues
            ? _value._securityIssues
            : securityIssues // ignore: cast_nullable_to_non_nullable
                  as List<OpenApiSecurityIssue>,
        recommendations: null == recommendations
            ? _value._recommendations
            : recommendations // ignore: cast_nullable_to_non_nullable
                  as List<String>,
        openapiContent: null == openapiContent
            ? _value.openapiContent
            : openapiContent // ignore: cast_nullable_to_non_nullable
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
class _$OpenApiAnalysisImpl implements _OpenApiAnalysis {
  const _$OpenApiAnalysisImpl({
    required this.specificationName,
    this.version,
    required this.valid,
    this.description,
    final Map<String, dynamic> metadata = const <String, dynamic>{},
    this.validationSummary,
    final List<OpenApiValidationIssue> validationErrors =
        const <OpenApiValidationIssue>[],
    required this.endpointCount,
    required this.schemaCount,
    final Map<String, int> operationsByMethod = const <String, int>{},
    final Map<String, dynamic> endpoints = const <String, dynamic>{},
    final Map<String, dynamic> schemas = const <String, dynamic>{},
    final List<OpenApiSecurityIssue> securityIssues =
        const <OpenApiSecurityIssue>[],
    final List<String> recommendations = const <String>[],
    required this.openapiContent,
    this.analyzedAt,
  }) : _metadata = metadata,
       _validationErrors = validationErrors,
       _operationsByMethod = operationsByMethod,
       _endpoints = endpoints,
       _schemas = schemas,
       _securityIssues = securityIssues,
       _recommendations = recommendations;

  factory _$OpenApiAnalysisImpl.fromJson(Map<String, dynamic> json) =>
      _$$OpenApiAnalysisImplFromJson(json);

  @override
  final String specificationName;
  @override
  final String? version;
  @override
  final bool valid;
  @override
  final String? description;
  final Map<String, dynamic> _metadata;
  @override
  @JsonKey()
  Map<String, dynamic> get metadata {
    if (_metadata is EqualUnmodifiableMapView) return _metadata;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(_metadata);
  }

  @override
  final OpenApiValidationSummary? validationSummary;
  final List<OpenApiValidationIssue> _validationErrors;
  @override
  @JsonKey()
  List<OpenApiValidationIssue> get validationErrors {
    if (_validationErrors is EqualUnmodifiableListView)
      return _validationErrors;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_validationErrors);
  }

  @override
  final int endpointCount;
  @override
  final int schemaCount;
  final Map<String, int> _operationsByMethod;
  @override
  @JsonKey()
  Map<String, int> get operationsByMethod {
    if (_operationsByMethod is EqualUnmodifiableMapView)
      return _operationsByMethod;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(_operationsByMethod);
  }

  final Map<String, dynamic> _endpoints;
  @override
  @JsonKey()
  Map<String, dynamic> get endpoints {
    if (_endpoints is EqualUnmodifiableMapView) return _endpoints;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(_endpoints);
  }

  final Map<String, dynamic> _schemas;
  @override
  @JsonKey()
  Map<String, dynamic> get schemas {
    if (_schemas is EqualUnmodifiableMapView) return _schemas;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(_schemas);
  }

  final List<OpenApiSecurityIssue> _securityIssues;
  @override
  @JsonKey()
  List<OpenApiSecurityIssue> get securityIssues {
    if (_securityIssues is EqualUnmodifiableListView) return _securityIssues;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_securityIssues);
  }

  final List<String> _recommendations;
  @override
  @JsonKey()
  List<String> get recommendations {
    if (_recommendations is EqualUnmodifiableListView) return _recommendations;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_recommendations);
  }

  @override
  final String openapiContent;
  @override
  final DateTime? analyzedAt;

  @override
  String toString() {
    return 'OpenApiAnalysis(specificationName: $specificationName, version: $version, valid: $valid, description: $description, metadata: $metadata, validationSummary: $validationSummary, validationErrors: $validationErrors, endpointCount: $endpointCount, schemaCount: $schemaCount, operationsByMethod: $operationsByMethod, endpoints: $endpoints, schemas: $schemas, securityIssues: $securityIssues, recommendations: $recommendations, openapiContent: $openapiContent, analyzedAt: $analyzedAt)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$OpenApiAnalysisImpl &&
            (identical(other.specificationName, specificationName) ||
                other.specificationName == specificationName) &&
            (identical(other.version, version) || other.version == version) &&
            (identical(other.valid, valid) || other.valid == valid) &&
            (identical(other.description, description) ||
                other.description == description) &&
            const DeepCollectionEquality().equals(other._metadata, _metadata) &&
            (identical(other.validationSummary, validationSummary) ||
                other.validationSummary == validationSummary) &&
            const DeepCollectionEquality().equals(
              other._validationErrors,
              _validationErrors,
            ) &&
            (identical(other.endpointCount, endpointCount) ||
                other.endpointCount == endpointCount) &&
            (identical(other.schemaCount, schemaCount) ||
                other.schemaCount == schemaCount) &&
            const DeepCollectionEquality().equals(
              other._operationsByMethod,
              _operationsByMethod,
            ) &&
            const DeepCollectionEquality().equals(
              other._endpoints,
              _endpoints,
            ) &&
            const DeepCollectionEquality().equals(other._schemas, _schemas) &&
            const DeepCollectionEquality().equals(
              other._securityIssues,
              _securityIssues,
            ) &&
            const DeepCollectionEquality().equals(
              other._recommendations,
              _recommendations,
            ) &&
            (identical(other.openapiContent, openapiContent) ||
                other.openapiContent == openapiContent) &&
            (identical(other.analyzedAt, analyzedAt) ||
                other.analyzedAt == analyzedAt));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    specificationName,
    version,
    valid,
    description,
    const DeepCollectionEquality().hash(_metadata),
    validationSummary,
    const DeepCollectionEquality().hash(_validationErrors),
    endpointCount,
    schemaCount,
    const DeepCollectionEquality().hash(_operationsByMethod),
    const DeepCollectionEquality().hash(_endpoints),
    const DeepCollectionEquality().hash(_schemas),
    const DeepCollectionEquality().hash(_securityIssues),
    const DeepCollectionEquality().hash(_recommendations),
    openapiContent,
    analyzedAt,
  );

  /// Create a copy of OpenApiAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$OpenApiAnalysisImplCopyWith<_$OpenApiAnalysisImpl> get copyWith =>
      __$$OpenApiAnalysisImplCopyWithImpl<_$OpenApiAnalysisImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$OpenApiAnalysisImplToJson(this);
  }
}

abstract class _OpenApiAnalysis implements OpenApiAnalysis {
  const factory _OpenApiAnalysis({
    required final String specificationName,
    final String? version,
    required final bool valid,
    final String? description,
    final Map<String, dynamic> metadata,
    final OpenApiValidationSummary? validationSummary,
    final List<OpenApiValidationIssue> validationErrors,
    required final int endpointCount,
    required final int schemaCount,
    final Map<String, int> operationsByMethod,
    final Map<String, dynamic> endpoints,
    final Map<String, dynamic> schemas,
    final List<OpenApiSecurityIssue> securityIssues,
    final List<String> recommendations,
    required final String openapiContent,
    final DateTime? analyzedAt,
  }) = _$OpenApiAnalysisImpl;

  factory _OpenApiAnalysis.fromJson(Map<String, dynamic> json) =
      _$OpenApiAnalysisImpl.fromJson;

  @override
  String get specificationName;
  @override
  String? get version;
  @override
  bool get valid;
  @override
  String? get description;
  @override
  Map<String, dynamic> get metadata;
  @override
  OpenApiValidationSummary? get validationSummary;
  @override
  List<OpenApiValidationIssue> get validationErrors;
  @override
  int get endpointCount;
  @override
  int get schemaCount;
  @override
  Map<String, int> get operationsByMethod;
  @override
  Map<String, dynamic> get endpoints;
  @override
  Map<String, dynamic> get schemas;
  @override
  List<OpenApiSecurityIssue> get securityIssues;
  @override
  List<String> get recommendations;
  @override
  String get openapiContent;
  @override
  DateTime? get analyzedAt;

  /// Create a copy of OpenApiAnalysis
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$OpenApiAnalysisImplCopyWith<_$OpenApiAnalysisImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
