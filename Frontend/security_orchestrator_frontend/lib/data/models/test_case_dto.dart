import 'package:json_annotation/json_annotation.dart';

part 'test_case_dto.g.dart';

@JsonSerializable()
class TestCaseDto {
  final String id;
  final String name;
  final String? description;
  final String specificationId;
  final String endpoint;
  final HttpMethod method;
  final Map<String, dynamic> requestData;
  final Map<String, dynamic> expectedResponse;
  final TestCaseStatus status;
  final DateTime createdAt;
  final DateTime updatedAt;

  TestCaseDto({
    required this.id,
    required this.name,
    this.description,
    required this.specificationId,
    required this.endpoint,
    required this.method,
    required this.requestData,
    required this.expectedResponse,
    required this.status,
    required this.createdAt,
    required this.updatedAt,
  });

  factory TestCaseDto.fromJson(Map<String, dynamic> json) =>
      _$TestCaseDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TestCaseDtoToJson(this);
}

enum HttpMethod {
  @JsonValue('GET')
  get,
  @JsonValue('POST')
  post,
  @JsonValue('PUT')
  put,
  @JsonValue('DELETE')
  delete,
  @JsonValue('PATCH')
  patch,
  @JsonValue('HEAD')
  head,
  @JsonValue('OPTIONS')
  options,
}

enum TestCaseStatus {
  @JsonValue('ACTIVE')
  active,
  @JsonValue('INACTIVE')
  inactive,
  @JsonValue('ARCHIVED')
  archived,
}

@JsonSerializable()
class TestCaseSummaryDto {
  final String id;
  final String name;
  final String endpoint;
  final HttpMethod method;
  final TestCaseStatus status;
  final DateTime createdAt;
  final String? lastResult;

  TestCaseSummaryDto({
    required this.id,
    required this.name,
    required this.endpoint,
    required this.method,
    required this.status,
    required this.createdAt,
    this.lastResult,
  });

  factory TestCaseSummaryDto.fromJson(Map<String, dynamic> json) =>
      _$TestCaseSummaryDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TestCaseSummaryDtoToJson(this);
}

@JsonSerializable()
class TestResultDto {
  final String testCaseId;
  final bool passed;
  final int responseTime;
  final int statusCode;
  final Map<String, dynamic> responseData;
  final List<String> errors;
  final DateTime executedAt;

  TestResultDto({
    required this.testCaseId,
    required this.passed,
    required this.responseTime,
    required this.statusCode,
    required this.responseData,
    required this.errors,
    required this.executedAt,
  });

  factory TestResultDto.fromJson(Map<String, dynamic> json) =>
      _$TestResultDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TestResultDtoToJson(this);
}