package com.example.insurancefraudapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insurancefraudapp.ui.theme.InsuranceFraudAppTheme
import com.example.insurancefraudapp.ui.theme.RiskHighBase
import com.example.insurancefraudapp.ui.theme.RiskHighContainer
import com.example.insurancefraudapp.ui.theme.RiskHighText
import com.example.insurancefraudapp.ui.theme.RiskLowBase
import com.example.insurancefraudapp.ui.theme.RiskLowContainer
import com.example.insurancefraudapp.ui.theme.RiskLowText
import com.example.insurancefraudapp.ui.theme.RiskMediumBase
import com.example.insurancefraudapp.ui.theme.RiskMediumContainer
import com.example.insurancefraudapp.ui.theme.RiskMediumText
import com.example.insurancefraudapp.ui.theme.Secondary
import com.example.insurancefraudapp.ui.theme.SurfaceContainerHigh
import com.example.insurancefraudapp.ui.theme.SurfaceContainerLow
import com.example.insurancefraudapp.ui.theme.SurfaceContainerLowest

/**
 * Redesigned Dashboard screen matching Stitch "Vigilance Insurtech M3" design.
 * Displays operational summary, risk distribution, trend chart, and suspicious claims.
 */
@Composable
fun DashboardScreen(
    onClaimClick: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Custom Top Bar
        item {
            Spacer(modifier = Modifier.height(8.dp))
            DashboardTopBar(onSettingsClick = onSettingsClick)
        }

        // Greeting + ML Status
        item { GreetingSection() }

        // 2x2 Stat Grid
        item { OperationalSummaryGrid() }

        // Quick Action Chips
        item { QuickActionRow() }

        // Risk Distribution Card
        item { RiskDistributionCard() }

        // Claims & Anomaly Trend
        item { ClaimsTrendCard() }

        // Suspicious Claims Header
        item { SuspiciousClaimsHeader() }

        // Claim cards from mock data (only high/medium risk)
        val suspiciousClaims = mockClaims.filter { it.fraudRiskScore >= 40 }
            .sortedByDescending { it.fraudRiskScore }
        items(suspiciousClaims) { claim ->
            SuspiciousClaimCard(
                claim = claim,
                onClick = { onClaimClick(claim.id) }
            )
        }

        // Statutory Disclaimer
        item { StatutoryDisclaimer() }

        // Bottom spacing for nav bar
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // App Icon placeholder
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "App Icon",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Vigilance ML Risk",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFCFE5FF),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PRO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF104A75)
                        )
                    }
                }
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row {
            IconButton(onClick = { }) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text("3", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// ─── Greeting + ML Status ───────────────────────────────────────────────────

@Composable
private fun GreetingSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ML Model Status Badge
            Row(
                modifier = Modifier
                    .background(
                        color = SurfaceContainerHigh,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF002D0B))
                )
                Text(
                    text = "ML Model v4.2 Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Thursday, Oct 24, 2026",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "Good morning, Officer Aryan",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Forensic risk queue & automated anomaly detection matrix",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── 2×2 Stat Grid ──────────────────────────────────────────────────────────

@Composable
private fun OperationalSummaryGrid() {
    val totalClaims = mockClaims.size
    val flaggedClaims = mockClaims.count { it.fraudRiskScore >= 70 }
    val approvedClaims = mockClaims.count { it.status == "Approved" }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Group,
                iconTint = Secondary,
                value = "$totalClaims",
                label = "Total Customers",
                badge = "+3.2%",
                badgeColor = Color(0xFF99F89E).copy(alpha = 0.6f),
                badgeTextColor = Color(0xFF002D0B)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.VerifiedUser,
                iconTint = Secondary,
                value = "$approvedClaims",
                label = "Active Policies",
                badge = "GWP",
                badgeColor = Color(0xFFE6EFF8),
                badgeTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Assignment,
                iconTint = Secondary,
                value = "$totalClaims",
                label = "Total Claims",
                badge = "+${mockClaims.count { it.status == "Under Review" }} today",
                badgeColor = Color(0xFFCFE5FF),
                badgeTextColor = MaterialTheme.colorScheme.primary
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Policy,
                iconTint = RiskHighBase,
                value = "$flaggedClaims Cases",
                label = "Under Investigation",
                badge = "Action req.",
                badgeColor = RiskHighContainer,
                badgeTextColor = RiskHighText,
                valueColor = RiskHighBase
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color,
    valueColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(50))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = badgeTextColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = valueColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Quick Action Chips ─────────────────────────────────────────────────────

@Composable
private fun QuickActionRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Primary action
        Row(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(50)
                )
                .clickable { }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DocumentScanner,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Analyze Claim",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        // Secondary actions
        QuickChip(icon = Icons.Default.Add, label = "New Claim")
        QuickChip(icon = Icons.Default.Shield, label = "New Policy")
    }
}

