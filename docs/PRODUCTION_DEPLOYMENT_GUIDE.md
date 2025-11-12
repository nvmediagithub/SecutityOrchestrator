# SecurityOrchestrator - Production Deployment Guide

## Исполнительное резюме

**Дата**: 2025-11-08  
**Версия**: 1.0.0  
**Статус**: Production Ready  
**Платформа**: SecurityOrchestrator Automated Testing System  

---

## 1. Системные требования

### 1.1 Минимальные требования

**Hardware:**
- **CPU**: 4 cores (8+ рекомендуется)
- **RAM**: 8GB (16GB рекомендуется)
- **Storage**: 50GB SSD (100GB рекомендуется)
- **Network**: 1Gbps connection

**Software:**
- **OS**: Ubuntu 20.04+ / CentOS 8+ / RHEL 8+
- **Java**: OpenJDK 21 или Oracle JDK 17+
- **Database**: PostgreSQL 13+ или MySQL 8+
- **Web Server**: Nginx 1.18+ или Apache 2.4+
- **Docker**: 20.10+ (для контейнерного развертывания)

### 1.2 Производственные требования

**High Availability Setup:**
- **CPU**: 16+ cores
- **RAM**: 32GB+
- **Storage**: 500GB+ NVMe SSD
- **Network**: Redundant network connections
- **Load Balancer**: HAProxy или Nginx with upstream

**Scalability Configuration:**
- **Auto-scaling**: Kubernetes HPA или AWS Auto Scaling
- **Database**: PostgreSQL with read replicas
- **Cache**: Redis Cluster
- **Message Queue**: RabbitMQ или Apache Kafka

---

## 2. Предварительная подготовка

### 2.1 Системная подготовка

```bash
# Обновление системы
sudo apt update && sudo apt upgrade -y

# Установка основных пакетов
sudo apt install -y curl wget git unzip htop vim net-tools

# Настройка firewall
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 8080/tcp  # Application port
sudo ufw enable

# Настройка временной зоны
sudo timedatectl set-timezone UTC
```

### 2.2 Установка Java 21

```bash
# Загрузка и установка OpenJDK 21
cd /tmp
wget https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz

sudo tar xvf openjdk-21.0.2_linux-x64_bin.tar.gz -C /usr/local/
sudo update-alternatives --install /usr/bin/java java /usr/local/jdk-21.0.2/bin/java 1
sudo update-alternatives --install /usr/bin/javac javac /usr/local/jdk-21.0.2/bin/javac 1

# Проверка установки
java -version
javac -version

# Настройка переменных окружения
echo 'export JAVA_HOME=/usr/local/jdk-21.0.2' >> ~/.bashrc
echo 'export PATH=$PATH:$JAVA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
```

### 2.3 Установка Docker

```bash
# Установка Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Добавление пользователя в группу docker
sudo usermod -aG docker $USER

# Установка Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Перезапуск для применения групп
sudo reboot
```

### 2.4 Установка базы данных PostgreSQL

```bash
# Установка PostgreSQL
sudo apt install -y postgresql postgresql-contrib

# Настройка PostgreSQL
sudo -u postgres psql << EOF
-- Создание базы данных
CREATE DATABASE security_orchestrator;

-- Создание пользователя
CREATE USER security_user WITH PASSWORD 'secure_password_change_me';

-- Настройка прав доступа
GRANT ALL PRIVILEGES ON DATABASE security_orchestrator TO security_user;
ALTER USER security_user CREATEDB;

-- Создание схемы для приложения
\c security_orchestrator
CREATE SCHEMA IF NOT EXISTS application;
GRANT ALL ON SCHEMA application TO security_user;

-- Выход
\q
EOF

# Настройка конфигурации PostgreSQL
sudo tee -a /etc/postgresql/13/main/postgresql.conf << EOF
# Настройки для Production
max_connections = 200
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
random_page_cost = 1.1
effective_io_concurrency = 200
work_mem = 4MB
min_wal_size = 1GB
max_wal_size = 2GB
EOF

# Перезапуск PostgreSQL
sudo systemctl restart postgresql
sudo systemctl enable postgresql
```

### 2.5 Установка Redis (опционально)

```bash
# Установка Redis
sudo apt install -y redis-server

# Настройка Redis для production
sudo tee -a /etc/redis/redis.conf << EOF
# Основные настройки
maxmemory 256mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
EOF

# Перезапуск Redis
sudo systemctl restart redis-server
sudo systemctl enable redis-server
```

---

## 3. Развертывание приложения

