// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'owasp_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

OwaspCategoryDto _$OwaspCategoryDtoFromJson(Map<String, dynamic> json) =>
    OwaspCategoryDto(
      code: json['code'] as String,
      name: json['name'] as String,
      description: json['description'] as String,
      severity: json['severity'] as String,
      testTypes: (json['testTypes'] as List<dynamic>)
          .map((e) => TestTypeDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      testParameters: json['testParameters'] as Map<String, dynamic>,
    );

Map<String, dynamic> _$OwaspCategoryDtoToJson(OwaspCategoryDto instance) =>
    <String, dynamic>{
      'code': instance.code,
      'name': instance.name,
      'description': instance.description,
      'severity': instance.severity,
      'testTypes': instance.testTypes,
      'testParameters': instance.testParameters,
    };

TestTypeDto _$TestTypeDtoFromJson(Map<String, dynamic> json) => TestTypeDto(
  id: json['id'] as String,
  name: json['name'] as String,
  description: json['description'] as String,
  category: json['category'] as String,
  payload: json['payload'] as String,
  parameters: json['parameters'] as Map<String, dynamic>,
  expectedResponse: json['expectedResponse'] as String,
  testCases: (json['testCases'] as List<dynamic>)
      .map((e) => e as String)
      .toList(),
);

Map<String, dynamic> _$TestTypeDtoToJson(TestTypeDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'description': instance.description,
      'category': instance.category,
      'payload': instance.payload,
      'parameters': instance.parameters,
      'expectedResponse': instance.expectedResponse,
      'testCases': instance.testCases,
    };

OwaspTestExecutionDto _$OwaspTestExecutionDtoFromJson(
  Map<String, dynamic> json,
) => OwaspTestExecutionDto(
  id: json['id'] as String,
  category: json['category'] as String,
  testType: json['testType'] as String,
  status: json['status'] as String,
  startedAt: DateTime.parse(json['startedAt'] as String),
  completedAt: json['completedAt'] == null
      ? null
      : DateTime.parse(json['completedAt'] as String),
  endpoint: json['endpoint'] as String,
  method: json['method'] as String,
  payload: json['payload'] as String,
  response: json['response'] as Map<String, dynamic>,
  statusCode: (json['statusCode'] as num).toInt(),
  vulnerabilitiesFound: (json['vulnerabilitiesFound'] as List<dynamic>)
      .map((e) => e as String)
      .toList(),
  errors: (json['errors'] as List<dynamic>).map((e) => e as String).toList(),
  cvssScore: (json['cvssScore'] as num).toDouble(),
);

Map<String, dynamic> _$OwaspTestExecutionDtoToJson(
  OwaspTestExecutionDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'category': instance.category,
  'testType': instance.testType,
  'status': instance.status,
  'startedAt': instance.startedAt.toIso8601String(),
  'completedAt': instance.completedAt?.toIso8601String(),
  'endpoint': instance.endpoint,
  'method': instance.method,
  'payload': instance.payload,
  'response': instance.response,
  'statusCode': instance.statusCode,
  'vulnerabilitiesFound': instance.vulnerabilitiesFound,
  'errors': instance.errors,
  'cvssScore': instance.cvssScore,
};

TestResultsDashboardDto _$TestResultsDashboardDtoFromJson(
  Map<String, dynamic> json,
) => TestResultsDashboardDto(
  sessionId: json['sessionId'] as String,
  resultsByCategory: Map<String, int>.from(json['resultsByCategory'] as Map),
  totalTests: (json['totalTests'] as num).toInt(),
  passedTests: (json['passedTests'] as num).toInt(),
  failedTests: (json['failedTests'] as num).toInt(),
  vulnerabilityCount: (json['vulnerabilityCount'] as num).toInt(),
  averageCvssScore: (json['averageCvssScore'] as num).toDouble(),
  criticalVulnerabilities: (json['criticalVulnerabilities'] as List<dynamic>)
      .map((e) => e as String)
      .toList(),
  cvssByCategory: (json['cvssByCategory'] as Map<String, dynamic>).map(
    (k, e) => MapEntry(k, (e as num).toDouble()),
  ),
);

Map<String, dynamic> _$TestResultsDashboardDtoToJson(
  TestResultsDashboardDto instance,
) => <String, dynamic>{
  'sessionId': instance.sessionId,
  'resultsByCategory': instance.resultsByCategory,
  'totalTests': instance.totalTests,
  'passedTests': instance.passedTests,
  'failedTests': instance.failedTests,
  'vulnerabilityCount': instance.vulnerabilityCount,
  'averageCvssScore': instance.averageCvssScore,
  'criticalVulnerabilities': instance.criticalVulnerabilities,
  'cvssByCategory': instance.cvssByCategory,
};

VulnerabilityReportDto _$VulnerabilityReportDtoFromJson(
  Map<String, dynamic> json,
) => VulnerabilityReportDto(
  id: json['id'] as String,
  title: json['title'] as String,
  category: json['category'] as String,
  severity: json['severity'] as String,
  cvssScore: (json['cvssScore'] as num).toDouble(),
  description: json['description'] as String,
  recommendation: json['recommendation'] as String,
  evidence: json['evidence'] as String,
  references: (json['references'] as List<dynamic>)
      .map((e) => e as String)
      .toList(),
  status: json['status'] as String,
);

Map<String, dynamic> _$VulnerabilityReportDtoToJson(
  VulnerabilityReportDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'title': instance.title,
  'category': instance.category,
  'severity': instance.severity,
  'cvssScore': instance.cvssScore,
  'description': instance.description,
  'recommendation': instance.recommendation,
  'evidence': instance.evidence,
  'references': instance.references,
  'status': instance.status,
};
