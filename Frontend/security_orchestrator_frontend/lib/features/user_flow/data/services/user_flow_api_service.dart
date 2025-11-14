import 'dart:convert';
import 'package:http/http.dart' as http;

import '../../../../shared/core/constants/api_constants.dart';
import '../models/analysis_request.dart';
import '../models/analysis_response.dart';

class UserFlowApiService {
  final http.Client _client;

  UserFlowApiService({http.Client? client}) : _client = client ?? http.Client();

  Future<bool> checkHealth() async {
    try {
      final response = await _client.get(
        Uri.parse('${ApiConstants.baseUrl}/health'),
        headers: ApiConstants.defaultHeaders,
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  Future<AnalysisResponse> startAnalysis(AnalysisRequest request) async {
    final response = await _client.post(
      Uri.parse('${ApiConstants.baseUrl}/user-flow/analyze'),
      headers: {
        ...ApiConstants.defaultHeaders,
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to start analysis: ${response.statusCode}');
    }

    final data = jsonDecode(response.body);
    return AnalysisResponse.fromJson(data);
  }

  Future<AnalysisStatus> getAnalysisStatus(String analysisId) async {
    final response = await _client.get(
      Uri.parse('${ApiConstants.baseUrl}/user-flow/status/$analysisId'),
      headers: ApiConstants.defaultHeaders,
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to get analysis status: ${response.statusCode}');
    }

    final data = jsonDecode(response.body);
    return AnalysisStatus.fromJson(data);
  }

  Future<AnalysisResult> getAnalysisResult(String analysisId) async {
    final response = await _client.get(
      Uri.parse('${ApiConstants.baseUrl}/user-flow/result/$analysisId'),
      headers: ApiConstants.defaultHeaders,
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to get analysis result: ${response.statusCode}');
    }

    final data = jsonDecode(response.body);
    return AnalysisResult.fromJson(data);
  }

  Future<List<AnalysisHistory>> getAnalysisHistory({
    int page = 0,
    int size = 20,
  }) async {
    final response = await _client.get(
      Uri.parse('${ApiConstants.baseUrl}/user-flow/history?page=$page&size=$size'),
      headers: ApiConstants.defaultHeaders,
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to get analysis history: ${response.statusCode}');
    }

    final data = jsonDecode(response.body) as List;
    return data.map((item) => AnalysisHistory.fromJson(item)).toList();
  }

  void dispose() {
    _client.close();
  }
}