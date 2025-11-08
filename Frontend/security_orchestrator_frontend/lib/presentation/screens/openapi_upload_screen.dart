import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/services/testing_service.dart';
import '../../presentation/widgets/file_upload_widget.dart';
import '../../data/models/api_response.dart';

class OpenApiUploadScreen extends ConsumerStatefulWidget {
  const OpenApiUploadScreen({super.key});

  @override
  ConsumerState<OpenApiUploadScreen> createState() => _OpenApiUploadScreenState();
}

class _OpenApiUploadScreenState extends ConsumerState<OpenApiUploadScreen> {
  final TextEditingController _urlController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  
  bool _isUrlMode = true;
  bool _isValidating = false;
  Map<String, dynamic>? _validationResult;
  String? _uploadedFileId;
  String? _uploadedFileName;

  @override
  void dispose() {
    _urlController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Загрузка OpenAPI спецификации'),
        elevation: 0,
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildModeSelector(),
              const SizedBox(height: 24),
              if (_isUrlMode) _buildUrlInputSection() else _buildFileUploadSection(),
              const SizedBox(height: 24),
              if (_validationResult != null) _buildValidationResult(),
              const SizedBox(height: 24),
              if (_uploadedFileId != null) _buildUploadSuccess(),
              const SizedBox(height: 24),
              _buildInfoSection(),
            ],
          ),
        ),
      ),
      bottomNavigationBar: _buildBottomActions(),
    );
  }

  Widget _buildModeSelector() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Способ загрузки',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _buildModeButton(
                    'По URL',
                    Icons.link,
                    _isUrlMode,
                    () => setState(() => _isUrlMode = true),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildModeButton(
                    'Файл',
                    Icons.upload_file,
                    !_isUrlMode,
                    () => setState(() => _isUrlMode = false),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildModeButton(String title, IconData icon, bool isSelected, VoidCallback onTap) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(8),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 12),
        decoration: BoxDecoration(
          color: isSelected 
              ? Theme.of(context).primaryColor.withOpacity(0.1)
              : null,
          border: Border.all(
            color: isSelected 
                ? Theme.of(context).primaryColor
                : Colors.grey.shade300,
            width: 2,
          ),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Column(
          children: [
            Icon(
              icon,
              color: isSelected 
                  ? Theme.of(context).primaryColor
                  : Colors.grey.shade600,
              size: 24,
            ),
            const SizedBox(height: 8),
            Text(
              title,
              style: TextStyle(
                color: isSelected 
                    ? Theme.of(context).primaryColor
                    : Colors.grey.shade600,
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.normal,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUrlInputSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'URL спецификации',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _urlController,
              decoration: const InputDecoration(
                labelText: 'OpenAPI/Swagger URL',
                hintText: 'https://api.example.com/docs или https://api.example.com/swagger.json',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.link),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Введите URL спецификации';
                }
                if (!Uri.tryParse(value)!.hasAbsolutePath) {
                  return 'Введите корректный URL';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: const InputDecoration(
                labelText: 'Описание (необязательно)',
                hintText: 'Краткое описание API',
                border: OutlineInputBorder(),
              ),
              maxLines: 2,
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: _isValidating ? null : _validateUrl,
                icon: _isValidating
                    ? const SizedBox(
                        width: 16,
                        height: 16,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Icon(Icons.check_circle),
                label: Text(_isValidating ? 'Валидация...' : 'Валидировать URL'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildFileUploadSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Загрузка файла',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 16),
            Text(
              'Поддерживаемые форматы: JSON, YAML',
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Colors.grey.shade600,
                  ),
            ),
            const SizedBox(height: 16),
            FileUploadWidget(
              acceptedFileTypes: ['.json', '.yaml', '.yml'],
              maxFileSize: 10 * 1024 * 1024, // 10MB
              onFileSelected: _onFileSelected,
              onUploadProgress: _onUploadProgress,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: const InputDecoration(
                labelText: 'Описание (необязательно)',
                hintText: 'Краткое описание API',
                border: OutlineInputBorder(),
              ),
              maxLines: 2,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildValidationResult() {
    final isValid = _validationResult?['valid'] == true;
    final issues = _validationResult?['issues'] as List<dynamic>? ?? [];
    
    return Card(
      color: isValid ? Colors.green.shade50 : Colors.orange.shade50,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  isValid ? Icons.check_circle : Icons.warning,
                  color: isValid ? Colors.green.shade700 : Colors.orange.shade700,
                ),
                const SizedBox(width: 12),
                Text(
                  isValid ? 'Спецификация валидна' : 'Обнаружены проблемы',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: isValid ? Colors.green.shade700 : Colors.orange.shade700,
                      ),
                ),
              ],
            ),
            if (issues.isNotEmpty) ...[
              const SizedBox(height: 12),
              Text(
                'Проблемы:',
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      color: Colors.orange.shade700,
                    ),
              ),
              const SizedBox(height: 8),
              ...issues.map<Widget>((issue) {
                return Padding(
                  padding: const EdgeInsets.symmetric(vertical: 2),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        width: 4,
                        height: 4,
                        margin: const EdgeInsets.only(top: 8, right: 8),
                        decoration: BoxDecoration(
                          color: Colors.orange.shade700,
                          shape: BoxShape.circle,
                        ),
                      ),
                      Expanded(
                        child: Text(
                          issue.toString(),
                          style: Theme.of(context).textTheme.bodySmall,
                        ),
                      ),
                    ],
                  ),
                );
              }).toList(),
            ],
            if (_validationResult?['summary'] != null) ...[
              const SizedBox(height: 12),
              Text(
                'Информация:',
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      color: Colors.green.shade700,
                    ),
              ),
              const SizedBox(height: 8),
              Text(
                _validationResult!['summary'].toString(),
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildUploadSuccess() {
    return Card(
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
                    'Файл успешно загружен',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          color: Colors.green.shade700,
                        ),
                  ),
                  Text(
                    _uploadedFileName ?? 'Неизвестный файл',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.green.shade600,
                        ),
                  ),
                ],
              ),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context, {
                  'fileId': _uploadedFileId,
                  'fileName': _uploadedFileName,
                  'description': _descriptionController.text,
                });
              },
              child: const Text('Использовать'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Поддерживаемые форматы',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 12),
            _buildFormatInfo(
              'OpenAPI 3.0',
              'Рекомендуемый формат',
              'https://swagger.io/specification/',
            ),
            const SizedBox(height: 8),
            _buildFormatInfo(
              'Swagger 2.0',
              'Поддерживается',
              'https://swagger.io/specification/v2/',
            ),
            const SizedBox(height: 8),
            _buildFormatInfo(
              'JSON/YAML',
              'Форматы файлов',
              'Поддерживаются оба формата',
            ),
            const SizedBox(height: 16),
            Text(
              'Примеры URL',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 12),
            _buildUrlExample('https://petstore3.swagger.io/api/v3/openapi.json'),
            const SizedBox(height: 8),
            _buildUrlExample('https://api.coinbase.com/v2/docs'),
            const SizedBox(height: 8),
            _buildUrlExample('https://jsonapi.org/examples'),
          ],
        ),
      ),
    );
  }

  Widget _buildFormatInfo(String title, String subtitle, String description) {
    return Row(
      children: [
        Container(
          width: 8,
          height: 8,
          decoration: BoxDecoration(
            color: Theme.of(context).primaryColor,
            shape: BoxShape.circle,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
              ),
              Text(
                '$subtitle • $description',
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

  Widget _buildUrlExample(String url) {
    return InkWell(
      onTap: () {
        _urlController.text = url;
        setState(() {});
      },
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.grey.shade50,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: Colors.grey.shade200),
        ),
        child: Text(
          url,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                fontFamily: 'monospace',
                color: Colors.blue.shade600,
              ),
        ),
      ),
    );
  }

  Widget _buildBottomActions() {
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
          OutlinedButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Отмена'),
          ),
          ElevatedButton.icon(
            onPressed: _canProceed() ? _proceed : null,
            icon: const Icon(Icons.check),
            label: const Text('Продолжить'),
          ),
        ],
      ),
    );
  }

  bool _canProceed() {
    if (_isUrlMode) {
      return _validationResult != null && _validationResult!['valid'] == true;
    } else {
      return _uploadedFileId != null;
    }
  }

  Future<void> _validateUrl() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _isValidating = true;
      _validationResult = null;
    });

    try {
      final testingService = ref.read(testingServiceProvider);
      final result = await testingService.validateOpenApi(_urlController.text);
      
      setState(() {
        _isValidating = false;
        _validationResult = result;
      });
    } catch (e) {
      setState(() {
        _isValidating = false;
        _validationResult = {
          'valid': false,
          'issues': ['Ошибка валидации: $e'],
        };
      });
    }
  }

  void _onFileSelected(String filePath) async {
    // File will be processed by FileUploadWidget
  }

  void _onUploadProgress(double progress) {
    // Handle upload progress
  }

  void _proceed() {
    if (_isUrlMode) {
      Navigator.pop(context, {
        'url': _urlController.text,
        'description': _descriptionController.text,
        'validationResult': _validationResult,
      });
    } else {
      Navigator.pop(context, {
        'fileId': _uploadedFileId,
        'fileName': _uploadedFileName,
        'description': _descriptionController.text,
      });
    }
  }
}