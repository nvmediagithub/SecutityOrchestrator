import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../data/models/user_flow_models.dart';
import '../../data/services/user_flow_api_service.dart';
import '../providers/user_flow_provider.dart';

class UserFlowResultsScreen extends ConsumerStatefulWidget {
  final String analysisId;
  final UserTask? initialTask;

  const UserFlowResultsScreen({
    super.key,
    required this.analysisId,
    this.initialTask,
  });

  @override
  ConsumerState<UserFlowResultsScreen> createState() => _UserFlowResultsScreenState();
}

class _UserFlowResultsScreenState extends ConsumerState<UserFlowResultsScreen> {
  AnalysisResult? _result;
  bool _isLoading = true;
  bool _isExporting = false;
  String _error = '';

  @override
  void initState() {
    super.initState();
    _loadResults();
    _connectWebSocket();
  }

  void _connectWebSocket() {
    final apiService = UserFlowApiService();
    
    apiService.connectWebSocket(widget.analysisId, (message) {
      if (mounted) {
        switch (message.type) {
          case 'analysis_started':
            _updateTaskStatus(TaskStatus.analyzing, message.message);
            break;
          case 'analysis_progress':
            _updateTaskStatus(TaskStatus.analyzing, message.message);
            break;
          case 'analysis_completed':
            _updateTaskStatus(TaskStatus.completed, message.message);
            _loadResults();
            break;
          case 'analysis_error':
            _updateTaskStatus(TaskStatus.error, message.message);
            setState(() {
              _error = message.message;
            });
            break;
        }
      }
    });
  }

  void _updateTaskStatus(TaskStatus status, String message) {
    // Update task status through provider
    ref.read(userFlowProvider.notifier).updateTaskStatus(widget.analysisId, status, message);
  }

  Future<void> _loadResults() async {
    try {
      setState(() {
        _isLoading = true;
        _error = '';
      });

      final apiService = UserFlowApiService();
      final result = await apiService.getAnalysisResult(widget.analysisId);

      if (mounted) {
        setState(() {
          _result = result;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = 'Ошибка загрузки результатов: $e';
          _isLoading = false;
        });
      }
    }
  }

  Future<void> _exportReport(String format) async {
    try {
      setState(() {
        _isExporting = true;
      });

      final apiService = UserFlowApiService();
      final options = ExportOptions(
        includeDetails: true,
        includeMetadata: true,
        format: format,
      );

      final filePath = await apiService.exportReport(widget.analysisId, options);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Отчет сохранен: $filePath'),
            action: SnackBarAction(
              label: 'Открыть',
              onPressed: () => _openFile(filePath),
            ),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка экспорта: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isExporting = false;
        });
      }
    }
  }

  Future<void> _openFile(String filePath) async {
    try {
      final uri = Uri.file(filePath);
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri);
      }
    } catch (e) {
      debugPrint('Failed to open file: $e');
    }
  }

  void _newAnalysis() {
    Navigator.of(context).popUntil((route) => route.isFirst);
  }

  @override
  void dispose() {
    UserFlowApiService().disconnectWebSocket();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Результаты анализа'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _isLoading ? null : _loadResults,
            tooltip: 'Обновить',
          ),
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _newAnalysis,
            tooltip: 'Новый анализ',
          ),
        ],
      ),
      body: _buildContent(),
      floatingActionButton: _result != null ? FloatingActionButton(
        onPressed: _newAnalysis,
        child: const Icon(Icons.add),
      ) : null,
    );
  }

  Widget _buildContent() {
    if (_isLoading) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 16),
            Text('Загрузка результатов...'),
          ],
        ),
      );
    }

    if (_error.isNotEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.error, size: 64, color: Colors.red[300]),
            const SizedBox(height: 16),
            Text(
              _error,
              textAlign: TextAlign.center,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: Colors.red[700],
              ),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadResults,
              child: const Text('Повторить'),
            ),
          ],
        ),
      );
    }

    if (_result == null) {
      return const Center(
        child: Text('Результаты не найдены'),
      );
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header
          _buildHeaderCard(),
          const SizedBox(height: 16),

          // Summary
          _buildSummaryCard(),
          const SizedBox(height: 16),

          // Report content
          _buildReportCard(),
          const SizedBox(height: 16),

          // Export options
          _buildExportCard(),
          const SizedBox(height: 16),

          // Actions
          _buildActionsCard(),
        ],
      ),
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
                  Icons.analytics,
                  color: Theme.of(context).colorScheme.primary,
                  size: 32,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        _result!.title,
                        style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        'Завершен: ${_formatDateTime(_result!.completedAt)}',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
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

  Widget _buildSummaryCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Краткое содержание',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              _result!.summary,
              style: Theme.of(context).textTheme.bodyLarge,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildReportCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Полный отчет',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey[50],
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.grey[300]!),
              ),
              child: SelectableText(
                _result!.reportContent,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildExportCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Экспорт отчета',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: _isExporting ? null : () => _exportReport('pdf'),
                    icon: const Icon(Icons.picture_as_pdf),
                    label: const Text('PDF'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.red[100],
                      foregroundColor: Colors.red[700],
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: _isExporting ? null : () => _exportReport('docx'),
                    icon: const Icon(Icons.description),
                    label: const Text('DOCX'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.blue[100],
                      foregroundColor: Colors.blue[700],
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: _isExporting ? null : () => _exportReport('html'),
                    icon: const Icon(Icons.web),
                    label: const Text('HTML'),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.green[100],
                      foregroundColor: Colors.green[700],
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActionsCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Действия',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: _newAnalysis,
                    icon: const Icon(Icons.add_circle_outline),
                    label: const Text('Новый анализ'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: () {
                      // TODO: Share functionality
                    },
                    icon: const Icon(Icons.share),
                    label: const Text('Поделиться'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  String _formatDateTime(DateTime dateTime) {
    return '${dateTime.day}.${dateTime.month}.${dateTime.year} ${dateTime.hour}:${dateTime.minute.toString().padLeft(2, '0')}';
  }
}