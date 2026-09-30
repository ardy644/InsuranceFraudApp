package com.example.insurancefraudapp.network

import com.example.insurancefraudapp.Claim
import com.example.insurancefraudapp.mockClaims
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Data model for Operational Dashboard statistics.
 */
data class DashboardStats(
    val totalCustomers: Int,
    val activePolicies: Int,
    val totalClaims: Int,
    val underInvestigationCases: Int,
    val underReviewToday: Int,
    val highRiskCount: Int,
    val highRiskPercent: Float,
    val mediumRiskCount: Int,
    val mediumRiskPercent: Float,
    val lowRiskCount: Int,
    val lowRiskPercent: Float
)

/**
 * Data model for Customer management.
 */
data class Customer(
    val id: String,
    val name: String,
    val email: String,
    val policyNumber: String,
    val policyType: String,
    val riskTier: String,
    val claimsCount: Int,
    val totalClaimed: Double,
    val joinDate: String
)

/**
 * Data model for Forensic Analytics.
 */
data class AnalyticsSummary(
    val totalAudited: Int,
    val flaggedCases: Int,
    val underInvestigation: Int,
    val precisionScore: Double,
    val averageTriageLatencyHours: Double,
    val totalClaimedAmount: Double,
    val fraudPreventionSavingsEstimate: Double,
    val claimsByType: Map<String, Int>
)

/**
 * Lightweight, zero-dependency Network Client for Vigilance Backend.
 * Uses Android's built-in HttpURLConnection and org.json with coroutines.
 * Incorporates seamless offline fallbacks to mock data.
 */
object VigilanceApiClient {
    // 10.0.2.2 is Android Emulator loopback to host; 127.0.0.1 is local desktop/unit-test.
    private const val DEFAULT_HOST = "10.0.2.2:8080"
    var baseUrl: String = "http://$DEFAULT_HOST/api"

