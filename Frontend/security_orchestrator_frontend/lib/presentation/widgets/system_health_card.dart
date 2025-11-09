import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/monitoring_models.dart';

class SystemHealthCard extends ConsumerWidget {
  final Map<String, dynamic> systemHealth;

  const SystemHealthCard({
    super.key,
    required this.systemHealth,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final health = SystemHealth.fromJson(systemHealth);
    
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  _getStatusIcon(health.overallStatus),
                  color: _getStatusColor(health.overallStatus),
                  size: 24,
                ),
                const SizedBox(width: 8),
                Text(
                  'System Health',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 4,
                  ),
                  decoration: BoxDecoration(
                    color: _getStatusColor(health.overallStatus).withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: _getStatusColor(health.overallStatus),
                    ),
                  ),
                  child: Text(
                    health.overallStatus,
                    style: TextStyle(
                      color: _getStatusColor(health.overallStatus),
                      fontWeight: FontWeight.bold,
                      fontSize: 12,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _buildHealthMetric(
                    context,
                    'CPU Usage',
                    '${health.cpuUsage.toStringAsFixed(1)}%',
                    Icons.memory,
                    _getUsageColor(health.cpuUsage),
                  ),
                ),
                Expanded(
                  child: _buildHealthMetric(
                    context,
                    'Memory Usage',
                    '${health.memoryUsage.toStringAsFixed(1)}%',
                    Icons.storage,
                    _getUsageColor(health.memoryUsage),
                  ),
                ),
                Expanded(
                  child: _buildHealthMetric(
                    context,
                    'Disk Usage',
                    '${health.diskUsage.toStringAsFixed(1)}%',
                    Icons.hard_drive,
                    _getUsageColor(health.diskUsage),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _buildInfoMetric(
                    context,
                    'DB Status',
                    health.databaseConnectionStatus,
                    Icons.database,
                    health.databaseConnectionStatus == 'CONNECTED' 
                        ? Colors.green 
                        : Colors.red,
                  ),
                ),
                Expanded(
                  child: _buildInfoMetric(
                    context,
                    'Avg Response',
                    '${health.averageResponseTime.toStringAsFixed(0)}ms',
                    Icons.speed,
                    Colors.blue,
                  ),
                ),
                Expanded(
                  child: _buildInfoMetric(
                    context,
                    'Active Models',
                    health.activeModelCount.toString(),
                    Icons.model_training,
                    Colors.purple,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              'Last Updated: ${_formatTimestamp(health.timestamp)}',
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Colors.grey[600],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHealthMetric(
    BuildContext context,
    String label,
    String value,
    IconData icon,
    Color color,
  ) {
    return Column(
      children: [
        Icon(
          icon,
          color: color,
          size: 20,
        ),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
            color: color,
            fontWeight: FontWeight.bold,
          ),
        ),
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall,
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildInfoMetric(
    BuildContext context,
    String label,
    String value,
    IconData icon,
    Color color,
  ) {
    return Column(
      children: [
        Icon(
          icon,
          color: color,
          size: 18,
        ),
        const SizedBox(height: 2),
        Text(
          value,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: color,
            fontWeight: FontWeight.w600,
          ),
        ),
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall,
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  IconData _getStatusIcon(String status) {
    switch (status) {
      case 'CRITICAL':
        return Icons.error;
      case 'WARNING':
        return Icons.warning;
      case 'DEGRADED':
        return Icons.info;
      default:
        return Icons.check_circle;
    }
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'CRITICAL':
        return Colors.red;
      case 'WARNING':
        return Colors.orange;
      case 'DEGRADED':
        return Colors.amber;
      default:
        return Colors.green;
    }
  }

  Color _getUsageColor(double usage) {
    if (usage > 80) {
      return Colors.red;
    } else if (usage > 60) {
      return Colors.orange;
    } else if (usage > 40) {
      return Colors.amber;
    } else {
      return Colors.green;
    }
  }

  String _formatTimestamp(DateTime timestamp) {
    final now = DateTime.now();
    final difference = now.difference(timestamp);
    
    if (difference.inSeconds < 60) {
      return 'just now';
    } else if (difference.inMinutes < 60) {
      return '${difference.inMinutes}m ago';
    } else if (difference.inHours < 24) {
      return '${difference.inHours}h ago';
    } else {
      return '${difference.inDays}d ago';
    }
  }
}