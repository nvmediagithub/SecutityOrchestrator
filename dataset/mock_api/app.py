import json
import secrets
import sqlite3
from contextlib import contextmanager
from datetime import datetime, timedelta
from pathlib import Path

from fastapi import Depends, FastAPI, Header, HTTPException, Query, Request, status
from fastapi.responses import JSONResponse
from pydantic import BaseModel, Field
from typing import Dict, List, Optional

DB_PATH = Path(__file__).resolve().parent / "mock_api.db"

APP = FastAPI(
    title="Mock Virtual Bank API",
    version="0.2.0",
    description="Сложная мок-версия OpenAPI с SQLite, транзакциями, предупреждениями и намеренными 'soft' ошибками.",
)


def init_db():
    if DB_PATH.exists():
        return
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.executescript(
        """
        CREATE TABLE tokens (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            client_id TEXT NOT NULL,
            secret TEXT NOT NULL,
            token TEXT NOT NULL UNIQUE,
            expires_at TEXT NOT NULL
        );
        CREATE TABLE accounts (
            account_id TEXT PRIMARY KEY,
            currency TEXT,
            account_type TEXT,
            nickname TEXT,
            servicer TEXT,
            owner_client TEXT,
            balance REAL DEFAULT 0
        );
        CREATE TABLE consents (
            consent_id TEXT PRIMARY KEY,
            client_id TEXT,
            permissions TEXT,
            requesting_bank TEXT,
            status TEXT
        );
        CREATE TABLE transactions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            account_id TEXT,
            amount REAL,
            currency TEXT,
            description TEXT,
            created_at TEXT
        );
        CREATE TABLE teams (
            team_id TEXT PRIMARY KEY,
            name TEXT,
            secret TEXT,
            registered_at TEXT
        );
        """
    )
    cursor.executemany(
        "INSERT INTO accounts (account_id, currency, account_type, nickname, servicer, owner_client, balance) VALUES (?, ?, ?, ?, ?, ?, ?)",
        [
            ("acc-1001", "RUB", "Personal", "Primary current", "VBank", "team100", 1200.0),
            ("acc-2001", "USD", "Business", "Merchant wallet", "ABank", "team200", 300.0),
        ],
    )
    cursor.executemany(
        "INSERT INTO transactions (account_id, amount, currency, description, created_at) VALUES (?, ?, ?, ?, ?)",
        [
            ("acc-1001", 100.0, "RUB", "Initial deposit", datetime.utcnow().isoformat()),
            ("acc-2001", 5000.0, "USD", "Merchant payout", datetime.utcnow().isoformat()),
        ],
    )
    conn.commit()
    conn.close()


@contextmanager
def get_db():
    conn = sqlite3.connect(DB_PATH, detect_types=sqlite3.PARSE_DECLTYPES)
    conn.row_factory = sqlite3.Row
    try:
        yield conn
    finally:
        conn.close()


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    client_id: str
    expires_in: int = 86400


class Account(BaseModel):
    account_id: str
    currency: str
    account_type: str
    nickname: str
    servicer: str
    owner_client: Optional[str]
    balance: float = 0.0


class ConsentRequest(BaseModel):
    client_id: str = Field(..., description="ID клиента, по которому создаётся согласие")
    permissions: List[str] = Field(..., description="Список разрешений на данные")
    requesting_bank: str = Field(..., description="ID запрашивающего банка")
    reason: Optional[str] = Field(None, description="Цель запроса согласия")


class ConsentResponse(BaseModel):
    consent_id: str
    status: str = "approved"
    auto_approved: bool = True
    warning: Optional[str]


class PaymentRequest(BaseModel):
    from_account: str
    to_account: str
    amount: float
    currency: str = "RUB"


class TeamRegistrationRequest(BaseModel):
    name: str = Field(..., description="Название команды/проекта")


class TeamRegistrationResponse(BaseModel):
    team_id: str
    secret: str
    registered_at: str


@APP.on_event("startup")
def setup_database():
    init_db()


def require_query_credentials(
    client_id: Optional[str] = Query(None, description="client_id команды"),
    client_secret: Optional[str] = Query(None, description="client_secret команды"),
):
    missing = []
    if not client_id:
        missing.append({"name": "client_id", "reason": "Обязателен"})
    if not client_secret:
        missing.append({"name": "client_secret", "reason": "Обязателен"})
    if missing:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=[{"type": "missing", "loc": ["query", entry["name"]], "msg": entry["reason"]} for entry in missing],
        )
    return {"client_id": client_id, "client_secret": client_secret}


