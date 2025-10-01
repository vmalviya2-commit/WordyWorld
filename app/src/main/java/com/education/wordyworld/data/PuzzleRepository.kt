package com.education.wordyworld.data

import com.education.wordyworld.model.PuzzleDifficulty
import com.education.wordyworld.model.WordPuzzle
import kotlin.random.Random

class PuzzleRepository {
    private val puzzles = listOf(
        createPuzzle(
            id = 1,
            theme = "Safari Buddies",
            title = "Roaring Pal",
            solution = "LION",
            hint = "The jungle's brave leader.",
            difficulty = PuzzleDifficulty.EASY,
            rewardStickerId = 1
        ),
        createPuzzle(
            id = 2,
            theme = "Ocean Wonders",
            title = "Playful Swimmer",
            solution = "DOLPHIN",
            hint = "Smart friend that flips in the sea.",
            difficulty = PuzzleDifficulty.MEDIUM,
            rewardStickerId = 2
        ),
        createPuzzle(
            id = 3,
            theme = "Garden Party",
            title = "Glow Time",
            solution = "FIREFLY",
            hint = "Lights up the night like a tiny lantern.",
            difficulty = PuzzleDifficulty.MEDIUM,
            rewardStickerId = 3
        ),
        createPuzzle(
            id = 4,
            theme = "Space Explorers",
            title = "Moon Hopper",
            solution = "ASTRONAUT",
            hint = "Floats in space and wears a shiny suit.",
            difficulty = PuzzleDifficulty.HARD,
            rewardStickerId = 4
        ),
        createPuzzle(
            id = 5,
            theme = "Snack Attack",
            title = "Crunch Time",
            solution = "CARROT",
            hint = "Bunnies munch it for a crunchy treat.",
            difficulty = PuzzleDifficulty.EASY,
            rewardStickerId = 5
        )
    )

    private fun createPuzzle(
        id: Int,
        theme: String,
        title: String,
        solution: String,
        hint: String,
        difficulty: PuzzleDifficulty,
        rewardStickerId: Int
    ): WordPuzzle {
        val uppercase = solution.uppercase()
        val scrambled = uppercase.toList().shuffled(Random(id))
        return WordPuzzle(
            id = id,
            theme = theme,
            title = title,
            solution = uppercase,
            hint = hint,
            difficulty = difficulty,
            rewardStickerId = rewardStickerId,
            scrambledLetters = scrambled
        )
    }

    fun getAllPuzzles(): List<WordPuzzle> = puzzles

    fun getDailyPuzzle(): WordPuzzle = puzzles.first()
}