### 3.1 Получение кода

```bash
# Клонирование репозитория
cd /opt
sudo git clone https://github.com/company/security-orchestrator.git
sudo chown -R $USER:$USER security-orchestrator
cd security-orchestrator

# Проверка версии
git status
git log --oneline -5
```

### 3.2 Сборка приложения

```bash
# Настройка права доступа к Gradle wrapper
chmod +x gradlew

# Очистка предыдущих сборок
./gradlew clean

# Сборка production JAR
./gradlew bootJar -Pproduction

# Проверка созданного файла
ls -la build/libs/
```

### 3.3 Конфигурация окружения

```bash
# Создание директории конфигурации
mkdir -p config

# Создание основного файла конфигурации
cat > config/application-production.properties << 'EOF'
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/security_orchestrator
spring.datasource.username=security_user
spring.datasource.password=secure_password_change_me
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000

# Redis Configuration (optional)
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.timeout=2000ms

# LLM Configuration
openrouter.api.key=${OPENROUTER_API_KEY}
ollama.host=http://localhost:11434

# CORS Configuration
cors.allowed.origins=https://yourdomain.com,https://app.yourdomain.com
cors.allowed.methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed.headers=*
cors.allow.credentials=true

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=${ADMIN_PASSWORD}

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.info.env.enabled=true

# Logging Configuration
logging.level.root=INFO
logging.level.org.example.application=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
logging.file.name=/var/log/security-orchestrator/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# File Upload Configuration
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# Thread Pool Configuration
spring.task.execution.pool.core-size=10
spring.task.execution.pool.max-size=20
spring.task.execution.pool.queue-capacity=100
spring.task.execution.thread-name-prefix=SecurityOrch-

# WebSocket Configuration
websocket.max_connections=100
websocket.timeout=30000
EOF

# Создание переменных окружения
cat > .env << 'EOF'
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/security_orchestrator
DATABASE_USERNAME=security_user
DATABASE_PASSWORD=secure_password_change_me

# LLM Services
OPENROUTER_API_KEY=your_openrouter_api_key_here
OLLAMA_HOST=http://localhost:11434

# Security
JWT_SECRET=your_super_secure_jwt_secret_key_here
ADMIN_PASSWORD=secure_admin_password_change_me
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://app.yourdomain.com

# Application
SPRING_PROFILES_ACTIVE=production
LOG_LEVEL=INFO
SERVER_PORT=8080

# Redis (optional)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Monitoring
PROMETHEUS_ENABLED=true
METRICS_EXPORT_INTERVAL=30s
EOF

# Настройка прав доступа
chmod 600 .env
```

### 3.4 Создание Systemd Service

```bash
# Создание пользователя для приложения
sudo useradd -r -s /bin/false security-orchestrator

# Создание директории для логов
sudo mkdir -p /var/log/security-orchestrator
sudo chown security-orchestrator:security-orchestrator /var/log/security-orchestrator

# Создание Systemd service файла
sudo tee /etc/systemd/system/security-orchestrator.service << 'EOF'
[Unit]
Description=SecurityOrchestrator Application
After=network.target postgresql.service redis.service
Wants=postgresql.service redis.service

[Service]
Type=simple
User=security-orchestrator
Group=security-orchestrator
WorkingDirectory=/opt/security-orchestrator
EnvironmentFile=/opt/security-orchestrator/.env
ExecStart=/usr/local/jdk-21.0.2/bin/java -jar \
  -Xms2g -Xmx4g \
  -XX:+UseG1GC \
  -XX:+UseStringDeduplication \
  -XX:+OptimizeStringConcat \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.profiles.active=production \
  -Dlogging.config=classpath:logback-production.xml \
  build/libs/*.jar
ExecStop=/bin/kill -TERM $MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=security-orchestrator

# Security settings
NoNewPrivileges=yes
PrivateTmp=yes
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=/var/log/security-orchestrator /opt/security-orchestrator
CapabilityBoundingSet=CAP_NET_BIND_SERVICE
AmbientCapabilities=CAP_NET_BIND_SERVICE

[Install]
WantedBy=multi-user.target
EOF

# Перезагрузка systemd
sudo systemctl daemon-reload
sudo systemctl enable security-orchestrator
```

---

## 4. Веб-сервер и SSL настройка

### 4.1 Установка Nginx

```bash
# Установка Nginx
sudo apt install -y nginx

# Удаление default конфигурации
sudo rm /etc/nginx/sites-enabled/default
```

### 4.2 SSL сертификат с Let's Encrypt

