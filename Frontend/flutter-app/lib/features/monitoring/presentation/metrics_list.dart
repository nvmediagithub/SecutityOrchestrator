import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../di/monitoring_providers.dart';
import '../domain/models/metric.dart';

class MetricsList extends ConsumerWidget {
  const MetricsList({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final metricsAsync = ref.watch(metricsProvider);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Key Metrics', style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 16),
            metricsAsync.when(
              data: (metrics) => metrics.isNotEmpty
                  ? Column(children: metrics.map(_buildMetricItem).toList())
                  : const Text(
                      'No metrics available',
                      style: TextStyle(color: Colors.grey),
                    ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (error, _) => Text(
                'Failed to load metrics: $error',
                style: const TextStyle(color: Colors.redAccent),
              ),
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
          Icon(Icons.trending_up, color: Colors.green, size: 20),
        ],
      ),
    );
  }
}
