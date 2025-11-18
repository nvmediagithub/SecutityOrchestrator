import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'connection_status_card.dart';
import 'system_health_card.dart';
import 'metrics_list.dart';
import 'alerts_list.dart';
import 'processes_overview_card.dart';
import '../../analysis-processes/presentation/create_process_dialog.dart';
import '../../analysis-processes/domain/create_analysis_process_usecase.dart';

class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Security Orchestrator'),
        backgroundColor: Theme.of(context).primaryColor,
        foregroundColor: Colors.white,
      ),
      body: const SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(height: 24),
            Text(
              'Security Orchestrator Dashboard',
              style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: Colors.black87),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 8),
            Text(
              'Monitor and manage your security infrastructure',
              style: TextStyle(fontSize: 16, color: Colors.black54),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 32),
            ConnectionStatusCard(),
            SystemHealthCard(),
            MetricsList(),
            AlertsList(),
            ProcessesOverviewCard(),
          ],
        ),
      ),
    );
  }
}