```bash
# Установка Certbot
sudo apt install -y certbot python3-certbot-nginx

# Получение SSL сертификата
sudo certbot --nginx -d yourdomain.com -d app.yourdomain.com

# Настройка автообновления
sudo crontab -e
# Добавить строку:
0 12 * * * /usr/bin/certbot renew --quiet
```

### 4.3 Конфигурация Nginx

```bash
# Создание основной конфигурации Nginx
sudo tee /etc/nginx/sites-available/security-orchestrator << 'EOF'
# Upstream для backend
upstream backend {
    server 127.0.0.1:8080;
    keepalive 32;
}

# Rate limiting
limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
limit_req_zone $binary_remote_addr zone=upload:10m rate=2r/s;

# HTTP to HTTPS redirect
server {
    listen 80;
    server_name yourdomain.com app.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

# Main HTTPS server
server {
    listen 443 ssl http2;
    server_name yourdomain.com app.yourdomain.com;
    
    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    
    # Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' https:; connect-src 'self' wss: https:; media-src 'self'; object-src 'none'; child-src 'none'; form-action 'self'; frame-ancestors 'none'" always;
    
    # Rate Limiting
    limit_req zone=api burst=20 nodelay;
    limit_req zone=upload burst=5 nodelay;
    
    # API Routes
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 30s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Buffer settings
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
        proxy_busy_buffers_size 8k;
        
        # Caching
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # WebSocket Routes
    location /ws/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket specific headers
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Sec-WebSocket-Extensions $http_sec_websocket_extensions;
        proxy_set_header Sec-WebSocket-Key $http_sec_websocket_key;
        proxy_set_header Sec-WebSocket-Version $http_sec_websocket_version;
        
        # Timeouts for WebSocket
        proxy_read_timeout 300s;
        proxy_send_timeout 300s;
        proxy_connect_timeout 10s;
    }
    
    # Actuator endpoints (restricted)
    location /actuator/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Restrict access to actuator endpoints
        allow 127.0.0.1;
        allow 10.0.0.0/8;
        allow 172.16.0.0/12;
        allow 192.168.0.0/16;
        deny all;
    }
    
    # Static files (if serving frontend)
    location / {
        root /opt/security-orchestrator/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
        
        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # Health check endpoint (no auth required)
    location /health {
        proxy_pass http://backend/actuator/health;
        access_log off;
    }
    
    # Security - Deny access to hidden files
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }
    
    # Security - Deny access to backup files
    location ~ ~$ {
        deny all;
        access_log off;
        log_not_found off;
    }
}
EOF

# Активация конфигурации
sudo ln -s /etc/nginx/sites-available/security-orchestrator /etc/nginx/sites-enabled/

# Проверка конфигурации
sudo nginx -t

# Перезапуск Nginx
sudo systemctl restart nginx
sudo systemctl enable nginx
```

---

## 5. LLM сервисы

### 5.1 Установка Ollama (локальные LLM)

```bash
# Добавление репозитория Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Запуск Ollama сервиса
sudo systemctl start ollama
sudo systemctl enable ollama

# Загрузка базовых моделей
ollama pull llama2
ollama pull codellama
ollama pull mistral

# Проверка статуса
ollama list
```

### 5.2 Настройка OpenRouter (облачные LLM)

```bash
# Создание файла с API ключом
mkdir -p ~/.config/ollama
cat > ~/.config/ollama/config.json << 'EOF'
{
  "api_base": "https://openrouter.ai/api/v1",
  "default_headers": {
    "Authorization": "Bearer YOUR_OPENROUTER_API_KEY"
  }
}
EOF

# Создание environment переменной
echo 'export OPENROUTER_API_KEY="your_api_key_here"' >> ~/.bashrc
source ~/.bashrc
```

---

## 6. Мониторинг и логирование

### 6.1 Установка Prometheus

