import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/services/monitoring_api_service.dart';
import '../di/monitoring_providers.dart';
import '../domain/models/llm_provider_status.dart';

class LlmStatusCard extends ConsumerWidget {
  const LlmStatusCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final analyticsAsync = ref.watch(llmAnalyticsProvider);

    return Card(
      margin: const EdgeInsets.symmetric(vertical: 16, horizontal: 16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: analyticsAsync.when(
          data: (analytics) {
            final active = analytics.providers.firstWhere(
              (provider) => provider.active,
              orElse: () => analytics.providers.isNotEmpty
                  ? analytics.providers.first
                  : LlmProviderStatus(
                      id: 'unknown',
                      displayName: 'Not configured',
                      mode: 'n/a',
                      baseUrl: '',
                      available: false,
                      requiresApiKey: false,
                    ),
            );

            return Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      'LLM Analytics',
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    const SizedBox(width: 12),
                    _ModeChip(mode: active.mode),
                    const Spacer(),
                    IconButton(
                      tooltip: 'Check connection',
                      icon: const Icon(Icons.wifi_tethering),
                      onPressed: () => _checkConnection(context, ref),
                    ),
                    PopupMenuButton<LlmProviderStatus>(
                      tooltip: 'Switch provider',
                      onSelected: (provider) =>
                          _switchProvider(provider.id, ref),
                      itemBuilder: (context) => analytics.providers
                          .where((provider) => !provider.active)
                          .map(
                            (provider) => PopupMenuItem(
                              value: provider,
                              child: Text(provider.displayName),
                            ),
                          )
                          .toList(),
                      child: const Icon(Icons.swap_horiz),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Text(
                  active.displayName,
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 4),
                Text(
                  active.baseUrl.isEmpty
                      ? 'Endpoint not configured'
                      : active.baseUrl,
                  style: const TextStyle(color: Colors.grey),
                ),
                const SizedBox(height: 12),
                Wrap(
                  spacing: 12,
                  runSpacing: 8,
                  children: [
                    _infoChip(
                      label: 'Switches',
                      value: analytics.switches.toString(),
                    ),
                    _infoChip(
                      label: 'Last switch',
                      value: analytics.lastSwitchAt != null
                          ? analytics.lastSwitchAt!.toLocal().toString()
                          : 'Never',
                    ),
                    _infoChip(
                      label: 'Providers',
                      value: analytics.providers.length.toString(),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                _ProvidersList(providers: analytics.providers),
              ],
            );
          },
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (error, _) => Text(
            'Failed to load LLM analytics: $error',
            style: const TextStyle(color: Colors.red),
          ),
        ),
      ),
    );
  }

  Widget _infoChip({required String label, required String value}) {
    return Chip(
      label: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: const TextStyle(fontSize: 12)),
          Text(value, style: const TextStyle(fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }

  Future<void> _switchProvider(String providerId, WidgetRef ref) async {
    try {
      final service = ref.read(monitoringApiServiceProvider);
      await service.activateProvider(providerId);
      ref.invalidate(llmAnalyticsProvider);
    } catch (error) {
      ref.invalidate(llmAnalyticsProvider);
    }
  }

  Future<void> _checkConnection(BuildContext context, WidgetRef ref) async {
    final messenger = ScaffoldMessenger.of(context);
    try {
      final service = ref.read(monitoringApiServiceProvider);
      final result = await service.checkConnectivity();
      messenger.showSnackBar(
        SnackBar(
          content: Text(
            result.success
                ? 'Connected to ${result.providerName} in ${result.latencyMs} ms'
                : 'LLM probe failed (${result.statusCode}): ${result.message}',
          ),
        ),
      );
    } catch (error) {
      messenger.showSnackBar(
        SnackBar(content: Text('Connectivity check failed: $error')),
      );
    }
  }
}

class _ProvidersList extends StatelessWidget {
  final List<LlmProviderStatus> providers;

  const _ProvidersList({required this.providers});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Configured Providers',
          style: Theme.of(context).textTheme.titleSmall,
        ),
        const SizedBox(height: 8),
        ...providers.map(
          (provider) => ListTile(
            contentPadding: EdgeInsets.zero,
            leading: Icon(
              provider.active
                  ? Icons.radio_button_checked
                  : Icons.radio_button_off,
              color: provider.active ? Colors.green : Colors.grey,
            ),
            title: Text(provider.displayName),
            subtitle: Text(
              '${provider.mode.toUpperCase()} • ${provider.baseUrl}',
            ),
            trailing: provider.requiresApiKey
                ? const Icon(Icons.vpn_key, size: 18, color: Colors.orange)
                : null,
          ),
        ),
      ],
    );
  }
}

class _ModeChip extends StatelessWidget {
  final String mode;

  const _ModeChip({required this.mode});

  @override
  Widget build(BuildContext context) {
    final isRemote = mode.toLowerCase() == 'remote';
    return Chip(
      avatar: Icon(
        isRemote ? Icons.cloud_outlined : Icons.memory,
        size: 18,
        color: isRemote ? Colors.blue : Colors.green,
      ),
      label: Text(isRemote ? 'Remote API' : 'Local model'),
    );
  }
}
