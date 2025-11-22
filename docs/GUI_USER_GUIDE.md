# SecurityOrchestrator GUI User Guide

## 🎯 Overview

SecurityOrchestrator is a comprehensive Flutter Web-based platform for end-to-end security testing of business processes and APIs. This guide will help you navigate and effectively use all features of the modern web interface.

**System Access:**
- Flutter Web Interface: `http://localhost:3000` (or port specified in Flutter configuration)
- Backend API: `http://localhost:8080`
- Health Check: `http://localhost:8080/api/health`

---

## 🚀 Getting Started

### 1. System Requirements

**Minimum Requirements:**
- Modern web browser (Chrome 88+, Firefox 85+, Safari 14+, Edge 88+)
- Java 21+ installed for backend
- Flutter Web runtime (auto-loaded in browser)
- 4GB RAM minimum, 8GB recommended
- Internet connection for initial setup

**Modern UI Features:**
- Material Design 3 implementation
- Responsive design for desktop, tablet, and mobile
- Dark/Light theme support
- Real-time updates via WebSocket
- Accessible design following WCAG 2.1 guidelines

**Access the System:**
1. Open your web browser
2. Navigate to `http://localhost:3000`
3. The Flutter web app will load automatically
4. Allow browser permissions for optimal performance

### 2. First Steps

Upon accessing the system, you'll see the **Main Dashboard** with:
- **Dashboard Cards**: Connection status, system health, LLM status
- **Real-time Metrics**: System performance and monitoring data
- **Navigation**: Easy access to different features via GoRouter
- **Quick Actions**: Create new analysis processes
- **Alerts Panel**: System notifications and warnings
- **Responsive Layout**: Optimized for your screen size

---

## 📊 Main Dashboard Features

### Dashboard Navigation

The main dashboard uses Flutter's GoRouter for seamless navigation between different features:

**Navigation Routes:**
- `/` - Main Dashboard
- `/processes` - Analysis Processes
- `/processes/:id` - Process Details

**Core Dashboard Cards:**

1. **Connection Status Card**
   - Real-time backend connectivity
   - API health monitoring
   - Connection diagnostics

2. **System Health Card**
   - CPU, memory, and disk usage
   - Performance metrics
   - System alerts and warnings

3. **LLM Status Card**
   - LLM provider configuration
   - Model availability
   - Provider health status

4. **Metrics List**
   - Key performance indicators
   - Historical trends
   - Real-time monitoring

5. **Alerts List**
   - System notifications
   - Warning indicators
   - Action items

6. **Processes Overview Card**
   - Active analysis processes
   - Process status summary
   - Quick access to detailed views

**Flutter UI Features:**
- **Material Design 3**: Modern, accessible design
- **Riverpod State Management**: Efficient state handling
- **Responsive Layout**: Adapts to screen size
- **Real-time Updates**: WebSocket integration
- **SelectionArea Support**: Keyboard navigation
- **Theme Support**: Light/dark mode

---

## 🤖 LLM Dashboard

### Overview
The LLM Dashboard provides comprehensive management of Large Language Models for AI-powered security analysis and test generation.

**Access:** Click the "LLM Dashboard" button from the main navigation or home screen.

### System Overview Card

**Key Information Displayed:**
- Active LLM Provider (OpenRouter or Local)
- Currently selected model
- Number of available models
- Number of configured providers

### Provider Configuration

#### OpenRouter Configuration (Cloud-based LLMs)

1. **API Key Setup:**
   - Enter your OpenRouter API key (starts with `sk-or-`)
   - System validates key format automatically
   - Secure storage of credentials

2. **Base URL Configuration:**
   - Default: `https://openrouter.ai/api/v1`
   - Custom endpoint support for enterprise setups

3. **Provider Status:**
   - Green indicator: Provider configured and operational
   - Orange indicator: Provider not configured
   - Real-time status updates

**Configuration Steps:**
```
1. Navigate to Provider Configuration section
2. Expand "OpenRouter Configuration"
3. Enter your API key in the secure field
4. Verify base URL (or use default)
5. Click "Configure OpenRouter" button
6. Wait for confirmation message
```

#### Local LLM Configuration