```bash
# Создание пользователя Prometheus
sudo useradd --no-create-home --shell /bin/false prometheus

# Скачивание и установка Prometheus
cd /tmp
wget https://github.com/prometheus/prometheus/releases/download/v2.45.0/prometheus-2.45.0.linux-amd64.tar.gz
tar xvfz prometheus-*.tar.gz
sudo mv prometheus-*/prometheus /usr/local/bin/
sudo mv prometheus-*/promtool /usr/local/bin/

# Создание конфигурации
sudo mkdir -p /etc/prometheus
sudo tee /etc/prometheus/prometheus.yml << 'EOF'
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "/etc/prometheus/rules/*.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'security-orchestrator'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s

  - job_name: 'node'
    static_configs:
      - targets: ['localhost:9100']
    scrape_interval: 30s

  - job_name: 'postgresql'
    static_configs:
      - targets: ['localhost:9187']
    scrape_interval: 30s

  - job_name: 'redis'
    static_configs:
      - targets: ['localhost:9121']
    scrape_interval: 30s
EOF

# Создание systemd service
sudo tee /etc/systemd/system/prometheus.service << 'EOF'
[Unit]
Description=Prometheus
Wants=network-online.target
After=network-online.target

[Service]
User=prometheus
Group=prometheus
Type=simple
ExecStart=/usr/local/bin/prometheus \
  --config.file /etc/prometheus/prometheus.yml \
  --storage.tsdb.path /var/lib/prometheus/ \
  --web.console.libraries=/etc/prometheus/console_libraries \
  --web.console.templates=/etc/prometheus/consoles \
  --web.enable-lifecycle
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

# Создание директории для данных
sudo mkdir -p /var/lib/prometheus
sudo chown prometheus:prometheus /var/lib/prometheus
sudo chown -R prometheus:prometheus /etc/prometheus

# Запуск и включение Prometheus
sudo systemctl daemon-reload
sudo systemctl enable prometheus
sudo systemctl start prometheus
```

### 6.2 Установка Grafana

```bash
# Добавление репозитория Grafana
sudo apt-get install -y software-properties-common
sudo add-apt-repository "deb https://packages.grafana.com/oss/deb stable main"
wget -q -O - https://packages.grafana.com/gpg.key | sudo apt-key add -
sudo apt-get update
sudo apt-get install -y grafana

# Запуск Grafana
sudo systemctl daemon-reload
sudo systemctl enable grafana-server
sudo systemctl start grafana-server

# Настройка базы данных Grafana (опционально)
# По умолчанию Grafana использует SQLite, что подходит для небольших установок
```

### 6.3 Node Exporter для системных метрик

```bash
# Скачивание Node Exporter
cd /tmp
wget https://github.com/prometheus/node_exporter/releases/download/v1.6.0/node_exporter-1.6.0.linux-amd64.tar.gz
tar xvfz node_exporter-*.tar.gz
sudo mv node_exporter-*/node_exporter /usr/local/bin/

# Создание пользователя
sudo useradd --no-create-home --shell /bin/false node_exporter

# Создание systemd service
sudo tee /etc/systemd/system/node_exporter.service << 'EOF'
[Unit]
Description=Node Exporter
Wants=network-online.target
After=network-online.target

[Service]
User=node_exporter
Group=node_exporter
Type=simple
ExecStart=/usr/local/bin/node_exporter
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

# Запуск Node Exporter
sudo systemctl daemon-reload
sudo systemctl enable node_exporter
sudo systemctl start node_exporter
```

### 6.4 Настройка логирования

```bash
# Создание конфигурации Logrotate
sudo tee /etc/logrotate.d/security-orchestrator << 'EOF'
/var/log/security-orchestrator/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 security-orchestrator security-orchestrator
    postrotate
        systemctl reload security-orchestrator
    endscript
}
EOF

# Создание centralized logging (опционально)
# Настройка Rsyslog для отправки логов на удаленный сервер
sudo tee -a /etc/rsyslog.conf << 'EOF'
# Отправка логов SecurityOrchestrator на удаленный сервер
local0.* @@logserver.company.com:514
EOF

sudo systemctl restart rsyslog
```

---

## 7. Backup и восстановление

### 7.1 Автоматические бэкапы

