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
import kotlin.math.max

private const val INITIAL_STREAK = 3
private const val BASE_SCORE = 600
private const val SCORE_DECREMENT = 60
private const val MIN_SCORE = 120

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

    fun inputLetter(letter: Char) {
        val state = uiState
        if (state.isInteractionLocked) return
        val normalized = letter.uppercaseChar()
        if (!normalized.isLetter()) return
        if (state.currentInput.length >= state.currentPuzzle.wordLength) return
        updateCurrentInput(state.currentInput + normalized)
    }

    fun deleteLetter() {
        val state = uiState
        if (state.isInteractionLocked) return
        if (state.currentInput.isEmpty()) return
        updateCurrentInput(state.currentInput.dropLast(1))
    }

    fun submitGuess() {
        val state = uiState
        if (state.isInteractionLocked) return
        val puzzle = state.currentPuzzle
        val guess = state.currentInput
        if (guess.length < puzzle.wordLength) {
            uiState = state.copy(
                feedbackMessage = "Add more letters to guess the ${puzzle.wordLength}-letter word.",
                isFeedbackPositive = false
            )
            return
        }
        if (puzzle.validGuesses.isNotEmpty() && guess !in puzzle.validGuesses) {
            uiState = state.copy(
                feedbackMessage = "That word isn't in today's list. Try another!",
                isFeedbackPositive = false
            )
            return
        }

        val evaluation = evaluateGuess(puzzle.targetWord.uppercase(), guess)
        val updatedRows = state.guessRows.toMutableList()
        updatedRows[state.currentRowIndex] = GuessRow(
            letters = List(puzzle.wordLength) { index -> guess[index] as Char? },
            feedback = evaluation,
            isSubmitted = true
        )
        val updatedKeyboard = mergeKeyboardState(state.keyboardState, guess, evaluation)
        val usedGuesses = state.usedGuesses + 1
        val solved = guess == puzzle.targetWord.uppercase()

        val solvedIds = if (solved) state.solvedPuzzleIds + puzzle.id else state.solvedPuzzleIds
        val unlocked = if (solved && puzzle.rewardStickerId !in state.unlockedStickerIds) {
            state.unlockedStickerIds + puzzle.rewardStickerId
        } else {
            state.unlockedStickerIds
        }
        val newStreak = when {
            solved && puzzle.id !in state.solvedPuzzleIds -> state.streakCount + 1
            !solved && usedGuesses >= puzzle.allowedGuesses -> max(0, state.streakCount - 1)
            else -> state.streakCount
        }
        val completedToday = state.completedToday || (solved && puzzle.id == dailyPuzzleId)

        val gainedScore = if (solved) {
            val provisional = BASE_SCORE - (usedGuesses - 1) * SCORE_DECREMENT
            max(MIN_SCORE, provisional)
        } else 0

        val nextRowIndex = (state.currentRowIndex + 1).coerceAtMost(puzzle.allowedGuesses - 1)
        val shouldAdvanceRow = !solved && usedGuesses < puzzle.allowedGuesses
        val resetInput = ""

        uiState = state.copy(
            guessRows = updatedRows,
            keyboardState = updatedKeyboard,
            currentRowIndex = if (shouldAdvanceRow) nextRowIndex else state.currentRowIndex,
            currentInput = resetInput,
            feedbackMessage = when {
                solved -> "You solved it in $usedGuesses tries!"
                usedGuesses >= puzzle.allowedGuesses -> "Out of guesses! The word was ${puzzle.targetWord.uppercase()}."
                else -> "Not quite. Keep going!"
            },
            isFeedbackPositive = solved,
            score = state.score + gainedScore,
            solvedPuzzleIds = solvedIds,
            unlockedStickerIds = unlocked,
            streakCount = newStreak,
            completedToday = completedToday
        )
    }

    fun revealHint() {
        val state = uiState
        val message = if (state.hintRevealed) {
            "Hint already revealed: starts with ${state.currentPuzzle.targetWord.first().uppercaseChar()}"
        } else {
            "Hint: starts with ${state.currentPuzzle.targetWord.first().uppercaseChar()} and ${state.currentPuzzle.hint.lowercase()}"
        }
        uiState = state.copy(
            feedbackMessage = message,
            isFeedbackPositive = true,
            hintRevealed = true
        )
    }

    fun togglePause() {
        val state = uiState
        uiState = state.copy(isPaused = !state.isPaused)
    }

    fun resetCurrentPuzzle() {
        val state = uiState
        uiState = state.copy(
            guessRows = buildGuessRows(state.currentPuzzle.wordLength, state.currentPuzzle.allowedGuesses),
            currentRowIndex = 0,
            currentInput = "",
            keyboardState = emptyMap(),
            feedbackMessage = null,
            isFeedbackPositive = false,
            hintRevealed = false,
            elapsedSeconds = 0,
            isPaused = false
        )
        startTimer()
    }

    fun goToNextPuzzle() {
        val state = uiState
        val nextIndex = findNextPuzzleIndex(state)
        if (nextIndex == currentPuzzleIndex) return
        currentPuzzleIndex = nextIndex
        val nextPuzzle = puzzles[currentPuzzleIndex]
        uiState = state.copy(
            currentPuzzle = nextPuzzle,
            guessRows = buildGuessRows(nextPuzzle.wordLength, nextPuzzle.allowedGuesses),
            currentRowIndex = 0,
            currentInput = "",
            keyboardState = emptyMap(),
            feedbackMessage = null,
            isFeedbackPositive = false,
            hintRevealed = false,
            elapsedSeconds = 0,
            isPaused = false
        )
        startTimer()
    }

    private fun updateCurrentInput(value: String) {
        val state = uiState
        val capped = value.take(state.currentPuzzle.wordLength)
        val updatedRows = state.guessRows.toMutableList()
        val letters = MutableList(state.currentPuzzle.wordLength) { index ->
            capped.getOrNull(index)?.uppercaseChar()
        }
        updatedRows[state.currentRowIndex] = state.guessRows[state.currentRowIndex].copy(
            letters = letters,
            isSubmitted = false
        )
        uiState = state.copy(
            guessRows = updatedRows,
            currentInput = capped,
            feedbackMessage = null
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                val state = uiState
                if (!state.isPaused && !state.isCurrentPuzzleSolved && !state.isOutOfGuesses) {
                    uiState = state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                }
            }
        }
    }

    private fun createInitialState(): PuzzleUiState {
        val startingPuzzle = puzzles.first()
        return PuzzleUiState(
            currentPuzzle = startingPuzzle,
            guessRows = buildGuessRows(startingPuzzle.wordLength, startingPuzzle.allowedGuesses),
            totalPuzzles = puzzles.size,
            dailyPuzzleId = dailyPuzzleId,
            streakCount = INITIAL_STREAK
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

    private fun evaluateGuess(target: String, guess: String): List<LetterFeedback> {
        val length = target.length
        val result = MutableList(length) { LetterFeedback.Absent }
        val targetChars = target.toCharArray()
        val consumed = BooleanArray(length)

        for (index in 0 until length) {
            if (guess[index] == target[index]) {
                result[index] = LetterFeedback.Correct
                consumed[index] = true
            }
        }
        for (index in 0 until length) {
            if (result[index] == LetterFeedback.Correct) continue
            val guessChar = guess[index]
            val matchIndex = targetChars.indices.firstOrNull { i -> !consumed[i] && targetChars[i] == guessChar }
            if (matchIndex != null) {
                consumed[matchIndex] = true
                result[index] = LetterFeedback.Present
            }
        }
        return result
    }

    private fun mergeKeyboardState(
        existing: Map<Char, LetterFeedback>,
        guess: String,
        evaluation: List<LetterFeedback>
    ): Map<Char, LetterFeedback> {
        val updated = existing.toMutableMap()
        guess.forEachIndexed { index, char ->
            val feedback = evaluation[index]
            val prior = updated[char]
            if (prior == null || feedback.priority > prior.priority) {
                updated[char] = feedback
            }
        }
        return updated
    }

    companion object {
        private fun buildGuessRows(wordLength: Int, allowedGuesses: Int): List<GuessRow> {
            return List(allowedGuesses) { GuessRow.empty(wordLength) }
        }
    }
}

data class PuzzleUiState(
    val currentPuzzle: WordPuzzle,
    val guessRows: List<GuessRow>,
    val currentRowIndex: Int = 0,
    val currentInput: String = "",
    val keyboardState: Map<Char, LetterFeedback> = emptyMap(),
    val score: Int = 0,
    val elapsedSeconds: Int = 0,
    val isPaused: Boolean = false,
    val feedbackMessage: String? = null,
    val isFeedbackPositive: Boolean = false,
    val hintRevealed: Boolean = false,
    val streakCount: Int = INITIAL_STREAK,
    val totalPuzzles: Int = 0,
    val dailyPuzzleId: Int,
    val solvedPuzzleIds: Set<Int> = emptySet(),
    val unlockedStickerIds: Set<Int> = setOf(StickerRepository.STARTER_STICKER_ID),
    val completedToday: Boolean = false
) {
    val solvedCount: Int get() = solvedPuzzleIds.size
    val hasCompletedCurrentPuzzle: Boolean get() = currentPuzzle.id in solvedPuzzleIds
    val isCurrentPuzzleSolved: Boolean get() = hasCompletedCurrentPuzzle
    val usedGuesses: Int get() = guessRows.count { it.isSubmitted }
    val remainingGuesses: Int get() = currentPuzzle.allowedGuesses - usedGuesses
    val isOutOfGuesses: Boolean get() = !isCurrentPuzzleSolved && usedGuesses >= currentPuzzle.allowedGuesses
    val isInteractionLocked: Boolean get() = isPaused || isCurrentPuzzleSolved || isOutOfGuesses
}

enum class LetterFeedback(val priority: Int) {
    Idle(0),
    Absent(1),
    Present(2),
    Correct(3)
}

data class GuessRow(
    val letters: List<Char?>,
    val feedback: List<LetterFeedback>,
    val isSubmitted: Boolean
) {
    companion object {
        fun empty(wordLength: Int): GuessRow = GuessRow(
            letters = List(wordLength) { null },
            feedback = List(wordLength) { LetterFeedback.Idle },
            isSubmitted = false
        )
    }
}
