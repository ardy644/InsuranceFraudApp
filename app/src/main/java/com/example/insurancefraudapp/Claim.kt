package com.example.insurancefraudapp

/**
 * Domain model representing an insurance claim with fraud risk assessment.
 */
data class Claim(
    val id: String,
    val policyholderName: String,
    val fraudRiskScore: Int,
    val claimAmount: Double,
    val claimType: String,
    val dateSubmitted: String,
    val status: String,
    val description: String,
    val policyNumber: String,
    val riskFactors: List<String>
)

/**
 * 10 mock Claim objects for development and UI prototyping.
 */
val mockClaims = listOf(
    Claim(
        id = "CLM-001",
        policyholderName = "Rajesh Kumar",
        fraudRiskScore = 87,
        claimAmount = 450000.00,
        claimType = "Auto Collision",
        dateSubmitted = "2026-09-15",
        status = "Under Review",
        description = "Reported head-on collision on NH-48. Vehicle totaled. No police FIR filed. Inconsistent witness statements.",
        policyNumber = "POL-AC-29481",
        riskFactors = listOf("No FIR", "High claim amount", "Recent policy purchase", "Inconsistent statements")
    ),
    Claim(
        id = "CLM-002",
        policyholderName = "Priya Sharma",
        fraudRiskScore = 23,
        claimAmount = 85000.00,
        claimType = "Health - Hospitalization",
        dateSubmitted = "2026-09-12",
        status = "Approved",
        description = "Appendectomy at Apollo Hospital. All documentation verified. Consistent medical history.",
        policyNumber = "POL-HE-10234",
        riskFactors = listOf("None identified")
    ),
    Claim(
        id = "CLM-003",
        policyholderName = "Mohammed Iqbal",
        fraudRiskScore = 62,
        claimAmount = 1200000.00,
        claimType = "Property Fire",
        dateSubmitted = "2026-09-10",
        status = "Under Investigation",
        description = "Warehouse fire reported at 2 AM. No CCTV footage. Insurance increased 30 days prior.",
        policyNumber = "POL-PR-44821",
        riskFactors = listOf("Recent coverage increase", "No surveillance footage", "Odd timing", "High value claim")
    ),
    Claim(
        id = "CLM-004",
        policyholderName = "Anita Desai",
        fraudRiskScore = 15,
        claimAmount = 32000.00,
        claimType = "Auto - Theft",
        dateSubmitted = "2026-09-08",
        status = "Approved",
        description = "Two-wheeler stolen from office parking. FIR filed. CCTV confirms theft. Low-value vehicle.",
        policyNumber = "POL-AC-55102",
        riskFactors = listOf("None identified")
    ),
    Claim(
        id = "CLM-005",
        policyholderName = "Vikram Singh",
        fraudRiskScore = 91,
        claimAmount = 2500000.00,
        claimType = "Life - Accidental Death",
        dateSubmitted = "2026-09-05",
        status = "Flagged",
        description = "Accidental death claim filed 48 hours after policy inception. Beneficiary is non-family member. Suspicious circumstances.",
        policyNumber = "POL-LI-78934",
        riskFactors = listOf("Policy < 90 days old", "Non-family beneficiary", "Extreme claim amount", "Suspicious timing")
    ),
    Claim(
        id = "CLM-006",
        policyholderName = "Sunita Patel",
        fraudRiskScore = 45,
        claimAmount = 175000.00,
        claimType = "Health - Surgery",
        dateSubmitted = "2026-09-03",
        status = "Under Review",
        description = "Knee replacement surgery claim. Pre-authorization obtained. Second opinion pending.",
        policyNumber = "POL-HE-33210",
        riskFactors = listOf("High procedure cost", "Awaiting second opinion")
    ),
    Claim(
        id = "CLM-007",
        policyholderName = "Arjun Reddy",
        fraudRiskScore = 78,
        claimAmount = 890000.00,
        claimType = "Auto Collision",
        dateSubmitted = "2026-08-28",
        status = "Under Investigation",
        description = "Luxury car rear-ended on empty road at midnight. Third claim in 12 months. Repair estimates vary significantly.",
        policyNumber = "POL-AC-67443",
        riskFactors = listOf("Multiple claims history", "Inconsistent repair estimates", "Late night incident", "Luxury vehicle")
    ),
    Claim(
        id = "CLM-008",
        policyholderName = "Meera Nair",
        fraudRiskScore = 8,
        claimAmount = 45000.00,
        claimType = "Travel - Baggage Loss",
        dateSubmitted = "2026-08-25",
        status = "Approved",
        description = "Baggage lost during international transit. Airline confirmation received. Claim within policy limits.",
        policyNumber = "POL-TR-21098",
        riskFactors = listOf("None identified")
    ),
    Claim(
        id = "CLM-009",
        policyholderName = "Deepak Joshi",
        fraudRiskScore = 73,
        claimAmount = 650000.00,
        claimType = "Property - Water Damage",
        dateSubmitted = "2026-08-20",
        status = "Under Review",
        description = "Claims burst pipe caused extensive water damage. Plumber report unavailable. Neighbor disputes timeline.",
        policyNumber = "POL-PR-89211",
        riskFactors = listOf("No plumber report", "Disputed timeline", "Above-average claim amount")
    ),
    Claim(
        id = "CLM-010",
        policyholderName = "Lakshmi Venkatesh",
        fraudRiskScore = 34,
        claimAmount = 120000.00,
        claimType = "Health - Diagnostic",
        dateSubmitted = "2026-08-18",
        status = "Approved",
        description = "Cardiac diagnostic tests at Narayana Health. Doctor referral verified. All receipts submitted.",
        policyNumber = "POL-HE-45609",
        riskFactors = listOf("Slightly above average cost")
    )
)
