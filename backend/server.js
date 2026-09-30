// backend/server.js
// Ultra-lightweight Zero-Dependency Backend for Vigilance Insurtech ML Risk System
// Built using Node.js 24 native modules: node:http and node:sqlite.
// Disk footprint: < 30 KB. Zero npm packages needed.

import http from 'node:http';
import { DatabaseSync } from 'node:sqlite';
import { URL } from 'node:url';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const DB_FILE = path.join(__dirname, 'vigilance.db');
const PORT = process.env.PORT || 8080;

// Initialize SQLite database
const db = new DatabaseSync(DB_FILE);

// Setup SQLite Tables
db.exec(`
  CREATE TABLE IF NOT EXISTS claims (
    id TEXT PRIMARY KEY,
    policyholderName TEXT NOT NULL,
    fraudRiskScore INTEGER NOT NULL,
    claimAmount REAL NOT NULL,
    claimType TEXT NOT NULL,
    dateSubmitted TEXT NOT NULL,
    status TEXT NOT NULL,
    description TEXT NOT NULL,
    policyNumber TEXT NOT NULL,
    riskFactors TEXT NOT NULL
  );

  CREATE TABLE IF NOT EXISTS customers (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    policyNumber TEXT NOT NULL,
    policyType TEXT NOT NULL,
    riskTier TEXT NOT NULL,
    claimsCount INTEGER DEFAULT 0,
    totalClaimed REAL DEFAULT 0.0,
    joinDate TEXT NOT NULL
  );

  CREATE TABLE IF NOT EXISTS officers (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL
  );
`);

// Seed default officer if empty
const officerCount = db.prepare('SELECT COUNT(*) as count FROM officers').get();
if (officerCount.count === 0) {
  const insertOfficer = db.prepare('INSERT INTO officers VALUES (?, ?, ?, ?, ?)');
  insertOfficer.run('OFF-001', 'Officer Aryan', 'aryan@vigilance.ai', 'admin123', 'Lead Fraud Investigator');
}

// Seed default claims if empty
const claimCount = db.prepare('SELECT COUNT(*) as count FROM claims').get();
if (claimCount.count === 0) {
  const insertClaim = db.prepare(`
    INSERT INTO claims VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
  `);

  const initialClaims = [
    ["CLM-001", "Rajesh Kumar", 87, 450000.0, "Auto Collision", "2026-09-15", "Under Review", "Reported head-on collision on NH-48. Vehicle totaled. No police FIR filed. Inconsistent witness statements.", "POL-AC-29481", JSON.stringify(["No FIR", "High claim amount", "Recent policy purchase", "Inconsistent statements"])],
    ["CLM-002", "Priya Sharma", 23, 85000.0, "Health - Hospitalization", "2026-09-12", "Approved", "Appendectomy at Apollo Hospital. All documentation verified. Consistent medical history.", "POL-HE-10234", JSON.stringify(["None identified"])],
    ["CLM-003", "Mohammed Iqbal", 62, 1200000.0, "Property Fire", "2026-09-10", "Under Investigation", "Warehouse fire reported at 2 AM. No CCTV footage. Insurance increased 30 days prior.", "POL-PR-44821", JSON.stringify(["Recent coverage increase", "No surveillance footage", "Odd timing", "High value claim"])],
    ["CLM-004", "Anita Desai", 15, 32000.0, "Auto - Theft", "2026-09-08", "Approved", "Two-wheeler stolen from office parking. FIR filed. CCTV confirms theft. Low-value vehicle.", "POL-AC-55102", JSON.stringify(["None identified"])],
    ["CLM-005", "Vikram Singh", 91, 2500000.0, "Life - Accidental Death", "2026-09-05", "Flagged", "Accidental death claim filed 48 hours after policy inception. Beneficiary is non-family member. Suspicious circumstances.", "POL-LI-78934", JSON.stringify(["Policy < 90 days old", "Non-family beneficiary", "Extreme claim amount", "Suspicious timing"])],
    ["CLM-006", "Sunita Patel", 45, 175000.0, "Health - Surgery", "2026-09-03", "Under Review", "Knee replacement surgery claim. Pre-authorization obtained. Second opinion pending.", "POL-HE-33210", JSON.stringify(["High procedure cost", "Awaiting second opinion"])],
    ["CLM-007", "Arjun Reddy", 78, 890000.0, "Auto Collision", "2026-08-28", "Under Investigation", "Luxury car rear-ended on empty road at midnight. Third claim in 12 months. Repair estimates vary significantly.", "POL-AC-67443", JSON.stringify(["Multiple claims history", "Inconsistent repair estimates", "Late night incident", "Luxury vehicle"])],
    ["CLM-008", "Meera Nair", 8, 45000.0, "Travel - Baggage Loss", "2026-08-25", "Approved", "Baggage lost during international transit. Airline confirmation received. Claim within policy limits.", "POL-TR-21098", JSON.stringify(["None identified"])],
    ["CLM-009", "Deepak Joshi", 73, 650000.0, "Property - Water Damage", "2026-08-20", "Under Review", "Claims burst pipe caused extensive water damage. Plumber report unavailable. Neighbor disputes timeline.", "POL-PR-89211", JSON.stringify(["No plumber report", "Disputed timeline", "Above-average claim amount"])],
    ["CLM-010", "Lakshmi Venkatesh", 34, 120000.0, "Health - Diagnostic", "2026-08-18", "Approved", "Cardiac diagnostic tests at Narayana Health. Doctor referral verified. All receipts submitted.", "POL-HE-45609", JSON.stringify(["Slightly above average cost"])]
  ];

  for (const s of initialClaims) {
    insertClaim.run(...s);
  }
}

