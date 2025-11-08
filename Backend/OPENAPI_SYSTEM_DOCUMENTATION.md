# OpenAPI Парсер и Утилиты

Комплексная система для парсинга, валидации и обработки OpenAPI спецификаций с поддержкой JSON и YAML форматов.

## 📋 Оглавление

- [Обзор](#обзор)
- [Архитектура](#архитектура)
- [Компоненты системы](#компоненты-системы)
- [Установка](#установка)
- [Быстрый старт](#быстрый-старт)
- [API Reference](#api-reference)
- [Примеры использования](#примеры-использования)
- [Интеграция с LLM](#интеграция-с-llm)
- [Производительность](#производительность)
- [Лицензия](#лицензия)

## 🎯 Обзор

Данная система предоставляет полный набор инструментов для работы с OpenAPI спецификациями:

- **Парсинг** OpenAPI 3.0 и Swagger 2.0 спецификаций
- **Валидация** структуры и соответствия стандартам
- **Анализ** проблем и рекомендации по улучшению
- **Извлечение данных** для LLM анализа
- **Разбивка** больших спецификаций на части
- **Сохранение** в базе данных
- **Интеграция** с существующими системами

## 🏗️ Архитектура

```
┌─────────────────────────────────────────────────────┐
│                 OpenAPI System                      │
├─────────────────────────────────────────────────────┤
│  OpenApiOrchestrationService                        │  ← Главный сервис
├─────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐ │
│  │   Parser    │ │  Validator  │ │    Extractor    │ │  ← Основные сервисы
│  └─────────────┘ └─────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐ │
│  │   Chunker   │ │   Detector  │ │   Repositories  │ │  ← Вспомогательные
│  └─────────────┘ └─────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐ │
│  │    OpenAPI  │ │  Swagger    │ │  Domain Models  │ │  ← Модели данных
│  │ Specification│ │  Parser     │ │                │ │
│  └─────────────┘ └─────────────┘ └─────────────────┘ │
└─────────────────────────────────────────────────────┘
```

## 🔧 Компоненты системы

### 1. OpenApiParserService

Основной сервис для парсинга OpenAPI спецификаций.

**Возможности:**
- Парсинг OpenAPI 3.0 и Swagger 2.0
- Поддержка JSON и YAML форматов
- Извлечение эндпоинтов, схем, параметров
- Валидация структуры

**Ключевые методы:**
```java
// Парсинг из строки
OpenApiSpecification parseSpecification(String specContent, String format)

// Парсинг из файла
OpenApiSpecification parseFromFile(String filePath)

// Парсинг по URL
OpenApiSpecification parseFromUrl(String specUrl)
```

### 2. OpenApiValidator

Валидатор спецификаций для проверки соответствия стандартам.

**Проверки:**
- Структурная целостность
- Корректность путей и методов
- Валидность схем
- Согласованность типов данных
- Соответствие OpenAPI стандартам

**Использование:**
```java
ValidationResult result = validator.validateSpecification(specification);
if (result.isValid()) {
    // Спецификация валидна
} else {
    for (String error : result.getErrors()) {
        // Обработка ошибок
    }
}
```

### 3. OpenApiDataExtractor

Извлекатель данных для подготовки к LLM анализу.

**Возможности:**
- Подготовка структурированных данных
- Генерация кратких описаний
- Извлечение статистики
- Группировка по тегам

**API:**
```java
// Подготовка для LLM
OpenApiLLMData data = extractor.prepareForLLMAnalysis(specification);

// Генерация краткого описания
String summary = extractor.generateSummary(specification);
```

### 4. OpenApiChunker

Разделитель больших спецификаций на части.

**Стратегии разбивки:**
- По эндпоинтам
- По схемам
- По метаданным

**Параметры:**
- Настраиваемый размер чанков
- Сохранение контекста
- Приоритетная сортировка

### 5. OpenApiIssueDetector

Детектор проблем в спецификациях.

**Типы проблем:**
- Структурные ошибки
- Проблемы документации
- Вопросы безопасности
- Проблемы производительности
- Проблемы дизайна API

**Уровни серьезности:**
- ERROR - критические ошибки
- WARNING - предупреждения
- INFO - информационные сообщения

## 📦 Установка

### Зависимости

Добавьте в `build.gradle.kts`:

```kotlin
dependencies {
    // OpenAPI парсинг
    implementation("io.swagger.parser:swagger-parser:2.1.14")
    implementation("io.swagger.core:v3-parser:2.2.0")
    implementation("io.swagger.core:v3-parser-core:2.2.0")
    
    // JSON и YAML обработка
    implementation("org.yaml:snakeyaml:2.2")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    
    // Валидация JSON Schema
    implementation("com.github.java-json-tools:json-schema-validator:2.2.14")
}
```

### Spring Configuration

Сервисы автоматически конфигурируются как Spring компоненты:

```java
@Autowired
private OpenApiOrchestrationService orchestrationService;
```

## 🚀 Быстрый старт

### 1. Базовый анализ спецификации

```java
@Autowired
private OpenApiOrchestrationService service;

public void analyzeSpec() {
    String specContent = """
        openapi: 3.0.3
        info:
          title: My API
          version: 1.0.0
        paths:
          /users:
            get:
              summary: Get users
        """;
    
    OpenApiAnalysisResult result = service.analyzeSpecification(specContent, "yaml");
    
    if (result.getStatus() == AnalysisStatus.COMPLETED) {
        System.out.println("Анализ завершен!");
        System.out.println("Эндпоинтов: " + result.getSpecification().getEndpoints().size());
    }
}
```

### 2. Загрузка из файла

```java
@PostMapping("/upload")
public ResponseEntity analyzeFile(@RequestParam("file") MultipartFile file) {
    OpenApiAnalysisResult result = service.analyzeSpecificationFromFile(file);
    return ResponseEntity.ok(result);
}
```

### 3. Анализ по URL

```java
@GetMapping("/analyze")
public ResponseEntity analyzeUrl(@RequestParam String url) {
    OpenApiAnalysisResult result = service.analyzeSpecificationFromUrl(url);
    return ResponseEntity.ok(result);
}
```

## 📖 API Reference

### OpenApiOrchestrationService

Основной оркестрационный сервис.

#### Методы:

**`analyzeSpecification(String specContent, String format)`**
- Полный анализ спецификации
- Возвращает: OpenApiAnalysisResult

**`analyzeSpecificationFromFile(MultipartFile file)`**
- Анализ загруженного файла
- Поддерживает JSON и YAML

**`analyzeSpecificationFromUrl(String specUrl)`**
- Анализ спецификации по URL
- Автоматическое определение формата

**`saveSpecification(OpenApiSpecification specification)`**
- Сохранение в базу данных
- Возвращает: ID спецификации

**`getSpecification(String specId)`**
- Получение спецификации из БД
- Возвращает: Optional<OpenApiSpecification>

### OpenApiParserService

Сервис парсинга.

#### Методы:

**`parseSpecification(String content, String format)`**
- Парсинг из строки
- Форматы: "json", "yaml", "auto"

**`parseFromFile(String filePath)`**
- Парсинг из файла
- Автоматическое определение формата

**`parseFromUrl(String specUrl)`**
- Загрузка и парсинг по URL
- Поддержка HTTP/HTTPS

### OpenApiDataExtractor

Извлекатель данных.

#### Методы:

**`prepareForLLMAnalysis(OpenApiSpecification spec)`**
- Подготовка для LLM анализа
- Возвращает: OpenApiLLMData

**`generateSummary(OpenApiSpecification spec)`**
- Генерация краткого описания
- Возвращает: String

**`getEndpointsForAnalysis(OpenApiSpecification spec)`**
- Извлечение эндпоинтов для анализа
- Возвращает: List<ApiEndpointSummary>

## 💡 Примеры использования

### Пример 1: Валидация спецификации

```java
public void validateSpec() {
    String spec = "...";
    
    try {
        OpenApiSpecification specification = parserService.parseSpecification(spec, "yaml");
        ValidationResult result = validator.validateSpecification(specification);
        
        if (!result.isValid()) {
            System.out.println("Ошибки валидации:");
            result.getErrors().forEach(System.out::println);
        }
    } catch (Exception e) {
        System.out.println("Ошибка парсинга: " + e.getMessage());
    }
}
```

### Пример 2: Анализ проблем

```java
public void analyzeIssues() {
    OpenApiSpecification spec = parserService.parseSpecification(content, "yaml");
    OpenApiIssueReport report = issueDetector.detectIssues(spec);
    
    System.out.println("Всего проблем: " + report.getTotalIssues());
    System.out.println("Ошибок: " + report.getErrorCount());
    System.out.println("Предупреждений: " + report.getWarningCount());
    
    // Детализация по типам проблем
    report.getIssuesByType(IssueType.SECURITY).forEach(issue -> {
        System.out.println("Безопасность: " + issue.getDescription());
    });
}
```

### Пример 3: Подготовка для LLM

```java
public void prepareForLLM() {
    OpenApiSpecification spec = parserService.parseSpecification(content, "yaml");
    OpenApiLLMData data = dataExtractor.prepareForLLMAnalysis(spec);
    
    // Статистика
    System.out.println("Статистика API:");
    System.out.println("- Эндпоинтов: " + data.getStatistics().getTotalEndpoints());
    System.out.println("- Схем: " + data.getStatistics().getTotalSchemas());
    System.out.println("- Безопасность: " + data.getSecuritySchemes().size());
    
    // Краткое описание
    String summary = dataExtractor.generateSummary(spec);
    System.out.println("\nКраткое описание:\n" + summary);
}
```

### Пример 4: Разбивка большой спецификации

```java
public void chunkLargeSpec() {
    OpenApiSpecification spec = parserService.parseSpecification(largeContent, "yaml");
    List<OpenApiChunk> chunks = chunker.chunkSpecification(spec, 10000);
    
    System.out.println("Разбито на " + chunks.size() + " частей:");
    for (OpenApiChunk chunk : chunks) {
        System.out.println("- " + chunk.getTitle() + 
            " (эндпоинтов: " + chunk.getEndpointCount() + ")");
    }
}
```

## 🤖 Интеграция с LLM

Система предоставляет специальные возможности для интеграции с LLM:

### 1. Подготовка данных

```java
// Структурированные данные для LLM
OpenApiLLMData data = extractor.prepareForLLMAnalysis(specification);

// Краткие описания
String summary = extractor.generateSummary(specification);

// Группировка по тегам
Map<String, List<ApiEndpointSummary>> groupedEndpoints = 
    extractor.getEndpointsByTag(specification);
```

### 2. Разбивка для обработки

```java
// Разбивка на части оптимального размера
List<OpenApiChunk> chunks = chunker.chunkSpecification(specification, maxSize);

// Каждый чанк содержит
// - Заголовок и метаданные
// - Содержимое оптимизированное для LLM
// - Информацию о контексте
```

### 3. Анализ проблем LLM

```java
// Автоматическое выявление проблем
OpenApiIssueReport report = issueDetector.detectIssues(specification);

// Группировка по типам для LLM анализа
Map<IssueType, List<OpenApiIssue>> issuesByType = 
    report.getIssues().stream()
        .collect(Collectors.groupingBy(OpenApiIssue::getType));
```

## ⚡ Производительность

### Оптимизации

- **Кэширование** результатов парсинга
- **Ленивая загрузка** больших спецификаций
- **Параллельная обработка** эндпоинтов
- **Оптимизация памяти** при работе с большими схемами

### Рекомендации

- Для спецификаций > 1MB используйте разбивку
- Настройте размер чанков в зависимости от LLM лимитов
- Используйте кэширование для повторяющихся анализов
- Мониторьте использование памяти при обработке больших схем

## 🔄 Интеграция с существующими компонентами

### Репозитории

```java
// Интеграция с OpenApiSpecRepository
@Autowired
private OpenApiSpecRepository specificationRepository;

// Сохранение результатов анализа
String specId = service.saveSpecification(specification);
Optional<OpenApiSpecification> retrieved = service.getSpecification(specId);
```

### Контроллеры

```java
@RestController
@RequestMapping("/api/openapi")
public class OpenApiController {
    
    @Autowired
    private OpenApiOrchestrationService service;
    
    @PostMapping("/analyze")
    public OpenApiAnalysisResult analyze(@RequestBody String specContent) {
        return service.analyzeSpecification(specContent, "auto");
    }
    
    @PostMapping("/upload")
    public OpenApiAnalysisResult upload(@RequestParam MultipartFile file) {
        return service.analyzeSpecificationFromFile(file);
    }
}
```

## 📊 Мониторинг и логирование

Система предоставляет детальное логирование операций:

```java
// Включение подробного логирования
logging.level.org.example.infrastructure.services.openapi=DEBUG

// Метрики производительности
- Время парсинга
- Количество обработанных эндпоинтов
- Размер спецификации
- Количество выявленных проблем
```

## 🐛 Отладка

### Частые проблемы

1. **Ошибки парсинга YAML**
   - Проверьте правильность отступов
   - Убедитесь в корректности синтаксиса

2. **Проблемы с большими файлами**
   - Используйте разбивку на чанки
   - Увеличьте память JVM

3. **Ошибки валидации**
   - Проверьте соответствие OpenAPI стандартам
   - Используйте issue detector для детального анализа

### Логирование

```java
// Включение debug логирования для OpenAPI компонентов
logging.level.org.example.infrastructure.services.openapi.OpenApiParserService=DEBUG
logging.level.org.example.infrastructure.services.openapi.OpenApiValidator=DEBUG
logging.level.org.example.infrastructure.services.openapi.OpenApiIssueDetector=DEBUG
```

## 📝 Лицензия

Система разработана в рамках проекта Security Orchestrator и распространяется под лицензией проекта.

## 🤝 Поддержка

Для получения поддержки и сообщения об ошибках используйте систему трекинга проекта.

---

**Версия документации:** 1.0.0  
**Дата обновления:** 2025-11-07  
**Автор:** Roo AI Assistant