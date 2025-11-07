// LLM Management Models for SecurityOrchestrator Flutter App

import 'llm_provider.dart';

class LLMProviderSettings {
  final LLMProvider provider;
  final String? apiKey;
  final String? baseUrl;
  final int timeout;
  final int maxRetries;

  LLMProviderSettings({
    required this.provider,
    this.apiKey,
    this.baseUrl,
    this.timeout = 30,
    this.maxRetries = 3,
  });

  factory LLMProviderSettings.fromJson(Map<String, dynamic> json) {
    return LLMProviderSettings(
      provider: LLMProvider.fromString(json['provider'] as String),
      apiKey: json['apiKey'] as String?,
      baseUrl: json['baseUrl'] as String?,
      timeout: json['timeout'] ?? 30,
      maxRetries: json['maxRetries'] ?? 3,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'provider': provider.value,
      'apiKey': apiKey,
      'baseUrl': baseUrl,
      'timeout': timeout,
      'maxRetries': maxRetries,
    };
  }

  /// Check if provider has an API key configured
  bool get hasApiKey {
    return apiKey != null && 
           apiKey!.isNotEmpty && 
           apiKey! != 'configured';
  }
}

class LLMModelConfig {
  final String modelName;
  final LLMProvider provider;
  final int contextWindow;
  final int maxTokens;
  final double temperature;
  final double topP;
  final double frequencyPenalty;
  final double presencePenalty;
  final double? sizeGB;

  LLMModelConfig({
    required this.modelName,
    required this.provider,
    this.contextWindow = 4096,
    this.maxTokens = 2048,
    this.temperature = 0.7,
    this.topP = 0.9,
    this.frequencyPenalty = 0.0,
    this.presencePenalty = 0.0,
    this.sizeGB,
  });

  factory LLMModelConfig.fromJson(Map<String, dynamic> json) {
    return LLMModelConfig(
      modelName: json['modelName'] as String,
      provider: LLMProvider.fromString(json['provider'] as String),
      contextWindow: json['contextWindow'] ?? 4096,
      maxTokens: json['maxTokens'] ?? 2048,
      temperature: (json['temperature'] as num?)?.toDouble() ?? 0.7,
      topP: (json['topP'] as num?)?.toDouble() ?? 0.9,
      frequencyPenalty: (json['frequencyPenalty'] as num?)?.toDouble() ?? 0.0,
      presencePenalty: (json['presencePenalty'] as num?)?.toDouble() ?? 0.0,
      sizeGB: (json['sizeGB'] as num?)?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'modelName': modelName,
      'provider': provider.value,
      'contextWindow': contextWindow,
      'maxTokens': maxTokens,
      'temperature': temperature,
      'topP': topP,
      'frequencyPenalty': frequencyPenalty,
      'presencePenalty': presencePenalty,
      'sizeGB': sizeGB,
    };
  }
}

class LLMStatusResponse {
  final LLMProvider provider;
  final bool available;
  final bool healthy;
  final double? responseTimeMs;
  final String? errorMessage;
  final DateTime lastCheckedAt;

  LLMStatusResponse({
    required this.provider,
    required this.available,
    required this.healthy,
    this.responseTimeMs,
    this.errorMessage,
    required this.lastCheckedAt,
  });

