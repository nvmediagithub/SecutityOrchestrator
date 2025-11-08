class ApiConstants {
  static const String baseUrl = 'http://localhost:8080';
  
  // Core endpoints
  static const String processesEndpoint = '/api/processes';
  static const String specificationsEndpoint = '/api/specifications';
  static const String workflowsEndpoint = '/api/workflows';
  static const String testCasesEndpoint = '/api/test-cases';
  static const String aiEndpoint = '/api/ai';
  static const String llmEndpoint = '/api/llm';
  
  // Testing system endpoints
  static const String testingEndpoint = '/api/testing';
  static const String executionEndpoint = '/api/execution';
  static const String vulnerabilityEndpoint = '/api/vulnerabilities';
  static const String owaspEndpoint = '/api/owasp';
  static const String logsEndpoint = '/api/logs';
  static const String openApiEndpoint = '/api/openapi';
  static const String bpmnEndpoint = '/api/bpmn';
  static const String reportsEndpoint = '/api/reports';
  
  // WebSocket endpoints
  static const String websocketEndpoint = 'ws://localhost:8080/ws/executions';
  static const String logsWebsocketEndpoint = 'ws://localhost:8080/ws/logs';
  static const String vulnerabilityWebsocketEndpoint = 'ws://localhost:8080/ws/vulnerabilities';
}