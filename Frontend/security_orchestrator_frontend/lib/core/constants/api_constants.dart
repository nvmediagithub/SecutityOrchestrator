class ApiConstants {
  // SecurityOrchestrator LLM Service (порт 8090)
  static const String llmBaseUrl = 'http://localhost:8090';
  static const String healthEndpoint = '/api/health';
  static const String llmStatusEndpoint = '/api/llm/status';
  static const String llmTestEndpoint = '/api/llm/test';
  static const String ollamaStatusEndpoint = '/api/llm/ollama/status';
  static const String llmCompleteEndpoint = '/api/llm/complete';
  
  // Legacy endpoints (порт 8080) - для совместимости
  static const String baseUrl = 'http://localhost:8080';
  static const String processesEndpoint = '/api/processes';
  static const String specificationsEndpoint = '/api/specifications';
  static const String workflowsEndpoint = '/api/workflows';
  static const String testCasesEndpoint = '/api/test-cases';
  static const String aiEndpoint = '/api/ai';
  static const String llmEndpoint = '/api/llm';
  
  // Testing system endpoints (OWASP Testing на порту 8091)
  static const String owaspBaseUrl = 'http://localhost:8091';
  static const String testingEndpoint = '/api/testing';
  static const String executionEndpoint = '/api/execution';
  static const String vulnerabilityEndpoint = '/api/vulnerabilities';
  static const String owaspEndpoint = '/api/owasp';
  static const String owaspStartEndpoint = '/api/owasp/start';
  static const String owaspStatusEndpoint = '/api/owasp/status';
  static const String owaspResultsEndpoint = '/api/owasp/results';
  static const String owaspProgressEndpoint = '/api/owasp/progress';
  static const String logsEndpoint = '/api/logs';
  static const String openApiEndpoint = '/api/openapi';
  static const String bpmnEndpoint = '/api/bpmn';
  static const String reportsEndpoint = '/api/reports';
  
  // WebSocket endpoints (обновлены для OWASP на порту 8091)
  static const String websocketEndpoint = 'ws://localhost:8091/ws/executions';
  static const String logsWebsocketEndpoint = 'ws://localhost:8091/ws/logs';
  static const String vulnerabilityWebsocketEndpoint = 'ws://localhost:8091/ws/vulnerabilities';
}