import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:fl_chart/fl_chart.dart';
import '../providers/monitoring_provider.dart';

class RealTimeChart extends ConsumerWidget {
  final String title;
  final String metricType;
  final Color color;
  final double? maxValue;

  const RealTimeChart({
    super.key,
    required this.title,
    required this.metricType,
    required this.color,
    this.maxValue,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final historicalMetrics = ref.watch(
      historicalMetricsProvider({
        'metricType': metricType,
        'period': '1h',
      }),
    );

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  title,
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                Container(
                  width: 12,
                  height: 12,
                  decoration: BoxDecoration(
                    color: color,
                    shape: BoxShape.circle,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 200,
              child: historicalMetrics.when(
                data: (metrics) => _buildChart(context, metrics),
                loading: () => const Center(
                  child: CircularProgressIndicator(),
                ),
                error: (error, stack) => Center(
                  child: Text(
                    'Error loading data: $error',
                    style: TextStyle(color: Theme.of(context).colorScheme.error),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildChart(BuildContext context, List<dynamic> metrics) {
    if (metrics.isEmpty) {
      return const Center(
        child: Text('No data available'),
      );
    }

    final spots = <FlSpot>[];
    for (int i = 0; i < metrics.length; i++) {
      final metric = metrics[i];
      final value = (metric['value'] ?? 0.0).toDouble();
      final timestamp = DateTime.parse(metric['timestamp']);
      spots.add(FlSpot(i.toDouble(), value));
    }

    return LineChart(
      LineChartData(
        gridData: const FlGridData(
          show: true,
          drawVerticalLine: true,
          horizontalInterval: 1,
          verticalInterval: 1,
        ),
        titlesData: FlTitlesData(
          show: true,
          rightTitles: const AxisTitles(
            sideTitles: SideTitles(showTitles: false),
          ),
          topTitles: const AxisTitles(
            sideTitles: SideTitles(showTitles: false),
          ),
          bottomTitles: AxisTitles(
            sideTitles: SideTitles(
              showTitles: true,
              reservedSize: 30,
              interval: 1,
              getTitlesWidget: (value, meta) {
                if (value % 5 == 0) {
                  final index = value.toInt();
                  if (index < metrics.length) {
                    final timestamp = DateTime.parse(metrics[index]['timestamp']);
                    return Text(
                      '${timestamp.hour}:${timestamp.minute.toString().padLeft(2, '0')}',
                      style: const TextStyle(fontSize: 10),
                    );
                  }
                }
                return const Text('');
              },
            ),
          ),
          leftTitles: AxisTitles(
            sideTitles: SideTitles(
              showTitles: true,
              interval: 1,
              getTitlesWidget: (value, meta) {
                return Text(
                  value.toStringAsFixed(0),
                  style: const TextStyle(fontSize: 10),
                );
              },
              reservedSize: 42,
            ),
          ),
        ),
        borderData: FlBorderData(
          show: true,
          border: Border.all(
            color: const Color(0xff37434d),
            width: 1,
          ),
        ),
        minX: 0,
        maxX: spots.length.toDouble() - 1,
        minY: 0,
        maxY: maxValue ?? _calculateMaxValue(spots),
        lineBarsData: [
          LineChartBarData(
            spots: spots,
            isCurved: true,
            color: color,
            barWidth: 2,
            isStrokeCapRound: true,
            dotData: const FlDotData(
              show: false,
            ),
            belowBarData: BarAreaData(
              show: true,
              gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [
                  color.withOpacity(0.3),
                  color.withOpacity(0.1),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  double _calculateMaxValue(List<FlSpot> spots) {
    if (spots.isEmpty) return 100.0;
    
    double max = spots.first.y;
    for (final spot in spots) {
      if (spot.y > max) {
        max = spot.y;
      }
    }
    
    // Add 10% padding
    return max * 1.1;
  }
}