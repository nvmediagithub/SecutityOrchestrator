import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../di/monitoring_providers.dart';
import '../domain/models/system_health.dart';

class SystemHealthCard extends ConsumerWidget {
  const SystemHealthCard({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final systemHealthAsync = ref.watch(systemHealthProvider);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: systemHealthAsync.when(
          data: (systemHealth) =>
              _SystemHealthDetails(systemHealth: systemHealth),
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (error, _) => Padding(
            padding: const EdgeInsets.symmetric(vertical: 16),
            child: Text(
              'Failed to load system health: $error',
              style: const TextStyle(color: Colors.redAccent),
            ),
          ),
        ),
      ),
    );
  }
}

class _SystemHealthDetails extends StatelessWidget {
  final SystemHealth systemHealth;

  const _SystemHealthDetails({required this.systemHealth});

  @override
  Widget build(BuildContext context) {
    final (icon, color) = _statusVisuals(systemHealth.status);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(icon, color: color, size: 24),
            const SizedBox(width: 12),
            Text(
              'System Health',
              style: Theme.of(context).textTheme.titleLarge,
            ),
          ],
        ),
        const SizedBox(height: 16),
        _buildHealthRow(
          'CPU Usage',
          '${systemHealth.cpuUsage.toStringAsFixed(1)}%',
        ),
        _buildHealthRow(
          'Memory Usage',
          '${systemHealth.memoryUsage.toStringAsFixed(1)}%',
        ),
        _buildHealthRow(
          'Disk Usage',
          '${systemHealth.diskUsage.toStringAsFixed(1)}%',
        ),
        _buildHealthRow(
          'Active Connections',
          '${systemHealth.activeConnections}',
        ),
        const SizedBox(height: 8),
        Text(
          systemHealth.details,
          style: TextStyle(color: Colors.grey.shade600, fontSize: 12),
        ),
      ],
    );
  }

  Widget _buildHealthRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(fontSize: 14)),
          Text(
            value,
            style: const TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
          ),
        ],
      ),
    );
  }

  (IconData, Color) _statusVisuals(HealthStatus status) {
    switch (status) {
      case HealthStatus.healthy:
        return (Icons.check_circle, Colors.green);
      case HealthStatus.degraded:
        return (Icons.warning, Colors.orange);
      case HealthStatus.unhealthy:
        return (Icons.error, Colors.red);
    }
  }
}
