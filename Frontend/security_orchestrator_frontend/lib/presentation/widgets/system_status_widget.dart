// System Status Widgets for SecurityOrchestrator LLM Service
// Real-time monitoring widgets for system status and health

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/system_status_model.dart';
import '../providers/system_dashboard_provider.dart';

/// Main system status widget for detailed view
class SystemStatusDetailedWidget extends ConsumerWidget {
  const SystemStatusDetailedWidget({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final dashboardAsync = ref.watch(systemDashboardProvider);

    return dashboardAsync.when(
      loading: () => const SystemStatusLoadingScreen(),
      error: (error, stackTrace) => SystemStatusErrorScreen(
        error: error.toString(),
        onRetry: () => ref.read(systemDashboardProvider.notifier).refresh(),
      ),
      data: (dashboard) => SystemStatusDetailScreen(dashboard: dashboard),
    );
  }
}

/// System status loading screen
class SystemStatusLoadingScreen extends StatelessWidget {
  const SystemStatusLoadingScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          CircularProgressIndicator(),
          SizedBox(height: 16),
          Text('Loading system status...'),
        ],
      ),
    );
  }
}

/// System status error screen
class SystemStatusErrorScreen extends StatelessWidget {
  final String error;
  final VoidCallback onRetry;

  const SystemStatusErrorScreen({
    super.key,
    required this.error,
    required this.onRetry,
  });

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 64, color: Colors.red),
          const SizedBox(height: 16),
          Text(
            'System Status Error',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 8),
          Text(
            error,
            style: Theme.of(context).textTheme.bodyMedium,
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: onRetry,
            child: const Text('Retry'),
          ),
        ],
      ),
    );
  }
}

/// Detailed system status screen with all components
class SystemStatusDetailScreen extends ConsumerWidget {
  final SystemDashboardData dashboard;

  const SystemStatusDetailScreen({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return RefreshIndicator(
      onRefresh: () => ref.read(systemDashboardProvider.notifier).refresh(),
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Overall system health
            SystemHealthCard(dashboard: dashboard),
            const SizedBox(height: 16),
            
            // SecurityOrchestrator LLM Service status
            LLMServicesCard(dashboard: dashboard),
            const SizedBox(height: 16),
            
            // CodeLlama 7B model status
            CodeLlamaModelCard(dashboard: dashboard),
            const SizedBox(height: 16),
            
            // Ollama connection status
            OllamaConnectionCard(dashboard: dashboard),
            const SizedBox(height: 16),
            
            // System metrics
            SystemMetricsCard(dashboard: dashboard),
            const SizedBox(height: 16),
            
            // Action buttons
            ActionButtonsCard(dashboard: dashboard),
          ],
        ),
      ),
    );
  }
}

/// System health overview card
class SystemHealthCard extends StatelessWidget {
  final SystemDashboardData dashboard;

  const SystemHealthCard({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  dashboard.isSystemHealthy ? Icons.check_circle : Icons.warning,
                  color: dashboard.isSystemHealthy ? Colors.green : Colors.orange,
                  size: 32,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'System Health',
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                      Text(
                        dashboard.isSystemHealthy 
                          ? 'All systems operational' 
                          : 'Issues detected',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: dashboard.isSystemHealthy ? Colors.green : Colors.orange,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            
            // Health indicators
            _buildHealthIndicator(context, 'SecurityOrchestrator LLM', dashboard.systemStatus.isHealthy),
            const SizedBox(height: 8),
            _buildHealthIndicator(context, 'LLM Services', dashboard.llmStatus.isOperational),
            const SizedBox(height: 8),
            _buildHealthIndicator(context, 'Ollama Connection', dashboard.ollamaStatus.isAccessible),
          ],
        ),
      ),
    );
  }

  Widget _buildHealthIndicator(BuildContext context, String serviceName, bool isHealthy) {
    return Row(
      children: [
        Icon(
          isHealthy ? Icons.check : Icons.close,
          color: isHealthy ? Colors.green : Colors.red,
          size: 16,
        ),
        const SizedBox(width: 8),
        Text(
          serviceName,
          style: Theme.of(context).textTheme.bodyMedium,
        ),
        const Spacer(),
        Text(
          isHealthy ? 'Healthy' : 'Unhealthy',
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: isHealthy ? Colors.green : Colors.red,
          ),
        ),
      ],
    );
  }
}

/// LLM services status card
class LLMServicesCard extends StatelessWidget {
  final SystemDashboardData dashboard;

  const LLMServicesCard({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'LLM Services',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 12),
            _buildServiceRow(context, 'Service', dashboard.llmStatus.service),
            _buildServiceRow(context, 'Status', dashboard.llmStatus.status),
            _buildServiceRow(context, 'Port', dashboard.llmStatus.port),
            _buildServiceRow(context, 'Integration', dashboard.llmStatus.integration),
            _buildServiceRow(context, 'Version', dashboard.llmStatus.version),
            _buildServiceRow(context, 'Completion', dashboard.llmStatus.completion),
            _buildServiceRow(context, 'Ready', dashboard.llmStatus.ready ? 'Yes' : 'No'),
          ],
        ),
      ),
    );
  }

  Widget _buildServiceRow(BuildContext context, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        children: [
          SizedBox(
            width: 120,
            child: Text(
              '$label:',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: Theme.of(context).textTheme.bodyMedium,
            ),
          ),
        ],
      ),
    );
  }
}