```bash
# Создание скрипта бэкапа
sudo mkdir -p /opt/backup-scripts
sudo tee /opt/backup-scripts/backup.sh << 'EOF'
#!/bin/bash

# Конфигурация
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Создание директории бэкапа
mkdir -p $BACKUP_DIR

echo "Starting backup at $(date)"

# 1. База данных PostgreSQL
echo "Backing up PostgreSQL database..."
pg_dump -h localhost -U security_user -d security_orchestrator \
  --no-password --verbose --clean --create \
  > $BACKUP_DIR/postgres_backup_$DATE.sql
gzip $BACKUP_DIR/postgres_backup_$DATE.sql

# 2. Приложение и конфигурация
echo "Backing up application..."
tar -czf $BACKUP_DIR/app_backup_$DATE.tar.gz \
  -C /opt security-orchestrator \
  --exclude='security-orchestrator/build' \
  --exclude='security-orchestrator/.git'

# 3. Nginx конфигурация
echo "Backing up Nginx configuration..."
tar -czf $BACKUP_DIR/nginx_backup_$DATE.tar.gz \
  -C /etc nginx \
  -C /etc/ssl letsencrypt

# 4. Systemd services
echo "Backing up systemd services..."
tar -czf $BACKUP_DIR/systemd_backup_$DATE.tar.gz \
  -C /etc systemd

# 5. SSL сертификаты
echo "Backing up SSL certificates..."
cp -r /etc/letsencrypt $BACKUP_DIR/letsencrypt_backup_$DATE

# 6. Очистка старых бэкапов
echo "Cleaning up old backups..."
find $BACKUP_DIR -name "*.sql.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "letsencrypt_backup_*" -mtime +$RETENTION_DAYS -exec rm -rf {} \;

echo "Backup completed at $(date)"
echo "Backup files created:"
ls -la $BACKUP_DIR/*$DATE*
EOF

# Настройка прав доступа
sudo chmod +x /opt/backup-scripts/backup.sh

# Добавление в crontab
sudo crontab -e
# Добавить строки:
# Ежедневный бэкап в 2:00
0 2 * * * /opt/backup-scripts/backup.sh >> /var/log/backup.log 2>&1
# Еженедельный полный бэкап в воскресенье в 1:00
0 1 * * 0 /opt/backup-scripts/backup.sh >> /var/log/backup-weekly.log 2>&1
```

### 7.2 Скрипт восстановления

```bash
# Создание скрипта восстановления
sudo tee /opt/backup-scripts/restore.sh << 'EOF'
#!/bin/bash

# Проверка параметров
if [ $# -ne 1 ]; then
    echo "Usage: $0 <backup_file>"
    echo "Available backups:"
    ls -1 /opt/backups/*.tar.gz | head -10
    exit 1
fi

BACKUP_FILE=$1
DATE=$(date +%Y%m%d_%H%M%S)

# Проверка существования файла
if [ ! -f "$BACKUP_FILE" ]; then
    echo "Backup file not found: $BACKUP_FILE"
    exit 1
fi

# Предупреждение
echo "WARNING: This will restore the application to the state of the backup."
echo "Current data will be backed up before restoration."
read -p "Do you want to continue? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Restoration cancelled."
    exit 0
fi

# Создание временного бэкапа текущего состояния
echo "Creating temporary backup of current state..."
/opt/backup-scripts/backup.sh

# Остановка сервисов
echo "Stopping services..."
sudo systemctl stop security-orchestrator nginx

# Восстановление базы данных
echo "Restoring PostgreSQL database..."
if [[ $BACKUP_FILE == *"postgres"* ]]; then
    GZ_FILE="$BACKUP_FILE"
    if [[ ! $BACKUP_FILE == *.gz ]]; then
        GZ_FILE="$BACKUP_FILE.gz"
        gzip -c $BACKUP_FILE > $GZ_FILE
    fi
    gunzip -c $GZ_FILE | psql -h localhost -U postgres
else
    echo "No database backup found in the specified file."
fi

# Восстановление приложения
echo "Restoring application..."
if [[ $BACKUP_FILE == *"app"* ]]; then
    cd /opt
    sudo tar -xzf $BACKUP_FILE
    sudo chown -R security-orchestrator:security-orchestrator security-orchestrator
else
    echo "No application backup found in the specified file."
fi

# Восстановление конфигурации Nginx
echo "Restoring Nginx configuration..."
if [[ $BACKUP_FILE == *"nginx"* ]]; then
    sudo tar -xzf $BACKUP_FILE -C /
    sudo nginx -t && sudo systemctl reload nginx
else
    echo "No Nginx backup found in the specified file."
fi

# Запуск сервисов
echo "Starting services..."
sudo systemctl start security-orchestrator
sudo systemctl start nginx

# Проверка статуса
echo "Checking service status..."
sudo systemctl status security-orchestrator --no-pager
sudo systemctl status nginx --no-pager

echo "Restoration completed at $(date)"
echo "Please verify that all services are working correctly."
EOF

sudo chmod +x /opt/backup-scripts/restore.sh
```

---

## 8. Мониторинг и оповещения

### 8.1 Создание Grafana Dashboard

