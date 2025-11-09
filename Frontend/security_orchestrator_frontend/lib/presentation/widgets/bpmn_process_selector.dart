import 'package:flutter/material.dart';
import 'package:security_orchestrator_frontend/data/services/testing_service.dart';

class BpmnProcessSelector extends StatefulWidget {
  final Function(String) onProcessSelected;

  const BpmnProcessSelector({
    Key? key,
    required this.onProcessSelected,
  }) : super(key: key);

  @override
  State<BpmnProcessSelector> createState() => _BpmnProcessSelectorState();
}

class _BpmnProcessSelectorState extends State<BpmnProcessSelector> {
  final TestingService _testingService = TestingService();
  List<Map<String, dynamic>> _availableProcesses = [];
  bool _isLoading = false;
  String? _selectedProcess;
  String? _searchQuery;

  @override
  void initState() {
    super.initState();
    _loadAvailableProcesses();
  }

  Future<void> _loadAvailableProcesses() async {
    setState(() => _isLoading = true);

    try {
      // Mock data for BPMN processes - in real app this would come from API
      _availableProcesses = [
        {
          'id': '01_bonus_payment',
          'name': 'Bonus Payment Process',
          'description': 'Process for paying bonuses to customers',
          'steps': 5,
          'complexity': 'Medium',
          'category': 'Payment',
        },
        {
          'id': '02_credit_application',
          'name': 'Credit Application',
          'description': 'Process for processing credit applications',
          'steps': 6,
          'complexity': 'High',
          'category': 'Loan',
        },
        {
          'id': '03_gibdd_fine',
          'name': 'GIBDD Fine Payment',
          'description': 'Process for paying traffic fines',
          'steps': 4,
          'complexity': 'Low',
          'category': 'Payment',
        },
        {
          'id': '04_mobile_payment',
          'name': 'Mobile Payment',
          'description': 'Process for mobile payment transactions',
          'steps': 7,
          'complexity': 'Medium',
          'category': 'Payment',
        },
        {
          'id': '05_prepaid_card',
          'name': 'Prepaid Card Management',
          'description': 'Process for managing prepaid cards',
          'steps': 8,
          'complexity': 'High',
          'category': 'Card',
        },
        {
          'id': '06_close_card',
          'name': 'Card Closure',
          'description': 'Process for closing bank cards',
          'steps': 5,
          'complexity': 'Medium',
          'category': 'Card',
        },
        {
          'id': '11_vrp_setup',
          'name': 'VRP Setup',
          'description': 'Variable Recurring Payments setup process',
          'steps': 6,
          'complexity': 'High',
          'category': 'Payment',
        },
        {
          'id': '18_utility_payment',
          'name': 'Utility Payment',
          'description': 'Process for paying utility bills',
          'steps': 5,
          'complexity': 'Low',
          'category': 'Payment',
        },
        // Add more processes as needed
      ];
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Error loading BPMN processes: $e'),
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(
        child: CircularProgressIndicator(),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Select BPMN process for workflow integration:',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 16),
        
        // Search
        TextField(
          decoration: const InputDecoration(
            labelText: 'Search processes',
            prefixIcon: Icon(Icons.search),
            border: OutlineInputBorder(),
          ),
          onChanged: (value) {
            setState(() {
              _searchQuery = value;
            });
          },
        ),
        
        const SizedBox(height: 16),
        
        // Filter chips
        _buildFilterChips(),
        
        const SizedBox(height: 16),
        
        // Process list
        Expanded(
          child: _buildProcessList(),
        ),
        
        const SizedBox(height: 16),
        
        // Selected process info
        if (_selectedProcess != null) _buildSelectedProcessInfo(),
      ],
    );
  }

  Widget _buildFilterChips() {
    final categories = <String>['All', 'Payment', 'Card', 'Loan'];
    
    return Wrap(
      spacing: 8,
      children: categories.map((category) {
        return FilterChip(
          label: Text(category),
          selected: category == 'All' && _searchQuery == null || 
                   _selectedProcess != null && category == _selectedProcess,
          onSelected: (selected) {
            // Handle category selection
            if (category == 'All') {
              setState(() {
                _searchQuery = null;
              });
            }
          },
        );
      }).toList(),
    );
  }

