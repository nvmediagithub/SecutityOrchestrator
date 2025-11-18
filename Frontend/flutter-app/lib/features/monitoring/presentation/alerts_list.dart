import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../di/monitoring_providers.dart';
import '../domain/models/alert.dart';

class AlertsList extends ConsumerWidget {
  const AlertsList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final alertsAsync = ref.watch(alertsProvider);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Active Alerts',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                alertsAsync.maybeWhen(
                  data: (alerts) => Text(
                    '${alerts.where((a) => a.status == AlertStatus.active).length} active',
                    style: TextStyle(color: Colors.grey.shade600),
                  ),
                  orElse: () => const SizedBox.shrink(),
                ),
              ],
            ),
            const SizedBox(height: 16),
            alertsAsync.when(
              data: (alerts) => alerts.isNotEmpty
                  ? Column(children: alerts.map(_buildAlertItem).toList())
                  : const Text(
                      'No active alerts',
                      style: TextStyle(color: Colors.grey),
                    ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, _) => Text(
                'Failed to load alerts: $error',
                style: const TextStyle(color: Colors.redAccent),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAlertItem(Alert alert) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: alert.color.withOpacity(0.1),
        border: Border.all(color: alert.color.withOpacity(0.3)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Icon(alert.icon, color: alert.color, size: 24),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        alert.title,
                        style: const TextStyle(fontWeight: FontWeight.w500),
                      ),
                    ),
                  ],
                ),
                Text(
                  alert.message,
                  style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
                ),
                const SizedBox(height: 4),
                Text(
                  'From ${alert.source} • ${alert.timestamp.toString().substring(11, 19)}',
                  style: TextStyle(color: Colors.grey.shade500, fontSize: 10),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