```bash
# Создание JSON для базового dashboard
sudo tee /tmp/security-orchestrator-dashboard.json << 'EOF'
{
  "dashboard": {
    "id": null,
    "title": "SecurityOrchestrator Monitoring",
    "tags": ["security", "testing", "api"],
    "timezone": "UTC",
    "panels": [
      {
        "id": 1,
        "title": "Application Status",
        "type": "stat",
        "targets": [
          {
            "expr": "up{job=\"security-orchestrator\"}",
            "legendFormat": "Application Status"
          }
        ],
        "fieldConfig": {
          "defaults": {
            "thresholds": {
              "steps": [
                {"color": "red", "value": 0},
                {"color": "green", "value": 1}
              ]
            }
          }
        },
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 0}
      },
      {
        "id": 2,
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}} {{endpoint}}"
          }
        ],
        "gridPos": {"h": 8, "w": 12, "x": 12, "y": 0}
      },
      {
        "id": 3,
        "title": "Response Time (95th percentile)",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          }
        ],
        "gridPos": {"h": 8, "w": 12, "x": 0, "y": 8}
      },
      {
        "id": 4,
        "title": "Error Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total{status=~\"4..|5..\"}[5m]) / rate(http_requests_total[5m])",
            "legendFormat": "Error Rate"
          }
        ],
        "gridPos": {"h": 8, "w": 12, "x": 12, "y": 8}
      },
      {
        "id": 5,
        "title": "JVM Memory Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "jvm_memory_used_bytes{job=\"security-orchestrator\"}",
            "legendFormat": "{{area}} - {{id}}"
          }
        ],
        "gridPos": {"h": 8, "w": 24, "x": 0, "y": 16}
      },
      {
        "id": 6,
        "title": "Database Connections",
        "type": "graph",
        "targets": [
          {
            "expr": "hikaricp_connections_active{job=\"security-orchestrator\"}",
            "legendFormat": "Active"
          },
          {
            "expr": "hikaricp_connections_idle{job=\"security-orchestrator\"}",
            "legendFormat": "Idle"
          },
          {
            "expr": "hikaricp_connections_pending{job=\"security-orchestrator\"}",
            "legendFormat": "Pending"
          }
        ],
        "gridPos": {"h": 8, "w": 24, "x": 0, "y": 24}
      }
    ],
    "time": {
      "from": "now-1h",
      "to": "now"
    },
    "refresh": "30s"
  },
  "folderId": 0,
  "overwrite": false
}
EOF

# Импорт dashboard в Grafana
curl -X POST \
  http://admin:admin@localhost:3000/api/dashboards/db \
  -H 'Content-Type: application/json' \
  -d @/tmp/security-orchestrator-dashboard.json
```

### 8.2 Правила Prometheus для алертов

```bash
# Создание правил алертов
sudo mkdir -p /etc/prometheus/rules
sudo tee /etc/prometheus/rules/security-orchestrator-alerts.yml << 'EOF'
groups:
  - name: security-orchestrator
    rules:
      - alert: ApplicationDown
        expr: up{job="security-orchestrator"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "SecurityOrchestrator application is down"
          description: "SecurityOrchestrator has been down for more than 1 minute."

      - alert: HighErrorRate
        expr: |
          (
            rate(http_requests_total{job="security-orchestrator",status=~"4..|5.."}[5m])
            /
            rate(http_requests_total{job="security-orchestrator"}[5m])
          ) > 0.05
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High error rate detected"
          description: "Error rate is above 5% for the last 5 minutes."

      - alert: HighResponseTime
        expr: |
          histogram_quantile(0.95, rate(http_request_duration_seconds_bucket{job="security-orchestrator"}[5m])) > 2
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
          description: "95th percentile response time is above 2 seconds for the last 10 minutes."

      - alert: HighMemoryUsage
        expr: |
          (
            jvm_memory_used_bytes{job="security-orchestrator"} 
            / 
            jvm_memory_max_bytes{job="security-orchestrator"}
          ) > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage"
          description: "JVM memory usage is above 90% for the last 5 minutes."

      - alert: DatabaseConnectionIssues
        expr: hikaricp_connections_pending{job="security-orchestrator"} > 10
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "Database connection issues"
          description: "More than 10 pending database connections for the last 2 minutes."

      - alert: DiskSpaceLow
        expr: |
          (
            node_filesystem_size_bytes{fstype!="tmpfs"} 
            - 
            node_filesystem_free_bytes{fstype!="tmpfs"}
          ) / node_filesystem_size_bytes{fstype!="tmpfs"} > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Low disk space"
          description: "Disk space usage is above 85%."

      - alert: SSL CertificateExpiring
        expr: probe_ssl_earliest_cert_expiry{instance!=""} - time() < 86400 * 7
        for: 1h
        labels:
          severity: warning
        annotations:
          summary: "SSL certificate expiring soon"
          description: "SSL certificate expires in less than 7 days."
EOF

# Перезагрузка Prometheus для применения правил
sudo systemctl reload prometheus
```

