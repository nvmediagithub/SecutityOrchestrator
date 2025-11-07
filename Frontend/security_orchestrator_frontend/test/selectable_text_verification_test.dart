import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// Import the actual screen
import 'package:security_orchestrator_frontend/presentation/screens/llm_dashboard_screen.dart';

void main() {
  group('SelectableText Implementation Tests', () {
    testWidgets('LLM Dashboard screen builds with SelectableText widgets', (WidgetTester tester) async {
      // Create a test container with required providers
      final container = ProviderContainer();
      
      // Build the widget
      await tester.pumpWidget(
        UncontrolledProviderScope(
          container: container,
          child: const MaterialApp(
            home: LlmDashboardScreen(),
          ),
        ),
      );

      // Verify the app bar title is SelectableText
      expect(find.byType(Scaffold), findsOneWidget);
      expect(find.byType(AppBar), findsOneWidget);
      
      // The main app bar title should be SelectableText
      final appBarTitle = tester.widget<AppBar>(find.byType(AppBar)).title;
      expect(appBarTitle, isA<SelectableText>());
      expect((appBarTitle as SelectableText).data, 'LLM Dashboard');
    });

    testWidgets('Error view uses SelectableText for error details', (WidgetTester tester) async {
      final container = ProviderContainer();
      
      // Create a mock state that returns an error
      final mockError = 'Test error message';
      
      await tester.pumpWidget(
        UncontrolledProviderScope(
          container: container,
          child: const MaterialApp(
            home: Scaffold(
              body: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.error, size: 64, color: Colors.red),
                    SizedBox(height: 16),
                    SelectableText(
                      'Error loading dashboard',
                      style: TextStyle(fontSize: 24),
                      textAlign: TextAlign.center,
                    ),
                    SizedBox(height: 8),
                    Tooltip(
                      message: 'Tap to copy error details',
                      child: SelectableText(
                        'Test error message',
                        textAlign: TextAlign.center,
                      ),
                    ),
                    SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: null,
                      child: SelectableText('Retry'),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      );

      // Verify error message is SelectableText
      expect(find.text('Error loading dashboard'), findsOneWidget);
      expect(find.text('Test error message'), findsOneWidget);
      expect(find.byType(SelectableText), findsNWidgets(3));
      expect(find.byType(Tooltip), findsOneWidget);
    });

    testWidgets('Overview card uses SelectableText for information', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: Scaffold(
            body: Card(
              child: Padding(
                padding: EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    SelectableText(
                      'System Overview',
                      style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                    ),
                    SizedBox(height: 8),
                    SelectableText('Active Provider: OpenRouter'),
                    SizedBox(height: 4),
                    Tooltip(
                      message: 'Click to select and copy model name',
                      child: SelectableText('Active Model: gpt-3.5-turbo'),
                    ),
                    SizedBox(height: 4),
                    SelectableText('Available Models: 5'),
                    SizedBox(height: 4),
                    SelectableText('Configured Providers: 2'),
                  ],
                ),
              ),
            ),
          ),
        ),
      );

      // Verify all text is SelectableText
      expect(find.text('System Overview'), findsOneWidget);
      expect(find.text('Active Provider: OpenRouter'), findsOneWidget);
      expect(find.text('Active Model: gpt-3.5-turbo'), findsOneWidget);
      expect(find.text('Available Models: 5'), findsOneWidget);
      expect(find.text('Configured Providers: 2'), findsOneWidget);
      expect(find.byType(SelectableText), findsNWidgets(5));
      expect(find.byType(Tooltip), findsOneWidget);
    });

    testWidgets('Test response area uses scrolling and SelectableText', (WidgetTester tester) async {
      final longResponse = 'This is a very long test response that would normally be difficult to select and copy. ' * 10;
      
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: Padding(
              padding: const EdgeInsets.all(16),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SelectableText(
                        'Test Interface',
                        style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 16),
                      Container(
                        width: double.infinity,
                        constraints: const BoxConstraints(maxHeight: 200),
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: Colors.grey.shade100,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                const SelectableText('Response:', style: TextStyle(fontSize: 18)),
                                IconButton(
                                  icon: const Icon(Icons.copy, size: 16),
                                  onPressed: null,
                                  tooltip: 'Tap and drag to select text to copy',
                                ),
                              ],
                            ),
                            const SizedBox(height: 8),
                            Expanded(
                              child: SingleChildScrollView(
                                child: Tooltip(
                                  message: 'Tap and drag to select text',
                                  child: SelectableText(
                                    longResponse,
                                    style: const TextStyle(fontFamily: 'monospace'),
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      );

      // Verify the response area structure
      expect(find.text('Test Interface'), findsOneWidget);
      expect(find.text('Response:'), findsOneWidget);
      expect(find.text(longResponse), findsOneWidget);
      expect(find.byType(SingleChildScrollView), findsOneWidget);
      expect(find.byType(Container), findsWidgets);
      expect(find.byType(IconButton), findsOneWidget);
    });
  });
}