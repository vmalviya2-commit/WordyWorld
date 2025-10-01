package com.education.wordyworld.ui.puzzle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@Composable
fun PuzzleScreen(
    state: PuzzleUiState,
    stickers: List<Sticker>,
    onSelectLetter: (Int) -> Unit,
    onRemoveLastLetter: () -> Unit,
    onClearGuess: () -> Unit,
    onSubmitGuess: () -> Unit,
    onShowHint: () -> Unit,
    onShuffle: () -> Unit,
    onNextPuzzle: () -> Unit,
    onResetPuzzle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val currentPuzzle = state.currentPuzzle
    val rewardSticker = stickers.firstOrNull { it.id == currentPuzzle.rewardStickerId }
    val guessCharacters = state.currentGuess.padEnd(currentPuzzle.solution.length, ' ')

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = currentPuzzle.theme,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = currentPuzzle.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        GuessPreviewRow(guessCharacters)
        LettersGrid(state = state, onSelectLetter = onSelectLetter)
        ActionsRow(
            onRemoveLastLetter = onRemoveLastLetter,
            onClearGuess = onClearGuess,
            onShuffle = onShuffle,
            onShowHint = onShowHint,
            isLocked = state.isCelebrationLocked
        )
        SubmitSection(
            isEnabled = state.currentGuess.length == currentPuzzle.solution.length && !state.isCelebrationLocked,
            onSubmit = onSubmitGuess
        )
        AnimatedVisibility(visible = state.isHintVisible, enter = fadeIn(), exit = fadeOut()) {
            HintCard(hint = currentPuzzle.hint)
        }
        state.feedbackMessage?.let { message ->
            FeedbackCard(
                message = message,
                isPositive = state.isFeedbackPositive
            )
        }
        AnimatedVisibility(visible = state.isCelebrationLocked, enter = fadeIn(), exit = fadeOut()) {
            CelebrationCard(
                rewardSticker = rewardSticker,
                onNextPuzzle = onNextPuzzle,
                onReplay = onResetPuzzle,
                hasNextPuzzle = state.solvedCount < state.totalPuzzles
            )
        }
    }
}

@Composable
private fun GuessPreviewRow(guessCharacters: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        guessCharacters.forEach { char ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (char == ' ') "?" else char.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LettersGrid(state: PuzzleUiState, onSelectLetter: (Int) -> Unit) {
    val columns = 4
    val letters = state.displayLetters
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        letters.chunked(columns).forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEachIndexed { columnIndex, character ->
                    val index = rowIndex * columns + columnIndex
                    LetterTile(
                        character = character,
                        enabled = index < letters.size && index !in state.usedLetterIndices && !state.isCelebrationLocked,
                        onClick = { onSelectLetter(index) }
                    )
                }
                if (row.size < columns) {
                    repeat(columns - row.size) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterTile(character: Char, enabled: Boolean, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
    ) {
        Text(
            text = character.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ActionsRow(
    onRemoveLastLetter: () -> Unit,
    onClearGuess: () -> Unit,
    onShuffle: () -> Unit,
    onShowHint: () -> Unit,
    isLocked: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(onClick = onRemoveLastLetter, enabled = !isLocked) {
            Icon(imageVector = Icons.Filled.Undo, contentDescription = "Undo letter")
        }
        IconButton(onClick = onClearGuess, enabled = !isLocked) {
            Icon(imageVector = Icons.Filled.Backspace, contentDescription = "Clear guess")
        }
        IconButton(onClick = onShuffle, enabled = !isLocked) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Shuffle letters")
        }
        OutlinedButton(onClick = onShowHint, enabled = !isLocked) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Hint")
        }
    }
}

@Composable
private fun SubmitSection(isEnabled: Boolean, onSubmit: () -> Unit) {
    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled
    ) {
        Text(text = "Check My Word")
    }
}

@Composable
private fun HintCard(hint: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Hint",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = hint, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun FeedbackCard(message: String, isPositive: Boolean) {
    val background = if (isPositive) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val textColor = if (isPositive) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Text(
            text = message,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CelebrationCard(
    rewardSticker: Sticker?,
    onNextPuzzle: () -> Unit,
    onReplay: () -> Unit,
    hasNextPuzzle: Boolean
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Celebration,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "You did it!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            rewardSticker?.let { sticker ->
                Text(
                    text = "Sticker earned: ${sticker.emoji} ${sticker.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onReplay) {
                    Text(text = "Play Again")
                }
                if (hasNextPuzzle) {
                    Button(onClick = onNextPuzzle) {
                        Text(text = "Next Puzzle")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}
