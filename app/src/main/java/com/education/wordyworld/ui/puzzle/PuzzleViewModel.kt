package com.education.wordyworld.ui.puzzle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.education.wordyworld.data.PuzzleRepository
import com.education.wordyworld.data.StickerRepository
import com.education.wordyworld.model.WordPuzzle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val INITIAL_STREAK = 3
private const val WORD_SCORE = 100
private const val INCORRECT_PENALTY = 10

class PuzzleViewModel(
    private val puzzleRepository: PuzzleRepository = PuzzleRepository()
) : ViewModel() {

    private val puzzles: List<WordPuzzle> = puzzleRepository.getAllPuzzles()
    private val dailyPuzzleId: Int = puzzleRepository.getDailyPuzzle().id

    private var currentPuzzleIndex = 0
    private var timerJob: Job? = null

    var uiState by mutableStateOf(createInitialState())
        private set

    init {
        startTimer()
    }

    private fun createInitialState(): PuzzleUiState {
        val startingPuzzle = puzzles.first()
        return PuzzleUiState(
            currentPuzzle = startingPuzzle,
            totalPuzzles = puzzles.size,
            dailyPuzzleId = dailyPuzzleId,
            streakCount = INITIAL_STREAK
        )
    }

    fun startSelection(position: GridPosition) {
        val state = uiState
        if (state.isCelebrationLocked || state.isPaused) return
        if (!position.isValidFor(state.currentPuzzle)) return
        val letter = state.currentPuzzle.letterAt(position)
        uiState = state.copy(
            activePath = listOf(position),
            activeWord = letter.toString(),
            feedbackMessage = null,
            hintPosition = null
        )
    }

    fun continueSelection(position: GridPosition) {
        val state = uiState
        if (state.isCelebrationLocked || state.isPaused) return
        val puzzle = state.currentPuzzle
        if (!position.isValidFor(puzzle)) return
        val path = state.activePath
        if (path.isEmpty()) {
            startSelection(position)
            return
        }
        val last = path.last()
        if (position == last) return
        if (!last.isNeighborOf(position)) return
        val newPath = when {
            path.contains(position) -> return
            path.size >= 2 -> {
                val expectedDirection = directionOf(path[path.size - 2], last)
                val newDirection = directionOf(last, position)
                if (expectedDirection != newDirection) return
                path + position
            }
            else -> path + position
        }
        val newWord = newPath.toWord(puzzle)
        uiState = state.copy(
            activePath = newPath,
            activeWord = newWord,
            feedbackMessage = null
        )
    }

    fun endSelection() {
        val state = uiState
        if (state.activePath.isEmpty()) return
        val puzzle = state.currentPuzzle
        val candidate = state.activeWord.uppercase()
        val words = puzzle.words
        val matchedWord = when {
            candidate in words -> candidate
            candidate.reversed() in words -> candidate.reversed()
            else -> null
        }

        val alreadyFound = matchedWord != null && matchedWord in state.foundWords
        val isNewWord = matchedWord != null && !alreadyFound

        val updatedState = when {
            isNewWord -> handleNewWord(state, matchedWord!!)
            alreadyFound -> state.copy(
                feedbackMessage = "You already found ${matchedWord}!",
                isFeedbackPositive = false,
                activePath = emptyList(),
                activeWord = ""
            )
            candidate.length <= 1 -> state.copy(
                feedbackMessage = "Keep dragging across the letters to make a word!",
                isFeedbackPositive = false,
                activePath = emptyList(),
                activeWord = ""
            )
            else -> state.copy(
                feedbackMessage = "That's not on the list yet. Try a new path!",
                isFeedbackPositive = false,
                activePath = emptyList(),
                activeWord = "",
                score = (state.score - INCORRECT_PENALTY).coerceAtLeast(0)
            )
        }

        uiState = updatedState
    }

    fun revealHint() {
        val state = uiState
        val remaining = state.currentPuzzle.words.filterNot { it in state.foundWords }
        if (remaining.isEmpty()) {
            uiState = state.copy(
                feedbackMessage = "All words found! Try a new puzzle for more fun.",
                isFeedbackPositive = true,
                hintPosition = null
            )
            return
        }
        val target = remaining.random()
        val path = findWordPath(state.currentPuzzle, target)
        val hintPosition = path?.firstOrNull()
        uiState = state.copy(
            hintPosition = hintPosition,
            feedbackMessage = hintPosition?.let { "Hint: Look for the glowing ${target.first()}!" }
                ?: "Try hunting for ${target.first()}...",
            isFeedbackPositive = true
        )
    }

    fun togglePause() {
        val state = uiState
        uiState = state.copy(isPaused = !state.isPaused)
    }

    fun resetCurrentPuzzle() {
        val state = uiState
        uiState = state.copy(
            foundWords = emptySet(),
            foundWordPaths = emptyMap(),
            activePath = emptyList(),
            activeWord = "",
            feedbackMessage = null,
            isFeedbackPositive = false,
            hintPosition = null,
            score = 0,
            elapsedSeconds = 0,
            isPaused = false
        )
    }

    fun goToNextPuzzle() {
        val state = uiState
        val nextIndex = findNextPuzzleIndex(state)
        if (nextIndex == currentPuzzleIndex) return
        currentPuzzleIndex = nextIndex
        val nextPuzzle = puzzles[currentPuzzleIndex]
        uiState = state.copy(
            currentPuzzle = nextPuzzle,
            foundWords = emptySet(),
            foundWordPaths = emptyMap(),
            activePath = emptyList(),
            activeWord = "",
            feedbackMessage = null,
            isFeedbackPositive = false,
            hintPosition = null,
            score = 0,
            elapsedSeconds = 0,
            isPaused = false
        )
        startTimer()
    }

    private fun handleNewWord(state: PuzzleUiState, word: String): PuzzleUiState {
        val updatedFound = state.foundWords + word
        val updatedPaths = state.foundWordPaths + (word to state.activePath)
        val puzzleSolved = updatedFound.size == state.currentPuzzle.words.size
        val alreadyCompleted = state.currentPuzzle.id in state.solvedPuzzleIds
        val solvedIds = if (puzzleSolved && !alreadyCompleted) {
            state.solvedPuzzleIds + state.currentPuzzle.id
        } else {
            state.solvedPuzzleIds
        }
        val unlocked = if (puzzleSolved && !alreadyCompleted) {
            state.unlockedStickerIds + state.currentPuzzle.rewardStickerId
        } else {
            state.unlockedStickerIds
        }
        val newStreak = if (puzzleSolved && !alreadyCompleted) {
            state.streakCount + 1
        } else {
            state.streakCount
        }
        val completedToday = state.completedToday || (
            puzzleSolved && !alreadyCompleted && state.currentPuzzle.id == dailyPuzzleId
        )

        return state.copy(
            foundWords = updatedFound,
            foundWordPaths = updatedPaths,
            feedbackMessage = if (puzzleSolved) {
                "Amazing! You solved every word in ${state.currentPuzzle.title}!"
            } else {
                "Great job! $word is checked off."
            },
            isFeedbackPositive = true,
            score = state.score + WORD_SCORE,
            activePath = emptyList(),
            activeWord = "",
            solvedPuzzleIds = solvedIds,
            unlockedStickerIds = unlocked,
            streakCount = newStreak,
            completedToday = completedToday,
            hintPosition = null
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                val state = uiState
                if (!state.isPaused && !state.isCurrentPuzzleSolved) {
                    uiState = state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                }
            }
        }
    }

    private fun findNextPuzzleIndex(state: PuzzleUiState): Int {
        val solvedIds = state.solvedPuzzleIds
        if (solvedIds.size == puzzles.size) {
            return currentPuzzleIndex
        }
        var nextIndex = (currentPuzzleIndex + 1) % puzzles.size
        repeat(puzzles.size) {
            val puzzle = puzzles[nextIndex]
            if (puzzle.id !in solvedIds) {
                return nextIndex
            }
            nextIndex = (nextIndex + 1) % puzzles.size
        }
        return currentPuzzleIndex
    }

    private fun GridPosition.isValidFor(puzzle: WordPuzzle): Boolean {
        val rows = puzzle.grid.size
        val columns = puzzle.grid.firstOrNull()?.length ?: 0
        return row in 0 until rows && column in 0 until columns
    }

    private fun WordPuzzle.letterAt(position: GridPosition): Char {
        return grid[position.row][position.column]
    }

    private fun List<GridPosition>.toWord(puzzle: WordPuzzle): String {
        if (isEmpty()) return ""
        return buildString(size) {
            for (position in this@toWord) {
                append(puzzle.letterAt(position))
            }
        }
    }

    private fun GridPosition.isNeighborOf(other: GridPosition): Boolean {
        val rowDiff = abs(row - other.row)
        val columnDiff = abs(column - other.column)
        return (rowDiff != 0 || columnDiff != 0) && rowDiff <= 1 && columnDiff <= 1
    }

    private fun directionOf(from: GridPosition, to: GridPosition): GridDirection {
        return GridDirection((to.row - from.row).sign(), (to.column - from.column).sign())
    }

    private fun findWordPath(puzzle: WordPuzzle, word: String): List<GridPosition>? {
        if (puzzle.grid.isEmpty()) return null
        val rows = puzzle.grid.size
        val columns = puzzle.grid.first().length
        val searchTargets = listOf(word.uppercase(), word.uppercase().reversed())
        val directions = listOf(
            GridDirection(-1, -1),
            GridDirection(-1, 0),
            GridDirection(-1, 1),
            GridDirection(0, -1),
            GridDirection(0, 1),
            GridDirection(1, -1),
            GridDirection(1, 0),
            GridDirection(1, 1)
        )

        for (target in searchTargets) {
            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    if (puzzle.grid[row][column] != target[0]) continue
                    for (direction in directions) {
                        var currentRow = row
                        var currentColumn = column
                        val path = mutableListOf<GridPosition>()
                        var matched = true
                        for (character in target) {
                            if (currentRow !in 0 until rows || currentColumn !in 0 until columns) {
                                matched = false
                                break
                            }
                            if (puzzle.grid[currentRow][currentColumn] != character) {
                                matched = false
                                break
                            }
                            path += GridPosition(currentRow, currentColumn)
                            currentRow += direction.rowDelta
                            currentColumn += direction.columnDelta
                        }
                        if (matched) {
                            return path
                        }
                    }
                }
            }
        }
        return null
    }
}

