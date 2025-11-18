import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../domain/models/system_health.dart';

// Placeholder provider for system health - should be implemented with actual service
final systemHealthProvider = NotifierProvider<SystemHealthNotifier, SystemHealth?>(() {
  return SystemHealthNotifier();
});

class SystemHealthNotifier extends Notifier<SystemHealth?> {
  @override
  SystemHealth? build() {
    // Initialize with sample data
    loadSystemHealth();
    return null;
  }

  void loadSystemHealth() {
    // Placeholder - in real implementation, fetch from service
    state = SystemHealth(
      id: '1',
      status: HealthStatus.healthy,
      cpuUsage: 45.5,
      memoryUsage: 67.8,
      diskUsage: 23.4,
      activeConnections: 42,
      timestamp: DateTime.now(),
      details: 'All systems operational',
    );
  }
}

class SystemHealthCard extends ConsumerWidget {
  const SystemHealthCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final systemHealth = ref.watch(systemHealthProvider);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  systemHealth?.status == HealthStatus.healthy
                      ? Icons.check_circle
                      : systemHealth?.status == HealthStatus.degraded
                          ? Icons.warning
                          : Icons.error,
                  color: systemHealth?.status == HealthStatus.healthy
                      ? Colors.green
                      : systemHealth?.status == HealthStatus.degraded
                          ? Colors.orange
                          : Colors.red,
                  size: 24,
                ),
                const SizedBox(width: 12),
                Text(
                  'System Health',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (systemHealth != null) ...[
              _buildHealthRow('CPU Usage', '${systemHealth.cpuUsage.toStringAsFixed(1)}%'),
              _buildHealthRow('Memory Usage', '${systemHealth.memoryUsage.toStringAsFixed(1)}%'),
              _buildHealthRow('Disk Usage', '${systemHealth.diskUsage.toStringAsFixed(1)}%'),
              _buildHealthRow('Active Connections', '${systemHealth.activeConnections}'),
              const SizedBox(height: 8),
              Text(
                systemHealth.details,
                style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
              ),
            ] else
              const Center(child: CircularProgressIndicator()),
          ],
        ),
      ),
    );
  }

  Widget _buildHealthRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(fontSize: 14)),
          Text(value, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500)),
        ],
      ),
    );
  }
}