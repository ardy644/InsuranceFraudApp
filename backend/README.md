# Vigilance Insurtech ML Risk Backend

An ultra-compact, zero-dependency REST backend for the Insurance Fraud Detection Android application.

## Specifications
- **Runtime:** Node.js 24+ (`node:http`, `node:sqlite`)
- **External Dependencies:** **0 (Zero npm packages, zero `node_modules`)**
- **Disk Footprint:** **~50 KB total** (including SQLite database file `vigilance.db`)
- **Port:** `8080` (Localhost: `http://127.0.0.1:8080`, Android Emulator: `http://10.0.2.2:8080`)

---

## Starting the Server

```bash
# From workspace root:
node backend/server.js
```
Or via npm script:
```bash
cd backend
npm start
```

---

## REST Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/health` | Server health, uptime, and engine info |
| `POST` | `/api/auth/login` | Officer authentication (email & password) |
| `GET` | `/api/dashboard/stats` | Operational KPIs, risk tier breakdown, 6-month trends |
| `GET` | `/api/claims` | List all claims (`?filter=suspicious`, `?status=...`, `?search=...`) |
| `GET` | `/api/claims/:id` | Lookup single claim by ID |
| `POST` | `/api/claims` | Create new claim + runs automated heuristic ML risk score |
| `PATCH` | `/api/claims/:id/status` | Update claim decision (`Approved`, `Flagged`, `Under Review`, etc.) |
| `POST` | `/api/claims/analyze` | Standalone fraud risk assessment & factor identification |
| `GET` | `/api/customers` | Customer portfolio with risk tiers |
| `GET` | `/api/analytics` | Forensic audit metrics & category breakdown |
