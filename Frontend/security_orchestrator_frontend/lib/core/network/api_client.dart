import 'dart:convert';
import 'package:http/http.dart' as http;
import '../../data/models/api_response.dart';
import '../constants/api_constants.dart';

class ApiClient {
  final http.Client _client;
  final String baseUrl;

  ApiClient({
    http.Client? client,
    this.baseUrl = ApiConstants.baseUrl,
  }) : _client = client ?? http.Client();

  Future<ApiResponse<T>> get<T>(
    String endpoint, {
    Map<String, String>? headers,
    required T Function(Object? json) fromJson,
  }) async {
    try {
      final response = await _client.get(
        Uri.parse('$baseUrl$endpoint'),
        headers: _buildHeaders(headers),
      );

      return _handleResponse<T>(response, fromJson);
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  Future<ApiResponse<T>> post<T>(
    String endpoint, {
    Map<String, String>? headers,
    Object? body,
    required T Function(Object? json) fromJson,
  }) async {
    try {
      final response = await _client.post(
        Uri.parse('$baseUrl$endpoint'),
        headers: _buildHeaders(headers),
        body: body != null ? jsonEncode(body) : null,
      );

      return _handleResponse<T>(response, fromJson);
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  Future<ApiResponse<T>> put<T>(
    String endpoint, {
    Map<String, String>? headers,
    Object? body,
    required T Function(Object? json) fromJson,
  }) async {
    try {
      final response = await _client.put(
        Uri.parse('$baseUrl$endpoint'),
        headers: _buildHeaders(headers),
        body: body != null ? jsonEncode(body) : null,
      );

      return _handleResponse<T>(response, fromJson);
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  Future<ApiResponse<T>> delete<T>(
    String endpoint, {
    Map<String, String>? headers,
    required T Function(Object? json) fromJson,
  }) async {
    try {
      final response = await _client.delete(
        Uri.parse('$baseUrl$endpoint'),
        headers: _buildHeaders(headers),
      );

      return _handleResponse<T>(response, fromJson);
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }

  Future<ApiResponse<T>> uploadFile<T>(
    String endpoint,
    String filePath, {
    Map<String, String>? fields,
    required T Function(Object? json) fromJson,
  }) async {
    try {
      final request = http.MultipartRequest(
        'POST',
        Uri.parse('$baseUrl$endpoint'),
      );

      request.files.add(await http.MultipartFile.fromPath('file', filePath));

      if (fields != null) {
        request.fields.addAll(fields);
      }

      final streamedResponse = await request.send();
      final response = await http.Response.fromStream(streamedResponse);

      return _handleResponse<T>(response, fromJson);
    } catch (e) {
      throw Exception('Upload error: $e');
    }
  }

  Map<String, String> _buildHeaders(Map<String, String>? additionalHeaders) {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    if (additionalHeaders != null) {
      headers.addAll(additionalHeaders);
    }

    return headers;
  }

  ApiResponse<T> _handleResponse<T>(
    http.Response response,
    T Function(Object? json) fromJson,
  ) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      final jsonData = jsonDecode(response.body);
      return ApiResponse<T>.fromJson(jsonData, fromJson);
    } else {
      throw Exception(
        'HTTP ${response.statusCode}: ${response.reasonPhrase}',
      );
    }
  }

  void dispose() {
    _client.close();
  }
}