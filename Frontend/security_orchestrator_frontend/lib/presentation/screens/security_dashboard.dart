import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'test_creation_wizard_screen.dart';
import 'vulnerability_dashboard_screen.dart';
import 'logs_viewer_screen.dart';
import '../providers/testing_provider.dart';
import '../providers/logs_provider.dart';

class SecurityDashboard extends ConsumerStatefulWidget {
  const SecurityDashboard({super.key});

  @override
  ConsumerState<SecurityDashboard> createState() => _SecurityDashboardState();
}

class _SecurityDashboardState extends ConsumerState<SecurityDashboard> {
  int _selectedIndex = 0;
  final PageController _pageController = PageController();

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final testingState = ref.watch(testingStateProvider);
    final logsState = ref.watch(logsStateProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Security Orchestrator - Тестирование'),
        elevation: 2,
        backgroundColor: Theme.of(context).primaryColor,
        foregroundColor: Colors.white,
        actions: [
          // Quick actions
          IconButton(
            onPressed: _refreshData,
            icon: const Icon(Icons.refresh),
            tooltip: 'Обновить данные',
          ),
          IconButton(
            onPressed: _showSystemStatus,
            icon: const Icon(Icons.info),
            tooltip: 'Статус системы',
          ),
          PopupMenuButton<String>(
            icon: const Icon(Icons.more_vert),
            onSelected: _handleMenuAction,
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'settings',
                child: ListTile(
                  leading: Icon(Icons.settings),
                  title: Text('Настройки'),
                ),
              ),
              const PopupMenuItem(
                value: 'help',
                child: ListTile(
                  leading: Icon(Icons.help),
                  title: Text('Справка'),
                ),
              ),
              const PopupMenuItem(
                value: 'about',
                child: ListTile(
                  leading: Icon(Icons.info),
                  title: Text('О программе'),
                ),
              ),
            ],
          ),
        ],
      ),
      body: Row(
        children: [
          // Side Navigation
          Container(
            width: 250,
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.surface,
              boxShadow: [
                BoxShadow(
                  color: Colors.grey.shade300,
                  blurRadius: 4,
                  offset: const Offset(2, 0),
                ),
              ],
            ),
            child: _buildSideNavigation(),
          ),
          // Main Content
          Expanded(
            child: PageView(
              controller: _pageController,
              onPageChanged: (index) {
                setState(() {
                  _selectedIndex = index;
                });
              },
              children: [
                _buildDashboardView(),
                _buildActiveTestsView(),
                _buildVulnerabilitiesView(),
                _buildLogsView(),
                _buildReportsView(),
              ],
            ),
          ),
        ],
      ),
      floatingActionButton: _buildFloatingActionButton(),
      bottomNavigationBar: _buildBottomNavigation(),
    );
  }

  Widget _buildSideNavigation() {
    return ListView(
      padding: const EdgeInsets.all(8),
      children: [
        const SizedBox(height: 16),
        _buildNavItem(
          index: 0,
          icon: Icons.dashboard,
          title: 'Главная панель',
          subtitle: 'Обзор системы',
        ),
        _buildNavItem(
          index: 1,
          icon: Icons.play_circle,
          title: 'Активные тесты',
          subtitle: 'Мониторинг выполнения',
        ),
        _buildNavItem(
          index: 2,
          icon: Icons.security,
          title: 'Уязвимости',
          subtitle: 'Найденные проблемы',
        ),
        _buildNavItem(
          index: 3,
          icon: Icons.article,
          title: 'Логи',
          subtitle: 'Системные логи',
        ),
        _buildNavItem(
          index: 4,
          icon: Icons.assessment,
          title: 'Отчеты',
          subtitle: 'Генерация отчетов',
        ),
        const Divider(),
        _buildQuickStats(),
        const Divider(),
        _buildSystemHealth(),
      ],
    );
  }

  Widget _buildNavItem({
    required int index,
    required IconData icon,
    required String title,
    required String subtitle,
  }) {
    final isSelected = _selectedIndex == index;
    
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 2),
      child: Material(
        color: isSelected 
            ? Theme.of(context).primaryColor.withOpacity(0.1)
            : Colors.transparent,
        borderRadius: BorderRadius.circular(8),
        child: InkWell(
          borderRadius: BorderRadius.circular(8),
          onTap: () {
            setState(() {
              _selectedIndex = index;
            });
            _pageController.animateToPage(
              index,
              duration: const Duration(milliseconds: 300),
              curve: Curves.easeInOut,
            );
          },
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Row(
              children: [
                Icon(
                  icon,
                  color: isSelected 
                      ? Theme.of(context).primaryColor
                      : Colors.grey.shade700,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: TextStyle(
                          color: isSelected 
                              ? Theme.of(context).primaryColor
                              : Colors.grey.shade900,
                          fontWeight: isSelected 
                              ? FontWeight.w600 
                              : FontWeight.normal,
                        ),
                      ),
                      Text(
                        subtitle,
                        style: TextStyle(
                          color: Colors.grey.shade600,
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildQuickStats() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Быстрая статистика',
              style: Theme.of(context).textTheme.titleSmall,
            ),
            const SizedBox(height: 8),
            _buildStatItem('Активные сессии', '3', Icons.radio_button_on),
            const SizedBox(height: 4),
            _buildStatItem('Найдено уязвимостей', '48', Icons.warning),
            const SizedBox(height: 4),
            _buildStatItem('Ошибки в логах', '12', Icons.error),
            const SizedBox(height: 4),
            _buildStatItem('Успешные тесты', '89%', Icons.check_circle),
          ],
        ),
      ),
    );
  }

  Widget _buildStatItem(String label, String value, IconData icon) {
    return Row(
      children: [
        Icon(icon, size: 16, color: Colors.blue),
        const SizedBox(width: 8),
        Expanded(
          child: Text(
            label,
            style: Theme.of(context).textTheme.bodySmall,
          ),
        ),
        Text(
          value,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: Colors.blue,
              ),
        ),
      ],
    );
  }

  Widget _buildSystemHealth() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Состояние модулей',
              style: Theme.of(context).textTheme.titleSmall,
            ),
            const SizedBox(height: 8),
            _buildModuleStatus('OpenAPI анализ', true),
            const SizedBox(height: 4),
            _buildModuleStatus('BPMN анализ', true),
            const SizedBox(height: 4),
            _buildModuleStatus('OWASP тестирование', false),
            const SizedBox(height: 4),
            _buildModuleStatus('LLM сервис', true),
          ],
        ),
      ),
    );
  }

  Widget _buildModuleStatus(String module, bool isHealthy) {
    return Row(
      children: [
        Container(
          width: 8,
          height: 8,
          decoration: BoxDecoration(
            color: isHealthy ? Colors.green : Colors.red,
            shape: BoxShape.circle,
          ),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: Text(
            module,
            style: Theme.of(context).textTheme.bodySmall,
          ),
        ),
        Text(
          isHealthy ? 'ОК' : 'Ошибка',
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: isHealthy ? Colors.green : Colors.red,
                fontWeight: FontWeight.w500,
              ),
        ),
      ],
    );
  }

  Widget _buildDashboardView() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Text(
                'Панель управления',
                style: Theme.of(context).textTheme.headlineMedium,
              ),
              const Spacer(),
              _buildTimeRangeSelector(),
            ],
          ),
          const SizedBox(height: 24),
          _buildStatsOverview(),
          const SizedBox(height: 24),
          _buildRecentActivity(),
          const SizedBox(height: 24),
          _buildSystemAlerts(),
        ],
      ),
    );
  }

  Widget _buildActiveTestsView() {
    final testingState = ref.watch(testingStateProvider);
    
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Text(
                'Активные тестовые сессии',
                style: Theme.of(context).textTheme.headlineMedium,
              ),
              const Spacer(),
              ElevatedButton.icon(
                onPressed: _startNewTest,
                icon: const Icon(Icons.add),
                label: const Text('Новый тест'),
              ),
            ],
          ),
        ),
        Expanded(
          child: testingState.when(
            data: (state) {
              if (state.activeSessions.isEmpty) {
                return const Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.play_circle_outline, size: 64, color: Colors.grey),
                      SizedBox(height: 16),
                      Text('Активных тестов нет'),
                    ],
                  ),
                );
              }
              
              return ListView.builder(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                itemCount: state.activeSessions.length,
                itemBuilder: (context, index) {
                  final session = state.activeSessions[index];
                  return _buildSessionCard(session);
                },
              );
            },
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (error, stack) => Center(
              child: Text('Ошибка: $error'),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildVulnerabilitiesView() {
    return const VulnerabilityDashboardScreen();
  }

  Widget _buildLogsView() {
    return const LogsViewerScreen();
  }

  Widget _buildReportsView() {
    return const Center(
      child: Text('Отчеты'),
    );
  }

  Widget _buildStatsOverview() {
    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: 4,
      crossAxisSpacing: 16,
      mainAxisSpacing: 16,
      children: [
        _buildStatCard(
          'Активные тесты',
          '3',
          Icons.play_circle,
          Colors.blue,
        ),
        _buildStatCard(
          'Найдено уязвимостей',
          '48',
          Icons.security,
          Colors.red,
        ),
        _buildStatCard(
          'Критические',
          '5',
          Icons.dangerous,
          Colors.red.shade700,
        ),
        _buildStatCard(
          'Пройдено тестов',
          '89%',
          Icons.check_circle,
          Colors.green,
        ),
      ],
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: color, size: 32),
            const SizedBox(height: 8),
            Text(
              value,
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: color,
                  ),
            ),
            Text(
              title,
              style: Theme.of(context).textTheme.bodyMedium,
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTimeRangeSelector() {
    return DropdownButton<String>(
      value: '24h',
      items: [
        DropdownMenuItem(value: '1h', child: Text('1 час')),
        DropdownMenuItem(value: '24h', child: Text('24 часа')),
        DropdownMenuItem(value: '7d', child: Text('7 дней')),
        DropdownMenuItem(value: '30d', child: Text('30 дней')),
      ],
      onChanged: (value) {
        // Handle time range change
      },
    );
  }

  Widget _buildRecentActivity() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Последняя активность',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 16),
            ...List.generate(5, (index) {
              return _buildActivityItem(
                'Завершен анализ OpenAPI спецификации',
                '10 мин назад',
                Icons.check_circle,
                Colors.green,
              );
            }),
          ],
        ),
      ),
    );
  }

  Widget _buildActivityItem(String message, String time, IconData icon, Color color) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Icon(icon, color: color, size: 20),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  message,
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
                Text(
                  time,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey.shade600,
                      ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSystemAlerts() {
    return Card(
      color: Colors.orange.shade50,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.warning, color: Colors.orange.shade700),
                const SizedBox(width: 8),
                Text(
                  'Системные предупреждения',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        color: Colors.orange.shade700,
                      ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Text(
              'Обнаружены высокие нагрузки на OpenAPI модуль',
              style: Theme.of(context).textTheme.bodyMedium,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSessionCard(dynamic session) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.radio_button_on, color: Colors.blue),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    session.name ?? 'Безымянная сессия',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                ),
                Chip(
                  label: Text(session.status),
                  backgroundColor: Colors.blue.shade100,
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              'OpenAPI: ${session.openApiUrl}',
              style: Theme.of(context).textTheme.bodySmall,
            ),
            const SizedBox(height: 8),
            LinearProgressIndicator(
              value: (session.progress?.percentage ?? 0) / 100,
              backgroundColor: Colors.grey.shade300,
            ),
            const SizedBox(height: 4),
            Text(
              '${session.progress?.percentage ?? 0}% - ${session.progress?.currentStep ?? ""}',
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBottomNavigation() {
    return BottomNavigationBar(
      currentIndex: _selectedIndex,
      onTap: (index) {
        setState(() {
          _selectedIndex = index;
        });
        _pageController.animateToPage(
          index,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeInOut,
        );
      },
      type: BottomNavigationBarType.fixed,
      items: const [
        BottomNavigationBarItem(
          icon: Icon(Icons.dashboard),
          label: 'Панель',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.play_circle),
          label: 'Тесты',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.security),
          label: 'Уязвимости',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.article),
          label: 'Логи',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.assessment),
          label: 'Отчеты',
        ),
      ],
    );
  }

  Widget _buildFloatingActionButton() {
    if (_selectedIndex != 1) {
      return null;
    }
    
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        FloatingActionButton(
          heroTag: "new_test",
          onPressed: _startNewTest,
          child: const Icon(Icons.add),
          tooltip: 'Новый тест',
        ),
        const SizedBox(height: 8),
        FloatingActionButton(
          heroTag: "pause_all",
          onPressed: _pauseAllTests,
          child: const Icon(Icons.pause),
          tooltip: 'Приостановить все',
        ),
      ],
    );
  }

  // Actions
  void _refreshData() async {
    try {
      final testingNotifier = ref.read(testingStateProvider.notifier);
      final logsNotifier = ref.read(logsStateProvider.notifier);
      
      await Future.wait([
        testingNotifier.getVulnerabilities(),
        logsNotifier.getLogs(),
      ]);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Ошибка обновления: $e'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  void _startNewTest() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => const TestCreationWizardScreen(),
      ),
    );
  }

  void _pauseAllTests() {
    // Pause all running tests
  }

  void _showSystemStatus() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Статус системы'),
        content: const Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: Icon(Icons.check_circle, color: Colors.green),
              title: Text('OpenAPI модуль'),
              subtitle: Text('Работает нормально'),
            ),
            ListTile(
              leading: Icon(Icons.check_circle, color: Colors.green),
              title: Text('BPMN модуль'),
              subtitle: Text('Работает нормально'),
            ),
            ListTile(
              leading: Icon(Icons.error, color: Colors.red),
              title: Text('OWASP модуль'),
              subtitle: Text('Недоступен'),
            ),
            ListTile(
              leading: Icon(Icons.check_circle, color: Colors.green),
              title: Text('LLM сервис'),
              subtitle: Text('Работает нормально'),
            ),
          ],
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

  void _handleMenuAction(String action) {
    switch (action) {
      case 'settings':
        // Show settings
        break;
      case 'help':
        // Show help
        break;
      case 'about':
        // Show about dialog
        break;
    }
  }
}