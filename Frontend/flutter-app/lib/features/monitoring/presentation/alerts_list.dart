import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../domain/models/alert.dart';

// Placeholder provider for alerts - should be implemented with actual service
final alertsProvider = NotifierProvider<AlertsNotifier, List<Alert>>(() {
  return AlertsNotifier();
});

class AlertsNotifier extends Notifier<List<Alert>> {
  @override
  List<Alert> build() {
    // Initialize with sample data
    return [
      Alert(
        id: '1',
        title: 'High CPU Usage',
        description: 'CPU usage has exceeded 80% for the last 5 minutes',
        severity: AlertSeverity.high,
        status: AlertStatus.active,
        source: 'System Monitor',
        createdAt: DateTime.now().subtract(const Duration(minutes: 5)),
        tags: 'performance,cpu',
      ),
      Alert(
        id: '2',
        title: 'Disk Space Low',
        description: 'Available disk space is below 10%',
        severity: AlertSeverity.medium,
        status: AlertStatus.active,
        source: 'Storage Monitor',
        createdAt: DateTime.now().subtract(const Duration(hours: 1)),
        tags: 'storage,disk',
      ),
    ];
  }

  void acknowledgeAlert(String alertId) {
    state = state.map((alert) {
      if (alert.id == alertId) {
        return Alert(
          id: alert.id,
          title: alert.title,
          description: alert.description,
          severity: alert.severity,
          status: AlertStatus.resolved,
          source: alert.source,
          createdAt: alert.createdAt,
          resolvedAt: DateTime.now(),
          tags: alert.tags,
        );
      }
      return alert;
    }).toList();
  }
}

class AlertsList extends ConsumerWidget {
  const AlertsList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final alerts = ref.watch(alertsProvider);
    final alertsNotifier = ref.read(alertsProvider.notifier);

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
                Text(
                  '${alerts.where((a) => a.status == AlertStatus.active).length} active',
                  style: TextStyle(color: Colors.grey.shade600),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (alerts.isNotEmpty) ...[
              ...alerts.map((alert) => _buildAlertItem(alert, alertsNotifier)),
            ] else
              const Text(
                'No active alerts',
                style: TextStyle(color: Colors.grey),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildAlertItem(Alert alert, AlertsNotifier notifier) {
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
          Icon(
            alert.icon,
            color: alert.color,
            size: 24,
          ),
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
                    if (!alert.isAcknowledged)
                      TextButton(
                        onPressed: () => notifier.acknowledgeAlert(alert.id),
                        child: const Text('Acknowledge'),
                        style: TextButton.styleFrom(
                          foregroundColor: alert.color,
                          textStyle: const TextStyle(fontSize: 12),
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