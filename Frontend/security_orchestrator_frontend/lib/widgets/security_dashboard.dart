import 'package:flutter/material.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:fl_chart/fl_chart.dart';
import 'dart:convert';
import 'dart:async';

class SecurityDashboard extends StatefulWidget {
  final WebSocketChannel? channel;
  
  const SecurityDashboard({Key? key, this.channel}) : super(key: key);
  
  @override
  _SecurityDashboardState createState() => _SecurityDashboardState();
}

class _SecurityDashboardState extends State<SecurityDashboard>
    with TickerProviderStateMixin {
  
  late TabController _tabController;
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;
  
  // Real-time data
  List<SecurityVulnerability> vulnerabilities = [];
  List<TestResult> testResults = [];
  List<BpmnProcessStep> bpmnSteps = [];
  Map<String, dynamic> llmAnalysisResults = {};
  
  // Statistics
  int totalVulnerabilities = 0;
  int criticalVulnerabilities = 0;
  int testsPassed = 0;
  int testsFailed = 0;
  
  StreamSubscription? _streamSubscription;
  
  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );
    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: _animationController, curve: Curves.easeIn),
    );
    
    _initializeWebSocket();
    _loadInitialData();
    _animationController.forward();
  }
  
  @override
  void dispose() {
    _tabController.dispose();
    _animationController.dispose();
    _streamSubscription?.cancel();
    super.dispose();
  }
  
  void _initializeWebSocket() {
    if (widget.channel != null) {
      _streamSubscription = widget.channel!.stream.listen(
        (data) {
          _handleRealTimeUpdate(json.decode(data));
        },
        onError: (error) => print('WebSocket error: $error'),
        onDone: () => print('WebSocket connection closed'),
      );
    }
  }
  
  void _handleRealTimeUpdate(Map<String, dynamic> data) {
    setState(() {
      switch (data['type']) {
        case 'vulnerability':
          _updateVulnerabilities(data['payload']);
          break;
        case 'test_result':
          _updateTestResults(data['payload']);
          break;
        case 'bpmn_step':
          _updateBpmnSteps(data['payload']);
          break;
        case 'llm_analysis':
          _updateLLMAnalysis(data['payload']);
          break;
      }
    });
  }
  
  void _updateVulnerabilities(Map<String, dynamic> vulnData) {
    final vuln = SecurityVulnerability.fromJson(vulnData);
    vulnerabilities.insert(0, vuln);
    if (vulnerabilities.length > 100) {
      vulnerabilities.removeLast();
    }
    _updateStatistics();
  }
  
  void _updateTestResults(Map<String, dynamic> testData) {
    final test = TestResult.fromJson(testData);
    testResults.insert(0, test);
    if (testResults.length > 50) {
      testResults.removeLast();
    }
    _updateStatistics();
  }
  
  void _updateBpmnSteps(Map<String, dynamic> stepData) {
    final step = BpmnProcessStep.fromJson(stepData);
    final existingIndex = bpmnSteps.indexWhere((s) => s.id == step.id);
    if (existingIndex >= 0) {
      bpmnSteps[existingIndex] = step;
    } else {
      bpmnSteps.add(step);
    }
  }
  
  void _updateLLMAnalysis(Map<String, dynamic> analysisData) {
    llmAnalysisResults.addAll(analysisData);
  }
  
  void _updateStatistics() {
    totalVulnerabilities = vulnerabilities.length;
    criticalVulnerabilities = vulnerabilities.where((v) => v.severity == 'CRITICAL').length;
    testsPassed = testResults.where((t) => t.passed).length;
    testsFailed = testResults.where((t) => !t.passed).length;
  }
  
  void _loadInitialData() {
    // Load sample data for demonstration
    setState(() {
      vulnerabilities = [
        SecurityVulnerability(
          id: '1',
          name: 'Broken Object Level Authorization',
          severity: 'HIGH',
          category: 'API1',
          description: 'Potential IDOR vulnerability in user endpoint',
          endpoint: '/api/users/{id}',
          confidence: 0.85,
        ),
        SecurityVulnerability(
          id: '2',
          name: 'Excessive Data Exposure',
          severity: 'MEDIUM',
          category: 'API3',
          description: 'Sensitive data exposed in user profile response',
          endpoint: '/api/profile',
          confidence: 0.72,
        ),
      ];
      
      testResults = [
        TestResult(
          id: '1',
          testName: 'Authorization Test',
          passed: true,
          executionTime: 250,
          payload: 'GET /api/admin/users',
        ),
        TestResult(
          id: '2',
          testName: 'SQL Injection Test',
          passed: false,
          executionTime: 180,
          payload: "GET /api/search?q='; DROP TABLE users; --",
        ),
      ];
      
      bpmnSteps = [
        BpmnProcessStep(
          id: '1',
          name: 'Parse OpenAPI',
          status: 'completed',
          executionTime: 1200,
          timestamp: DateTime.now(),
        ),
        BpmnProcessStep(
          id: '2',
          name: 'LLM Security Analysis',
          status: 'running',
          executionTime: 0,
          timestamp: DateTime.now(),
        ),
        BpmnProcessStep(
          id: '3',
          name: 'Generate Tests',
          status: 'pending',
          executionTime: 0,
          timestamp: DateTime.now(),
        ),
      ];
      
      _updateStatistics();
    });
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Security Orchestrator Dashboard'),
        backgroundColor: Colors.blueGrey[800],
        foregroundColor: Colors.white,
        bottom: TabBar(
          controller: _tabController,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white70,
          indicatorColor: Colors.orange,
          tabs: const [
            Tab(icon: Icon(Icons.dashboard), text: 'Overview'),
            Tab(icon: Icon(Icons.security), text: 'Vulnerabilities'),
            Tab(icon: Icon(Icons.play_circle), text: 'BPMN Process'),
            Tab(icon: Icon(Icons.psychology), text: 'LLM Analysis'),
          ],
        ),
      ),
      body: FadeTransition(
        opacity: _fadeAnimation,
        child: TabBarView(
          controller: _tabController,
          children: [
            _buildOverviewTab(),
            _buildVulnerabilitiesTab(),
            _buildBpmnTab(),
            _buildLLMAnalysisTab(),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _startNewAnalysis,
        backgroundColor: Colors.orange,
        child: const Icon(Icons.play_arrow),
      ),
    );
  }
  
  Widget _buildOverviewTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Statistics Cards
          Row(
            children: [
              Expanded(child: _buildStatCard('Total Vulnerabilities', totalVulnerabilities, Colors.red)),
              const SizedBox(width: 16),
              Expanded(child: _buildStatCard('Critical Issues', criticalVulnerabilities, Colors.red[800]!)),
            ],
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(child: _buildStatCard('Tests Passed', testsPassed, Colors.green)),
              const SizedBox(width: 16),
              Expanded(child: _buildStatCard('Tests Failed', testsFailed, Colors.red)),
            ],
          ),
          const SizedBox(height: 24),
          
          // Vulnerability Distribution Chart
          Text(
            'Vulnerability Distribution',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          Container(
            height: 200,
            child: _buildVulnerabilityChart(),
          ),
          const SizedBox(height: 24),
          
          // Recent Activity
          Text(
            'Recent Security Events',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          _buildRecentActivityList(),
        ],
      ),
    );
  }
  
  Widget _buildVulnerabilitiesTab() {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: vulnerabilities.length,
      itemBuilder: (context, index) {
        final vuln = vulnerabilities[index];
        return Card(
          margin: const EdgeInsets.only(bottom: 12),
          child: ListTile(
            leading: _getSeverityIcon(vuln.severity),
            title: Text(vuln.name),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(vuln.description),
                Text('Endpoint: ${vuln.endpoint}'),
                Text('Confidence: ${(vuln.confidence * 100).toStringAsFixed(1)}%'),
              ],
            ),
            trailing: Text(
              vuln.severity,
              style: TextStyle(
                color: _getSeverityColor(vuln.severity),
                fontWeight: FontWeight.bold,
              ),
            ),
            onTap: () => _showVulnerabilityDetails(vuln),
          ),
        );
      },
    );
  }
  
  Widget _buildBpmnTab() {
    return Column(
      children: [
        // BPMN Process Visualization
        Expanded(
          flex: 2,
          child: Container(
            margin: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              border: Border.all(color: Colors.grey),
              borderRadius: BorderRadius.circular(8),
            ),
            child: _buildBpmnVisualization(),
          ),
        ),
        
        // Process Steps Timeline
        Expanded(
          flex: 1,
          child: Container(
            margin: const EdgeInsets.symmetric(horizontal: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Process Steps',
                  style: Theme.of(context).textTheme.headlineSmall,
                ),
                const SizedBox(height: 8),
                Expanded(
                  child: ListView.builder(
                    itemCount: bpmnSteps.length,
                    itemBuilder: (context, index) {
                      final step = bpmnSteps[index];
                      return ListTile(
                        leading: _getStepStatusIcon(step.status),
                        title: Text(step.name),
                        subtitle: Text('${step.executionTime}ms'),
                        trailing: Text(
                          step.status.toUpperCase(),
                          style: TextStyle(
                            color: _getStepStatusColor(step.status),
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
  
  Widget _buildLLMAnalysisTab() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'CodeLlama Analysis Results',
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          
          // LLM Performance Metrics
          Row(
            children: [
              Expanded(child: _buildLLMMetricCard('Response Time', '2.3s', Icons.timer)),
              const SizedBox(width: 16),
              Expanded(child: _buildLLMMetricCard('Confidence', '87%', Icons.psychology)),
            ],
          ),
          const SizedBox(height: 16),
          
          // Analysis Results
          if (llmAnalysisResults.isNotEmpty) ...[
            Text(
              'Detailed Analysis',
              style: Theme.of(context).textTheme.headlineSmall,
            ),
            const SizedBox(height: 16),
            _buildAnalysisResults(),
          ] else
            const Center(
              child: Text('No LLM analysis results available'),
            ),
        ],
      ),
    );
  }
  
  Widget _buildStatCard(String title, int value, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Text(
              value.toString(),
              style: TextStyle(
                fontSize: 32,
                fontWeight: FontWeight.bold,
                color: color,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              title,
              style: const TextStyle(fontSize: 14),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
  
  Widget _buildVulnerabilityChart() {
    final severityCounts = <String, int>{};
    for (final vuln in vulnerabilities) {
      severityCounts[vuln.severity] = (severityCounts[vuln.severity] ?? 0) + 1;
    }
    
    return PieChart(
      PieChartData(
        sections: severityCounts.entries.map((entry) {
          final color = _getSeverityColor(entry.key);
          return PieChartSectionData(
            value: entry.value.toDouble(),
            title: '${entry.value}',
            color: color,
            radius: 60,
          );
        }).toList(),
      ),
    );
  }
  
  Widget _buildRecentActivityList() {
    return Column(
      children: [
        ...vulnerabilities.take(5).map((vuln) => ListTile(
              leading: const Icon(Icons.warning, color: Colors.orange),
              title: Text(vuln.name),
              subtitle: Text('${vuln.category} - ${vuln.severity}'),
              trailing: Text('${DateTime.now().difference(vuln.timestamp).inMinutes}m ago'),
            )),
        ...testResults.take(3).map((test) => ListTile(
              leading: Icon(
                test.passed ? Icons.check_circle : Icons.error,
                color: test.passed ? Colors.green : Colors.red,
              ),
              title: Text(test.testName),
              subtitle: Text('${test.executionTime}ms'),
              trailing: Text(test.passed ? 'PASS' : 'FAIL'),
            )),
      ],
    );
  }
  
  Widget _buildBpmnVisualization() {
    return CustomPaint(
      size: const Size(double.infinity, double.infinity),
      painter: BpmnProcessPainter(bpmnSteps),
    );
  }
  
  Widget _buildLLMMetricCard(String title, String value, IconData icon) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Icon(icon, size: 32, color: Colors.blue),
            const SizedBox(height: 8),
            Text(
              value,
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 4),
            Text(title),
          ],
        ),
      ),
    );
  }
  
  Widget _buildAnalysisResults() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: llmAnalysisResults.entries.map((entry) {
            return Padding(
              padding: const EdgeInsets.symmetric(vertical: 8),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('${entry.key}: '),
                  Expanded(child: Text(entry.value.toString())),
                ],
              ),
            );
          }).toList(),
        ),
      ),
    );
  }
  
  Widget _getSeverityIcon(String severity) {
    switch (severity.toUpperCase()) {
      case 'CRITICAL':
        return const Icon(Icons.error, color: Colors.red);
      case 'HIGH':
        return const Icon(Icons.warning, color: Colors.orange);
      case 'MEDIUM':
        return const Icon(Icons.info, color: Colors.yellow);
      default:
        return const Icon(Icons.check_circle, color: Colors.green);
    }
  }
  
  Color _getSeverityColor(String severity) {
    switch (severity.toUpperCase()) {
      case 'CRITICAL':
        return Colors.red[800]!;
      case 'HIGH':
        return Colors.orange;
      case 'MEDIUM':
        return Colors.yellow[700]!;
      default:
        return Colors.green;
    }
  }
  
  Widget _getStepStatusIcon(String status) {
    switch (status.toLowerCase()) {
      case 'completed':
        return const Icon(Icons.check_circle, color: Colors.green);
      case 'running':
        return const Icon(Icons.play_circle, color: Colors.blue);
      case 'failed':
        return const Icon(Icons.error, color: Colors.red);
      default:
        return const Icon(Icons.pending, color: Colors.grey);
    }
  }
  
  Color _getStepStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'completed':
        return Colors.green;
      case 'running':
        return Colors.blue;
      case 'failed':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }
  
  void _showVulnerabilityDetails(SecurityVulnerability vuln) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(vuln.name),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Category: ${vuln.category}'),
            Text('Severity: ${vuln.severity}'),
            Text('Endpoint: ${vuln.endpoint}'),
            Text('Confidence: ${(vuln.confidence * 100).toStringAsFixed(1)}%'),
            const SizedBox(height: 16),
            Text(vuln.description),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Close'),
          ),
          TextButton(
            onPressed: () => _generateTestForVulnerability(vuln),
            child: const Text('Generate Test'),
          ),
        ],
      ),
    );
  }
  
  void _generateTestForVulnerability(SecurityVulnerability vuln) {
    // Implement test generation logic
    Navigator.of(context).pop();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Generating test for ${vuln.name}...')),
    );
  }
  
  void _startNewAnalysis() {
    // Implement new analysis logic
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Starting new security analysis...')),
    );
  }
}

