// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'system_health.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

SystemHealth _$SystemHealthFromJson(Map<String, dynamic> json) {
  return _SystemHealth.fromJson(json);
}

/// @nodoc
mixin _$SystemHealth {
  String get id => throw _privateConstructorUsedError;
  @JsonKey(unknownEnumValue: HealthStatus.degraded)
  HealthStatus get status => throw _privateConstructorUsedError;
  double get cpuUsage => throw _privateConstructorUsedError;
  double get memoryUsage => throw _privateConstructorUsedError;
  double get diskUsage => throw _privateConstructorUsedError;
  int get activeConnections => throw _privateConstructorUsedError;
  DateTime get timestamp => throw _privateConstructorUsedError;
  String get details => throw _privateConstructorUsedError;

  /// Serializes this SystemHealth to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SystemHealth
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SystemHealthCopyWith<SystemHealth> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SystemHealthCopyWith<$Res> {
  factory $SystemHealthCopyWith(
    SystemHealth value,
    $Res Function(SystemHealth) then,
  ) = _$SystemHealthCopyWithImpl<$Res, SystemHealth>;
  @useResult
  $Res call({
    String id,
    @JsonKey(unknownEnumValue: HealthStatus.degraded) HealthStatus status,
    double cpuUsage,
    double memoryUsage,
    double diskUsage,
    int activeConnections,
    DateTime timestamp,
    String details,
  });
}

/// @nodoc
class _$SystemHealthCopyWithImpl<$Res, $Val extends SystemHealth>
    implements $SystemHealthCopyWith<$Res> {
  _$SystemHealthCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SystemHealth
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? status = null,
    Object? cpuUsage = null,
    Object? memoryUsage = null,
    Object? diskUsage = null,
    Object? activeConnections = null,
    Object? timestamp = null,
    Object? details = null,
  }) {
    return _then(
      _value.copyWith(
            id: null == id
                ? _value.id
                : id // ignore: cast_nullable_to_non_nullable
                      as String,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as HealthStatus,
            cpuUsage: null == cpuUsage
                ? _value.cpuUsage
                : cpuUsage // ignore: cast_nullable_to_non_nullable
                      as double,
            memoryUsage: null == memoryUsage
                ? _value.memoryUsage
                : memoryUsage // ignore: cast_nullable_to_non_nullable
                      as double,
            diskUsage: null == diskUsage
                ? _value.diskUsage
                : diskUsage // ignore: cast_nullable_to_non_nullable
                      as double,
            activeConnections: null == activeConnections
                ? _value.activeConnections
                : activeConnections // ignore: cast_nullable_to_non_nullable
                      as int,
            timestamp: null == timestamp
                ? _value.timestamp
                : timestamp // ignore: cast_nullable_to_non_nullable
                      as DateTime,
            details: null == details
                ? _value.details
                : details // ignore: cast_nullable_to_non_nullable
                      as String,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SystemHealthImplCopyWith<$Res>
    implements $SystemHealthCopyWith<$Res> {
  factory _$$SystemHealthImplCopyWith(
    _$SystemHealthImpl value,
    $Res Function(_$SystemHealthImpl) then,
  ) = __$$SystemHealthImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    String id,
    @JsonKey(unknownEnumValue: HealthStatus.degraded) HealthStatus status,
    double cpuUsage,
    double memoryUsage,
    double diskUsage,
    int activeConnections,
    DateTime timestamp,
    String details,
  });
}

/// @nodoc
class __$$SystemHealthImplCopyWithImpl<$Res>
    extends _$SystemHealthCopyWithImpl<$Res, _$SystemHealthImpl>
    implements _$$SystemHealthImplCopyWith<$Res> {
  __$$SystemHealthImplCopyWithImpl(
    _$SystemHealthImpl _value,
    $Res Function(_$SystemHealthImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SystemHealth
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? id = null,
    Object? status = null,
    Object? cpuUsage = null,
    Object? memoryUsage = null,
    Object? diskUsage = null,
    Object? activeConnections = null,
    Object? timestamp = null,
    Object? details = null,
  }) {
    return _then(
      _$SystemHealthImpl(
        id: null == id
            ? _value.id
            : id // ignore: cast_nullable_to_non_nullable
                  as String,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as HealthStatus,
        cpuUsage: null == cpuUsage
            ? _value.cpuUsage
            : cpuUsage // ignore: cast_nullable_to_non_nullable
                  as double,
        memoryUsage: null == memoryUsage
            ? _value.memoryUsage
            : memoryUsage // ignore: cast_nullable_to_non_nullable
                  as double,
        diskUsage: null == diskUsage
            ? _value.diskUsage
            : diskUsage // ignore: cast_nullable_to_non_nullable
                  as double,
        activeConnections: null == activeConnections
            ? _value.activeConnections
            : activeConnections // ignore: cast_nullable_to_non_nullable
                  as int,
        timestamp: null == timestamp
            ? _value.timestamp
            : timestamp // ignore: cast_nullable_to_non_nullable
                  as DateTime,
        details: null == details
            ? _value.details
            : details // ignore: cast_nullable_to_non_nullable
                  as String,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$SystemHealthImpl implements _SystemHealth {
  const _$SystemHealthImpl({
    required this.id,
    @JsonKey(unknownEnumValue: HealthStatus.degraded) required this.status,
    required this.cpuUsage,
    required this.memoryUsage,
    required this.diskUsage,
    required this.activeConnections,
    required this.timestamp,
    required this.details,
  });

  factory _$SystemHealthImpl.fromJson(Map<String, dynamic> json) =>
      _$$SystemHealthImplFromJson(json);

  @override
  final String id;
  @override
  @JsonKey(unknownEnumValue: HealthStatus.degraded)
  final HealthStatus status;
  @override
  final double cpuUsage;
  @override
  final double memoryUsage;
  @override
  final double diskUsage;
  @override
  final int activeConnections;
  @override
  final DateTime timestamp;
  @override
  final String details;

  @override
  String toString() {
    return 'SystemHealth(id: $id, status: $status, cpuUsage: $cpuUsage, memoryUsage: $memoryUsage, diskUsage: $diskUsage, activeConnections: $activeConnections, timestamp: $timestamp, details: $details)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SystemHealthImpl &&
            (identical(other.id, id) || other.id == id) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.cpuUsage, cpuUsage) ||
                other.cpuUsage == cpuUsage) &&
            (identical(other.memoryUsage, memoryUsage) ||
                other.memoryUsage == memoryUsage) &&
            (identical(other.diskUsage, diskUsage) ||
                other.diskUsage == diskUsage) &&
            (identical(other.activeConnections, activeConnections) ||
                other.activeConnections == activeConnections) &&
            (identical(other.timestamp, timestamp) ||
                other.timestamp == timestamp) &&
            (identical(other.details, details) || other.details == details));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    id,
    status,
    cpuUsage,
    memoryUsage,
    diskUsage,
    activeConnections,
    timestamp,
    details,
  );

  /// Create a copy of SystemHealth
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SystemHealthImplCopyWith<_$SystemHealthImpl> get copyWith =>
      __$$SystemHealthImplCopyWithImpl<_$SystemHealthImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$SystemHealthImplToJson(this);
  }
}

abstract class _SystemHealth implements SystemHealth {
  const factory _SystemHealth({
    required final String id,
    @JsonKey(unknownEnumValue: HealthStatus.degraded)
    required final HealthStatus status,
    required final double cpuUsage,
    required final double memoryUsage,
    required final double diskUsage,
    required final int activeConnections,
    required final DateTime timestamp,
    required final String details,
  }) = _$SystemHealthImpl;

  factory _SystemHealth.fromJson(Map<String, dynamic> json) =
      _$SystemHealthImpl.fromJson;

  @override
  String get id;
  @override
  @JsonKey(unknownEnumValue: HealthStatus.degraded)
  HealthStatus get status;
  @override
  double get cpuUsage;
  @override
  double get memoryUsage;
  @override
  double get diskUsage;
  @override
  int get activeConnections;
  @override
  DateTime get timestamp;
  @override
  String get details;

  /// Create a copy of SystemHealth
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SystemHealthImplCopyWith<_$SystemHealthImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
