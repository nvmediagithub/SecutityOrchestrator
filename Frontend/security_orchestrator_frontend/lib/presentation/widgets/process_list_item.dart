import 'package:flutter/material.dart';
import '../../data/models/process_dto.dart';
import '../screens/process_details_screen.dart';
import '../screens/workflow_creation_screen.dart';
import '../screens/execution_monitoring_screen.dart';

class ProcessListItem extends StatelessWidget {
  final ProcessSummaryDto process;

  const ProcessListItem({
    super.key,
    required this.process,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: _getStatusColor(process.status),
          child: Icon(
            _getStatusIcon(process.status),
            color: Colors.white,
          ),
        ),
        title: Text(
          process.name,
          style: Theme.of(context).textTheme.titleMedium,
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Created: ${_formatDate(process.createdAt)}',
              style: Theme.of(context).textTheme.bodySmall,
            ),
            Text(
              'Elements: ${process.elementCount}',
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ],
        ),
        trailing: PopupMenuButton<String>(
          onSelected: (value) => _handleMenuAction(context, value),
          itemBuilder: (context) => [
            const PopupMenuItem(
              value: 'details',
              child: ListTile(
                leading: Icon(Icons.info),
                title: Text('View Details'),
                contentPadding: EdgeInsets.zero,
              ),
            ),
            const PopupMenuItem(
              value: 'workflow',
              child: ListTile(
                leading: Icon(Icons.work),
                title: Text('Create Workflow'),
                contentPadding: EdgeInsets.zero,
              ),
            ),
            const PopupMenuItem(
              value: 'execute',
              child: ListTile(
                leading: Icon(Icons.play_arrow),
                title: Text('Execute Process'),
                contentPadding: EdgeInsets.zero,
              ),
            ),
            const PopupMenuItem(
              value: 'history',
              child: ListTile(
                leading: Icon(Icons.history),
                title: Text('Execution History'),
                contentPadding: EdgeInsets.zero,
              ),
            ),
          ],
        ),
        onTap: () => _navigateToDetails(context),
      ),
    );
  }

  void _handleMenuAction(BuildContext context, String action) {
    switch (action) {
      case 'details':
        _navigateToDetails(context);
        break;
      case 'workflow':
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (context) => WorkflowCreationScreen(processId: process.id),
          ),
        );
        break;
      case 'execute':
        // Mock execution ID for demonstration
        final executionId = 'exec_${process.id}_${DateTime.now().millisecondsSinceEpoch}';
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (context) => ExecutionMonitoringScreen(
              workflowId: 'wf_${process.id}',
              executionId: executionId,
            ),
          ),
        );
        break;
      case 'history':
        _showExecutionHistory(context);
        break;
    }
  }

  void _navigateToDetails(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => ProcessDetailsScreen(processId: process.id),
      ),
    );
  }

  void _showExecutionHistory(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Execution History',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            // Mock execution history items
            _buildHistoryItem(
              'Execution #1234',
              'Completed',
              '2 hours ago',
              () {
                Navigator.of(context).pop();
                // Navigate to results
              },
            ),
            _buildHistoryItem(
              'Execution #1233',
              'Failed',
              '1 day ago',
              () {
                Navigator.of(context).pop();
                // Navigate to results
              },
            ),
            _buildHistoryItem(
              'Execution #1232',
              'Completed',
              '2 days ago',
              () {
                Navigator.of(context).pop();
                // Navigate to results
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHistoryItem(String title, String status, String time, VoidCallback onTap) {
    return ListTile(
      leading: CircleAvatar(
        radius: 16,
        backgroundColor: status == 'Completed' ? Colors.green : Colors.red,
        child: Icon(
          status == 'Completed' ? Icons.check : Icons.error,
          color: Colors.white,
          size: 16,
        ),
      ),
      title: Text(title),
      subtitle: Text('$status • $time'),
      trailing: const Icon(Icons.chevron_right),
      onTap: onTap,
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

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}