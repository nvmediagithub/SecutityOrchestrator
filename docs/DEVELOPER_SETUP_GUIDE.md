# SecurityOrchestrator - Руководство по настройке среды разработки

## Исполнительное резюме

**Дата**: 2025-11-21  
**Версия**: 1.0.0  
**Цель**: Полные инструкции по настройке локальной среды разработки  
**Платформа**: SecurityOrchestrator - Система автоматического тестирования безопасности  

---

## 1. Системные требования для разработки

### 1.1 Минимальные требования

**Hardware:**
- **CPU**: 4 cores (Intel/AMD x64)
- **RAM**: 8GB (16GB рекомендуется)
- **Storage**: 20GB свободного места
- **Network**: Стабильное подключение к интернету

**Software:**
- **OS**: macOS 12+, Ubuntu 20.04+, Windows 10+ с WSL2
- **Java**: OpenJDK 21 или Oracle JDK 17+
- **Node.js**: 18.x или выше
- **Python**: 3.9+ (опционально)

### 1.2 Рекомендуемые инструменты

**IDE и редакторы:**
- IntelliJ IDEA Ultimate (для Java/Spring Boot)
- Visual Studio Code (для Flutter/Dart)
- Android Studio (для Flutter mobile development)
- Postman (для API тестирования)

**Системы контроля версий:**
- Git 2.30+
- GitHub Desktop (опционально)

---

## 2. Установка основных компонентов

### 2.1 Установка Java Development Kit

#### Windows (с WSL2)
```bash
# В WSL2 терминале
sudo apt update && sudo apt upgrade -y

# Установка OpenJDK 21
sudo apt install -y openjdk-21-jdk

# Проверка установки
java -version
javac -version

# Настройка JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$PATH:$JAVA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
```

#### macOS
```bash
# Установка через Homebrew
brew install openjdk@21

# Создание симлинка (опционально)
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# Настройка JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc
```

#### Linux (Ubuntu/Debian)
```bash
# Добавление репозитория AdoptOpenJDK
wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | sudo apt-key add -
sudo add-apt-repository https://adoptopenjdk.jfrog.io/adoptopenjdk/deb

# Установка OpenJDK 21
sudo apt update
sudo apt install -y adoptopenjdk-21-hotspot

# Проверка
java -version
javac -version
```

### 2.2 Установка Gradle

```bash
# Загрузка последней версии Gradle
cd /tmp
wget https://services.gradle.org/distributions/gradle-8.7-bin.zip
unzip gradle-8.7-bin.zip
sudo mv gradle-8.7 /opt/gradle

# Настройка PATH
echo 'export GRADLE_HOME=/opt/gradle' >> ~/.bashrc
echo 'export PATH=$PATH:$GRADLE_HOME/bin' >> ~/.bashrc
source ~/.bashrc

# Проверка установки
gradle --version
```

### 2.3 Установка Node.js и npm

#### Windows (WSL2)
```bash
# Установка через NodeSource repository
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Проверка
node --version
npm --version
```

#### macOS
```bash
# Установка через Homebrew
brew install node@18

# Проверка
node --version
npm --version
```

#### Linux
```bash
# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# CentOS/RHEL
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs
```

### 2.4 Установка Flutter (для frontend разработки)

#### Windows
```powershell
# Загрузка Flutter SDK
$url = "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.16.5-stable.zip"
Invoke-WebRequest -Uri $url -OutFile "flutter.zip"

# Извлечение в Program Files
Expand-Archive -Path "flutter.zip" -DestinationPath "C:\Flutter"

# Добавление в PATH
$env:PATH += ";C:\Flutter\bin"

# Постоянное добавление в PATH
[Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";C:\Flutter\bin", "Machine")
```

#### macOS
```bash
# Загрузка Flutter SDK
cd /usr/local/lib
sudo wget https://storage.googleapis.com/flutter_infra_release/releases/stable/macos/flutter_macos_3.16.5-stable.zip
sudo unzip flutter_macos_3.16.5-stable.zip

# Добавление в PATH
echo 'export PATH="$PATH:/usr/local/lib/flutter/bin"' >> ~/.zshrc
source ~/.zshrc

# Принятие лицензий
sudo xcodebuild -license accept
sudo xcode-select --install
```