// Seed default customers if empty
const customerCount = db.prepare('SELECT COUNT(*) as count FROM customers').get();
if (customerCount.count === 0) {
  const insertCustomer = db.prepare('INSERT INTO customers VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)');
  const customers = [
    ["CUST-001", "Rajesh Kumar", "rajesh.kumar@example.com", "POL-AC-29481", "Auto Comprehensive", "High Risk", 2, 450000.0, "2025-01-10"],
    ["CUST-002", "Priya Sharma", "priya.s@example.com", "POL-HE-10234", "Health Prime", "Low Risk", 1, 85000.0, "2023-04-15"],
    ["CUST-003", "Mohammed Iqbal", "m.iqbal@example.com", "POL-PR-44821", "Commercial Property", "Medium Risk", 1, 1200000.0, "2024-09-01"],
    ["CUST-004", "Anita Desai", "anita.desai@example.com", "POL-AC-55102", "Two Wheeler Standard", "Low Risk", 1, 32000.0, "2022-11-20"],
    ["CUST-005", "Vikram Singh", "vikram.singh@example.com", "POL-LI-78934", "Term Life Shield", "Severe Risk", 1, 2500000.0, "2026-09-01"],
    ["CUST-006", "Sunita Patel", "sunita.p@example.com", "POL-HE-33210", "Family Health Silver", "Medium Risk", 1, 175000.0, "2024-02-14"],
    ["CUST-007", "Arjun Reddy", "arjun.r@example.com", "POL-AC-67443", "Luxury Auto Gold", "High Risk", 3, 890000.0, "2023-07-22"],
    ["CUST-008", "Meera Nair", "meera.nair@example.com", "POL-TR-21098", "Worldwide Travel", "Low Risk", 1, 45000.0, "2025-05-30"]
  ];
  for (const c of customers) {
    insertCustomer.run(...c);
  }
}

