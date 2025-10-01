package com.education.wordyworld.ui.puzzle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.education.wordyworld.model.Sticker

private val keyboardRows = listOf(
    "QWERTYUIOP",
    "ASDFGHJKL",
    "ZXCVBNM"
)

@Composable
fun PuzzleScreen(
    state: PuzzleUiState,
    stickers: List<Sticker>,
    onLetter: (Char) -> Unit,
    onSubmit: () -> Unit,
    onDelete: () -> Unit,
    onRevealHint: () -> Unit,
    onTogglePause: () -> Unit,
    onNextPuzzle: () -> Unit,
    onResetPuzzle: () -> Unit,
    onSharePuzzle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val gradient = remember(colorScheme) {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.primary.copy(alpha = 0.12f),
                colorScheme.surface
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PuzzleTopToolbar(
                title = state.currentPuzzle.title,
                progressLabel = "${state.usedGuesses}/${state.currentPuzzle.allowedGuesses}",
                onShare = onSharePuzzle,
                onSettings = onTogglePause
            )
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    PuzzleGrid(state = state)
                    KeyboardPanel(
                        state = state,
                        onLetter = onLetter,
                        onSubmit = onSubmit,
                        onDelete = onDelete
                    )
                    BottomGameBar(
                        state = state,
                        onRevealHint = onRevealHint,
                        onReset = onResetPuzzle
                    )
                }
            }
            AnimatedVisibility(
                visible = state.feedbackMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.feedbackMessage?.let { message ->
                    ElevatedSuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = message,
                                color = if (state.isFeedbackPositive) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.error
                                }
                            )
                        }
                    )
                }
            }
        }

        CelebrationOverlay(
            modifier = Modifier.fillMaxSize(),
            state = state,
            stickers = stickers,
            onNextPuzzle = onNextPuzzle,
            onReplay = onResetPuzzle
        )
    }
}

@Composable
private fun PuzzleTopToolbar(
    title: String,
    progressLabel: String,
    onShare: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Daily Word Puzzle",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .padding(end = 12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = progressLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = onShare) {
            Icon(imageVector = Icons.Filled.Send, contentDescription = "Share puzzle")
        }
        IconButton(onClick = onSettings) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Game settings")
        }
    }
}

