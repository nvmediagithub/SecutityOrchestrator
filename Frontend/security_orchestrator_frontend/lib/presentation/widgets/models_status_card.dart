import 'package:flutter/material.dart';
import '../../data/models/monitoring_models.dart';

class ModelsStatusCard extends StatelessWidget {
  final List<Map<String, dynamic>> activeModels;

  const ModelsStatusCard({
    super.key,
    required this.activeModels,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  Icons.model_training,
                  color: Colors.purple,
                ),
                const SizedBox(width: 8),
                Text(
                  'AI Models Status',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const Spacer(),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 8,
                    vertical: 4,
                  ),
                  decoration: BoxDecoration(
                    color: Colors.purple.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    '${activeModels.length} models',
                    style: TextStyle(
                      color: Colors.purple,
                      fontWeight: FontWeight.bold,
                      fontSize: 12,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            if (activeModels.isEmpty) ...[
              Center(
                child: Column(
                  children: [
                    Icon(
                      Icons.model_training,
                      color: Colors.grey,
                      size: 48,
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'No Models Loaded',
                      style: Theme.of(context).textTheme.titleSmall,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'No AI models are currently active',
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey[600],
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
            ] else ...[
              Expanded(
                child: ListView.separated(
                  itemCount: activeModels.length,
                  separatorBuilder: (context, index) => const Divider(),
                  itemBuilder: (context, index) {
                    final model = ModelInfo.fromJson(activeModels[index]);
                    return _buildModelItem(context, model);
                  },
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildModelItem(BuildContext context, ModelInfo model) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        border: Border.all(
          color: _getStatusColor(model.status).withOpacity(0.3),
          width: 1,
        ),
        borderRadius: BorderRadius.circular(8),
        color: _getStatusColor(model.status).withOpacity(0.05),
      ),
      child: Row(
        children: [
          Container(
            width: 12,
            height: 12,
            decoration: BoxDecoration(
              color: _getStatusColor(model.status),
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  model.modelName,
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 2),
                Row(
                  children: [
                    Icon(
                      _getProviderIcon(model.provider),
                      size: 14,
                      color: Colors.grey[600],
                    ),
                    const SizedBox(width: 4),
                    Text(
                      model.provider,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey[600],
                      ),
                    ),
                    if (model.sizeGB != null) ...[
                      const SizedBox(width: 12),
                      Icon(
                        Icons.storage,
                        size: 14,
                        color: Colors.grey[600],
                      ),
                      const SizedBox(width: 4),
                      Text(
                        '${model.sizeGB!.toStringAsFixed(1)}GB',
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ],
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    _buildStatusChip(context, model.status),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatusChip(BuildContext context, String status) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: 8,
        vertical: 2,
      ),
      decoration: BoxDecoration(
        color: _getStatusColor(status).withOpacity(0.2),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: _getStatusColor(status),
          width: 1,
        ),
      ),
      child: Text(
        status.toUpperCase(),
        style: TextStyle(
          color: _getStatusColor(status),
          fontSize: 10,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  IconData _getProviderIcon(String provider) {
    switch (provider.toUpperCase()) {
      case 'LOCAL':
        return Icons.computer;
      case 'OPENROUTER':
        return Icons.cloud;
      case 'ONNX':
        return Icons.memory;
      default:
        return Icons.smart_toy;
    }
  }

  Color _getStatusColor(String status) {
    switch (status.toUpperCase()) {
      case 'LOADED':
        return Colors.green;
      case 'LOADING':
        return Colors.orange;
      case 'AVAILABLE':
        return Colors.blue;
      case 'UNLOADED':
        return Colors.grey;
      case 'ERROR':
        return Colors.red;
      default:
        return Colors.blue;
    }
  }
}