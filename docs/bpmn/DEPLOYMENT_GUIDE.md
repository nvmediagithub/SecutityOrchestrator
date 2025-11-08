# BPMN Analysis System - Deployment Guide

## Table of Contents

1. [Deployment Overview](#deployment-overview)
2. [Production Environment Requirements](#production-environment-requirements)
3. [Infrastructure Setup](#infrastructure-setup)
4. [Application Deployment](#application-deployment)
5. [Security Configuration](#security-configuration)
6. [Monitoring and Observability](#monitoring-and-observability)
7. [Scaling and Performance](#scaling-and-performance)
8. [Backup and Disaster Recovery](#backup-and-disaster-recovery)
9. [Production Checklist](#production-checklist)

## Deployment Overview

This guide provides comprehensive instructions for deploying the BPMN Analysis System in production environments. The system supports multiple deployment models including on-premises, hybrid cloud, and containerized deployments.

### Supported Deployment Models

- **Container Deployment**: Docker, Kubernetes, OpenShift
- **Traditional Deployment**: Virtual Machines, Physical Servers
- **Cloud-Native**: AWS, Azure, GCP managed services
- **Hybrid**: Combination of on-premises and cloud components

### Architecture Patterns

1. **Single Instance**: Small-scale deployments with single application instance
2. **Load Balanced**: Multiple application instances with load balancer
3. **Microservices**: Distributed architecture with separate services
4. **High Availability**: Multi-zone deployment with automatic failover

## Production Environment Requirements

### Hardware Requirements

#### Minimum Configuration
- **CPU**: 4 cores (2.4 GHz or higher)
- **Memory**: 8 GB RAM
- **Storage**: 100 GB SSD
- **Network**: 1 Gbps bandwidth

#### Recommended Configuration
- **CPU**: 8+ cores (3.0 GHz or higher)
- **Memory**: 16+ GB RAM
- **Storage**: 500 GB NVMe SSD
- **Network**: 10 Gbps bandwidth

#### High-Performance Configuration
- **CPU**: 16+ cores (3.5 GHz or higher)
- **Memory**: 32+ GB RAM
- **Storage**: 1 TB NVMe SSD (RAID 10)
- **Network**: 10+ Gbps bandwidth

### Software Requirements

#### Operating Systems
- **Linux**: Ubuntu 20.04+, RHEL 8+, CentOS 8+
- **Windows**: Windows Server 2019, 2022
- **Container**: Any OS supporting Docker/Podman

#### Runtime Environment
- **Java**: OpenJDK 21 or Oracle JDK 21
- **Database**: PostgreSQL 13+, MySQL 8+, Oracle 19c
- **Cache**: Redis 6.0+ or Memcached 1.6+
- **Web Server**: Nginx 1.20+, Apache 2.4+

### Network Requirements

#### Port Configuration
- **8080**: REST API (configurable)
- **8081**: Management/Health endpoints
- **5432**: PostgreSQL (internal)
- **6379**: Redis (internal)
- **9090**: Prometheus metrics
- **3000**: Grafana dashboard

#### Security Requirements
- **TLS 1.2+**: For all external communications
- **Firewall**: Restrict access to essential ports only
- **VPN/Private Network**: For database and cache connections
- **Load Balancer**: SSL termination and request routing

## Infrastructure Setup

### Database Setup

#### PostgreSQL Configuration
```sql
-- Create database and user
CREATE DATABASE bpmn_analysis;
CREATE USER bpmn_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE bpmn_analysis TO bpmn_user;

-- Configure for production
ALTER SYSTEM SET max_connections = '200';
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET work_mem = '4MB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;
ALTER SYSTEM SET random_page_cost = 1.1;
ALTER SYSTEM SET effective_io_concurrency = 200;
```

#### Database Schema Setup
```bash
# Run database migrations
./gradlew flywayMigrate

# Or manually apply SQL scripts
psql -U bpmn_user -d bpmn_analysis -f src/main/resources/db/migration/V1__Initial_schema.sql
```

### Redis Setup

#### Redis Configuration
```conf
# redis.conf
port 6379
bind 127.0.0.1
requirepass secure_redis_password
maxmemory 512mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

#### Redis Cluster Setup (High Availability)
```bash
# Create Redis cluster
redis-cli --cluster create node1:7000 node2:7001 node3:7002 \
  --cluster-replicas 1 -a cluster_auth_password
```

### Load Balancer Setup

#### Nginx Configuration
```nginx
# nginx.conf
upstream bpmn_backend {
    server 10.0.1.10:8080 weight=1 max_fails=3 fail_timeout=30s;
    server 10.0.1.11:8080 weight=1 max_fails=3 fail_timeout=30s;
    server 10.0.1.12:8080 weight=1 max_fails=3 fail_timeout=30s backup;
}

server {
    listen 443 ssl http2;
    server_name bpmn.yourcompany.com;
    
    ssl_certificate /etc/ssl/certs/bpmn.crt;
    ssl_certificate_key /etc/ssl/private/bpmn.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    
    location / {
        proxy_pass http://bpmn_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 30s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Health checks
        proxy_next_upstream error timeout invalid_header http_500 http_502 http_503;
    }
    
    location /api/v1/bpmn/health {
        proxy_pass http://bpmn_backend;
        access_log off;
    }
}
```

## Application Deployment

### Container Deployment

#### Docker Deployment
```dockerfile
# Dockerfile
FROM openjdk:21-jre-slim

# Create app user
RUN addgroup --system bpmn && adduser --system --ingroup bpmn bpmn

# Install dependencies
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy application
COPY target/bpmn-analysis-system-*.jar app.jar
COPY docker/entrypoint.sh /entrypoint.sh

# Set permissions
RUN chown -R bpmn:bpmn /app.jar /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Switch to app user
USER bpmn

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/v1/bpmn/health || exit 1

# Start application
ENTRYPOINT ["/entrypoint.sh"]
CMD ["java", "-jar", "/app.jar"]
```

#### Kubernetes Deployment
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bpmn-analysis-system
  namespace: bpmn-system
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: bpmn-analysis-system
  template:
    metadata:
      labels:
        app: bpmn-analysis-system
    spec:
      containers:
      - name: bpmn-analysis
        image: bpmn-analysis:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: bpmn-db-secret
              key: url
        - name: DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: bpmn-db-secret
              key: password
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /api/v1/bpmn/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /api/v1/bpmn/health/ready
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: bpmn-analysis-service
  namespace: bpmn-system
spec:
  selector:
    app: bpmn-analysis-system
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: ClusterIP
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: bpmn-analysis-ingress
  namespace: bpmn-system
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - bpmn.yourcompany.com
    secretName: bpmn-tls-secret
  rules:
  - host: bpmn.yourcompany.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: bpmn-analysis-service
            port:
              number: 80
```

### Application Configuration

#### Production Application Properties
```yaml
# application-prod.yml
server:
  port: 8080
  ssl:
    enabled: true
    key-store: classpath:keystore/keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12

spring:
  profiles:
    active: prod
  
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/bpmn_analysis}
    username: ${DATABASE_USERNAME:bpmn_user}
    password: ${DATABASE_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

  cache:
    type: redis
    redis:
      time-to-live: 3600000
      use-key-prefix: true

  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}
    database: 0
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 8
        min-idle: 0

# BPMN Analysis Configuration
bpmn:
  analysis:
    timeout: 300s
    max-concurrent-analyses: 10
    cache:
      enabled: true
      ttl: 3600s
    ai:
      enabled: true
      provider: openrouter
      model: anthropic/claude-3-haiku
      max-tokens: 2048
      temperature: 0.1
      timeout: 30s

# Security Configuration
security:
  require-https: true
  cors:
    allowed-origins: https://bpmn.yourcompany.com
  api:
    key:
      header: X-API-Key
    rate-limit:
      enabled: true
      requests-per-minute: 100

# Logging Configuration
logging:
  level:
    root: WARN
    com.securityorchestrator.bpmn: INFO
    org.springframework.web: WARN
    org.camunda.bpm: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /var/log/bpmn-analysis/application.log
    max-size: 100MB
    max-history: 30
    total-size-cap: 1GB

# Management Endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

## Security Configuration

### SSL/TLS Configuration

#### Certificate Management
```bash
# Generate self-signed certificate for testing
keytool -genkeypair -alias bpmn-analysis -keyalg RSA -keysize 2048 \
  -validity 365 -keystore keystore.p12 -storetype PKCS12 \
  -dname "CN=bpmn.yourcompany.com,O=Your Company,L=City,ST=State,C=US"

# Or use Let's Encrypt
certbot certonly --standalone -d bpmn.yourcompany.com
```

### Authentication and Authorization

#### API Key Management
```yaml
# API Key configuration
security:
  api:
    keys:
      - name: "production-key-1"
        value: "prod-key-12345"
        permissions: ["READ", "WRITE", "ADMIN"]
        expiry: "2024-12-31"
      - name: "integration-key"
        value: "int-key-67890"
        permissions: ["READ", "WRITE"]
        expiry: "2024-06-30"
```

#### JWT Configuration
```yaml
security:
  jwt:
    secret: ${JWT_SECRET:your-jwt-secret-key}
    expiration: 3600 # 1 hour
    refresh-expiration: 86400 # 24 hours
    issuer: bpmn-analysis-system
    audiences: ["bpmn-web", "bpmn-api"]
```

### Network Security

#### Firewall Rules
```bash
# UFW configuration
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow from 10.0.0.0/8 to any port 5432
ufw allow from 10.0.0.0/8 to any port 6379
ufw enable
```

#### VPN Configuration
```bash
# OpenVPN server configuration
port 1194
proto udp
dev tun
ca ca.crt
cert server.crt
key server.key
dh dh.pem
server 10.8.0.0 255.255.255.0
ifconfig-pool-persist ipp.txt
push "route 10.0.0.0 255.255.255.0"
client-config-dir /etc/openvpn/ccd
```

## Monitoring and Observability

### Health Checks

#### Application Health Endpoints
```bash
# Basic health check
curl -f http://localhost:8080/api/v1/bpmn/health

# Detailed health check
curl -H "Authorization: Bearer admin-token" \
  http://localhost:8080/api/v1/bpmn/health/details

# Database health check
curl -f http://localhost:8080/api/v1/bpmn/health/db

# AI service health check
curl -f http://localhost:8080/api/v1/bpmn/health/ai
```

### Metrics and Monitoring

#### Prometheus Metrics
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'bpmn-analysis'
    static_configs:
      - targets: ['bpmn-app:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-exporter:9187']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']
```

#### Grafana Dashboard
```json
{
  "dashboard": {
    "title": "BPMN Analysis System",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}} {{status}}"
          }
        ]
      },
      {
        "title": "Analysis Duration",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(bpmn_analysis_duration_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          }
        ]
      },
      {
        "title": "Active Analyses",
        "type": "stat",
        "targets": [
          {
            "expr": "bpmn_active_analyses",
            "legendFormat": "Active"
          }
        ]
      }
    ]
  }
}
```

### Logging and Alerting

#### ELK Stack Configuration
```yaml
# logstash.conf
input {
  file {
    path => "/var/log/bpmn-analysis/*.log"
    start_position => "beginning"
    codec => "json"
  }
}

filter {
  if [logger_name] =~ /com\.securityorchestrator\.bpmn/ {
    grok {
      match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} \[%{DATA:thread}\] %{LOGLEVEL:level} %{DATA:logger} - %{GREEDYDATA:msg}" }
    }
    
    date {
      match => [ "timestamp", "ISO8601" ]
    }
    
    if [level] == "ERROR" {
      mutate {
        add_tag => [ "alert" ]
      }
    }
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "bpmn-logs-%{+YYYY.MM.dd}"
  }
  
  if "alert" in [tags] {
    http {
      url => "http://alertmanager:9093/api/v1/alerts"
      http_method => "post"
      content_type => "application/json"
    }
  }
}
```

## Scaling and Performance

### Horizontal Scaling

#### Application Scaling
```bash
# Scale application instances
kubectl scale deployment bpmn-analysis-system --replicas=5

# Or using Docker Compose
docker-compose up --scale bpmn-app=3
```

#### Database Scaling
```sql
-- Read replica setup
-- On primary
ALTER SYSTEM SET wal_level = replica;
ALTER SYSTEM SET max_wal_senders = 3;
ALTER SYSTEM SET archive_mode = on;

-- On replica
ALTER SYSTEM SET hot_standby = on;
ALTER SYSTEM SET hot_standby_feedback = on;
```

### Performance Optimization

#### JVM Tuning
```bash
# Production JVM options
java -Xms4g -Xmx8g \
     -XX:+UseG1GC \
     -XX:G1HeapRegionSize=16m \
     -XX:MaxGCPauseMillis=200 \
     -XX:+UseStringDeduplication \
     -XX:+OptimizeStringConcat \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/tmp/heap-dump.hprof \
     -Djava.security.egd=file:/dev/./urandom \
     -Duser.timezone=UTC \
     -Dfile.encoding=UTF-8 \
     -Djava.awt.headless=true \
     -jar bpmn-analysis-system.jar
```

#### Database Optimization
```sql
-- Performance indexes
CREATE INDEX CONCURRENTLY idx_analysis_status ON security_analyses(status);
CREATE INDEX CONCURRENTLY idx_analysis_created ON security_analyses(created_at);
CREATE INDEX CONCURRENTLY idx_process_name ON bpmn_processes(name);

-- Query optimization
SET work_mem = '256MB';
SET shared_buffers = '1GB';
SET effective_cache_size = '4GB';
```

## Backup and Disaster Recovery

### Database Backup

#### Automated Backup Script
```bash
#!/bin/bash
# backup-database.sh

BACKUP_DIR="/backup/postgres"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/bpmn_analysis_$DATE.sql"

# Create backup directory
mkdir -p $BACKUP_DIR

# Create database backup
pg_dump -U bpmn_user -h localhost bpmn_analysis > $BACKUP_FILE

# Compress backup
gzip $BACKUP_FILE

# Remove backups older than 30 days
find $BACKUP_DIR -name "*.gz" -mtime +30 -delete

# Upload to S3 (optional)
aws s3 cp $BACKUP_FILE.gz s3://bpmn-backups/database/
```

#### Continuous WAL Archiving
```sql
-- On primary database
ALTER SYSTEM SET archive_mode = on;
ALTER SYSTEM SET archive_command = 'aws s3 cp %p s3://bpmn-wal-archive/%f';
ALTER SYSTEM SET wal_level = replica;
```

### Configuration Backup

#### GitOps for Configuration
```bash
# Store configuration in Git
git add -A
git commit -m "Update production configuration"
git push origin main
```

### Disaster Recovery Plan

#### Recovery Procedures
1. **Database Recovery**
   ```bash
   # Restore from backup
   psql -U postgres -c "DROP DATABASE bpmn_analysis;"
   psql -U postgres -c "CREATE DATABASE bpmn_analysis OWNER bpmn_user;"
   gunzip -c backup_20241108_120000.sql.gz | psql -U bpmn_user -d bpmn_analysis
   ```

2. **Application Recovery**
   ```bash
   # Rollback to previous version
   kubectl rollout undo deployment/bpmn-analysis-system
   ```

3. **Infrastructure Recovery**
   ```bash
   # Rebuild infrastructure
   terraform apply -var="environment=prod" -var="rebuild=true"
   ```

## Production Checklist

### Pre-Deployment Checklist

- [ ] Infrastructure requirements verified
- [ ] SSL certificates installed and configured
- [ ] Database schemas created and migrated
- [ ] Environment variables configured
- [ ] Security policies implemented
- [ ] Monitoring and alerting configured
- [ ] Backup and recovery procedures tested
- [ ] Performance baselines established
- [ ] Load testing completed
- [ ] Security scanning passed

### Deployment Checklist

- [ ] Application deployed to staging environment
- [ ] All health checks passing
- [ ] Database migrations applied
- [ ] Cache warming completed
- [ ] Monitoring dashboards configured
- [ ] Log aggregation working
- [ ] Alert rules tested
- [ ] Performance monitoring active
- [ ] Security controls verified
- [ ] Documentation updated

### Post-Deployment Checklist

- [ ] Application accessible via load balancer
- [ ] API endpoints responding correctly
- [ ] WebSocket connections working
- [ ] Database connections stable
- [ ] Cache functioning properly
- [ ] AI services operational
- [ ] Logs flowing to ELK stack
- [ ] Metrics collection active
- [ ] Alert notifications working
- [ ] Backup jobs scheduled and running

### Monitoring Checklist

- [ ] Health check endpoints responding
- [ ] Application metrics being collected
- [ ] Database performance monitored
- [ ] Cache hit rates acceptable
- [ ] Memory usage within limits
- [ ] CPU utilization normal
- [ ] Disk space adequate
- [ ] Network latency acceptable
- [ ] Error rates within threshold
- [ ] Response times meeting SLA

This deployment guide provides comprehensive instructions for successfully deploying the BPMN Analysis System in production environments. Always test deployment procedures in a staging environment before applying to production.