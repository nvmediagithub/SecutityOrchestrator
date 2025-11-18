import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/analysis_process_service.dart';
import '../di/analysis_processes_providers.dart';
import '../domain/analysis_process.dart';
import '../domain/create_analysis_process_usecase.dart';
import 'create_process_dialog.dart';

class AnalysisProcessesPage extends ConsumerStatefulWidget {
  const AnalysisProcessesPage({super.key});

  @override
  ConsumerState<AnalysisProcessesPage> createState() =>
      _AnalysisProcessesPageState();
}

class _AnalysisProcessesPageState extends ConsumerState<AnalysisProcessesPage> {
  late Future<List<AnalysisProcess>> _processesFuture;

  @override
  void initState() {
    super.initState();
    _loadProcesses();
  }

  void _loadProcesses() {
    // Assuming provider for AnalysisProcessService is defined
    final service = ref.read(analysisProcessServiceProvider);
    _processesFuture = service.getProcesses();
  }

  Future<void> _showCreateProcessDialog() async {
    // Assuming provider for CreateAnalysisProcessUseCase is defined
    final createUseCase = ref.read(createAnalysisProcessUseCaseProvider);

    final result = await showDialog<AnalysisProcess>(
      context: context,
      builder: (context) =>
          CreateProcessDialog(createProcessUseCase: createUseCase),
    );

    if (result != null) {
      // Refresh the list
      setState(() {
        _loadProcesses();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Analysis Processes'),
        backgroundColor: Theme.of(context).primaryColor,
        foregroundColor: Colors.white,
      ),
      body: FutureBuilder<List<AnalysisProcess>>(
        future: _processesFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error, size: 64, color: Colors.red),
                  const SizedBox(height: 16),
                  Text('Error loading processes: ${snapshot.error}'),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: _loadProcesses,
                    child: const Text('Retry'),
                  ),
                ],
              ),
            );
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.list_alt, size: 64, color: Colors.grey),
                  const SizedBox(height: 16),
                  const Text('No analysis processes found'),
                  const SizedBox(height: 16),
                  ElevatedButton.icon(
                    onPressed: _showCreateProcessDialog,
                    icon: const Icon(Icons.add),
                    label: const Text('Create First Process'),
                  ),
                ],
              ),
            );
          } else {
            final processes = snapshot.data!;
            return ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: processes.length,
              itemBuilder: (context, index) {
                final process = processes[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 8),
                  child: ListTile(
                    title: Text(process.name),
                    subtitle: Text(
                      '${process.type.displayName} - ${process.status.displayName}',
                    ),
                    trailing: Icon(
                      process.status == ProcessStatus.running
                          ? Icons.play_arrow
                          : process.status == ProcessStatus.completed
                          ? Icons.check_circle
                          : process.status == ProcessStatus.failed
                          ? Icons.error
                          : Icons.schedule,
                      color: process.status == ProcessStatus.running
                          ? Colors.blue
                          : process.status == ProcessStatus.completed
                          ? Colors.green
                          : process.status == ProcessStatus.failed
                          ? Colors.red
                          : Colors.grey,
                    ),
                    onTap: () {
                      // TODO: Navigate to process details
                    },
                  ),
                );
              },
            );
          }
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showCreateProcessDialog,
        tooltip: 'Create Process',
        child: const Icon(Icons.add),
      ),
    );
  }
}
