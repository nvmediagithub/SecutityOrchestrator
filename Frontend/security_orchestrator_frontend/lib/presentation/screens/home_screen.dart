import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/process_dto.dart';
import '../../data/models/system_status_model.dart';
import '../../data/services/user_flow_api_service.dart';
import '../providers/process_provider.dart';
import '../providers/system_dashboard_provider.dart';
import '../widgets/process_list_item.dart';
import 'user_flow_main_screen.dart';
import 'user_flow_history_screen.dart';
import 'process_creation_screen.dart';
import 'workflow_creation_screen.dart';
import 'llm_dashboard_screen.dart';

class HomeScreen extends ConsumerStatefulWidget {
  const HomeScreen({super.key});

  @override
  ConsumerState<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends ConsumerState<HomeScreen> {
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = '';
  ProcessStatus? _statusFilter;
  int _selectedTab = 0;

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final systemDashboardAsync = ref.watch(systemDashboardProvider);
    final processesAsync = ref.watch(processesProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Security Orchestrator LLM'),
        actions: [
          // System status indicator
          systemDashboardAsync.when(
            loading: () => const Padding(
              padding: EdgeInsets.all(16),
              child: SizedBox(
                width: 16,
                height: 16,
                child: CircularProgressIndicator(strokeWidth: 2),
              ),
            ),
            error: (error, stack) => IconButton(
              icon: const Icon(Icons.error, color: Colors.red),
              onPressed: () => _showSystemDetails(context),
            ),
            data: (dashboard) => IconButton(
              icon: Icon(
                dashboard.isSystemHealthy ? Icons.check_circle : Icons.warning,
                color: dashboard.isSystemHealthy ? Colors.green : Colors.orange,
              ),
              onPressed: () => _showSystemDetails(context),
            ),
          ),
          // Refresh button
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => _refreshAll(),
          ),
        ],
      ),
      body: Column(
        children: [
          // System status overview
          systemDashboardAsync.when(
            loading: () => const SystemStatusLoadingWidget(),
            error: (error, stack) => SystemStatusErrorWidget(
              error: error.toString(),
              onRetry: () => ref.read(systemDashboardProvider.notifier).refresh(),
            ),
            data: (dashboard) => SystemStatusOverview(dashboard: dashboard),
          ),
          
          // Tab navigation
          Container(
            color: Theme.of(context).colorScheme.surface,
            child: Row(
              children: [
                _buildTabButton('User Flow', 0, Icons.analytics),
                _buildTabButton('Processes', 1, Icons.business),
                _buildTabButton('System Monitor', 2, Icons.monitor_heart),
                _buildTabButton('LLM Status', 3, Icons.smart_toy),
              ],
            ),
          ),
          
          // Tab content
          Expanded(
            child: _selectedTab == 0 ? _buildUserFlowTab() :
                   _selectedTab == 1 ? _buildProcessesTab() :
                   _selectedTab == 2 ? _buildSystemMonitorTab() :
                   _buildLLMStatusTab(),
          ),
        ],
      ),
      floatingActionButton: _selectedTab == 0 ? FloatingActionButton(
        onPressed: () => _startUserFlow(context),
        child: const Icon(Icons.play_arrow),
      ) : _selectedTab == 1 ? FloatingActionButton(
        onPressed: () => _showCreateOptions(context),
        child: const Icon(Icons.add),
      ) : null,
    );
  }

  Widget _buildTabButton(String label, int index, IconData icon) {
    final isSelected = _selectedTab == index;
    return Expanded(
      child: TextButton.icon(
        onPressed: () => setState(() => _selectedTab = index),
        icon: Icon(
          icon,
          color: isSelected ? Theme.of(context).colorScheme.primary : Colors.grey,
          size: 20,
        ),
        label: Text(
          label,
          style: TextStyle(
            color: isSelected ? Theme.of(context).colorScheme.primary : Colors.grey,
            fontSize: 12,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
          ),
        ),
        style: TextButton.styleFrom(
          backgroundColor: isSelected ?
            Theme.of(context).colorScheme.primaryContainer :
            Colors.transparent,
          padding: const EdgeInsets.symmetric(vertical: 12),
        ),
      ),
    );
  }

  Widget _buildProcessesTab() {
    final processesAsync = ref.watch(processesProvider);

    return Column(
      children: [
        // Search and filter bar
        Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              TextField(
                controller: _searchController,
                decoration: InputDecoration(
                  hintText: 'Search processes...',
                  prefixIcon: const Icon(Icons.search),
                  suffixIcon: _searchQuery.isNotEmpty
                      ? IconButton(
                          icon: const Icon(Icons.clear),
                          onPressed: () {
                            _searchController.clear();
                            setState(() {
                              _searchQuery = '';
                            });
                          },
                        )
                      : null,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  filled: true,
                  fillColor: Theme.of(context).colorScheme.surface,
                ),
                onChanged: (value) {
                  setState(() {
                    _searchQuery = value;
                  });
                },
              ),
              const SizedBox(height: 8),
              SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    FilterChip(
                      label: const Text('All'),
                      selected: _statusFilter == null,
                      onSelected: (selected) {
                        setState(() {
                          _statusFilter = null;
                        });
                      },
                    ),
                    const SizedBox(width: 8),
                    FilterChip(
                      label: const Text('Active'),
                      selected: _statusFilter == ProcessStatus.active,
                      onSelected: (selected) {
                        setState(() {
                          _statusFilter = selected ? ProcessStatus.active : null;
                        });
                      },
                    ),
                    const SizedBox(width: 8),
                    FilterChip(
                      label: const Text('Inactive'),
                      selected: _statusFilter == ProcessStatus.inactive,
                      onSelected: (selected) {
                        setState(() {
                          _statusFilter = selected ? ProcessStatus.inactive : null;
                        });
                      },
                    ),
                    const SizedBox(width: 8),
                    FilterChip(
                      label: const Text('Archived'),
                      selected: _statusFilter == ProcessStatus.archived,
                      onSelected: (selected) {
                        setState(() {
                          _statusFilter = selected ? ProcessStatus.archived : null;
                        });
                      },
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        
        // Processes list
        Expanded(
          child: processesAsync.when(
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (error, stack) => Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline, size: 64, color: Colors.red),
                  const SizedBox(height: 16),
                  Text(
                    'Error loading processes',
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
                    onPressed: () => ref.read(processesProvider.notifier).refresh(),
                    child: const Text('Retry'),
                  ),
                ],
              ),
            ),
            data: (processes) {
              final filteredProcesses = _filterProcesses(processes);

              return filteredProcesses.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.search_off,
                            size: 64,
                            color: Colors.grey[400],
                          ),
                          const SizedBox(height: 16),
                          const Text(
                            'No processes found',
                            style: TextStyle(
                              fontSize: 18,
                              color: Colors.grey,
                            ),
                          ),
                          if (_searchQuery.isNotEmpty || _statusFilter != null)
                            Padding(
                              padding: const EdgeInsets.only(top: 8),
                              child: Text(
                                'Try adjusting your search or filter criteria',
                                style: TextStyle(
                                  color: Colors.grey[600],
                                  fontSize: 14,
                                ),
                              ),
                            ),
                        ],
                      ),
                    )
                  : ListView.builder(
                      itemCount: filteredProcesses.length,
                      itemBuilder: (context, index) {
                        final process = filteredProcesses[index];
                        return ProcessListItem(process: process);
                      },
                    );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildSystemMonitorTab() {
    final systemDashboardAsync = ref.watch(systemDashboardProvider);

    return systemDashboardAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stack) => Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64, color: Colors.red),
            const SizedBox(height: 16),
            Text(
              'System monitoring error',
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
              onPressed: () => ref.read(systemDashboardProvider.notifier).refresh(),
              child: const Text('Retry'),
            ),
          ],
        ),
      ),
      data: (dashboard) => const SystemStatusDetailedWidget(),
    );
  }

  Widget _buildLLMStatusTab() {
    final systemDashboardAsync = ref.watch(systemDashboardProvider);

    return systemDashboardAsync.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stack) => Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64, color: Colors.red),
            const SizedBox(height: 16),
            Text(
              'LLM status error',
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
              onPressed: () => ref.read(systemDashboardProvider.notifier).refresh(),
              child: const Text('Retry'),
            ),
          ],
        ),
      ),
      data: (dashboard) => const LLMStatusWidget(),
    );
  }

  List<ProcessSummaryDto> _filterProcesses(List<ProcessSummaryDto> processes) {
    return processes.where((process) {
      final matchesSearch = _searchQuery.isEmpty ||
          process.name.toLowerCase().contains(_searchQuery.toLowerCase());
      final matchesFilter = _statusFilter == null || process.status == _statusFilter;
      return matchesSearch && matchesFilter;
    }).toList();
  }

  void _refreshAll() {
    ref.read(processesProvider.notifier).refresh();
    ref.read(systemDashboardProvider.notifier).refresh();
  }

  void _showSystemDetails(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => const SystemDetailsBottomSheet(),
    );
  }

  void _showCreateOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.business),
              title: const Text('Create Process'),
              subtitle: const Text('Upload BPMN file and configure process'),
              onTap: () {
                Navigator.of(context).pop();
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (context) => const ProcessCreationScreen(),
                  ),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.work),
              title: const Text('Create Workflow'),
              subtitle: const Text('Build workflow from existing process'),
              onTap: () {
                Navigator.of(context).pop();
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (context) => const WorkflowCreationScreen(),
                  ),
                );
              },
            ),
            ListTile(
              leading: const Icon(Icons.smart_toy),
              title: const Text('LLM Dashboard'),
              subtitle: const Text('Configure and manage LLM providers and models'),
              onTap: () {
                Navigator.of(context).pop();
                Navigator.of(context).pushNamed('/llm-dashboard');
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUserFlowTab() {
    return FutureBuilder<bool>(
      future: UserFlowApiService().checkHealth(),
      builder: (context, snapshot) {
        final isConnected = snapshot.data ?? false;
        
        return Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Header card
              Card(
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
                                  'Анализ безопасности',
                                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  'Простой и быстрый анализ безопасности с ИИ',
                                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                    color: Colors.grey[600],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 16),
                      // Connection status
                      Row(
                        children: [
                          Icon(
                            isConnected ? Icons.check_circle : Icons.error,
                            color: isConnected ? Colors.green : Colors.red,
                            size: 16,
                          ),
                          const SizedBox(width: 4),
                          Text(
                            isConnected ? 'Backend подключен' : 'Backend недоступен',
                            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                              color: isConnected ? Colors.green : Colors.red,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              
              const SizedBox(height: 16),
              
              // Quick actions
              Text(
                'Быстрые действия',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 12),
              
              // New analysis button
              SizedBox(
                width: double.infinity,
                child: ElevatedButton.icon(
                  onPressed: isConnected ? () => _startUserFlow(context) : null,
                  icon: const Icon(Icons.play_arrow),
                  label: const Text('Новый анализ'),
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                ),
              ),
              
              // History button
              SizedBox(
                width: double.infinity,
                child: OutlinedButton.icon(
                  onPressed: isConnected ? () => _showHistory(context) : null,
                  icon: const Icon(Icons.history),
                  label: const Text('История анализов'),
                  style: OutlinedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                ),
              ),
              
              const SizedBox(height: 20),
              
              // Features
              Text(
                'Возможности',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 12),
              
              _buildFeatureCard(
                Icons.upload_file,
                'Загрузка документов',
                'Поддержка DOCX, TXT, PDF файлов с drag & drop',
              ),
              const SizedBox(height: 8),
              
              _buildFeatureCard(
                Icons.psychology,
                'ИИ-анализ',
                'Автоматический анализ безопасности с помощью LLM',
              ),
              const SizedBox(height: 8),
              
              _buildFeatureCard(
                Icons.timeline,
                'Real-time обновления',
                'Отслеживание прогресса в реальном времени',
              ),
              const SizedBox(height: 8),
              
              _buildFeatureCard(
                Icons.download,
                'Экспорт отчетов',
                'Экспорт в PDF, DOCX, HTML форматы',
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildFeatureCard(IconData icon, String title, String description) {
    return Card(
      child: ListTile(
        leading: Icon(
          icon,
          color: Theme.of(context).colorScheme.primary,
        ),
        title: Text(
          title,
          style: const TextStyle(fontWeight: FontWeight.w600),
        ),
        subtitle: Text(description),
      ),
    );
  }

  void _startUserFlow(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const UserFlowMainScreen(),
      ),
    );
  }

  void _showHistory(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const UserFlowHistoryScreen(),
      ),
    );
  }
}

// System status overview widget
class SystemStatusOverview extends StatelessWidget {
  final SystemDashboardData dashboard;

  const SystemStatusOverview({super.key, required this.dashboard});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      color: Theme.of(context).colorScheme.surface,
      child: Row(
        children: [
          Icon(
            dashboard.isSystemHealthy ? Icons.check_circle : Icons.warning,
            color: dashboard.isSystemHealthy ? Colors.green : Colors.orange,
            size: 24,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  dashboard.isSystemHealthy ? 'All Systems Operational' : 'System Issues Detected',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    color: dashboard.isSystemHealthy ? Colors.green : Colors.orange,
                  ),
                ),
                Text(
                  'Last updated: ${_formatTime(dashboard.lastUpdated)}',
                  style: Theme.of(context).textTheme.bodySmall,
                ),
              ],
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                '${dashboard.criticalServicesStatus.where((s) => s).length}/${dashboard.criticalServicesStatus.length}',
                style: Theme.of(context).textTheme.titleSmall,
              ),
              Text(
                'Services Online',
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ],
          ),
        ],
      ),
    );
  }

  String _formatTime(DateTime time) {
    final now = DateTime.now();
    final difference = now.difference(time);
    
    if (difference.inMinutes < 1) {
      return 'Just now';
    } else if (difference.inMinutes < 60) {
      return '${difference.inMinutes}m ago';
    } else if (difference.inHours < 24) {
      return '${difference.inHours}h ago';
    } else {
      return '${difference.inDays}d ago';
    }
  }
}

