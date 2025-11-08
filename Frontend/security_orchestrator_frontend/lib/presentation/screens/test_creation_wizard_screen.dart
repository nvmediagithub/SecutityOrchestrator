import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/owasp_dto.dart';
import '../../presentation/providers/testing_provider.dart';
import '../../presentation/widgets/progress_indicator_widget.dart';
import '../../presentation/widgets/file_upload_widget.dart';
import 'openapi_upload_screen.dart';
import 'bpmn_upload_screen.dart';

class TestCreationWizardScreen extends ConsumerStatefulWidget {
  const TestCreationWizardScreen({super.key});

  @override
  ConsumerState<TestCreationWizardScreen> createState() => _TestCreationWizardScreenState();
}

class _TestCreationWizardScreenState extends ConsumerState<TestCreationWizardScreen> {
  final PageController _pageController = PageController();
  final TextEditingController _sessionNameController = TextEditingController();
  final TextEditingController _openApiUrlController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  
  int _currentStep = 0;
  String? _openApiUrl;
  List<String> _bpmnFiles = [];
  List<String> _selectedOwaspCategories = [];
  Map<String, dynamic> _testConfiguration = {};
  
  @override
  void dispose() {
    _pageController.dispose();
    _sessionNameController.dispose();
    _openApiUrlController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Мастер создания тестов'),
        elevation: 0,
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: Column(
        children: [
          _buildStepIndicator(),
          Expanded(
            child: PageView(
              controller: _pageController,
              onPageChanged: (index) {
                setState(() {
                  _currentStep = index;
                });
              },
              children: [
                _buildBasicInfoStep(),
                _buildOpenApiStep(),
                _buildBpmnStep(),
                _buildOwaspCategoriesStep(),
                _buildConfigurationStep(),
                _buildReviewStep(),
              ],
            ),
          ),
          _buildNavigationButtons(),
        ],
      ),
    );
  }

  Widget _buildStepIndicator() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: List.generate(6, (index) {
          final isActive = index <= _currentStep;
          final isCompleted = index < _currentStep;
          
          return Expanded(
            child: Row(
              children: [
                Container(
                  width: 32,
                  height: 32,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: isCompleted
                        ? Colors.green
                        : isActive
                            ? Theme.of(context).primaryColor
                            : Colors.grey.shade300,
                  ),
                  child: isCompleted
                      ? const Icon(Icons.check, color: Colors.white, size: 16)
                      : Text(
                          '${index + 1}',
                          style: TextStyle(
                            color: isActive ? Colors.white : Colors.grey.shade600,
                            fontWeight: FontWeight.bold,
                            fontSize: 12,
                          ),
                        ),
                ),
                if (index < 5)
                  Expanded(
                    child: Container(
                      height: 2,
                      color: isCompleted ? Colors.green : Colors.grey.shade300,
                    ),
                  ),
              ],
            ),
          );
        }),
      ),
    );
  }

  Widget _buildBasicInfoStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Основная информация',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 24),
          TextFormField(
            controller: _sessionNameController,
            decoration: const InputDecoration(
              labelText: 'Название тестовой сессии',
              hintText: 'Введите название для новой сессии тестирования',
              border: OutlineInputBorder(),
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Название обязательно';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),
          TextFormField(
            controller: _descriptionController,
            decoration: const InputDecoration(
              labelText: 'Описание (необязательно)',
              hintText: 'Краткое описание цели тестирования',
              border: OutlineInputBorder(),
            ),
            maxLines: 3,
          ),
          const SizedBox(height: 24),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Что мы будем тестировать?',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 12),
                  _buildInfoTile(
                    Icons.link,
                    'OpenAPI/Swagger спецификации',
                    'Анализ API на соответствие стандартам и безопасность',
                  ),
                  const SizedBox(height: 8),
                  _buildInfoTile(
                    Icons.account_tree,
                    'BPMN процессы',
                    'Проверка бизнес-логики и процессов',
                  ),
                  const SizedBox(height: 8),
                  _buildInfoTile(
                    Icons.security,
                    'OWASP уязвимости',
                    'Тестирование на распространенные уязвимости',
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOpenApiStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'OpenAPI/Swagger спецификация',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 24),
          TextFormField(
            controller: _openApiUrlController,
            decoration: const InputDecoration(
              labelText: 'URL OpenAPI документации',
              hintText: 'https://api.example.com/docs или https://api.example.com/swagger.json',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.link),
            ),
            onChanged: (value) {
              setState(() {
                _openApiUrl = value;
              });
            },
          ),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: _openApiUrl != null && _openApiUrl!.isNotEmpty
                  ? () => _validateOpenApiUrl()
                  : null,
              icon: const Icon(Icons.check_circle),
              label: const Text('Валидировать URL'),
            ),
          ),
          const SizedBox(height: 16),
          if (_openApiUrl != null && _openApiUrl!.isNotEmpty) ...[
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Валидация спецификации',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Icon(
                          _openApiUrl != null ? Icons.check_circle : Icons.error,
                          color: _openApiUrl != null ? Colors.green : Colors.red,
                        ),
                        const SizedBox(width: 8),
                        Expanded(
                          child: Text(
                            _openApiUrl != null
                                ? 'Спецификация готова к анализу'
                                : 'Введите корректный URL',
                            style: Theme.of(context).textTheme.bodyMedium,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ],
          const SizedBox(height: 16),
          InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const OpenApiUploadScreen(),
                ),
              );
            },
            child: Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    Icon(
                      Icons.cloud_upload,
                      color: Theme.of(context).primaryColor,
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Загрузить файл спецификации',
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          Text(
                            'Поддерживаются JSON и YAML форматы',
                            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                  color: Colors.grey.shade600,
                                ),
                          ),
                        ],
                      ),
                    ),
                    const Icon(Icons.chevron_right),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBpmnStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'BPMN диаграммы',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 24),
          Text(
            'Загрузите BPMN файлы для анализа бизнес-процессов',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 16),
          InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const BpmnUploadScreen(),
                ),
              );
            },
            child: Container(
              width: double.infinity,
              height: 200,
              decoration: BoxDecoration(
                border: Border.all(
                  color: Theme.of(context).primaryColor,
                  width: 2,
                  style: BorderStyle.solid,
                ),
                borderRadius: BorderRadius.circular(12),
                color: Theme.of(context).primaryColor.withOpacity(0.1),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.cloud_upload,
                    size: 48,
                    color: Theme.of(context).primaryColor,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Нажмите для загрузки BPMN файлов',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Поддерживаются .bpmn и .xml файлы',
                    style: Theme.of(context).textTheme.bodySmall,
                  ),
                ],
              ),
            ),
          ),
          if (_bpmnFiles.isNotEmpty) ...[
            const SizedBox(height: 24),
            Text(
              'Загруженные файлы (${_bpmnFiles.length})',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            ..._bpmnFiles.map((file) {
              return Card(
                child: ListTile(
                  leading: const Icon(Icons.description),
                  title: Text(file),
                  trailing: IconButton(
                    icon: const Icon(Icons.remove_circle),
                    onPressed: () {
                      setState(() {
                        _bpmnFiles.remove(file);
                      });
                    },
                  ),
                ),
              );
            }).toList(),
          ],
        ],
      ),
    );
  }

  Widget _buildOwaspCategoriesStep() {
    final categoriesAsync = ref.watch(testingStateProvider);
    
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'OWASP категории для тестирования',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          Text(
            'Выберите категории уязвимостей для тестирования',
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 24),
          categoriesAsync.when(
            data: (state) {
              if (state.owaspCategories.isEmpty) {
                return const Center(child: CircularProgressIndicator());
              }
              return GridView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  childAspectRatio: 1.5,
                  crossAxisSpacing: 8,
                  mainAxisSpacing: 8,
                ),
                itemCount: state.owaspCategories.length,
                itemBuilder: (context, index) {
                  final category = state.owaspCategories[index];
                  final isSelected = _selectedOwaspCategories.contains(category.code);
                  
                  return Card(
                    color: isSelected 
                        ? Theme.of(context).primaryColor.withOpacity(0.1)
                        : null,
                    child: InkWell(
                      onTap: () {
                        setState(() {
                          if (isSelected) {
                            _selectedOwaspCategories.remove(category.code);
                          } else {
                            _selectedOwaspCategories.add(category.code);
                          }
                        });
                      },
                      borderRadius: BorderRadius.circular(8),
                      child: Padding(
                        padding: const EdgeInsets.all(12),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(
                              category.code,
                              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                                    fontWeight: FontWeight.bold,
                                    color: isSelected 
                                        ? Theme.of(context).primaryColor
                                        : null,
                                  ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              category.name,
                              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                    color: isSelected 
                                        ? Theme.of(context).primaryColor
                                        : null,
                                  ),
                              textAlign: TextAlign.center,
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                            ),
                            const SizedBox(height: 4),
                            Container(
                              width: 8,
                              height: 8,
                              decoration: BoxDecoration(
                                shape: BoxShape.circle,
                                color: _getSeverityColor(category.severity),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  );
                },
              );
            },
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (error, stack) => Center(
              child: Text('Ошибка загрузки категорий: $error'),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildConfigurationStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Конфигурация тестирования',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 24),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Общие настройки',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 16),
                  SwitchListTile(
                    title: const Text('Включить анализ LLM'),
                    subtitle: const Text('Использовать AI для анализа результатов'),
                    value: _testConfiguration['enableLlmAnalysis'] ?? true,
                    onChanged: (value) {
                      setState(() {
                        _testConfiguration['enableLlmAnalysis'] = value;
                      });
                    },
                  ),
                  SwitchListTile(
                    title: const Text('Подробное логирование'),
                    subtitle: const Text('Сохранять детальные логи выполнения'),
                    value: _testConfiguration['verboseLogging'] ?? false,
                    onChanged: (value) {
                      setState(() {
                        _testConfiguration['verboseLogging'] = value;
                      });
                    },
                  ),
                  SwitchListTile(
                    title: const Text('Автоматическое исправление'),
                    subtitle: const Text('Попытка автоматического устранения найденных проблем'),
                    value: _testConfiguration['autoFix'] ?? false,
                    onChanged: (value) {
                      setState(() {
                        _testConfiguration['autoFix'] = value;
                      });
                    },
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Таймауты и лимиты',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    decoration: const InputDecoration(
                      labelText: 'Таймаут запроса (секунды)',
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.number,
                    initialValue: '30',
                    onChanged: (value) {
                      _testConfiguration['requestTimeout'] = int.tryParse(value) ?? 30;
                    },
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    decoration: const InputDecoration(
                      labelText: 'Максимальное количество запросов',
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.number,
                    initialValue: '100',
                    onChanged: (value) {
                      _testConfiguration['maxRequests'] = int.tryParse(value) ?? 100;
                    },
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildReviewStep() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Проверка конфигурации',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 24),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildReviewItem(
                    'Название сессии',
                    _sessionNameController.text,
                    Icons.folder,
                  ),
                  _buildReviewItem(
                    'OpenAPI URL',
                    _openApiUrl ?? 'Не указан',
                    Icons.link,
                  ),
                  _buildReviewItem(
                    'BPMN файлы',
                    '${_bpmnFiles.length} файл(ов)',
                    Icons.account_tree,
                  ),
                  _buildReviewItem(
                    'OWASP категории',
                    '${_selectedOwaspCategories.length} категорий',
                    Icons.security,
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          Card(
            color: Colors.green.shade50,
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  const Icon(Icons.check_circle, color: Colors.green),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Готово к запуску',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                                color: Colors.green.shade700,
                              ),
                        ),
                        Text(
                          'Все необходимые данные заполнены',
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: Colors.green.shade600,
                              ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoTile(IconData icon, String title, String subtitle) {
    return Row(
      children: [
        Icon(icon, color: Theme.of(context).primaryColor),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: Theme.of(context).textTheme.titleSmall,
              ),
              Text(
                subtitle,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Colors.grey.shade600,
                    ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildReviewItem(String title, String value, IconData icon) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Icon(icon, color: Theme.of(context).primaryColor),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey.shade600,
                      ),
                ),
                Text(
                  value,
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        fontWeight: FontWeight.w500,
                      ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNavigationButtons() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).cardColor,
        boxShadow: [
          BoxShadow(
            color: Colors.grey.shade300,
            blurRadius: 4,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          if (_currentStep > 0)
            OutlinedButton(
              onPressed: () {
                _pageController.previousPage(
                  duration: const Duration(milliseconds: 300),
                  curve: Curves.easeInOut,
                );
              },
              child: const Text('Назад'),
            ),
          const SizedBox(),
          if (_currentStep < 5)
            ElevatedButton(
              onPressed: _canProceedToNext()
                  ? () {
                      _pageController.nextPage(
                        duration: const Duration(milliseconds: 300),
                        curve: Curves.easeInOut,
                      );
                    }
                  : null,
              child: Text(_currentStep == 4 ? 'Завершить' : 'Далее'),
            )
          else
            ElevatedButton.icon(
              onPressed: _startTesting,
              icon: const Icon(Icons.play_arrow),
              label: const Text('Запустить тестирование'),
            ),
        ],
      ),
    );
  }

  bool _canProceedToNext() {
    switch (_currentStep) {
      case 0: // Basic Info
        return _sessionNameController.text.isNotEmpty;
      case 1: // OpenAPI
        return _openApiUrl != null && _openApiUrl!.isNotEmpty;
      case 2: // BPMN
        return true; // Optional step
      case 3: // OWASP
        return _selectedOwaspCategories.isNotEmpty;
      case 4: // Configuration
        return true;
      default:
        return true;
    }
  }

  Color _getSeverityColor(String severity) {
    switch (severity.toUpperCase()) {
      case 'CRITICAL':
        return Colors.red;
      case 'HIGH':
        return Colors.orange;
      case 'MEDIUM':
        return Colors.yellow;
      case 'LOW':
        return Colors.green;
      default:
        return Colors.grey;
    }
  }

  Future<void> _validateOpenApiUrl() async {
    // TODO: Implement OpenAPI URL validation
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Валидация OpenAPI URL...'),
        duration: Duration(seconds: 2),
      ),
    );
  }

  Future<void> _startTesting() async {
    try {
      final testingNotifier = ref.read(testingStateProvider.notifier);
      
      await testingNotifier.startEndToEndTesting(
        openApiUrl: _openApiUrl!,
        bpmnFiles: _bpmnFiles,
        owaspCategories: _selectedOwaspCategories,
        sessionName: _sessionNameController.text,
        configuration: _testConfiguration,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Тестирование запущено!'),
            backgroundColor: Colors.green,
          ),
        );
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка запуска тестирования: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }
}