/// CodeLlama model status card
class CodeLlamaModelCard extends StatelessWidget {
  final SystemDashboardData dashboard;

  const CodeLlamaModelCard({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    final model = dashboard.codeLlamaStatus;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  model.isActive ? Icons.smart_toy : Icons.smart_toy_outlined,
                  color: model.isActive ? Colors.blue : Colors.grey,
                ),
                const SizedBox(width: 8),
                Text(
                  'CodeLlama 7B Model',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: model.isActive ? Colors.green : Colors.red,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    model.isActive ? 'Active' : 'Inactive',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            _buildModelInfoRow(context, 'Model Name', model.modelName),
            _buildModelInfoRow(context, 'Model Type', model.modelType),
            _buildModelInfoRow(context, 'Loaded', model.loaded ? 'Yes' : 'No'),
            _buildModelInfoRow(context, 'Size', '${model.sizeGb} GB'),
            _buildModelInfoRow(context, 'Context Window', '${model.contextWindow} tokens'),
            _buildModelInfoRow(context, 'Max Tokens', '${model.maxTokens} tokens'),
            _buildModelInfoRow(context, 'Status', model.status ?? 'Unknown'),
          ],
        ),
      ),
    );
  }

  Widget _buildModelInfoRow(BuildContext context, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        children: [
          SizedBox(
            width: 120,
            child: Text(
              '$label:',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: Theme.of(context).textTheme.bodyMedium,
            ),
          ),
        ],
      ),
    );
  }
}

/// Ollama connection status card
class OllamaConnectionCard extends StatelessWidget {
  final SystemDashboardData dashboard;

  const OllamaConnectionCard({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    final ollama = dashboard.ollamaStatus;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  ollama.isAccessible ? Icons.cloud_done : Icons.cloud_off,
                  color: ollama.isAccessible ? Colors.green : Colors.red,
                ),
                const SizedBox(width: 8),
                Text(
                  'Ollama Connection',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: ollama.isAccessible ? Colors.green : Colors.red,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    ollama.isAccessible ? 'Connected' : 'Disconnected',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            _buildConnectionRow(context, 'URL', ollama.ollamaUrl),
            _buildConnectionRow(context, 'Status', ollama.status),
            _buildConnectionRow(context, 'Message', ollama.message),
            if (ollama.error != null) ...[
              const SizedBox(height: 8),
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.red.shade50,
                  border: Border.all(color: Colors.red.shade200),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.error, color: Colors.red, size: 16),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        ollama.error!,
                        style: const TextStyle(color: Colors.red, fontSize: 12),
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

  Widget _buildConnectionRow(BuildContext context, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 80,
            child: Text(
              '$label:',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: Theme.of(context).textTheme.bodyMedium,
            ),
          ),
        ],
      ),
    );
  }
}

/// System metrics card
class SystemMetricsCard extends StatelessWidget {
  final SystemDashboardData dashboard;