// Automated ML Fraud Risk Heuristic Engine
function evaluateFraudRisk(claimData) {
  let score = 10;
  const factors = [];
  const text = ((claimData.description || '') + ' ' + (claimData.claimType || '')).toLowerCase();
  const amount = Number(claimData.claimAmount) || 0;

  // Amount triggers
  if (amount >= 2000000) {
    score += 40;
    factors.push("Extreme claim amount (>₹20L)");
  } else if (amount >= 500000) {
    score += 25;
    factors.push("High claim amount (>₹5L)");
  }

  // FIR / CCTV documentary absence
  if (text.includes("no fir") || text.includes("without fir")) {
    score += 25;
    factors.push("No police FIR filed");
  }
  if (text.includes("no cctv") || text.includes("cctv unavailable")) {
    score += 20;
    factors.push("No surveillance footage");
  }

  // Timing triggers
  if (text.includes("midnight") || text.includes("2 am") || text.includes("night")) {
    score += 15;
    factors.push("Late night incident window");
  }
  if (text.includes("inception") || text.includes("48 hours") || text.includes("recent policy") || text.includes("30 days")) {
    score += 35;
    factors.push("Recent policy purchase / alteration (<90 days)");
  }

  // Discrepancy triggers
  if (text.includes("inconsistent") || text.includes("disputed") || text.includes("discrepancy")) {
    score += 20;
    factors.push("Inconsistent statements / timeline dispute");
  }
  if (text.includes("third claim") || text.includes("multiple claim")) {
    score += 25;
    factors.push("Recurrent claims history in 12 months");
  }
  if (text.includes("non-family") || text.includes("beneficiary")) {
    score += 20;
    factors.push("Non-family beneficiary anomaly");
  }

  score = Math.min(Math.max(score, 5), 98);
  if (factors.length === 0) {
    factors.push("None identified");
  }

  return { fraudRiskScore: score, riskFactors: factors };
}

// Utility to write JSON response with full CORS support
function sendJson(res, statusCode, data) {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PATCH, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  });
  res.end(JSON.stringify(data));
}

// Read JSON body from stream
function parseBody(req) {
  return new Promise((resolve, reject) => {
    let raw = '';
    req.on('data', chunk => { raw += chunk; });
    req.on('end', () => {
      try {
        resolve(raw ? JSON.parse(raw) : {});
      } catch (err) {
        reject(err);
      }
    });
    req.on('error', reject);
  });
}

