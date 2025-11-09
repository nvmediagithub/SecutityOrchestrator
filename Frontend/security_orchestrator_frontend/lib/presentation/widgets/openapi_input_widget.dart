import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:security_orchestrator_frontend/data/services/testing_service.dart';

class OpenApiInputWidget extends StatefulWidget {
  final Function(String) onOpenApiSelected;

  const OpenApiInputWidget({
    Key? key,
    required this.onOpenApiSelected,
  }) : super(key: key);

  @override
  State<OpenApiInputWidget> createState() => _OpenApiInputWidgetState();
}

class _OpenApiInputWidgetState extends State<OpenApiInputWidget> {
  final TextEditingController _urlController = TextEditingController();
  final TestingService _testingService = TestingService();
  bool _isValidating = false;
  bool _isUrlValid = false;
  String? _validationError;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Provide OpenAPI/Swagger specification:',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 16),
        
        // URL Input
        TextFormField(
          controller: _urlController,
          decoration: InputDecoration(
            labelText: 'OpenAPI URL',
            hintText: 'https://api.example.com/openapi.json',
            prefixIcon: const Icon(Icons.link),
            suffixIcon: _isValidating
                ? const SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : _isUrlValid
                    ? const Icon(Icons.check_circle, color: Colors.green)
                    : null,
            border: const OutlineInputBorder(),
          ),
          onChanged: (value) {
            if (value.isNotEmpty) {
              _validateUrl(value);
            } else {
              setState(() {
                _isUrlValid = false;
                _validationError = null;
              });
            }
          },
        ),
        
        if (_validationError != null) ...[
          const SizedBox(height: 8),
          Text(
            _validationError!,
            style: const TextStyle(color: Colors.red, fontSize: 12),
          ),
        ],
        
        const SizedBox(height: 16),
        const Text('OR', textAlign: TextAlign.center),
        const SizedBox(height: 16),
        
        // File Upload
        ElevatedButton.icon(
          onPressed: _uploadFile,
          icon: const Icon(Icons.upload_file),
          label: const Text('Upload OpenAPI File'),
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.blue,
            foregroundColor: Colors.white,
          ),
        ),
        
        if (_isUrlValid) ...[
          const SizedBox(height: 16),
          const Text(
            '✓ Valid OpenAPI specification detected',
            style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          const Text(
            'OpenAPI URL:',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          Text(
            _urlController.text,
            style: const TextStyle(
              fontFamily: 'monospace',
              fontSize: 12,
            ),
          ),
        ],
        
        const SizedBox(height: 16),
        const Divider(),
        
        // Example URLs
        const Text(
          'Example OpenAPI URLs:',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        _buildExampleUrl(
          'https://banking-api.example.com/openapi.yaml',
          'Banking API (Mock)',
        ),
        _buildExampleUrl(
          'https://petstore.swagger.io/v2/swagger.json',
          'Pet Store API',
        ),
        _buildExampleUrl(
          'https://jsonplaceholder.typicode.com/openapi.json',
          'JSONPlaceholder API',
        ),
        
        const SizedBox(height: 16),
        const Text(
          'Supported formats: JSON, YAML, URL endpoints',
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey,
            fontStyle: FontStyle.italic,
          ),
        ),
      ],
    );
  }

  Widget _buildExampleUrl(String url, String name) {
    return InkWell(
      onTap: () {
        setState(() {
          _urlController.text = url;
        });
        _validateUrl(url);
      },
      child: Container(
        padding: const EdgeInsets.all(8),
        margin: const EdgeInsets.only(bottom: 4),
        decoration: BoxDecoration(
          color: Colors.grey[100],
          borderRadius: BorderRadius.circular(4),
          border: Border.all(color: Colors.grey[300]!),
        ),
        child: Row(
          children: [
            Icon(
              Icons.link,
              size: 16,
              color: Theme.of(context).primaryColor,
            ),
            const SizedBox(width: 8),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    name,
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  Text(
                    url,
                    style: const TextStyle(
                      fontSize: 12,
                      fontFamily: 'monospace',
                    ),
                  ),
                ],
              ),
            ),
            const Icon(Icons.arrow_forward_ios, size: 12),
          ],
        ),
      ),
    );
  }

  Future<void> _uploadFile() async {
    try {
      final result = await FilePicker.platform.pickFiles(
        type: FileType.custom,
        allowedExtensions: ['json', 'yaml', 'yml'],
        allowMultiple: false,
      );

      if (result != null && result.files.single.path != null) {
        final file = File(result.files.single.path!);
        final fileName = result.files.single.name;
        
        // Validate file
        final isValid = await _validateOpenApiFile(file);
        
        if (isValid) {
          // For file upload, we would need to implement file upload API
          // For now, we'll use the file path
          widget.onOpenApiSelected('file://${file.absolute.path}');
          
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('File selected: $fileName'),
              backgroundColor: Colors.green,
            ),
          );
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text('Invalid OpenAPI file: $fileName'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Error selecting file: $e'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  Future<void> _validateUrl(String url) async {
    if (url.isEmpty) return;

    setState(() {
      _isValidating = true;
      _validationError = null;
    });

    try {
      // Basic URL validation
      final uri = Uri.tryParse(url);
      if (uri == null || !uri.hasAbsolutePath) {
        setState(() {
          _validationError = 'Invalid URL format';
          _isUrlValid = false;
          _isValidating = false;
        });
        return;
      }

      // Test the URL
      final isValid = await _testingService.validateOpenApiUrl(url);
      
      if (isValid) {
        setState(() {
          _isUrlValid = true;
          _validationError = null;
        });
        widget.onOpenApiSelected(url);
      } else {
        setState(() {
          _isUrlValid = false;
          _validationError = 'Could not validate OpenAPI specification at this URL';
        });
      }
    } catch (e) {
      setState(() {
        _isUrlValid = false;
        _validationError = 'Error validating URL: $e';
      });
    } finally {
      setState(() {
        _isValidating = false;
      });
    }
  }

  Future<bool> _validateOpenApiFile(File file) async {
    try {
      final content = await file.readAsString();
      
      // Basic validation - check if it contains OpenAPI structure
      if (content.contains('"openapi"') || content.contains('"swagger"')) {
        return true;
      }
      
      // YAML check
      if (content.contains('openapi:') || content.contains('swagger:')) {
        return true;
      }
      
      return false;
    } catch (e) {
      return false;
    }
  }

  @override
  void dispose() {
    _urlController.dispose();
    super.dispose();
  }
}