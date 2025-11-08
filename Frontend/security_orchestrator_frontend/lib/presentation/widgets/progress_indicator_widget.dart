import 'package:flutter/material.dart';
import '../../data/models/execution_monitoring_dto.dart';

class ExecutionProgressWidget extends StatelessWidget {
  final ExecutionProgressDto progress;
  final double width;
  final double height;
  final bool showPercentage;
  final bool showTimeRemaining;

  const ExecutionProgressWidget({
    super.key,
    required this.progress,
    this.width = 200,
    this.height = 20,
    this.showPercentage = true,
    this.showTimeRemaining = true,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              progress.currentStep,
              style: Theme.of(context).textTheme.bodySmall,
            ),
            if (showPercentage)
              Text(
                '${progress.percentage.toStringAsFixed(0)}%',
                style: Theme.of(context).textTheme.bodySmall,
              ),
          ],
        ),
        const SizedBox(height: 4),
        Stack(
          children: [
            Container(
              width: width,
              height: height,
              decoration: BoxDecoration(
                color: Colors.grey.shade300,
                borderRadius: BorderRadius.circular(height / 2),
              ),
            ),
            Container(
              width: width * (progress.percentage / 100),
              height: height,
              decoration: BoxDecoration(
                color: _getProgressColor(),
                borderRadius: BorderRadius.circular(height / 2),
              ),
            ),
          ],
        ),
        if (showTimeRemaining && progress.estimatedTimeRemaining.isNotEmpty) ...[
          const SizedBox(height: 4),
          Text(
            'Осталось: ${progress.estimatedTimeRemaining}',
            style: Theme.of(context).textTheme.bodySmall?.copyWith(
                  color: Colors.grey.shade600,
                ),
          ),
        ],
      ],
    );
  }

  Color _getProgressColor() {
    if (progress.percentage >= 100) {
      return Colors.green;
    } else if (progress.percentage >= 70) {
      return Colors.blue;
    } else if (progress.percentage >= 30) {
      return Colors.orange;
    } else {
      return Colors.blueGrey;
    }
  }
}

class CircularExecutionProgressWidget extends StatelessWidget {
  final ExecutionProgressDto progress;
  final double size;
  final double strokeWidth;
  final bool showPercentage;
  final bool showStepInfo;

  const CircularExecutionProgressWidget({
    super.key,
    required this.progress,
    this.size = 80,
    this.strokeWidth = 8,
    this.showPercentage = true,
    this.showStepInfo = true,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Stack(
          alignment: Alignment.center,
          children: [
            SizedBox(
              width: size,
              height: size,
              child: CircularProgressIndicator(
                value: progress.percentage / 100,
                backgroundColor: Colors.grey.shade300,
                valueColor: AlwaysStoppedAnimation<Color>(
                  _getProgressColor(),
                ),
                strokeWidth: strokeWidth,
              ),
            ),
            if (showPercentage)
              Text(
                '${progress.percentage.toStringAsFixed(0)}%',
                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
          ],
        ),
        if (showStepInfo) ...[
          const SizedBox(height: 8),
          Text(
            progress.currentStep,
            style: Theme.of(context).textTheme.bodySmall,
            textAlign: TextAlign.center,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ],
    );
  }

  Color _getProgressColor() {
    if (progress.percentage >= 100) {
      return Colors.green;
    } else if (progress.percentage >= 70) {
      return Colors.blue;
    } else if (progress.percentage >= 30) {
      return Colors.orange;
    } else {
      return Colors.blueGrey;
    }
  }
}

class StepProgressWidget extends StatelessWidget {
  final List<StepExecutionDto> steps;
  final int currentStepIndex;

  const StepProgressWidget({
    super.key,
    required this.steps,
    this.currentStepIndex = 0,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: steps.asMap().entries.map((entry) {
        final index = entry.key;
        final step = entry.value;
        final isCompleted = index < currentStepIndex;
        final isCurrent = index == currentStepIndex;
        final isPending = index > currentStepIndex;

        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 4),
          child: Row(
            children: [
              Container(
                width: 20,
                height: 20,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: _getStepColor(isCompleted, isCurrent, isPending),
                  border: Border.all(
                    color: _getBorderColor(isCompleted, isCurrent, isPending),
                    width: 2,
                  ),
                ),
                child: isCompleted
                    ? const Icon(
                        Icons.check,
                        size: 12,
                        color: Colors.white,
                      )
                    : isCurrent
                        ? SizedBox(
                            width: 8,
                            height: 8,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              valueColor: AlwaysStoppedAnimation<Color>(
                                _getProgressColor(),
                              ),
                            ),
                          )
                        : null,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      step.name,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            fontWeight: isCurrent ? FontWeight.w600 : FontWeight.normal,
                            color: _getTextColor(isCompleted, isCurrent, isPending),
                          ),
                    ),
                    if (step.status != 'PENDING' && step.status != 'RUNNING')
                      Text(
                        _getStatusText(step.status),
                        style: Theme.of(context).textTheme.bodySmall?.copyWith(
                              color: _getStatusTextColor(step.status),
                            ),
                      ),
                  ],
                ),
              ),
              if (step.completedAt != null)
                Text(
                  '${step.executionTimeMs}ms',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Colors.grey.shade600,
                      ),
                ),
            ],
          ),
        );
      }).toList(),
    );
  }

  Color _getStepColor(bool isCompleted, bool isCurrent, bool isPending) {
    if (isCompleted) return Colors.green;
    if (isCurrent) return Colors.blue;
    if (isPending) return Colors.grey.shade400;
    return Colors.grey;
  }

  Color _getBorderColor(bool isCompleted, bool isCurrent, bool isPending) {
    if (isCompleted) return Colors.green;
    if (isCurrent) return Colors.blue;
    return Colors.grey.shade400;
  }

  Color _getTextColor(bool isCompleted, bool isCurrent, bool isPending) {
    if (isCompleted) return Colors.green.shade800;
    if (isCurrent) return Colors.blue.shade800;
    return Colors.grey.shade600;
  }

  Color _getStatusTextColor(String status) {
    switch (status) {
      case 'COMPLETED':
        return Colors.green;
      case 'FAILED':
        return Colors.red;
      case 'SKIPPED':
        return Colors.orange;
      default:
        return Colors.grey;
    }
  }

  Color _getProgressColor() {
    return Colors.blue;
  }

  String _getStatusText(String status) {
    switch (status) {
      case 'COMPLETED':
        return 'Завершено';
      case 'FAILED':
        return 'Ошибка';
      case 'SKIPPED':
        return 'Пропущено';
      case 'RUNNING':
        return 'Выполняется...';
      default:
        return status;
    }
  }
}