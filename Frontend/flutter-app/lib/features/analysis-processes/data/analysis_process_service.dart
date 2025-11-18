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

  @override
  Future<List<AnalysisProcess>> getProcesses() async {
    try {
      final response = await _client.get(Uri.parse('$_baseUrl/api/analysis-processes'));

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        return data.map((json) => AnalysisProcess.fromJson(json)).toList();
      } else {
        throw Exception('Failed to load processes: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to fetch processes: $e');
    }
  }

  @override
  Future<AnalysisProcess> createProcess(AnalysisProcess process) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/api/analysis-processes'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode(process.toJson()),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        final data = json.decode(response.body);
        return AnalysisProcess.fromJson(data);
      } else {
        throw Exception('Failed to create process: ${response.statusCode}');
      }
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
        final data = json.decode(response.body);
        return AnalysisProcess.fromJson(data);
      } else {
        throw Exception('Failed to load process: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Failed to fetch process: $e');
    }
  }
}