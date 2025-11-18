import 'dart:convert';

import 'package:http/http.dart' as http;

import '../domain/analysis_process.dart';

abstract class AnalysisProcessService {
  Future<List<AnalysisProcess>> getProcesses();
  Future<AnalysisProcess> createProcess(AnalysisProcess process);
  Future<void> deleteProcess(String id);
  Future<AnalysisProcess> getProcess(String id);
}

class AnalysisProcessServiceImpl implements AnalysisProcessService {
  final http.Client _client;
  final String _baseUrl;

  AnalysisProcessServiceImpl(this._client, this._baseUrl);

  static const _jsonHeaders = {'Content-Type': 'application/json'};

  @override
  Future<List<AnalysisProcess>> getProcesses() async {
    try {
      final data = await _getData('/api/analysis-processes') as List<dynamic>;
      return data
          .map((json) => AnalysisProcess.fromJson(json as Map<String, dynamic>))
          .toList();
    } catch (e) {
      throw Exception('Failed to fetch processes: $e');
    }
  }

  @override
  Future<AnalysisProcess> createProcess(AnalysisProcess process) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/api/analysis-processes'),
        headers: _jsonHeaders,
        body: json.encode(process.toJson()),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final payload = _parseApiResponse(response);
        return AnalysisProcess.fromJson(payload as Map<String, dynamic>);
      }
      throw Exception('Failed to create process: ${response.statusCode}');
    } catch (e) {
      throw Exception('Failed to create process: $e');
    }
  }

  @override
  Future<void> deleteProcess(String id) async {
    try {
      final response = await _client.delete(
        Uri.parse('$_baseUrl/api/analysis-processes/$id'),
      );
      if (response.statusCode != 204 && response.statusCode != 200) {
        throw Exception('Failed to delete process: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to delete process: $e');
    }
  }

  @override
  Future<AnalysisProcess> getProcess(String id) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/api/analysis-processes/$id'),
      );
      if (response.statusCode == 200) {
        final payload = _parseApiResponse(response);
        return AnalysisProcess.fromJson(payload as Map<String, dynamic>);
      } else if (response.statusCode == 404) {
        throw Exception('Process not found');
      } else {
        throw Exception('Failed to load process: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to fetch process: $e');
    }
  }

  Future<dynamic> _getData(String path) async {
    final response = await _client.get(
      Uri.parse('$_baseUrl$path'),
      headers: _jsonHeaders,
    );
    if (response.statusCode != 200) {
      throw Exception(
        'Request to $path failed with code ${response.statusCode}',
      );
    }
    return _parseApiResponse(response);
  }

  dynamic _parseApiResponse(http.Response response) {
    final decoded = json.decode(response.body) as Map<String, dynamic>;
    if (decoded['success'] != true) {
      final message = decoded['message'] ?? 'Unknown error';
      throw Exception(message);
    }
    return decoded['data'];
  }
}
