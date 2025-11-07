import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../data/models/llm_models.dart';
import '../../data/models/llm_provider.dart';
import '../../data/models/llm_dashboard_state.dart';
import '../../data/services/llm_service.dart';
import '../providers/llm_dashboard_provider.dart';

class LlmDashboardScreen extends ConsumerStatefulWidget {
  const LlmDashboardScreen({super.key});

  @override
  ConsumerState<LlmDashboardScreen> createState() => _LlmDashboardScreenState();
}

class _LlmDashboardScreenState extends ConsumerState<LlmDashboardScreen> {
  late final LlmService _llmService;
  String _testPrompt = '';
  String _testResponse = '';
  String _apiKey = '';
  String _baseUrl = 'https://openrouter.ai/api/v1';

  @override
  void initState() {
    super.initState();
    _llmService = ref.read(llmServiceProvider);
  }

  @override
  Widget build(BuildContext context) {
    final llmDashboardAsync = ref.watch(llmDashboardProvider);

    return Scaffold(
      appBar: AppBar(
        title: const SelectableText(
          'LLM Dashboard',
          style: TextStyle(fontWeight: FontWeight.w500),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref.read(llmDashboardProvider.notifier).refresh(force: true),
          ),
        ],
      ),
      body: llmDashboardAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stack) => _buildErrorView(error),
        data: (state) => _buildDashboardView(state),
      ),
    );
  }

  Widget _buildErrorView(Object error) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error, size: 64, color: Colors.red),
          const SizedBox(height: 16),
          SelectableText(
            'Error loading dashboard',
            style: Theme.of(context).textTheme.headlineSmall,
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 8),
          Tooltip(
            message: 'Tap to copy error details',
            child: SelectableText(
              error.toString(),
              style: Theme.of(context).textTheme.bodyMedium,
              textAlign: TextAlign.center,
            ),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: () => ref.read(llmDashboardProvider.notifier).refresh(force: true),
            child: const SelectableText('Retry'),
          ),
        ],
      ),
    );
  }

  Widget _buildDashboardView(LlmDashboardState state) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildOverviewCard(state),
          const SizedBox(height: 16),
          _buildProviderConfigurationCard(state),
          const SizedBox(height: 16),
          _buildModelSelectionCard(state),
          const SizedBox(height: 16),
          _buildStatusMonitoringCard(state),
          const SizedBox(height: 16),
          _buildTestInterfaceCard(state),
        ],
      ),
    );
  }

  Widget _buildOverviewCard(LlmDashboardState state) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SelectableText(
              'System Overview',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Icon(
                  _getProviderIcon(state.config.activeProvider),
                  color: _getProviderColor(state.config.activeProvider),
                ),
                const SizedBox(width: 8),
                SelectableText('Active Provider: ${state.config.activeProvider.value}'),
              ],
            ),
            const SizedBox(height: 4),
            Tooltip(
              message: 'Click to select and copy model name',
              child: SelectableText('Active Model: ${state.config.activeModel}'),
            ),
            const SizedBox(height: 4),
            SelectableText('Available Models: ${state.config.models.length}'),
            const SizedBox(height: 4),
            SelectableText('Configured Providers: ${state.config.providers.length}'),
          ],
        ),
      ),
    );
  }

  Widget _buildProviderConfigurationCard(LlmDashboardState state) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SelectableText(
              'Provider Configuration',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 16),

            // OpenRouter Configuration
            ExpansionTile(
              title: Row(
                children: [
                  Icon(
                    Icons.cloud, 
                    color: state.config.providers['OPENROUTER']?.hasApiKey == true 
                        ? Colors.green 
                        : Colors.orange
                  ),
                  const SizedBox(width: 8),
                  const SelectableText('OpenRouter Configuration'),
                  const Spacer(),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: state.config.providers['OPENROUTER']?.hasApiKey == true 
                          ? Colors.green 
                          : Colors.orange,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: SelectableText(
                      state.config.providers['OPENROUTER']?.hasApiKey == true
                          ? 'Configured'
                          : 'Not Configured',
                      style: const TextStyle(color: Colors.white, fontSize: 12),
                    ),
                  ),
                ],
              ),
              children: [
                Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    children: [
                      // API Key Input
                      TextFormField(
                        decoration: InputDecoration(
                          labelText: 'OpenRouter API Key',
                          hintText: 'sk-or-...',
                          prefixIcon: const Icon(Icons.key),
                          suffixIcon: _apiKey.isNotEmpty
                              ? (_isValidOpenRouterKey(_apiKey)
                                  ? const Icon(Icons.check_circle, color: Colors.green)
                                  : const Icon(Icons.error, color: Colors.red))
                              : null,
                          border: const OutlineInputBorder(),
                          errorBorder: const OutlineInputBorder(borderSide: BorderSide(color: Colors.red)),
                        ),
                        obscureText: true,
                        autovalidateMode: AutovalidateMode.onUserInteraction,
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return null;
                          }
                          if (!_isValidOpenRouterKey(value)) {
                            return 'Invalid format. Should start with "sk-or-" and be at least 20 characters';
                          }
                          return null;
                        },
                        onChanged: (value) {
                          setState(() {
                            _apiKey = value;
                          });
                        },
                      ),
                      const SizedBox(height: 12),
                      
                      // Base URL Input
                      TextFormField(
                        decoration: const InputDecoration(
                          labelText: 'Base URL',
                          hintText: 'https://openrouter.ai/api/v1',
                          prefixIcon: Icon(Icons.link),
                          border: OutlineInputBorder(),
                        ),
                        controller: TextEditingController(text: _baseUrl),
                        onChanged: (value) {
                          setState(() {
                            _baseUrl = value;
                          });
                        },
                      ),
                      const SizedBox(height: 16),
                      
                      // Configure Button
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton.icon(
                          onPressed: _configureOpenRouter,
                          icon: const Icon(Icons.cloud_done),
                          label: const SelectableText('Configure OpenRouter'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.orange,
                            foregroundColor: Colors.white,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),

            // Provider List
            const SizedBox(height: 16),
            SelectableText(
              'Available Providers',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            ...state.config.providers.entries.map(
              (entry) => Card(
                child: ListTile(
                  leading: Icon(
                    _getProviderIcon(LLMProvider.fromString(entry.key)),
                    color: _getProviderColor(LLMProvider.fromString(entry.key)),
                  ),
                  title: SelectableText(
                    entry.key,
                    style: state.config.activeProvider.value == entry.key
                        ? const TextStyle(fontWeight: FontWeight.bold)
                        : null,
                  ),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      SelectableText(_isProviderConfigured(LLMProvider.fromString(entry.key)) ? 'Configured' : 'Not configured'),
                      if (state.config.activeProvider.value == entry.key)
                        const SelectableText('Active', style: TextStyle(color: Colors.green, fontWeight: FontWeight.w500)),
                    ],
                  ),
                  trailing: state.config.activeProvider.value == entry.key
                      ? const Icon(Icons.check_circle, color: Colors.green)
                      : null,
                  onTap: state.config.activeProvider.value == entry.key
                      ? null
                      : () => _switchProvider(LLMProvider.fromString(entry.key)),
                  enabled: state.config.activeProvider.value != entry.key,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildModelSelectionCard(LlmDashboardState state) {
    final availableModels = state.config.models.values
        .where((model) => model.provider == state.config.activeProvider)
        .toList();

    // Create unique model list to prevent duplicate values
    final uniqueModels = <String>{};
    final uniqueAvailableModels = <LLMModelConfig>[];
    
    for (final model in availableModels) {
      if (uniqueModels.add(model.modelName)) {
        uniqueAvailableModels.add(model);
      } else {
        debugPrint('Duplicate model found: ${model.modelName} for provider ${model.provider.value}');
      }
    }

    // Ensure active model is included and valid
    String? dropdownValue = state.config.activeModel;
    if (!uniqueAvailableModels.any((model) => model.modelName == dropdownValue)) {
      if (uniqueAvailableModels.isNotEmpty) {
        dropdownValue = uniqueAvailableModels.first.modelName;
      } else {
        dropdownValue = null;
      }
    }

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SelectableText(
              'Model Selection',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 16),

            // Check if we have any available models
            if (uniqueAvailableModels.isEmpty) ...[
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.orange.shade50,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.orange.shade300),
                ),
                child: Row(
                  children: [
                    Icon(Icons.warning, color: Colors.orange.shade700),
                    const SizedBox(width: 8),
                    Expanded(
                      child: SelectableText(
                        state.config.activeProvider == LLMProvider.openrouter
                            ? 'No models available. Please configure OpenRouter with a valid API key to load available models.'
                            : 'No local models available. Please load a local model to continue.',
                        style: TextStyle(color: Colors.orange.shade700),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              ElevatedButton.icon(
                onPressed: () {
                  if (state.config.activeProvider == LLMProvider.openrouter) {
                    _showOpenRouterConfigHelp();
                  } else {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: SelectableText('Please load a local model through the provider status section')),
                    );
                  }
                },
                icon: const Icon(Icons.help),
                label: SelectableText(
                  state.config.activeProvider == LLMProvider.openrouter
                      ? 'Configure OpenRouter'
                      : 'Load Local Model',
                ),
              ),
            ] else ...[
              DropdownButtonFormField<String>(
                value: dropdownValue,
                decoration: const InputDecoration(
                  labelText: 'Select Model',
                  border: OutlineInputBorder(),
                ),
                items: uniqueAvailableModels.map((model) {
                  return DropdownMenuItem<String>(
                    value: model.modelName,
                    child: Tooltip(
                      message: 'Context: ${model.contextWindow}, Max Tokens: ${model.maxTokens}, Temp: ${model.temperature}',
                      child: SelectableText(model.modelName),
                    ),
                  );
                }).toList(),
                onChanged: (String? newValue) {
                  if (newValue != null && newValue != dropdownValue) {
                    _switchModel(newValue);
                  }
                },
              ),

              if (state.config.activeModelConfig != null) ...[
                const SizedBox(height: 16),
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.grey.shade100,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      SelectableText('Model Configuration', style: Theme.of(context).textTheme.titleSmall),
                      const SizedBox(height: 8),
                      SelectableText('Provider: ${state.config.activeModelConfig!.provider.value}'),
                      SelectableText('Context Window: ${state.config.activeModelConfig!.contextWindow}'),
                      SelectableText('Max Tokens: ${state.config.activeModelConfig!.maxTokens}'),
                      SelectableText('Temperature: ${state.config.activeModelConfig!.temperature}'),
                    ],
                  ),
                ),
              ],
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildStatusMonitoringCard(LlmDashboardState state) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SelectableText(
              'Provider Status',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 16),

            ...state.statuses
                .map(
                  (status) => ListTile(
                    leading: CircleAvatar(
                      backgroundColor: _getStatusColor(status),
                      child: Icon(_getStatusIcon(status), color: Colors.white),
                    ),
                    title: SelectableText(status.provider.value),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        SelectableText(_getStatusText(status)),
                        if (status.responseTimeMs != null)
                          SelectableText('Response time: ${status.responseTimeMs!.toStringAsFixed(0)}ms'),
                        if (status.errorMessage != null)
                          SelectableText(status.errorMessage!, style: const TextStyle(color: Colors.red)),
                      ],
                    ),
                    trailing: SelectableText(
                      _formatTime(status.lastCheckedAt),
                      style: Theme.of(context).textTheme.bodySmall,
                    ),
                  ),
                )
                .toList(),
          ],
        ),
      ),
    );
  }

  Widget _buildTestInterfaceCard(LlmDashboardState state) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SelectableText(
              'Test Interface',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 16),

            TextField(
              decoration: const InputDecoration(
                labelText: 'Test Prompt',
                hintText: 'Enter a test prompt for the LLM...',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
              onChanged: (value) {
                setState(() {
                  _testPrompt = value;
                });
              },
            ),
            const SizedBox(height: 12),

            ElevatedButton.icon(
              onPressed: _testLLM,
              icon: const Icon(Icons.send),
              label: const SelectableText('Test LLM'),
            ),

            if (_testResponse.isNotEmpty) ...[
              const SizedBox(height: 16),
              Container(
                width: double.infinity,
                constraints: const BoxConstraints(maxHeight: 200),
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.grey.shade100,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.grey.shade300),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        SelectableText('Response:', style: Theme.of(context).textTheme.titleSmall),
                        IconButton(
                          icon: const Icon(Icons.copy, size: 16),
                          onPressed: () {
                            // Copy response to clipboard
                            // This will be handled by the system selection
                          },
                          tooltip: 'Tap and drag to select text to copy',
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Expanded(
                      child: SingleChildScrollView(
                        child: Tooltip(
                          message: 'Tap and drag to select text',
                          child: SelectableText(
                            _testResponse,
                            style: const TextStyle(fontFamily: 'monospace'),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  // Helper methods
  Future<void> _configureOpenRouter() async {
    if (_apiKey.trim().isEmpty) {
      _showValidationError('Please enter an OpenRouter API key');
      return;
    }

    if (!_isValidOpenRouterKey(_apiKey)) {
      _showValidationError('Invalid API key format. OpenRouter keys should start with "sk-or-"');
      return;
    }

    if (_baseUrl.trim().isEmpty) {
      _showValidationError('Please enter a base URL');
      return;
    }

    try {
      await _llmService.configureOpenRouter(
        apiKey: _apiKey.trim(),
        baseUrl: _baseUrl.trim(),
        timeout: 30,
        maxRetries: 3,
      );

      await ref.read(llmDashboardProvider.notifier).refresh(force: true);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
              content: SelectableText('OpenRouter configured successfully! Models are now available.'),
              backgroundColor: Colors.green,
            ),
        );
      }
    } catch (e) {
      if (mounted) {
        final errorMessage = e.toString();
        String userFriendlyMessage;
        
        if (errorMessage.contains('401') || errorMessage.contains('unauthorized')) {
          userFriendlyMessage = 'Invalid API key. Please check your OpenRouter API key and try again.';
        } else if (errorMessage.contains('403') || errorMessage.contains('forbidden')) {
          userFriendlyMessage = 'Access denied. Please verify your API key has the necessary permissions.';
        } else if (errorMessage.contains('timeout') || errorMessage.contains('connection')) {
          userFriendlyMessage = 'Connection failed. Please check your internet connection and try again.';
        } else {
          userFriendlyMessage = 'Configuration failed: ${errorMessage.contains('Exception:') ? errorMessage.replaceAll('Exception: ', '') : errorMessage}';
        }
        
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content: SelectableText(userFriendlyMessage),
              backgroundColor: Colors.red,
            ),
        );
      }
    }
  }

  Future<void> _switchProvider(LLMProvider provider) async {
    try {
      await _llmService.setActiveProvider(provider);
      await ref.read(llmDashboardProvider.notifier).refresh(force: true);
      
      if (mounted) {
        final providerName = provider.value;
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: SelectableText('Successfully switched to $providerName'),
            backgroundColor: Colors.green,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: SelectableText('Failed to switch provider: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _switchModel(String modelName) async {
    try {
      final state = ref.read(llmDashboardProvider);
      final config = state.valueOrNull?.config;
      if (config != null) {
        await _llmService.setActiveModel(modelName, provider: config.activeProvider);
        await ref.read(llmDashboardProvider.notifier).refresh(force: true);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: SelectableText('Failed to switch model: $e')),
        );
      }
    }
  }

  Future<void> _testLLM() async {
    if (_testPrompt.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: SelectableText('Please enter a test prompt')),
      );
      return;
    }

    setState(() {
      _testResponse = '';
    });

    try {
      final testResponse = await _llmService.testLLM(_testPrompt);
      setState(() {
        _testResponse = testResponse.response;
      });
    } catch (e) {
      setState(() {
        _testResponse = 'Error: $e';
      });
    }
  }

  bool _isValidOpenRouterKey(String key) {
    return key.trim().startsWith('sk-or-') && key.trim().length > 20;
  }

  void _showValidationError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: SelectableText(message),
        backgroundColor: Colors.red,
      ),
    );
  }

  void _showOpenRouterConfigHelp() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const SelectableText('OpenRouter Configuration Help'),
          content: const Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              SelectableText('To configure OpenRouter:'),
              SizedBox(height: 8),
              SelectableText.rich(
                TextSpan(
                  children: [
                    TextSpan(text: '1. Get your API key from '),
                    TextSpan(
                      text: 'https://openrouter.ai/keys',
                      style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline),
                    ),
                  ],
                ),
              ),
              SelectableText('2. Enter it in the OpenRouter Configuration section above'),
              SelectableText('3. Click "Configure OpenRouter"'),
              SelectableText('4. Models will automatically load after successful configuration'),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const SelectableText('OK'),
            ),
          ],
        );
      },
    );
  }

  IconData _getProviderIcon(LLMProvider provider) {
    switch (provider) {
      case LLMProvider.local:
        return Icons.computer;
      case LLMProvider.openrouter:
        return Icons.cloud;
    }
  }

  Color _getProviderColor(LLMProvider provider) {
    switch (provider) {
      case LLMProvider.local:
        return Colors.blue;
      case LLMProvider.openrouter:
        return Colors.orange;
    }
  }

  Color _getStatusColor(LLMStatusResponse status) {
    if (!status.available) return Colors.red;
    if (!status.healthy) return Colors.orange;
    return Colors.green;
  }

  IconData _getStatusIcon(LLMStatusResponse status) {
    if (!status.available) return Icons.error;
    if (!status.healthy) return Icons.warning;
    return Icons.check_circle;
  }

  String _getStatusText(LLMStatusResponse status) {
    if (!status.available) return 'Unavailable';
    if (!status.healthy) return 'Unhealthy';
    return 'Healthy';
  }

  String _formatTime(DateTime dateTime) {
    return '${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}:${dateTime.second.toString().padLeft(2, '0')}';
  }

  bool _isProviderConfigured(LLMProvider provider) {
    final settings = ref.read(llmDashboardProvider).valueOrNull?.config.providers[provider.value];
    if (settings == null) return false;

    switch (provider) {
      case LLMProvider.local:
        return true;
      case LLMProvider.openrouter:
        return settings.hasApiKey;
    }
  }
}