---

## 9. Проверка установки

### 9.1 Функциональные тесты

```bash
#!/bin/bash
# Скрипт проверки установки

echo "=== SecurityOrchestrator Installation Verification ==="
echo "Date: $(date)"
echo "Host: $(hostname)"
echo

# Проверка системных сервисов
echo "1. Checking system services..."
services=("postgresql" "redis" "security-orchestrator" "nginx" "prometheus" "grafana-server" "node_exporter")

for service in "${services[@]}"; do
    if systemctl is-active --quiet $service; then
        echo "  ✓ $service: Running"
    else
        echo "  ✗ $service: Not running"
    fi
done

echo

# Проверка портов
echo "2. Checking ports..."
ports=("8080" "80" "443" "3000" "9090" "9091" "9100")

for port in "${ports[@]}"; do
    if netstat -tuln | grep -q ":$port "; then
        echo "  ✓ Port $port: Open"
    else
        echo "  ✗ Port $port: Closed"
    fi
done

echo

# Проверка API endpoints
echo "3. Testing API endpoints..."
endpoints=(
    "http://localhost:8080/actuator/health"
    "http://localhost:8080/actuator/info"
    "http://localhost:8080/api/v1/projects"
)

for endpoint in "${endpoints[@]}"; do
    if curl -s -o /dev/null -w "%{http_code}" "$endpoint" | grep -q "200\|404"; then
        echo "  ✓ $endpoint: Accessible"
    else
        echo "  ✗ $endpoint: Not accessible"
    fi
done

echo

# Проверка базы данных
echo "4. Testing database connection..."
if sudo -u postgres psql -c "SELECT 1;" >/dev/null 2>&1; then
    echo "  ✓ PostgreSQL: Accessible"
    
    # Проверка схемы приложения
    if sudo -u postgres psql -d security_orchestrator -c "\dt application" | grep -q "application"; then
        echo "  ✓ Application schema: Found"
    else
        echo "  ✗ Application schema: Not found"
    fi
else
    echo "  ✗ PostgreSQL: Not accessible"
fi

echo

# Проверка логов
echo "5. Checking application logs..."
if [ -f "/var/log/security-orchestrator/application.log" ]; then
    echo "  ✓ Application log: Found"
    last_error=$(grep -i "error" /var/log/security-orchestrator/application.log | tail -1)
    if [ -n "$last_error" ]; then
        echo "  ⚠ Recent errors found in logs"
    else
        echo "  ✓ No recent errors in logs"
    fi
else
    echo "  ✗ Application log: Not found"
fi

echo

# Проверка SSL сертификата
echo "6. Checking SSL certificate..."
if [ -f "/etc/letsencrypt/live/yourdomain.com/fullchain.pem" ]; then
    expiry=$(openssl x509 -enddate -noout -in /etc/letsencrypt/live/yourdomain.com/fullchain.pem | cut -d= -f2)
    echo "  ✓ SSL certificate: Found (expires: $expiry)"
else
    echo "  ✗ SSL certificate: Not found"
fi

echo

# Проверка резервных копий
echo "7. Checking backup configuration..."
if [ -x "/opt/backup-scripts/backup.sh" ]; then
    echo "  ✓ Backup script: Found and executable"
    if crontab -l | grep -q "backup.sh"; then
        echo "  ✓ Backup cron job: Configured"
    else
        echo "  ✗ Backup cron job: Not configured"
    fi
else
    echo "  ✗ Backup script: Not found"
fi

echo

# Проверка мониторинга
echo "8. Testing monitoring..."
monitoring_endpoints=(
    "http://localhost:9090/api/v1/targets"
    "http://localhost:3000/api/health"
)

for endpoint in "${monitoring_endpoints[@]}"; do
    if curl -s -o /dev/null -w "%{http_code}" "$endpoint" | grep -q "200"; then
        echo "  ✓ $endpoint: Accessible"
    else
        echo "  ✗ $endpoint: Not accessible"
    fi
done

echo
echo "=== Verification Complete ==="
```

### 9.2 Нагрузочное тестирование