  const SystemMetricsCard({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'System Metrics',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 12),
            _buildMetricRow(
              context, 
              'Services Online', 
              '${dashboard.criticalServicesStatus.where((s) => s).length}/${dashboard.criticalServicesStatus.length}',
            ),
            _buildMetricRow(
              context, 
              'System Health', 
              '${dashboard.isSystemHealthy ? 100 : 0}%',
            ),
            _buildMetricRow(
              context, 
              'Last Updated', 
              _formatTime(dashboard.lastUpdated),
            ),
            if (dashboard.errorMessage != null) ...[
              const SizedBox(height: 8),
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.orange.shade50,
                  border: Border.all(color: Colors.orange.shade200),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.warning, color: Colors.orange, size: 16),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        dashboard.errorMessage!,
                        style: const TextStyle(color: Colors.orange, fontSize: 12),
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

  Widget _buildMetricRow(BuildContext context, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          Expanded(
            child: Text(
              label,
              style: Theme.of(context).textTheme.bodyMedium,
            ),
          ),
          Text(
            value,
            style: Theme.of(context).textTheme.bodyMedium?.copyWith(
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  String _formatTime(DateTime time) {
    final now = DateTime.now();
    final difference = now.difference(time);
    
    if (difference.inMinutes < 1) {
      return 'Just now';
    } else if (difference.inMinutes < 60) {
      return '${difference.inMinutes}m ago';
    } else if (difference.inHours < 24) {
      return '${difference.inHours}h ago';
    } else {
      return '${difference.inDays}d ago';
    }
  }
}

/// Action buttons card for system operations
class ActionButtonsCard extends ConsumerWidget {
  final SystemDashboardData dashboard;

  const ActionButtonsCard({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Actions',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () => _testLLMIntegration(context, ref),
                    icon: const Icon(Icons.science),
                    label: const Text('Test LLM'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () => _checkCompletion(context, ref),
                    icon: const Icon(Icons.check_circle),
                    label: const Text('Check Status'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            SizedBox(
              width: double.infinity,
              child: OutlinedButton.icon(
                onPressed: () => _refreshAll(context, ref),
                icon: const Icon(Icons.refresh),
                label: const Text('Refresh All'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _testLLMIntegration(BuildContext context, WidgetRef ref) async {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => const Center(
        child: CircularProgressIndicator(),
      ),
    );

    try {
      final result = await ref.read(systemDashboardProvider.notifier).testLLMIntegration();
      Navigator.of(context).pop();
      
      if (context.mounted) {
        _showResultDialog(context, 'LLM Integration Test', result);
      }
    } catch (e) {
      Navigator.of(context).pop();
      if (context.mounted) {
        _showErrorDialog(context, 'Test failed: $e');
      }
    }
  }

  void _checkCompletion(BuildContext context, WidgetRef ref) async {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => const Center(
        child: CircularProgressIndicator(),
      ),
    );

    try {
      final result = await ref.read(systemDashboardProvider.notifier).checkCompletionStatus();
      Navigator.of(context).pop();
      
      if (context.mounted) {
        _showResultDialog(context, 'Completion Status', result);
      }
    } catch (e) {
      Navigator.of(context).pop();
      if (context.mounted) {
        _showErrorDialog(context, 'Check failed: $e');
      }
    }
  }

  void _refreshAll(BuildContext context, WidgetRef ref) {
    ref.read(systemDashboardProvider.notifier).refresh();
  }

  void _showResultDialog(BuildContext context, String title, Map<String, dynamic> result) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: result.entries.map((entry) {
              return Padding(
                padding: const EdgeInsets.symmetric(vertical: 2),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    SizedBox(
                      width: 80,
                      child: Text(
                        '${entry.key}:',
                        style: const TextStyle(fontWeight: FontWeight.w500),
                      ),
                    ),
                    Expanded(
                      child: Text(entry.value.toString()),
                    ),
                  ],
                ),
              );
            }).toList(),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  void _showErrorDialog(BuildContext context, String message) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Error'),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }
}

/// LLM Status widget for the LLM tab
class LLMStatusWidget extends ConsumerWidget {
  const LLMStatusWidget({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final dashboardAsync = ref.watch(systemDashboardProvider);

    return dashboardAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stackTrace) => Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64, color: Colors.red),
            const SizedBox(height: 16),
            Text(
              'Error loading LLM status',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 8),
            Text(
              error.toString(),
              style: Theme.of(context).textTheme.bodyMedium,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => ref.read(systemDashboardProvider.notifier).refresh(),
              child: const Text('Retry'),
            ),
          ],
        ),
      ),
      data: (dashboard) => LLMStatusScreen(dashboard: dashboard),
    );
  }
}

/// LLM Status screen with focused LLM information
class LLMStatusScreen extends StatelessWidget {
  final SystemDashboardData dashboard;

  const LLMStatusScreen({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          // CodeLlama model status
          CodeLlamaModelCard(dashboard: dashboard),
          const SizedBox(height: 16),
          
          // LLM services overview
          LLMServicesCard(dashboard: dashboard),
          const SizedBox(height: 16),
          
          // Integration status
          IntegrationStatusCard(dashboard: dashboard),
        ],
      ),
    );
  }
}

/// Integration status card
class IntegrationStatusCard extends StatelessWidget {
  final SystemDashboardData dashboard;

  const IntegrationStatusCard({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Integration Status',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 12),
            _buildIntegrationRow(
              context, 
              'SecurityOrchestrator LLM', 
              dashboard.llmStatus.isOperational,
              'Port ${dashboard.llmStatus.port}',
            ),
            const SizedBox(height: 8),
            _buildIntegrationRow(
              context, 
              'Ollama Backend', 
              dashboard.ollamaStatus.isAccessible,
              'CodeLlama 7B Host',
            ),
            const SizedBox(height: 8),
            _buildIntegrationRow(
              context, 
              'CodeLlama 7B Model', 
              dashboard.codeLlamaStatus.isActive,
              '${dashboard.codeLlamaStatus.modelName}',
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildIntegrationRow(BuildContext context, String service, bool isActive, String details) {
    return Row(
      children: [
        Icon(
          isActive ? Icons.check_circle : Icons.cancel,
          color: isActive ? Colors.green : Colors.red,
          size: 20,
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                service,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
              Text(
                details,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: Colors.grey,
                ),
              ),
            ],
          ),
        ),
        Text(
          isActive ? 'Active' : 'Inactive',
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: isActive ? Colors.green : Colors.red,
            fontWeight: FontWeight.w600,
          ),
        ),
      ],
    );
  }
}