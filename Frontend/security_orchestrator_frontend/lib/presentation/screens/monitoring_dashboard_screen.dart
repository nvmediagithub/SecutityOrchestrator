import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/monitoring_provider.dart';
import '../widgets/realtime_chart.dart';
import '../widgets/system_health_card.dart';
import '../widgets/metrics_grid.dart';
import '../widgets/alerts_panel.dart';
import '../widgets/models_status_card.dart';

class MonitoringDashboardScreen extends ConsumerWidget {
  const MonitoringDashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final dashboardAsync = ref.watch(monitoringDashboardProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('System Monitoring'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () {
              ref.refresh(monitoringDashboardProvider);
            },
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              _showSettingsDialog(context, ref);
            },
          ),
        ],
      ),
      body: dashboardAsync.when(
        data: (dashboard) => _buildDashboardContent(context, ref, dashboard),
        loading: () => const Center(
          child: CircularProgressIndicator(),
        ),
        error: (error, stack) => _buildErrorState(context, error),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ref.refresh(monitoringDashboardProvider);
        },
        child: const Icon(Icons.refresh),
      ),
    );
  }

  Widget _buildDashboardContent(BuildContext context, WidgetRef ref, dynamic dashboard) {
    return RefreshIndicator(
      onRefresh: () async {
        ref.refresh(monitoringDashboardProvider);
      },
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // System Health Overview
            SystemHealthCard(
              systemHealth: dashboard['systemHealth'],
            ),
            const SizedBox(height: 16),
            
            // Real-time Metrics Grid
            MetricsGrid(
              metricsReport: dashboard['metricsReport'],
            ),
            const SizedBox(height: 16),
            
            // Performance Charts Row
            Row(
              children: [
                Expanded(
                  child: RealTimeChart(
                    title: 'CPU Usage',
                    metricType: 'cpu_usage',
                    color: Colors.red,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: RealTimeChart(
                    title: 'Memory Usage',
                    metricType: 'memory_usage',
                    color: Colors.blue,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            
            // Response Time Chart
            RealTimeChart(
              title: 'API Response Time (ms)',
              metricType: 'api_response_time',
              color: Colors.green,
            ),
            const SizedBox(height: 16),
            
            // Models Status and Alerts Row
            Row(
              children: [
                Expanded(
                  child: ModelsStatusCard(
                    activeModels: dashboard['activeModels'] ?? [],
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: AlertsPanel(
                    alerts: dashboard['activeAlerts'] ?? [],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            
            // LLM Performance Chart
            RealTimeChart(
              title: 'LLM Response Time (ms)',
              metricType: 'llm_response_time',
              color: Colors.purple,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildErrorState(BuildContext context, Object error) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.error_outline,
            size: 64,
            color: Theme.of(context).colorScheme.error,
          ),
          const SizedBox(height: 16),
          Text(
            'Error loading monitoring data',
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
            onPressed: () {
              // Retry loading
            },
            child: const Text('Retry'),
          ),
        ],
      ),
    );
  }

  void _showSettingsDialog(BuildContext context, WidgetRef ref) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Monitoring Settings'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              title: const Text('Auto Refresh Interval'),
              subtitle: const Text('5 seconds'),
              trailing: Icon(Icons.chevron_right),
            ),
            ListTile(
              title: const Text('Chart Time Range'),
              subtitle: const Text('Last 1 hour'),
              trailing: Icon(Icons.chevron_right),
            ),
            ListTile(
              title: const Text('Alert Notifications'),
              subtitle: const Text('Enabled'),
              trailing: Switch(
                value: true,
                onChanged: (value) {
                  // Toggle notifications
                },
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }
}