@Composable
private fun QuickChip(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .background(SurfaceContainerLowest, RoundedCornerShape(50))
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─── Risk Distribution Card ─────────────────────────────────────────────────

@Composable
private fun RiskDistributionCard() {
    val highCount = mockClaims.count { it.fraudRiskScore >= 70 }
    val mediumCount = mockClaims.count { it.fraudRiskScore in 40..69 }
    val lowCount = mockClaims.count { it.fraudRiskScore < 40 }
    val total = mockClaims.size.toFloat()
    val highPct = if (total > 0) highCount / total else 0f
    val medPct = if (total > 0) mediumCount / total else 0f
    val lowPct = if (total > 0) lowCount / total else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Risk Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${mockClaims.size} Audited",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Segmented progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE6EFF8)),
                horizontalArrangement = Arrangement.Start
            ) {
                if (highPct > 0) {
                    Box(
                        modifier = Modifier
                            .weight(highPct.coerceAtLeast(0.01f))
                            .height(12.dp)
                            .background(RiskHighBase)
                    )
                }
                if (medPct > 0) {
                    Box(
                        modifier = Modifier
                            .weight(medPct.coerceAtLeast(0.01f))
                            .height(12.dp)
                            .background(RiskMediumBase)
                    )
                }
                if (lowPct > 0) {
                    Box(
                        modifier = Modifier
                            .weight(lowPct.coerceAtLeast(0.01f))
                            .height(12.dp)
                            .background(RiskLowBase)
                    )
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RiskLegendItem(
                    dotColor = RiskHighBase,
                    label = "High",
                    count = "$highCount",
                    detail = "${"%.1f".format(highPct * 100)}%",
                    containerColor = RiskHighContainer.copy(alpha = 0.4f),
                    textColor = RiskHighText
                )
                RiskLegendItem(
                    dotColor = RiskMediumBase,
                    label = "Medium",
                    count = "$mediumCount",
                    detail = "${"%.1f".format(medPct * 100)}%",
                    containerColor = RiskMediumContainer.copy(alpha = 0.4f),
                    textColor = RiskMediumText
                )
                RiskLegendItem(
                    dotColor = RiskLowBase,
                    label = "Low",
                    count = "$lowCount",
                    detail = "${"%.1f".format(lowPct * 100)}%",
                    containerColor = RiskLowContainer.copy(alpha = 0.4f),
                    textColor = RiskLowText
                )
            }
        }
    }
}

@Composable
private fun RiskLegendItem(
    dotColor: Color,
    label: String,
    count: String,
    detail: String,
    containerColor: Color,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = textColor
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            text = detail,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = textColor
        )
    }
}

// ─── Claims & Anomaly Trend Chart ───────────────────────────────────────────

