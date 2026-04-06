# Finance Dashboard Backend

A role-based finance management backend created for the Zorvyn internship assessment. 
It offers secure REST APIs to manage financial records, users, and dashboard analytics. 
JWT-based authentication and role-level access control are enforced at the security layer.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (Virtual Threads enabled via Project Loom) |
| Framework | Spring Boot 3.5.13 |
| Security | Spring Security 6 + JWT (jjwt 0.12.6) |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA + Hibernate |
| Validation | Jakarta Bean Validation |
| Documentation | Springdoc OpenAPI 2.6.0 (Swagger UI) |
| Build Tool | Maven |
| Containerization | Docker + Docker Compose |

---

## Running the Project

### Option 1 — Docker (Recommended)

No Java or PostgreSQL installation required. Just Docker Desktop.

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/finance-dashboard.git
cd finance-dashboard

# 2. Start everything
docker-compose up --build

# 3. Wait for this line in the logs:
# --- Seeding complete. Use these credentials to test ---

# 4. Open Swagger UI
http://localhost:8081/swagger-ui.html
```

To stop:
```bash
docker-compose down

# To also reset the database
docker-compose down -v
```

---

### Option 2 — Local Development

**Prerequisites:**
- Java 21
- PostgreSQL 14+
- Maven 3.8+

```bash
# 1. Create the database
psql -U postgres -c "CREATE DATABASE finance_dashboard;"

# 2. Copy and configure environment file
cp .env.example .env
# Edit .env with your PostgreSQL credentials

# 3. Run the application
mvn spring-boot:run

# 4. Open Swagger UI
http://localhost:8081/swagger-ui.html
```

Hibernate creates all tables automatically on the first run. Three users and some sample transactions are added at startup.

---

## Test Credentials

Three users are seeded automatically when the app starts.

| Role | Email | Password | Access Level |
|---|---|---|---|
| Admin | admin@zorvyn.com | admin123 | Full access |
| Analyst | analyst@zorvyn.com | analyst123 | Read + Dashboard analytics |
| Viewer | viewer@zorvyn.com | viewer123 | Read only |

### How to authenticate in Swagger UI

1. Open `http://localhost:8081/swagger-ui.html`
2. Call `POST /api/auth/login` with any credential above
3. Copy the `token` value from the response
4. Click the **Authorize** button at the top right of Swagger UI
5. Paste the token and click Authorize
6. All subsequent requests will carry the token automatically

---

## Role Permissions

| Action | Viewer | Analyst | Admin |
|---|---|---|---|
| Register / Login / Logout | Yes | Yes | Yes |
| View own profile | Yes | Yes | Yes |
| View transactions | Yes | Yes | Yes |
| Filter transactions | Yes | Yes | Yes |
| Export transactions to CSV | No | Yes | Yes |
| View dashboard analytics | No | Yes | Yes |
| Detect anomalies | No | Yes | Yes |
| Create transaction | No | No | Yes |
| Update transaction | No | No | Yes |
| Delete transaction (soft) | No | No | Yes |
| List all users | No | No | Yes |
| Change user role | No | No | Yes |
| Activate / deactivate user | No | No | Yes |
| View audit logs | No | No | Yes |

---

## API Reference

### Authentication - `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | /api/auth/register | Public | Register new account (defaults to VIEWER role) |
| POST | /api/auth/login | Public | Login and receive JWT token |
| POST | /api/auth/logout | Any | Logout (client discards token) |

### Users - `/api/users`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | /api/users | Admin | List all users (paginated) |
| GET | /api/users/me | Any | Get own profile |
| GET | /api/users/{id} | Admin | Get user by ID |
| PUT | /api/users/{id}/role | Admin | Change user role |
| PUT | /api/users/{id}/status | Admin | Activate or deactivate user |

### Transactions - `/api/transactions`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | /api/transactions | Admin | Create transaction |
| GET | /api/transactions | Any | List with filters and pagination |
| GET | /api/transactions/{id} | Any | Get by ID |
| PUT | /api/transactions/{id} | Admin | Update transaction |
| DELETE | /api/transactions/{id} | Admin | Soft delete |
| GET | /api/transactions/export | Admin, Analyst | Download as CSV |

#### Filter parameters for `GET /api/transactions`

| Parameter | Type | Example | Description |
|---|---|---|---|
| type | Enum | INCOME or EXPENSE | Filter by type |
| category | Enum | SALARY, FOOD, RENT | Filter by category |
| startDate | Date | 2026-01-01 | From date (yyyy-MM-dd) |
| endDate | Date | 2026-01-31 | To date (yyyy-MM-dd) |
| keyword | String | salary | Search in notes field |
| page | Integer | 0 | Page number (0-based) |
| size | Integer | 10 | Records per page |

