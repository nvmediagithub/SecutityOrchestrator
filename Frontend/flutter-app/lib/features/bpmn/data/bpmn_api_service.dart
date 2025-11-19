import 'dart:convert';
import 'dart:typed_data';

import 'package:http/http.dart' as http;

import '../../../core/providers.dart';
import '../domain/models/bpmn_analysis.dart';

class BpmnApiService {
  final String baseUrl;
  final http.Client client;

  BpmnApiService({required this.baseUrl, http.Client? client})
    : client = client ?? http.Client();

  Future<BpmnAnalysis> analyzeDiagram({
    required Uint8List bytes,
    required String fileName,
    String? diagramName,
  }) async {
    final uri = Uri.parse('$baseUrl/api/bpmn/analyze');
    final request = http.MultipartRequest('POST', uri);
    request.files.add(
      http.MultipartFile.fromBytes(
        'file',
        bytes,
        filename: fileName,
        contentType: null,
      ),
    );
    if (diagramName != null && diagramName.isNotEmpty) {
      request.fields['diagramName'] = diagramName;
    }

    final streamedResponse = await request.send();
    final response = await http.Response.fromStream(streamedResponse);
    final payload = _parseApiResponse(response);
    return BpmnAnalysis.fromJson(payload as Map<String, dynamic>);
  }

  Future<List<BpmnAnalysis>> getExamples() async {
    final response = await client.get(
      Uri.parse('$baseUrl/api/bpmn/examples'),
      headers: {'Content-Type': 'application/json'},
    );
    final payload = _parseApiResponse(response);
    final list = payload as List<dynamic>;
    return list
        .map((item) => BpmnAnalysis.fromJson(item as Map<String, dynamic>))
        .toList();
  }

  dynamic _parseApiResponse(http.Response response) {
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('BPMN API request failed: ${response.statusCode}');
    }
    final decoded = jsonDecode(response.body) as Map<String, dynamic>;
    if (decoded['success'] != true) {
      final message = decoded['message'] ?? 'Unknown error';
      throw Exception(message);
    }
    return decoded['data'];
  }

  void dispose() {
    client.close();
  }
}
