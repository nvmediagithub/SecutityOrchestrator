class ApiConstants {
  // Base configuration
  static const String baseUrl = 'http://localhost:8000';
  static const Duration defaultTimeout = Duration(seconds: 30);
  static const int maxRetries = 3;

  // Headers
  static const Map<String, String> defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  // API endpoints
  static const String healthEndpoint = '/health';

  // User Flow endpoints
  static const String userFlowBase = '/user-flow';
  static const String analyzeEndpoint = '$userFlowBase/analyze';
  static const String statusEndpoint = '$userFlowBase/status';
  static const String resultEndpoint = '$userFlowBase/result';
  static const String historyEndpoint = '$userFlowBase/history';

  // Process Management endpoints
  static const String processBase = '/processes';
  static const String workflowBase = '/workflows';

  // System Monitoring endpoints
  static const String monitoringBase = '/monitoring';
  static const String dashboardEndpoint = '$monitoringBase/dashboard';

  // LLM Management endpoints
  static const String llmBase = '/llm';
  static const String providersEndpoint = '$llmBase/providers';
  static const String modelsEndpoint = '$llmBase/models';
  static const String testEndpoint = '$llmBase/test';

  // Error messages
  static const String networkError = 'Network connection failed';
  static const String timeoutError = 'Request timed out';
  static const String serverError = 'Server error occurred';
  static const String parsingError = 'Failed to parse response';

  // HTTP status codes
  static const int ok = 200;
  static const int created = 201;
  static const int badRequest = 400;
  static const int unauthorized = 401;
  static const int forbidden = 403;
  static const int notFound = 404;
  static const int internalServerError = 500;

  // Pagination defaults
  static const int defaultPageSize = 20;
  static const int maxPageSize = 100;
}