  Widget _buildProcessList() {
    final filteredProcesses = _getFilteredProcesses();
    
    if (filteredProcesses.isEmpty) {
      return const Center(
        child: Text('No processes found'),
      );
    }

    return ListView.builder(
      itemCount: filteredProcesses.length,
      itemBuilder: (context, index) {
        final process = filteredProcesses[index];
        return _buildProcessCard(process);
      },
    );
  }

  Widget _buildProcessCard(Map<String, dynamic> process) {
    final isSelected = _selectedProcess == process['id'];
    
    return Card(
      elevation: isSelected ? 4 : 2,
      color: isSelected ? Theme.of(context).primaryColor.withOpacity(0.1) : null,
      child: InkWell(
        onTap: () {
          setState(() {
            _selectedProcess = process['id'];
          });
          widget.onProcessSelected(process['id']);
        },
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Icon(
                    _getProcessIcon(process['category']),
                    color: isSelected ? Theme.of(context).primaryColor : Colors.grey,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          process['name'],
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: isSelected ? Theme.of(context).primaryColor : null,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          process['description'],
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.grey[600],
                          ),
                        ),
                      ],
                    ),
                  ),
                  if (isSelected)
                    Icon(
                      Icons.check_circle,
                      color: Theme.of(context).primaryColor,
                    ),
                ],
              ),
              
              const SizedBox(height: 12),
              
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  _buildInfoChip(
                    Icons.workflow,
                    '${process['steps']} steps',
                    Colors.blue,
                  ),
                  _buildInfoChip(
                    Icons.timeline,
                    process['complexity'],
                    _getComplexityColor(process['complexity']),
                  ),
                  _buildInfoChip(
                    Icons.category,
                    process['category'],
                    Colors.orange,
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInfoChip(IconData icon, String text, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: color),
          const SizedBox(width: 4),
          Text(
            text,
            style: TextStyle(
              fontSize: 12,
              color: color,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSelectedProcessInfo() {
    final process = _availableProcesses
        .firstWhere((p) => p['id'] == _selectedProcess);
    
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).primaryColor.withOpacity(0.1),
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Theme.of(context).primaryColor.withOpacity(0.3)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                Icons.check_circle,
                color: Theme.of(context).primaryColor,
              ),
              const SizedBox(width: 8),
              Text(
                'Selected Process:',
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Theme.of(context).primaryColor,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            process['name'],
            style: const TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 16,
            ),
          ),
          const SizedBox(height: 4),
          Text(process['description']),
          const SizedBox(height: 8),
          Text(
            'This process will be used to generate context-aware tests and understand API workflow dependencies.',
            style: TextStyle(
              fontSize: 12,
              color: Colors.grey[600],
              fontStyle: FontStyle.italic,
            ),
          ),
        ],
      ),
    );
  }

  List<Map<String, dynamic>> _getFilteredProcesses() {
    if (_searchQuery == null || _searchQuery!.isEmpty) {
      return _availableProcesses;
    }
    
    return _availableProcesses.where((process) {
      final name = process['name'].toString().toLowerCase();
      final description = process['description'].toString().toLowerCase();
      final category = process['category'].toString().toLowerCase();
      final query = _searchQuery!.toLowerCase();
      
      return name.contains(query) ||
             description.contains(query) ||
             category.contains(query);
    }).toList();
  }

  IconData _getProcessIcon(String category) {
    switch (category.toLowerCase()) {
      case 'payment':
        return Icons.payment;
      case 'card':
        return Icons.credit_card;
      case 'loan':
        return Icons.account_balance;
      default:
        return Icons.workflow;
    }
  }

  Color _getComplexityColor(String complexity) {
    switch (complexity.toLowerCase()) {
      case 'low':
        return Colors.green;
      case 'medium':
        return Colors.orange;
      case 'high':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }
}