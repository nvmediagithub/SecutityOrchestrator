import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/workflow_dto.dart';
import '../../data/models/process_dto.dart';

class WorkflowCreationScreen extends ConsumerStatefulWidget {
  final String? processId;

  const WorkflowCreationScreen({super.key, this.processId});

  @override
  ConsumerState<WorkflowCreationScreen> createState() => _WorkflowCreationScreenState();
}

class _WorkflowCreationScreenState extends ConsumerState<WorkflowCreationScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _descriptionController = TextEditingController();
  String? _selectedProcessId;
  bool _parallelExecution = false;
  int _maxRetries = 3;
  int _timeoutSeconds = 300;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _selectedProcessId = widget.processId;
  }

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
        title: const Text('Create Workflow'),
        actions: [
          TextButton(
            onPressed: _isLoading ? null : _saveWorkflow,
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
              _buildProcessSelectionSection(),
              const SizedBox(height: 24),
              _buildExecutionConfigSection(),
              const SizedBox(height: 24),
              _buildTestCasesSection(),
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
                labelText: 'Workflow Name',
                hintText: 'Enter workflow name',
                border: OutlineInputBorder(),
              ),
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please enter a workflow name';
                }
                return null;
              },
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: const InputDecoration(
                labelText: 'Description (Optional)',
                hintText: 'Enter workflow description',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProcessSelectionSection() {
    // Mock process list - in real app would come from provider
    final mockProcesses = [
      ProcessSummaryDto(
        id: 'process1',
        name: 'Security Validation Process',
        status: ProcessStatus.active,
        createdAt: DateTime.now(),
        elementCount: 5,
      ),
      ProcessSummaryDto(
        id: 'process2',
        name: 'API Testing Workflow',
        status: ProcessStatus.active,
        createdAt: DateTime.now(),
        elementCount: 8,
      ),
    ];

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Select Process',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _selectedProcessId,
              decoration: const InputDecoration(
                labelText: 'Process',
                border: OutlineInputBorder(),
              ),
              items: mockProcesses.map((process) {
                return DropdownMenuItem(
                  value: process.id,
                  child: Text('${process.name} (${process.elementCount} elements)'),
                );
              }).toList(),
              onChanged: (value) {
                setState(() {
                  _selectedProcessId = value;
                });
              },
              validator: (value) {
                if (value == null || value.isEmpty) {
                  return 'Please select a process';
                }
                return null;
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildExecutionConfigSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Execution Configuration',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            SwitchListTile(
              title: const Text('Parallel Execution'),
              subtitle: const Text('Execute tasks in parallel when possible'),
              value: _parallelExecution,
              onChanged: (value) {
                setState(() {
                  _parallelExecution = value;
                });
              },
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: TextFormField(
                    initialValue: _maxRetries.toString(),
                    decoration: const InputDecoration(
                      labelText: 'Max Retries',
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.number,
                    onChanged: (value) {
                      _maxRetries = int.tryParse(value) ?? 3;
                    },
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Required';
                      }
                      final num = int.tryParse(value);
                      if (num == null || num < 0) {
                        return 'Invalid number';
                      }
                      return null;
                    },
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: TextFormField(
                    initialValue: _timeoutSeconds.toString(),
                    decoration: const InputDecoration(
                      labelText: 'Timeout (seconds)',
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.number,
                    onChanged: (value) {
                      _timeoutSeconds = int.tryParse(value) ?? 300;
                    },
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return 'Required';
                      }
                      final num = int.tryParse(value);
                      if (num == null || num <= 0) {
                        return 'Invalid timeout';
                      }
                      return null;
                    },
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTestCasesSection() {
    // Mock test cases - in real app would come from provider
    final mockTestCases = [
      'Input Validation Test',
      'Authentication Test',
      'Authorization Test',
      'Data Sanitization Test',
      'Output Validation Test',
    ];

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Test Cases',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            const Text(
              'Select test cases to include in this workflow:',
              style: TextStyle(color: Colors.grey),
            ),
            const SizedBox(height: 16),
            ...mockTestCases.map((testCase) => CheckboxListTile(
              title: Text(testCase),
              value: true, // All selected by default for now
              onChanged: (value) {
                // Handle selection
              },
            )),
          ],
        ),
      ),
    );
  }

  Future<void> _saveWorkflow() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    if (_selectedProcessId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a process')),
      );
      return;
    }

    setState(() {
      _isLoading = true;
    });

    try {
      // Mock workflow creation
      final workflow = WorkflowDto(
        id: 'wf_${DateTime.now().millisecondsSinceEpoch}',
        name: _nameController.text,
        description: _descriptionController.text.isEmpty ? null : _descriptionController.text,
        status: WorkflowStatus.draft,
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
        processId: _selectedProcessId!,
        testCaseIds: ['tc1', 'tc2', 'tc3'], // Mock test case IDs
        executionConfig: ExecutionConfigurationDto(
          parallelExecution: _parallelExecution,
          maxRetries: _maxRetries,
          timeoutSeconds: _timeoutSeconds,
          variables: {},
        ),
      );

      // Simulate API call delay
      await Future.delayed(const Duration(seconds: 2));

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Workflow created successfully')),
        );
        Navigator.of(context).pop(workflow);
      }
    } catch (error) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error creating workflow: $error')),
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