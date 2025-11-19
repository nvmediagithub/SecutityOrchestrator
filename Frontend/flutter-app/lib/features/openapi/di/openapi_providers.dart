import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/providers.dart';
import '../data/openapi_api_service.dart';

final openApiServiceProvider = Provider<OpenApiApiService>((ref) {
  final baseUrl = ref.watch(backendBaseUrlProvider);
  final client = ref.watch(httpClientProvider);
  final service = OpenApiApiService(baseUrl: baseUrl, client: client);
  ref.onDispose(service.dispose);
  return service;
});
