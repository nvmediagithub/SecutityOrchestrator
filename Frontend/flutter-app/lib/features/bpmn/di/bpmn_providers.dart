import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers.dart';
import '../data/bpmn_api_service.dart';

final bpmnApiServiceProvider = Provider<BpmnApiService>((ref) {
  final baseUrl = ref.watch(backendBaseUrlProvider);
  final client = ref.watch(httpClientProvider);
  final service = BpmnApiService(baseUrl: baseUrl, client: client);
  ref.onDispose(service.dispose);
  return service;
});
