# Mock Bank REST API

Сложный FastAPI-сервис на SQLite, покрывающий ключевые маршруты из `dataset/openapi/openapi_test.json`, включая:

- `POST /auth/bank-token` — генерирует тестовый токен и сохраняет его в SQLite.
- `GET /accounts`, `/accounts/{id}`, `/accounts/{id}/balances` и `/accounts/{id}/transactions` — подставные ответы, `balances` требует заголовок `X-Requesting-Bank`.
- `POST /account-consents/request` — сохраняет согласие и возвращает предупреждение для некоторых permissions.
- `POST /payments` — имитирует платёж с предупреждением для больших сумм; транзакции сохраняются в SQLite.
- Мидлвар добавляет заголовки `X-Mock-Api-Version` и `X-Account-Flow`, а для мелких проблем (например, высокий лимит) возвращается `warning` вместо `500`.

## Служебные детали

- БД: `dataset/mock_api/mock_api.db`, хранимая таблицами `tokens`, `accounts`, `consents`, `transactions`, `teams`.
- Админский сценарий `"bad-client"` специально возвращает 422, чтобы тестировать обработку LLM-ошибок.
- Зависимость `require_bearer_token` проверяет срок действия токена, что позволяет моделировать истечение.
- Новые маршруты `POST /teams` и `GET /teams/{team_id}/secret` позволяют зарегистрировать команду и сразу получить `client_secret`, чтобы можно было проверять анализ, когда BPMN требует new credentials.
- `/payments` учитывает хранимый баланс: если `amount` превышает остаток, возвращается 402 с деталями; можно форсировать отказ с заголовком `X-Simulate-Insufficient: true`.

## Запуск

1. Установите зависимости (если ещё не установлены):

   ```bash
   python3 -m pip install fastapi uvicorn
   ```

2. Запустите сервис:

   ```bash
   python3 -m uvicorn dataset.mock_api.app:APP --host 0.0.0.0 --port 9080 --reload
   ```

3. Swagger UI: `http://localhost:9080/docs`, OpenAPI: `/openapi.json`.

Используйте этот API для отладки LLM-процессов, проверяйте HTTP-запросы и анализируйте BPMN-флоу из каталога `dataset/bpmn/`.