@Composable
private fun ClaimsTrendCard() {
    // Mock monthly data: (totalHeight fraction, flaggedHeight fraction)
    val monthlyData = listOf(
        "May" to (0.57f to 0.11f),
        "Jun" to (0.71f to 0.14f),
        "Jul" to (0.64f to 0.11f),
        "Aug" to (0.86f to 0.21f),
        "Sep" to (0.79f to 0.18f),
        "Oct" to (1.0f to 0.25f)
    )
    val secondaryColor = Secondary
    val errorColor = RiskHighBase

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Claims & Anomaly Trend",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Last 6 Months ML Flagged Ratio",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LegendDot(color = secondaryColor, label = "Total")
                    LegendDot(color = errorColor, label = "Flagged")
                }
            }

            // Bar Chart
            val chartHeight = 112.dp
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
            ) {
                val barCount = monthlyData.size
                val gapFraction = 0.4f
                val totalBarWidth = size.width / barCount
                val barWidth = totalBarWidth * (1 - gapFraction)
                val gap = totalBarWidth * gapFraction

                monthlyData.forEachIndexed { index, (_, data) ->
                    val (totalFraction, flaggedFraction) = data
                    val x = index * totalBarWidth + gap / 2
                    val totalHeight = size.height * totalFraction
                    val flaggedHeight = size.height * flaggedFraction

                    // Total bar (lighter)
                    drawRoundRect(
                        color = secondaryColor.copy(alpha = 0.2f),
                        topLeft = Offset(x, size.height - totalHeight),
                        size = Size(barWidth, totalHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    // Flagged portion (on top)
                    drawRoundRect(
                        color = errorColor,
                        topLeft = Offset(x, size.height - flaggedHeight),
                        size = Size(barWidth, flaggedHeight),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }

            // Month labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                monthlyData.forEach { (month, _) ->
                    Text(
                        text = month,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (month == "Oct") MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (month == "Oct") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Average monthly triage latency: 4.2h",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Precision: 96.4%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Suspicious Claims Section ──────────────────────────────────────────────

@Composable
private fun SuspiciousClaimsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "Suspicious Claims",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Requires immediate manual verification",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "View all (${mockClaims.count { it.fraudRiskScore >= 40 }}) >",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Secondary
        )
    }
}

// ─── Redesigned Claim Card ──────────────────────────────────────────────────

@Composable
private fun SuspiciousClaimCard(
    claim: Claim,
    onClick: () -> Unit
) {
    val riskColor = when {
        claim.fraudRiskScore >= 70 -> RiskHighBase
        claim.fraudRiskScore >= 40 -> RiskMediumBase
        else -> RiskLowBase
    }
    val riskContainerColor = when {
        claim.fraudRiskScore >= 70 -> RiskHighContainer
        claim.fraudRiskScore >= 40 -> RiskMediumContainer
        else -> RiskLowContainer
    }
    val riskTextColor = when {
        claim.fraudRiskScore >= 70 -> RiskHighText
        claim.fraudRiskScore >= 40 -> RiskMediumText
        else -> RiskLowText
    }
    val riskIcon = when {
        claim.fraudRiskScore >= 70 -> Icons.Default.Warning
        claim.fraudRiskScore >= 40 -> Icons.Default.Report
        else -> Icons.Default.CheckCircle
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (claim.fraudRiskScore >= 70) 3.dp else 1.dp
        )
    ) {
        Column {
            // Red top accent strip for high risk
            if (claim.fraudRiskScore >= 70) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(RiskHighBase)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row: Claim ID + Date + Risk Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = claim.id,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = claim.dateSubmitted,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = claim.policyholderName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${claim.policyNumber} • ${claim.claimType}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // Risk Badge
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            modifier = Modifier
                                .background(riskContainerColor, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = riskIcon,
                                contentDescription = null,
                                tint = riskTextColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${claim.fraudRiskScore} / 100",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = riskTextColor
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Fraud Prob. ${claim.fraudRiskScore}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = riskColor
                        )
                    }
                }

                // Detail Meta Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CLAIMED VALUE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${"%,.0f".format(claim.claimAmount)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "DECISION STATE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(riskColor)
                            )
                            Text(
                                text = claim.status,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = riskColor
                            )
                        }
                    }
                }

                // Anomaly Tags
                if (claim.riskFactors.isNotEmpty() && claim.riskFactors.first() != "None identified") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        claim.riskFactors.take(3).forEach { factor ->
                            Text(
                                text = factor,
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE6EFF8),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Action Button (for high risk only)
                if (claim.fraudRiskScore >= 70) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(onClick = onClick)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyze Risk & Indicators",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Statutory Disclaimer ───────────────────────────────────────────────────

@Composable
private fun StatutoryDisclaimer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerHigh, RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Policy,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "Statutory ML Guidance: Machine Learning decision-support platform. " +
                    "The final verification and settlement decision belongs solely to the " +
                    "authorized insurance officer under IRDAI guidelines.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    InsuranceFraudAppTheme {
        DashboardScreen()
    }
}