- **Computer Icon**: Indicates local model provider
- **Direct Integration**: Works with locally installed models
- **Privacy-First**: No data leaves your system

### Model Selection

**Available Models Display:**
- Dropdown menu with all configured models
- Tooltip information showing:
  - Context window size
  - Maximum tokens
  - Temperature settings
  - Provider information

**Model Management:**
- Switch between different models
- View model configuration details
- Performance monitoring per model

### Status Monitoring

**Real-time Provider Status:**
- **Green**: Healthy and available
- **Orange**: Unhealthy but available
- **Red**: Unavailable or error state

**Status Information Includes:**
- Response time measurements
- Last check timestamp
- Error messages (if any)
- Provider health indicators

### Test Interface

**Direct LLM Testing:**
1. Enter test prompt in the text area
2. Click "Test LLM" button
3. View response in the results panel
4. Copy responses for documentation

**Use Cases:**
- Validate model responses
- Test prompt engineering
- Verify model capabilities
- Quality assurance

---

## 🔧 API Testing Dashboard

### Overview
Comprehensive API security testing platform combining OpenAPI analysis, BPMN workflow integration, and OWASP security standards.

**Access:** Available through the main navigation or home screen actions.

### Testing Workflow

#### Step 1: OpenAPI Specification Analysis

**Input Methods:**
- **URL Input**: Direct OpenAPI/Swagger URL
- **File Upload**: Upload .yaml or .json files
- **Example URLs**: Pre-configured sample specifications

**Analysis Features:**
- Automatic endpoint extraction
- Schema validation
- Security scheme detection
- Authentication method analysis
- Response structure analysis

**Process:**
```
1. Expand "OpenAPI Specification" section
2. Enter URL or upload file
3. Click "Analyze OpenAPI"
4. Wait for completion confirmation
5. Review analysis results
```

#### Step 2: BPMN Process Integration

**Available Processes:**
- 20 pre-configured business processes
- Financial services workflows
- Payment processing systems
- Customer onboarding processes
- And more...

**Integration Features:**
- Workflow step extraction
- Data flow analysis
- Business logic understanding
- Process validation
- Performance monitoring

**Process Selection:**
```
1. Expand "BPMN Process Integration" section
2. Browse available processes
3. Select relevant process for your API
4. Review process details
5. Confirm integration
```

#### Step 3: OWASP API Security Categories

**OWASP API Security Top 10 Coverage:**

1. **API1: Broken Object Level Authorization (BOLA)**
   - Object-level access control testing
   - IDOR vulnerability detection
   - User data isolation verification

2. **API2: Broken User Authentication (BUA)**
   - Authentication flow testing
   - Session management validation
   - Credential handling assessment

3. **API3: Broken Object Property Level Authorization (BOPLA)**
   - Field-level access control
   - Property authorization testing
   - Data exposure prevention

4. **API4: Unrestricted Resource Consumption (URC)**
   - Rate limiting verification
   - Resource exhaustion testing
   - API quota management

5. **API5: Broken Function Level Authorization (BFLA)**
   - Function-level access control
   - Privilege escalation testing
   - Role-based access verification

6. **API6: Unrestricted Access to Sensitive Business Flows (UASBF)**
   - Business process security
   - Workflow integrity testing
   - Critical path protection

7. **API7: Server Side Request Forgery (SSRF)**
   - Internal service exploitation
   - Network boundary testing
   - Request manipulation detection

8. **API8: Security Misconfiguration (SM)**
   - Configuration security audit
   - Default setting analysis
   - Security headers verification

9. **API9: Improper Inventory Management (IIM)**
   - API discovery and cataloging
   - Version management testing
   - Legacy endpoint detection

10. **API10: Unsafe Consumption of APIs (UCA)**
    - Third-party API security
    - Trust boundary testing
    - External dependency analysis

**Selection Process:**
```
1. Expand "OWASP API Security Categories" section
2. Review all 10 OWASP categories
3. Select relevant categories for your use case
4. Configure category-specific parameters
5. Review security testing scope
```

#### Step 4: Test Configuration