def require_bearer_token(authorization: Optional[str] = Header(None), request: Request = None):
    if not authorization or not authorization.lower().startswith("bearer "):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authorization: Bearer <token> required",
        )
    token = authorization.split(" ", 1)[1]
    with get_db() as conn:
        row = conn.execute("SELECT expires_at FROM tokens WHERE token = ?", (token,)).fetchone()
    if not row:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token unknown")
    if datetime.fromisoformat(row["expires_at"]) < datetime.utcnow():
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token expired")
    if request:
        request.state.token = token
    return token


def store_token(client_id: str, client_secret: str) -> TokenResponse:
    expires = datetime.utcnow() + timedelta(hours=24)
    token = f"token-{client_id}-{int(expires.timestamp())}"
    with get_db() as conn:
        conn.execute(
            "INSERT OR REPLACE INTO tokens (client_id, secret, token, expires_at) VALUES (?, ?, ?, ?)",
            (client_id, client_secret, token, expires.isoformat()),
        )
        conn.commit()
    return TokenResponse(access_token=token, client_id=client_id, expires_in=86400)


def register_team_secret(name: str) -> TeamRegistrationResponse:
    team_id = f"team-{name.lower().replace(' ', '-')}-{int(datetime.utcnow().timestamp()) % 10000}"
    secret = secrets.token_urlsafe(12)
    at = datetime.utcnow().isoformat()
    with get_db() as conn:
        conn.execute(
            "INSERT INTO teams (team_id, name, secret, registered_at) VALUES (?, ?, ?, ?)",
            (team_id, name, secret, at),
        )
        conn.commit()
    return TeamRegistrationResponse(team_id=team_id, secret=secret, registered_at=at)


def fetch_team_secret(team_id: str) -> Optional[Dict[str, str]]:
    with get_db() as conn:
        row = conn.execute("SELECT * FROM teams WHERE team_id = ?", (team_id,)).fetchone()
    if not row:
        return None
    return {"team_id": row["team_id"], "secret": row["secret"], "name": row["name"], "registered_at": row["registered_at"]}


@APP.middleware("http")
async def response_headers(request: Request, call_next):
    response = await call_next(request)
    response.headers.setdefault("X-Mock-Api-Version", "0.2")
    if request.url.path.startswith("/accounts"):
        response.headers.setdefault("X-Account-Flow", "true")
    return response


@APP.post("/auth/bank-token", response_model=TokenResponse)
def create_bank_token(credentials: Dict[str, str] = Depends(require_query_credentials)):
    if credentials["client_id"] == "bad-client":
        return JSONResponse(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            content={"detail": [{"type": "invalid", "msg": "Test client barred"}]},
        )
    return store_token(credentials["client_id"], credentials["client_secret"])


@APP.post("/teams", response_model=TeamRegistrationResponse)
def register_team(payload: TeamRegistrationRequest):
    return register_team_secret(payload.name)


@APP.get("/teams/{team_id}/secret")
def get_team_secret(team_id: str):
    secret = fetch_team_secret(team_id)
    if not secret:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Team not registered")
    return secret


def fetch_accounts(conn: sqlite3.Connection) -> List[Account]:
    cursor = conn.execute("SELECT * FROM accounts")
    return [Account(**dict(row)) for row in cursor.fetchall()]


def get_account_row(conn: sqlite3.Connection, account_id: str) -> Optional[sqlite3.Row]:
    return conn.execute("SELECT * FROM accounts WHERE account_id = ?", (account_id,)).fetchone()


def adjust_account_balance(account_id: str, delta: float):
    with get_db() as conn:
        conn.execute("UPDATE accounts SET balance = balance + ? WHERE account_id = ?", (delta, account_id))
        conn.commit()


@APP.get("/accounts", response_model=List[Account])
def list_accounts(
    token: str = Depends(require_bearer_token),
    client_id: Optional[str] = Query(None, description="client_id клиента для межбанковых запросов"),
):
    with get_db() as conn:
        accounts = fetch_accounts(conn)
    if client_id == "risky-client":
        raise HTTPException(status_code=status.HTTP_429_TOO_MANY_REQUESTS, detail="Rate limit hit")
    return accounts


