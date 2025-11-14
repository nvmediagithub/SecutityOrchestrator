import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../data/services/user_flow_api_service.dart';
import '../providers/user_flow_provider.dart';
import '../widgets/file_upload_widget.dart';
import '../widgets/analysis_progress_widget.dart';

class UserFlowMainScreen extends ConsumerStatefulWidget {
  const UserFlowMainScreen({super.key});

  @override
  ConsumerState<UserFlowMainScreen> createState() => _UserFlowMainScreenState();
}

class _UserFlowMainScreenState extends ConsumerState<UserFlowMainScreen> {
  late final UserFlowApiService _apiService;

  @override
  void initState() {
    super.initState();
    _apiService = UserFlowApiService();
  }

  @override
  Widget build(BuildContext context) {
    final userFlowState = ref.watch(userFlowProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Security Analysis'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref.read(userFlowProvider.notifier).refresh(),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeaderCard(),
            const SizedBox(height: 24),
            _buildUploadSection(),
            const SizedBox(height: 24),
            if (userFlowState.isAnalyzing) _buildProgressSection(),
            if (userFlowState.analysisResult != null) _buildResultsSection(),
          ],
        ),
      ),
      floatingActionButton: userFlowState.canStartAnalysis
          ? FloatingActionButton.extended(
              onPressed: _startAnalysis,
              icon: const Icon(Icons.play_arrow),
              label: const Text('Start Analysis'),
            )
          : null,
    );
  }

  Widget _buildHeaderCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  Icons.security,
                  color: Theme.of(context).colorScheme.primary,
                  size: 32,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'AI-Powered Security Analysis',
                        style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'Upload your documents for comprehensive security testing with LLM analysis',
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUploadSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Document Upload',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 16),
        const FileUploadWidget(),
      ],
    );
  }

  Widget _buildProgressSection() {
    return const Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Analysis Progress',
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
        ),
        SizedBox(height: 16),
        AnalysisProgressWidget(),
      ],
    );
  }

  Widget _buildResultsSection() {
    final result = ref.watch(userFlowProvider).analysisResult!;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Analysis Results',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 16),
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Security Score: ${result.securityScore}/100'),
                Text('Vulnerabilities Found: ${result.vulnerabilities.length}'),
                Text('Analysis Time: ${result.analysisTime}ms'),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () => _navigateToResults(),
                  child: const Text('View Detailed Results'),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  void _startAnalysis() {
    ref.read(userFlowProvider.notifier).startAnalysis();
  }

  void _navigateToResults() {
    // Navigate to results screen
  }
}