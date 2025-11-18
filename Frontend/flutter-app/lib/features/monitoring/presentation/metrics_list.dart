import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../domain/models/metric.dart';

// Placeholder provider for metrics - should be implemented with actual service
final metricsProvider = NotifierProvider<MetricsNotifier, List<Metric>>(() {
  return MetricsNotifier();
});

class MetricsNotifier extends Notifier<List<Metric>> {
  @override
  List<Metric> build() {
    // Initialize with sample data
    loadMetrics();
    return [];
  }

  void loadMetrics() {
    state = [
      Metric(
        id: '1',
        name: 'Response Time',
        type: MetricType.responseTime,
        value: 245.5,
        unit: 'ms',
        timestamp: DateTime.now(),
        description: 'Average response time',
      ),
      Metric(
        id: '2',
        name: 'Throughput',
        type: MetricType.networkIo,
        value: 1234.0,
        unit: 'req/s',
        timestamp: DateTime.now(),
        description: 'Requests per second',
      ),
      Metric(
        id: '3',
        name: 'Error Rate',
        type: MetricType.cpuUsage,
        value: 0.5,
        unit: '%',
        timestamp: DateTime.now(),
        description: 'Percentage of failed requests',
      ),
    ];
  }
}

class MetricsList extends ConsumerWidget {
  const MetricsList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final metrics = ref.watch(metricsProvider);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Key Metrics',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 16),
            if (metrics.isNotEmpty) ...[
              ...metrics.map((metric) => _buildMetricItem(metric)),
            ] else
              const Text(
                'No metrics available',
                style: TextStyle(color: Colors.grey),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildMetricItem(Metric metric) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey.shade300),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  metric.name,
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
                Text(
                  '${metric.value.toStringAsFixed(1)} ${metric.unit}',
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ),
          Icon(
            Icons.trending_up,
            color: Colors.green,
            size: 20,
          ),
        ],
      ),
    );
  }
}