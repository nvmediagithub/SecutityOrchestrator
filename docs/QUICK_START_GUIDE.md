# Security Orchestrator - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

Welcome to Security Orchestrator! This guide will get you up and running quickly with essential information for developers, administrators, and users.

---

## 📋 Prerequisites

### For Developers
- **Java 21+** (LTS recommended)
- **Gradle 8.0+**
- **Git**
- **Flutter SDK** (for frontend development)
- **Docker** (optional, for containerized development)

### For Administrators
- **Java 21+**
- **Gradle 8.0+**
- **4GB+ RAM available**
- **2GB+ disk space**

### For End Users
- **Modern web browser** (Chrome, Firefox, Safari, Edge)
- **No software installation required** (web-based interface)

---

## 🏃‍♂️ Quick Setup (Local Development)

### Step 1: Clone and Setup
```bash
git clone <repository-url>
cd SecutityOrchestrator
```

### Step 2: Start Backend
```bash
cd Backend
./gradlew bootRun
```
**Expected Output**: Application starts on `http://localhost:8080`

### Step 3: Start Frontend (Optional)
```bash
cd Frontend/flutter-app
flutter run -d web
```
**Expected Output**: Frontend available at `http://localhost:3000`

### Step 4: Verify Installation
Open browser to: `http://localhost:8080/health`

**Expected Response**:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

---

## 🎯 Essential Information by Role

### 👨‍💻 For Developers

#### 🔧 **Development Setup**
- **Backend Entry Point**: `Backend/app/src/main/java/org/example/SecurityOrchestratorApplication.java`
- **Main Configuration**: `Backend/app/src/main/resources/application.properties`
- **API Documentation**: `http://localhost:8080/swagger-ui.html`
- **Development Port**: 8080

#### 🏗️ **Architecture Quick Reference**
```
Backend/
├── app/                    # Main application
├── features/               # Feature modules
│   ├── bpmn/              # BPMN processing
│   ├── llm/               # LLM integration
│   ├── monitoring/        # System monitoring
│   ├── openapi/           # OpenAPI processing
│   └── ...
├── shared/                # Shared components
└── settings.gradle.kts    # Module configuration
```

#### 🔌 **Key API Endpoints**
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/health` | GET | Health check |
| `/api/analysis` | POST | Start analysis |
| `/api/bpmn/analyze` | POST | BPMN analysis |
| `/api/openapi/validate` | POST | OpenAPI validation |
| `/api/llm/chat` | POST | LLM chat completion |

#### 📚 **Essential Documents**
- [Developer Setup Guide](docs/DEVELOPER_SETUP_GUIDE.md)
- [API Reference](docs/API_REFERENCE.md)
- [Architecture Overview](docs/architecture-overview.md)

#### 🚀 **Common Development Commands**
```bash
# Run tests
./gradlew test

# Build application
./gradlew build

# Run specific feature tests
./gradlew :features:bpmn:test

# Clean build
./gradlew clean build
```

### 🛡️ For Administrators

#### 🔧 **System Requirements**
- **RAM**: Minimum 4GB, Recommended 8GB+
- **CPU**: 2+ cores recommended
- **Disk**: 2GB+ for application + data
- **Network**: Port 8080 (configurable)

#### 📊 **Monitoring & Health**
- **Health Endpoint**: `http://localhost:8080/actuator/health`
- **Metrics Endpoint**: `http://localhost:8080/actuator/metrics`
- **Admin Interface**: `http://localhost:8080/admin`
- **Log Files**: `Backend/logs/` directory

#### 🔐 **Security Configuration**
```yaml
# application.properties
security.encryption.key=${ENCRYPTION_KEY:your-key}
security.llm.providers=${LLM_PROVIDERS:ollama,openrouter}
security.api.rate.limit=${API_RATE_LIMIT:1000}
```

#### 📈 **Performance Tuning**
- **JVM Options**: `-Xms2g -Xmx4g` for production
- **Database**: H2 file-based (development), configure external DB for production
- **LLM Cache**: Configure memory limits for local models

#### 🚨 **Troubleshooting**
| Issue | Solution |
|-------|----------|
| Port 8080 busy | Set `server.port=8081` in properties |
| Out of memory | Increase JVM heap: `-Xmx4g` |
| LLM connection fails | Check Ollama/OpenRouter configuration |
| Frontend not loading | Clear browser cache, check CORS settings |

### 👤 For End Users

