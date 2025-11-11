import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:security_orchestrator_frontend/data/models/owasp_dto.dart';
import 'package:security_orchestrator_frontend/data/models/api_response.dart';
import 'package:security_orchestrator_frontend/data/services/testing_service.dart';
import 'dart:convert';

class OwaspApiSecurityTester extends ConsumerStatefulWidget {
  const OwaspApiSecurityTester({super.key});

  @override
  ConsumerState<OwaspApiSecurityTester> createState() => _OwaspApiSecurityTesterState();
}

class _OwaspApiSecurityTesterState extends ConsumerState<OwaspApiSecurityTester> {
  String _testingStatus = 'not_started';
  int _currentStep = 0;
  int _progress = 0;
  String _currentMessage = '';
  OwaspTestResults? _testResults;
  bool _isPolling = false;

  // OWASP Testing Steps Configuration
  final List<OwaspStep> _owaspSteps = [
    OwaspStep(
      id: 1,
      name: 'BPMN Analysis',
      description: 'Analyzing BPMN processes with CodeLlama 7B',
      icon: Icons.description,
    ),
    OwaspStep(
      id: 2,
      name: 'OpenAPI Analysis',
      description: 'Analyzing OpenAPI specification for security risks',
      icon: Icons.analytics,
    ),
    OwaspStep(
      id: 3,
      name: 'OWASP Tests Generation',
      description: 'Generating OWASP API Security Top 10 tests',
      icon: Icons.security,
    ),
    OwaspStep(
      id: 4,
      name: 'Test Execution',
      description: 'Executing generated security tests',
      icon: Icons.flash_on,
    ),
    OwaspStep(
      id: 5,
      name: 'Report Generation',
      description: 'Generating comprehensive security report',
      icon: Icons.download,
    ),
  ];

  @override
  void initState() {
    super.initState();
    _fetchOWASPStatus();
  }

  Future<void> _fetchOWASPStatus() async {
    try {
      final testingService = TestingService();
      final response = await testingService.getOwaspStatus();
      
      if (response is Map<String, dynamic> && response['success'] == true) {
        setState(() {
          final status = response['data'];
          _testingStatus = status['status'] ?? 'unknown';
          _currentStep = status['currentStep'] ?? 0;
          _progress = status['progress'] ?? 0;
          _currentMessage = status['message'] ?? '';
        });
      }
    } catch (e) {
      debugPrint('Error fetching OWASP status: $e');
    }
  }

  Future<void> _fetchOWASPResults() async {
    try {
      final testingService = TestingService();
      final response = await testingService.getOwaspResults();
      
      if (response is Map<String, dynamic> && response['success'] == true) {
        setState(() {
          // Mock results for now
          _testResults = null;
        });
      }
    } catch (e) {
      debugPrint('Error fetching OWASP results: $e');
    }
  }

  Future<void> _startOWASPTesting() async {
    try {
      setState(() {
        _isPolling = true;
        _testResults = null;
        _testingStatus = 'starting';
      });

      final testingService = TestingService();
      final response = await testingService.startOwaspTesting();
      
      if (response is Map<String, dynamic> && response['success'] == true) {
        _startPolling();
        await _fetchOWASPStatus();
      } else {
        final message = response is Map<String, dynamic> ? response['message'] ?? 'Unknown error' : 'Unknown error';
        _showErrorSnackBar('Failed to start OWASP testing: $message');
      }
    } catch (e) {
      debugPrint('Error starting OWASP testing: $e');
      _showErrorSnackBar('Error starting OWASP testing: $e');
      setState(() {
        _isPolling = false;
        _testingStatus = 'error';
      });
    }
  }

  void _startPolling() {
    const pollInterval = Duration(seconds: 2);
    
    Future.delayed(pollInterval, () async {
      if (_isPolling) {
        await _fetchOWASPStatus();
        
        if (_testingStatus == 'completed' || _testingStatus == 'error') {
          setState(() {
            _isPolling = false;
          });
          
          if (_testingStatus == 'completed') {
            await _fetchOWASPResults();
          }
        } else {
          _startPolling();
        }
      }
    });
  }

