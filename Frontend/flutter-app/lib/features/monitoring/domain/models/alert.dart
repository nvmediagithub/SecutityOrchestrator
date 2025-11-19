import 'package:freezed_annotation/freezed_annotation.dart';

part 'alert.freezed.dart';
part 'alert.g.dart';

@JsonEnum(fieldRename: FieldRename.screamingSnake)
enum AlertSeverity { low, medium, high, critical }

@JsonEnum(fieldRename: FieldRename.screamingSnake)
enum AlertStatus { active, resolved }

@freezed
class Alert with _$Alert {
  const factory Alert({
    required String id,
    required String title,
    required String description,
    @JsonKey(unknownEnumValue: AlertSeverity.medium)
    required AlertSeverity severity,
    @JsonKey(unknownEnumValue: AlertStatus.active) required AlertStatus status,
    required String source,
    required DateTime createdAt,
    DateTime? resolvedAt,
    @Default('') String tags,
  }) = _Alert;

  factory Alert.fromJson(Map<String, dynamic> json) => _$AlertFromJson(json);
}
