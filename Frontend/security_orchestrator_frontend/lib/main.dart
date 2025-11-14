import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:file_picker/file_picker.dart';

import 'shared/theme/app_theme.dart';
import 'presentation/screens/home_screen.dart';

void main() {
  // Initialize file picker for cross-platform file handling
  FilePicker.platform;

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
        // Feature-first routing will be added here
        // Example: '/user-flow': (context) => const UserFlowMainScreen(),
      },
      debugShowCheckedModeBanner: false,
    );
  }
}