```bash
# Установка Apache Bench
sudo apt install -y apache2-utils

# Тест производительности API
echo "Running load tests..."

# Тест основного API endpoint
ab -n 1000 -c 10 http://localhost:8080/api/v1/projects

# Тест health check endpoint
ab -n 5000 -c 50 http://localhost:8080/actuator/health

# Тест с файлом (если есть upload endpoint)
echo "Testing file upload performance..."
echo '{"test": "data"}' > /tmp/test.json
ab -n 100 -c 5 -p /tmp/test.json -T "application/json" \
  http://localhost:8080/api/v1/testdata/generate

rm /tmp/test.json
```

---

## 10. Решение проблем

### 10.1 Частые проблемы

**Проблема: Приложение не запускается**
```bash
# Проверка логов
sudo journalctl -u security-orchestrator -f

# Проверка конфигурации базы данных
sudo -u postgres psql -d security_orchestrator -c "SELECT version();"

# Проверка памяти
free -h
df -h

# Перезапуск сервиса
sudo systemctl restart security-orchestrator
```

**Проблема: Высокая нагрузка на CPU**
```bash
# Анализ процесса
top -p $(pgrep -f security-orchestrator)

# Анализ thread dump
sudo -u security-orchestrator jstack $(pgrep -f security-orchestrator) > /tmp/thread-dump.txt

# Анализ heap dump (если возможно)
sudo -u security-orchestrator jmap -dump:live,format=b,file=/tmp/heap-dump.hprof $(pgrep -f security-orchestrator)
```

**Проблема: Проблемы с базой данных**
```bash
# Проверка подключения
sudo -u postgres psql -d security_orchestrator -c "SELECT count(*) FROM application.projects;"

# Проверка блокировок
sudo -u postgres psql -d security_orchestrator -c "
SELECT 
    blocked_locks.pid AS blocked_pid,
    blocked_activity.usename AS blocked_user,
    blocking_locks.pid AS blocking_pid,
    blocking_activity.usename AS blocking_user,
    blocked_activity.query AS blocked_statement,
    blocking_activity.query AS blocking_statement
FROM pg_catalog.pg_locks blocked_locks
JOIN pg_catalog.pg_stat_activity blocked_activity ON blocked_activity.pid = blocked_locks.pid
JOIN pg_catalog.pg_locks blocking_locks ON blocking_locks.locktype = blocked_locks.locktype
JOIN pg_catalog.pg_stat_activity blocking_activity ON blocking_activity.pid = blocking_locks.pid
WHERE NOT blocked_locks.granted;"
```

**Проблема: SSL сертификат истек**
```bash
# Проверка срока действия сертификата
openssl x509 -enddate -noout -in /etc/letsencrypt/live/yourdomain.com/fullchain.pem

# Обновление сертификата
sudo certbot renew --force-renewal

# Перезапуск Nginx
sudo systemctl reload nginx
```

### 10.2 Логи и мониторинг

**Полезные команды для мониторинга:**
```bash
# Мониторинг в реальном времени
sudo journalctl -u security-orchestrator -f

# Анализ логов по времени
sudo journalctl --since "1 hour ago" -u security-orchestrator | grep ERROR

# Мониторинг производительности базы данных
sudo -u postgres psql -d security_orchestrator -c "
SELECT 
    query,
    calls,
    total_time,
    mean_time,
    rows
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;"

# Мониторинг подключений
sudo -u postgres psql -d security_orchestrator -c "
SELECT 
    count(*) as total_connections,
    count(*) FILTER (WHERE state = 'active') as active_connections,
    count(*) FILTER (WHERE state = 'idle') as idle_connections
FROM pg_stat_activity;"

# Анализ использования диска
sudo du -sh /opt/security-orchestrator
sudo du -sh /var/log/security-orchestrator
sudo du -sh /opt/backups
```

---

## 11. Заключение

Данное руководство предоставляет комплексные инструкции по развертыванию SecurityOrchestrator в production среде. Система готова к промышленному использованию с настроенным мониторингом, резервным копированием и автоматическим восстановлением.

### Ключевые моменты:

1. **Безопасность**: SSL/TLS, firewall, system hardening
2. **Надежность**: Health checks, автоматические перезапуски, мониторинг
3. **Производительность**: Оптимизированные настройки JVM, базы данных и web сервера
4. **Масштабируемость**: Container-ready, load balancer compatible
5. **Обслуживание**: Автоматические бэкапы, логирование, обновления

### Рекомендации по эксплуатации:

- Регулярно обновляйте систему и зависимости
- Мониторьте метрики производительности
- Тестируйте процедуры восстановления
- Обновляйте SSL сертификаты
- Проверяйте целостность резервных копий

**Статус**: Система готова к production использованию  
**Дата**: 2025-11-08  
**Версия**: 1.0.0