**Configuration Parameters:**
- Test execution timeout
- Concurrent request limits
- Authentication credentials
- Custom test data sets
- Reporting format preferences

**Advanced Options:**
- Custom payload generation
- Boundary value testing
- Negative test scenarios
- Performance benchmarking
- Integration testing modes

---

## 📈 Real-time Monitoring

### Monitoring Dashboard

**Access:** Navigate to Monitoring section from main menu.

### System Health Monitoring

**Metrics Tracked:**
- **CPU Usage**: Real-time processor utilization
- **Memory Usage**: RAM consumption monitoring
- **Disk Usage**: Storage utilization tracking
- **Database Connection**: Connection status and performance
- **API Response Times**: Average response time monitoring
- **Active Models**: Currently loaded LLM models

**Health Status Indicators:**
- 🟢 **Healthy**: All systems operating normally
- 🟡 **Warning**: Some metrics approaching thresholds
- 🔴 **Critical**: Immediate attention required
- ⚠️ **Degraded**: Performance issues detected

### Real-time Charts

**Interactive Visualizations:**
- Line charts for performance trends
- Time-series data for historical analysis
- Responsive charts for mobile viewing
- Zoom and filter capabilities
- Export functionality for reports

**Chart Types:**
- System resource usage
- API performance metrics
- LLM response times
- Error rate trends
- User activity patterns

### Alert System

**Alert Types:**
- **System Alerts**: Resource usage warnings
- **Performance Alerts**: Response time degradation
- **Security Alerts**: Anomaly detection
- **Business Alerts**: Process completion monitoring

**Alert Management:**
- Real-time notification system
- Alert history and trending
- Custom threshold configuration
- Escalation rule setup

---

## 🔍 Results and Reporting

### Test Results Dashboard

**Result Categories:**
- **Security Findings**: Detected vulnerabilities
- **Performance Metrics**: Response time analysis
- **Compliance Reports**: OWASP standard coverage
- **Business Process Analysis**: Workflow validation

### Report Generation

**Available Formats:**
- **PDF**: Executive summary reports
- **JSON**: Machine-readable data
- **HTML**: Interactive web reports
- **CSV**: Spreadsheet-compatible data

**Report Contents:**
- Executive summary
- Technical findings
- Risk assessments
- Remediation recommendations
- Compliance status
- Performance benchmarks

### Vulnerability Analysis

**Severity Levels:**
- **Critical**: Immediate action required
- **High**: High priority remediation
- **Medium**: Standard remediation timeline
- **Low**: Improvement opportunities
- **Informational**: Best practice suggestions

**Detailed Analysis:**
- Vulnerability description
- Impact assessment
- Exploitation scenarios
- Remediation steps
- Reference materials

---

## 🔧 Configuration and Settings

### System Configuration

**Global Settings:**
- Default timeout values
- Retry configuration
- Logging levels
- Security policies

### User Preferences

**Interface Customization:**
- Theme selection
- Layout preferences
- Notification settings
- Dashboard widgets

### API Integration Settings

**Connection Management:**
- Target API endpoints
- Authentication methods
- Rate limiting preferences
- Proxy configurations

---

## 🛠️ Troubleshooting

### Common Issues and Solutions

#### 1. "Connection Refused" Errors

**Problem**: Cannot connect to backend services

**Solutions:**
- Verify backend is running on port 8080
- Check firewall settings
- Confirm Java process is active
- Restart services if needed

**Verification Steps:**
```bash
# Check if backend is running
curl http://localhost:8080/api/health

# Check process status
ps aux | grep java
```

#### 2. LLM Provider Configuration Issues

**Problem**: OpenRouter API key not working

**Solutions:**
- Verify API key format (starts with "sk-or-")
- Check API key permissions
- Confirm network connectivity
- Validate base URL configuration

**Diagnostic Steps:**
1. Navigate to LLM Dashboard
2. Check provider status indicators
3. Test with simple prompts
4. Review error messages in real-time

#### 3. OpenAPI Analysis Failures

**Problem**: Cannot parse or analyze OpenAPI specifications

**Solutions:**
- Validate OpenAPI specification format
- Check URL accessibility
- Verify file format (.yaml or .json)
- Ensure proper schema structure

