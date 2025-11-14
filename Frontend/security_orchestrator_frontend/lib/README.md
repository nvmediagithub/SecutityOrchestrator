# Security Orchestrator Frontend

A Flutter application for security testing and orchestration with AI-powered analysis capabilities.

## Architecture

This application follows a **feature-first architecture** where code is organized around business domains rather than technical layers. This approach improves maintainability, testability, and team collaboration.

### Project Structure

```
lib/
├── features/                    # Feature modules (business domains)
│   ├── user_flow/              # Document analysis & AI security testing
│   ├── process_management/     # BPMN processing & workflow orchestration
│   ├── system_monitoring/      # System health & dashboard
│   ├── llm_management/         # LLM provider & model configuration
│   └── README.md               # Feature architecture documentation
├── shared/                      # Cross-cutting concerns
│   ├── core/                   # Constants, configurations
│   ├── utils/                  # Common utilities
│   └── widgets/                # Shared UI components
├── presentation/               # Legacy layered structure (being migrated)
├── data/                       # Legacy layered structure (being migrated)
└── main.dart                   # Application entry point
```

## Features

### 🔍 User Flow
Document upload and AI-powered security analysis
- Support for DOCX, TXT, PDF files
- Real-time analysis progress
- Comprehensive security reporting
- Analysis history tracking

### ⚙️ Process Management
BPMN workflow orchestration
- BPMN file parsing and validation
- Visual process modeling
- Workflow execution monitoring

### 📊 System Monitoring
Real-time system health monitoring
- Service status tracking
- Performance metrics
- Alert management

### 🤖 LLM Management
AI model configuration and testing
- Multiple provider support (OpenRouter, Local)
- Model performance monitoring
- API key management

## Technology Stack

- **Framework**: Flutter 3.x
- **State Management**: Riverpod
- **Networking**: HTTP package
- **File Handling**: File Picker
- **Architecture**: Feature-First (Domain-Driven)

## Development Setup

### Prerequisites
- Flutter 3.0 or higher
- Dart 3.0 or higher
- Android Studio / VS Code with Flutter extensions

### Installation

1. Clone the repository
2. Navigate to the frontend directory:
   ```bash
   cd SecurityOrchestrator/Frontend/security_orchestrator_frontend
   ```

3. Install dependencies:
   ```bash
   flutter pub get
   ```

4. Run the application:
   ```bash
   flutter run
   ```

### Backend Integration

The frontend communicates with the Security Orchestrator backend microservices:

- **Base URL**: `http://localhost:8000` (configurable)
- **Health Check**: `GET /health`
- **API Documentation**: Available at backend service endpoints

## Architecture Migration

This application is undergoing migration from a traditional layered architecture to a feature-first architecture:

### Current State
- ✅ Feature-first structure implemented
- ✅ User Flow feature completed
- 🔄 Other features in layered structure
- 🔄 Gradual migration in progress

### Migration Benefits
- **Improved Maintainability**: Related code co-located
- **Better Testability**: Features tested in isolation
- **Team Scalability**: Independent feature development
- **Deployment Flexibility**: Feature-level deployments

## Testing

```bash
# Run all tests
flutter test

# Run tests with coverage
flutter test --coverage

# Run integration tests
flutter test integration_test/
```

## Building for Production

```bash
# Build for Android APK
flutter build apk --release

# Build for iOS (macOS only)
flutter build ios --release

# Build for Web
flutter build web --release
```

## Contributing

### Code Organization
1. All new features should follow the feature-first structure
2. Shared components go in the `shared/` directory
3. Use Riverpod for state management
4. Follow the established naming conventions

### Feature Development Process
1. Create feature directory structure
2. Implement data models and services
3. Add provider logic
4. Create UI components
5. Write comprehensive tests
6. Update documentation

## Documentation

- [Feature Architecture](./features/README.md) - Detailed feature organization
- [User Flow Feature](./features/user_flow/README.md) - Feature implementation details
- [Migration Roadmap](../docs/migration-roadmap/) - Backend migration context

## License

This project is part of the Security Orchestrator ecosystem.