import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/process_dto.dart';
import '../../data/models/system_status_model.dart';
import '../providers/process_provider.dart';
import '../providers/system_dashboard_provider.dart';
import '../widgets/process_list_item.dart';
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
                _buildTabButton('Processes', 0, Icons.business),
                _buildTabButton('System Monitor', 1, Icons.monitor_heart),
                _buildTabButton('LLM Status', 2, Icons.smart_toy),
              ],
            ),
          ),
          
          // Tab content
          Expanded(
            child: _selectedTab == 0 ? _buildProcessesTab() :
                   _selectedTab == 1 ? _buildSystemMonitorTab() :
                   _buildLLMStatusTab(),
          ),
        ],
      ),
      floatingActionButton: _selectedTab == 0 ? FloatingActionButton(
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