**Validation Checklist:**
- OpenAPI version compatibility
- Required fields present
- URL accessibility
- Authentication requirements

#### 4. BPMN Process Integration Issues

**Problem**: BPMN process not loading or integrating

**Solutions:**
- Verify BPMN 2.0 format compliance
- Check process file integrity
- Confirm process contains required elements
- Review process execution requirements

**Format Requirements:**
- Valid BPMN 2.0 XML structure
- Process ID and name
- Executable process flag
- Proper element relationships

### Performance Optimization

#### 1. System Performance

**Optimization Tips:**
- Monitor resource usage regularly
- Adjust concurrent request limits
- Optimize database connections
- Use local models for privacy

#### 2. API Testing Performance

**Best Practices:**
- Configure appropriate timeouts
- Use connection pooling
- Implement request caching
- Monitor rate limiting

#### 3. LLM Model Selection

**Model Optimization:**
- Choose models appropriate for task complexity
- Monitor response times
- Balance accuracy vs. speed
- Consider local vs. cloud models

### Error Messages and Meanings

**Common Error Codes:**
- **400 Bad Request**: Invalid input parameters
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not available
- **500 Internal Server Error**: System error
- **503 Service Unavailable**: Service temporarily down

**Error Message Patterns:**
- "Connection refused": Service not running
- "Timeout exceeded": Request taking too long
- "Invalid API key": Authentication failure
- "File format error": Unsupported file type
- "Network error": Connectivity issues

## ⌨️ Keyboard Shortcuts and Productivity Tips

### Global Keyboard Shortcuts

**Navigation:**
- `Ctrl+/` (Cmd+/ on Mac) - Toggle search
- `F5` - Refresh current view
- `Ctrl+R` (Cmd+R on Mac) - Browser refresh
- `Escape` - Close dialogs/modals
- `Tab` - Navigate between UI elements
- `Shift+Tab` - Navigate backwards

**Common Actions:**
- `Enter` - Confirm/Execute primary action
- `Space` - Activate buttons/selections
- `Ctrl+S` (Cmd+S on Mac) - Save data
- `Ctrl+N` (Cmd+N on Mac) - Create new item
- `Ctrl+O` (Cmd+O on Mac) - Open file/dialog

### Productivity Tips

**Dashboard Navigation:**
1. **Quick Access**: Use browser bookmarks for frequently accessed routes
2. **Tab Management**: Open monitoring and analysis in separate tabs
3. **Responsive Design**: Resize browser window to test mobile view
4. **Theme Switching**: Use browser dev tools to test light/dark themes

**Efficient Workflows:**
1. **Batch Processing**: Upload multiple specifications simultaneously
2. **State Persistence**: Let Flutter's state management handle session data
3. **Real-time Updates**: Monitor progress without page refreshes
4. **Responsive Cards**: Each dashboard card can be viewed independently

**Browser Optimization:**
1. **Enable Hardware Acceleration**: Better Flutter performance
2. **Clear Cache**: Regularly clear browser cache for updates
3. **DevTools**: Use browser dev tools for debugging
4. **Extensions**: Disable ad blockers that might interfere with WebSocket

**Mobile Usage:**
1. **Touch Navigation**: Tap and swipe gestures supported
2. **Responsive Cards**: Cards stack vertically on smaller screens
3. **Mobile Keyboard**: Optimized for mobile keyboards
4. **Offline Support**: Basic functionality works offline

### Common User Issues and Quick Fixes

#### Flutter Web Specific Issues

**Problem**: White screen or loading spinner
**Solutions:**
- Check browser compatibility (Chrome 88+, Firefox 85+, Safari 14+)
- Clear browser cache and reload
- Disable browser extensions temporarily
- Check browser console for JavaScript errors

**Problem**: Slow performance
**Solutions:**
- Enable hardware acceleration in browser settings
- Close unused browser tabs
- Check system resources (RAM, CPU)
- Use incognito/private browsing mode

**Problem**: WebSocket connection issues
**Solutions:**
- Verify firewall settings allow WebSocket connections
- Check if behind corporate proxy
- Try different browser
- Restart backend services

