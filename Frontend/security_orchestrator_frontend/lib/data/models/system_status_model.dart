// System Status Models for SecurityOrchestrator LLM Service
// Real-time monitoring of SecurityOrchestratorLLMFinal service (port 8090)

class SystemStatus {
  final String service;
  final String status;
  final String message;
  final DateTime timestamp;
  final bool isHealthy;
  final Map<String, dynamic>? additionalData;

  const SystemStatus({
    required this.service,
    required this.status,
    required this.message,
    required this.timestamp,
    required this.isHealthy,
    this.additionalData,
  });

  factory SystemStatus.fromJson(Map<String, dynamic> json) {
    return SystemStatus(
      service: json['service'] as String? ?? 'SecurityOrchestrator LLM',
      status: json['status'] as String? ?? 'unknown',
      message: json['message'] as String? ?? '',
      timestamp: DateTime.now(),
      isHealthy: json['status'] == 'ready' || json['healthy'] == true,
      additionalData: json,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'service': service,
      'status': status,
      'message': message,
      'timestamp': timestamp.toIso8601String(),
      'isHealthy': isHealthy,
      ...?additionalData,
    };
  }
}

class LLMStatus {
  final String service;
  final String status;
  final String port;
  final String integration;
  final bool ready;
  final String version;
  final String completion;
  final DateTime timestamp;
  final bool isOperational;

  const LLMStatus({
    required this.service,
    required this.status,
    required this.port,
    required this.integration,
    required this.ready,
    required this.version,
    required this.completion,
    required this.timestamp,
    required this.isOperational,
  });

  factory LLMStatus.fromJson(Map<String, dynamic> json) {
    return LLMStatus(
      service: json['service'] as String? ?? 'SecurityOrchestrator LLM',
      status: json['status'] as String? ?? 'unknown',
      port: json['port'] as String? ?? '8090',
      integration: json['integration'] as String? ?? '',
      ready: json['ready'] as bool? ?? false,
      version: json['version'] as String? ?? 'unknown',
      completion: json['completion'] as String? ?? '0%',
      timestamp: DateTime.now(),
      isOperational: json['ready'] == true || json['status'] == 'ready',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'service': service,
      'status': status,
      'port': port,
      'integration': integration,
      'ready': ready,
      'version': version,
      'completion': completion,
      'timestamp': timestamp.toIso8601String(),
      'isOperational': isOperational,
    };
  }
}

class OllamaStatus {
  final String status;
  final String ollamaUrl;
  final String message;
  final bool connected;
  final String? response;
  final String? error;
  final DateTime timestamp;
  final bool isAccessible;

  const OllamaStatus({
    required this.status,
    required this.ollamaUrl,
    required this.message,
    required this.connected,
    this.response,
    this.error,
    required this.timestamp,
    required this.isAccessible,
  });

  factory OllamaStatus.fromJson(Map<String, dynamic> json) {
    return OllamaStatus(
      status: json['status'] as String? ?? 'unknown',
      ollamaUrl: json['ollama_url'] as String? ?? 'http://localhost:11434',
      message: json['message'] as String? ?? '',
      connected: json['connected'] as bool? ?? false,
      response: json['response'] as String?,
      error: json['error'] as String?,
      timestamp: DateTime.now(),
      isAccessible: json['connected'] == true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'status': status,
      'ollama_url': ollamaUrl,
      'message': message,
      'connected': connected,
      'response': response,
      'error': error,
      'timestamp': timestamp.toIso8601String(),
      'isAccessible': isAccessible,
    };
  }
}

class CodeLlamaStatus {
  final String modelName;
  final String modelType;
  final bool loaded;
  final double sizeGb;
  final int contextWindow;
  final int maxTokens;
  final String? status;
  final String? lastChecked;
  final bool isActive;
  final DateTime timestamp;

  const CodeLlamaStatus({
    required this.modelName,
    required this.modelType,
    required this.loaded,
    required this.sizeGb,
    required this.contextWindow,
    required this.maxTokens,
    this.status,
    this.lastChecked,
    required this.isActive,
    required this.timestamp,
  });

