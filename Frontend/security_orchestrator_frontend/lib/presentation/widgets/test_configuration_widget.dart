import 'package:flutter/material.dart';
import 'dart:convert';

class TestConfigurationWidget extends StatefulWidget {
  final Function(Map<String, dynamic>) onConfigChanged;

  const TestConfigurationWidget({
    Key? key,
    required this.onConfigChanged,
  }) : super(key: key);

  @override
  State<TestConfigurationWidget> createState() => _TestConfigurationWidgetState();
}

class _TestConfigurationWidgetState extends State<TestConfigurationWidget> {
  final Map<String, dynamic> _config = {};
  final TextEditingController _baseUrlController = TextEditingController();
  final TextEditingController _timeoutController = TextEditingController();
  final TextEditingController _retryCountController = TextEditingController();
  final TextEditingController _parallelTestsController = TextEditingController();
  final TextEditingController _customHeadersController = TextEditingController();
  
  bool _includeSecurityTests = true;
  bool _includePerformanceTests = true;
  bool _includeFunctionalTests = true;
  bool _includeNegativeTests = true;
  bool _realTimeVisualization = true;
  bool _generateReport = true;
  bool _saveResults = true;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Configure test execution parameters:',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 20),

        // Basic Configuration
        _buildSectionCard(
          'Basic Configuration',
          Icons.settings,
          [
            _buildTextField(
              _baseUrlController,
              'Target Base URL',
              'https://api.example.com',
              'The base URL of the API to test',
            ),
            const SizedBox(height: 16),
            _buildTextField(
              _timeoutController,
              'Request Timeout (seconds)',
              '30',
              'Timeout for each API request',
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 16),
            _buildTextField(
              _retryCountController,
              'Retry Count',
              '3',
              'Number of retries for failed requests',
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 16),
            _buildTextField(
              _parallelTestsController,
              'Parallel Test Threads',
              '5',
              'Number of tests to run in parallel',
              keyboardType: TextInputType.number,
            ),
          ],
        ),

        const SizedBox(height: 16),

        // Test Types
        _buildSectionCard(
          'Test Types',
          Icons.testTube,
          [
            _buildCheckboxTile(
              'Include Security Tests',
              _includeSecurityTests,
              'Execute OWASP API security tests',
              Icons.security,
              (value) => setState(() => _includeSecurityTests = value ?? false),
            ),
            const SizedBox(height: 8),
            _buildCheckboxTile(
              'Include Performance Tests',
              _includePerformanceTests,
              'Execute performance and load tests',
              Icons.speed,
              (value) => setState(() => _includePerformanceTests = value ?? false),
            ),
            const SizedBox(height: 8),
            _buildCheckboxTile(
              'Include Functional Tests',
              _includeFunctionalTests,
              'Execute functional business logic tests',
              Icons.functions,
              (value) => setState(() => _includeFunctionalTests = value ?? false),
            ),
            const SizedBox(height: 8),
            _buildCheckboxTile(
              'Include Negative Tests',
              _includeNegativeTests,
              'Execute edge case and error handling tests',
              Icons.error_outline,
              (value) => setState(() => _includeNegativeTests = value ?? false),
            ),
          ],
        ),

        const SizedBox(height: 16),

        // Visualization & Reporting
        _buildSectionCard(
          'Visualization & Reporting',
          Icons.analytics,
          [
            _buildCheckboxTile(
              'Real-time Visualization',
              _realTimeVisualization,
              'Show live test execution visualization',
              Icons.visibility,
              (value) => setState(() => _realTimeVisualization = value ?? false),
            ),
            const SizedBox(height: 8),
            _buildCheckboxTile(
              'Generate Test Report',
              _generateReport,
              'Generate comprehensive test report',
              Icons.description,
              (value) => setState(() => _generateReport = value ?? false),
            ),
            const SizedBox(height: 8),
            _buildCheckboxTile(
              'Save Test Results',
              _saveResults,
              'Save test results to database',
              Icons.save,
              (value) => setState(() => _saveResults = value ?? false),
            ),
          ],
        ),

        const SizedBox(height: 16),

        // Advanced Configuration
        _buildSectionCard(
          'Advanced Configuration',
          Icons.tune,
          [
            _buildTextField(
              _customHeadersController,
              'Custom Headers (JSON)',
              '{"Authorization": "Bearer token", "X-Custom-Header": "value"}',
              'Additional headers to include in requests',
              maxLines: 3,
            ),
          ],
        ),

        const SizedBox(height: 20),

