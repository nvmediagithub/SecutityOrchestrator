import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:security_orchestrator_frontend/core/constants/api_constants.dart';
import 'package:security_orchestrator_frontend/data/models/api_response.dart';
import 'package:security_orchestrator_frontend/data/services/testing_service.dart';
import 'package:security_orchestrator_frontend/presentation/providers/testing_provider.dart';
import 'package:security_orchestrator_frontend/presentation/widgets/owasp_categories_widget.dart';
import 'package:security_orchestrator_frontend/presentation/widgets/bpmn_process_selector.dart';
import 'package:security_orchestrator_frontend/presentation/widgets/openapi_input_widget.dart';
import 'package:security_orchestrator_frontend/presentation/widgets/test_configuration_widget.dart';
import 'package:security_orchestrator_frontend/presentation/widgets/progress_indicator_widget.dart';

class ApiTestingDashboard extends StatefulWidget {
  const ApiTestingDashboard({Key? key}) : super(key: key);

  @override
  State<ApiTestingDashboard> createState() => _ApiTestingDashboardState();
}

class _ApiTestingDashboardState extends State<ApiTestingDashboard> {
  final TestingService _testingService = TestingService();
  String? _selectedOpenApiUrl;
  String? _selectedBpmnProcess;
  List<String> _selectedOwaspCategories = [];
  Map<String, dynamic> _testConfig = {};
  bool _isLoading = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('API Security Testing Dashboard'),
        backgroundColor: Theme.of(context).primaryColor,
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: _openSettings,
          ),
          IconButton(
            icon: const Icon(Icons.help),
            onPressed: _showHelp,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CustomProgressIndicator())
          : SingleChildScrollView(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildHeaderCard(),
                  const SizedBox(height: 20),
                  _buildOpenApiSection(),
                  const SizedBox(height: 20),
                  _buildBpmnSection(),
                  const SizedBox(height: 20),
                  _buildOwaspCategoriesSection(),
                  const SizedBox(height: 20),
                  _buildTestConfigSection(),
                  const SizedBox(height: 20),
                  _buildActionButtons(),
                ],
              ),
            ),
    );
  }

  Widget _buildHeaderCard() {
    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  Icons.security,
                  size: 32,
                  color: Theme.of(context).primaryColor,
                ),
                const SizedBox(width: 12),
                const Text(
                  'Comprehensive API Security Testing',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            const Text(
              'This tool provides comprehensive API security testing by analyzing OpenAPI specifications, '
              'integrating BPMN process workflows, and applying OWASP API Security Top 10 guidelines.',
              style: TextStyle(
                fontSize: 16,
                color: Colors.grey,
              ),
            ),
            const SizedBox(height: 16),
            _buildFeatureList(),
          ],
        ),
      ),
    );
  }

  Widget _buildFeatureList() {
    final features = [
      {'icon': Icons.analytics, 'title': 'OpenAPI Analysis', 'desc': 'Parse and analyze API specifications'},
      {'icon': Icons.account_tree, 'title': 'BPMN Integration', 'desc': 'Integrate with business process workflows'},
      {'icon': Icons.shield, 'title': 'OWASP Security', 'desc': 'Apply OWASP API Security Top 10'},
      {'icon': Icons.psychology, 'title': 'LLM Analysis', 'desc': 'AI-powered inconsistency detection'},
      {'icon': Icons.play_circle, 'title': 'Real-time Testing', 'desc': 'Execute tests with live visualization'},
    ];

    return Wrap(
      spacing: 16,
      runSpacing: 12,
      children: features.map((feature) {
        return Container(
          width: 200,
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: Colors.grey[50],
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: Colors.grey[300]!),
          ),
          child: Row(
            children: [
              Icon(
                feature['icon'] as IconData,
                size: 20,
                color: Theme.of(context).primaryColor,
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      feature['title'] as String,
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                    Text(
                      feature['desc'] as String,
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.grey[600],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }

  Widget _buildOpenApiSection() {
    return Card(
      elevation: 2,
      child: ExpansionTile(
        leading: const Icon(Icons.description, color: Colors.blue),
        title: const Text(
          '1. OpenAPI Specification',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(
          _selectedOpenApiUrl ?? 'No OpenAPI specification selected',
          style: TextStyle(
            color: _selectedOpenApiUrl != null ? Colors.green : Colors.grey,
          ),
        ),
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: OpenApiInputWidget(
              onOpenApiSelected: (url) {
                setState(() {
                  _selectedOpenApiUrl = url;
                });
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBpmnSection() {
    return Card(
      elevation: 2,
      child: ExpansionTile(
        leading: const Icon(Icons.account_tree, color: Colors.green),
        title: const Text(
          '2. BPMN Process Integration',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(
          _selectedBpmnProcess ?? 'No BPMN process selected',
          style: TextStyle(
            color: _selectedBpmnProcess != null ? Colors.green : Colors.grey,
          ),
        ),
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: BpmnProcessSelector(
              onProcessSelected: (processId) {
                setState(() {
                  _selectedBpmnProcess = processId;
                });
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOwaspCategoriesSection() {
    return Card(
      elevation: 2,
      child: ExpansionTile(
        leading: const Icon(Icons.shield, color: Colors.orange),
        title: const Text(
          '3. OWASP API Security Categories',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(
          _selectedOwaspCategories.isNotEmpty
              ? '${_selectedOwaspCategories.length} categories selected'
              : 'No OWASP categories selected',
          style: TextStyle(
            color: _selectedOwaspCategories.isNotEmpty ? Colors.green : Colors.grey,
          ),
        ),
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: OwaspCategoriesWidget(
              onCategoriesSelected: (categories) {
                setState(() {
                  _selectedOwaspCategories = categories;
                });
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTestConfigSection() {
    return Card(
      elevation: 2,
      child: ExpansionTile(
        leading: const Icon(Icons.settings, color: Colors.purple),
        title: const Text(
          '4. Test Configuration',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: const Text('Configure test execution parameters'),
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TestConfigurationWidget(
              onConfigChanged: (config) {
                setState(() {
                  _testConfig = config;
                });
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActionButtons() {
    return Card(
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton.icon(
                  onPressed: _analyzeOpenApi,
                  icon: const Icon(Icons.analytics),
                  label: const Text('Analyze OpenAPI'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.blue,
                    foregroundColor: Colors.white,
                  ),
                ),
                ElevatedButton.icon(
                  onPressed: _analyzeBpmn,
                  icon: const Icon(Icons.account_tree),
                  label: const Text('Analyze BPMN'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green,
                    foregroundColor: Colors.white,
                ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton.icon(
                  onPressed: _generateTests,
                  icon: const Icon(Icons.generation_task),
                  label: const Text('Generate Tests'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.orange,
                    foregroundColor: Colors.white,
                  ),
                ),
                ElevatedButton.icon(
                  onPressed: _executeE2E,
                  icon: const Icon(Icons.play_circle),
                  label: const Text('Execute E2E Tests'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red,
                    foregroundColor: Colors.white,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: _validateAndStart,
                icon: const Icon(Icons.check_circle),
                label: const Text('Start Comprehensive Testing'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Theme.of(context).primaryColor,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 12),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _openSettings() {
    // Navigate to settings or open settings dialog
  }

  void _showHelp() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('API Testing Help'),
        content: const Text(
          'This tool provides comprehensive API security testing. '
          '1. Upload or provide an OpenAPI specification URL\n'
          '2. Select a BPMN process for workflow integration\n'
          '3. Choose OWASP API Security categories to test\n'
          '4. Configure test parameters\n'
          '5. Execute comprehensive testing',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }

  void _analyzeOpenApi() async {
    if (_selectedOpenApiUrl == null) {
      _showError('Please select an OpenAPI specification first');
      return;
    }

    setState(() => _isLoading = true);

    try {
      final result = await _testingService.analyzeOpenApi(
        _selectedOpenApiUrl!,
        null, // baseUrl
      );

      if (result.success) {
        _showSuccess('OpenAPI analysis completed successfully');
        // Navigate to analysis results
      } else {
        _showError('OpenAPI analysis failed: ${result.error}');
      }
    } catch (e) {
      _showError('Error analyzing OpenAPI: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _analyzeBpmn() async {
    if (_selectedBpmnProcess == null) {
      _showError('Please select a BPMN process first');
      return;
    }

    setState(() => _isLoading = true);

    try {
      final result = await _testingService.analyzeBpmn(_selectedBpmnProcess!);

      if (result.success) {
        _showSuccess('BPMN analysis completed successfully');
        // Navigate to analysis results
      } else {
        _showError('BPMN analysis failed: ${result.error}');
      }
    } catch (e) {
      _showError('Error analyzing BPMN: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _generateTests() async {
    if (_selectedOpenApiUrl == null || _selectedBpmnProcess == null) {
      _showError('Please select OpenAPI and BPMN first');
      return;
    }

    if (_selectedOwaspCategories.isEmpty) {
      _showError('Please select at least one OWASP category');
      return;
    }

    setState(() => _isLoading = true);

    try {
      final result = await _testingService.generateTests(
        _selectedOpenApiUrl!,
        _selectedBpmnProcess!,
        _selectedOwaspCategories,
      );

      if (result.success) {
        _showSuccess('OWASP tests generated successfully');
        // Navigate to test results
      } else {
        _showError('Test generation failed: ${result.error}');
      }
    } catch (e) {
      _showError('Error generating tests: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _executeE2E() async {
    if (_selectedOpenApiUrl == null || _selectedBpmnProcess == null) {
      _showError('Please select OpenAPI and BPMN first');
      return;
    }

    setState(() => _isLoading = true);

    try {
      final result = await _testingService.executeE2ETests(
        _selectedOpenApiUrl!,
        _selectedBpmnProcess!,
        _testConfig,
      );

      if (result.success) {
        _showSuccess('End-to-end testing started successfully');
        // Navigate to real-time testing visualization
      } else {
        _showError('E2E testing failed: ${result.error}');
      }
    } catch (e) {
      _showError('Error executing E2E tests: $e');
    } finally {
      setState(() => _isLoading = false);
    }
  }

  void _validateAndStart() {
    final issues = <String>[];

    if (_selectedOpenApiUrl == null) {
      issues.add('OpenAPI specification is required');
    }
    if (_selectedBpmnProcess == null) {
      issues.add('BPMN process is required');
    }
    if (_selectedOwaspCategories.isEmpty) {
      issues.add('At least one OWASP category must be selected');
    }

    if (issues.isNotEmpty) {
      _showError('Please fix the following issues:\n${issues.join('\n')}');
      return;
    }

    _executeE2E();
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
        duration: const Duration(seconds: 4),
      ),
    );
  }

  void _showSuccess(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.green,
        duration: const Duration(seconds: 3),
      ),
    );
  }
}