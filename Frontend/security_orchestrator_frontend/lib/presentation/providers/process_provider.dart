import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/process_dto.dart';
import '../../data/models/workflow_dto.dart';
import '../../data/repositories/process_repository.dart';
import '../../core/network/api_client.dart';
import '../../core/network/websocket_client.dart';

// API client provider
final apiClientProvider = Provider<ApiClient>((ref) {
  return ApiClient();
});

// WebSocket client provider
final stompClientProvider = Provider<WebSocketClient>((ref) {
  return WebSocketClient();
});

// Process repository provider with API client injection
final processRepositoryProvider = Provider<ProcessRepository>((ref) {
  final apiClient = ref.watch(apiClientProvider);
  return ProcessRepository(apiClient: apiClient);
});

final processesProvider = StateNotifierProvider<ProcessesNotifier, AsyncValue<List<ProcessSummaryDto>>>((ref) {
  final repository = ref.watch(processRepositoryProvider);
  final stompClient = ref.watch(stompClientProvider);
  return ProcessesNotifier(repository, stompClient);
});

// Enhanced provider for full process details
final processDetailsProvider = FutureProvider.family<ProcessDto, String>((ref, processId) async {
  final repository = ref.watch(processRepositoryProvider);
  return repository.getProcess(processId);
});

// Provider for workflows
final workflowsProvider = StateNotifierProvider<WorkflowsNotifier, AsyncValue<List<WorkflowSummaryDto>>>((ref) {
  return WorkflowsNotifier();
});

// Provider for workflow details
final workflowDetailsProvider = FutureProvider.family<WorkflowDto, String>((ref, workflowId) async {
  // Mock workflow details - in real app would fetch from repository
  await Future.delayed(const Duration(milliseconds: 500)); // Simulate API call
  return WorkflowDto(
    id: workflowId,
    name: 'Sample Workflow',
    description: 'A sample security validation workflow',
    status: WorkflowStatus.active,
    createdAt: DateTime.now().subtract(const Duration(days: 2)),
    updatedAt: DateTime.now(),
    processId: 'process1',
    testCaseIds: ['tc1', 'tc2', 'tc3'],
    executionConfig: ExecutionConfigurationDto(
      parallelExecution: true,
      maxRetries: 3,
      timeoutSeconds: 300,
      variables: {'env': 'production'},
    ),
  );
});

// Provider for execution status
final executionStatusProvider = StateNotifierProvider.family<ExecutionNotifier, AsyncValue<ExecutionStatusDto>, String>((ref, executionId) {
  final stompClient = ref.watch(stompClientProvider);
  return ExecutionNotifier(executionId, stompClient);
});

class ProcessesNotifier extends StateNotifier<AsyncValue<List<ProcessSummaryDto>>> {
  final ProcessRepository _repository;
  final WebSocketClient _stompClient;

  ProcessesNotifier(this._repository, this._stompClient) : super(const AsyncValue.loading()) {
    loadProcesses();
    _connectStomp();
  }

  Future<void> loadProcesses() async {
    state = const AsyncValue.loading();
    try {
      final processes = await _repository.getProcesses();
      // Add mock data if API returns empty
      if (processes.isEmpty) {
        state = AsyncValue.data(_getMockProcesses());
      } else {
        state = AsyncValue.data(processes);
      }
    } catch (error, stackTrace) {
      // Fallback to mock data on error
      state = AsyncValue.data(_getMockProcesses());
    }
  }

  List<ProcessSummaryDto> _getMockProcesses() {
    return [
      ProcessSummaryDto(
        id: 'process1',
        name: 'Security Validation Process',
        status: ProcessStatus.active,
        createdAt: DateTime.now().subtract(const Duration(days: 5)),
        elementCount: 5,
      ),
      ProcessSummaryDto(
        id: 'process2',
        name: 'API Testing Workflow',
        status: ProcessStatus.active,
        createdAt: DateTime.now().subtract(const Duration(days: 3)),
        elementCount: 8,
      ),
      ProcessSummaryDto(
        id: 'process3',
        name: 'Data Processing Pipeline',
        status: ProcessStatus.inactive,
        createdAt: DateTime.now().subtract(const Duration(days: 10)),
        elementCount: 12,
      ),
    ];
  }

  void _connectStomp() {
    _stompClient.connect();

    _stompClient.onMessage = (message) {
      // Handle real-time updates from WebSocket
      if (message['type'] == 'PROCESS_UPDATED' && mounted) {
        // Refresh processes when a process is updated
        loadProcesses();
      }
    };

    _stompClient.onConnected = () {
      // WebSocket connected successfully
      print('STOMP connected for process updates');
      // Subscribe to process updates
      _stompClient.send({'type': 'SUBSCRIBE', 'topic': 'processes'});
    };

    _stompClient.onError = (error) {
      print('WebSocket error: $error');
    };

    _stompClient.onDisconnected = () {
      print('WebSocket disconnected');
    };
  }

  Future<void> refresh() async {
    await loadProcesses();
  }

  @override
  void dispose() {
    _stompClient.disconnect();
    super.dispose();
  }
}