#### Linux (Ubuntu/Debian)
```bash
# Установка зависимостей
sudo apt update
sudo apt install -y curl git unzip xz-utils zip libglu1-mesa

# Загрузка Flutter SDK
cd /opt
sudo wget https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.16.5-stable.tar.xz
sudo tar xf flutter_linux_3.16.5-stable.tar.xz

# Добавление в PATH
echo 'export PATH="$PATH:/opt/flutter/bin"' >> ~/.bashrc
source ~/.bashrc

# Настройка Flutter
flutter doctor
```

### 2.5 Установка Ollama (для локальных LLM)

```bash
# Установка Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Запуск Ollama сервиса
ollama serve

# В отдельном терминале - загрузка моделей
ollama pull qwen3-coder:480b-cloud
ollama pull codellama:7b-instruct-q4_0
ollama pull deepseek-r1t-chimera:free

# Проверка установленных моделей
ollama list

# Тест работы Ollama
curl -X POST http://localhost:11434/api/generate -d '{
  "model": "qwen3-coder:480b-cloud",
  "prompt": "Hello, test model",
  "stream": false
}'
```

---

## 3. Backend разработка - Настройка проекта

### 3.1 Клонирование репозитория

```bash
# Клонирование основного репозитория
git clone https://github.com/company/security-orchestrator.git
cd security-orchestrator

# Проверка структуры проекта
ls -la
tree -L 3
```

### 3.2 Импорт проекта в IntelliJ IDEA

1. **Открыть IntelliJ IDEA**
2. **File → Open** → выбрать папку `Backend`
3. **Выбрать "Import project from external model" → Gradle**
4. **Настроить Gradle JVM**: Use Gradle 'gradle-8.7' and JDK 21
5. **Снять галочку** с "Create separate module per source set"
6. **Нажать Finish**

### 3.3 Настройка базы данных для разработки

#### H2 In-Memory (по умолчанию)
```properties
# В файле src/main/resources/application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console для отладки
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### PostgreSQL (опционально для более сложных тестов)
```bash
# Установка PostgreSQL для разработки
sudo apt install -y postgresql postgresql-contrib

# Запуск PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Создание пользователя и базы данных
sudo -u postgres psql << EOF
CREATE DATABASE security_orchestrator_dev;
CREATE USER dev_user WITH PASSWORD 'dev_password';
GRANT ALL PRIVILEGES ON DATABASE security_orchestrator_dev TO dev_user;
\q
EOF

# Конфигурация для разработки
cat > src/main/resources/application-dev.properties << 'EOF'
spring.datasource.url=jdbc:postgresql://localhost:5432/security_orchestrator_dev
spring.datasource.username=dev_user
spring.datasource.password=dev_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA настройки для разработки
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Spring DevTools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
EOF
```

### 3.4 Первый запуск backend приложения

```bash
# Переход в директорию Backend
cd Backend

# Очистка и сборка проекта
./gradlew clean build

# Запуск приложения
./gradlew bootRun

# Или запуск с профилем development
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 3.5 Проверка работы backend

1. **Открыть браузер** и перейти на `http://localhost:8090/actuator/health`
2. **Должно вернуться JSON** с информацией о состоянии приложения
3. **Swagger UI**: `http://localhost:8090/swagger-ui.html`
4. **H2 Console**: `http://localhost:8090/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`)

### 3.6 Дополнительные Gradle команды для разработки

```bash
# Запуск тестов
./gradlew test

# Запуск интеграционных тестов
./gradlew integrationTest

# Форматирование кода
./gradlew spotlessApply

# Проверка кода (checkstyle, spotbugs)
./gradlew check

# Создание JAR файла
./gradlew bootJar

# Создание полной документации
./gradlew dokkaHtml

# Очистка build файлов
./gradlew clean
```

---

## 4. Frontend разработка - Настройка Flutter

### 4.1 Настройка Flutter проекта

```bash
# Переход в директорию frontend
cd Frontend/flutter-app

# Проверка Flutter установки
flutter doctor

# Получение зависимостей
flutter pub get

# Анализ кода
flutter analyze

# Проверка кодстайла
dart fix --dry-run
```

### 4.2 Настройка Android/Desktop разработки

#### Android Setup
```bash
# Установка Android Studio и Android SDK
# Скачать Android Studio с https://developer.android.com/studio

# Настройка Android SDK в Flutter
flutter config --android-studio-dir /path/to/android-studio
flutter config --android-sdk /path/to/android-sdk

# Проверка лицензий Android
flutter doctor --android-licenses

# Создание Android эмулятора
flutter emulators --create --name dev_emulator
```

