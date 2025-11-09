# Monitoring Dashboard Testing Guide

## Overview

This guide provides comprehensive testing procedures for the SecurityOrchestrator Monitoring Dashboard, covering both backend API testing and frontend functionality verification.

## Backend API Testing

### 1. **Start the Backend Application**

```bash
cd SecutityOrchestrator/Backend
./gradlew bootRun
```

Expected output: Spring Boot application starts on port 8080

### 2. **Test Health Endpoint**

```bash
curl -X GET http://localhost:8080/api/monitoring/health
```

**Expected Response**:
```json
{
  "cpuUsage": 45.2,
  "memoryUsage": 67.8,
  "diskUsage": 23.1,
  "databaseConnectionStatus": "CONNECTED",
  "averageResponseTime": 125.5,
  "activeModelCount": 3,
  "timestamp": "2025-11-09T00:24:58.935Z"
}
```

**Verification**:
- ✅ Response returns within 500ms
- ✅ All numeric values are realistic (0-100 for percentages)
- ✅ Database status is "CONNECTED"
- ✅ Timestamp is recent

### 3. **Test Dashboard Summary**

```bash
curl -X GET http://localhost:8080/api/monitoring/dashboard
```

**Expected Response**:
```json
{
  "systemHealth": {
    "cpuUsage": 45.2,
    "memoryUsage": 67.8,
    "diskUsage": 23.1,
    "databaseConnectionStatus": "CONNECTED",
    "averageResponseTime": 125.5,
    "activeModelCount": 3,
    "timestamp": "2025-11-09T00:24:58.935Z"
  },
  "metricsReport": {
    "systemHealth": { ... },
    "llmMetrics": { "responseTime": 120.0, "successRate": 0.98 },
    "apiSuccessRate": 0.97,
    "totalApiRequests": 15420,
    "activeUsers": 23,
    "uptime": "PT5H42M",
    "systemResourceUsage": { ... },
    "activeModels": [ ... ],
    "recentAlerts": [ ... ]
  },
  "activeAlerts": [ ... ],
  "activeModels": [ ... ],
  "timestamp": "2025-11-09T00:24:58.935Z"
}
```

**Verification**:
- ✅ Response contains system health
- ✅ Metrics report is present
- ✅ Active alerts section exists
- ✅ Active models list is included

### 4. **Test Historical Metrics**

```bash
curl -X GET "http://localhost:8080/api/monitoring/metrics/cpu_usage?period=1h"
```

**Expected Response**:
```json
[
  {
    "type": "cpu_usage",
    "value": 45.2,
    "timestamp": "2025-11-09T00:20:00Z"
  }
]
```

**Verification**:
- ✅ Returns array of metric points
- ✅ Each point has type, value, and timestamp
- ✅ Timestamps are within the specified period

### 5. **Test Alerts Endpoint**

```bash
curl -X GET http://localhost:8080/api/monitoring/alerts
```

**Expected Response**:
```json
[
  {
    "id": "high_cpu_usage_1",
    "type": "HIGH_CPU_USAGE",
    "message": "CPU usage above 80%",
    "severity": "WARNING",
    "timestamp": "2025-11-09T00:24:58.935Z"
  }
]
```

**Verification**:
- ✅ Returns array of alerts
- ✅ Alert severity levels are valid (INFO, WARNING, ERROR, CRITICAL)
- ✅ Timestamps are recent

### 6. **Test Models Status**

```bash
curl -X GET http://localhost:8080/api/monitoring/models
```

**Expected Response**:
```json
[
  {
    "modelId": "local-llm-1",
    "modelName": "llama-2-7b",
    "provider": "LOCAL",
    "status": "LOADED",
    "sizeGB": 1.2
  },
  {
    "modelId": "openrouter-1",
    "modelName": "gpt-3.5-turbo",
    "provider": "OPENROUTER",
    "status": "AVAILABLE"
  }
]
```

**Verification**:
- ✅ Returns array of models
- ✅ Providers are valid (LOCAL, OPENROUTER, ONNX)
- ✅ Status values are valid (LOADED, AVAILABLE, ERROR)

### 7. **Test System Resources**

```bash
curl -X GET http://localhost:8080/api/monitoring/resources
```

**Expected Response**:
```json
{
  "cpuUsage": 45.2,
  "memoryUsage": 67.8,
  "diskUsage": 23.1,
  "activeConnections": 12,
  "threadCount": 45
}
```

**Verification**:
- ✅ Returns system resource metrics
- ✅ All values are within expected ranges

### 8. **Test API Request Recording**

```bash
curl -X POST "http://localhost:8080/api/monitoring/api-request?successful=true&responseTimeMs=125"
```

**Expected Response**: `200 OK` (No content body)

**Verification**:
- ✅ Returns 200 status
- ✅ Request is processed without errors