class WorkflowsNotifier extends StateNotifier<AsyncValue<List<WorkflowSummaryDto>>> {
  WorkflowsNotifier() : super(const AsyncValue.loading()) {
    loadWorkflows();
  }

  Future<void> loadWorkflows() async {
    state = const AsyncValue.loading();
    await Future.delayed(const Duration(milliseconds: 500)); // Simulate API call

    // Mock workflows
    final workflows = [
      WorkflowSummaryDto(
        id: 'wf1',
        name: 'Security Test Suite',
        status: WorkflowStatus.active,
        createdAt: DateTime.now().subtract(const Duration(days: 2)),
        processName: 'Security Validation Process',
        testCaseCount: 5,
        lastExecutionResult: 'PASSED',
      ),
      WorkflowSummaryDto(
        id: 'wf2',
        name: 'API Integration Tests',
        status: WorkflowStatus.running,
        createdAt: DateTime.now().subtract(const Duration(days: 1)),
        processName: 'API Testing Workflow',
        testCaseCount: 8,
        lastExecutionResult: 'RUNNING',
      ),
    ];

    state = AsyncValue.data(workflows);
  }
}

class ExecutionNotifier extends StateNotifier<AsyncValue<ExecutionStatusDto>> {
  final String executionId;
  final WebSocketClient _stompClient;

  ExecutionNotifier(this.executionId, this._stompClient) : super(const AsyncValue.loading()) {
    loadExecutionStatus();
    _connectStomp();
  }

  Future<void> loadExecutionStatus() async {
    state = const AsyncValue.loading();
    await Future.delayed(const Duration(milliseconds: 300)); // Simulate API call

    // Mock execution status
    final execution = ExecutionStatusDto(
      workflowId: 'wf1',
      executionId: executionId,
      state: ExecutionState.running,
      progress: 0.6,
      startedAt: DateTime.now().subtract(const Duration(minutes: 10)),
      steps: [
        StepExecutionDto(
          stepId: 'step1',
          stepName: 'Input Validation',
          state: ExecutionState.completed,
          startedAt: DateTime.now().subtract(const Duration(minutes: 10)),
          completedAt: DateTime.now().subtract(const Duration(minutes: 8)),
          result: 'PASSED - All inputs validated successfully',
        ),
        StepExecutionDto(
          stepId: 'step2',
          stepName: 'Authentication Check',
          state: ExecutionState.completed,
          startedAt: DateTime.now().subtract(const Duration(minutes: 8)),
          completedAt: DateTime.now().subtract(const Duration(minutes: 5)),
          result: 'PASSED - Authentication successful',
        ),
        StepExecutionDto(
          stepId: 'step3',
          stepName: 'Authorization Check',
          state: ExecutionState.running,
          startedAt: DateTime.now().subtract(const Duration(minutes: 5)),
        ),
      ],
    );

    state = AsyncValue.data(execution);
  }

  void _connectStomp() {
    _stompClient.onMessage = (message) {
      if (message['executionId'] == executionId && mounted) {
        // Update execution status from STOMP
        if (state.value != null) {
          // Parse the STOMP message and update the execution status
          final type = message['type'];
          final stepId = message['stepId'];
          final statusMessage = message['message'];
          
          final currentExecution = state.value!;
          if (type == 'STEP_COMPLETED' && stepId != null) {
            // Update specific step
            final updatedSteps = currentExecution.steps.map((step) {
              if (step.stepId == stepId) {
                return StepExecutionDto(
                  stepId: step.stepId,
                  stepName: step.stepName,
                  state: ExecutionState.completed,
                  startedAt: step.startedAt,
                  completedAt: DateTime.now(),
                  result: statusMessage ?? 'Step completed',
                );
              }
              return step;
            }).toList();
            
            // Calculate new progress
            final completedSteps = updatedSteps.where((s) => s.state == ExecutionState.completed).length;
            final progress = completedSteps / updatedSteps.length;
            
            // Create updated execution status
            final updatedExecution = ExecutionStatusDto(
              workflowId: currentExecution.workflowId,
              executionId: currentExecution.executionId,
              state: currentExecution.state,
              progress: progress,
              steps: updatedSteps,
              startedAt: currentExecution.startedAt,
              completedAt: currentExecution.completedAt,
              error: currentExecution.error,
            );
            
            state = AsyncValue.data(updatedExecution);
          } else if (type == 'EXECUTION_COMPLETED') {
            // Mark execution as completed
            final updatedExecution = ExecutionStatusDto(
              workflowId: currentExecution.workflowId,
              executionId: currentExecution.executionId,
              state: ExecutionState.completed,
              progress: 1.0,
              steps: currentExecution.steps,
              startedAt: currentExecution.startedAt,
              completedAt: DateTime.now(),
              error: currentExecution.error,
            );
            state = AsyncValue.data(updatedExecution);
          }
        }
      }
    };
  }

  void subscribeToExecution() {
    if (_stompClient.isConnected) {
      _stompClient.subscribeToExecution(executionId);
      _stompClient.send({'type': 'SUBSCRIBE', 'executionId': executionId});
    }
  }

  @override
  void dispose() {
    // STOMP cleanup handled by parent provider
    super.dispose();
  }
}