import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:security_orchestrator_frontend/main.dart';
import 'package:security_orchestrator_frontend/app.dart';

void main() {
  testWidgets('Security Orchestrator app builds without errors', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const ProviderScope(child: MyApp()));

    // Verify that our app builds successfully.
    expect(find.text('Security Orchestrator'), findsOneWidget);
    expect(find.byType(MaterialApp), findsOneWidget);
  });
}