## Frontend Testing

### 1. **Start Flutter Application**

```bash
cd SecutityOrchestrator/Frontend/security_orchestrator_frontend
flutter run
```

**Expected**: Flutter app starts and displays monitoring dashboard

### 2. **Test Dashboard UI**

**Verification Checklist**:
- ✅ System health card displays with status indicators
- ✅ Metrics grid shows performance overview
- ✅ Real-time charts render properly
- ✅ Alerts panel shows active alerts (if any)
- ✅ Models status card displays model information
- ✅ Auto-refresh functionality works
- ✅ Manual refresh button works
- ✅ Settings dialog opens

### 3. **Test Real-Time Updates**

**Verification Steps**:
1. Open browser developer console
2. Check for WebSocket connection messages
3. Verify data updates every 5-10 seconds
4. Monitor console for any connection errors

**Expected Behavior**:
- WebSocket connection established
- No JavaScript errors in console
- Data updates automatically
- Loading states display properly

### 4. **Test Responsive Design**

**Test on different screen sizes**:
- Desktop (1920x1080)
- Tablet (768x1024)
- Mobile (375x667)

**Verification**:
- ✅ Layout adapts to screen size
- ✅ Charts remain readable
- ✅ Text is appropriately sized
- ✅ Navigation works on all devices

## WebSocket Testing

### 1. **Test WebSocket Connection**

**Using WebSocket Testing Tool** (e.g., Postman or Simple WebSocket Client):

1. Connect to: `ws://localhost:8080/ws`
2. Send subscription message:

```json
{
  "metricTypes": ["cpu_usage", "memory_usage"],
  "dashboardId": "test_dashboard"
}
```

**Expected Response**:
```json
{
  "cpuUsage": 45.2,
  "memoryUsage": 67.8,
  "timestamp": "2025-11-09T00:24:58.935Z"
}
```

### 2. **Test Real-Time Messages**

**Verification**:
- ✅ Connection established successfully
- ✅ Subscription acknowledged
- ✅ Periodic updates received (every 5 seconds)
- ✅ System health updates received (every 30 seconds)
- ✅ Alert notifications work
- ✅ Connection handling works on disconnect/reconnect

## Performance Testing

### 1. **API Response Time Testing**

```bash
# Test with multiple concurrent requests
for i in {1..10}; do
  curl -w "@curl-format.txt" -o /dev/null -s "http://localhost:8080/api/monitoring/health" &
done
wait
```

**Expected Results**:
- ✅ All requests complete within 1 second
- ✅ No timeouts occur
- ✅ Response times are consistent

### 2. **Memory Usage Testing**

```bash
# Monitor memory usage during high load
ps aux | grep java
```

**Expected Results**:
- ✅ Memory usage remains stable
- ✅ No memory leaks detected
- ✅ GC activity is normal

### 3. **WebSocket Load Testing**

**Test with multiple WebSocket connections**:
1. Open 10+ browser tabs
2. Each tab connects to WebSocket
3. Monitor server resource usage
4. Verify all connections remain active

**Expected Results**:
- ✅ All connections remain active
- ✅ Server handles load without issues
- ✅ No connection drops

## Error Handling Testing

### 1. **Test Invalid API Endpoints**

```bash
curl -X GET http://localhost:8080/api/monitoring/invalid
```

**Expected Response**: `404 Not Found`

### 2. **Test Malformed Requests**

```bash
curl -X GET "http://localhost:8080/api/monitoring/metrics/invalid_metric"
```

**Expected Response**: `400 Bad Request` with error message

### 3. **Test Database Disconnection**

**Simulate database connection loss** and verify:
- ✅ System health reflects database status
- ✅ Appropriate alerts are generated
- ✅ Dashboard shows degraded status
- ✅ System recovers when database connection restored

## Integration Testing

### 1. **Test LLM Integration**

**Simulate LLM operations** and verify:
- ✅ LLM metrics are collected
- ✅ Response times are tracked
- ✅ Success/failure rates are calculated
- ✅ Model status changes are reflected

### 2. **Test Alert Integration**

**Create test scenarios**:
1. High CPU usage simulation
2. Memory leak simulation
3. Database connection issues
4. LLM service failures

**Verification**:
- ✅ Appropriate alerts are generated
- ✅ Alert severity levels are correct
- ✅ Alerts are displayed in UI
- ✅ Alert history is maintained

## Automated Testing

### 1. **Create Test Scripts**

**Backend API Test Script**:

