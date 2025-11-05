import '../../core/network/api_client.dart';
import '../../core/constants/api_constants.dart';
import '../models/process_dto.dart';
import '../models/api_response.dart';

class ProcessRepository {
  final ApiClient _apiClient;

  ProcessRepository({ApiClient? apiClient})
      : _apiClient = apiClient ?? ApiClient();

  Future<List<ProcessSummaryDto>> getProcesses() async {
    final response = await _apiClient.get<List<dynamic>>(
      ApiConstants.processesEndpoint,
      fromJson: (json) => json as List<dynamic>,
    );

    if (response.data == null) {
      return [];
    }

    return response.data!.map((processJson) {
      // Map backend ProcessResponse to frontend ProcessSummaryDto
      return ProcessSummaryDto(
        id: processJson['id'] as String,
        name: processJson['name'] as String,
        status: _mapStatus(processJson['status'] as String),
        createdAt: DateTime.parse(processJson['createdAt'] as String),
        elementCount: 0, // Backend doesn't provide this in list endpoint
      );
    }).toList();
  }

  Future<ProcessDto> getProcess(String id) async {
    final response = await _apiClient.get<Map<String, dynamic>>(
      '${ApiConstants.processesEndpoint}/$id',
      fromJson: (json) => json as Map<String, dynamic>,
    );

    if (response.data == null) {
      throw Exception('Process not found');
    }

    // Map backend ProcessResponse to frontend ProcessDto
    return ProcessDto(
      id: response.data!['id'] as String,
      name: response.data!['name'] as String,
      description: null, // Backend doesn't provide description
      status: _mapStatus(response.data!['status'] as String),
      createdAt: DateTime.parse(response.data!['createdAt'] as String),
      updatedAt: DateTime.parse(response.data!['updatedAt'] as String),
      bpmnXml: '<?xml version="1.0" encoding="UTF-8"?><bpmn:definitions></bpmn:definitions>', // Mock BPMN XML
      elements: [], // Backend doesn't provide elements yet
    );
  }

  ProcessStatus _mapStatus(String backendStatus) {
    switch (backendStatus.toUpperCase()) {
      case 'ACTIVE':
        return ProcessStatus.active;
      case 'INACTIVE':
        return ProcessStatus.inactive;
      case 'ARCHIVED':
        return ProcessStatus.archived;
      default:
        return ProcessStatus.active;
    }
  }
}