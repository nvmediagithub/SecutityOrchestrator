import 'dart:convert';
import 'dart:typed_data';

import 'package:http/http.dart' as http;

import '../../../core/providers.dart';
import '../domain/models/openapi_analysis.dart';

class OpenApiApiService {
  final String baseUrl;
  final http.Client client;

  OpenApiApiService({required this.baseUrl, http.Client? client})
    : client = client ?? http.Client();

  Future<OpenApiAnalysis> analyzeSpec({
    required Uint8List bytes,
    required String fileName,
    String? specName,
  }) async {
    final uri = Uri.parse('$baseUrl/api/openapi/analyze');
    final request = http.MultipartRequest('POST', uri);
    request.files.add(
      http.MultipartFile.fromBytes('file', bytes, filename: fileName),
    );
    if (specName != null && specName.isNotEmpty) {
      request.fields['specName'] = specName;
    }
    final response = await http.Response.fromStream(await request.send());
    final payload = _parseApiResponse(response);
    return OpenApiAnalysis.fromJson(payload as Map<String, dynamic>);
  }

  Future<List<OpenApiAnalysis>> getExamples() async {
    final response = await client.get(
      Uri.parse('$baseUrl/api/openapi/examples'),
      headers: {'Content-Type': 'application/json'},
    );
    final payload = _parseApiResponse(response);
    return (payload as List<dynamic>)
        .map((json) => OpenApiAnalysis.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  Future<OpenApiAnalysis?> getProcessSpec(String processId) async {
    final uri = Uri.parse('$baseUrl/api/analysis-processes/$processId/openapi');
    final response = await client.get(uri);
    if (response.statusCode == 404) {
      return null;
    }
    final payload = _parseApiResponse(response);
    return OpenApiAnalysis.fromJson(payload as Map<String, dynamic>);
  }

  Future<OpenApiAnalysis> uploadProcessSpec({
    required String processId,
    required Uint8List bytes,
    required String fileName,
  }) async {
    final uri = Uri.parse('$baseUrl/api/analysis-processes/$processId/openapi');
    final request = http.MultipartRequest('POST', uri);
    request.files.add(
      http.MultipartFile.fromBytes('file', bytes, filename: fileName),
    );
    final response = await http.Response.fromStream(await request.send());
    final payload = _parseApiResponse(response);
    return OpenApiAnalysis.fromJson(payload as Map<String, dynamic>);
  }

  dynamic _parseApiResponse(http.Response response) {
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception(
        'OpenAPI API request failed: ${response.statusCode} ${response.reasonPhrase}',
      );
    }
    final decoded = jsonDecode(response.body) as Map<String, dynamic>;
    if (decoded['success'] != true) {
      throw Exception(decoded['message'] ?? 'Unknown OpenAPI API error');
    }
    return decoded['data'];
  }

  void dispose() {
    client.close();
  }
}