  void _showErrorSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
        action: SnackBarAction(
          label: 'Dismiss',
          textColor: Colors.white,
          onPressed: () {},
        ),
      ),
    );
  }

  void _downloadReport() {
    if (_testResults != null) {
      final jsonString = jsonEncode(_testResults!.toJson());
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Report JSON: Use browser dev tools to download'),
          backgroundColor: Colors.blue,
        ),
      );
    }
  }

  Widget _getStepStatusWidget(int stepId) {
    if (_currentStep > stepId) {
      return Icon(Icons.check_circle, color: Colors.green[600], size: 24);
    } else if (_currentStep == stepId) {
      return SizedBox(
        width: 24,
        height: 24,
        child: CircularProgressIndicator(strokeWidth: 2),
      );
    } else {
      return Icon(Icons.radio_button_unchecked, color: Colors.grey, size: 24);
    }
  }

  Widget _buildOWASPResults() {
    if (_testResults == null) return SizedBox.shrink();

    final results = _testResults!;
    
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Summary Cards
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Test Summary',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 16),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildSummaryCard(
                      'Total Tests',
                      results.summary.totalTests.toString(),
                      Colors.blue,
                    ),
                    _buildSummaryCard(
                      'Vulnerabilities',
                      results.summary.vulnerabilitiesFound.toString(),
                      Colors.red,
                    ),
                    _buildSummaryCard(
                      'Vulnerability Rate',
                      '${results.summary.vulnerabilityRate}%',
                      Colors.orange,
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Text('Overall Risk Level: '),
                    Chip(
                      label: Text(
                        results.summary.overallRiskLevel,
                        style: const TextStyle(color: Colors.white),
                      ),
                      backgroundColor: _getRiskLevelColor(results.summary.overallRiskLevel),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),

        // OWASP Top 10 Results
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'OWASP API Security Top 10',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 16),
                ...results.owaspTop10.map((category) => _buildOWASPCategory(category)),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),

        // Vulnerabilities
        if (results.vulnerabilities.isNotEmpty)
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Detected Vulnerabilities',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  ...results.vulnerabilities.map((vuln) => _buildVulnerabilityCard(vuln)),
                ],
              ),
            ),
          ),
        
        if (results.vulnerabilities.isEmpty)
          Card(
            child: Padding(
              padding: const EdgeInsets.all(32.0),
              child: Column(
                children: [
                  Icon(Icons.check_circle, size: 64, color: Colors.green[600]),
                  const SizedBox(height: 16),
                  Text(
                    'No Vulnerabilities Detected',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'All security tests passed successfully!',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: Colors.grey[600],
                    ),
                  ),
                ],
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildSummaryCard(String title, String value, Color color) {
    return Column(
      children: [
        Text(
          value,
          style: Theme.of(context).textTheme.headlineMedium?.copyWith(
            color: color,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          title,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
            color: Colors.grey[600],
          ),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildOWASPCategory(OwaspCategory category) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    category.category,
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    category.description,
                    style: TextStyle(
                      color: Colors.grey[600],
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 12),
            Column(
              children: [
                Chip(
                  label: Text('${category.testCount} tests'),
                  backgroundColor: Colors.blue[100],
                  visualDensity: VisualDensity.compact,
                ),
                const SizedBox(height: 4),
                Chip(
                  label: Text(
                    '${category.vulnerabilitiesFound} found',
                    style: const TextStyle(color: Colors.white),
                  ),
                  backgroundColor: category.vulnerabilitiesFound > 0 
                    ? Colors.red 
                    : Colors.green,
                  visualDensity: VisualDensity.compact,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildVulnerabilityCard(OwaspVulnerability vuln) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    vuln.title,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Colors.red,
                    ),
                  ),
                ),
                Chip(
                  label: Text(vuln.severity),
                  backgroundColor: _getSeverityColor(vuln.severity),
                  visualDensity: VisualDensity.compact,
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              vuln.description,
              style: TextStyle(
                color: Colors.grey[600],
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'OWASP Category: ${vuln.owaspCategory}',
              style: TextStyle(
                color: Colors.grey[500],
                fontSize: 12,
              ),
            ),
            Text(
              'Endpoint: ${vuln.endpoint}',
              style: TextStyle(
                color: Colors.grey[500],
                fontSize: 12,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Color _getRiskLevelColor(String riskLevel) {
    switch (riskLevel.toLowerCase()) {
      case 'high':
        return Colors.red;
      case 'medium':
        return Colors.orange;
      case 'low':
        return Colors.green;
      default:
        return Colors.grey;
    }
  }

  Color _getSeverityColor(String severity) {
    switch (severity.toLowerCase()) {
      case 'critical':
        return Colors.red[800]!;
      case 'high':
        return Colors.red;
      case 'medium':
        return Colors.orange;
      case 'low':
        return Colors.yellow[600]!;
      default:
        return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('OWASP API Security Testing'),
        backgroundColor: Colors.red[600],
        foregroundColor: Colors.white,
        actions: [
          if (_testResults != null)
            IconButton(
              icon: const Icon(Icons.download),
              onPressed: _downloadReport,
              tooltip: 'Download Report',
            ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(Icons.security, color: Colors.red[600], size: 32),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'OWASP API Security Testing',
                                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                              const SizedBox(height: 4),
                              Text(
                                'Comprehensive security analysis of BPMN processes and OpenAPI specifications',
                                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                  color: Colors.grey[600],
                                ),
                              ),
                            ],
                          ),
                        ),
                        ElevatedButton.icon(
                          onPressed: _testingStatus == 'running' 
                            ? null 
                            : _startOWASPTesting,
                          icon: const Icon(Icons.play_arrow),
                          label: const Text('Start Security Testing'),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.red[600],
                            foregroundColor: Colors.white,
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Testing Progress
            if (_testingStatus == 'running' || _testingStatus == 'starting')
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Security Testing Progress',
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 16),
                      
                      // Overall Progress
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              const Text('Overall Progress'),
                              Text('$_progress%'),
                            ],
                          ),
                          const SizedBox(height: 8),
                          LinearProgressIndicator(
                            value: _progress / 100,
                            backgroundColor: Colors.grey[300],
                            valueColor: AlwaysStoppedAnimation<Color>(Colors.red[600]!),
                          ),
                        ],
                      ),
                      
                      const SizedBox(height: 16),
                      
                      // Current Step
                      if (_currentMessage.isNotEmpty)
                        Text(
                          'Current Step: $_currentMessage',
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),

                      const SizedBox(height: 16),

                      // Step-by-step progress
                      Column(
                        children: _owaspSteps.map((step) => 
                          Card(
                            child: ListTile(
                              leading: _getStepStatusWidget(step.id),
                              title: Text(step.name),
                              subtitle: Text(step.description),
                            ),
                          )
                        ).toList(),
                      ),
                    ],
                  ),
                ),
              ),

            // Results
            if (_testResults != null) ...[
              const SizedBox(height: 16),
              _buildOWASPResults(),
            ],

            // Error State
            if (_testingStatus == 'error')
              Card(
                color: Colors.red[50],
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Row(
                    children: [
                      Icon(Icons.error, color: Colors.red[600]),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Text(
                          'OWASP API Security Testing failed. Please check the server logs for more details.',
                          style: TextStyle(color: Colors.red[800]),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}

class OwaspStep {
  final int id;
  final String name;
  final String description;
  final IconData icon;

  const OwaspStep({
    required this.id,
    required this.name,
    required this.description,
    required this.icon,
  });
}