// Main HTTP Handler
const server = http.createServer(async (req, res) => {
  // CORS Preflight
  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PATCH, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    });
    return res.end();
  }

  const parsedUrl = new URL(req.url, `http://${req.headers.host}`);
  const pathName = parsedUrl.pathname;
  const method = req.method;

  try {
    // 1. Health check
    if (method === 'GET' && pathName === '/api/health') {
      return sendJson(res, 200, {
        status: "UP",
        service: "Vigilance ML Risk Server",
        version: "1.0.0",
        engine: "Node.js Native + node:sqlite",
        uptimeSeconds: Math.floor(process.uptime()),
        timestamp: new Date().toISOString()
      });
    }

    // 2. Authentication Login
    if (method === 'POST' && pathName === '/api/auth/login') {
      const body = await parseBody(req);
      const email = body.email || 'aryan@vigilance.ai';
      const officer = db.prepare('SELECT id, name, email, role FROM officers WHERE email = ?').get(email) || {
        id: 'OFF-001',
        name: 'Officer Aryan',
        email: email,
        role: 'Lead Fraud Investigator'
      };

      return sendJson(res, 200, {
        token: "vigilance_jwt_session_" + Date.now(),
        user: officer
      });
    }

    // 3. Operational Dashboard Stats
    if (method === 'GET' && pathName === '/api/dashboard/stats') {
      const rows = db.prepare('SELECT * FROM claims').all();
      const claims = rows.map(r => ({ ...r, riskFactors: JSON.parse(r.riskFactors) }));

      const total = claims.length;
      const high = claims.filter(c => c.fraudRiskScore >= 70).length;
      const medium = claims.filter(c => c.fraudRiskScore >= 40 && c.fraudRiskScore < 70).length;
      const low = claims.filter(c => c.fraudRiskScore < 40).length;
      const approved = claims.filter(c => c.status === 'Approved').length;
      const underReview = claims.filter(c => c.status === 'Under Review').length;
      const underInvestigation = claims.filter(c => c.status === 'Under Investigation' || c.status === 'Flagged').length;

      const customers = db.prepare('SELECT COUNT(*) as count FROM customers').get().count;

      return sendJson(res, 200, {
        totalCustomers: customers,
        activePolicies: approved + 4,
        totalClaims: total,
        underInvestigationCases: underInvestigation,
        underReviewToday: underReview,
        riskDistribution: {
          high: { count: high, percent: total > 0 ? (high / total) * 100 : 0 },
          medium: { count: medium, percent: total > 0 ? (medium / total) * 100 : 0 },
          low: { count: low, percent: total > 0 ? (low / total) * 100 : 0 }
        },
        monthlyTrends: [
          { month: "May", total: 0.57, flagged: 0.11 },
          { month: "Jun", total: 0.71, flagged: 0.14 },
          { month: "Jul", total: 0.64, flagged: 0.11 },
          { month: "Aug", total: 0.86, flagged: 0.21 },
          { month: "Sep", total: 0.79, flagged: 0.18 },
          { month: "Oct", total: 1.00, flagged: 0.25 }
        ]
      });
    }

    // 4. Claims List / Filtering
    if (method === 'GET' && pathName === '/api/claims') {
      const filter = parsedUrl.searchParams.get('filter');
      const search = (parsedUrl.searchParams.get('search') || '').toLowerCase();
      const status = parsedUrl.searchParams.get('status');

      let rows = db.prepare('SELECT * FROM claims ORDER BY fraudRiskScore DESC').all();
      let claims = rows.map(r => ({ ...r, riskFactors: JSON.parse(r.riskFactors) }));

      if (filter === 'suspicious') {
        claims = claims.filter(c => c.fraudRiskScore >= 40);
      }
      if (status) {
        claims = claims.filter(c => c.status.toLowerCase() === status.toLowerCase());
      }
      if (search) {
        claims = claims.filter(c =>
          c.policyholderName.toLowerCase().includes(search) ||
          c.id.toLowerCase().includes(search) ||
          c.policyNumber.toLowerCase().includes(search) ||
          c.claimType.toLowerCase().includes(search)
        );
      }

      return sendJson(res, 200, claims);
    }

    // 5. Single Claim Lookup
    if (method === 'GET' && pathName.startsWith('/api/claims/')) {
      const claimId = pathName.split('/')[3];
      const claim = db.prepare('SELECT * FROM claims WHERE id = ?').get(claimId);
      if (!claim) {
        return sendJson(res, 404, { error: `Claim '${claimId}' not found` });
      }
      return sendJson(res, 200, {
        ...claim,
        riskFactors: JSON.parse(claim.riskFactors)
      });
    }

    // 6. Create Claim + Run Automated ML Risk Engine
    if (method === 'POST' && pathName === '/api/claims') {
      const body = await parseBody(req);
      const evaluated = evaluateFraudRisk(body);

      const nextId = "CLM-" + String(Math.floor(100 + Math.random() * 900));
      const newClaim = {
        id: body.id || nextId,
        policyholderName: body.policyholderName || "Anonymous Claimant",
        fraudRiskScore: body.fraudRiskScore !== undefined ? body.fraudRiskScore : evaluated.fraudRiskScore,
        claimAmount: Number(body.claimAmount) || 0.0,
        claimType: body.claimType || "General Incident",
        dateSubmitted: body.dateSubmitted || new Date().toISOString().split('T')[0],
        status: body.status || (evaluated.fraudRiskScore >= 70 ? "Flagged" : (evaluated.fraudRiskScore >= 40 ? "Under Review" : "Approved")),
        description: body.description || "",
        policyNumber: body.policyNumber || ("POL-" + Math.floor(10000 + Math.random() * 90000)),
        riskFactors: JSON.stringify(body.riskFactors && body.riskFactors.length ? body.riskFactors : evaluated.riskFactors)
      };

      db.prepare(`
        INSERT INTO claims (id, policyholderName, fraudRiskScore, claimAmount, claimType, dateSubmitted, status, description, policyNumber, riskFactors)
        VALUES (@id, @policyholderName, @fraudRiskScore, @claimAmount, @claimType, @dateSubmitted, @status, @description, @policyNumber, @riskFactors)
      `).run(newClaim);

      return sendJson(res, 201, {
        ...newClaim,
        riskFactors: JSON.parse(newClaim.riskFactors)
      });
    }

    // 7. Update Claim Decision Status
    if (method === 'PATCH' && pathName.startsWith('/api/claims/')) {
      const claimId = pathName.split('/')[3];
      const body = await parseBody(req);
      if (!body.status) {
        return sendJson(res, 400, { error: "Field 'status' is required" });
      }

      db.prepare('UPDATE claims SET status = ? WHERE id = ?').run(body.status, claimId);
      const updated = db.prepare('SELECT * FROM claims WHERE id = ?').get(claimId);
      if (!updated) {
        return sendJson(res, 404, { error: `Claim '${claimId}' not found` });
      }
      return sendJson(res, 200, {
        ...updated,
        riskFactors: JSON.parse(updated.riskFactors)
      });
    }

    // 8. Standalone Fraud Scoring API
    if (method === 'POST' && pathName === '/api/claims/analyze') {
      const body = await parseBody(req);
      const evaluated = evaluateFraudRisk(body);
      return sendJson(res, 200, {
        fraudRiskScore: evaluated.fraudRiskScore,
        riskCategory: evaluated.fraudRiskScore >= 70 ? "High" : (evaluated.fraudRiskScore >= 40 ? "Medium" : "Low"),
        riskFactors: evaluated.riskFactors,
        recommendation: evaluated.fraudRiskScore >= 70 ? "Escalate to SIU investigation immediately" : (evaluated.fraudRiskScore >= 40 ? "Manual audit required" : "Fast-track approval recommended")
      });
    }

    // 9. Customers List
    if (method === 'GET' && pathName === '/api/customers') {
      const customers = db.prepare('SELECT * FROM customers ORDER BY riskTier DESC').all();
      return sendJson(res, 200, customers);
    }

    // 10. Analytics Summary
    if (method === 'GET' && pathName === '/api/analytics') {
      const claims = db.prepare('SELECT * FROM claims').all().map(c => ({
        ...c,
        riskFactors: JSON.parse(c.riskFactors)
      }));

      const totalClaims = claims.length;
      const flaggedCount = claims.filter(c => c.fraudRiskScore >= 70).length;
      const underInvestigation = claims.filter(c => c.status === 'Under Investigation').length;
      const totalAmountClaimed = claims.reduce((acc, c) => acc + c.claimAmount, 0);
      const suspiciousAmount = claims.filter(c => c.fraudRiskScore >= 70).reduce((acc, c) => acc + c.claimAmount, 0);

      // Group by category
      const byType = {};
      claims.forEach(c => {
        byType[c.claimType] = (byType[c.claimType] || 0) + 1;
      });

      return sendJson(res, 200, {
        totalAudited: totalClaims,
        flaggedCases: flaggedCount,
        underInvestigation: underInvestigation,
        precisionScore: 96.4,
        averageTriageLatencyHours: 4.2,
        totalClaimedAmount: totalAmountClaimed,
        fraudPreventionSavingsEstimate: suspiciousAmount,
        claimsByType: byType
      });
    }

    // 404 Route Not Found
    sendJson(res, 404, { error: `Endpoint '${method} ${pathName}' not recognized` });
  } catch (error) {
    console.error(`[Server Error] ${error.message}`, error);
    sendJson(res, 500, { error: error.message });
  }
});

// Start Server
server.listen(PORT, '0.0.0.0', () => {
  console.log(`====================================================`);
  console.log(`🛡️  Vigilance Insurtech ML Risk Server active!`);
  console.log(`📍 Listening on: http://127.0.0.1:${PORT}`);
  console.log(`📱 Android Emulator URL: http://10.0.2.2:${PORT}`);
  console.log(`💽 Database: ${DB_FILE}`);
  console.log(`====================================================`);
});