#### Desktop Setup
```bash
# Включение desktop поддержки
flutter config --enable-desktop

# Для macOS
flutter config --enable-macos-desktop

# Для Windows
flutter config --enable-windows-desktop

# Для Linux
flutter config --enable-linux-desktop

# Проверка desktop platforms
flutter devices
```

### 4.3 Запуск Flutter приложения

#### Web версия (рекомендуется для разработки)
```bash
# Запуск Flutter web приложения
flutter run -d chrome

# Запуск на specific порту
flutter run -d chrome --web-port=8080

# Hot reload включен по умолчанию
# Для остановки: Ctrl+C
```

#### Desktop версия
```bash
# macOS
flutter run -d macos

# Windows
flutter run -d windows

# Linux
flutter run -d linux
```

#### Mobile (эмулятор)
```bash
# Запуск на Android эмуляторе
flutter run -d emulator-5554

# Запуск на iOS симуляторе (только macOS)
flutter run -d ios
```

### 4.4 Flutter специфические команды

```bash
# Сборка приложения
flutter build apk --debug
flutter build apk --release
flutter build web
flutter build macos
flutter build windows

# Анализ и оптимизация
flutter build apk --analyze-size
flutter build web --analyze-size

# Установка на устройство
flutter install android
flutter install ios

# Обновление зависимостей
flutter pub upgrade

# Создание нового проекта для тестирования
flutter create test_project
```

### 4.5 Настройка VS Code для Flutter

1. **Установить расширения:**
   - Flutter
   - Dart
   - Flutter Tree
   - Awesome Flutter Snippets

2. **Настройка launch.json:**
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Flutter: Launch",
            "type": "dart",
            "request": "launch",
            "program": "lib/main.dart",
            "deviceId": "chrome"
        },
        {
            "name": "Flutter: Attach",
            "type": "dart",
            "request": "attach"
        }
    ]
}
```

---

## 5. LLM интеграция для разработки

### 5.1 Настройка LLM провайдеров для разработки

```yaml
# Создать src/main/resources/config/llm-providers-dev.yml
activeProvider: ollama
providers:
  - id: ollama
    displayName: Local Ollama (Development)
    mode: local
    baseUrl: http://localhost:11434
    model: "qwen3-coder:480b-cloud"
    enabled: true
    timeout: 120
  - id: openrouter
    displayName: OpenRouter (Development)
    mode: remote
    baseUrl: https://openrouter.ai/api/v1
    apiKey: "${OPENROUTER_API_KEY:}"
    model: tngtech/deepseek-r1t-chimera:free
    enabled: true
    timeout: 60
```

### 5.2 Настройка переменных окружения

```bash
# Создать .env файл в корне проекта
cat > .env << 'EOF'
# LLM Configuration
OPENROUTER_API_KEY=your_development_api_key_here
OLLAMA_HOST=http://localhost:11434

# Database Configuration (для development)
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:h2:mem:testdb
DATABASE_USERNAME=sa
DATABASE_PASSWORD=

# Application Configuration
SERVER_PORT=8090
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
LOG_LEVEL=DEBUG

# Frontend Configuration
FLUTTER_WEB_RENDERER=html
FLUTTER_FRONTEND_URL=http://localhost:8080
EOF

# Настройка для IntelliJ IDEA
# Run → Edit Configurations → Environment variables:
# Load from .env file
```

### 5.3 Тестирование LLM интеграции

```java
// Создать тестовый класс для проверки LLM
package org.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:config/llm-providers-dev.yml")
public class LLMIntegrationTest {
    
    @Autowired
    private LLMService llmService;
    
    @Test
    public void testOllamaConnection() {
        String response = llmService.generate("Hello, test the security analysis model");
        assertThat(response).isNotNull();
        assertThat(response.length()).isGreaterThan(0);
    }
    
    @Test
    public void testMultipleLLMProviders() {
        // Тест локального Ollama
        String localResponse = llmService.generate("Test local model");
        assertThat(localResponse).isNotNull();
        
        // Если настроен OpenRouter, тест облачной модели
        // String cloudResponse = llmService.generate("Test cloud model");
        // assertThat(cloudResponse).isNotNull();
    }
}
```

---

## 6. Тестирование и разработка

### 6.1 Структура тестов в проекте

```
Backend/
├── src/test/java/
│   ├── org/example/
│   │   ├── unit/              # Юнит тесты
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── repository/
│   │   ├── integration/       # Интеграционные тесты
│   │   ├── performance/       # Производственные тесты
│   │   └── SecurityOrchestratorApplicationTests.java
│   └── resources/
│       ├── application-test.properties
│       └── config/
│           └── llm-providers-test.yml
```

### 6.2 Запуск тестов

```bash
# Все тесты
./gradlew test