// System status loading widget
class SystemStatusLoadingWidget extends StatelessWidget {
  const SystemStatusLoadingWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      color: Theme.of(context).colorScheme.surface,
      child: Row(
        children: [
          const SizedBox(
            width: 24,
            height: 24,
            child: CircularProgressIndicator(strokeWidth: 2),
          ),
          const SizedBox(width: 12),
          Text(
            'Loading system status...',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
        ],
      ),
    );
  }
}

// System status error widget
class SystemStatusErrorWidget extends StatelessWidget {
  final String error;
  final VoidCallback onRetry;

  const SystemStatusErrorWidget({
    super.key,
    required this.error,
    required this.onRetry,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      color: Theme.of(context).colorScheme.surface,
      child: Row(
        children: [
          const Icon(Icons.error, color: Colors.red, size: 24),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'System Status Error',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(color: Colors.red),
                ),
                Text(
                  error,
                  style: Theme.of(context).textTheme.bodySmall,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
          TextButton(
            onPressed: onRetry,
            child: const Text('Retry'),
          ),
        ],
      ),
    );
  }
}

// Placeholder widgets for system monitoring and LLM status tabs
class SystemStatusDetailedWidget extends StatelessWidget {
  const SystemStatusDetailedWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Text('System Monitor - Detailed View'),
    );
  }
}

class LLMStatusWidget extends StatelessWidget {
  const LLMStatusWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Text('LLM Status - Real-time Monitoring'),
    );
  }
}

class SystemDetailsBottomSheet extends StatelessWidget {
  const SystemDetailsBottomSheet({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'System Details',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          const Text('This will show detailed system information.'),
        ],
      ),
    );
  }
}