// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'analysis_session.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

AnalysisSession _$AnalysisSessionFromJson(Map<String, dynamic> json) {
  return _AnalysisSession.fromJson(json);
}

/// @nodoc
mixin _$AnalysisSession {
  String get id => throw _privateConstructorUsedError;
  String get processId => throw _privateConstructorUsedError;
  AnalysisSessionStatus get status => throw _privateConstructorUsedError;
  String? get currentStepId => throw _privateConstructorUsedError;
  List<AnalysisStep> get steps => throw _privateConstructorUsedError;
  Map<String, dynamic> get context => throw _privateConstructorUsedError;
  DateTime get createdAt => throw _privateConstructorUsedError;
  DateTime get updatedAt => throw _privateConstructorUsedError;

  /// Serializes this AnalysisSession to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of AnalysisSession
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $AnalysisSessionCopyWith<AnalysisSession> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $AnalysisSessionCopyWith<$Res> {
  factory $AnalysisSessionCopyWith(
    AnalysisSession value,
    $Res Function(AnalysisSession) then,
  ) = _$AnalysisSessionCopyWithImpl<$Res, AnalysisSession>;
  @useResult
  $Res call({
    String id,
    String processId,
    AnalysisSessionStatus status,
    String? currentStepId,
    List<AnalysisStep> steps,
    Map<String, dynamic> context,
    DateTime createdAt,
    DateTime updatedAt,
  });
}

/// @nodoc
class _$AnalysisSessionCopyWithImpl<$Res, $Val extends AnalysisSession>
    implements $AnalysisSessionCopyWith<$Res> {
  _$AnalysisSessionCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of AnalysisSession
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? processId = null,
    Object? status = null,
    Object? currentStepId = freezed,
    Object? steps = null,
    Object? context = null,
    Object? createdAt = null,
    Object? updatedAt = null,
  }) {
    return _then(
      _value.copyWith(
            id: null == id
                ? _value.id
                : id // ignore: cast_nullable_to_non_nullable
                      as String,
            processId: null == processId
                ? _value.processId
                : processId // ignore: cast_nullable_to_non_nullable
                      as String,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as AnalysisSessionStatus,
            currentStepId: freezed == currentStepId
                ? _value.currentStepId
                : currentStepId // ignore: cast_nullable_to_non_nullable
                      as String?,
            steps: null == steps
                ? _value.steps
                : steps // ignore: cast_nullable_to_non_nullable
                      as List<AnalysisStep>,
            context: null == context
                ? _value.context
                : context // ignore: cast_nullable_to_non_nullable
                      as Map<String, dynamic>,
            createdAt: null == createdAt
                ? _value.createdAt
                : createdAt // ignore: cast_nullable_to_non_nullable
                      as DateTime,
            updatedAt: null == updatedAt
                ? _value.updatedAt
                : updatedAt // ignore: cast_nullable_to_non_nullable
                      as DateTime,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$AnalysisSessionImplCopyWith<$Res>
    implements $AnalysisSessionCopyWith<$Res> {
  factory _$$AnalysisSessionImplCopyWith(
    _$AnalysisSessionImpl value,
    $Res Function(_$AnalysisSessionImpl) then,
  ) = __$$AnalysisSessionImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String id,
    String processId,
    AnalysisSessionStatus status,
    String? currentStepId,
    List<AnalysisStep> steps,
    Map<String, dynamic> context,
    DateTime createdAt,
    DateTime updatedAt,
  });
}

/// @nodoc
class __$$AnalysisSessionImplCopyWithImpl<$Res>
    extends _$AnalysisSessionCopyWithImpl<$Res, _$AnalysisSessionImpl>
    implements _$$AnalysisSessionImplCopyWith<$Res> {
  __$$AnalysisSessionImplCopyWithImpl(
    _$AnalysisSessionImpl _value,
    $Res Function(_$AnalysisSessionImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of AnalysisSession
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? processId = null,
    Object? status = null,
    Object? currentStepId = freezed,
    Object? steps = null,
    Object? context = null,
    Object? createdAt = null,
    Object? updatedAt = null,
  }) {
    return _then(
      _$AnalysisSessionImpl(
        id: null == id
            ? _value.id
            : id // ignore: cast_nullable_to_non_nullable
                  as String,
        processId: null == processId
            ? _value.processId
            : processId // ignore: cast_nullable_to_non_nullable
                  as String,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as AnalysisSessionStatus,
        currentStepId: freezed == currentStepId
            ? _value.currentStepId
            : currentStepId // ignore: cast_nullable_to_non_nullable
                  as String?,
        steps: null == steps
            ? _value._steps
            : steps // ignore: cast_nullable_to_non_nullable
                  as List<AnalysisStep>,
        context: null == context
            ? _value._context
            : context // ignore: cast_nullable_to_non_nullable
                  as Map<String, dynamic>,
        createdAt: null == createdAt
            ? _value.createdAt
            : createdAt // ignore: cast_nullable_to_non_nullable
                  as DateTime,
        updatedAt: null == updatedAt
            ? _value.updatedAt
            : updatedAt // ignore: cast_nullable_to_non_nullable
                  as DateTime,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$AnalysisSessionImpl implements _AnalysisSession {
  const _$AnalysisSessionImpl({
    required this.id,
    required this.processId,
    required this.status,
    this.currentStepId,
    final List<AnalysisStep> steps = const <AnalysisStep>[],
    final Map<String, dynamic> context = const <String, dynamic>{},
    required this.createdAt,
    required this.updatedAt,
  }) : _steps = steps,
       _context = context;

  factory _$AnalysisSessionImpl.fromJson(Map<String, dynamic> json) =>
      _$$AnalysisSessionImplFromJson(json);

  @override
  final String id;
  @override
  final String processId;
  @override
  final AnalysisSessionStatus status;
  @override
  final String? currentStepId;
  final List<AnalysisStep> _steps;
  @override
  @JsonKey()
  List<AnalysisStep> get steps {
    if (_steps is EqualUnmodifiableListView) return _steps;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_steps);
  }

  final Map<String, dynamic> _context;
  @override
  @JsonKey()
  Map<String, dynamic> get context {
    if (_context is EqualUnmodifiableMapView) return _context;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(_context);
  }

  @override
  final DateTime createdAt;
  @override
  final DateTime updatedAt;

  @override
  String toString() {
    return 'AnalysisSession(id: $id, processId: $processId, status: $status, currentStepId: $currentStepId, steps: $steps, context: $context, createdAt: $createdAt, updatedAt: $updatedAt)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$AnalysisSessionImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.processId, processId) ||
                other.processId == processId) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.currentStepId, currentStepId) ||
                other.currentStepId == currentStepId) &&
            const DeepCollectionEquality().equals(other._steps, _steps) &&
            const DeepCollectionEquality().equals(other._context, _context) &&
            (identical(other.createdAt, createdAt) ||
                other.createdAt == createdAt) &&
            (identical(other.updatedAt, updatedAt) ||
                other.updatedAt == updatedAt));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    id,
    processId,
    status,
    currentStepId,
    const DeepCollectionEquality().hash(_steps),
    const DeepCollectionEquality().hash(_context),
    createdAt,
    updatedAt,
  );

  /// Create a copy of AnalysisSession
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$AnalysisSessionImplCopyWith<_$AnalysisSessionImpl> get copyWith =>
      __$$AnalysisSessionImplCopyWithImpl<_$AnalysisSessionImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$AnalysisSessionImplToJson(this);
  }
}