# Только юнит тесты
./gradlew test --tests "*Unit*"

# Только интеграционные тесты
./gradlew test --tests "*Integration*"

# С отчетом о покрытии
./gradlew jacocoTestReport

# С определенными тэгами
./gradlew test -Dtest.tags="slow"

# С определенным профилем
./gradlew test --args='--spring.profiles.active=test'
```

### 6.3 Написание тестов

#### Юнит тест для сервиса
```java
@ExtendWith(MockitoExtension.class)
class SecurityAnalysisServiceTest {
    
    @Mock
    private LLMService llmService;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @InjectMocks
    private SecurityAnalysisService securityAnalysisService;
    
    @Test
    void testAnalyzeProject() {
        // Given
        String projectId = "test-project-id";
        OpenApiSpec spec = OpenApiSpec.builder()
            .paths(Map.of("/api/users", createUserPath()))
            .build();
        
        when(llmService.generate(any())).thenReturn("Security analysis completed");
        
        // When
        SecurityAnalysisResult result = securityAnalysisService.analyzeProject(projectId, spec);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFindings()).isNotEmpty();
        verify(llmService).generate(any());
    }
}
```

#### Интеграционный тест
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityOrchestratorIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Test
    void testFullAnalysisWorkflow() {
        // Создание проекта
        Project project = Project.builder()
            .name("Test Project")
            .description("Integration Test Project")
            .build();
        project = projectRepository.save(project);
        
        // Создание анализа
        AnalysisRequest request = AnalysisRequest.builder()
            .projectId(project.getId())
            .specificationType(SpecificationType.OPENAPI)
            .fileContent("openapi: 3.0.0\npaths:\n  /users:\n    get:\n      responses:\n        200:\n          description: OK")
            .build();
        
        // Отправка запроса
        ResponseEntity<ApiResponse<AnalysisResult>> response = restTemplate.postForEntity(
            "/api/v1/analysis/analyze", 
            request, 
            new ParameterizedTypeReference<ApiResponse<AnalysisResult>>() {}
        );
        
        // Проверка результата
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isNotNull();
    }
}
```

---

## 7. Отладка и разработка

### 7.1 Настройка отладки в IntelliJ IDEA

#### Backend отладка
1. **Создать Run Configuration:**
   - Run → Edit Configurations → + → Spring Boot
   - Name: `SecurityOrchestrator (Debug)`
   - Main class: `org.example.SecurityOrchestratorApplication`
   - VM options: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`
   - Active profiles: `dev`

2. **Запуск в debug режиме:**
   - Run → Debug 'SecurityOrchestrator (Debug)'

#### Frontend отладка в VS Code
```javascript
// launch.json для Flutter отладки
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Flutter: Launch Chrome",
            "type": "dart",
            "request": "launch",
            "program": "lib/main.dart",
            "deviceId": "chrome",
            "debugExternalPackageLibraries": true,
            "debugSdkLibraries": true
        }
    ]
}
```

### 7.2 Мониторинг в development

```bash
# Мониторинг логов Spring Boot приложения
tail -f /var/log/security-orchestrator/application.log

# Мониторинг через Actuator endpoints
curl http://localhost:8090/actuator/metrics | jq
curl http://localhost:8090/actuator/health | jq

# Мониторинг LLM сервиса
curl http://localhost:11434/api/tags | jq

# Мониторинг ресурсов системы
htop
df -h
free -h
```

### 7.3 Профилирование и производительность

```bash
# Профилирование с JProfiler (для IntelliJ IDEA)
# Скачать JProfiler с https://www.ej-technologies.com/

# Профилирование с VisualVM
sudo apt install visualvm

# Профилирование CPU и Memory
jstat -gc pid
jstack pid > thread-dump.txt
jmap -dump:format=b,file=heap-dump.hprof pid

# Flutter performance monitoring
flutter run --profile
flutter run --trace-startup
```

---

## 8. Работа с базой данных

### 8.1 H2 Database Console (по умолчанию)

1. **Открыть браузер** и перейти на `http://localhost:8090/h2-console`
2. **JDBC URL**: `jdbc:h2:mem:testdb`
3. **User Name**: `sa`
4. **Password**: (оставить пустым)

### 8.2 Data Source Configuration

```properties
# src/main/resources/application-dev.properties
# H2 Database (по умолчанию)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driverClassName=org.h2.Driver

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
```

### 8.3 Миграция с Flyway

