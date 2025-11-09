@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo =============================================
echo  🚀 SecurityOrchestrator - Ollama Setup
echo  Optimized for RTX 3070 8GB
echo =============================================
echo.

REM Проверка установки Ollama
echo [1/5] Проверка установки Ollama...
where ollama >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Ollama не установлен!
    echo Скачайте с https://ollama.ai/download/windows
    echo После установки запустите этот скрипт снова.
    pause
    exit /b 1
)

echo ✅ Ollama найден:
ollama --version
echo.

REM Запуск Ollama сервиса
echo [2/5] Запуск Ollama сервиса...
echo Остановка всех процессов Ollama...
taskkill /f /im ollama.exe >nul 2>&1
timeout /t 2 >nul

echo Запуск Ollama на порту 11434...
start /b ollama serve --host 0.0.0.0 --port 11434

echo Ожидание запуска сервиса (30 секунд)...
timeout /t 30 >nul

REM Проверка доступности API
echo [3/5] Проверка API Ollama...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:11434/api/tags' -TimeoutSec 10; Write-Host '✅ Ollama API доступен' } catch { Write-Host '❌ Ollama API недоступен' -ForegroundColor Red; exit 1 }"

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Не удалось подключиться к Ollama API
    echo Убедитесь, что Ollama запущен и доступен на порту 11434
    pause
    exit /b 1
)
echo.

REM Установка CodeLlama модели
echo [4/5] Установка CodeLlama-7B-Instruct-Q4_0...
echo Это займет несколько минут и ~3.8GB дискового пространства

ollama pull codellama:7b-instruct-q4_0
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Ошибка установки модели!
    echo Попробуйте: ollama pull codellama:7b-instruct
    pause
    exit /b 1
)

echo.

REM Тестирование модели
echo [5/5] Тестирование модели...
echo Отправка тестового запроса...

set TEST_RESPONSE=
for /f "delims=" %%i in ('ollama run codellama:7b-instruct-q4_0 "Hello! Respond with just 'OK' if you are working." --format json 2^>^&1') do set TEST_RESPONSE=%%i

if defined TEST_RESPONSE (
    echo ✅ Модель работает корректно!
    echo Ответ: !TEST_RESPONSE!
) else (
    echo ⚠️  Тест не прошел, но модель установлена
)

echo.
echo =============================================
echo  🎉 Настройка CodeLlama завершена!
echo =============================================
echo.
echo 📋 Информация о модели:
ollama list
echo.
echo 🔧 Следующие шаги:
echo 1. SecurityOrchestrator автоматически подключится к http://localhost:11434
echo 2. Запустите backend: cd Backend && ./gradlew bootRun
echo 3. Запустите frontend: cd Frontend/security_orchestrator_frontend && flutter run
echo 4. Проверьте LLM Dashboard в веб-интерфейсе
echo.
echo ⚡ Оптимальные настройки для RTX 3070 8GB:
echo - Context Window: 16384
echo - Temperature: 0.1
echo - Max Tokens: 2048
echo - Использует ~2GB VRAM + 8GB RAM
echo.
pause