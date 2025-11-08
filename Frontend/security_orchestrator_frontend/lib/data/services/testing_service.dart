import 'package:dio/dio.dart';
import '../models/execution_monitoring_dto.dart';
import '../models/owasp_dto.dart';
import '../models/vulnerability_dto.dart';
import '../models/log_dto.dart';
import '../../core/constants/api_constants.dart';

class TestingService {
  late final Dio _dio;

  TestingService() {
    _dio = Dio();
    _dio.options.baseUrl = ApiConstants.baseUrl;
    _dio.options.headers['Content-Type'] ??= 'application/json';
  }

  // Execution Management
  Future<ExecutionSessionDto> startEndToEndTesting({
    required String openApiUrl,
    required List<String> bpmnFiles,
    required List<String> owaspCategories,
    required String sessionName,
    Map<String, dynamic>? configuration,
  }) async {
    try {
      final response = await _dio.post(
        '${ApiConstants.executionEndpoint}/start',
        data: {
          'openApiUrl': openApiUrl,
          'bpmnFiles': bpmnFiles,
          'owaspCategories': owaspCategories,
          'sessionName': sessionName,
          'configuration': configuration ?? {},
        },
      );
      return ExecutionSessionDto.fromJson(
        Map<String, dynamic>.from(response.data as Map),
      );
    } on DioException catch (e) {
      throw Exception('Failed to start testing: ${e.message}');
    }
  }

  Future<ExecutionSessionDto> getExecutionSession(String sessionId) async {
    try {
      final response = await _dio.get(
        '${ApiConstants.executionEndpoint}/$sessionId',
      );
      return ExecutionSessionDto.fromJson(
        Map<String, dynamic>.from(response.data as Map),
      );
    } on DioException catch (e) {
      throw Exception('Failed to get execution session: ${e.message}');
    }
  }

