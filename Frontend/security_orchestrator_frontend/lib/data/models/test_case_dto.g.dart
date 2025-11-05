// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'test_case_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

TestCaseDto _$TestCaseDtoFromJson(Map<String, dynamic> json) => TestCaseDto(
  id: json['id'] as String,
  name: json['name'] as String,
  description: json['description'] as String?,
  specificationId: json['specificationId'] as String,
  endpoint: json['endpoint'] as String,
  method: $enumDecode(_$HttpMethodEnumMap, json['method']),
  requestData: json['requestData'] as Map<String, dynamic>,
  expectedResponse: json['expectedResponse'] as Map<String, dynamic>,
  status: $enumDecode(_$TestCaseStatusEnumMap, json['status']),
  createdAt: DateTime.parse(json['createdAt'] as String),
  updatedAt: DateTime.parse(json['updatedAt'] as String),
);

Map<String, dynamic> _$TestCaseDtoToJson(TestCaseDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'description': instance.description,
      'specificationId': instance.specificationId,
      'endpoint': instance.endpoint,
      'method': _$HttpMethodEnumMap[instance.method]!,
      'requestData': instance.requestData,
      'expectedResponse': instance.expectedResponse,
      'status': _$TestCaseStatusEnumMap[instance.status]!,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt.toIso8601String(),
    };

const _$HttpMethodEnumMap = {
  HttpMethod.get: 'GET',
  HttpMethod.post: 'POST',
  HttpMethod.put: 'PUT',
  HttpMethod.delete: 'DELETE',
  HttpMethod.patch: 'PATCH',
  HttpMethod.head: 'HEAD',
  HttpMethod.options: 'OPTIONS',
};

const _$TestCaseStatusEnumMap = {
  TestCaseStatus.active: 'ACTIVE',
  TestCaseStatus.inactive: 'INACTIVE',
  TestCaseStatus.archived: 'ARCHIVED',
};

TestCaseSummaryDto _$TestCaseSummaryDtoFromJson(Map<String, dynamic> json) =>
    TestCaseSummaryDto(
      id: json['id'] as String,
      name: json['name'] as String,
      endpoint: json['endpoint'] as String,
      method: $enumDecode(_$HttpMethodEnumMap, json['method']),
      status: $enumDecode(_$TestCaseStatusEnumMap, json['status']),
      createdAt: DateTime.parse(json['createdAt'] as String),
      lastResult: json['lastResult'] as String?,
    );

Map<String, dynamic> _$TestCaseSummaryDtoToJson(TestCaseSummaryDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'endpoint': instance.endpoint,
      'method': _$HttpMethodEnumMap[instance.method]!,
      'status': _$TestCaseStatusEnumMap[instance.status]!,
      'createdAt': instance.createdAt.toIso8601String(),
      'lastResult': instance.lastResult,
    };

TestResultDto _$TestResultDtoFromJson(Map<String, dynamic> json) =>
    TestResultDto(
      testCaseId: json['testCaseId'] as String,
      passed: json['passed'] as bool,
      responseTime: (json['responseTime'] as num).toInt(),
      statusCode: (json['statusCode'] as num).toInt(),
      responseData: json['responseData'] as Map<String, dynamic>,
      errors: (json['errors'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      executedAt: DateTime.parse(json['executedAt'] as String),
    );

Map<String, dynamic> _$TestResultDtoToJson(TestResultDto instance) =>
    <String, dynamic>{
      'testCaseId': instance.testCaseId,
      'passed': instance.passed,
      'responseTime': instance.responseTime,
      'statusCode': instance.statusCode,
      'responseData': instance.responseData,
      'errors': instance.errors,
      'executedAt': instance.executedAt.toIso8601String(),
    };
