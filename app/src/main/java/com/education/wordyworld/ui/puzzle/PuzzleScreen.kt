package com.education.wordyworld.ui.puzzle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.education.wordyworld.model.Sticker
import kotlin.math.abs
import kotlin.math.min

@Composable
fun PuzzleScreen(
    state: PuzzleUiState,
    stickers: List<Sticker>,
    onStartSelection: (GridPosition) -> Unit,
    onContinueSelection: (GridPosition) -> Unit,
    onEndSelection: () -> Unit,
    onRevealHint: () -> Unit,
    onTogglePause: () -> Unit,
    onNextPuzzle: () -> Unit,
    onResetPuzzle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val rewardSticker = remember(state.currentPuzzle.rewardStickerId, stickers) {
        stickers.firstOrNull { it.id == state.currentPuzzle.rewardStickerId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PuzzleHeader(state = state)
        AnimatedVisibility(visible = state.activeWord.isNotEmpty()) {
            ActiveWordPreview(word = state.activeWord)
        }
        PuzzleBoard(
            state = state,
            onStartSelection = onStartSelection,
            onContinueSelection = onContinueSelection,
            onEndSelection = onEndSelection
        )
        AnimatedVisibility(visible = state.feedbackMessage != null, enter = fadeIn(), exit = fadeOut()) {
            FeedbackMessage(message = state.feedbackMessage, isPositive = state.isFeedbackPositive)
        }
        BottomControlBar(
            isPaused = state.isPaused,
            onTogglePause = onTogglePause,
            onRevealHint = onRevealHint,
            onNextPuzzle = onNextPuzzle,
            remainingWords = state.remainingWords
        )
        AnimatedVisibility(visible = state.isCurrentPuzzleSolved, enter = fadeIn(), exit = fadeOut()) {
            CelebrationCard(
                sticker = rewardSticker,
                onNextPuzzle = onNextPuzzle,
                onReplay = onResetPuzzle,
                hasNextPuzzle = state.solvedCount < state.totalPuzzles
            )
        }
    }
}

@Composable
private fun PuzzleHeader(state: PuzzleUiState) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.currentPuzzle.theme,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = state.currentPuzzle.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${state.foundWords.size}/${state.currentPuzzle.words.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatChip(
                    label = "Timer",
                    value = state.elapsedSeconds.toClock(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Score",
                    value = state.score.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Words Left",
                    value = state.remainingWords.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ActiveWordPreview(word: String) {
    ElevatedSuggestionChip(onClick = {}, label = { Text(text = "Tracing: ${word.uppercase()}") })
}

@Composable
private fun PuzzleBoard(
    state: PuzzleUiState,
    onStartSelection: (GridPosition) -> Unit,
    onContinueSelection: (GridPosition) -> Unit,
    onEndSelection: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isWide = maxWidth > 600.dp
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.Top
            ) {
                WordSearchGrid(
                    state = state,
                    modifier = Modifier
                        .weight(1.6f)
                        .fillMaxHeight(),
                    onStartSelection = onStartSelection,
                    onContinueSelection = onContinueSelection,
                    onEndSelection = onEndSelection
                )
                WordListPanel(
                    words = state.currentPuzzle.words,
                    foundWords = state.foundWords,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WordSearchGrid(
                    state = state,
                    modifier = Modifier.fillMaxWidth(),
                    onStartSelection = onStartSelection,
                    onContinueSelection = onContinueSelection,
                    onEndSelection = onEndSelection
                )
                WordListPanel(
                    words = state.currentPuzzle.words,
                    foundWords = state.foundWords,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun WordSearchGrid(
    state: PuzzleUiState,
    modifier: Modifier = Modifier,
    onStartSelection: (GridPosition) -> Unit,
    onContinueSelection: (GridPosition) -> Unit,
    onEndSelection: () -> Unit
) {
    val puzzle = state.currentPuzzle
    val rows = puzzle.grid.size
    val columns = puzzle.grid.firstOrNull()?.length ?: 0
    val cellSpacing = 4.dp
    val density = LocalDensity.current
    val colorScheme = MaterialTheme.colorScheme
    val highlightColors = remember(colorScheme) {
        listOf(
            colorScheme.primaryContainer,
            colorScheme.tertiaryContainer,
            colorScheme.secondaryContainer,
            colorScheme.inversePrimary
        )
    }
    val foundCells = remember(state.foundWordPaths) {
        buildMap {
            state.foundWordPaths.forEach { (word, path) ->
                path.forEach { position -> put(position, word) }
            }
        }
    }
    val activePositions = state.activePath.toSet()
    val hintPosition = state.hintPosition

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val availableSize = maxWidth
        val cellSize = remember(availableSize, columns) {
            if (columns == 0) 0.dp else (availableSize - cellSpacing * (columns - 1)) / columns
        }
        val gridSize = cellSize * columns + cellSpacing * (columns - 1)
        val cellSizePx = with(density) { cellSize.toPx() }
        val spacingPx = with(density) { cellSpacing.toPx() }
        val strokeWidth = with(density) { 6.dp.toPx() }
        val paletteSize = highlightColors.size

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(gridSize)) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    state.foundWordPaths.forEach { (word, path) ->
                        val color = highlightColors[abs(word.hashCode()) % paletteSize]
                        path.zipWithNext { start, end ->
                            drawLine(
                                color = color,
                                start = start.toCenterOffset(cellSizePx, spacingPx),
                                end = end.toCenterOffset(cellSizePx, spacingPx),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(cellSpacing)
                ) {
                    puzzle.grid.forEachIndexed { rowIndex, rowValue ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(cellSpacing)
                        ) {
                            rowValue.forEachIndexed { columnIndex, character ->
                                val position = GridPosition(rowIndex, columnIndex)
                                val foundWord = foundCells[position]
                                val isActive = position in activePositions
                                val isFound = foundWord != null
                                val isHint = hintPosition == position
                                val backgroundColor = when {
                                    isActive -> MaterialTheme.colorScheme.secondaryContainer
                                    isHint -> MaterialTheme.colorScheme.tertiaryContainer
                                    isFound -> highlightColors[abs(foundWord!!.hashCode()) % paletteSize]
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                LetterCell(
                                    letter = character,
                                    size = cellSize,
                                    backgroundColor = backgroundColor,
                                    isFound = isFound,
                                    isActive = isActive,
                                    isHint = isHint
                                )
                            }
                        }
                    }
                }
                Canvas(modifier = Modifier.matchParentSize()) {
                    state.activePath.zipWithNext { start, end ->
                        drawLine(
                            color = MaterialTheme.colorScheme.secondary,
                            start = start.toCenterOffset(cellSizePx, spacingPx),
                            end = end.toCenterOffset(cellSizePx, spacingPx),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(state.activePath, state.isPaused, state.isCelebrationLocked) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val position = offset.toGridPosition(cellSizePx, spacingPx, rows, columns)
                                    if (position != null) {
                                        onStartSelection(position)
                                    }
                                },
                                onDrag = { change, _ ->
                                    val position = change.position.toGridPosition(cellSizePx, spacingPx, rows, columns)
                                    if (position != null) {
                                        onContinueSelection(position)
                                    }
                                    change.consumePositionChange()
                                },
                                onDragEnd = { onEndSelection() },
                                onDragCancel = { onEndSelection() }
                            )
                        }
                )
            }
        }
    }
}

private fun Offset.toGridPosition(
    cellSize: Float,
    spacing: Float,
    rows: Int,
    columns: Int
): GridPosition? {
    if (cellSize <= 0f) return null
    val totalSize = cellSize * columns + spacing * (columns - 1)
    if (x !in 0f..totalSize || y !in 0f..totalSize) return null
    val cellTotal = cellSize + spacing
    val columnIndex = min(columns - 1, (x / cellTotal).toInt())
    val rowIndex = min(rows - 1, (y / cellTotal).toInt())
    val cellStartX = columnIndex * cellTotal
    val cellStartY = rowIndex * cellTotal
    if (x > cellStartX + cellSize || y > cellStartY + cellSize) return null
    return GridPosition(rowIndex, columnIndex)
}

private fun GridPosition.toCenterOffset(cellSize: Float, spacing: Float): Offset {
    val cellTotal = cellSize + spacing
    val centerX = column * cellTotal + cellSize / 2f
    val centerY = row * cellTotal + cellSize / 2f
    return Offset(centerX, centerY)
}

@Composable
private fun LetterCell(
    letter: Char,
    size: Dp,
    backgroundColor: Color,
    isFound: Boolean,
    isActive: Boolean,
    isHint: Boolean
) {
    val textColor = if (isFound || isActive || isHint) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Surface(
        modifier = Modifier.size(size),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        tonalElevation = if (isFound || isActive) 4.dp else 0.dp,
        shadowElevation = if (isActive) 4.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun WordListPanel(words: List<String>, foundWords: Set<String>, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Find these words",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Divider()
            val mid = (words.size + 1) / 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WordListColumn(words = words.take(mid), foundWords = foundWords, modifier = Modifier.weight(1f))
                WordListColumn(words = words.drop(mid), foundWords = foundWords, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WordListColumn(words: List<String>, foundWords: Set<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        words.forEach { word ->
            val isFound = word in foundWords
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    color = if (isFound) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = if (isFound) 6.dp else 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isFound) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isFound) FontWeight.Bold else FontWeight.Normal,
                    color = if (isFound) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun FeedbackMessage(message: String?, isPositive: Boolean) {
    if (message == null) return
    val colors = if (isPositive) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = colors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Filled.Celebration else Icons.Filled.TipsAndUpdates,
                contentDescription = null
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isPositive) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun BottomControlBar(
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    onRevealHint: () -> Unit,
    onNextPuzzle: () -> Unit,
    remainingWords: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onNextPuzzle,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = if (remainingWords == 0) "New Puzzle" else "Skip Puzzle")
        }
        FilledTonalButton(
            onClick = onRevealHint,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.Filled.TipsAndUpdates, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Hint")
        }
        OutlinedButton(
            onClick = onTogglePause,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (isPaused) "Resume" else "Pause")
        }
    }
}

@Composable
private fun CelebrationCard(
    sticker: Sticker?,
    onNextPuzzle: () -> Unit,
    onReplay: () -> Unit,
    hasNextPuzzle: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Celebration,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Puzzle Complete!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "All words are checked off. You're a puzzle pro!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            if (sticker != null) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = sticker.emoji, fontSize = 32.sp)
                        Text(text = sticker.name, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReplay,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Play Again")
                }
                Button(
                    onClick = onNextPuzzle,
                    enabled = hasNextPuzzle,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = if (hasNextPuzzle) "Next Puzzle" else "All Done")
                }
            }
        }
    }
}

private fun Int.toClock(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}
