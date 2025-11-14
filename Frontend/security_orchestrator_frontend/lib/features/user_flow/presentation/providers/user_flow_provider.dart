import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../data/models/analysis_request.dart';
import '../../data/models/analysis_response.dart';
import '../../data/services/user_flow_api_service.dart';

// Service provider
final userFlowApiServiceProvider = Provider<UserFlowApiService>((ref) {
  return UserFlowApiService();
});

// State class
class UserFlowState {
  final bool isAnalyzing;
  final bool isLoading;
  final AnalysisStatus? analysisStatus;
  final AnalysisResult? analysisResult;
  final List<AnalysisHistory> analysisHistory;
  final String? error;
  final bool hasUploadedFile;
  final String? uploadedFileName;

  const UserFlowState({
    this.isAnalyzing = false,
    this.isLoading = false,
    this.analysisStatus,
    this.analysisResult,
    this.analysisHistory = const [],
    this.error,
    this.hasUploadedFile = false,
    this.uploadedFileName,
  });

  UserFlowState copyWith({
    bool? isAnalyzing,
    bool? isLoading,
    AnalysisStatus? analysisStatus,
    AnalysisResult? analysisResult,
    List<AnalysisHistory>? analysisHistory,
    String? error,
    bool? hasUploadedFile,
    String? uploadedFileName,
  }) {
    return UserFlowState(
      isAnalyzing: isAnalyzing ?? this.isAnalyzing,
      isLoading: isLoading ?? this.isLoading,
      analysisStatus: analysisStatus ?? this.analysisStatus,
      analysisResult: analysisResult ?? this.analysisResult,
      analysisHistory: analysisHistory ?? this.analysisHistory,
      error: error,
      hasUploadedFile: hasUploadedFile ?? this.hasUploadedFile,
      uploadedFileName: uploadedFileName ?? this.uploadedFileName,
    );
  }

  bool get canStartAnalysis => hasUploadedFile && !isAnalyzing && error == null;
}

// State notifier
class UserFlowNotifier extends StateNotifier<UserFlowState> {
  final UserFlowApiService _apiService;

  UserFlowNotifier(this._apiService) : super(const UserFlowState());

  Future<void> uploadFile(String content, String fileName, String fileType) async {
    state = state.copyWith(
      hasUploadedFile: true,
      uploadedFileName: fileName,
      error: null,
    );
  }

  Future<void> startAnalysis() async {
    if (!state.canStartAnalysis) return;

    state = state.copyWith(isAnalyzing: true, error: null);

    try {
      // Create analysis request
      final request = AnalysisRequest(
        documentContent: 'placeholder', // This would come from file upload
        documentType: 'text/plain',
        documentName: state.uploadedFileName,
      );

      final response = await _apiService.startAnalysis(request);
      state = state.copyWith(
        analysisStatus: AnalysisStatus(
          analysisId: response.analysisId,
          status: 'pending',
          progress: 0.0,
          lastUpdated: DateTime.now(),
        ),
      );

      // Start polling for status updates
      _pollAnalysisStatus(response.analysisId);
    } catch (e) {
      state = state.copyWith(
        isAnalyzing: false,
        error: e.toString(),
      );
    }
  }

  Future<void> _pollAnalysisStatus(String analysisId) async {
    while (state.isAnalyzing) {
      try {
        final status = await _apiService.getAnalysisStatus(analysisId);
        state = state.copyWith(analysisStatus: status);

        if (status.status == 'completed') {
          await _loadAnalysisResult(analysisId);
          break;
        } else if (status.status == 'failed') {
          state = state.copyWith(
            isAnalyzing: false,
            error: status.error ?? 'Analysis failed',
          );
          break;
        }

        await Future.delayed(const Duration(seconds: 2));
      } catch (e) {
        state = state.copyWith(
          isAnalyzing: false,
          error: e.toString(),
        );
        break;
      }
    }
  }

  Future<void> _loadAnalysisResult(String analysisId) async {
    try {
      final result = await _apiService.getAnalysisResult(analysisId);
      state = state.copyWith(
        isAnalyzing: false,
        analysisResult: result,
      );
    } catch (e) {
      state = state.copyWith(
        isAnalyzing: false,
        error: e.toString(),
      );
    }
  }

  Future<void> loadAnalysisHistory({int page = 0, int size = 20}) async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      final history = await _apiService.getAnalysisHistory(page: page, size: size);
      state = state.copyWith(
        analysisHistory: history,
        isLoading: false,
      );
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  void clearError() {
    state = state.copyWith(error: null);
  }

  void resetAnalysis() {
    state = state.copyWith(
      isAnalyzing: false,
      analysisStatus: null,
      analysisResult: null,
      error: null,
    );
  }

  void refresh() {
    if (state.analysisStatus != null) {
      _pollAnalysisStatus(state.analysisStatus!.analysisId);
    }
  }
}

// Provider
final userFlowProvider = StateNotifierProvider<UserFlowNotifier, UserFlowState>((ref) {
  final apiService = ref.watch(userFlowApiServiceProvider);
  return UserFlowNotifier(apiService);
});