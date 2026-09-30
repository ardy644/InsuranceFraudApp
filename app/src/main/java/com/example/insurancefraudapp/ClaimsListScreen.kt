package com.example.insurancefraudapp

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insurancefraudapp.network.VigilanceApiClient
import com.example.insurancefraudapp.ui.theme.RiskHighBase
import com.example.insurancefraudapp.ui.theme.RiskHighContainer
import com.example.insurancefraudapp.ui.theme.RiskHighText
import com.example.insurancefraudapp.ui.theme.RiskLowBase
import com.example.insurancefraudapp.ui.theme.RiskLowContainer
import com.example.insurancefraudapp.ui.theme.RiskLowText
import com.example.insurancefraudapp.ui.theme.RiskMediumBase
import com.example.insurancefraudapp.ui.theme.RiskMediumContainer
import com.example.insurancefraudapp.ui.theme.RiskMediumText
import com.example.insurancefraudapp.ui.theme.SurfaceContainerLow
import com.example.insurancefraudapp.ui.theme.SurfaceContainerLowest
import kotlinx.coroutines.launch

/**
 * Live Claims list screen connected to Vigilance REST Backend.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimsListScreen(
    onClaimClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var claims by remember { mutableStateOf<List<Claim>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val scope = rememberCoroutineScope()

    val refreshData = {
        scope.launch {
            isLoading = true
            claims = VigilanceApiClient.fetchClaims()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    val filteredClaims = claims.filter { claim ->
        val matchesSearch = claim.policyholderName.contains(searchQuery, ignoreCase = true) ||
                claim.id.contains(searchQuery, ignoreCase = true) ||
                claim.policyNumber.contains(searchQuery, ignoreCase = true) ||
                claim.claimType.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "High Risk" -> claim.fraudRiskScore >= 70
            "Medium Risk" -> claim.fraudRiskScore in 40..69
            "Low Risk" -> claim.fraudRiskScore < 40
            "Approved" -> claim.status.equals("Approved", ignoreCase = true)
            "Under Review" -> claim.status.equals("Under Review", ignoreCase = true)
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Claims Audit Queue", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${filteredClaims.size} Claims Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Search by name, claim ID, or policy...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", "High Risk", "Medium Risk", "Low Risk", "Under Review", "Approved")
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            // Main Content Area
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (filteredClaims.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No claims match criteria",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredClaims, key = { it.id }) { claim ->
                        ClaimListItemCard(claim = claim, onClick = { onClaimClick(claim.id) })
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ClaimListItemCard(
    claim: Claim,
    onClick: () -> Unit
) {
    val riskColor = when {
        claim.fraudRiskScore >= 70 -> RiskHighBase
        claim.fraudRiskScore >= 40 -> RiskMediumBase
        else -> RiskLowBase
    }
    val riskContainer = when {
        claim.fraudRiskScore >= 70 -> RiskHighContainer
        claim.fraudRiskScore >= 40 -> RiskMediumContainer
        else -> RiskLowContainer
    }
    val riskText = when {
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
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = claim.id,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = claim.dateSubmitted,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Risk Badge
                Row(
                    modifier = Modifier
                        .background(riskContainer, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = riskIcon,
                        contentDescription = null,
                        tint = riskText,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${claim.fraudRiskScore}% Risk",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = riskText
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = claim.policyholderName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${claim.policyNumber} • ${claim.claimType}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${"%,.0f".format(claim.claimAmount)}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = claim.status,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = riskColor
                )
            }
        }
    }
}
