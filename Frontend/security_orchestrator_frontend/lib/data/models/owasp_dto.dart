import 'package:json_annotation/json_annotation.dart';

part 'owasp_dto.g.dart';

@JsonSerializable()
class OwaspCategoryDto {
  final String code; // A01, A02, A03, etc.
  final String name;
  final String description;
  final String severity; // LOW, MEDIUM, HIGH, CRITICAL
  final List<TestTypeDto> testTypes;
  final Map<String, dynamic> testParameters;

  OwaspCategoryDto({
    required this.code,
    required this.name,
    required this.description,
    required this.severity,
    required this.testTypes,
    required this.testParameters,
  });

  factory OwaspCategoryDto.fromJson(Map<String, dynamic> json) =>
      _$OwaspCategoryDtoFromJson(json);

  Map<String, dynamic> toJson() => _$OwaspCategoryDtoToJson(this);
}

@JsonSerializable()
class TestTypeDto {
  final String id;
  final String name;
  final String description;
  final String category; // OwaspCategoryDto code
  final String payload;
  final Map<String, dynamic> parameters;
  final String expectedResponse;
  final List<String> testCases;

  TestTypeDto({
    required this.id,
    required this.name,
    required this.description,
    required this.category,
    required this.payload,
    required this.parameters,
    required this.expectedResponse,
    required this.testCases,
  });

  factory TestTypeDto.fromJson(Map<String, dynamic> json) =>
      _$TestTypeDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TestTypeDtoToJson(this);
}

@JsonSerializable()
class OwaspTestExecutionDto {
  final String id;
  final String category;
  final String testType;
  final String status; // PENDING, RUNNING, COMPLETED, FAILED
  final DateTime startedAt;
  final DateTime? completedAt;
  final String endpoint;
  final String method;
  final String payload;
  final Map<String, dynamic> response;
  final int statusCode;
  final List<String> vulnerabilitiesFound;
  final List<String> errors;
  final double cvssScore;

  OwaspTestExecutionDto({
    required this.id,
    required this.category,
    required this.testType,
    required this.status,
    required this.startedAt,
    this.completedAt,
    required this.endpoint,
    required this.method,
    required this.payload,
    required this.response,
    required this.statusCode,
    required this.vulnerabilitiesFound,
    required this.errors,
    required this.cvssScore,
  });

  factory OwaspTestExecutionDto.fromJson(Map<String, dynamic> json) =>
      _$OwaspTestExecutionDtoFromJson(json);

  Map<String, dynamic> toJson() => _$OwaspTestExecutionDtoToJson(this);
}

@JsonSerializable()
class TestResultsDashboardDto {
  final String sessionId;
  final Map<String, int> resultsByCategory;
  final int totalTests;
  final int passedTests;
  final int failedTests;
  final int vulnerabilityCount;
  final double averageCvssScore;
  final List<String> criticalVulnerabilities;
  final Map<String, double> cvssByCategory;

  TestResultsDashboardDto({
    required this.sessionId,
    required this.resultsByCategory,
    required this.totalTests,
    required this.passedTests,
    required this.failedTests,
    required this.vulnerabilityCount,
    required this.averageCvssScore,
    required this.criticalVulnerabilities,
    required this.cvssByCategory,
  });

  factory TestResultsDashboardDto.fromJson(Map<String, dynamic> json) =>
      _$TestResultsDashboardDtoFromJson(json);

  Map<String, dynamic> toJson() => _$TestResultsDashboardDtoToJson(this);
}

@JsonSerializable()
class VulnerabilityReportDto {
  final String id;
  final String title;
  final String category;
  final String severity;
  final double cvssScore;
  final String description;
  final String recommendation;
  final String evidence;
  final List<String> references;
  final String status;

  VulnerabilityReportDto({
    required this.id,
    required this.title,
    required this.category,
    required this.severity,
    required this.cvssScore,
    required this.description,
    required this.recommendation,
    required this.evidence,
    required this.references,
    required this.status,
  });

  factory VulnerabilityReportDto.fromJson(Map<String, dynamic> json) =>
      _$VulnerabilityReportDtoFromJson(json);

  Map<String, dynamic> toJson() => _$VulnerabilityReportDtoToJson(this);
}

// OWASP Top 10 categories
enum OwaspCategory {
  @JsonValue('A01')
  a01_injection,
  @JsonValue('A02')
  a02_brokenAuth,
  @JsonValue('A03')
  a03_sensitiveData,
  @JsonValue('A04')
  a04_xmlExternalEntities,
  @JsonValue('A05')
  a05_brokenAccessControl,
  @JsonValue('A06')
  a06_securityMisconfiguration,
  @JsonValue('A07')
  a07_xss,
  @JsonValue('A08')
  a08_insecureDeserialization,
  @JsonValue('A09')
  a09_vulnerableComponents,
  @JsonValue('A10')
  a10_insufficientLogging,
}