# OpenAPI/Swagger Парсинг и Анализ Модуль

## Обзор

Модуль для парсинга и анализа OpenAPI/Swagger спецификаций, разработанный для системы автоматического тестирования SecurityOrchestrator. Модуль предоставляет комплексные возможности для загрузки, парсинга и анализа API спецификаций с выявлением потенциальных проблем.

## Архитектура

### Доменные Модели

#### 1. OpenApiService
Основная сущность для хранения информации о сервисе с OpenAPI спецификацией:
- Метаданные сервиса (название, версия, описание)
- URL спецификации и версия OpenAPI
- Статус парсинга и эндпоинты
- Связи с анализами и схемами

#### 2. ApiSchema
Сущность для хранения схем данных API:
- Информация о типе, формате, описании
- Обязательные поля и свойства
- Примеры и ограничения
- Связь с родительским сервисом

#### 3. EndpointAnalysis
Сущность для хранения результатов анализа эндпоинтов:
- Тип анализа (быстрый, полный, безопасность, валидация)
- Статус выполнения и время анализа
- Количество найденных проблем
- Рекомендации и отчеты

#### 4. ApiEndpoint
Существующая сущность для описания эндпоинтов API:
- HTTP метод, путь, описание
- Параметры и теги
- Схемы запросов и ответов

### Репозитории

#### 1. OpenApiServiceRepository
- Поиск по имени, URL, статусу
- Фильтрация по версии OpenAPI и тегам
- Статистика и пагинация

#### 2. ApiSchemaRepository
- Поиск по сервису, типу, формату
- Фильтрация по обязательным полям
- Сложность схем и статистика

#### 3. EndpointAnalysisRepository
- Поиск по типу, статусу, проблемам
- Анализы по сервису и датам
- Статистика анализов

### Сервисы

#### 1. OpenApiParsingServiceFixed
Основной сервис для парсинга OpenAPI спецификаций:
```java
// Парсинг по URL
CompletableFuture<OpenApiParsingResult> result = parsingService.parseFromUrl("https://api.example.com/openapi.json");

// Парсинг из файла
OpenApiParsingResult result = parsingService.parseFromFile(fileContent, "openapi.yaml");

// Парсинг из строки
OpenApiParsingResult result = parsingService.parseFromContent(specContent, "inline");
```

**Возможности:**
- Загрузка спецификаций по URL
- Парсинг JSON и YAML форматов
- Валидация структуры OpenAPI
- Извлечение эндпоинтов, схем, метаданных
- Сохранение в базу данных
- Обработка ошибок и timeout'ов

#### 2. EndpointAnalysisService
Сервис для анализа эндпоинтов API:
```java
// Запуск анализа
CompletableFuture<EndpointAnalysis> analysis = analysisService.startAnalysis(serviceId, AnalysisType.FULL_ANALYSIS);

// Выполнение детального анализа
CompletableFuture<EndpointAnalysis> result = analysisService.performAnalysis(
    serviceId, 
    AnalysisType.SECURITY_AUDIT, 
    List.of("security", "validation")
);
```

**Типы анализа:**
- QUICK_SCAN - быстрое сканирование
- FULL_ANALYSIS - полный анализ
- SECURITY_AUDIT - аудит безопасности
- VALIDATION_CHECK - проверка валидации
- CONSISTENCY_AUDIT - аудит консистентности
- PERFORMANCE_ANALYSIS - анализ производительности

### REST API

#### Эндпоинты

**1. Парсинг спецификации**
```
POST /api/test/openapi/parse?url={specificationUrl}
POST /api/test/openapi/parse/file
```

**2. Управление сервисами**
```
GET /api/test/openapi/services
GET /api/test/openapi/services/{serviceId}
GET /api/test/openapi/services/{serviceId}/endpoints
GET /api/test/openapi/services/{serviceId}/schemas
```

**3. Анализ эндпоинтов**
```
POST /api/test/openapi/services/{serviceId}/analyze?analysisType={type}
GET /api/test/openapi/analysis/{analysisId}
GET /api/test/openapi/services/{serviceId}/analysis
```

#### Пример использования

```java
// 1. Загрузка и парсинг спецификации
var parseResult = parsingService.parseFromUrl("https://abank.open.bankingapi.ru/docs")
    .get(30, TimeUnit.SECONDS);

if (parseResult.isSuccess()) {
    UUID serviceId = parseResult.getService().getId();
    
    // 2. Запуск анализа безопасности
    var analysisResult = analysisService.performAnalysis(
        serviceId, 
        EndpointAnalysis.AnalysisType.SECURITY_AUDIT,
        List.of("security", "validation")
    ).get(5, TimeUnit.MINUTES);
    
    // 3. Получение результатов
    var results = analysisResult.get();
    System.out.println("Найдено проблем безопасности: " + results.getSecurityIssues());
    System.out.println("Оценка качества: " + results.getQualityScore() + "%");
    
    // 4. Рекомендации
    results.getRecommendations().forEach(System.out::println);
}
```

## Конфигурация

### application.properties
```properties
# OpenAPI Parsing Service
openapi.parsing.timeout.seconds=30
openapi.parsing.max.file.size.mb=50
openapi.parsing.retry.attempts=3

# OpenAPI Analysis Service
openapi.analysis.max.concurrent.analyses=5
openapi.analysis.analysis.timeout.minutes=10
openapi.analysis.enable.security.checks=true
openapi.analysis.enable.validation.checks=true
```

## Функции анализа

### 1. Базовый анализ
- Проверка наличия описаний эндпоинтов
- Валидация operationId
- Анализ структуры параметров

### 2. Анализ безопасности
- Отсутствие схем аутентификации
- Чувствительные данные в URL
- Небезопасные методы без защиты

### 3. Анализ валидации
- Отсутствие описаний параметров
- Неполные схемы запросов
- Неправильная типизация данных

### 4. Анализ консистентности
- Дублирующиеся operationId
- Неиспользуемые теги
- Неконсистентное именование

### 5. Анализ производительности
- Слишком длинные пути
- Избыточное количество параметров
- Чрезмерно длинные описания

## Возможности расширения

1. **Интеграция с LLM** - для семантического анализа
2. **Кастомные правила валидации** - для специфичных требований
3. **Интеграция с тестированием** - автоматическая генерация тестов
4. **Визуализация результатов** - графики и диаграммы
5. **Экспорт отчетов** - в различных форматах

## Технические особенности

- **Java 21** + Spring Boot 3.x
- **Асинхронная обработка** - CompletableFuture
- **Валидация данных** - Jakarta Validation
- **JPA/Hibernate** - для работы с БД
- **OpenAPI 3.x** - поддержка последней версии
- **JSON/YAML** - поддержка обоих форматов
- **Обработка ошибок** - comprehensive error handling
- **Логирование** - подробные логи для отладки

## Готовность к интеграции

Модуль полностью интегрирован с существующей архитектурой SecurityOrchestrator и готов к:
- Соединению с LLM системой
- Интеграции с BPMN анализом
- Подключению к системам тестирования
- Расширению функциональности

Модуль обеспечивает прочную основу для следующего этапа разработки - BPMN парсинг и интегрированный анализ API-BPMN.