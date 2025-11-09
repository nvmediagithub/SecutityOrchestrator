import 'package:flutter/material.dart';
import '../../data/models/monitoring_models.dart';

class MetricsGrid extends StatelessWidget {
  final Map<String, dynamic> metricsReport;

  const MetricsGrid({
    super.key,
    required this.metricsReport,
  });

  @override
  Widget build(BuildContext context) {
    final metrics = MetricsReport.fromJson(metricsReport);
    
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Performance Overview',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 16),
            GridView.count(
              crossAxisCount: 4,
              crossAxisSpacing: 16,
              mainAxisSpacing: 16,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              childAspectRatio: 1.2,
              children: [
                _buildMetricCard(
                  context,
                  'API Success Rate',
                  '${(metrics.apiSuccessRate * 100).toStringAsFixed(1)}%',
                  Icons.check_circle,
                  _getSuccessRateColor(metrics.apiSuccessRate),
                ),
                _buildMetricCard(
                  context,
                  'Total Requests',
                  metrics.totalApiRequests.toString(),
                  Icons.trending_up,
                  Colors.blue,
                ),
                _buildMetricCard(
                  context,
                  'Active Users',
                  metrics.activeUsers.toString(),
                  Icons.people,
                  Colors.green,
                ),
                _buildMetricCard(
                  context,
                  'Uptime',
                  _formatUptime(metrics.uptime),
                  Icons.timer,
                  Colors.purple,
                ),
                _buildMetricCard(
                  context,
                  'CPU Usage',
                  '${metrics.systemResourceUsage.cpuUsage.toStringAsFixed(1)}%',
                  Icons.memory,
                  _getUsageColor(metrics.systemResourceUsage.cpuUsage),
                ),
                _buildMetricCard(
                  context,
                  'Memory Usage',
                  '${metrics.systemResourceUsage.memoryUsage.toStringAsFixed(1)}%',
                  Icons.storage,
                  _getUsageColor(metrics.systemResourceUsage.memoryUsage),
                ),
                _buildMetricCard(
                  context,
                  'Active Connections',
                  metrics.systemResourceUsage.activeConnections.toString(),
                  Icons.link,
                  Colors.orange,
                ),
                _buildMetricCard(
                  context,
                  'Thread Count',
                  metrics.systemResourceUsage.threadCount.toString(),
                  Icons.code,
                  Colors.teal,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMetricCard(
    BuildContext context,
    String title,
    String value,
    IconData icon,
    Color color,
  ) {
    return Container(
      decoration: BoxDecoration(
        border: Border.all(
          color: color.withOpacity(0.3),
          width: 1,
        ),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              icon,
              color: color,
              size: 24,
            ),
            const SizedBox(height: 8),
            Text(
              value,
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: color,
                fontWeight: FontWeight.bold,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 4),
            Text(
              title,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Colors.grey[600],
              ),
              textAlign: TextAlign.center,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
          ],
        ),
      ),
    );
  }

  Color _getSuccessRateColor(double rate) {
    if (rate >= 0.99) {
      return Colors.green;
    } else if (rate >= 0.95) {
      return Colors.orange;
    } else {
      return Colors.red;
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

  String _formatUptime(Duration uptime) {
    final days = uptime.inDays;
    final hours = uptime.inHours % 24;
    final minutes = uptime.inMinutes % 60;
    
    if (days > 0) {
      return '${days}d ${hours}h';
    } else if (hours > 0) {
      return '${hours}h ${minutes}m';
    } else {
      return '${minutes}m';
    }
  }
}