```bash
# Создание миграционного скрипта
cd Backend
./gradlew flywayCreate -Dflyway.migrationFilesLocation=db/migration

# Выполнение миграций
./gradlew flywayMigrate

# Информация о миграциях
./gradlew flywayInfo

# Валидация миграций
./gradlew flywayValidate
```

### 8.4 Database Reset для тестирования

```bash
# Создать скрипт для очистки базы данных
cat > reset-db.sh << 'EOF'
#!/bin/bash

echo "Остановка приложения..."
pkill -f "SecurityOrchestratorApplication"

echo "Удаление H2 файлов..."
rm -f *.mv.db
rm -f *.trace.db

echo "Очистка Gradle cache (опционально)..."
# ./gradlew clean

echo "Запуск приложения с очищенной базой..."
./gradlew bootRun --args='--spring.jpa.hibernate.ddl-auto=create-drop'

EOF

chmod +x reset-db.sh
```

---

## 9. Инструменты разработки

### 9.1 Полезные Gradle плагины

```kotlin
// build.gradle.kts (общие плагины для разработки)
plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.sonarqube") version "4.4.1.3373"
    id("org.springframework.boot.renderless") version "3.3.5"
    id("com.google.cloud.tools.jib") version "3.4.2"
    id("org.springframework.experimental.aot") version "0.13.5"
}

// Плагины для разработки
plugins {
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.jacoco") version "0.8.11"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

// Конфигурация Spotless для форматирования кода
spotless {
    java {
        googleJavaFormat("1.17.0").aosp()
        importOrder()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
```

### 9.2 Настройка Git hooks

```bash
# Установка pre-commit hooks
cd Backend
mkdir -p .git/hooks

# Создание pre-commit hook
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/sh

echo "Running pre-commit checks..."

# Форматирование кода
./gradlew spotlessApply

# Запуск тестов
./gradlew test --no-daemon

# Проверка качества кода
./gradlew check --no-daemon

if [ $? -ne 0 ]; then
    echo "Pre-commit checks failed!"
    exit 1
fi

echo "Pre-commit checks passed!"
exit 0
EOF

chmod +x .git/hooks/pre-commit

# Commitizen дляConventional Commits
npm install -g commitizen
npm install -g cz-conventional-changelog

# Настройка commitizen
echo '{ "path": "cz-conventional-changelog" }' > .czrc
```

### 9.3 Docker для разработки

```dockerfile
# Dockerfile.dev для development
FROM openjdk:21-jdk-slim

# Установка дополнительных инструментов
RUN apt-get update && apt-get install -y \
    curl \
    git \
    vim \
    htop \
    && rm -rf /var/lib/apt/lists/*

# Создание пользователя development
RUN useradd -m -s /bin/bash developer
USER developer
WORKDIR /home/developer/app

# Копирование исходного кода
COPY --chown=developer:developer . .

# Сборка приложения
RUN ./gradlew bootJar

EXPOSE 8090

# Запуск приложения
CMD ["java", "-jar", "build/libs/*.jar"]
```

```yaml
# docker-compose.dev.yml
version: '3.8'

services:
  app:
    build:
      context: ./Backend
      dockerfile: Dockerfile.dev
    ports:
      - "8090:8090"
      - "5005:5005"  # Debug port
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
    volumes:
      - ./Backend:/home/developer/app
      - ~/.m2:/home/developer/.m2
    depends_on:
      - postgres-dev
    networks:
      - dev-network

  postgres-dev:
    image: postgres:15
    environment:
      - POSTGRES_DB=security_orchestrator_dev
      - POSTGRES_USER=dev_user
      - POSTGRES_PASSWORD=dev_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_dev_data:/var/lib/postgresql/data
    networks:
      - dev-network

  redis-dev:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - dev-network

  ollama-dev:
    image: ollama/ollama:latest
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    networks:
      - dev-network

volumes:
  postgres_dev_data:
  ollama_data:

networks:
  dev-network:
    driver: bridge
```

---

## 10. Мониторинг и логирование в разработке

### 10.1 Настройка логирования для разработки

```properties
# application-dev.properties
# Логирование
logging.level.root=DEBUG
logging.level.org.example.application=DEBUG
logging.level.org.springframework=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Консольное логирование
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Файловое логирование (опционально)
logging.file.name=logs/security-orchestrator-dev.log
logging.file.max-size=10MB
logging.file.max-history=30

# Spring Boot DevTools
spring.devtools.restart.enabled=true
spring.devtools.restart.exclude=static/**,public/**
spring.devtools.livereload.enabled=true
```

