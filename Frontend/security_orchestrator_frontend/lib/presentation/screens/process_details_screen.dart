import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/process_dto.dart';
import '../../shared/theme/app_theme.dart';

class ProcessDetailsScreen extends ConsumerWidget {
  final String processId;

  const ProcessDetailsScreen({super.key, required this.processId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // For now, use mock data since we don't have a full provider yet
    final mockProcess = ProcessDto(
      id: processId,
      name: 'Sample Security Process',
      description: 'A comprehensive security validation workflow',
      status: ProcessStatus.active,
      createdAt: DateTime.now().subtract(const Duration(days: 5)),
      updatedAt: DateTime.now(),
      bpmnXml: '<?xml version="1.0" encoding="UTF-8"?><bpmn:definitions></bpmn:definitions>',
      elements: [
        FlowElementDto(
          id: 'start',
          name: 'Start Event',
          type: ElementType.startEvent,
          properties: {},
        ),
        FlowElementDto(
          id: 'task1',
          name: 'Validate Input',
          type: ElementType.task,
          properties: {'taskType': 'validation'},
        ),
        FlowElementDto(
          id: 'gateway',
          name: 'Decision Point',
          type: ElementType.gateway,
          properties: {},
        ),
        FlowElementDto(
          id: 'end',
          name: 'End Event',
          type: ElementType.endEvent,
          properties: {},
        ),
      ],
    );

    return Scaffold(
      appBar: AppBar(
        title: const Text('Process Details'),
        actions: [
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () {
              // Navigate to edit process
            },
          ),
          IconButton(
            icon: const Icon(Icons.play_arrow),
            onPressed: () {
              // Execute process
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildProcessHeader(mockProcess),
            const SizedBox(height: 24),
            _buildProcessElements(mockProcess.elements),
            const SizedBox(height: 24),
            _buildActions(context),
          ],
        ),
      ),
    );
  }

  Widget _buildProcessHeader(ProcessDto process) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  backgroundColor: _getStatusColor(process.status),
                  child: Icon(
                    _getStatusIcon(process.status),
                    color: Colors.white,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        process.name,
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      if (process.description != null)
                        Text(
                          process.description!,
                          style: TextStyle(color: Colors.grey[600]),
                        ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                _buildInfoChip('Status', process.status.name.toUpperCase()),
                const SizedBox(width: 8),
                _buildInfoChip('Elements', process.elements.length.toString()),
                const SizedBox(width: 8),
                _buildInfoChip('Created', _formatDate(process.createdAt)),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProcessElements(List<FlowElementDto> elements) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Process Elements',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: elements.length,
              itemBuilder: (context, index) {
                final element = elements[index];
                return ListTile(
                  leading: CircleAvatar(
                    backgroundColor: _getElementTypeColor(element.type),
                    child: Icon(
                      _getElementTypeIcon(element.type),
                      color: Colors.white,
                      size: 20,
                    ),
                  ),
                  title: Text(element.name),
                  subtitle: Text(element.type.name.replaceAll('Event', ' Event').toUpperCase()),
                  trailing: IconButton(
                    icon: const Icon(Icons.info_outline),
                    onPressed: () {
                      // Show element details
                    },
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActions(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Actions',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    icon: const Icon(Icons.play_arrow),
                    label: const Text('Execute Process'),
                    onPressed: () {
                      // Navigate to execution monitoring
                    },
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: OutlinedButton.icon(
                    icon: const Icon(Icons.work),
                    label: const Text('Create Workflow'),
                    onPressed: () {
                      // Navigate to workflow creation
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton.icon(
                    icon: const Icon(Icons.history),
                    label: const Text('View History'),
                    onPressed: () {
                      // Navigate to execution history
                    },
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: OutlinedButton.icon(
                    icon: const Icon(Icons.file_download),
                    label: const Text('Export BPMN'),
                    onPressed: () {
                      // Export BPMN file
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

  Widget _buildInfoChip(String label, String value) {
    return Chip(
      label: Text('$label: $value'),
      backgroundColor: Colors.grey[100],
    );
  }

  Color _getStatusColor(ProcessStatus status) {
    switch (status) {
      case ProcessStatus.active:
        return Colors.green;
      case ProcessStatus.inactive:
        return Colors.orange;
      case ProcessStatus.archived:
        return Colors.grey;
    }
  }

  IconData _getStatusIcon(ProcessStatus status) {
    switch (status) {
      case ProcessStatus.active:
        return Icons.play_arrow;
      case ProcessStatus.inactive:
        return Icons.pause;
      case ProcessStatus.archived:
        return Icons.archive;
    }
  }

  Color _getElementTypeColor(ElementType type) {
    switch (type) {
      case ElementType.startEvent:
        return Colors.green;
      case ElementType.endEvent:
        return Colors.red;
      case ElementType.task:
        return Colors.blue;
      case ElementType.gateway:
        return Colors.purple;
      case ElementType.sequenceFlow:
        return Colors.grey;
    }
  }

  IconData _getElementTypeIcon(ElementType type) {
    switch (type) {
      case ElementType.startEvent:
        return Icons.play_circle;
      case ElementType.endEvent:
        return Icons.stop_circle;
      case ElementType.task:
        return Icons.task;
      case ElementType.gateway:
        return Icons.call_split;
      case ElementType.sequenceFlow:
        return Icons.arrow_forward;
    }
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}