        // Configuration Summary
        _buildConfigSummary(),
      ],
    );
  }

  Widget _buildSectionCard(String title, IconData icon, List<Widget> children) {
    return Card(
      elevation: 2,
      child: ExpansionTile(
        leading: Icon(icon, color: Theme.of(context).primaryColor),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(children: children),
          ),
        ],
      ),
    );
  }

  Widget _buildTextField(
    TextEditingController controller,
    String label,
    String hint,
    String description, {
    TextInputType? keyboardType,
    int maxLines = 1,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        TextFormField(
          controller: controller,
          keyboardType: keyboardType,
          maxLines: maxLines,
          decoration: InputDecoration(
            labelText: label,
            hintText: hint,
            border: const OutlineInputBorder(),
            helperText: description,
            helperMaxLines: 3,
          ),
          onChanged: (value) => _updateConfig(),
        ),
      ],
    );
  }

  Widget _buildCheckboxTile(
    String title,
    bool value,
    String subtitle,
    IconData icon,
    ValueChanged<bool?> onChanged,
  ) {
    return CheckboxListTile(
      title: Text(title),
      subtitle: Text(subtitle),
      value: value,
      onChanged: onChanged,
      secondary: Icon(icon, color: Theme.of(context).primaryColor),
      controlAffinity: ListTileControlAffinity.leading,
      contentPadding: EdgeInsets.zero,
    );
  }

  Widget _buildConfigSummary() {
    return Card(
      color: Theme.of(context).primaryColor.withOpacity(0.1),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  Icons.summary,
                  color: Theme.of(context).primaryColor,
                ),
                const SizedBox(width: 8),
                Text(
                  'Configuration Summary',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    color: Theme.of(context).primaryColor,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Text(
              _generateConfigSummary(),
              style: const TextStyle(fontSize: 14),
            ),
            const SizedBox(height: 12),
            TextButton.icon(
              onPressed: _showConfigJson,
              icon: const Icon(Icons.code),
              label: const Text('Show Configuration JSON'),
              style: TextButton.styleFrom(
                foregroundColor: Theme.of(context).primaryColor,
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _generateConfigSummary() {
    final testTypes = <String>[];
    if (_includeSecurityTests) testTypes.add('Security');
    if (_includePerformanceTests) testTypes.add('Performance');
    if (_includeFunctionalTests) testTypes.add('Functional');
    if (_includeNegativeTests) testTypes.add('Negative');

    return 'Configuration: ${testTypes.join(', ')} tests, '
           'Timeout: ${_timeoutController.text}s, '
           'Parallel: ${_parallelTestsController.text} threads, '
           'Base URL: ${_baseUrlController.text.isNotEmpty ? _baseUrlController.text : 'Not set'}';
  }

  void _updateConfig() {
    _config.clear();
    
    // Basic configuration
    if (_baseUrlController.text.isNotEmpty) {
      _config['baseUrl'] = _baseUrlController.text;
    }
    if (_timeoutController.text.isNotEmpty) {
      _config['timeout'] = int.parse(_timeoutController.text);
    }
    if (_retryCountController.text.isNotEmpty) {
      _config['retryCount'] = int.parse(_retryCountController.text);
    }
    if (_parallelTestsController.text.isNotEmpty) {
      _config['parallelTests'] = int.parse(_parallelTestsController.text);
    }

    // Test types
    _config['testTypes'] = {
      'security': _includeSecurityTests,
      'performance': _includePerformanceTests,
      'functional': _includeFunctionalTests,
      'negative': _includeNegativeTests,
    };

    // Visualization & reporting
    _config['realTimeVisualization'] = _realTimeVisualization;
    _config['generateReport'] = _generateReport;
    _config['saveResults'] = _saveResults;

    // Custom headers
    if (_customHeadersController.text.isNotEmpty) {
      try {
        final headers = jsonDecode(_customHeadersController.text);
        _config['customHeaders'] = headers;
      } catch (e) {
        // Invalid JSON, ignore
      }
    }

    widget.onConfigChanged(_config);
  }

  void _showConfigJson() {
    _updateConfig();
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Test Configuration JSON'),
        content: SingleChildScrollView(
          child: SelectableText(
            jsonEncode(_config, indent: 2),
            style: const TextStyle(fontFamily: 'monospace'),
          ),
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

  @override
  void dispose() {
    _baseUrlController.dispose();
    _timeoutController.dispose();
    _retryCountController.dispose();
    _parallelTestsController.dispose();
    _customHeadersController.dispose();
    super.dispose();
  }
}