package com.education.wordyworld.ui.stickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.education.wordyworld.model.Sticker

@Composable
fun StickerAlbumScreen(
    stickers: List<Sticker>,
    unlockedStickerIds: Set<Int>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(stickers) { sticker ->
            val isUnlocked = sticker.id in unlockedStickerIds
            StickerCard(sticker = sticker, unlocked = isUnlocked)
        }
    }
}

@Composable
private fun StickerCard(sticker: Sticker, unlocked: Boolean) {
    val backgroundColor = if (unlocked) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = sticker.emoji,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.alpha(if (unlocked) 1f else 0.3f)
            )
            Text(
                text = sticker.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(if (unlocked) 1f else 0.5f)
            )
            Text(
                text = if (unlocked) sticker.description else "Solve more puzzles to unlock!",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(if (unlocked) 1f else 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