```bash
#!/bin/bash
# test-monitoring-api.sh

BASE_URL="http://localhost:8080/api/monitoring"

echo "Testing Monitoring API..."

# Test health endpoint
echo "Testing health endpoint..."
response=$(curl -s -w "%{http_code}" -o /tmp/health.json "$BASE_URL/health")
if [ "$response" = "200" ]; then
  echo "✅ Health endpoint: PASS"
else
  echo "❌ Health endpoint: FAIL (HTTP $response)"
fi

# Test dashboard endpoint
echo "Testing dashboard endpoint..."
response=$(curl -s -w "%{http_code}" -o /tmp/dashboard.json "$BASE_URL/dashboard")
if [ "$response" = "200" ]; then
  echo "✅ Dashboard endpoint: PASS"
else
  echo "❌ Dashboard endpoint: FAIL (HTTP $response)"
fi

# Test metrics endpoint
echo "Testing metrics endpoint..."
response=$(curl -s -w "%{http_code}" -o /tmp/metrics.json "$BASE_URL/metrics/cpu_usage?period=1h")
if [ "$response" = "200" ]; then
  echo "✅ Metrics endpoint: PASS"
else
  echo "❌ Metrics endpoint: FAIL (HTTP $response)"
fi

echo "API testing complete!"
```

### 2. **Run Performance Benchmarks**

```bash
# Install Apache Bench
sudo apt-get install apache2-utils

# Benchmark health endpoint
ab -n 1000 -c 10 http://localhost:8080/api/monitoring/health

# Benchmark dashboard endpoint
ab -n 100 -c 5 http://localhost:8080/api/monitoring/dashboard
```

**Expected Results**:
- ✅ RPS > 100 for health endpoint
- ✅ RPS > 50 for dashboard endpoint
- ✅ 95th percentile response time < 500ms

## Troubleshooting

### **Common Issues and Solutions**

1. **WebSocket Connection Fails**
   - Check if WebSocket is enabled in backend
   - Verify CORS configuration
   - Check firewall settings

2. **Charts Not Rendering**
   - Verify fl_chart dependency is installed
   - Check console for JavaScript errors
   - Ensure data format is correct

3. **API Returns 500 Errors**
   - Check backend logs for errors
   - Verify database connection
   - Check for missing dependencies

4. **Real-time Updates Not Working**
   - Verify WebSocket connection
   - Check browser console for errors
   - Verify subscription message format

### **Debug Commands**

```bash
# Check if backend is running
curl http://localhost:8080/actuator/health

# View backend logs
tail -f /path/to/backend/logs/application.log

# Check WebSocket endpoint
curl -i -N -H "Connection: Upgrade" \
  -H "Upgrade: websocket" \
  -H "Sec-WebSocket-Key: SGVsbG8sIHdvcmxkIQ==" \
  -H "Sec-WebSocket-Version: 13" \
  http://localhost:8080/ws

# Monitor network traffic
sudo tcpdump -i any port 8080
```

## Success Criteria

The monitoring dashboard is considered successfully tested when:

### **Backend Criteria**
- ✅ All API endpoints return expected responses
- ✅ WebSocket connections work reliably
- ✅ System health monitoring is accurate
- ✅ Real-time data collection functions properly
- ✅ Alert system triggers appropriately
- ✅ Performance meets specified requirements

### **Frontend Criteria**
- ✅ Dashboard displays all components correctly
- ✅ Real-time charts update automatically
- ✅ Responsive design works on all devices
- ✅ Error handling displays appropriate messages
- ✅ Loading states provide good UX
- ✅ WebSocket integration is seamless

### **Integration Criteria**
- ✅ LLM performance metrics are collected
- ✅ System resource monitoring is accurate
- ✅ Historical data retention works
- ✅ Alert notifications are delivered
- ✅ Data consistency across UI and API
- ✅ No memory leaks or performance degradation

## Test Report Template

```
# Monitoring Dashboard Test Report

## Test Environment
- Backend Version: 1.0.0
- Frontend Version: 1.0.0
- Test Date: YYYY-MM-DD
- Tester: [Name]

## Test Results Summary
- Total Tests: XX
- Passed: XX
- Failed: XX
- Success Rate: XX%

## Backend API Tests
[Results for each endpoint test]

## Frontend UI Tests
[Results for each UI component test]

## WebSocket Tests
[Results for real-time functionality test]

## Performance Tests
[Results for load and performance tests]

## Issues Found
[List any issues discovered during testing]

## Recommendations
[Any recommendations for improvement]

## Conclusion
[Overall assessment of monitoring dashboard readiness]
```

## Next Steps

After successful testing:

1. **Production Deployment**
   - Configure production environment
   - Set up monitoring and alerting
   - Deploy to production servers

2. **User Training**
   - Create user documentation
   - Train operations team
   - Set up monitoring procedures

3. **Ongoing Maintenance**
   - Regular health checks
   - Performance monitoring
   - Alert threshold adjustments
   - Feature enhancements based on usage

The monitoring dashboard is now ready for production deployment and provides comprehensive observability for the SecurityOrchestrator system.