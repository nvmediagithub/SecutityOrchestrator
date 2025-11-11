import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/user_flow_models.dart';
import '../../data/services/user_flow_api_service.dart';
import '../providers/user_flow_provider.dart';
import '../widgets/file_upload_widget.dart';
import '../widgets/progress_indicator_widget.dart';
import 'user_flow_results_screen.dart';

class UserFlowMainScreen extends ConsumerStatefulWidget {
  const UserFlowMainScreen({super.key});

  @override
  ConsumerState<UserFlowMainScreen> createState() => _UserFlowMainScreenState();
}

class _UserFlowMainScreenState extends ConsumerState<UserFlowMainScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  final List<UploadedFile> _uploadedFiles = [];
  bool _isUploading = false;
  bool _isAnalyzing = false;

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _onFilesSelected(List<File> files) async {
    setState(() {
      _isUploading = true;
    });

    try {
      final apiService = UserFlowApiService();
      
      for (final file in files) {
        final uploadedFile = await apiService.uploadFile(file);
        setState(() {
          _uploadedFiles.add(uploadedFile);
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка загрузки файлов: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isUploading = false;
        });
      }
    }
  }

  void _removeFile(UploadedFile file) {
    setState(() {
      _uploadedFiles.removeWhere((f) => f.id == file.id);
    });
  }

  Future<void> _startAnalysis() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() {
      _isAnalyzing = true;
    });

    try {
      final apiService = UserFlowApiService();
      
      final request = AnalysisRequest(
        title: _titleController.text.trim(),
        description: _descriptionController.text.trim(),
        fileIds: _uploadedFiles.map((f) => f.id).toList(),
      );

      final response = await apiService.startAnalysis(request);

      if (mounted) {
        // Navigate to results screen with analysis ID
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (context) => UserFlowResultsScreen(
              analysisId: response.analysisId,
              initialTask: UserTask(
                id: response.analysisId,
                title: request.title,
                description: request.description,
                attachments: _uploadedFiles,
                createdAt: DateTime.now(),
                status: TaskStatus.analyzing,
              ),
            ),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка запуска анализа: $e'),
            backgroundColor: Colors.red,
          ),
        );
        setState(() {
          _isAnalyzing = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Security Orchestrator - Анализ'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header
            Card(
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Анализ безопасности',
                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Опишите вашу задачу или проблему, загрузите необходимые документы и запустите анализ безопасности.',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: Colors.grey[600],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),

            // Main form
            Card(
              child: Padding(
                padding: const EdgeInsets.all(20),
                child: Form(
                  key: _formKey,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Описание задачи',
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 16),

                      // Title field
                      TextFormField(
                        controller: _titleController,
                        decoration: const InputDecoration(
                          labelText: 'Название задачи',
                          hintText: 'Например: Анализ безопасности веб-приложения',
                          border: OutlineInputBorder(),
                        ),
                        validator: (value) {
                          if (value == null || value.trim().isEmpty) {
                            return 'Пожалуйста, введите название задачи';
                          }
                          if (value.trim().length < 3) {
                            return 'Название должно содержать минимум 3 символа';
                          }
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),

                      // Description field
                      TextFormField(
                        controller: _descriptionController,
                        decoration: const InputDecoration(
                          labelText: 'Описание задачи',
                          hintText: 'Подробно опишите вашу задачу, проблему или требования к анализу...',
                          border: OutlineInputBorder(),
                          alignLabelWithHint: true,
                        ),
                        maxLines: 5,
                        validator: (value) {
                          if (value == null || value.trim().isEmpty) {
                            return 'Пожалуйста, введите описание задачи';
                          }
                          if (value.trim().length < 10) {
                            return 'Описание должно содержать минимум 10 символов';
                          }
                          return null;
                        },
                      ),
                      const SizedBox(height: 24),

                      // File upload section
                      Text(
                        'Документы (необязательно)',
                        style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Поддерживаемые форматы: DOCX, TXT, PDF',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.grey[600],
                        ),
                      ),
                      const SizedBox(height: 16),

                      // File upload widget
                      FileUploadWidget(
                        onFilesSelected: _onFilesSelected,
                        isUploading: _isUploading,
                      ),
                      const SizedBox(height: 16),

                      // Uploaded files list
                      if (_uploadedFiles.isNotEmpty) ...[
                        Text(
                          'Загруженные файлы:',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        const SizedBox(height: 8),
                        ListView.separated(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemCount: _uploadedFiles.length,
                          separatorBuilder: (context, index) => const Divider(),
                          itemBuilder: (context, index) {
                            final file = _uploadedFiles[index];
                            return ListTile(
                              leading: Icon(
                                _getFileIcon(file.type),
                                color: Colors.blue,
                              ),
                              title: Text(file.name),
                              subtitle: Text('${_formatFileSize(file.size)} • ${file.type.toUpperCase()}'),
                              trailing: IconButton(
                                icon: const Icon(Icons.delete, color: Colors.red),
                                onPressed: () => _removeFile(file),
                              ),
                            );
                          },
                        ),
                        const SizedBox(height: 24),
                      ],

                      // Start analysis button
                      SizedBox(
                        width: double.infinity,
                        height: 48,
                        child: ElevatedButton.icon(
                          onPressed: _isAnalyzing || _isUploading ? null : _startAnalysis,
                          icon: _isAnalyzing
                              ? const SizedBox(
                                  width: 20,
                                  height: 20,
                                  child: CircularProgressIndicator(strokeWidth: 2),
                                )
                              : const Icon(Icons.play_arrow),
                          label: Text(_isAnalyzing ? 'Анализ запущен...' : 'Старт анализа'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Theme.of(context).colorScheme.primary,
                            foregroundColor: Theme.of(context).colorScheme.onPrimary,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  IconData _getFileIcon(String type) {
    switch (type.toLowerCase()) {
      case 'pdf':
        return Icons.picture_as_pdf;
      case 'docx':
      case 'doc':
        return Icons.description;
      case 'txt':
        return Icons.text_snippet;
      default:
        return Icons.insert_drive_file;
    }
  }

  String _formatFileSize(int bytes) {
    if (bytes < 1024) {
      return '$bytes B';
    } else if (bytes < 1024 * 1024) {
      return '${(bytes / 1024).toStringAsFixed(1)} KB';
    } else {
      return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
    }
  }
}