#### 🌐 **Web Interface Access**
- **Main Interface**: `http://localhost:8080`
- **Dashboard**: Shows analysis status and results
- **Upload Interface**: Drag-and-drop BPMN files and OpenAPI specs

#### 🔄 **Basic Workflow**
1. **Upload Files**: BPMN diagrams and/or OpenAPI specifications
2. **Configure Analysis**: Select analysis types and options
3. **Run Analysis**: Click "Start Analysis" button
4. **View Results**: Monitor progress in real-time dashboard
5. **Download Reports**: Export security assessment reports

#### 📋 **Supported File Types**
| Type | Extensions | Description |
|------|------------|-------------|
| **BPMN** | `.bpmn`, `.bpmn2` | Business process diagrams |
| **OpenAPI** | `.json`, `.yaml`, `.yml` | API specifications |
| **Configuration** | `.properties`, `.yml` | System configuration |

#### 🎯 **Quick Actions**
- **Upload BPMN**: Drag `.bpmn` file to main interface
- **Upload OpenAPI**: Upload `.json` or `.yaml` API spec
- **View Results**: Click on completed analysis
- **Download Report**: Use "Export" button in results view

---

## 🔥 Quick Reference

### 📞 **Emergency Commands**
```bash
# Stop all services
pkill -f "SecurityOrchestrator"

# Check application status
curl http://localhost:8080/actuator/health

# View recent logs
tail -f Backend/logs/application.log
```

### 🆘 **Quick Troubleshooting**
- **Application won't start**: Check Java version (`java -version`)
- **Frontend not connecting**: Verify backend is running on port 8080
- **Analysis failing**: Check file formats and LLM provider status
- **Performance issues**: Monitor memory usage and consider JVM tuning

### 📚 **Key Documentation Links**
- [📖 Master Documentation Index](docs/MASTER_DOCUMENTATION_INDEX.md)
- [🏗️ Architecture Overview](docs/architecture-overview.md)
- [🔧 Developer Setup Guide](docs/DEVELOPER_SETUP_GUIDE.md)
- [🚀 Production Deployment](docs/PRODUCTION_DEPLOYMENT_GUIDE.md)
- [🔐 Security Best Practices](docs/SECURITY_BEST_PRACTICES.md)

---

## 🎯 Next Steps

### For Developers
1. Read [Architecture Overview](docs/architecture-overview.md)
2. Explore [Feature Module Structure](docs/feature-modules.md)
3. Review [API Integration Patterns](docs/API_INTEGRATION_PATTERNS.md)
4. Set up [Development Environment](docs/DEVELOPER_SETUP_GUIDE.md)

### For Administrators
1. Review [Production Deployment Guide](docs/PRODUCTION_DEPLOYMENT_GUIDE.md)
2. Study [Security Best Practices](docs/SECURITY_BEST_PRACTICES.md)
3. Configure [Monitoring System](docs/MONITORING_USER_GUIDE.md)
4. Plan [Enterprise Integration](docs/ENTERPRISE_INTEGRATIONS.md)

### For End Users
1. Try [GUI User Guide](docs/GUI_USER_GUIDE.md)
2. Learn about [BPMN Analysis](docs/bpmn/README.md)
3. Understand [API Security Testing](docs/OPENAPI_USER_GUIDE.md)
4. Explore [Monitoring Dashboard](docs/MONITORING_USER_GUIDE.md)

---

## 🆘 Getting Help

### 📧 **Support Channels**
- **Documentation**: Start with [Master Documentation Index](docs/MASTER_DOCUMENTATION_INDEX.md)
- **Architecture Questions**: See [Architecture Overview](docs/architecture-overview.md)
- **Technical Issues**: Check [Troubleshooting Guide](docs/TROUBLESHOOTING.md)
- **Security Concerns**: Review [Security Best Practices](docs/SECURITY_BEST_PRACTICES.md)

### 🐛 **Issue Reporting**
When reporting issues, include:
1. **Error messages** from logs
2. **Steps to reproduce** the problem
3. **System information** (OS, Java version, etc.)
4. **Configuration files** (sanitized of sensitive data)

### 📖 **Documentation Status**
- **✅ Complete**: Architecture, API reference, security guides
- **🔄 In Progress**: Advanced features, enterprise integration
- **📋 Planned**: Mobile app guide, advanced troubleshooting

---

*Last Updated: November 2025 | Version: 1.0 | For latest updates, see [Documentation Roadmap](docs/DOCUMENTATION_ROADMAP.md)*