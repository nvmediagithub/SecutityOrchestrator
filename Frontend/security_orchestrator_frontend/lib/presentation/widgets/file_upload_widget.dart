import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';

class FileUploadWidget extends StatefulWidget {
  final String label;
  final List<String> allowedExtensions;
  final Function(File)? onFileSelected;
  final Function()? onFileRemoved;
  final String? initialFileName;

  const FileUploadWidget({
    super.key,
    required this.label,
    required this.allowedExtensions,
    this.onFileSelected,
    this.onFileRemoved,
    this.initialFileName,
  });

  @override
  State<FileUploadWidget> createState() => _FileUploadWidgetState();
}

class _FileUploadWidgetState extends State<FileUploadWidget> {
  File? _selectedFile;
  String? _fileName;

  @override
  void initState() {
    super.initState();
    _fileName = widget.initialFileName;
  }

  Future<void> _pickFile() async {
    try {
      FilePickerResult? result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: widget.allowedExtensions,
        allowMultiple: false,
      );

      if (result != null && result.files.single.path != null) {
        final file = File(result.files.single.path!);
        setState(() {
          _selectedFile = file;
          _fileName = result.files.single.name;
        });
        widget.onFileSelected?.call(file);
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error selecting file: $e')),
      );
    }
  }

  void _removeFile() {
    setState(() {
      _selectedFile = null;
      _fileName = null;
    });
    widget.onFileRemoved?.call();
  }

  @override
  Widget build(BuildContext context) {
    final hasFile = _fileName != null;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          widget.label,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 8),
        InkWell(
          onTap: hasFile ? null : _pickFile,
          child: Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              border: Border.all(
                color: hasFile ? Colors.green : Colors.grey[300]!,
                width: hasFile ? 2 : 1,
              ),
              borderRadius: BorderRadius.circular(8),
              color: hasFile ? Colors.green[50] : Colors.grey[50],
            ),
            child: Row(
              children: [
                Icon(
                  hasFile ? Icons.insert_drive_file : Icons.cloud_upload,
                  color: hasFile ? Colors.green : Colors.grey[600],
                  size: 24,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        hasFile ? _fileName! : 'Click to select ${widget.allowedExtensions.join(' or ')} file',
                        style: TextStyle(
                          color: hasFile ? Colors.green[800] : Colors.grey[600],
                          fontWeight: hasFile ? FontWeight.w500 : FontWeight.normal,
                        ),
                      ),
                      if (!hasFile)
                        Text(
                          'Allowed formats: ${widget.allowedExtensions.join(', ').toUpperCase()}',
                          style: TextStyle(
                            color: Colors.grey[500],
                            fontSize: 12,
                          ),
                        ),
                      if (hasFile && _selectedFile != null)
                        FutureBuilder<int>(
                          future: _selectedFile!.length(),
                          builder: (context, snapshot) {
                            if (snapshot.hasData) {
                              return Text(
                                '${(snapshot.data! / 1024).round()} KB',
                                style: TextStyle(
                                  color: Colors.grey[600],
                                  fontSize: 12,
                                ),
                              );
                            }
                            return const SizedBox.shrink();
                          },
                        ),
                    ],
                  ),
                ),
                if (hasFile)
                  IconButton(
                    icon: const Icon(Icons.close, color: Colors.red),
                    onPressed: _removeFile,
                    tooltip: 'Remove file',
                  )
                else
                  IconButton(
                    icon: Icon(Icons.add, color: Colors.grey[600]),
                    onPressed: _pickFile,
                    tooltip: 'Select file',
                  ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class MultiFileUploadWidget extends StatefulWidget {
  final String label;
  final List<String> allowedExtensions;
  final Function(List<File>)? onFilesSelected;
  final Function()? onFilesCleared;
  final int maxFiles;

  const MultiFileUploadWidget({
    super.key,
    required this.label,
    required this.allowedExtensions,
    this.onFilesSelected,
    this.onFilesCleared,
    this.maxFiles = 5,
  });

  @override
  State<MultiFileUploadWidget> createState() => _MultiFileUploadWidgetState();
}

class _MultiFileUploadWidgetState extends State<MultiFileUploadWidget> {
  List<File> _selectedFiles = [];

  Future<void> _pickFiles() async {
    try {
      FilePickerResult? result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: widget.allowedExtensions,
        allowMultiple: true,
      );

      if (result != null) {
        final newFiles = result.files
            .where((file) => file.path != null)
            .map((file) => File(file.path!))
            .toList();

        if (_selectedFiles.length + newFiles.length > widget.maxFiles) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Maximum ${widget.maxFiles} files allowed')),
          );
          return;
        }

        setState(() {
          _selectedFiles.addAll(newFiles);
        });
        widget.onFilesSelected?.call(_selectedFiles);
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error selecting files: $e')),
      );
    }
  }

  void _removeFile(int index) {
    setState(() {
      _selectedFiles.removeAt(index);
    });
    widget.onFilesSelected?.call(_selectedFiles);
  }

  void _clearAll() {
    setState(() {
      _selectedFiles.clear();
    });
    widget.onFilesCleared?.call();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              widget.label,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            if (_selectedFiles.isNotEmpty)
              TextButton(
                onPressed: _clearAll,
                child: const Text('Clear All'),
              ),
          ],
        ),
        const SizedBox(height: 8),
        InkWell(
          onTap: _selectedFiles.length < widget.maxFiles ? _pickFiles : null,
          child: Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              border: Border.all(
                color: _selectedFiles.isNotEmpty ? Colors.green : Colors.grey[300]!,
                width: _selectedFiles.isNotEmpty ? 2 : 1,
              ),
              borderRadius: BorderRadius.circular(8),
              color: _selectedFiles.isNotEmpty ? Colors.green[50] : Colors.grey[50],
            ),
            child: Row(
              children: [
                Icon(
                  Icons.cloud_upload,
                  color: _selectedFiles.isNotEmpty ? Colors.green : Colors.grey[600],
                  size: 24,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        _selectedFiles.isEmpty
                            ? 'Click to select ${widget.allowedExtensions.join(' or ')} files'
                            : '${_selectedFiles.length} file(s) selected',
                        style: TextStyle(
                          color: _selectedFiles.isNotEmpty ? Colors.green[800] : Colors.grey[600],
                          fontWeight: _selectedFiles.isNotEmpty ? FontWeight.w500 : FontWeight.normal,
                        ),
                      ),
                      Text(
                        'Allowed formats: ${widget.allowedExtensions.join(', ').toUpperCase()} (max ${widget.maxFiles})',
                        style: TextStyle(
                          color: Colors.grey[500],
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ),
                IconButton(
                  icon: Icon(
                    Icons.add,
                    color: _selectedFiles.length < widget.maxFiles ? Colors.grey[600] : Colors.grey[300],
                  ),
                  onPressed: _selectedFiles.length < widget.maxFiles ? _pickFiles : null,
                  tooltip: 'Add files',
                ),
              ],
            ),
          ),
        ),
        if (_selectedFiles.isNotEmpty) ...[
          const SizedBox(height: 12),
          ListView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: _selectedFiles.length,
            itemBuilder: (context, index) {
              final file = _selectedFiles[index];
              return Container(
                margin: const EdgeInsets.only(bottom: 4),
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.grey[300]!),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.insert_drive_file, size: 16),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        file.path.split(Platform.pathSeparator).last,
                        style: const TextStyle(fontSize: 12),
                      ),
                    ),
                    IconButton(
                      icon: const Icon(Icons.close, size: 16),
                      onPressed: () => _removeFile(index),
                      tooltip: 'Remove file',
                    ),
                  ],
                ),
              );
            },
          ),
        ],
      ],
    );
  }
}