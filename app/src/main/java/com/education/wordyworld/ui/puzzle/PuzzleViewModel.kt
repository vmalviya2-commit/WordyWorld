package com.education.wordyworld.ui.puzzle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.education.wordyworld.data.PuzzleRepository
import com.education.wordyworld.data.StickerRepository
import com.education.wordyworld.model.WordPuzzle
import kotlin.math.max

private const val INITIAL_STREAK = 3

class PuzzleViewModel(
    private val puzzleRepository: PuzzleRepository = PuzzleRepository()
) : ViewModel() {

    private val puzzles: List<WordPuzzle> = puzzleRepository.getAllPuzzles()
    private val dailyPuzzleId: Int = puzzleRepository.getDailyPuzzle().id

    private var currentPuzzleIndex = 0

    var uiState by mutableStateOf(createInitialState())
        private set

    private fun createInitialState(): PuzzleUiState {
        val startingPuzzle = puzzles.first()
        return PuzzleUiState(
            currentPuzzle = startingPuzzle,
            displayLetters = startingPuzzle.scrambledLetters,
            totalPuzzles = puzzles.size,
            dailyPuzzleId = dailyPuzzleId,
            streakCount = INITIAL_STREAK
        )
    }

    fun selectLetter(index: Int) {
        val state = uiState
        if (index in state.usedLetterIndices || state.isCelebrationLocked) return
        val letter = state.displayLetters.getOrNull(index) ?: return
        val updatedGuess = state.currentGuess + letter
        uiState = state.copy(
            currentGuess = updatedGuess,
            usedLetterIndices = state.usedLetterIndices + index,
            feedbackMessage = null,
            isFeedbackPositive = false
        )
    }

    fun removeLastLetter() {
        val state = uiState
        if (state.currentGuess.isEmpty() || state.isCelebrationLocked) return
        uiState = state.copy(
            currentGuess = state.currentGuess.dropLast(1),
            usedLetterIndices = state.usedLetterIndices.dropLast(1)
        )
    }

    fun clearGuess() {
        val state = uiState
        if (state.currentGuess.isEmpty()) return
        uiState = state.copy(
            currentGuess = "",
            usedLetterIndices = emptyList(),
            feedbackMessage = null,
            isFeedbackPositive = false
        )
    }

    fun revealHint() {
        val state = uiState
        if (!state.isHintVisible) {
            uiState = state.copy(isHintVisible = true)
        }
    }

    fun shuffleLetters() {
        val state = uiState
        if (state.isCelebrationLocked) return
        val shuffled = state.displayLetters.shuffled()
        uiState = state.copy(
            displayLetters = shuffled,
            currentGuess = "",
            usedLetterIndices = emptyList(),
            feedbackMessage = null,
            isFeedbackPositive = false
        )
    }

    fun submitGuess() {
        val state = uiState
        if (state.currentGuess.length != state.currentPuzzle.solution.length) {
            return
        }
        val guess = state.currentGuess.uppercase()
        val isCorrect = guess == state.currentPuzzle.solution
        if (isCorrect) {
            val alreadySolved = state.isCurrentPuzzleSolved
            val solvedIds = if (alreadySolved) state.solvedPuzzleIds else state.solvedPuzzleIds + state.currentPuzzle.id
            val unlocked = if (alreadySolved) state.unlockedStickerIds else state.unlockedStickerIds + state.currentPuzzle.rewardStickerId
            val newStreak = if (alreadySolved) state.streakCount else state.streakCount + 1
            val completedToday = state.completedToday || state.currentPuzzle.id == dailyPuzzleId
            uiState = state.copy(
                currentGuess = state.currentPuzzle.solution,
                usedLetterIndices = state.displayLetters.indices.toList(),
                solvedPuzzleIds = solvedIds,
                unlockedStickerIds = unlocked,
                feedbackMessage = if (alreadySolved) {
                    "Awesome practice! ${state.currentPuzzle.solution} still fits perfectly."
                } else {
                    "Great job! ${state.currentPuzzle.solution} is correct!"
                },
                isFeedbackPositive = true,
                streakCount = newStreak,
                completedToday = completedToday,
                isHintVisible = false
            )
        } else {
            uiState = state.copy(
                feedbackMessage = "Not quite yet. Try a different letter order!",
                isFeedbackPositive = false,
                streakCount = max(1, state.streakCount - 1),
                currentGuess = "",
                usedLetterIndices = emptyList()
            )
        }
    }

    fun resetCurrentPuzzle() {
        val state = uiState
        uiState = state.copy(
            currentGuess = "",
            usedLetterIndices = emptyList(),
            isHintVisible = false,
            feedbackMessage = null,
            isFeedbackPositive = false
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
            displayLetters = nextPuzzle.scrambledLetters,
            currentGuess = "",
            usedLetterIndices = emptyList(),
            isHintVisible = false,
            feedbackMessage = null,
            isFeedbackPositive = false
        )
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
}

data class PuzzleUiState(
    val currentPuzzle: WordPuzzle,
    val displayLetters: List<Char>,
    val currentGuess: String = "",
    val usedLetterIndices: List<Int> = emptyList(),
    val solvedPuzzleIds: Set<Int> = emptySet(),
    val unlockedStickerIds: Set<Int> = setOf(StickerRepository.STARTER_STICKER_ID),
    val isHintVisible: Boolean = false,
    val feedbackMessage: String? = null,
    val isFeedbackPositive: Boolean = false,
    val streakCount: Int = INITIAL_STREAK,
    val totalPuzzles: Int = 0,
    val dailyPuzzleId: Int,
    val completedToday: Boolean = false
) {
    val solvedCount: Int get() = solvedPuzzleIds.size
    val isCurrentPuzzleSolved: Boolean get() = currentPuzzle.id in solvedPuzzleIds
    val isCelebrationLocked: Boolean get() = isCurrentPuzzleSolved && currentGuess == currentPuzzle.solution
}
