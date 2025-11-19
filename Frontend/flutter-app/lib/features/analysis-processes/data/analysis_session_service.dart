import 'dart:convert';

import 'package:http/http.dart' as http;

import '../domain/analysis_session.dart';

class AnalysisSessionService {
  final http.Client _client;
  final String _baseUrl;

  AnalysisSessionService(this._client, this._baseUrl);

  static const _jsonHeaders = {'Content-Type': 'application/json'};

  Future<AnalysisSession?> getLatestSession(String processId) async {
    final response = await _client.get(
      Uri.parse(
        '$_baseUrl/api/analysis-processes/$processId/analysis-sessions/latest',
      ),
      headers: _jsonHeaders,
    );
    final data = _parseResponse(response);
    if (data == null) {
      return null;
    }
    return AnalysisSession.fromJson(data as Map<String, dynamic>);
  }

  Future<AnalysisSession> startSession(String processId) async {
    final response = await _client.post(
      Uri.parse(
        '$_baseUrl/api/analysis-processes/$processId/analysis-sessions',
      ),
      headers: _jsonHeaders,
    );
    final data = _parseResponse(response);
    return AnalysisSession.fromJson(data as Map<String, dynamic>);
  }

  Future<AnalysisSession> provideInputs(
    String sessionId,
    Map<String, String> inputs,
  ) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/api/analysis-sessions/$sessionId/inputs'),
      headers: _jsonHeaders,
      body: jsonEncode(inputs),
    );
    final data = _parseResponse(response);
    return AnalysisSession.fromJson(data as Map<String, dynamic>);
  }

  Future<AnalysisSession> completeLlm(
    String sessionId, {
    String? script,
  }) async {
    final body = script != null ? jsonEncode({'script': script}) : '{}';
    final response = await _client.post(
      Uri.parse('$_baseUrl/api/analysis-sessions/$sessionId/llm'),
      headers: _jsonHeaders,
      body: body,
    );
    final data = _parseResponse(response);
    return AnalysisSession.fromJson(data as Map<String, dynamic>);
  }

  Future<AnalysisSession> submitTestResult(
    String sessionId,
    Map<String, Object> payload,
  ) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/api/analysis-sessions/$sessionId/tests'),
      headers: _jsonHeaders,
      body: jsonEncode(payload),
    );
    final data = _parseResponse(response);
    return AnalysisSession.fromJson(data as Map<String, dynamic>);
  }

  dynamic _parseResponse(http.Response response) {
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('Session request failed: ${response.statusCode}');
    }
    final decoded = jsonDecode(response.body) as Map<String, dynamic>;
    if (decoded['success'] != true) {
      final message = decoded['message'] ?? 'Unknown error';
      throw Exception(message);
    }
    return decoded['data'];
  }
}