// Data Models
class SecurityVulnerability {
  final String id;
  final String name;
  final String severity;
  final String category;
  final String description;
  final String endpoint;
  final double confidence;
  final DateTime timestamp;
  
  SecurityVulnerability({
    required this.id,
    required this.name,
    required this.severity,
    required this.category,
    required this.description,
    required this.endpoint,
    required this.confidence,
  }) : timestamp = DateTime.now();
  
  factory SecurityVulnerability.fromJson(Map<String, dynamic> json) {
    return SecurityVulnerability(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      severity: json['severity'] ?? '',
      category: json['category'] ?? '',
      description: json['description'] ?? '',
      endpoint: json['endpoint'] ?? '',
      confidence: (json['confidence'] ?? 0).toDouble(),
    );
  }
}

class TestResult {
  final String id;
  final String testName;
  final bool passed;
  final int executionTime;
  final String payload;
  
  TestResult({
    required this.id,
    required this.testName,
    required this.passed,
    required this.executionTime,
    required this.payload,
  });
  
  factory TestResult.fromJson(Map<String, dynamic> json) {
    return TestResult(
      id: json['id'] ?? '',
      testName: json['testName'] ?? '',
      passed: json['passed'] ?? false,
      executionTime: json['executionTime'] ?? 0,
      payload: json['payload'] ?? '',
    );
  }
}

