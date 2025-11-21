import 'package:flutter/material.dart';

import '../../domain/analysis_session.dart';

String sessionStatusLabel(AnalysisSessionStatus status) {
  switch (status) {
    case AnalysisSessionStatus.waitingForInput:
      return 'Ожидание ввода';
    case AnalysisSessionStatus.running:
    case AnalysisSessionStatus.waitingForTest:
      return 'В процессе';
    case AnalysisSessionStatus.completed:
      return 'Завершено';
    case AnalysisSessionStatus.failed:
      return 'Сбой';
  }
}

String stepStatusLabel(AnalysisStepStatus status) {
  switch (status) {
    case AnalysisStepStatus.pending:
      return 'Ожидание';
    case AnalysisStepStatus.waiting:
      return 'Ожидается';
    case AnalysisStepStatus.running:
      return 'В процессе';
    case AnalysisStepStatus.completed:
      return 'Завершен';
    case AnalysisStepStatus.failed:
      return 'Сбой';
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
