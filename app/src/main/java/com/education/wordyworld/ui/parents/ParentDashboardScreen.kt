package com.education.wordyworld.ui.parents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ParentDashboardScreen(
    streakCount: Int,
    modifier: Modifier = Modifier
) {
    var safeAdsEnabled by rememberSaveable { mutableStateOf(true) }
    var requirePin by rememberSaveable { mutableStateOf(true) }
    var weeklyReport by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Parent Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Daily streak: $streakCount days",
            style = MaterialTheme.typography.bodyMedium
        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ParentToggleRow(
                    title = "Family-safe ads only",
                    description = "All ads are reviewed and approved for kids.",
                    checked = safeAdsEnabled,
                    onCheckedChange = { safeAdsEnabled = it }
                )
                ParentToggleRow(
                    title = "Require parent PIN",
                    description = "Gate in-app purchases and settings with a PIN.",
                    checked = requirePin,
                    onCheckedChange = { requirePin = it }
                )
                ParentToggleRow(
                    title = "Email weekly progress",
                    description = "Send streaks and sticker updates to your inbox.",
                    checked = weeklyReport,
                    onCheckedChange = { weeklyReport = it }
                )
            }
        }
    }
}

@Composable
private fun ParentToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
