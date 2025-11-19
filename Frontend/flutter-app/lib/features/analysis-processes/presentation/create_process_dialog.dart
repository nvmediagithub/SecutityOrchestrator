import 'package:flutter/material.dart';

import '../domain/analysis_process.dart';
import '../domain/create_analysis_process_usecase.dart';

class CreateProcessDialog extends StatefulWidget {
  final CreateAnalysisProcessUseCase createProcessUseCase;

  const CreateProcessDialog({super.key, required this.createProcessUseCase});

  @override
  State<CreateProcessDialog> createState() => _CreateProcessDialogState();
}

class _CreateProcessDialogState extends State<CreateProcessDialog> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();
  ProcessType _selectedType = ProcessType.securityAnalysis; // default
  bool _isLoading = false;

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final process = await widget.createProcessUseCase.execute(
        name: _nameController.text.trim(),
        description: _descriptionController.text.trim(),
        type: _selectedType,
      );
      Navigator.of(context).pop(process);
    } catch (e) {
      // Show error message
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('Failed to create process: $e')));
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('Create Analysis Process'),
      content: SingleChildScrollView(
        child: Form(
          key: _formKey,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(
                  labelText: 'Process Name',
                  hintText: 'Enter process name',
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Process name is required';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _descriptionController,
                decoration: const InputDecoration(
                  labelText: 'Description',
                  hintText: 'Enter process description',
                ),
                maxLines: 3,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Description is required';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              DropdownButtonFormField<ProcessType>(
                value: _selectedType,
                decoration: const InputDecoration(labelText: 'Process Type'),
                items: ProcessType.values.map((type) {
                  return DropdownMenuItem(
                    value: type,
                    child: Text(type.displayName),
                  );
                }).toList(),
                onChanged: (value) {
                  if (value != null) {
                    setState(() => _selectedType = value);
                  }
                },
                validator: (value) {
                  if (value == null) return 'Please select a process type';
                  return null;
                },
              ),
            ],
          ),
        ),
      ),
      actions: [
        TextButton(
          onPressed: _isLoading ? null : () => Navigator.of(context).pop(),
          child: const Text('Cancel'),
        ),
        ElevatedButton(
          onPressed: _isLoading ? null : _submit,
          child: _isLoading
              ? const SizedBox(
                  width: 20,
                  height: 20,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              : const Text('Create'),
        ),
      ],
    );
  }
}
