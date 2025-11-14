import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../providers/user_flow_provider.dart';

class FileUploadWidget extends ConsumerStatefulWidget {
  const FileUploadWidget({super.key});

  @override
  ConsumerState<FileUploadWidget> createState() => _FileUploadWidgetState();
}

class _FileUploadWidgetState extends ConsumerState<FileUploadWidget> {
  bool _isDragging = false;

  @override
  Widget build(BuildContext context) {
    final userFlowState = ref.watch(userFlowProvider);

    return Card(
      child: Container(
        height: 200,
        decoration: BoxDecoration(
          border: Border.all(
            color: _isDragging
                ? Theme.of(context).colorScheme.primary
                : Colors.grey[300]!,
            width: _isDragging ? 2 : 1,
          ),
          borderRadius: BorderRadius.circular(8),
          color: _isDragging
              ? Theme.of(context).colorScheme.primaryContainer.withOpacity(0.1)
              : Colors.grey[50],
        ),
        child: InkWell(
          onTap: _pickFile,
          child: DragTarget<String>(
            onWillAccept: (data) => true,
            onAccept: (data) => _handleFileDrop(data),
            onEnter: (details) => setState(() => _isDragging = true),
            onExit: (details) => setState(() => _isDragging = false),
            builder: (context, candidateData, rejectedData) {
              return Center(
                child: userFlowState.hasUploadedFile
                    ? _buildUploadedFileView(userFlowState.uploadedFileName!)
                    : _buildUploadPrompt(),
              );
            },
          ),
        ),
      ),
    );
  }

  Widget _buildUploadPrompt() {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(
          _isDragging ? Icons.file_download : Icons.cloud_upload,
          size: 48,
          color: _isDragging
              ? Theme.of(context).colorScheme.primary
              : Colors.grey[400],
        ),
        const SizedBox(height: 16),
        Text(
          _isDragging ? 'Drop file here' : 'Click to select or drag & drop',
          style: TextStyle(
            fontSize: 16,
            color: _isDragging
                ? Theme.of(context).colorScheme.primary
                : Colors.grey[600],
            fontWeight: _isDragging ? FontWeight.w600 : FontWeight.normal,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          'Supported formats: DOCX, TXT, PDF',
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[500],
          ),
        ),
      ],
    );
  }

  Widget _buildUploadedFileView(String fileName) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(
          Icons.insert_drive_file,
          size: 48,
          color: Theme.of(context).colorScheme.primary,
        ),
        const SizedBox(height: 16),
        Text(
          'File Uploaded',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.w600,
            color: Theme.of(context).colorScheme.primary,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          fileName,
          style: TextStyle(
            fontSize: 14,
            color: Colors.grey[600],
          ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 16),
        TextButton.icon(
          onPressed: _pickFile,
          icon: const Icon(Icons.refresh),
          label: const Text('Change File'),
        ),
      ],
    );
  }

  Future<void> _pickFile() async {
    try {
      final result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['docx', 'txt', 'pdf'],
        allowMultiple: false,
      );

      if (result != null && result.files.isNotEmpty) {
        final file = result.files.first;
        final content = file.bytes != null
            ? String.fromCharCodes(file.bytes!)
            : 'File content not available';

        await ref.read(userFlowProvider.notifier).uploadFile(
          content,
          file.name,
          _getMimeType(file.extension ?? ''),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error picking file: $e')),
      );
    }
  }

  void _handleFileDrop(String data) {
    setState(() => _isDragging = false);
    // Handle file drop if needed
    // For now, we'll let the user pick files manually
  }

  String _getMimeType(String extension) {
    switch (extension.toLowerCase()) {
      case 'docx':
        return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
      case 'txt':
        return 'text/plain';
      case 'pdf':
        return 'application/pdf';
      default:
        return 'application/octet-stream';
    }
  }
}