data class GridPosition(val row: Int, val column: Int)

private data class GridDirection(val rowDelta: Int, val columnDelta: Int)

private fun Int.sign(): Int = when {
    this > 0 -> 1
    this < 0 -> -1
    else -> 0
}

data class PuzzleUiState(
    val currentPuzzle: WordPuzzle,
    val foundWords: Set<String> = emptySet(),
    val foundWordPaths: Map<String, List<GridPosition>> = emptyMap(),
    val activePath: List<GridPosition> = emptyList(),
    val activeWord: String = "",
    val score: Int = 0,
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false,
    val feedbackMessage: String? = null,
    val isFeedbackPositive: Boolean = false,
    val hintPosition: GridPosition? = null,
    val streakCount: Int = INITIAL_STREAK,
    val totalPuzzles: Int = 0,
    val dailyPuzzleId: Int,
    val solvedPuzzleIds: Set<Int> = emptySet(),
    val unlockedStickerIds: Set<Int> = setOf(StickerRepository.STARTER_STICKER_ID),
    val completedToday: Boolean = false
) {
    val solvedCount: Int get() = solvedPuzzleIds.size
    val remainingWords: Int get() = currentPuzzle.words.size - foundWords.size
    val hasCompletedCurrentPuzzle: Boolean get() = currentPuzzle.id in solvedPuzzleIds
    val isCurrentPuzzleSolved: Boolean get() = foundWords.size == currentPuzzle.words.size
    val isCelebrationLocked: Boolean get() = isCurrentPuzzleSolved
}