@APP.get("/accounts/{account_id}", response_model=Account)
def get_account(account_id: str, token: str = Depends(require_bearer_token)):
    with get_db() as conn:
        row = conn.execute("SELECT * FROM accounts WHERE account_id = ?", (account_id,)).fetchone()
    if not row:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Account not found")
    return Account(**dict(row))


@APP.get("/accounts/{account_id}/balances")
def get_balance(
    account_id: str,
    token: str = Depends(require_bearer_token),
    requesting_bank: Optional[str] = Header(None),
):
    if not requesting_bank:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="X-Requesting-Bank header is expected for sandbox flows",
        )
    with get_db() as conn:
        row = get_account_row(conn, account_id)
    if not row:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Account not found")
    return {
        "account_id": account_id,
        "balance": row["balance"] or 0,
        "currency": "RUB",
        "warning": "Баланс может быть слегка устаревшим",
    }


@APP.post("/account-consents/request", response_model=ConsentResponse)
def request_consent(payload: ConsentRequest, token: str = Depends(require_bearer_token)):
    consent_id = f"consent-{payload.client_id}-{int(datetime.utcnow().timestamp())}"
    with get_db() as conn:
        conn.execute(
            "INSERT INTO consents (consent_id, client_id, permissions, requesting_bank, status) VALUES (?, ?, ?, ?, ?)",
            (
                consent_id,
                payload.client_id,
                json.dumps(payload.permissions),
                payload.requesting_bank,
                "approved",
            ),
        )
        conn.commit()
    warning = (
        "Согласие на чтение балансов предоставлено, но в реальной системе требуется подтверждение клиента."
        if "ReadBalances" in payload.permissions
        else None
    )
    return ConsentResponse(consent_id=consent_id, warning=warning)


def persist_transaction(account_id: str, amount: float, currency: str, description: str):
    with get_db() as conn:
        conn.execute(
            "INSERT INTO transactions (account_id, amount, currency, description, created_at) VALUES (?, ?, ?, ?, ?)",
            (account_id, amount, currency, description, datetime.utcnow().isoformat()),
        )
        conn.commit()


@APP.get("/accounts/{account_id}/transactions")
def account_transactions(
    account_id: str,
    token: str = Depends(require_bearer_token),
    limit: int = Query(10, ge=1, le=50),
):
    with get_db() as conn:
        rows = conn.execute(
            "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC LIMIT ?", (account_id, limit)
        ).fetchall()
    if not rows:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Transactions not found")
    return [
        {
            "id": row["id"],
            "amount": row["amount"],
            "currency": row["currency"],
            "description": row["description"],
            "created_at": row["created_at"],
        }
        for row in rows
    ]


@APP.post("/payments")
def create_payment(
    payment: PaymentRequest,
    token: str = Depends(require_bearer_token),
    requesting_bank: Optional[str] = Header(None),
    simulate_insufficient: Optional[str] = Header(None, alias="X-Simulate-Insufficient"),
):
    if not requesting_bank:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="X-Requesting-Bank header required")
    with get_db() as conn:
        row = get_account_row(conn, payment.from_account)
    if not row:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Source account not found")
    balance = row["balance"] or 0
    if simulate_insufficient and simulate_insufficient.lower() == "true":
        return JSONResponse(
            status_code=status.HTTP_402_PAYMENT_REQUIRED,
            content={"detail": "Insufficient funds transaction simulated", "balance": balance},
        )
    if payment.amount > balance:
        return JSONResponse(
            status_code=status.HTTP_402_PAYMENT_REQUIRED,
            content={
                "detail": "Insufficient funds",
                "available": balance,
                "required": payment.amount,
            },
        )
    if payment.amount > 100000:
        return JSONResponse(
            status_code=status.HTTP_202_ACCEPTED,
            content={
                "status": "accepted",
                "warning": "Сумма высока, ручная проверка перед подтверждением",
                "note": "Пока выполняется в фоновом режиме",
            },
        )
    adjust_account_balance(payment.from_account, -payment.amount)
    adjust_account_balance(payment.to_account, payment.amount)
    return {
        "status": "completed",
        "processed_at": datetime.utcnow().isoformat(),
        "to_account": payment.to_account,
        "amount": payment.amount,
        "currency": payment.currency,
    }