  Future<List<ExecutionSessionDto>> getExecutionSessions({
    int page = 0,
    int size = 20,
    String? status,
  }) async {
    try {
      final queryParams = {
        'page': page,
        'size': size,
        if (status != null) 'status': status,
      };
      
      final response = await _dio.get(
        '${ApiConstants.executionEndpoint}/sessions',
        queryParameters: queryParams,
      );
      
      final list = response.data['content'] as List<dynamic>;
      return list
          .map((item) => ExecutionSessionDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get execution sessions: ${e.message}');
    }
  }

  Future<void> cancelExecution(String sessionId) async {
    try {
      await _dio.post('${ApiConstants.executionEndpoint}/$sessionId/cancel');
    } on DioException catch (e) {
      throw Exception('Failed to cancel execution: ${e.message}');
    }
  }

  Future<void> deleteExecution(String sessionId) async {
    try {
      await _dio.delete('${ApiConstants.executionEndpoint}/$sessionId');
    } on DioException catch (e) {
      throw Exception('Failed to delete execution: ${e.message}');
    }
  }

  // OWASP Testing
  Future<List<OwaspCategoryDto>> getOwaspCategories() async {
    try {
      final response = await _dio.get('${ApiConstants.owaspEndpoint}/categories');
      final list = response.data as List<dynamic>;
      return list
          .map((item) => OwaspCategoryDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get OWASP categories: ${e.message}');
    }
  }

  Future<List<TestTypeDto>> getTestTypes(String category) async {
    try {
      final response = await _dio.get(
        '${ApiConstants.owaspEndpoint}/categories/$category/tests',
      );
      final list = response.data as List<dynamic>;
      return list
          .map((item) => TestTypeDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get test types: ${e.message}');
    }
  }

  Future<List<OwaspTestExecutionDto>> getOwaspTestExecutions(String sessionId) async {
    try {
      final response = await _dio.get(
        '${ApiConstants.owaspEndpoint}/executions/$sessionId',
      );
      final list = response.data as List<dynamic>;
      return list
          .map((item) => OwaspTestExecutionDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get OWASP test executions: ${e.message}');
    }
  }

  Future<TestResultsDashboardDto> getTestResultsDashboard(String sessionId) async {
    try {
      final response = await _dio.get(
        '${ApiConstants.reportsEndpoint}/results/$sessionId',
      );
      return TestResultsDashboardDto.fromJson(
        Map<String, dynamic>.from(response.data as Map),
      );
    } on DioException catch (e) {
      throw Exception('Failed to get test results dashboard: ${e.message}');
    }
  }

  // Vulnerability Management
  Future<List<VulnerabilityDto>> getVulnerabilities({
    String? sessionId,
    String? severity,
    String? category,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final queryParams = {
        'page': page,
        'size': size,
        if (sessionId != null) 'sessionId': sessionId,
        if (severity != null) 'severity': severity,
        if (category != null) 'category': category,
      };
      
      final response = await _dio.get(
        ApiConstants.vulnerabilityEndpoint,
        queryParameters: queryParams,
      );
      
      final list = response.data['content'] as List<dynamic>;
      return list
          .map((item) => VulnerabilityDto.fromJson(
                Map<String, dynamic>.from(item as Map),
              ))
          .toList();
    } on DioException catch (e) {
      throw Exception('Failed to get vulnerabilities: ${e.message}');
    }
  }

  Future<VulnerabilityDto> updateVulnerabilityStatus(
    String vulnerabilityId,
    String status,
  ) async {
    try {
      final response = await _dio.put(
        '${ApiConstants.vulnerabilityEndpoint}/$vulnerabilityId/status',
        data: {'status': status},
      );
      return VulnerabilityDto.fromJson(
        Map<String, dynamic>.from(response.data as Map),
      );
    } on DioException catch (e) {
      throw Exception('Failed to update vulnerability status: ${e.message}');
    }
  }

  // OpenAPI Management
  Future<String> uploadOpenApiSpecification({
    required String url,
    Map<String, dynamic>? headers,
  }) async {
    try {
      final response = await _dio.post(
        '${ApiConstants.openApiEndpoint}/upload',
        data: {
          'url': url,
          'headers': headers ?? {},
        },
      );
      return response.data['specificationId'] as String;
    } on DioException catch (e) {
      throw Exception('Failed to upload OpenAPI specification: ${e.message}');
    }
  }

  Future<Map<String, dynamic>> validateOpenApi(String url) async {
    try {
      final response = await _dio.post(
        '${ApiConstants.openApiEndpoint}/validate',
        data: {'url': url},
      );
      return response.data as Map<String, dynamic>;
    } on DioException catch (e) {
      throw Exception('Failed to validate OpenAPI: ${e.message}');
    }
  }

  // BPMN Management
  Future<String> uploadBpmnFile(String filePath) async {
    try {
      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(filePath),
      });
      
      final response = await _dio.post(
        '${ApiConstants.bpmnEndpoint}/upload',
        data: formData,
      );
      return response.data['processId'] as String;
    } on DioException catch (e) {
      throw Exception('Failed to upload BPMN file: ${e.message}');
    }
  }

  Future<List<String>> getBpmnProcesses() async {
    try {
      final response = await _dio.get('${ApiConstants.bpmnEndpoint}/processes');
      return List<String>.from(response.data as List);
    } on DioException catch (e) {
      throw Exception('Failed to get BPMN processes: ${e.message}');
    }
  }

  // Reports and Export
  Future<Map<String, dynamic>> generateReport(
    String sessionId,
    String format, // 'PDF', 'JSON', 'CSV', 'EXCEL'
  ) async {
    try {
      final response = await _dio.post(
        '${ApiConstants.reportsEndpoint}/generate',
        data: {
          'sessionId': sessionId,
          'format': format,
        },
      );
      return response.data as Map<String, dynamic>;
    } on DioException catch (e) {
      throw Exception('Failed to generate report: ${e.message}');
    }
  }

  Future<String> downloadReport(String reportId) async {
    try {
      final response = await _dio.get(
        '${ApiConstants.reportsEndpoint}/download/$reportId',
      );
      return response.data['downloadUrl'] as String;
    } on DioException catch (e) {
      throw Exception('Failed to download report: ${e.message}');
    }
  }

  // Test Configuration
  Future<Map<String, dynamic>> getTestConfiguration() async {
    try {
      final response = await _dio.get('${ApiConstants.testingEndpoint}/config');
      return response.data as Map<String, dynamic>;
    } on DioException catch (e) {
      throw Exception('Failed to get test configuration: ${e.message}');
    }
  }

  Future<void> updateTestConfiguration(Map<String, dynamic> config) async {
    try {
      await _dio.put('${ApiConstants.testingEndpoint}/config', data: config);
    } on DioException catch (e) {
      throw Exception('Failed to update test configuration: ${e.message}');
    }
  }

  // Health and Status
  Future<Map<String, dynamic>> getSystemHealth() async {
    try {
      final response = await _dio.get('${ApiConstants.testingEndpoint}/health');
      return response.data as Map<String, dynamic>;
    } on DioException catch (e) {
      throw Exception('Failed to get system health: ${e.message}');
    }
  }

  Future<Map<String, bool>> checkModuleConnectivity() async {
    try {
      final response = await _dio.get('${ApiConstants.testingEndpoint}/connectivity');
      return Map<String, bool>.from(response.data as Map);
    } on DioException catch (e) {
      throw Exception('Failed to check module connectivity: ${e.message}');
    }
  }

  void dispose() {
    _dio.close();
  }
}