### Dashboard - `/api/dashboard`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | /api/dashboard/overview | Admin, Analyst | Total income, expense, net balance, counts |
| GET | /api/dashboard/category-wise | Admin, Analyst | Breakdown by category and type |
| GET | /api/dashboard/recent-activity | Admin, Analyst | Latest transactions (default 10, max 50) |
| GET | /api/dashboard/monthly-trends | Admin, Analyst | Month by month income vs expense |

### Audit Logs - `/api/audit-logs`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | /api/audit-logs | Admin | Full paginated audit trail |
| GET | /api/audit-logs/entity/{entityId} | Admin | Change history for one record |
| GET | /api/audit-logs/user/{userId} | Admin | All actions by a specific user |
| GET | /api/audit-logs/action/{action} | Admin | Filter by CREATED, UPDATED, or DELETED |

### Anomaly Detection - `/api/anomalies`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | /api/anomalies | Admin, Analyst | All flagged anomalous transactions |
| GET | /api/anomalies/critical | Admin, Analyst | Critical anomalies only |

---

## Database Schema

### users

| Column | Type | Notes |
|---|---|---|
| id | UUID (PK) | Auto-generated |
| full_name | VARCHAR(100) | Required |
| email | VARCHAR(150) | Unique |
| password_hash | VARCHAR(255) | BCrypt hashed |
| role | VARCHAR(20) | VIEWER, ANALYST, ADMIN |
| status | VARCHAR(20) | ACTIVE, INACTIVE |
| created_at | TIMESTAMP | Auto-set on insert |
| updated_at | TIMESTAMP | Auto-set on update |

### transactions

| Column | Type | Notes |
|---|---|---|
| id | UUID (PK) | Auto-generated |
| created_by | UUID (FK) | References users.id |
| amount | DECIMAL(15,2) | Never float — fintech precision |
| type | VARCHAR(10) | INCOME or EXPENSE |
| category | VARCHAR(30) | SALARY, FOOD, RENT, UTILITIES, INVESTMENT, ENTERTAINMENT, OTHER |
| transaction_date | DATE | Actual date money moved |
| notes | TEXT | Optional |
| is_deleted | BOOLEAN | Soft delete flag — default false |
| created_at | TIMESTAMP | When record was entered |
| updated_at | TIMESTAMP | Last modification |

### audit_logs

| Column | Type | Notes |
|---|---|---|
| id | UUID (PK) | Auto-generated |
| entity_type | VARCHAR(50) | e.g. TRANSACTION |
| entity_id | UUID | ID of the changed record |
| action | VARCHAR(20) | CREATED, UPDATED, DELETED |
| performed_by | UUID | User who made the change |
| performed_by_email | VARCHAR | Email for readability |
| old_value | TEXT | JSON snapshot before change |
| new_value | TEXT | JSON snapshot after change |
| ip_address | VARCHAR(50) | Client IP address |
| created_at | TIMESTAMP | When the action occurred |

---

## Project Structure

```
src/main/java/com/zorvyn/financedashboard/
├── config/
│   ├── DataSeeder.java           
│   ├── OpenApiConfig.java        
│   └── SecurityConfig.java       
├── controller/
│   ├── AnomalyController.java
│   ├── AuditController.java
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── TransactionController.java
│   ├── UserController.java
│   └── HealthController.java
├── service/
│   ├── AnomalyService.java
│   ├── AuditService.java
│   ├── AuthService.java
│   ├── CsvExportService.java
│   ├── DashboardService.java
│   ├── TransactionService.java
│   └── UserService.java
├── repository/
│   ├── AuditLogRepository.java
│   ├── TransactionRepository.java
│   ├── TransactionSpecification.java
│   └── UserRepository.java
├── entity/
│   ├── AuditLog.java
│   ├── Transaction.java
│   └── User.java
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── TransactionRequest.java
│   │   ├── UpdateUserRoleRequest.java
│   │   └── UpdateUserStatusRequest.java
│   └── response/
│       ├── AnomalyResponse.java
│       ├── AnomalySummaryResponse.java
│       ├── ApiResponse.java
│       ├── AuditLogResponse.java
│       ├── AuthResponse.java
│       ├── CategorySummaryResponse.java
│       ├── DashboardOverviewResponse.java
│       ├── MonthlyTrendResponse.java
│       ├── TransactionResponse.java
│       └── UserResponse.java
├── enums/
│   ├── AnomalySeverity.java
│   ├── AuditAction.java
│   ├── Category.java
│   ├── Role.java
│   ├── TransactionType.java
│   └── UserStatus.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
└── security/
    ├── CustomUserDetailsService.java
    ├── JwtAuthFilter.java
    └── JwtService.java
```

