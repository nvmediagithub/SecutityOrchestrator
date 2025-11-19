import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'features/monitoring/presentation/home_page.dart';
import 'features/monitoring/di/connectivity_provider.dart';
import 'features/analysis-processes/presentation/analysis_processes_page.dart';
import 'features/analysis-processes/presentation/process_detail_page.dart';

final _router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => SelectionArea(child: const HomePage()),
    ),
    GoRoute(
      path: '/processes',
      builder: (context, state) =>
          SelectionArea(child: const AnalysisProcessesPage()),
      routes: [
        GoRoute(
          path: ':id',
          builder: (context, state) {
            final id = state.pathParameters['id']!;
            return SelectionArea(
              child: ProcessDetailPage(key: state.pageKey, processId: id),
            );
          },
        ),
      ],
    ),
  ],
);

class MyApp extends ConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    print('MyApp build called');
    return MaterialApp.router(
      title: 'Security Orchestrator',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      routerConfig: _router,
    );
  }
}
