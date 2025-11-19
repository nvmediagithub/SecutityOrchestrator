// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'metric.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

Metric _$MetricFromJson(Map<String, dynamic> json) {
  return _Metric.fromJson(json);
}

/// @nodoc
mixin _$Metric {
  String get id => throw _privateConstructorUsedError;
  String get name => throw _privateConstructorUsedError;
  @JsonKey(unknownEnumValue: MetricType.unknown)
  MetricType get type => throw _privateConstructorUsedError;
  double get value => throw _privateConstructorUsedError;
  String get unit => throw _privateConstructorUsedError;
  DateTime get timestamp => throw _privateConstructorUsedError;
  String get description => throw _privateConstructorUsedError;
  String get tags => throw _privateConstructorUsedError;

  /// Serializes this Metric to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of Metric
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $MetricCopyWith<Metric> get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $MetricCopyWith<$Res> {
  factory $MetricCopyWith(Metric value, $Res Function(Metric) then) =
      _$MetricCopyWithImpl<$Res, Metric>;
  @useResult
  $Res call({
    String id,
    String name,
    @JsonKey(unknownEnumValue: MetricType.unknown) MetricType type,
    double value,
    String unit,
    DateTime timestamp,
    String description,
    String tags,
  });
}

/// @nodoc
class _$MetricCopyWithImpl<$Res, $Val extends Metric>
    implements $MetricCopyWith<$Res> {
  _$MetricCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of Metric
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? name = null,
    Object? type = null,
    Object? value = null,
    Object? unit = null,
    Object? timestamp = null,
    Object? description = null,
    Object? tags = null,
  }) {
    return _then(
      _value.copyWith(
            id: null == id
                ? _value.id
                : id // ignore: cast_nullable_to_non_nullable
                      as String,
            name: null == name
                ? _value.name
                : name // ignore: cast_nullable_to_non_nullable
                      as String,
            type: null == type
                ? _value.type
                : type // ignore: cast_nullable_to_non_nullable
                      as MetricType,
            value: null == value
                ? _value.value
                : value // ignore: cast_nullable_to_non_nullable
                      as double,
            unit: null == unit
                ? _value.unit
                : unit // ignore: cast_nullable_to_non_nullable
                      as String,
            timestamp: null == timestamp
                ? _value.timestamp
                : timestamp // ignore: cast_nullable_to_non_nullable
                      as DateTime,
            description: null == description
                ? _value.description
                : description // ignore: cast_nullable_to_non_nullable
                      as String,
            tags: null == tags
                ? _value.tags
                : tags // ignore: cast_nullable_to_non_nullable
                      as String,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$MetricImplCopyWith<$Res> implements $MetricCopyWith<$Res> {
  factory _$$MetricImplCopyWith(
    _$MetricImpl value,
    $Res Function(_$MetricImpl) then,
  ) = __$$MetricImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String id,
    String name,
    @JsonKey(unknownEnumValue: MetricType.unknown) MetricType type,
    double value,
    String unit,
    DateTime timestamp,
    String description,
    String tags,
  });
}

/// @nodoc
class __$$MetricImplCopyWithImpl<$Res>
    extends _$MetricCopyWithImpl<$Res, _$MetricImpl>
    implements _$$MetricImplCopyWith<$Res> {
  __$$MetricImplCopyWithImpl(
    _$MetricImpl _value,
    $Res Function(_$MetricImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of Metric
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? name = null,
    Object? type = null,
    Object? value = null,
    Object? unit = null,
    Object? timestamp = null,
    Object? description = null,
    Object? tags = null,
  }) {
    return _then(
      _$MetricImpl(
        id: null == id
            ? _value.id
            : id // ignore: cast_nullable_to_non_nullable
                  as String,
        name: null == name
            ? _value.name
            : name // ignore: cast_nullable_to_non_nullable
                  as String,
        type: null == type
            ? _value.type
            : type // ignore: cast_nullable_to_non_nullable
                  as MetricType,
        value: null == value
            ? _value.value
            : value // ignore: cast_nullable_to_non_nullable
                  as double,
        unit: null == unit
            ? _value.unit
            : unit // ignore: cast_nullable_to_non_nullable
                  as String,
        timestamp: null == timestamp
            ? _value.timestamp
            : timestamp // ignore: cast_nullable_to_non_nullable
                  as DateTime,
        description: null == description
            ? _value.description
            : description // ignore: cast_nullable_to_non_nullable
                  as String,
        tags: null == tags
            ? _value.tags
            : tags // ignore: cast_nullable_to_non_nullable
                  as String,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$MetricImpl implements _Metric {
  const _$MetricImpl({
    required this.id,
    required this.name,
    @JsonKey(unknownEnumValue: MetricType.unknown) required this.type,
    required this.value,
    required this.unit,
    required this.timestamp,
    this.description = '',
    this.tags = '',
  });

  factory _$MetricImpl.fromJson(Map<String, dynamic> json) =>
      _$$MetricImplFromJson(json);

  @override
  final String id;
  @override
  final String name;
  @override
  @JsonKey(unknownEnumValue: MetricType.unknown)
  final MetricType type;
  @override
  final double value;
  @override
  final String unit;
  @override
  final DateTime timestamp;
  @override
  @JsonKey()
  final String description;
  @override
  @JsonKey()
  final String tags;

  @override
  String toString() {
    return 'Metric(id: $id, name: $name, type: $type, value: $value, unit: $unit, timestamp: $timestamp, description: $description, tags: $tags)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$MetricImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.name, name) || other.name == name) &&
            (identical(other.type, type) || other.type == type) &&
            (identical(other.value, value) || other.value == value) &&
            (identical(other.unit, unit) || other.unit == unit) &&
            (identical(other.timestamp, timestamp) ||
                other.timestamp == timestamp) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.tags, tags) || other.tags == tags));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    id,
    name,
    type,
    value,
    unit,
    timestamp,
    description,
    tags,
  );

  /// Create a copy of Metric
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$MetricImplCopyWith<_$MetricImpl> get copyWith =>
      __$$MetricImplCopyWithImpl<_$MetricImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$MetricImplToJson(this);
  }
}

abstract class _Metric implements Metric {
  const factory _Metric({
    required final String id,
    required final String name,
    @JsonKey(unknownEnumValue: MetricType.unknown)
    required final MetricType type,
    required final double value,
    required final String unit,
    required final DateTime timestamp,
    final String description,
    final String tags,
  }) = _$MetricImpl;

  factory _Metric.fromJson(Map<String, dynamic> json) = _$MetricImpl.fromJson;

  @override
  String get id;
  @override
  String get name;
  @override
  @JsonKey(unknownEnumValue: MetricType.unknown)
  MetricType get type;
  @override
  double get value;
  @override
  String get unit;
  @override
  DateTime get timestamp;
  @override
  String get description;
  @override
  String get tags;

  /// Create a copy of Metric
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$MetricImplCopyWith<_$MetricImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
