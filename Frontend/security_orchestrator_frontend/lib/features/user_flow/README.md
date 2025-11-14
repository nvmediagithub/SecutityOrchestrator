# User Flow Feature

The User Flow feature handles document analysis and AI-powered security testing in the Security Orchestrator application.

## Overview

This feature allows users to upload documents (DOCX, TXT, PDF) and perform comprehensive security analysis using AI/LLM capabilities. It provides real-time progress tracking, detailed results visualization, and analysis history management.

## Architecture

```
user_flow/
├── data/
│   ├── models/
│   │   ├── analysis_request.dart      # Request models for analysis
│   │   └── analysis_response.dart     # Response models for results
│   ├── services/
│   │   └── user_flow_api_service.dart # API client for backend communication
│   └── repositories/                  # Data access layer (future use)
├── presentation/
│   ├── providers/
│   │   └── user_flow_provider.dart    # State management with Riverpod
│   ├── widgets/
│   │   ├── file_upload_widget.dart    # Drag & drop file upload
│   │   └── analysis_progress_widget.dart # Progress indicator
│   └── screens/
│       └── user_flow_main_screen.dart  # Main analysis interface
```

## Key Components

### Models

#### AnalysisRequest
Represents a document analysis request sent to the backend.

```dart
const AnalysisRequest({
  required String documentContent,
  required String documentType,
  String? documentName,
  Map<String, dynamic>? metadata,
  List<String>? analysisTypes,
});
```

#### AnalysisResponse
Represents the initial response when analysis starts.

#### AnalysisStatus
Tracks real-time analysis progress.

#### AnalysisResult
Contains complete analysis results including vulnerabilities and recommendations.

### Services

#### UserFlowApiService
Handles all backend communication for the user flow feature.

**Key Methods**:
- `checkHealth()`: Check backend connectivity
- `startAnalysis()`: Initiate document analysis
- `getAnalysisStatus()`: Poll analysis progress
- `getAnalysisResult()`: Retrieve complete results
- `getAnalysisHistory()`: Fetch user's analysis history

### Providers

#### UserFlowProvider
Manages state for the user flow feature using Riverpod StateNotifier.

**State Properties**:
- `isAnalyzing`: Whether analysis is in progress
- `analysisStatus`: Current analysis status
- `analysisResult`: Completed analysis results
- `hasUploadedFile`: Whether a file has been uploaded
- `error`: Current error message

**Actions**:
- `uploadFile()`: Handle file upload
- `startAnalysis()`: Begin analysis process
- `loadAnalysisHistory()`: Load user's analysis history
- `resetAnalysis()`: Clear current analysis state

### Widgets

#### FileUploadWidget
Drag & drop file upload interface with support for DOCX, TXT, and PDF files.

**Features**:
- Visual feedback for drag operations
- File type validation
- Upload progress indication
- Error handling for unsupported formats

#### AnalysisProgressWidget
Real-time progress indicator showing analysis status.

**Features**:
- Progress bar with percentage
- Status messages and current step
- Time remaining estimation
- Error display with retry options

### Screens

#### UserFlowMainScreen
Main interface for document analysis workflow.

**Features**:
- File upload interface
- Analysis initiation
- Progress monitoring
- Results preview
- Navigation to detailed results

## User Journey

1. **File Upload**: User selects or drags a document
2. **Analysis Start**: User initiates analysis with uploaded file
3. **Progress Tracking**: Real-time updates on analysis progress
4. **Results Display**: View security score, vulnerabilities, and recommendations
5. **History Access**: Review previous analyses

## API Integration

The feature communicates with the backend `/user-flow` endpoints:

- `POST /user-flow/analyze`: Start analysis
- `GET /user-flow/status/{id}`: Get analysis status
- `GET /user-flow/result/{id}`: Get analysis results
- `GET /user-flow/history`: Get analysis history
- `GET /user-flow/health`: Check service health

## Error Handling

The feature implements comprehensive error handling:

- Network connectivity issues
- File upload failures
- Analysis timeouts
- Backend service errors
- Invalid file formats

## Future Enhancements

- Batch file processing
- Advanced analysis options
- Custom analysis templates
- Export results to PDF/Word
- Real-time collaboration features
- Advanced filtering and search in history

## Testing

The feature includes comprehensive test coverage:

- Unit tests for providers and services
- Widget tests for UI components
- Integration tests for complete workflows
- Mock implementations for backend services

## Dependencies

- `flutter_riverpod`: State management
- `http`: HTTP client for API calls
- `file_picker`: File selection
- `equatable`: Value equality for models