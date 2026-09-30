package com.example.insurancefraudapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.insurancefraudapp.network.VigilanceApiClient
import com.example.insurancefraudapp.ui.theme.InsuranceFraudAppTheme
import kotlinx.coroutines.launch

/**
 * Deep-dive screen for a single claim, showing full details,
 * risk factor chips, and placeholder ML risk visualizations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimDetailsScreen(
    claimId: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var claim by remember { mutableStateOf(mockClaims.find { it.id == claimId }) }
    var isUpdating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(claimId) {
        val remote = VigilanceApiClient.fetchClaimById(claimId)
        if (remote != null) {
            claim = remote
        }
    }

    val updateStatus: (String) -> Unit = { newStatus ->
        scope.launch {
            isUpdating = true
            val success = VigilanceApiClient.updateClaimStatus(claimId, newStatus)
            if (success) {
                claim = claim?.copy(status = newStatus)
            }
            isUpdating = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(claim?.id ?: "Claim Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (claim == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Claim not found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            val currentClaim = claim!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Policyholder header
                PolicyholderHeader(currentClaim)

                HorizontalDivider()

                // Claim info
                ClaimInfoSection(currentClaim)

                HorizontalDivider()

                // ML Risk Score Chart (placeholder)
                RiskScoreGauge(currentClaim)

                // Risk Factor Breakdown (placeholder bar chart)
                RiskFactorBreakdownChart(currentClaim)

                // Risk factors chips
                RiskFactorsSection(currentClaim)

                // Description
                DescriptionSection(currentClaim)

                // Decision Action Buttons
                DecisionActionsSection(
                    currentStatus = currentClaim.status,
                    isUpdating = isUpdating,
                    onUpdateStatus = updateStatus
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DecisionActionsSection(
    currentStatus: String,
    isUpdating: Boolean,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Officer Decision & Triage Actions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Current Status: $currentStatus",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onUpdateStatus("Approved") },
                    enabled = !isUpdating && currentStatus != "Approved",
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Approve")
                }
                Button(
                    onClick = { onUpdateStatus("Under Investigation") },
                    enabled = !isUpdating && currentStatus != "Under Investigation",
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text("Investigate")
                }
                Button(
                    onClick = { onUpdateStatus("Flagged") },
                    enabled = !isUpdating && currentStatus != "Flagged",
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Flag")
                }
            }
        }
    }
}

@Composable
private fun PolicyholderHeader(claim: Claim) {
    val riskColor = when {
        claim.fraudRiskScore >= 70 -> MaterialTheme.colorScheme.error
        claim.fraudRiskScore >= 40 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = claim.policyholderName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Policy: ${claim.policyNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = riskColor.copy(alpha = 0.15f))
        ) {
            Text(
                text = "${claim.fraudRiskScore}% Risk",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = riskColor
            )
        }
    }
}

@Composable
private fun ClaimInfoSection(claim: Claim) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Claim Information",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            InfoRow("Claim ID", claim.id)
            InfoRow("Type", claim.claimType)
            InfoRow("Amount", "₹${"%,.0f".format(claim.claimAmount)}")
            InfoRow("Date Submitted", claim.dateSubmitted)
            InfoRow("Status", claim.status)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Placeholder donut/gauge chart for the ML fraud risk score.
 * Renders a partial arc proportional to the risk score percentage.
 */
@Composable
private fun RiskScoreGauge(claim: Claim) {
    val riskColor = when {
        claim.fraudRiskScore >= 70 -> MaterialTheme.colorScheme.error
        claim.fraudRiskScore >= 40 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    val trackColor = riskColor.copy(alpha = 0.15f)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ML Fraud Risk Score",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    val strokeWidth = 20.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Track
                    drawArc(
                        color = trackColor,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Score arc
                    drawArc(
                        color = riskColor,
                        startAngle = 135f,
                        sweepAngle = 270f * (claim.fraudRiskScore / 100f),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${claim.fraudRiskScore}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = riskColor
                    )
                    Text(
                        text = when {
                            claim.fraudRiskScore >= 70 -> "High Risk"
                            claim.fraudRiskScore >= 40 -> "Medium Risk"
                            else -> "Low Risk"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Placeholder horizontal bar chart showing simulated ML model
 * confidence scores across multiple risk categories.
 */
@Composable
private fun RiskFactorBreakdownChart(claim: Claim) {
    // Simulated ML model output scores derived from overall risk
    val baseScore = claim.fraudRiskScore
    val categories = listOf(
        "Document Anomaly" to (baseScore * 0.9f).coerceIn(0f, 100f),
        "Behavioral Pattern" to (baseScore * 1.1f).coerceIn(0f, 100f),
        "Claim History" to (baseScore * 0.7f).coerceIn(0f, 100f),
        "Financial Signal" to (baseScore * 0.85f).coerceIn(0f, 100f),
        "Network Analysis" to (baseScore * 0.6f).coerceIn(0f, 100f)
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Risk Factor Breakdown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            categories.forEach { (label, score) ->
                val barColor = when {
                    score >= 70f -> MaterialTheme.colorScheme.error
                    score >= 40f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${"%.0f".format(score)}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { score / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = barColor,
                        trackColor = barColor.copy(alpha = 0.15f),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RiskFactorsSection(claim: Claim) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Identified Risk Factors",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                claim.riskFactors.forEach { factor ->
                    AssistChip(
                        onClick = { },
                        label = { Text(factor, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DescriptionSection(claim: Claim) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Investigation Notes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = claim.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClaimDetailsScreenPreview() {
    InsuranceFraudAppTheme {
        ClaimDetailsScreen(claimId = "CLM-005")
    }
}
