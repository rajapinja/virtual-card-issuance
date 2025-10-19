# Project Details 💳

A **modular fintech platform** enabling merchants to issue virtual cards, fund accounts, and track transactions through a **secure, auditable ledger**.

---

## 📝 Requirements – Virtual Card Issuance Application

- Develop and implement **merchant onboarding** processes, including registration, login, and verification.
- Design and manage **cardholder management** features (name, email, status, etc.).
- Create **APIs for virtual card issuance** with mock bank integration.
- Implement **wallet funding** and balance tracking functionalities.
- Develop a comprehensive **Ledger API** to log all transactions (debits & credits).
- Implement **spending limit validation** logic.
- Manage **transaction processing** using an event-driven architecture (Kafka).

---

## 🛠️ Key Skills

**Key Requirements:**
- Proficiency in Java & Spring Boot
- Experience with modern architecture & event-driven processing
- Familiarity with third-party integrations
- Strong understanding of **secure, auditable ledger systems**

**Ideal Skills & Experience:**
- Backend development experience in financial or payment systems
- Knowledge of authentication methods (Email/Password, 2FA, OAuth)
- Ability to design scalable and secure APIs
- Strong problem-solving skills and attention to detail

> If you're passionate about building cutting-edge fintech, this project is for you!

---

## 🚀 Solution Details

You're building a **foundational fintech platform** with modular architecture. Here’s the high-level implementation plan:

---

### ✅ 1. Merchant Onboarding

**Features:**
- Registration: Collect name, email, phone, business info
- Login: Secure with bcrypt + JWT
- Verification: KYC document upload, email/OTP verification

**Stack:**
- Spring Boot + JPA
- PostgreSQL
- Spring Security + JWT

**Endpoints:**  

    POST /api/merchants/register
    POST /api/merchants/login
    GET /api/merchants/verify-email
    POST /api/merchants/upload-kyc


---

### ✅ 2. Cardholder Management

**Data Model:**
- Cardholder: `id, merchant_id, name, email, status`

**Endpoints:**  

    POST /api/cardholders
    GET /api/cardholders/{id}
    PATCH /api/cardholders/{id}


---

### ✅ 3. Virtual Card Issuance (Mock Bank Integration)

**Mock Bank:**
- Simulate card issuance using REST stub or WireMock
- Store: `card_number, expiry, cvv, cardholder_id, wallet_id`

**Endpoint:**  

    POST /api/cards/issue


---

### ✅ 4. Wallet Funding & Balance Tracking

**Wallet Table:**
- `wallet: id, merchant_id, balance, currency, status`

**Funding via:**
- Manual admin load
- Simulated payment webhook

**Endpoints:**  

    POST /api/wallets/fund
    GET /api/wallets/{walletId}/balance


---

### ✅ 5. Ledger API (Double-Entry)

**Schema:**
- `ledger_entry: id, from_account, to_account, amount, currency, txn_id, type, status, timestamp`

**Notes:**
- Each wallet/card/account is an account in the ledger
- Every action creates **balanced debit/credit entries**

**Endpoints:**  

    POST /api/ledger/entries
    GET /api/ledger/txns/{txnId}


---

### ✅ 6. Spending Limit Validation

- Limit per cardholder (e.g., per day, per txn)
- Enforced via pre-check before processing
- Store limits in `limits` table:  

limits: cardholder_id, daily_limit, txn_limit, spent_today


---

### ✅ 7. Event-Driven Transaction Processing (Kafka)

**Topics:**
- `txn.funded`
- `txn.spend_requested`
- `txn.spending_validated`
- `ledger.entry_created`

**Flow:**
1. `/api/cards/spend` → publishes to `txn.spend_requested`
2. Kafka consumer checks limits & wallet balance
3. If valid → creates Ledger entries
4. Updates balances → emits `txn.completed`

---

### 🧠 Additional Notes

- Use **Outbox pattern** for transactional Kafka publishing
- Spring Cloud Stream or plain `spring-kafka`
- Idempotency tokens to prevent double spends
- Prometheus/Grafana for monitoring transactions & balances

---

> Would you like me to **also create a full folder structure, DB schema diagram, and Kafka producer/consumer examples** next?  


---

### ✅ 7. Event-Driven Transaction Processing (Kafka)

**Topics:**
- `txn.funded`
- `txn.spend_requested`
- `txn.spending_validated`
- `ledger.entry_created`

**Flow:**
1. `/api/cards/spend` → publishes to `txn.spend_requested`
2. Kafka consumer checks limits & wallet balance
3. If valid → creates Ledger entries
4. Updates balances → emits `txn.completed`

---

### 🧠 Additional Notes

- Use **Outbox pattern** for transactional Kafka publishing
- Spring Cloud Stream or plain `spring-kafka`
- Idempotency tokens to prevent double spends
- Prometheus/Grafana for monitoring transactions & balances

---

> Would you like me to **also create a full folder structure, DB schema diagram, and Kafka producer/consumer examples** next?  

This .md version now has:
Structured sections & headings for easy reading
Code blocks for endpoints & DB examples
✅ Task checkmarks for each module
💡 Additional notes section for best practices