abstract class _AnalysisSession implements AnalysisSession {
  const factory _AnalysisSession({
    required final String id,
    required final String processId,
    required final AnalysisSessionStatus status,
    final String? currentStepId,
    final List<AnalysisStep> steps,
    final Map<String, dynamic> context,
    required final DateTime createdAt,
    required final DateTime updatedAt,
  }) = _$AnalysisSessionImpl;

  factory _AnalysisSession.fromJson(Map<String, dynamic> json) =
      _$AnalysisSessionImpl.fromJson;

  @override
  String get id;
  @override
  String get processId;
  @override
  AnalysisSessionStatus get status;
  @override
  String? get currentStepId;
  @override
  List<AnalysisStep> get steps;
  @override
  Map<String, dynamic> get context;
  @override
  DateTime get createdAt;
  @override
  DateTime get updatedAt;

  /// Create a copy of AnalysisSession
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$AnalysisSessionImplCopyWith<_$AnalysisSessionImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

AnalysisStep _$AnalysisStepFromJson(Map<String, dynamic> json) {
  return _AnalysisStep.fromJson(json);
}

/// @nodoc
mixin _$AnalysisStep {
  String get id => throw _privateConstructorUsedError;
  String get title => throw _privateConstructorUsedError;
  String? get description => throw _privateConstructorUsedError;
  AnalysisStepType get type => throw _privateConstructorUsedError;
  AnalysisStepStatus get status => throw _privateConstructorUsedError;
  Map<String, dynamic> get metadata => throw _privateConstructorUsedError;

  /// Serializes this AnalysisStep to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of AnalysisStep
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $AnalysisStepCopyWith<AnalysisStep> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $AnalysisStepCopyWith<$Res> {
  factory $AnalysisStepCopyWith(
    AnalysisStep value,
    $Res Function(AnalysisStep) then,
  ) = _$AnalysisStepCopyWithImpl<$Res, AnalysisStep>;
  @useResult
  $Res call({
    String id,
    String title,
    String? description,
    AnalysisStepType type,
    AnalysisStepStatus status,
    Map<String, dynamic> metadata,
  });
}

/// @nodoc
class _$AnalysisStepCopyWithImpl<$Res, $Val extends AnalysisStep>
    implements $AnalysisStepCopyWith<$Res> {
  _$AnalysisStepCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of AnalysisStep
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? title = null,
    Object? description = freezed,
    Object? type = null,
    Object? status = null,
    Object? metadata = null,
  }) {
    return _then(
      _value.copyWith(
            id: null == id
                ? _value.id
                : id // ignore: cast_nullable_to_non_nullable
                      as String,
            title: null == title
                ? _value.title
                : title // ignore: cast_nullable_to_non_nullable
                      as String,
            description: freezed == description
                ? _value.description
                : description // ignore: cast_nullable_to_non_nullable
                      as String?,
            type: null == type
                ? _value.type
                : type // ignore: cast_nullable_to_non_nullable
                      as AnalysisStepType,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as AnalysisStepStatus,
            metadata: null == metadata
                ? _value.metadata
                : metadata // ignore: cast_nullable_to_non_nullable
                      as Map<String, dynamic>,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$AnalysisStepImplCopyWith<$Res>
    implements $AnalysisStepCopyWith<$Res> {
  factory _$$AnalysisStepImplCopyWith(
    _$AnalysisStepImpl value,
    $Res Function(_$AnalysisStepImpl) then,
  ) = __$$AnalysisStepImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String id,
    String title,
    String? description,
    AnalysisStepType type,
    AnalysisStepStatus status,
    Map<String, dynamic> metadata,
  });
}

/// @nodoc
class __$$AnalysisStepImplCopyWithImpl<$Res>
    extends _$AnalysisStepCopyWithImpl<$Res, _$AnalysisStepImpl>
    implements _$$AnalysisStepImplCopyWith<$Res> {
  __$$AnalysisStepImplCopyWithImpl(
    _$AnalysisStepImpl _value,
    $Res Function(_$AnalysisStepImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of AnalysisStep
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? title = null,
    Object? description = freezed,
    Object? type = null,
    Object? status = null,
    Object? metadata = null,
  }) {
    return _then(
      _$AnalysisStepImpl(
        id: null == id
            ? _value.id
            : id // ignore: cast_nullable_to_non_nullable
                  as String,
        title: null == title
            ? _value.title
            : title // ignore: cast_nullable_to_non_nullable
                  as String,
        description: freezed == description
            ? _value.description
            : description // ignore: cast_nullable_to_non_nullable
                  as String?,
        type: null == type
            ? _value.type
            : type // ignore: cast_nullable_to_non_nullable
                  as AnalysisStepType,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as AnalysisStepStatus,
        metadata: null == metadata
            ? _value._metadata
            : metadata // ignore: cast_nullable_to_non_nullable
                  as Map<String, dynamic>,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$AnalysisStepImpl implements _AnalysisStep {
  const _$AnalysisStepImpl({
    required this.id,
    required this.title,
    this.description,
    required this.type,
    required this.status,
    final Map<String, dynamic> metadata = const <String, dynamic>{},
  }) : _metadata = metadata;

  factory _$AnalysisStepImpl.fromJson(Map<String, dynamic> json) =>
      _$$AnalysisStepImplFromJson(json);

  @override
  final String id;
  @override
  final String title;
  @override
  final String? description;
  @override
  final AnalysisStepType type;
  @override
  final AnalysisStepStatus status;
  final Map<String, dynamic> _metadata;
  @override
  @JsonKey()
  Map<String, dynamic> get metadata {
    if (_metadata is EqualUnmodifiableMapView) return _metadata;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableMapView(_metadata);
  }

  @override
  String toString() {
    return 'AnalysisStep(id: $id, title: $title, description: $description, type: $type, status: $status, metadata: $metadata)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$AnalysisStepImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.title, title) || other.title == title) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.type, type) || other.type == type) &&
            (identical(other.status, status) || other.status == status) &&
            const DeepCollectionEquality().equals(other._metadata, _metadata));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    id,
    title,
    description,
    type,
    status,
    const DeepCollectionEquality().hash(_metadata),
  );

  /// Create a copy of AnalysisStep
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$AnalysisStepImplCopyWith<_$AnalysisStepImpl> get copyWith =>
      __$$AnalysisStepImplCopyWithImpl<_$AnalysisStepImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$AnalysisStepImplToJson(this);
  }
}

abstract class _AnalysisStep implements AnalysisStep {
  const factory _AnalysisStep({
    required final String id,
    required final String title,
    final String? description,
    required final AnalysisStepType type,
    required final AnalysisStepStatus status,
    final Map<String, dynamic> metadata,
  }) = _$AnalysisStepImpl;

  factory _AnalysisStep.fromJson(Map<String, dynamic> json) =
      _$AnalysisStepImpl.fromJson;

  @override
  String get id;
  @override
  String get title;
  @override
  String? get description;
  @override
  AnalysisStepType get type;
  @override
  AnalysisStepStatus get status;
  @override
  Map<String, dynamic> get metadata;

  /// Create a copy of AnalysisStep
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$AnalysisStepImplCopyWith<_$AnalysisStepImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