---

## Sample curl Commands

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@zorvyn.com","password":"admin123"}'
```

### Get all transactions
```bash
curl http://localhost:8081/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Create a transaction
```bash
curl -X POST http://localhost:8081/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 75000.00,
    "type": "INCOME",
    "category": "SALARY",
    "transactionDate": "2026-01-15",
    "notes": "Monthly salary"
  }'
```

### Export transactions to CSV
```bash
curl -X GET "http://localhost:8081/api/transactions/export?type=INCOME" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -o transactions.csv
```

### Get dashboard overview
```bash
curl http://localhost:8081/api/dashboard/overview \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Detect anomalies
```bash
curl http://localhost:8081/api/anomalies \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## Error Response Format

All errors return a consistent JSON shape:

```json
{
  "status": 403,
  "message": "You do not have permission to perform this action",
  "timestamp": "2026-01-15T10:30:00",
  "fieldErrors": null
}
```

Validation errors include field-level details:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-01-15T10:30:00",
  "fieldErrors": {
    "amount": "Amount must be greater than 0",
    "email": "Email must be valid"
  }
}
```

---

## Design Decisions and Assumptions

### Assumptions

- All newly registered users are assigned the VIEWER role by default. Admins promote them afterward via `PUT /api/users/{id}/role`.
- The assignment describes Viewer as able to "view dashboard data" - this was interpreted as viewing transaction records only. Dashboard analytics are restricted to Analyst and Admin to create meaningful role separation and realistic access boundaries.
- Soft delete is used for transactions to preserve financial audit history. Hard delete is never performed on financial records - this is a compliance requirement in real fintech systems.
- `transaction_date` represents when the money actually moved, separate from `created_at` which records when the entry was added to the system. Conflating these two is a common mistake in financial systems.
- Audit logs are only generated for changes made through the API. Seeded data does not produce audit entries by design - seeding bypasses the service layer intentionally to avoid polluting the audit trail with test data.

### Tradeoffs

- `DECIMAL(15,2)` is used for all monetary amounts instead of `FLOAT` or `DOUBLE` to avoid floating-point precision errors. This is a strict requirement in fintech. Floating-point arithmetic on currency leads to rounding errors that add up over time.
- All dashboard aggregations use JPQL `@Query` with `SUM`, `COUNT`, and `GROUP BY`. No records are loaded into Java memory for processing. This approach scales to millions of records without losing performance.
- `JpaSpecificationExecutor` is used for transaction filtering to build dynamic queries cleanly without string concatenation or multiple repository methods.
- Enums are stored as strings (`@Enumerated(EnumType.STRING)`) to prevent unnoticed data corruption if the enum ordinal order changes.
- Java 21 virtual threads are enabled via `spring.threads.virtual.enabled=true` for better request concurrency without code changes. This matters for a high-throughput finance platform.
- Roles are enforced at the Spring Security method level using `@PreAuthorize`. They are never checked with if-else statements inside service methods. This keeps access control centralized and auditable.
- Anomaly detection is done entirely in SQL using `AVG()` with `HAVING` clauses, with three severity levels: NORMAL, WARNING (50–200% above average), and CRITICAL (200%+ above average). This mimics the statistical baseline comparison used in real fraud detection systems.
- The audit log holds JSON snapshots of records before and after each change, along with the performing user's ID, email, and IP address. This meets compliance requirements in financial systems.
---

## Additional Features Beyond Requirements

| Feature | Description                                                                           |
|---|---------------------------------------------------------------------------------------|
| Audit Log | Complete trail of every create, update, and delete with before/after JSON snapshots   |
| Anomaly Detection | Flags transactions deviating significantly from category average with severity levels |
| CSV Export | Download filtered transactions as a properly escaped CSV file with Excel BOM support  |
| Health Endpoint | `GET /api/health` - service status and version                                        |
| Request ID | Every response includes `X-Request-Id` header for tracing                             |
| Request Logging | Every request logs method, path, status code, and duration                            |
| Docker Support | Single `docker-compose up --build` starts the full stack with no setup required       |

---

## Health Check

```bash
curl http://localhost:8081/api/health
```

```json
{
  "status": "UP",
  "version": "1.0.0",
  "service": "Finance Dashboard API",
  "timestamp": "2026-04-06T10:00:00"
}
```