@Composable
private fun PuzzleGrid(state: PuzzleUiState) {
    val tileSize = 50.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(5f / 6f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.guessRows.forEachIndexed { rowIndex, guessRow ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                guessRow.letters.forEachIndexed { columnIndex, letter ->
                    val feedback = guessRow.feedback.getOrNull(columnIndex) ?: LetterFeedback.Idle
                    val isActiveRow = !guessRow.isSubmitted && rowIndex == state.currentRowIndex
                    val tileColor = tileColorFor(feedback, isActiveRow)
                    val borderColor = tileBorderColorFor(feedback, isActiveRow)
                    val textColor = if (feedback == LetterFeedback.Correct || feedback == LetterFeedback.Present) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }

                    Box(
                        modifier = Modifier
                            .size(tileSize)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tileColor)
                            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter?.toString() ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun tileColorFor(feedback: LetterFeedback, isActive: Boolean): Color {
    val scheme = MaterialTheme.colorScheme
    return when (feedback) {
        LetterFeedback.Correct -> scheme.primary
        LetterFeedback.Present -> scheme.tertiary
        LetterFeedback.Absent -> scheme.surfaceVariant
        LetterFeedback.Idle -> if (isActive) scheme.secondaryContainer else scheme.surface
    }
}

@Composable
private fun tileBorderColorFor(feedback: LetterFeedback, isActive: Boolean): Color {
    val scheme = MaterialTheme.colorScheme
    return when (feedback) {
        LetterFeedback.Correct -> scheme.onPrimary
        LetterFeedback.Present -> scheme.onTertiary
        LetterFeedback.Absent -> scheme.outline
        LetterFeedback.Idle -> if (isActive) scheme.secondary else scheme.outline.copy(alpha = 0.4f)
    }
}

@Composable
private fun KeyboardPanel(
    state: PuzzleUiState,
    onLetter: (Char) -> Unit,
    onSubmit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keyboardRows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (index == keyboardRows.lastIndex) {
                    KeyboardSpecialKey(label = "ENTER", enabled = !state.isInteractionLocked, onClick = onSubmit)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                row.forEach { letter ->
                    val feedback = state.keyboardState[letter] ?: LetterFeedback.Idle
                    KeyboardKey(
                        letter = letter,
                        feedback = feedback,
                        enabled = !state.isInteractionLocked,
                        onClick = { onLetter(letter) }
                    )
                }
                if (index == keyboardRows.lastIndex) {
                    Spacer(modifier = Modifier.width(6.dp))
                    KeyboardSpecialKey(label = "⌫", enabled = state.currentInput.isNotEmpty(), onClick = onDelete)
                }
            }
        }
    }
}

@Composable
private fun KeyboardKey(letter: Char, feedback: LetterFeedback, enabled: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val container = when (feedback) {
        LetterFeedback.Correct -> scheme.primary
        LetterFeedback.Present -> scheme.tertiary
        LetterFeedback.Absent -> scheme.surfaceVariant
        LetterFeedback.Idle -> scheme.surface
    }
    val content = when (feedback) {
        LetterFeedback.Correct -> scheme.onPrimary
        LetterFeedback.Present -> scheme.onTertiary
        else -> scheme.onSurface
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container.copy(alpha = 0.6f),
            disabledContentColor = content.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .height(48.dp)
            .width(44.dp)
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun KeyboardSpecialKey(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .height(48.dp)
            .width(72.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BottomGameBar(
    state: PuzzleUiState,
    onRevealHint: () -> Unit,
    onReset: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = (state.usedGuesses.toFloat() / state.currentPuzzle.allowedGuesses).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Theme: ${state.currentPuzzle.theme}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Time: ${state.elapsedSeconds}s • Score: ${state.score}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = onRevealHint) {
                    Icon(imageVector = Icons.Filled.Help, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (state.hintRevealed) "Hint Shown" else "Hint")
                }
                OutlinedButton(onClick = onReset) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun CelebrationOverlay(
    modifier: Modifier,
    state: PuzzleUiState,
    stickers: List<Sticker>,
    onNextPuzzle: () -> Unit,
    onReplay: () -> Unit
) {
    val showOverlay = state.isCurrentPuzzleSolved || state.isOutOfGuesses
    AnimatedVisibility(
        modifier = modifier,
        visible = showOverlay,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val sticker = remember(state.currentPuzzle.rewardStickerId, stickers) {
            stickers.firstOrNull { it.id == state.currentPuzzle.rewardStickerId }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            AlertDialog(
                onDismissRequest = onReplay,
                confirmButton = {
                    Button(onClick = onNextPuzzle, enabled = state.isCurrentPuzzleSolved) {
                        Icon(imageVector = Icons.Filled.Celebration, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Next Puzzle")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = onReplay) {
                        Text(text = "Play Again")
                    }
                },
                icon = {
                    Text(text = if (state.isCurrentPuzzleSolved) "🎉" else "💡", fontSize = 36.sp)
                },
                title = {
                    Text(
                        text = if (state.isCurrentPuzzleSolved) "Amazing!" else "Nice Try",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (state.isCurrentPuzzleSolved) {
                                "You solved ${state.currentPuzzle.targetWord.uppercase()} in ${state.usedGuesses} guesses!"
                            } else {
                                "The secret word was ${state.currentPuzzle.targetWord.uppercase()}."
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (sticker != null && state.isCurrentPuzzleSolved) {
                            RewardStickerHighlight(sticker = sticker)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun RewardStickerHighlight(sticker: Sticker) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = sticker.emoji, fontSize = 28.sp)
                }
            }
            Column {
                Text(
                    text = "Sticker unlocked!",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = sticker.name, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