### 10.2 Мониторинг через Actuator

```properties
# Настройка Actuator endpoints для разработки
management.endpoints.web.exposure.include=health,info,metrics,prometheus,flyway,loggers,env,configprops,beans,mappings,scheduledtasks,httptrace
management.endpoint.health.show-details=always
management.endpoint.health.show-components=always
management.info.env.enabled=true
management.metrics.export.prometheus.enabled=true

# Информация о приложении
info.app.name=SecurityOrchestrator
info.app.description=Security Analysis and Testing Platform
info.app.version=@project.version@
info.app.java.version=@java.version@
```

### 10.3 REST API тестирование

```bash
# Создание набора curl команд для тестирования
cat > api-test.sh << 'EOF'
#!/bin/bash

BASE_URL="http://localhost:8090"

echo "=== SecurityOrchestrator API Testing ==="

# Health check
echo "1. Health Check:"
curl -s $BASE_URL/actuator/health | jq

# Getting projects
echo "2. Getting Projects:"
curl -s $BASE_URL/api/v1/projects | jq

# Creating a new project
echo "3. Creating Project:"
PROJECT_RESPONSE=$(curl -s -X POST $BASE_URL/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Project",
    "description": "API Test Project"
  }')
echo $PROJECT_RESPONSE | jq

# Getting analysis results
echo "4. Getting Analysis Results:"
curl -s $BASE_URL/api/v1/analysis/results | jq

# LLM Testing
echo "5. LLM Test:"
curl -s -X POST $BASE_URL/api/v1/llm/generate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Test security analysis prompt",
    "provider": "ollama"
  }' | jq

echo "=== API Testing Complete ==="
EOF

chmod +x api-test.sh
```

---

## 11. Общие команды и утилиты

### 11.1 Полезные команды

```bash
# Backend
./gradlew bootRun --args='--debug'           # Запуск в debug режиме
./gradlew bootRun --args='--profile=dev'     # Запуск с профилем
./gradlew clean build -x test                # Сборка без тестов
./gradlew dependencies --configuration compileClasspath  # Проверка зависимостей

# Frontend
flutter clean && flutter pub get             # Очистка и переустановка зависимостей
flutter doctor -v                            # Подробная диагностика Flutter
flutter run --trace-startup                  # Отслеживание времени запуска

# Docker
docker-compose -f docker-compose.dev.yml up --build  # Запуск development окружения
docker-compose -f docker-compose.dev.yml down        # Остановка development окружения

# Система
htop                                            # Мониторинг процессов
netstat -tlnp | grep :8090                    # Проверка порта приложения
journalctl -u security-orchestrator -f        # Мониторинг systemd сервиса
```

### 11.2 Troubleshooting

```bash
# Проблемы с Java
java -version                                 # Проверка версии Java
echo $JAVA_HOME                               # Проверка JAVA_HOME
which java                                    # Проверка пути к Java

# Проблемы с Gradle
./gradlew --version                           # Проверка версии Gradle
./gradlew --stop                              # Остановка Gradle daemon
./gradlew clean                               # Очистка build файлов

# Проблемы с Flutter
flutter clean                                 # Очистка Flutter build
flutter doctor                                # Диагностика Flutter
flutter pub get                               # Обновление зависимостей

# Проблемы с LLM
curl -f http://localhost:11434/api/tags       # Проверка Ollama
ollama logs                                   # Логи Ollama
```

---

## 12. Заключение

Данное руководство предоставляет полную информацию по настройке среды разработки для SecurityOrchestrator. 

### Ключевые моменты:

1. **Полная настройка**: От Java до Flutter, включая LLM интеграцию
2. **Разработка в containerized окружении**: Docker Compose для быстрого старта
3. **Testing framework**: Поддержка юнит, интеграционных и performance тестов
4. **Инструменты разработки**: Gradle плагины, форматирование кода, мониторинг
5. **Debugging**: Настройка отладки для Backend и Frontend
6. **Database**: Поддержка H2 для разработки и PostgreSQL для сложных сценариев

### Рекомендации:

- Используйте профиль `dev` для разработки
- Регулярно обновляйте зависимости: `./gradlew dependencyUpdates`
- Следите за качеством кода: `./gradlew sonarqube`
- Мониторьте производительность с помощью Actuator endpoints
- Используйте Docker для изоляции окружения

**Статус**: Готово к разработке  
**Дата**: 2025-11-21  
**Версия**: 1.0.0