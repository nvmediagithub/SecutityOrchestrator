import 'dart:convert';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:web_socket_channel/status.dart' as websocket_status;
import '../models/user_flow_models.dart';
import '../../core/constants/api_constants.dart';

class UserFlowApiService {
  static final UserFlowApiService _instance = UserFlowApiService._internal();
  factory UserFlowApiService() => _instance;
  UserFlowApiService._internal();

  final Dio _dio = Dio(BaseOptions(
    baseUrl: ApiConstants.llmBaseUrl,
    connectTimeout: const Duration(seconds: 30),
    receiveTimeout: const Duration(seconds: 60),
    sendTimeout: const Duration(seconds: 30),
  ));

  WebSocketChannel? _websocketChannel;

  // Health check
  Future<bool> checkHealth() async {
    try {
      final response = await _dio.get(ApiConstants.healthEndpoint);
      return response.statusCode == 200;
    } catch (e) {
      print('Health check failed: $e');
      return false;
    }
  }

  // Start analysis
  Future<AnalysisResponse> startAnalysis(AnalysisRequest request) async {
    try {
      final response = await _dio.post(
        '/api/analyze',
        data: request.toJson(),
        options: Options(
          headers: {
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200) {
        return AnalysisResponse.fromJson(response.data);
      } else {
        throw Exception('Failed to start analysis: ${response.statusMessage}');
      }
    } on DioException catch (e) {
      throw Exception('Network error: ${e.message}');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  // Get analysis status
  Future<Map<String, dynamic>> getAnalysisStatus(String analysisId) async {
    try {
      final response = await _dio.get('/api/report/$analysisId');
      
      if (response.statusCode == 200) {
        return response.data;
      } else {
        throw Exception('Failed to get analysis status: ${response.statusMessage}');
      }
    } on DioException catch (e) {
      throw Exception('Network error: ${e.message}');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  // Upload file
  Future<UploadedFile> uploadFile(File file) async {
    try {
      final fileName = file.path.split('/').last;
      final extension = fileName.split('.').last.toLowerCase();
      
      // Check file type
      if (!['docx', 'txt', 'pdf'].contains(extension)) {
        throw Exception('Unsupported file type. Only docx, txt, and pdf files are allowed.');
      }

      final formData = FormData.fromMap({
        'file': await MultipartFile.fromFile(
          file.path,
          filename: fileName,
        ),
      });

      final response = await _dio.post(
        '/api/upload',
        data: formData,
        options: Options(
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        ),
      );

      if (response.statusCode == 200) {
        return UploadedFile.fromJson(response.data);
      } else {
        throw Exception('Failed to upload file: ${response.statusMessage}');
      }
    } on DioException catch (e) {
      throw Exception('Network error: ${e.message}');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  // Get analysis result
  Future<AnalysisResult> getAnalysisResult(String analysisId) async {
    try {
      final response = await _dio.get('/api/result/$analysisId');
      
      if (response.statusCode == 200) {
        return AnalysisResult.fromJson(response.data);
      } else {
        throw Exception('Failed to get analysis result: ${response.statusMessage}');
      }
    } on DioException catch (e) {
      throw Exception('Network error: ${e.message}');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  // Export report
  Future<String> exportReport(String analysisId, ExportOptions options) async {
    try {
      final response = await _dio.post(
        '/api/export/$analysisId',
        data: options.toJson(),
        options: Options(
          responseType: ResponseType.bytes,
          headers: {
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200) {
        // Save file locally
        final fileName = 'analysis_report_${analysisId}.${options.format}';
        final directory = Directory.systemTemp;
        final file = File('${directory.path}/$fileName');
        await file.writeAsBytes(response.data);
        return file.path;
      } else {
        throw Exception('Failed to export report: ${response.statusMessage}');
      }
    } on DioException catch (e) {
      throw Exception('Network error: ${e.message}');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  // Get analysis history
  Future<List<UserTask>> getAnalysisHistory() async {
    try {
      final response = await _dio.get('/api/history');
      
      if (response.statusCode == 200) {
        final List<dynamic> data = response.data;
        return data.map((json) => UserTask.fromJson(json)).toList();
      } else {
        throw Exception('Failed to get analysis history: ${response.statusMessage}');
      }
    } on DioException catch (e) {
      throw Exception('Network error: ${e.message}');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  // WebSocket connection for real-time updates
  void connectWebSocket(String analysisId, Function(WebSocketMessage) onMessage) {
    try {
      final wsUrl = '${ApiConstants.websocketEndpoint}?analysisId=$analysisId';
      _websocketChannel = WebSocketChannel.connect(Uri.parse(wsUrl));

      _websocketChannel!.stream.listen(
        (data) {
          try {
            final messageData = json.decode(data);
            final message = WebSocketMessage.fromJson(messageData);
            onMessage(message);
          } catch (e) {
            print('Failed to parse WebSocket message: $e');
          }
        },
        onError: (error) {
          print('WebSocket error: $error');
        },
        onDone: () {
          print('WebSocket connection closed');
        },
      );
    } catch (e) {
      print('Failed to connect WebSocket: $e');
    }
  }

  // Disconnect WebSocket
  void disconnectWebSocket() {
    _websocketChannel?.sink.close(websocket_status.normalClosure);
    _websocketChannel = null;
  }

  // Test with simple LLM request (for development)
  Future<String> testLLMRequest(String prompt) async {
    try {
      final response = await _dio.post(
        ApiConstants.llmCompleteEndpoint,
        data: {'prompt': prompt},
        options: Options(
          headers: {
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200) {
        return response.data['completion'] ?? 'No completion received';
      } else {
        throw Exception('LLM test failed: ${response.statusMessage}');
      }
    } on DioException catch (e) {
      throw Exception('Network error: ${e.message}');
    } catch (e) {
      throw Exception('Unexpected error: $e');
    }
  }

  // Cleanup
  void dispose() {
    _dio.close();
    disconnectWebSocket();
  }
}