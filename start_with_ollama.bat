@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo =============================================
echo  🚀 SecurityOrchestrator - Full Startup
echo  With CodeLlama 7B Local LLM
echo  Optimized for RTX 3070 8GB
echo =============================================
echo.

REM Проверка установки Ollama
echo [1/6] Проверка Ollama...
where ollama >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Ollama не установлен!
    echo Скачайте с https://ollama.ai/download/windows
    echo Затем выполните: install_ollama.bat
    pause
    exit /b 1
)

echo ✅ Ollama найден:
ollama --version
echo.

REM Запуск Ollama сервиса
echo [2/6] Запуск Ollama сервиса...
echo Остановка старых процессов...
taskkill /f /im ollama.exe >nul 2>&1
timeout /t 3 >nul

echo Запуск Ollama на порту 11434...
start /b ollama serve --host 0.0.0.0 --port 11434
echo Ожидание запуска (20 секунд)...
timeout /t 20 >nul

REM Проверка доступности API
echo [3/6] Проверка Ollama API...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:11434/api/tags' -TimeoutSec 10; Write-Host '✅ Ollama API готов' } catch { Write-Host '❌ Ollama API недоступен' -ForegroundColor Red; exit 1 }"

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Остановка - Ollama не доступен
    pause
    exit /b 1
)

REM Проверка наличия CodeLlama модели
echo [4/6] Проверка CodeLlama модели...
for /f "delims=" %%i in ('curl -s http://localhost:11434/api/tags 2^>^&1 ^| findstr /i "codellama"') do set CODE_LLAMA=%%i

if not defined CODE_LLAMA (
    echo ⚠️  CodeLlama модель не найдена, установка...
    echo Это займет несколько минут...
    ollama pull codellama:7b-instruct-q4_0
    if !ERRORLEVEL! NEQ 0 (
        echo ❌ Ошибка установки модели
        echo Попробуйте: ollama pull codellama:7b-instruct
        pause
        exit /b 1
    )
    echo ✅ CodeLlama модель установлена
) else (
    echo ✅ CodeLlama модель найдена
)

echo.

REM Тестирование LLM
echo [5/6] Тестирование CodeLlama...
echo Быстрый тест генерации...

set TEST_RESPONSE=
for /f "delims=" %%i in ('curl -s -X POST http://localhost:11434/api/generate -H "Content-Type: application/json" -d "{\"model\":\"codellama:7b-instruct-q4_0\",\"prompt\":\"Hello! Respond with OK.\",\"stream\":false}" 2^>^&1') do set TEST_RESPONSE=%%i

if defined TEST_RESPONSE (
    echo ✅ CodeLlama работает корректно
) else (
    echo ⚠️  Тест не прошел, но модель установлена
)

echo.

REM Проверка и запуск SecurityOrchestrator
echo [6/6] Проверка SecurityOrchestrator...

REM Проверка backend
if exist "Backend\gradlew.bat" (
    echo Запуск SecurityOrchestrator Backend...
    cd Backend
    
    REM Проверка, запущен ли уже backend
    powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 3; Write-Host '✅ Backend уже запущен' } catch { Write-Host '🚀 Запуск backend...' }"
    
    if !ERRORLEVEL! NEQ 0 (
        start /b gradlew.bat bootRun
        echo Ожидание запуска backend (30 секунд)...
        timeout /t 30 >nul
    )
    cd ..
) else (
    echo ❌ Backend не найден
)

REM Проверка frontend
if exist "Frontend\security_orchestrator_frontend\pubspec.yaml" (
    echo Проверка Frontend...
    powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:3000' -TimeoutSec 3; Write-Host '✅ Frontend уже запущен' } catch { Write-Host '💻 Frontend не запущен - запустите вручную' }"
) else (
    echo ❌ Frontend не найден
)

echo.
echo =============================================
echo  🎉 Система запущена!
echo =============================================
echo.
echo 📍 Доступные сервисы:
echo   - Ollama API:     http://localhost:11434
echo   - Backend API:    http://localhost:8080
echo   - Frontend:       http://localhost:3000
echo   - LLM Dashboard:  http://localhost:3000 (вкладка LLM)
echo.
echo 📊 Статус компонентов:
ollama list
echo.
echo 🔧 Следующие шаги:
echo 1. Откройте http://localhost:3000 в браузере
echo 2. Перейдите в раздел LLM Dashboard
echo 3. Настройте локального провайдера LLM
echo 4. Протестируйте генерацию кода
echo.
echo ⚡ Оптимальные настройки для RTX 3070 8GB:
echo   - Context Window: 16384
echo   - Temperature: 0.1
echo   - Max Tokens: 2048
echo   - Потребление: ~2GB VRAM + 8GB RAM
echo.
echo 🛑 Для остановки: закройте окна или нажмите Ctrl+C в терминалах
echo.
pause