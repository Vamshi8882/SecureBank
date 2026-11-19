# SecureBank — Digital Banking Demonstration

A Java 21 / Spring Boot 3.3 / MySQL demonstration application with a same-origin web UI.

## Included modules
- Customer registration/login (BCrypt password hashing, JWT authentication, in-memory login lockout)
- Customer profile: full name, email, phone, address, date of birth
- Account number and generated SecureBank demo UPI alias
- Account balance, atomic internal account transfers, demo UPI-alias transfers
- Paginated transaction history
- KYC metadata submission and internal admin review workflow
- Loan application submission and customer status tracking
- Admin-only KYC and loan review endpoints
- CSP, security headers, server-side validation and generic error responses

## Run locally (PowerShell)
```powershell
$bytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$env:DB_USER = "root"
$env:DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
$env:DEMO_DEPOSITS = "true"
mvn clean spring-boot:run
```
Open http://localhost:8080.

## Optional admin reviewer
Set `ADMIN_EMAILS` to a comma-separated allowlist of already registered reviewer accounts before issuing a fresh login token:
```powershell
$env:ADMIN_EMAILS = "reviewer@example.com"
```
The allowlist is a demo role assignment mechanism. For production, use managed identity provisioning, MFA, audited role administration and separation of duties.

## API summary
- `POST /api/auth/register`, `POST /api/auth/login`
- `GET /api/account`, `POST /api/account/transfer`, `POST /api/account/upi-transfer`
- `POST /api/account/deposit` (demo-only; disable outside local testing)
- `GET /api/account/transactions`
- `GET/PUT /api/profile`
- `GET/POST /api/kyc`
- `POST/GET /api/loans`
- `GET /api/admin/kyc`, `PUT /api/admin/kyc/{id}`, `GET /api/admin/loans`, `PUT /api/admin/loans/{id}` (ADMIN role)

All customer APIs except authentication require `Authorization: Bearer <token>`.

## Important scope and security limitations
This is a learning/demo system, NOT a production bank, payment app or regulated KYC platform.
- Internal UPI-like transfers are simulated using SecureBank aliases; no NPCI/UPI/payment gateway is connected.
- KYC stores only document type and the last four characters. It does not collect full Aadhaar/PAN, upload identity documents, or verify with UIDAI/NSDL/government systems.
- Loan applications are workflow records only; there is no credit bureau, underwriting or lending/disbursement integration.
- Demo deposits create test funds and must be disabled outside local development.
- `ADMIN_EMAILS` is a local demonstration allowlist, not a complete production IAM solution.
- For real deployment: use TLS, strong external secrets, dedicated least-privilege DB credentials, Flyway migrations, `ddl-auto=validate`, encrypted object storage, KMS-managed field encryption, MFA, refresh-token rotation/revocation, distributed rate limiting, immutable ledger design, idempotency keys, monitoring, backups, penetration testing and applicable regulatory/legal review.
- Do not enter real personal identifiers, card details, OTPs or passwords from other services.
