package com.education.wordyworld.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.education.wordyworld.model.Sticker
import com.education.wordyworld.ui.puzzle.PuzzleUiState

@Composable
fun HomeScreen(
    state: PuzzleUiState,
    stickers: List<Sticker>,
    onStartPuzzle: () -> Unit,
    onOpenRewards: () -> Unit,
    onOpenStickerAlbum: () -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val unlockedStickers = stickers.count { it.id in state.unlockedStickerIds }
    val mascotMessage = if (state.completedToday) {
        "You finished today's challenge! Try a new puzzle or decorate your album."
    } else {
        "Let's solve today's puzzle together!"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MascotWelcomeCard(message = mascotMessage, streakCount = state.streakCount)
        DailyPuzzleCard(state = state, onStartPuzzle = onStartPuzzle)
        ProgressOverviewRow(
            solved = state.solvedCount,
            total = state.totalPuzzles,
            unlockedStickers = unlockedStickers,
            streakCount = state.streakCount
        )
        QuickActionsRow(
            onOpenRewards = onOpenRewards,
            onOpenStickerAlbum = onOpenStickerAlbum,
            onOpenShop = onOpenShop
        )
    }
}

@Composable
private fun MascotWelcomeCard(message: String, streakCount: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🦊",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Hi, I'm Faye the Fox!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Daily streak: $streakCount",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DailyPuzzleCard(state: PuzzleUiState, onStartPuzzle: () -> Unit) {
    val puzzle = state.currentPuzzle
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's Puzzle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = puzzle.theme,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${puzzle.title} (${puzzle.solution.length} letters)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Difficulty: ${puzzle.difficulty.name.lowercase().replaceFirstChar { it.titlecase() }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Button(
                onClick = onStartPuzzle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (state.isCurrentPuzzleSolved) "Play Again" else "Play Now")
            }
        }
    }
}

@Composable
private fun ProgressOverviewRow(
    solved: Int,
    total: Int,
    unlockedStickers: Int,
    streakCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Puzzles Solved",
            value = "$solved / $total",
            accent = "🧠"
        )
        StatCard(
            title = "Sticker Count",
            value = "$unlockedStickers",
            accent = "🌈"
        )
        StatCard(
            title = "Streak",
            value = "$streakCount",
            accent = "🔥"
        )
    }
}

@Composable
private fun StatCard(title: String, value: String, accent: String) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = accent, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onOpenRewards: () -> Unit,
    onOpenStickerAlbum: () -> Unit,
    onOpenShop: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Choose your next stop",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedButton(onClick = onOpenRewards, modifier = Modifier.fillMaxWidth()) {
            Text(text = "View Rewards & Milestones")
        }
        OutlinedButton(onClick = onOpenStickerAlbum, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Open Sticker Album")
        }
        OutlinedButton(onClick = onOpenShop, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Visit Friendly Shop")
        }
    }
}
