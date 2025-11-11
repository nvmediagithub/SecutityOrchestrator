import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/user_flow_models.dart';
import '../../data/services/user_flow_api_service.dart';
import '../providers/user_flow_provider.dart';
import 'user_flow_results_screen.dart';

class UserFlowHistoryScreen extends ConsumerStatefulWidget {
  const UserFlowHistoryScreen({super.key});

  @override
  ConsumerState<UserFlowHistoryScreen> createState() => _UserFlowHistoryScreenState();
}

class _UserFlowHistoryScreenState extends ConsumerState<UserFlowHistoryScreen> {
  List<UserTask> _history = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadHistory();
  }

  Future<void> _loadHistory() async {
    try {
      setState(() {
        _isLoading = true;
      });

      final apiService = UserFlowApiService();
      final history = await apiService.getAnalysisHistory();

      if (mounted) {
        setState(() {
          _history = history;
          _isLoading = false;
        });
        
        // Update provider with loaded history
        for (final task in history) {
          ref.read(userFlowProvider.notifier).addTask(task);
        }
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
        
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка загрузки истории: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  void _viewResults(UserTask task) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => UserFlowResultsScreen(
          analysisId: task.id,
          initialTask: task,
        ),
      ),
    );
  }

  void _deleteTask(UserTask task) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Удалить анализ?'),
        content: Text('Вы уверены, что хотите удалить анализ "${task.title}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('Отмена'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('Удалить'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      // Remove from local state
      setState(() {
        _history.removeWhere((t) => t.id == task.id);
      });
      
      // Update provider
      ref.read(userFlowProvider.notifier).removeTask(task.id);

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Анализ удален'),
          backgroundColor: Colors.orange,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('История анализов'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _isLoading ? null : _loadHistory,
            tooltip: 'Обновить',
          ),
        ],
      ),
      body: _buildContent(),
      floatingActionButton: FloatingActionButton(
        onPressed: () => Navigator.of(context).pop(),
        child: const Icon(Icons.add),
      ),
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
            Text('Загрузка истории...'),
          ],
        ),
      );
    }

    if (_history.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.history,
              size: 64,
              color: Colors.grey[400],
            ),
            const SizedBox(height: 16),
            Text(
              'История анализов пуста',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: Colors.grey[600],
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Создайте первый анализ для начала работы',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: Colors.grey[500],
              ),
            ),
            const SizedBox(height: 24),
            ElevatedButton.icon(
              onPressed: () => Navigator.of(context).pop(),
              icon: const Icon(Icons.add),
              label: const Text('Создать анализ'),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadHistory,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _history.length,
        itemBuilder: (context, index) {
          final task = _history[index];
          return _buildTaskCard(task);
        },
      ),
    );
  }

  Widget _buildTaskCard(UserTask task) {
    final statusColor = _getStatusColor(task.status);
    final statusIcon = _getStatusIcon(task.status);
    final statusText = _getStatusText(task.status);
    
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: InkWell(
        onTap: task.status == TaskStatus.completed ? () => _viewResults(task) : null,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header
              Row(
                children: [
                  Icon(
                    statusIcon,
                    color: statusColor,
                    size: 24,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          task.title,
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          _formatDateTime(task.createdAt),
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: Colors.grey[600],
                          ),
                        ),
                      ],
                    ),
                  ),
                  PopupMenuButton<String>(
                    onSelected: (value) {
                      switch (value) {
                        case 'view':
                          if (task.status == TaskStatus.completed) {
                            _viewResults(task);
                          }
                          break;
                        case 'delete':
                          _deleteTask(task);
                          break;
                      }
                    },
                    itemBuilder: (context) => [
                      if (task.status == TaskStatus.completed)
                        const PopupMenuItem(
                          value: 'view',
                          child: Row(
                            children: [
                              Icon(Icons.visibility),
                              SizedBox(width: 8),
                              Text('Просмотр'),
                            ],
                          ),
                        ),
                      const PopupMenuItem(
                        value: 'delete',
                        child: Row(
                          children: [
                            Icon(Icons.delete, color: Colors.red),
                            SizedBox(width: 8),
                            Text('Удалить', style: TextStyle(color: Colors.red)),
                          ],
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              
              const SizedBox(height: 12),
              
              // Description
              Text(
                task.description,
                maxLines: 3,
                overflow: TextOverflow.ellipsis,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              
              const SizedBox(height: 12),
              
              // Status and metadata
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: statusColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          statusIcon,
                          size: 16,
                          color: statusColor,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          statusText,
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: statusColor,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ],
                    ),
                  ),
                  
                  if (task.attachments.isNotEmpty) ...[
                    const SizedBox(width: 8),
                    Icon(
                      Icons.attach_file,
                      size: 16,
                      color: Colors.grey[600],
                    ),
                    const SizedBox(width: 4),
                    Text(
                      '${task.attachments.length} файл(ов)',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey[600],
                      ),
                    ),
                  ],
                  
                  const Spacer(),
                  
                  if (task.status == TaskStatus.completed)
                    TextButton(
                      onPressed: () => _viewResults(task),
                      child: const Text('Просмотр'),
                    ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Color _getStatusColor(TaskStatus status) {
    switch (status) {
      case TaskStatus.pending:
        return Colors.orange;
      case TaskStatus.analyzing:
        return Colors.blue;
      case TaskStatus.completed:
        return Colors.green;
      case TaskStatus.error:
        return Colors.red;
    }
  }

  IconData _getStatusIcon(TaskStatus status) {
    switch (status) {
      case TaskStatus.pending:
        return Icons.schedule;
      case TaskStatus.analyzing:
        return Icons.hourglass_empty;
      case TaskStatus.completed:
        return Icons.check_circle;
      case TaskStatus.error:
        return Icons.error;
    }
  }

  String _getStatusText(TaskStatus status) {
    switch (status) {
      case TaskStatus.pending:
        return 'Ожидает';
      case TaskStatus.analyzing:
        return 'Анализ...';
      case TaskStatus.completed:
        return 'Завершен';
      case TaskStatus.error:
        return 'Ошибка';
    }
  }

  String _formatDateTime(DateTime dateTime) {
    return '${dateTime.day}.${dateTime.month}.${dateTime.year} ${dateTime.hour}:${dateTime.minute.toString().padLeft(2, '0')}';
  }
}