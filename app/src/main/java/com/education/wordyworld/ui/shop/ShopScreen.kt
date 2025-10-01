package com.education.wordyworld.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.education.wordyworld.model.ShopItem
import com.education.wordyworld.model.ShopItemType

@Composable
fun ShopScreen(
    items: List<ShopItem>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Friendly Shop",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Parents can unlock ad-free play, extra hints, and sticker surprises.",
            style = MaterialTheme.typography.bodyMedium
        )
        items.forEach { item ->
            ShopItemCard(item = item)
        }
    }
}

@Composable
private fun ShopItemCard(item: ShopItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = when (item.type) {
                    ShopItemType.HINT_PACK -> "Includes extra hints for tricky puzzles."
                    ShopItemType.STICKER_PACK -> "Unlocks bonus stickers for the album."
                    ShopItemType.AD_FREE -> "Removes ads after parent approval."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Button(onClick = { /* Placeholder for billing flow */ }, enabled = false) {
                Text(text = "${item.price} — Coming Soon")
            }
        }
    }
}