  factory LLMStatusResponse.fromJson(Map<String, dynamic> json) {
    return LLMStatusResponse(
      provider: LLMProvider.fromString(json['provider'] as String),
      available: json['available'] as bool,
      healthy: json['healthy'] as bool,
      responseTimeMs: (json['responseTimeMs'] as num?)?.toDouble(),
      errorMessage: json['errorMessage'] as String?,
      lastCheckedAt: DateTime.parse(json['lastCheckedAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'provider': provider.value,
      'available': available,
      'healthy': healthy,
      'responseTimeMs': responseTimeMs,
      'errorMessage': errorMessage,
      'lastCheckedAt': lastCheckedAt.toIso8601String(),
    };
  }
}

class LLMConfigResponse {
  final LLMProvider activeProvider;
  final String activeModel;
  final Map<String, LLMProviderSettings> providers;
  final Map<String, LLMModelConfig> models;

  LLMConfigResponse({
    required this.activeProvider,
    required this.activeModel,
    required this.providers,
    required this.models,
  });

  factory LLMConfigResponse.fromJson(Map<String, dynamic> json) {
    final providersJson = json['providers'] as Map<String, dynamic>?;
    final providers = <String, LLMProviderSettings>{};
    if (providersJson != null) {
      providersJson.forEach((key, value) {
        providers[key] = LLMProviderSettings.fromJson(Map<String, dynamic>.from(value as Map));
      });
    }

    final modelsJson = json['models'] as Map<String, dynamic>?;
    final models = <String, LLMModelConfig>{};
    if (modelsJson != null) {
      modelsJson.forEach((key, value) {
        models[key] = LLMModelConfig.fromJson(Map<String, dynamic>.from(value as Map));
      });
    }

    return LLMConfigResponse(
      activeProvider: LLMProvider.fromString(json['activeProvider'] as String),
      activeModel: json['activeModel'] as String,
      providers: providers,
      models: models,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'activeProvider': activeProvider.value,
      'activeModel': activeModel,
      'providers': providers.map((key, value) => MapEntry(key, value.toJson())),
      'models': models.map((key, value) => MapEntry(key, value.toJson())),
    };
  }

  /// Get the configuration for the active provider
  LLMProviderSettings? get activeProviderSettings {
    return providers[activeProvider.value];
  }

  /// Get the configuration for the active model
  LLMModelConfig? get activeModelConfig {
    return models[activeModel];
  }

  /// Get all models for a specific provider
  List<String> getModelsByProvider(LLMProvider provider) {
    return models.entries
        .where((entry) => entry.value.provider == provider)
        .map((entry) => entry.key)
        .toList();
  }

  /// Check if a provider is available
  bool isProviderAvailable(LLMProvider provider) {
    final settings = providers[provider.value];
    if (settings == null) return false;

    if (provider == LLMProvider.openrouter) {
      return settings.hasApiKey;
    }

    return true; // Local provider is always available if configured
  }
}

class LLMTestResponse {
  final String modelName;
  final LLMProvider provider;
  final String prompt;
  final String response;
  final int tokensUsed;
  final double responseTimeMs;
  final bool success;

  const LLMTestResponse({
    required this.modelName,
    required this.provider,
    required this.prompt,
    required this.response,
    required this.tokensUsed,
    required this.responseTimeMs,
    required this.success,
  });

  factory LLMTestResponse.fromJson(Map<String, dynamic> json) {
    return LLMTestResponse(
      modelName: json['modelName'] as String,
      provider: LLMProvider.fromString(json['provider'] as String),
      prompt: json['prompt'] as String,
      response: json['response'] as String,
      tokensUsed: json['tokensUsed'] as int,
      responseTimeMs: (json['responseTimeMs'] as num).toDouble(),
      success: json['success'] as bool,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'modelName': modelName,
      'provider': provider.value,
      'prompt': prompt,
      'response': response,
      'tokensUsed': tokensUsed,
      'responseTimeMs': responseTimeMs,
      'success': success,
    };
  }
}

class LocalModelInfo {
  final String modelName;
  final double sizeGb;
  final bool loaded;
  final int contextWindow;
  final int maxTokens;
  final DateTime? lastUsed;
  bool isLoading = false;

  LocalModelInfo({
    required this.modelName,
    required this.sizeGb,
    required this.loaded,
    required this.contextWindow,
    required this.maxTokens,
    this.lastUsed,
    this.isLoading = false,
  });

  factory LocalModelInfo.fromJson(Map<String, dynamic> json) {
    return LocalModelInfo(
      modelName: json['modelName'] as String,
      sizeGb: (json['sizeGb'] as num).toDouble(),
      loaded: json['loaded'] as bool,
      contextWindow: json['contextWindow'] as int,
      maxTokens: json['maxTokens'] as int,
      lastUsed: json['lastUsed'] != null 
          ? DateTime.parse(json['lastUsed'] as String) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'modelName': modelName,
      'sizeGb': sizeGb,
      'loaded': loaded,
      'contextWindow': contextWindow,
      'maxTokens': maxTokens,
      'lastUsed': lastUsed?.toIso8601String(),
    };
  }

  LocalModelInfo copyWith({
    String? modelName,
    double? sizeGb,
    bool? loaded,
    int? contextWindow,
    int? maxTokens,
    DateTime? lastUsed,
    bool? isLoading,
  }) {
    return LocalModelInfo(
      modelName: modelName ?? this.modelName,
      sizeGb: sizeGb ?? this.sizeGb,
      loaded: loaded ?? this.loaded,
      contextWindow: contextWindow ?? this.contextWindow,
      maxTokens: maxTokens ?? this.maxTokens,
      lastUsed: lastUsed ?? this.lastUsed,
      isLoading: isLoading ?? this.isLoading,
    );
  }
}

class LocalModelsListResponse {
  final List<LocalModelInfo> models;
  final List<String> loadedModels;

  LocalModelsListResponse({required this.models, required this.loadedModels});

  factory LocalModelsListResponse.fromJson(Map<String, dynamic> json) {
    return LocalModelsListResponse(
      models: (json['models'] as List<dynamic>)
          .map((e) => LocalModelInfo.fromJson(e as Map<String, dynamic>))
          .toList(),
      loadedModels: List<String>.from(json['loadedModels']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'models': models.map((e) => e.toJson()).toList(),
      'loadedModels': loadedModels,
    };
  }

  LocalModelsListResponse copyWith({
    List<LocalModelInfo>? models,
    List<String>? loadedModels,
  }) {
    return LocalModelsListResponse(
      models: models ?? this.models,
      loadedModels: loadedModels ?? this.loadedModels,
    );
  }
}

class OpenRouterModelsListResponse {
  final List<String> models;
  final int total;

  OpenRouterModelsListResponse({required this.models, required this.total});

  factory OpenRouterModelsListResponse.fromJson(Map<String, dynamic> json) {
    return OpenRouterModelsListResponse(
      models: List<String>.from(json['models']),
      total: json['total'] as int,
    );
  }

  Map<String, dynamic> toJson() {
    return {'models': models, 'total': total};
  }
}

class OpenRouterStatusResponse {
  final bool connected;
  final double? creditsRemaining;
  final int? rateLimitRemaining;
  final String? errorMessage;

  OpenRouterStatusResponse({
    required this.connected,
    this.creditsRemaining,
    this.rateLimitRemaining,
    this.errorMessage,
  });

  factory OpenRouterStatusResponse.fromJson(Map<String, dynamic> json) {
    return OpenRouterStatusResponse(
      connected: json['connected'] as bool,
      creditsRemaining: (json['creditsRemaining'] as num?)?.toDouble(),
      rateLimitRemaining: json['rateLimitRemaining'] as int?,
      errorMessage: json['errorMessage'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'connected': connected,
      'creditsRemaining': creditsRemaining,
      'rateLimitRemaining': rateLimitRemaining,
      'errorMessage': errorMessage,
    };
  }
}

class PerformanceMetrics {
  final int totalRequests;
  final int successfulRequests;
  final int failedRequests;
  final double averageResponseTimeMs;
  final int totalTokensUsed;
  final double errorRate;
  final double uptimePercentage;

  PerformanceMetrics({
    required this.totalRequests,
    required this.successfulRequests,
    required this.failedRequests,
    required this.averageResponseTimeMs,
    required this.totalTokensUsed,
    required this.errorRate,
    required this.uptimePercentage,
  });

  factory PerformanceMetrics.fromJson(Map<String, dynamic> json) {
    return PerformanceMetrics(
      totalRequests: json['totalRequests'] as int,
      successfulRequests: json['successfulRequests'] as int,
      failedRequests: json['failedRequests'] as int,
      averageResponseTimeMs: (json['averageResponseTimeMs'] as num).toDouble(),
      totalTokensUsed: json['totalTokensUsed'] as int,
      errorRate: (json['errorRate'] as num).toDouble(),
      uptimePercentage: (json['uptimePercentage'] as num).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalRequests': totalRequests,
      'successfulRequests': successfulRequests,
      'failedRequests': failedRequests,
      'averageResponseTimeMs': averageResponseTimeMs,
      'totalTokensUsed': totalTokensUsed,
      'errorRate': errorRate,
      'uptimePercentage': uptimePercentage,
    };
  }
}

class PerformanceReportResponse {
  final LLMProvider provider;
  final PerformanceMetrics metrics;
  final String timeRange;
  final DateTime generatedAt;

  PerformanceReportResponse({
    required this.provider,
    required this.metrics,
    required this.timeRange,
    required this.generatedAt,
  });

  factory PerformanceReportResponse.fromJson(Map<String, dynamic> json) {
    return PerformanceReportResponse(
      provider: LLMProvider.fromString(json['provider'] as String),
      metrics: PerformanceMetrics.fromJson(json['metrics'] as Map<String, dynamic>),
      timeRange: json['timeRange'] as String,
      generatedAt: DateTime.parse(json['generatedAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'provider': provider.value,
      'metrics': metrics.toJson(),
      'timeRange': timeRange,
      'generatedAt': generatedAt.toIso8601String(),
    };
  }
}

class LLMHealthSummary {
  final List<LLMStatusResponse> providersStatus;
  final int localModelsLoaded;
  final int localModelsAvailable;
  final bool openRouterConnected;
  final LLMProvider activeProvider;
  final String activeModel;
  final bool systemHealthy;

  LLMHealthSummary({
    required this.providersStatus,
    required this.localModelsLoaded,
    required this.localModelsAvailable,
    required this.openRouterConnected,
    required this.activeProvider,
    required this.activeModel,
    required this.systemHealthy,
  });

  factory LLMHealthSummary.fromJson(Map<String, dynamic> json) {
    final statusList = (json['providersStatus'] as List<dynamic>?)
        ?.map((item) => LLMStatusResponse.fromJson(Map<String, dynamic>.from(item as Map)))
        .toList() ?? <LLMStatusResponse>[];

    return LLMHealthSummary(
      providersStatus: statusList,
      localModelsLoaded: json['localModelsLoaded'] as int? ?? 0,
      localModelsAvailable: json['localModelsAvailable'] as int? ?? 0,
      openRouterConnected: json['openRouterConnected'] as bool? ?? false,
      activeProvider: LLMProvider.fromString(json['activeProvider'] as String),
      activeModel: json['activeModel'] as String? ?? '',
      systemHealthy: json['systemHealthy'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'providersStatus': providersStatus.map((status) => status.toJson()).toList(),
      'localModelsLoaded': localModelsLoaded,
      'localModelsAvailable': localModelsAvailable,
      'openRouterConnected': openRouterConnected,
      'activeProvider': activeProvider.value,
      'activeModel': activeModel,
      'systemHealthy': systemHealthy,
    };
  }
}