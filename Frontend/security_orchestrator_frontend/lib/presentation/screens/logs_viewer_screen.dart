import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/log_dto.dart';
import '../../data/services/logs_service.dart';
import '../../presentation/widgets/file_upload_widget.dart';

class LogsViewerScreen extends ConsumerStatefulWidget {
  final String? sessionId;

  const LogsViewerScreen({
    super.key,
    this.sessionId,
  });

  @override
  ConsumerState<LogsViewerScreen> createState() => _LogsViewerScreenState();
}

class _LogsViewerScreenState extends ConsumerState<LogsViewerScreen> {
  final TextEditingController _searchController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  
  List<LogEntryDto> _logs = [];
  List<String> _selectedLevels = [];
  List<String> _selectedCategories = [];
  List<String> _selectedSources = [];
  bool _isLoading = false;
  bool _isRealTimeEnabled = true;
  LogSummaryDto? _summary;
  String _sortBy = 'timestamp_desc';
  int _currentPage = 0;
  final int _pageSize = 50;

  @override
  void initState() {
    super.initState();
    _loadInitialData();
    _setupRealTimeUpdates();
  }

  @override
  void dispose() {
    _searchController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Просмотр логов'),
        elevation: 0,
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          IconButton(
            onPressed: _refreshLogs,
            icon: const Icon(Icons.refresh),
            tooltip: 'Обновить логи',
          ),
          IconButton(
            onPressed: _toggleRealTime,
            icon: Icon(_isRealTimeEnabled ? Icons.pause : Icons.play_arrow),
            tooltip: _isRealTimeEnabled ? 'Приостановить обновления' : 'Возобновить обновления',
          ),
          PopupMenuButton<String>(
            onSelected: _handleMenuAction,
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'export',
                child: ListTile(
                  leading: Icon(Icons.download),
                  title: Text('Экспорт логов'),
                ),
              ),
              const PopupMenuItem(
                value: 'clear',
                child: ListTile(
                  leading: Icon(Icons.clear_all),
                  title: Text('Очистить логи'),
                ),
              ),
              const PopupMenuItem(
                value: 'settings',
                child: ListTile(
                  leading: Icon(Icons.settings),
                  title: Text('Настройки'),
                ),
              ),
            ],
          ),
        ],
      ),
      body: Column(
        children: [
          _buildFilters(),
          _buildSummary(),
          _buildLogView(),
        ],
      ),
      floatingActionButton: _buildFloatingActionButton(),
    );
  }

  Widget _buildFilters() {
    return Card(
      margin: const EdgeInsets.all(16),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(
                  'Фильтры',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const Spacer(),
                TextButton.icon(
                  onPressed: _clearFilters,
                  icon: const Icon(Icons.clear, size: 16),
                  label: const Text('Очистить'),
                ),
              ],
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _searchController,
              decoration: const InputDecoration(
                labelText: 'Поиск в логах',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(),
                contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              ),
              onChanged: (value) => _applyFilters(),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _buildLevelFilter(),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: _buildCategoryFilter(),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: _buildSourceFilter(),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLevelFilter() {
    final levels = [
      'DEBUG',
      'INFO', 
      'WARN',
      'ERROR'
    ];

    return PopupMenuButton<String>(
      child: Chip(
        avatar: const Icon(Icons.filter_list, size: 16),
        label: Text(_selectedLevels.isEmpty ? 'Все уровни' : '${_selectedLevels.length} уровней'),
      ),
      onSelected: (level) {
        setState(() {
          if (_selectedLevels.contains(level)) {
            _selectedLevels.remove(level);
          } else {
            _selectedLevels.add(level);
          }
        });
        _applyFilters();
      },
      itemBuilder: (context) {
        return levels.map((level) {
          final isSelected = _selectedLevels.contains(level);
          return PopupMenuItem(
            value: level,
            child: Row(
              children: [
                Icon(
                  _getLevelIcon(level),
                  color: _getLevelColor(level),
                  size: 16,
                ),
                const SizedBox(width: 8),
                Text(level),
                const Spacer(),
                if (isSelected) const Icon(Icons.check, size: 16),
              ],
            ),
          );
        }).toList();
      },
    );
  }

  Widget _buildCategoryFilter() {
    final categories = [
      'API_CALL',
      'TEST_EXECUTION',
      'VULNERABILITY_SCAN',
      'BPMN_ANALYSIS',
      'OPENAPI_ANALYSIS',
      'OWASP_TEST',
      'LLM_ANALYSIS',
      'SYSTEM',
    ];

    return PopupMenuButton<String>(
      child: Chip(
        avatar: const Icon(Icons.category, size: 16),
        label: Text(_selectedCategories.isEmpty ? 'Все категории' : '${_selectedCategories.length} категорий'),
      ),
      onSelected: (category) {
        setState(() {
          if (_selectedCategories.contains(category)) {
            _selectedCategories.remove(category);
          } else {
            _selectedCategories.add(category);
          }
        });
        _applyFilters();
      },
      itemBuilder: (context) {
        return categories.map((category) {
          final isSelected = _selectedCategories.contains(category);
          return PopupMenuItem(
            value: category,
            child: Row(
              children: [
                Text(_getCategoryDisplayName(category)),
                const Spacer(),
                if (isSelected) const Icon(Icons.check, size: 16),
              ],
            ),
          );
        }).toList();
      },
    );
  }

  Widget _buildSourceFilter() {
    final sources = [
      'OPENAPI_MODULE',
      'BPMN_MODULE',
      'OWASP_MODULE',
      'LLM_MODULE',
      'TESTING_ORCHESTRATOR',
      'DATABASE',
    ];

    return PopupMenuButton<String>(
      child: Chip(
        avatar: const Icon(Icons.source, size: 16),
        label: Text(_selectedSources.isEmpty ? 'Все источники' : '${_selectedSources.length} источников'),
      ),
      onSelected: (source) {
        setState(() {
          if (_selectedSources.contains(source)) {
            _selectedSources.remove(source);
          } else {
            _selectedSources.add(source);
          }
        });
        _applyFilters();
      },
      itemBuilder: (context) {
        return sources.map((source) {
          final isSelected = _selectedSources.contains(source);
          return PopupMenuItem(
            value: source,
            child: Row(
              children: [
                Text(_getSourceDisplayName(source)),
                const Spacer(),
                if (isSelected) const Icon(Icons.check, size: 16),
              ],
            ),
          );
        }).toList();
      },
    );
  }

  Widget _buildSummary() {
    if (_summary == null) {
      return const SizedBox.shrink();
    }

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Expanded(
                child: _buildSummaryItem(
                  'Всего записей',
                  '${_summary!.totalLogs}',
                  Icons.list,
                  Colors.blue,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: _buildSummaryItem(
                  'Ошибки',
                  '${_summary!.errorCount}',
                  Icons.error,
                  Colors.red,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: _buildSummaryItem(
                  'Предупреждения',
                  '${_summary!.warningCount}',
                  Icons.warning,
                  Colors.orange,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: _buildSummaryItem(
                  'Информация',
                  '${_summary!.infoCount}',
                  Icons.info,
                  Colors.green,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSummaryItem(String title, String value, IconData icon, Color color) {
    return Column(
      children: [
        Icon(icon, color: color, size: 20),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
                color: color,
              ),
        ),
        Text(
          title,
          style: Theme.of(context).textTheme.bodySmall,
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildLogView() {
    return Expanded(
      child: Card(
        margin: const EdgeInsets.all(16),
        child: Column(
          children: [
            _buildLogHeader(),
            Expanded(
              child: _logs.isEmpty
                  ? const Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.description, size: 64, color: Colors.grey),
                          SizedBox(height: 16),
                          Text('Логи не найдены'),
                        ],
                      ),
                    )
                  : ListView.builder(
                      controller: _scrollController,
                      padding: const EdgeInsets.all(8),
                      itemCount: _logs.length,
                      itemBuilder: (context, index) {
                        final log = _logs[index];
                        return _buildLogEntry(log);
                      },
                    ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLogHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surface,
        border: Border(
          bottom: BorderSide(
            color: Colors.grey.shade300,
          ),
        ),
      ),
      child: Row(
        children: [
          Expanded(
            flex: 1,
            child: Text(
              'Время',
              style: Theme.of(context).textTheme.titleSmall,
            ),
          ),
          Expanded(
            flex: 1,
            child: Text(
              'Уровень',
              style: Theme.of(context).textTheme.titleSmall,
            ),
          ),
          Expanded(
            flex: 2,
            child: Text(
              'Категория',
              style: Theme.of(context).textTheme.titleSmall,
            ),
          ),
          Expanded(
            flex: 3,
            child: Text(
              'Сообщение',
              style: Theme.of(context).textTheme.titleSmall,
            ),
          ),
          Expanded(
            flex: 1,
            child: Text(
              'Источник',
              style: Theme.of(context).textTheme.titleSmall,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLogEntry(LogEntryDto log) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 2),
      child: InkWell(
        onTap: () => _showLogDetails(log),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              Expanded(
                flex: 1,
                child: Text(
                  _formatTimestamp(log.timestamp),
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ),
              Expanded(
                flex: 1,
                child: Row(
                  children: [
                    Icon(
                      _getLevelIcon(log.level),
                      color: _getLevelColor(log.level),
                      size: 16,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      log.level,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: _getLevelColor(log.level),
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                  ],
                ),
              ),
              Expanded(
                flex: 2,
                child: Text(
                  _getCategoryDisplayName(log.category),
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ),
              Expanded(
                flex: 3,
                child: Text(
                  log.message,
                  style: Theme.of(context).textTheme.bodySmall,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              Expanded(
                flex: 1,
                child: Text(
                  _getSourceDisplayName(log.source),
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey.shade600,
                      ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildFloatingActionButton() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        FloatingActionButton(
          heroTag: "export",
          onPressed: _exportLogs,
          child: const Icon(Icons.download),
          tooltip: 'Экспорт логов',
        ),
        const SizedBox(height: 8),
        FloatingActionButton(
          heroTag: "scroll_to_bottom",
          onPressed: _scrollToBottom,
          child: const Icon(Icons.arrow_downward),
          tooltip: 'Перейти к последним записям',
        ),
      ],
    );
  }

  void _loadInitialData() async {
    setState(() {
      _isLoading = true;
    });

    try {
      final logsService = ref.read(logsServiceProvider);
      
      // Load logs
      final logs = await logsService.getLogs(
        sessionId: widget.sessionId,
        page: _currentPage,
        size: _pageSize,
      );
      
      // Load summary
      final summary = await logsService.getLogsSummary(
        sessionId: widget.sessionId,
      );

      setState(() {
        _logs = logs;
        _summary = summary;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
      
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка загрузки логов: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _setupRealTimeUpdates() {
    // Set up real-time WebSocket updates for logs
    // This would connect to the logs WebSocket endpoint
  }

  void _applyFilters() {
    // Apply current filters to the log list
    // This is a simplified version - in reality, you would filter server-side
  }

  void _clearFilters() {
    setState(() {
      _searchController.clear();
      _selectedLevels.clear();
      _selectedCategories.clear();
      _selectedSources.clear();
    });
  }

  void _refreshLogs() async {
    await _loadInitialData();
  }

  void _toggleRealTime() {
    setState(() {
      _isRealTimeEnabled = !_isRealTimeEnabled;
    });
  }

  void _showLogDetails(LogEntryDto log) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Детали записи'),
        content: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              _buildDetailRow('Время', _formatTimestamp(log.timestamp)),
              _buildDetailRow('Уровень', log.level),
              _buildDetailRow('Категория', _getCategoryDisplayName(log.category)),
              _buildDetailRow('Источник', _getSourceDisplayName(log.source)),
              const SizedBox(height: 16),
              Text(
                'Сообщение',
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
              const SizedBox(height: 8),
              SelectableText(
                log.message,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              if (log.requestPayload != null) ...[
                const SizedBox(height: 16),
                Text(
                  'Request Payload',
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const SizedBox(height: 8),
                SelectableText(
                  log.requestPayload!,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        fontFamily: 'monospace',
                      ),
                ),
              ],
              if (log.responsePayload != null) ...[
                const SizedBox(height: 16),
                Text(
                  'Response Payload',
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const SizedBox(height: 8),
                SelectableText(
                  log.responsePayload!,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        fontFamily: 'monospace',
                      ),
                ),
              ],
              if (log.metadata.isNotEmpty) ...[
                const SizedBox(height: 16),
                Text(
                  'Метаданные',
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                ),
                const SizedBox(height: 8),
                ...log.metadata.entries.map((entry) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(vertical: 2),
                    child: SelectableText(
                      '${entry.key}: ${entry.value}',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            fontFamily: 'monospace',
                          ),
                    ),
                  );
                }).toList(),
              ],
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Закрыть'),
          ),
        ],
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
            ),
          ),
          Expanded(
            child: SelectableText(
              value,
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ),
        ],
      ),
    );
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
      );
    }
  }

  void _handleMenuAction(String action) {
    switch (action) {
      case 'export':
        _exportLogs();
        break;
      case 'clear':
        _clearLogs();
        break;
      case 'settings':
        _showSettings();
        break;
    }
  }

  void _exportLogs() async {
    try {
      final logsService = ref.read(logsServiceProvider);
      final exportId = await logsService.exportLogs(
        sessionId: widget.sessionId,
        levels: _selectedLevels.isEmpty ? null : _selectedLevels,
        categories: _selectedCategories.isEmpty ? null : _selectedCategories,
        sources: _selectedSources.isEmpty ? null : _selectedSources,
        format: 'CSV',
      );
      
      final downloadUrl = await logsService.downloadExportedLogs(exportId);
      
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Логи экспортированы успешно'),
          backgroundColor: Colors.green,
        ),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Ошибка экспорта: $e'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  void _clearLogs() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Очистить логи'),
        content: const Text('Вы уверены, что хотите очистить все логи? Это действие нельзя отменить.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Отмена'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Очистить'),
          ),
        ],
      ),
    );
    
    if (confirm == true) {
      try {
        final logsService = ref.read(logsServiceProvider);
        await logsService.clearLogs(sessionId: widget.sessionId);
        await _refreshLogs();
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка очистки: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _showSettings() {
    // Show settings dialog
  }

  // Helper methods
  String _formatTimestamp(DateTime timestamp) {
    return '${timestamp.hour.toString().padLeft(2, '0')}:${timestamp.minute.toString().padLeft(2, '0')}:${timestamp.second.toString().padLeft(2, '0')}';
  }

  IconData _getLevelIcon(String level) {
    switch (level.toUpperCase()) {
      case 'ERROR':
        return Icons.error;
      case 'WARN':
        return Icons.warning;
      case 'INFO':
        return Icons.info;
      case 'DEBUG':
        return Icons.bug_report;
      default:
        return Icons.circle;
    }
  }

  Color _getLevelColor(String level) {
    switch (level.toUpperCase()) {
      case 'ERROR':
        return Colors.red;
      case 'WARN':
        return Colors.orange;
      case 'INFO':
        return Colors.blue;
      case 'DEBUG':
        return Colors.grey;
      default:
        return Colors.grey.shade600;
    }
  }

  String _getCategoryDisplayName(String category) {
    switch (category) {
      case 'API_CALL':
        return 'API вызов';
      case 'TEST_EXECUTION':
        return 'Выполнение тестов';
      case 'VULNERABILITY_SCAN':
        return 'Сканирование уязвимостей';
      case 'BPMN_ANALYSIS':
        return 'Анализ BPMN';
      case 'OPENAPI_ANALYSIS':
        return 'Анализ OpenAPI';
      case 'OWASP_TEST':
        return 'OWASP тест';
      case 'LLM_ANALYSIS':
        return 'LLM анализ';
      case 'SYSTEM':
        return 'Система';
      default:
        return category;
    }
  }

  String _getSourceDisplayName(String source) {
    switch (source) {
      case 'OPENAPI_MODULE':
        return 'OpenAPI';
      case 'BPMN_MODULE':
        return 'BPMN';
      case 'OWASP_MODULE':
        return 'OWASP';
      case 'LLM_MODULE':
        return 'LLM';
      case 'TESTING_ORCHESTRATOR':
        return 'Оркестратор';
      case 'DATABASE':
        return 'База данных';
      default:
        return source;
    }
  }
}