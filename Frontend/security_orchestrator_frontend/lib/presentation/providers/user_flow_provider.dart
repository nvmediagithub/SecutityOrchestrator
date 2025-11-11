import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/user_flow_models.dart';

class UserFlowState {
  final List<UserTask> tasks;
  final Map<String, String> taskMessages; // taskId -> status message
  final bool isLoading;
  final String? error;

  const UserFlowState({
    this.tasks = const [],
    this.taskMessages = const {},
    this.isLoading = false,
    this.error,
  });

  UserTask? getTaskById(String id) {
    try {
      return tasks.firstWhere((task) => task.id == id);
    } catch (e) {
      return null;
    }
  }

  String? getTaskMessage(String taskId) {
    return taskMessages[taskId];
  }

  UserFlowState copyWith({
    List<UserTask>? tasks,
    Map<String, String>? taskMessages,
    bool? isLoading,
    String? error,
  }) {
    return UserFlowState(
      tasks: tasks ?? this.tasks,
      taskMessages: taskMessages ?? this.taskMessages,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }
}

class UserFlowNotifier extends StateNotifier<UserFlowState> {
  UserFlowNotifier() : super(const UserFlowState());

  void addTask(UserTask task) {
    state = state.copyWith(
      tasks: [...state.tasks, task],
    );
  }

  void updateTaskStatus(String taskId, TaskStatus status, String message) {
    final updatedTasks = state.tasks.map((task) {
      if (task.id == taskId) {
        return task.copyWith(status: status);
      }
      return task;
    }).toList();

    final updatedMessages = {...state.taskMessages};
    updatedMessages[taskId] = message;

    state = state.copyWith(
      tasks: updatedTasks,
      taskMessages: updatedMessages,
    );
  }

  void removeTask(String taskId) {
    state = state.copyWith(
      tasks: state.tasks.where((task) => task.id != taskId).toList(),
      taskMessages: Map.from(state.taskMessages)..remove(taskId),
    );
  }

  void setLoading(bool isLoading) {
    state = state.copyWith(isLoading: isLoading);
  }

  void setError(String? error) {
    state = state.copyWith(error: error, isLoading: false);
  }

  void clearError() {
    state = state.copyWith(error: null);
  }

  void clearAllTasks() {
    state = state.copyWith(
      tasks: [],
      taskMessages: {},
      error: null,
    );
  }

  List<UserTask> getTasksByStatus(TaskStatus status) {
    return state.tasks.where((task) => task.status == status).toList();
  }

  List<UserTask> getCompletedTasks() {
    return getTasksByStatus(TaskStatus.completed);
  }

  List<UserTask> getPendingTasks() {
    return getTasksByStatus(TaskStatus.pending);
  }

  List<UserTask> getAnalyzingTasks() {
    return getTasksByStatus(TaskStatus.analyzing);
  }

  List<UserTask> getErrorTasks() {
    return getTasksByStatus(TaskStatus.error);
  }
}

final userFlowProvider = StateNotifierProvider<UserFlowNotifier, UserFlowState>(
  (ref) => UserFlowNotifier(),
);

// Helper providers for specific task lists
final completedTasksProvider = Provider<List<UserTask>>((ref) {
  final state = ref.watch(userFlowProvider);
  return state.tasks.where((task) => task.status == TaskStatus.completed).toList();
});

final pendingTasksProvider = Provider<List<UserTask>>((ref) {
  final state = ref.watch(userFlowProvider);
  return state.tasks.where((task) => task.status == TaskStatus.pending).toList();
});

final analyzingTasksProvider = Provider<List<UserTask>>((ref) {
  final state = ref.watch(userFlowProvider);
  return state.tasks.where((task) => task.status == TaskStatus.analyzing).toList();
});

final errorTasksProvider = Provider<List<UserTask>>((ref) {
  final state = ref.watch(userFlowProvider);
  return state.tasks.where((task) => task.status == TaskStatus.error).toList();
});