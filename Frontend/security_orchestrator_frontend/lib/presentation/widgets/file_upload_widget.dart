import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';

class FileUploadWidget extends StatefulWidget {
  final Function(List<File> files) onFilesSelected;
  final bool isUploading;

  const FileUploadWidget({
    super.key,
    required this.onFilesSelected,
    this.isUploading = false,
  });

  @override
  State<FileUploadWidget> createState() => _FileUploadWidgetState();
}

class _FileUploadWidgetState extends State<FileUploadWidget> {
  bool _isDragOver = false;
  final List<String> _supportedFormats = ['docx', 'txt', 'pdf'];

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.isUploading ? null : _pickFiles,
      child: Container(
        width: double.infinity,
        height: 120,
        decoration: BoxDecoration(
          border: Border.all(
            color: _isDragOver 
                ? Theme.of(context).colorScheme.primary
                : Colors.grey.shade300,
            width: 2,
            style: BorderStyle.solid,
          ),
          borderRadius: BorderRadius.circular(12),
          color: _isDragOver
              ? Theme.of(context).colorScheme.primary.withOpacity(0.1)
              : Colors.grey.shade50,
        ),
        child: Stack(
          children: [
            // Drag target overlay
            if (_isDragOver)
              Positioned.fill(
                child: Container(
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.primary.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.cloud_upload,
                          size: 48,
                          color: Theme.of(context).colorScheme.primary,
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Отпустите файлы для загрузки',
                          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            color: Theme.of(context).colorScheme.primary,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),

            // Main content
            Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  if (widget.isUploading) ...[
                    const SizedBox(
                      width: 32,
                      height: 32,
                      child: CircularProgressIndicator(strokeWidth: 3),
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'Загрузка файлов...',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: Colors.grey[600],
                      ),
                    ),
                  ] else ...[
                    Icon(
                      Icons.cloud_upload_outlined,
                      size: 48,
                      color: Colors.grey[400],
                    ),
                    const SizedBox(height: 12),
                    Text(
                      'Нажмите для выбора файлов\nили перетащите сюда',
                      textAlign: TextAlign.center,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: Colors.grey[600],
                      ),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Форматы: ${_supportedFormats.join(", ").toUpperCase()}',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey[500],
                        fontSize: 12,
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _pickFiles() async {
    try {
      final result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: _supportedFormats,
        allowMultiple: true,
        dialogTitle: 'Выберите файлы для загрузки',
      );

      if (result != null && result.files.isNotEmpty) {
        final files = result.files
            .where((file) => 
                file.extension != null && 
                _supportedFormats.contains(file.extension!.toLowerCase()))
            .map((file) => File(file.path!))
            .toList();

        if (files.isNotEmpty) {
          widget.onFilesSelected(files);
        }
      }
    } catch (e) {
      debugPrint('Error picking files: $e');
    }
  }
}