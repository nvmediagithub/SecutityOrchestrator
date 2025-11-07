import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'shared/theme/app_theme.dart';
import 'presentation/screens/home_screen.dart';
import 'presentation/screens/llm_dashboard_screen.dart';

void main() {
  runApp(const ProviderScope(child: MyApp()));
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Security Orchestrator',
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      home: const HomeScreen(),
      routes: {
        '/llm-dashboard': (context) => const LlmDashboardScreen(),
      },
      debugShowCheckedModeBanner: false,
    );
  }
}