class BpmnProcessStep {
  final String id;
  final String name;
  final String status;
  final int executionTime;
  final DateTime timestamp;
  
  BpmnProcessStep({
    required this.id,
    required this.name,
    required this.status,
    required this.executionTime,
    required this.timestamp,
  });
  
  factory BpmnProcessStep.fromJson(Map<String, dynamic> json) {
    return BpmnProcessStep(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      status: json['status'] ?? '',
      executionTime: json['executionTime'] ?? 0,
      timestamp: DateTime.parse(json['timestamp'] ?? DateTime.now().toIso8601String()),
    );
  }
}

// BPMN Visualization Painter
class BpmnProcessPainter extends CustomPainter {
  final List<BpmnProcessStep> steps;
  
  BpmnProcessPainter(this.steps);
  
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..style = PaintingStyle.fill
      ..strokeWidth = 2;
    
    const double stepWidth = 120;
    const double stepHeight = 60;
    const double spacing = 20;
    final double startX = (size.width - (steps.length * stepWidth + (steps.length - 1) * spacing)) / 2;
    const double startY = size.height / 2 - stepHeight / 2;
    
    for (int i = 0; i < steps.length; i++) {
      final step = steps[i];
      final x = startX + i * (stepWidth + spacing);
      final y = startY;
      
      // Draw step box
      paint.color = _getStatusColor(step.status);
      canvas.drawRRect(
        RRect.fromLTRBR(x, y, x + stepWidth, y + stepHeight, const Radius.circular(8)),
        paint,
      );
      
      // Draw step text
      final textPainter = TextPainter(
        text: TextSpan(
          text: step.name,
          style: const TextStyle(color: Colors.white, fontSize: 12),
        ),
        textDirection: TextDirection.ltr,
      );
      textPainter.layout(maxWidth: stepWidth - 16);
      textPainter.paint(
        canvas,
        Offset(x + 8, y + stepHeight / 2 - textPainter.height / 2),
      );
      
      // Draw connector
      if (i < steps.length - 1) {
        paint.color = Colors.grey;
        canvas.drawLine(
          Offset(x + stepWidth, y + stepHeight / 2),
          Offset(x + stepWidth + spacing, y + stepHeight / 2),
          paint,
        );
        
        // Draw arrow
        final arrowPaint = Paint()..color = Colors.grey;
        canvas.drawPath(
          Path()
            ..moveTo(x + stepWidth + spacing - 10, y + stepHeight / 2 - 5)
            ..lineTo(x + stepWidth + spacing, y + stepHeight / 2)
            ..lineTo(x + stepWidth + spacing - 10, y + stepHeight / 2 + 5),
          arrowPaint,
        );
      }
    }
  }
  
  Color _getStatusColor(String status) {
    switch (status.toLowerCase()) {
      case 'completed':
        return Colors.green;
      case 'running':
        return Colors.blue;
      case 'failed':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }
  
  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}