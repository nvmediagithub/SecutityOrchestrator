import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/services/testing_service.dart';
import '../../presentation/widgets/file_upload_widget.dart';
import '../../data/models/api_response.dart';

class BpmnUploadScreen extends ConsumerStatefulWidget {
  const BpmnUploadScreen({super.key});

  @override
  ConsumerState<BpmnUploadScreen> createState() => _BpmnUploadScreenState();
}

class _BpmnUploadScreenState extends ConsumerState<BpmnUploadScreen> {
  final TextEditingController _descriptionController = TextEditingController();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  
  List<UploadedBpmnFile> _uploadedFiles = [];
  bool _isValidating = false;
  String? _validationResult;

  @override
  void dispose() {
    _descriptionController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Загрузка BPMN диаграмм'),
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
              _buildUploadSection(),
              const SizedBox(height: 24),
              if (_uploadedFiles.isNotEmpty) _buildUploadedFilesSection(),
              const SizedBox(height: 24),
              if (_validationResult != null) _buildValidationResult(),
              const SizedBox(height: 24),
              _buildInfoSection(),
            ],
          ),
        ),
      ),
      bottomNavigationBar: _buildBottomActions(),
    );
  }

  Widget _buildUploadSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Загрузка BPMN файлов',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 16),
            Text(
              'Поддерживаемые форматы: .bpmn, .xml',
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    color: Colors.grey.shade600,
                  ),
            ),
            const SizedBox(height: 16),
            FileUploadWidget(
              acceptedFileTypes: ['.bpmn', '.xml'],
              maxFileSize: 20 * 1024 * 1024, // 20MB
              onFileSelected: _onFileSelected,
              onUploadProgress: _onUploadProgress,
              allowMultipleFiles: true,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: const InputDecoration(
                labelText: 'Описание (необязательно)',
                hintText: 'Краткое описание бизнес-процессов',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildUploadedFilesSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(
                  'Загруженные файлы (${_uploadedFiles.length})',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const Spacer(),
                TextButton.icon(
                  onPressed: _clearAllFiles,
                  icon: const Icon(Icons.clear_all, size: 16),
                  label: const Text('Очистить все'),
                ),
              ],
            ),
            const SizedBox(height: 16),
            ..._uploadedFiles.map((file) => _buildFileItem(file)).toList(),
            if (_uploadedFiles.isNotEmpty) ...[
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton.icon(
                  onPressed: _isValidating ? null : _validateAllFiles,
                  icon: _isValidating
                      ? const SizedBox(
                          width: 16,
                          height: 16,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : const Icon(Icons.check_circle),
                  label: Text(_isValidating ? 'Валидация...' : 'Валидировать все файлы'),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildFileItem(UploadedBpmnFile file) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 4),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Container(
              width: 40,
              height: 40,
              decoration: BoxDecoration(
                color: file.isValid
                    ? Colors.green.shade100
                    : file.validationError != null
                        ? Colors.red.shade100
                        : Colors.grey.shade100,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Icon(
                file.isValid
                    ? Icons.check_circle
                    : file.validationError != null
                        ? Icons.error
                        : Icons.description,
                color: file.isValid
                    ? Colors.green.shade600
                    : file.validationError != null
                        ? Colors.red.shade600
                        : Colors.grey.shade600,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    file.fileName,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          fontWeight: FontWeight.w500,
                        ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  Text(
                    '${_formatFileSize(file.fileSize)} • ${file.validationStatus}',
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.grey.shade600,
                        ),
                  ),
                  if (file.validationError != null)
                    Text(
                      file.validationError!,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: Colors.red.shade600,
                          ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                  if (file.processInfo != null)
                    Text(
                      file.processInfo!,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: Colors.blue.shade600,
                          ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                ],
              ),
            ),
            Column(
              children: [
                IconButton(
                  onPressed: () => _removeFile(file),
                  icon: const Icon(Icons.remove_circle, color: Colors.red),
                  tooltip: 'Удалить файл',
                ),
                if (file.isValid)
                  Icon(
                    Icons.check_circle,
                    color: Colors.green.shade600,
                    size: 16,
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildValidationResult() {
    final validFiles = _uploadedFiles.where((f) => f.isValid).length;
    final totalFiles = _uploadedFiles.length;
    final hasErrors = _uploadedFiles.any((f) => f.validationError != null);

    return Card(
      color: hasErrors ? Colors.orange.shade50 : Colors.green.shade50,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  hasErrors ? Icons.warning : Icons.check_circle,
                  color: hasErrors ? Colors.orange.shade700 : Colors.green.shade700,
                ),
                const SizedBox(width: 12),
                Text(
                  'Результат валидации',
                  style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        color: hasErrors ? Colors.orange.shade700 : Colors.green.shade700,
                      ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Text(
              'Валидных файлов: $validFiles из $totalFiles',
              style: Theme.of(context).textTheme.bodyMedium,
            ),
            if (hasErrors) ...[
              const SizedBox(height: 8),
              Text(
                'Некоторые файлы содержат ошибки и не будут включены в анализ',
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Colors.orange.shade700,
                    ),
              ),
            ],
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
              'BPMN 2.0',
              'Стандартный формат',
              'Business Process Model and Notation',
            ),
            const SizedBox(height: 8),
            _buildFormatInfo(
              'XML BPMN',
              'Стандартный формат',
              'Экспортированный из инструментов моделирования',
            ),
            const SizedBox(height: 16),
            Text(
              'Возможности анализа',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 12),
            _buildCapabilityInfo(
              'Анализ структуры процесса',
              'Проверка корректности потоков и соединений',
              Icons.account_tree,
            ),
            const SizedBox(height: 8),
            _buildCapabilityInfo(
              'Выявление узких мест',
              'Поиск проблем в логике бизнес-процессов',
              Icons.trending_up,
            ),
            const SizedBox(height: 8),
            _buildCapabilityInfo(
              'Проверка соответствия',
              'Соответствие best practices и стандартам',
              Icons.verified,
            ),
            const SizedBox(height: 8),
            _buildCapabilityInfo(
              'Генерация тестов',
              'Создание тест-кейсов на основе процессов',
              Icons.bolt,
            ),
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

  Widget _buildCapabilityInfo(String title, String description, IconData icon) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(
          icon,
          color: Theme.of(context).primaryColor,
          size: 20,
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      fontWeight: FontWeight.w500,
                    ),
              ),
              Text(
                description,
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

  Widget _buildBottomActions() {
    final validFiles = _uploadedFiles.where((f) => f.isValid).toList();
    
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
            onPressed: validFiles.isNotEmpty ? _proceed : null,
            icon: const Icon(Icons.check),
            label: Text('Продолжить (${validFiles.length})'),
          ),
        ],
      ),
    );
  }

  void _onFileSelected(String filePath) async {
    final fileName = filePath.split('/').last;
    final fileSize = await _getFileSize(filePath);
    
    setState(() {
      _uploadedFiles.add(UploadedBpmnFile(
        fileName: fileName,
        filePath: filePath,
        fileSize: fileSize,
        validationStatus: 'Загружен',
        isValid: false,
      ));
    });

    // Auto-validate the file
    _validateFile(_uploadedFiles.last);
  }

  void _onUploadProgress(double progress) {
    // Handle upload progress if needed
  }

  Future<void> _validateFile(UploadedBpmnFile file) async {
    setState(() {
      file.validationStatus = 'Валидация...';
    });

    try {
      // Here you would call the actual validation API
      await Future.delayed(const Duration(seconds: 1)); // Simulate validation
      
      // For now, just set as valid if file exists
      setState(() {
        file.isValid = true;
        file.validationStatus = 'Валиден';
        file.processInfo = 'Обнаружено 1 событие, 3 задачи';
      });
    } catch (e) {
      setState(() {
        file.isValid = false;
        file.validationStatus = 'Ошибка';
        file.validationError = e.toString();
      });
    }
  }

  Future<void> _validateAllFiles() async {
    if (_uploadedFiles.isEmpty) return;

    setState(() {
      _isValidating = true;
    });

    try {
      // Validate all files in parallel
      await Future.wait(
        _uploadedFiles.map((file) => _validateFile(file)),
      );
    } finally {
      setState(() {
        _isValidating = false;
      });
    }
  }

  void _removeFile(UploadedBpmnFile file) {
    setState(() {
      _uploadedFiles.remove(file);
    });
  }

  void _clearAllFiles() {
    setState(() {
      _uploadedFiles.clear();
      _validationResult = null;
    });
  }

  String _formatFileSize(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
  }

  Future<int> _getFileSize(String filePath) async {
    // In a real implementation, you would get the actual file size
    // For now, return a mock size
    return 1024 * 1024; // 1MB
  }

  void _proceed() {
    final validFiles = _uploadedFiles
        .where((f) => f.isValid)
        .map((f) => f.filePath)
        .toList();
    
    Navigator.pop(context, {
      'files': validFiles,
      'description': _descriptionController.text,
    });
  }
}

class UploadedBpmnFile {
  final String fileName;
  final String filePath;
  final int fileSize;
  String validationStatus;
  bool isValid;
  String? validationError;
  String? processInfo;

  UploadedBpmnFile({
    required this.fileName,
    required this.filePath,
    required this.fileSize,
    required this.validationStatus,
    this.isValid = false,
    this.validationError,
    this.processInfo,
  });
}