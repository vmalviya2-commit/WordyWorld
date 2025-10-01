package com.education.wordyworld.data

import com.education.wordyworld.model.PuzzleDifficulty
import com.education.wordyworld.model.WordPuzzle

class PuzzleRepository {
    private val puzzles = listOf(
        WordPuzzle(
            id = 1,
            theme = "Morning Boost",
            title = "Sunrise Starter",
            difficulty = PuzzleDifficulty.EASY,
            rewardStickerId = 1,
            targetWord = "SMILE",
            hint = "How you greet the day",
            validGuesses = setOf(
                "SMILE", "STYLE", "SLIDE", "SLIME", "MILES", "LIMES", "FILES", "SMITE"
            )
        ),
        WordPuzzle(
            id = 2,
            theme = "Ocean Wonders",
            title = "Splashy Secret",
            difficulty = PuzzleDifficulty.MEDIUM,
            rewardStickerId = 2,
            targetWord = "OCEAN",
            hint = "Big blue playground",
            validGuesses = setOf(
                "OCEAN", "CANOE", "OCTAL", "CLEAN", "OZONE", "BACON", "CORAL", "OASIS"
            )
        ),
        WordPuzzle(
            id = 3,
            theme = "Tasty Treats",
            title = "Juicy Jumble",
            difficulty = PuzzleDifficulty.EASY,
            rewardStickerId = 5,
            targetWord = "MANGO",
            hint = "Tropical smoothie star",
            validGuesses = setOf(
                "MANGO", "AMONG", "GAMON", "GOING", "MONEY", "MINTY", "MANGY", "MANGO"
            )
        )
    )

    fun getAllPuzzles(): List<WordPuzzle> = puzzles

    fun getDailyPuzzle(): WordPuzzle = puzzles.first()
}