  factory CodeLlamaStatus.fromJson(Map<String, dynamic> json) {
    return CodeLlamaStatus(
      modelName: json['modelName'] as String? ?? 'codellama:7b',
      modelType: json['modelType'] as String? ?? 'Code Generation',
      loaded: json['loaded'] as bool? ?? false,
      sizeGb: (json['sizeGb'] as num?)?.toDouble() ?? 3.8,
      contextWindow: json['contextWindow'] as int? ?? 4096,
      maxTokens: json['maxTokens'] as int? ?? 2048,
      status: json['status'] as String?,
      lastChecked: json['lastChecked'] as String?,
      isActive: json['isActive'] as bool? ?? false,
      timestamp: DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'modelName': modelName,
      'modelType': modelType,
      'loaded': loaded,
      'sizeGb': sizeGb,
      'contextWindow': contextWindow,
      'maxTokens': maxTokens,
      'status': status,
      'lastChecked': lastChecked,
      'isActive': isActive,
      'timestamp': timestamp.toIso8601String(),
    };
  }
}

class SystemDashboardData {
  final SystemStatus systemStatus;
  final LLMStatus llmStatus;
  final OllamaStatus ollamaStatus;
  final CodeLlamaStatus codeLlamaStatus;
  final DateTime lastUpdated;
  final bool isLoading;
  final String? errorMessage;

  const SystemDashboardData({
    required this.systemStatus,
    required this.llmStatus,
    required this.ollamaStatus,
    required this.codeLlamaStatus,
    required this.lastUpdated,
    required this.isLoading,
    this.errorMessage,
  });

  factory SystemDashboardData.initial() {
    final now = DateTime.now();
    return SystemDashboardData(
      systemStatus: SystemStatus(
        service: 'SecurityOrchestrator LLM',
        status: 'loading',
        message: 'Initializing...',
        timestamp: now,
        isHealthy: false,
      ),
      llmStatus: LLMStatus(
        service: 'SecurityOrchestrator LLM',
        status: 'loading',
        port: '8090',
        integration: '',
        ready: false,
        version: 'unknown',
        completion: '0%',
        timestamp: now,
        isOperational: false,
      ),
      ollamaStatus: OllamaStatus(
        status: 'loading',
        ollamaUrl: 'http://localhost:11434',
        message: 'Checking connection...',
        connected: false,
        response: null,
        error: null,
        timestamp: now,
        isAccessible: false,
      ),
      codeLlamaStatus: CodeLlamaStatus(
        modelName: 'codellama:7b',
        modelType: 'Code Generation',
        loaded: false,
        sizeGb: 3.8,
        contextWindow: 4096,
        maxTokens: 2048,
        status: 'unknown',
        lastChecked: null,
        isActive: false,
        timestamp: now,
      ),
      lastUpdated: now,
      isLoading: true,
      errorMessage: null,
    );
  }

  SystemDashboardData copyWith({
    SystemStatus? systemStatus,
    LLMStatus? llmStatus,
    OllamaStatus? ollamaStatus,
    CodeLlamaStatus? codeLlamaStatus,
    DateTime? lastUpdated,
    bool? isLoading,
    String? errorMessage,
  }) {
    return SystemDashboardData(
      systemStatus: systemStatus ?? this.systemStatus,
      llmStatus: llmStatus ?? this.llmStatus,
      ollamaStatus: ollamaStatus ?? this.ollamaStatus,
      codeLlamaStatus: codeLlamaStatus ?? this.codeLlamaStatus,
      lastUpdated: lastUpdated ?? this.lastUpdated,
      isLoading: isLoading ?? this.isLoading,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }

  /// Get overall system health status
  bool get isSystemHealthy {
    return systemStatus.isHealthy && 
           llmStatus.isOperational && 
           ollamaStatus.isAccessible;
  }

  /// Get all critical services status
  List<bool> get criticalServicesStatus {
    return [
      systemStatus.isHealthy,
      llmStatus.isOperational,
      ollamaStatus.isAccessible,
    ];
  }

  /// Get system uptime status
  String get overallStatus {
    if (errorMessage != null) return 'error';
    if (isLoading) return 'loading';
    if (isSystemHealthy) return 'healthy';
    return 'degraded';
  }
}