    suspend fun checkHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/health")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 2000
            conn.readTimeout = 2000
            conn.requestMethod = "GET"
            conn.responseCode == 200
        } catch (_: Exception) {
            false
        }
    }

    suspend fun fetchClaims(suspiciousOnly: Boolean = false): List<Claim> = withContext(Dispatchers.IO) {
        val endpoint = if (suspiciousOnly) "$baseUrl/claims?filter=suspicious" else "$baseUrl/claims"
        try {
            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2500
                readTimeout = 2500
                requestMethod = "GET"
            }

            if (conn.responseCode == 200) {
                val raw = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(raw)
                val list = mutableListOf<Claim>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val factorsArray = obj.optJSONArray("riskFactors") ?: JSONArray()
                    val factors = (0 until factorsArray.length()).map { factorsArray.getString(it) }
                    list.add(
                        Claim(
                            id = obj.getString("id"),
                            policyholderName = obj.getString("policyholderName"),
                            fraudRiskScore = obj.getInt("fraudRiskScore"),
                            claimAmount = obj.getDouble("claimAmount"),
                            claimType = obj.getString("claimType"),
                            dateSubmitted = obj.getString("dateSubmitted"),
                            status = obj.getString("status"),
                            description = obj.optString("description", ""),
                            policyNumber = obj.getString("policyNumber"),
                            riskFactors = factors
                        )
                    )
                }
                list
            } else {
                fallbackClaims(suspiciousOnly)
            }
        } catch (_: Exception) {
            fallbackClaims(suspiciousOnly)
        }
    }

    suspend fun fetchClaimById(claimId: String): Claim? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/claims/$claimId")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2500
                readTimeout = 2500
                requestMethod = "GET"
            }
            if (conn.responseCode == 200) {
                val raw = conn.inputStream.bufferedReader().use { it.readText() }
                val obj = JSONObject(raw)
                val factorsArray = obj.optJSONArray("riskFactors") ?: JSONArray()
                val factors = (0 until factorsArray.length()).map { factorsArray.getString(it) }
                Claim(
                    id = obj.getString("id"),
                    policyholderName = obj.getString("policyholderName"),
                    fraudRiskScore = obj.getInt("fraudRiskScore"),
                    claimAmount = obj.getDouble("claimAmount"),
                    claimType = obj.getString("claimType"),
                    dateSubmitted = obj.getString("dateSubmitted"),
                    status = obj.getString("status"),
                    description = obj.optString("description", ""),
                    policyNumber = obj.getString("policyNumber"),
                    riskFactors = factors
                )
            } else {
                mockClaims.find { it.id == claimId }
            }
        } catch (_: Exception) {
            mockClaims.find { it.id == claimId }
        }
    }

    suspend fun fetchDashboardStats(): DashboardStats = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/dashboard/stats")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2500
                readTimeout = 2500
                requestMethod = "GET"
            }

            if (conn.responseCode == 200) {
                val raw = conn.inputStream.bufferedReader().use { it.readText() }
                val obj = JSONObject(raw)
                val dist = obj.getJSONObject("riskDistribution")
                val high = dist.getJSONObject("high")
                val med = dist.getJSONObject("medium")
                val low = dist.getJSONObject("low")

                DashboardStats(
                    totalCustomers = obj.optInt("totalCustomers", 8),
                    activePolicies = obj.optInt("activePolicies", 8),
                    totalClaims = obj.optInt("totalClaims", 10),
                    underInvestigationCases = obj.optInt("underInvestigationCases", 3),
                    underReviewToday = obj.optInt("underReviewToday", 3),
                    highRiskCount = high.optInt("count", 4),
                    highRiskPercent = high.optDouble("percent", 40.0).toFloat(),
                    mediumRiskCount = med.optInt("count", 2),
                    mediumRiskPercent = med.optDouble("percent", 20.0).toFloat(),
                    lowRiskCount = low.optInt("count", 4),
                    lowRiskPercent = low.optDouble("percent", 40.0).toFloat()
                )
            } else {
                fallbackDashboardStats()
            }
        } catch (_: Exception) {
            fallbackDashboardStats()
        }
    }

    suspend fun updateClaimStatus(claimId: String, newStatus: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/claims/$claimId")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2500
                readTimeout = 2500
                requestMethod = "PATCH"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }
            val payload = JSONObject().put("status", newStatus).toString()
            conn.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
            conn.responseCode == 200
        } catch (_: Exception) {
            false
        }
    }

    suspend fun fetchCustomers(): List<Customer> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/customers")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2500
                readTimeout = 2500
                requestMethod = "GET"
            }
            if (conn.responseCode == 200) {
                val raw = conn.inputStream.bufferedReader().use { it.readText() }
                val array = JSONArray(raw)
                val list = mutableListOf<Customer>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Customer(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            email = obj.getString("email"),
                            policyNumber = obj.getString("policyNumber"),
                            policyType = obj.getString("policyType"),
                            riskTier = obj.getString("riskTier"),
                            claimsCount = obj.optInt("claimsCount", 0),
                            totalClaimed = obj.optDouble("totalClaimed", 0.0),
                            joinDate = obj.optString("joinDate", "")
                        )
                    )
                }
                list
            } else {
                fallbackCustomers()
            }
        } catch (_: Exception) {
            fallbackCustomers()
        }
    }

    suspend fun fetchAnalytics(): AnalyticsSummary = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/analytics")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2500
                readTimeout = 2500
                requestMethod = "GET"
            }
            if (conn.responseCode == 200) {
                val raw = conn.inputStream.bufferedReader().use { it.readText() }
                val obj = JSONObject(raw)
                val byTypeObj = obj.optJSONObject("claimsByType") ?: JSONObject()
                val byType = mutableMapOf<String, Int>()
                val keys = byTypeObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    byType[k] = byTypeObj.getInt(k)
                }

                AnalyticsSummary(
                    totalAudited = obj.optInt("totalAudited", 10),
                    flaggedCases = obj.optInt("flaggedCases", 4),
                    underInvestigation = obj.optInt("underInvestigation", 2),
                    precisionScore = obj.optDouble("precisionScore", 96.4),
                    averageTriageLatencyHours = obj.optDouble("averageTriageLatencyHours", 4.2),
                    totalClaimedAmount = obj.optDouble("totalClaimedAmount", 6152000.0),
                    fraudPreventionSavingsEstimate = obj.optDouble("fraudPreventionSavingsEstimate", 4490000.0),
                    claimsByType = byType
                )
            } else {
                fallbackAnalytics()
            }
        } catch (_: Exception) {
            fallbackAnalytics()
        }
    }

    private fun fallbackClaims(suspiciousOnly: Boolean): List<Claim> {
        return if (suspiciousOnly) {
            mockClaims.filter { it.fraudRiskScore >= 40 }.sortedByDescending { it.fraudRiskScore }
        } else {
            mockClaims
        }
    }

    private fun fallbackDashboardStats(): DashboardStats {
        val total = mockClaims.size
        val high = mockClaims.count { it.fraudRiskScore >= 70 }
        val med = mockClaims.count { it.fraudRiskScore in 40..69 }
        val low = mockClaims.count { it.fraudRiskScore < 40 }
        val approved = mockClaims.count { it.status == "Approved" }
        val underReview = mockClaims.count { it.status == "Under Review" }
        val underInvestigation = mockClaims.count { it.status == "Under Investigation" || it.status == "Flagged" }

        return DashboardStats(
            totalCustomers = 8,
            activePolicies = approved + 4,
            totalClaims = total,
            underInvestigationCases = underInvestigation,
            underReviewToday = underReview,
            highRiskCount = high,
            highRiskPercent = (high.toFloat() / total) * 100,
            mediumRiskCount = med,
            mediumRiskPercent = (med.toFloat() / total) * 100,
            lowRiskCount = low,
            lowRiskPercent = (low.toFloat() / total) * 100
        )
    }

    private fun fallbackCustomers(): List<Customer> = listOf(
        Customer("CUST-001", "Rajesh Kumar", "rajesh.kumar@example.com", "POL-AC-29481", "Auto Comprehensive", "High Risk", 2, 450000.0, "2025-01-10"),
        Customer("CUST-002", "Priya Sharma", "priya.s@example.com", "POL-HE-10234", "Health Prime", "Low Risk", 1, 85000.0, "2023-04-15"),
        Customer("CUST-003", "Mohammed Iqbal", "m.iqbal@example.com", "POL-PR-44821", "Commercial Property", "Medium Risk", 1, 1200000.0, "2024-09-01"),
        Customer("CUST-004", "Anita Desai", "anita.desai@example.com", "POL-AC-55102", "Two Wheeler Standard", "Low Risk", 1, 32000.0, "2022-11-20"),
        Customer("CUST-005", "Vikram Singh", "vikram.singh@example.com", "POL-LI-78934", "Term Life Shield", "Severe Risk", 1, 2500000.0, "2026-09-01"),
        Customer("CUST-006", "Sunita Patel", "sunita.p@example.com", "POL-HE-33210", "Family Health Silver", "Medium Risk", 1, 175000.0, "2024-02-14"),
        Customer("CUST-007", "Arjun Reddy", "arjun.r@example.com", "POL-AC-67443", "Luxury Auto Gold", "High Risk", 3, 890000.0, "2023-07-22"),
        Customer("CUST-008", "Meera Nair", "meera.nair@example.com", "POL-TR-21098", "Worldwide Travel", "Low Risk", 1, 45000.0, "2025-05-30")
    )

    private fun fallbackAnalytics(): AnalyticsSummary = AnalyticsSummary(
        totalAudited = 10,
        flaggedCases = 4,
        underInvestigation = 2,
        precisionScore = 96.4,
        averageTriageLatencyHours = 4.2,
        totalClaimedAmount = 6152000.0,
        fraudPreventionSavingsEstimate = 4490000.0,
        claimsByType = mapOf(
            "Auto Collision" to 2,
            "Health - Hospitalization" to 1,
            "Property Fire" to 1,
            "Auto - Theft" to 1,
            "Life - Accidental Death" to 1,
            "Health - Surgery" to 1,
            "Travel - Baggage Loss" to 1,
            "Property - Water Damage" to 1,
            "Health - Diagnostic" to 1
        )
    )
}
