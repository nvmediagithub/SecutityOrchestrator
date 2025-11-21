import 'package:flutter/material.dart';

import '../../domain/analysis_session.dart';

String sessionStatusLabel(AnalysisSessionStatus status) {
  switch (status) {
    case AnalysisSessionStatus.waitingForInput:
      return 'Waiting for input';
    case AnalysisSessionStatus.running:
    case AnalysisSessionStatus.waitingForTest:
      return 'In progress';
    case AnalysisSessionStatus.completed:
      return 'Completed';
    case AnalysisSessionStatus.failed:
      return 'Failed';
  }
}

String stepStatusLabel(AnalysisStepStatus status) {
  switch (status) {
    case AnalysisStepStatus.pending:
      return 'Pending';
    case AnalysisStepStatus.waiting:
      return 'Waiting';
    case AnalysisStepStatus.running:
      return 'Running';
    case AnalysisStepStatus.completed:
      return 'Completed';
    case AnalysisStepStatus.failed:
      return 'Failed';
  }
}

Color statusColor(AnalysisSessionStatus status) {
  switch (status) {
    case AnalysisSessionStatus.waitingForInput:
      return Colors.orange;
    case AnalysisSessionStatus.running:
    case AnalysisSessionStatus.waitingForTest:
      return Colors.blue;
    case AnalysisSessionStatus.completed:
      return Colors.green;
    case AnalysisSessionStatus.failed:
      return Colors.red;
  }
}

Color stepStatusColor(AnalysisStepStatus status) {
  switch (status) {
    case AnalysisStepStatus.completed:
      return Colors.green;
    case AnalysisStepStatus.failed:
      return Colors.red;
    case AnalysisStepStatus.running:
      return Colors.blue;
    default:
      return Colors.orange;
  }
}
