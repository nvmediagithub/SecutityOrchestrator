import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../widgets/file_upload_widget.dart';

class ProcessCreationScreen extends ConsumerStatefulWidget {
  const ProcessCreationScreen({super.key});

  @override
  ConsumerState<ProcessCreationScreen> createState() => _ProcessCreationScreenState();
}

class _ProcessCreationScreenState extends ConsumerState<ProcessCreationScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _descriptionController = TextEditingController();
  File? _bpmnFile;
  List<File> _openapiFiles = [];
  bool _isLoading = false;
  bool _autoGenerateFromBpmn = true;

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Create Process'),
        actions: [
          TextButton(
            onPressed: _isLoading ? null : _saveProcess,
            child: _isLoading
                ? const SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : const Text('Save'),
          ),
        ],
      ),
      body: Form(
        key: _formKey,
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildBasicInfoSection(),
              const SizedBox(height: 24),
              _buildBpmnUploadSection(),
              const SizedBox(height: 24),
              _buildOpenApiUploadSection(),
              const SizedBox(height: 24),
              _buildAdvancedOptions(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildBasicInfoSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Basic Information',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _nameController,
              decoration: const InputDecoration(
                labelText: 'Process Name',
                hintText: 'Enter process name',
                border: OutlineInputBorder(),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter a process name';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: const InputDecoration(
                labelText: 'Description (Optional)',
                hintText: 'Enter process description',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBpmnUploadSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'BPMN Model',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            const Text(
              'Upload a BPMN 2.0 XML file to define your process flow.',
              style: TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 16),
            FileUploadWidget(
              label: 'BPMN File',
              allowedExtensions: ['bpmn', 'xml'],
              onFileSelected: (file) {
                setState(() {
                  _bpmnFile = file;
                });
              },
              onFileRemoved: () {
                setState(() {
                  _bpmnFile = null;
                });
              },
            ),
            const SizedBox(height: 16),
            if (_bpmnFile != null)
              SwitchListTile(
                title: const Text('Auto-generate process elements'),
                subtitle: const Text('Automatically extract tasks, gateways, and events from BPMN'),
                value: _autoGenerateFromBpmn,
                onChanged: (value) {
                  setState(() {
                    _autoGenerateFromBpmn = value;
                  });
                },
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildOpenApiUploadSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'API Specifications (Optional)',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            const Text(
              'Upload OpenAPI specifications to integrate external APIs into your process.',
              style: TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 16),
            MultiFileUploadWidget(
              label: 'OpenAPI Files',
              allowedExtensions: ['json', 'yaml', 'yml'],
              maxFiles: 10,
              onFilesSelected: (files) {
                setState(() {
                  _openapiFiles = files;
                });
              },
              onFilesCleared: () {
                setState(() {
                  _openapiFiles.clear();
                });
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAdvancedOptions() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Advanced Options',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            const Text(
              'Configure additional process settings:',
              style: TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 16),
            SwitchListTile(
              title: const Text('Enable versioning'),
              subtitle: const Text('Track changes and maintain version history'),
              value: true,
              onChanged: (value) {
                // Handle versioning toggle
              },
            ),
            SwitchListTile(
              title: const Text('Require approval'),
              subtitle: const Text('Require approval before process can be executed'),
              value: false,
              onChanged: (value) {
                // Handle approval toggle
              },
            ),
            SwitchListTile(
              title: const Text('Enable monitoring'),
              subtitle: const Text('Collect execution metrics and logs'),
              value: true,
              onChanged: (value) {
                // Handle monitoring toggle
              },
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _saveProcess() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    if (_bpmnFile == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please upload a BPMN file')),
      );
      return;
    }

    setState(() {
      _isLoading = true;
    });

    try {
      // Mock process creation
      await Future.delayed(const Duration(seconds: 3));

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Process created successfully')),
        );
        Navigator.of(context).pop();
      }
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error creating process: $error')),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }
}