**Problem**: File upload not working
**Solutions:**
- Check file size limits (usually 10MB per file)
- Verify supported file formats (.json, .yaml, .xml, .bpmn)
- Disable browser extensions that interfere with uploads
- Try drag-and-drop instead of file browser

#### Interface Responsiveness

**Problem**: Layout breaks on mobile
**Solutions:**
- Use landscape orientation for better tablet experience
- Zoom out to see full interface
- Use Chrome DevTools device simulation
- Report responsive design issues

**Problem**: Text too small/large
**Solutions:**
- Use browser zoom (Ctrl/Cmd + +/-)
- Adjust browser font settings
- Enable browser accessibility features
- Use high contrast mode if needed

#### Data and State Management

**Problem**: Lost data after refresh
**Solutions:**
- Use Flutter's auto-save features
- Export important results regularly
- Note that some session data is temporary
- Use browser bookmarks to save analysis URLs

**Problem**: Inconsistent state across tabs
**Solutions:**
- Use separate browser profiles if needed
- Log out and log back in to reset state
- Be aware that each tab maintains separate state
- Refresh entire application if needed

### Performance Optimization Tips

**System Performance:**
1. **Resource Monitoring**: Use the built-in system health cards
2. **Concurrent Operations**: Limit to 3-5 simultaneous analyses
3. **Memory Management**: Close completed analyses regularly
4. **Network Optimization**: Use stable internet connection

**Browser Performance:**
1. **Hardware Acceleration**: Enable in browser settings
2. **Extension Management**: Disable non-essential extensions
3. **Cache Management**: Clear cache weekly for optimal performance
4. **Memory Usage**: Monitor browser memory consumption

---

## 🔐 Security Considerations

### Data Privacy

**Local-First Architecture:**
- All processing happens locally
- No data sent to external services (except configured LLM providers)
- Secure storage of sensitive information
- Audit trail for all actions

### Access Control

**Authentication Requirements:**
- API key management for external services
- Role-based access control
- Session management
- Secure credential storage

### Compliance

**Security Standards:**
- OWASP API Security Top 10 compliance
- Industry-standard encryption
- Secure communication protocols
- Regular security updates

---

## 📚 Advanced Features

### Custom Test Development

**Test Case Creation:**
- Custom OWASP test scenarios
- Business-specific security tests
- Integration with existing test suites
- Automated test generation

### Integration Capabilities

**External Tool Integration:**
- CI/CD pipeline integration
- Bug tracking system integration
- Reporting tool integration
- Monitoring system integration

### API Extensions

**Custom Endpoints:**
- RESTful API extensions
- Webhook support
- Real-time event notifications
- Custom authentication methods

---

## 📞 Support and Resources

### Documentation

**Available Resources:**
- Technical documentation in `/docs` folder
- API reference documentation
- Video tutorials and guides
- Best practices guides

### Community Support

**Getting Help:**
- GitHub Issues for bug reports
- Community discussions
- Feature request tracking
- Documentation contributions

### Professional Services

**Enterprise Support:**
- Custom implementation services
- Training and certification
- Consulting services
- Custom feature development

---

## 🚀 Best Practices

### Effective Usage

1. **Start with Simple Tests**: Begin with basic security scans
2. **Use Real Data**: Test with realistic business scenarios
3. **Monitor Performance**: Regularly check system health
4. **Document Results**: Maintain comprehensive test records
5. **Regular Updates**: Keep systems and models current

### Security Testing Strategy

1. **Comprehensive Coverage**: Use all OWASP categories
2. **Regular Testing**: Implement continuous security testing
3. **Risk-Based Approach**: Prioritize critical business processes
4. **Team Collaboration**: Share results across security teams
5. **Continuous Improvement**: Learn from each test cycle

### Performance Optimization

1. **Resource Management**: Monitor and optimize system resources
2. **Model Selection**: Choose appropriate models for each task
3. **Batch Processing**: Use batch operations for efficiency
4. **Caching**: Implement caching for repeated operations
5. **Scaling**: Plan for increased load and usage

---

**SecurityOrchestrator GUI User Guide v1.0**
*For questions or support, please refer to the technical documentation or community resources.*