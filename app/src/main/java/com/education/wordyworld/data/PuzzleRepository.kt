package com.education.wordyworld.data

import com.education.wordyworld.model.PuzzleDifficulty
import com.education.wordyworld.model.WordPuzzle

class PuzzleRepository {
    private val puzzles = listOf(
        WordPuzzle(
            id = 1,
            theme = "Safari Buddies",
            title = "Jungle Word Hunt",
            difficulty = PuzzleDifficulty.EASY,
            rewardStickerId = 1,
            grid = listOf(
                "ABRMYEKNOM",
                "TTORRAPAFK",
                "RGIRAFFENQ",
                "RJALEMURXB",
                "AERENZMHMI",
                "NKBAIOILTG",
                "EAEDAPIKDC",
                "YNZHPQWLLJ",
                "HSNOTIGERA",
                "VHGHBPMKLW"
            ),
            words = listOf(
                "LION",
                "ZEBRA",
                "HIPPO",
                "GIRAFFE",
                "PARROT",
                "MONKEY",
                "TIGER",
                "SNAKE",
                "LEMUR",
                "HYENA"
            )
        ),
        WordPuzzle(
            id = 2,
            theme = "Ocean Wonders",
            title = "Splashy Search",
            difficulty = PuzzleDifficulty.MEDIUM,
            rewardStickerId = 2,
            grid = listOf(
                "EMDOLPHINB",
                "LOQPFZWXBV",
                "ABCWRCRABA",
                "HSGTMCORAL",
                "WIHROBPTGV",
                "MUEAKPZOAD",
                "BEAARMUDXR",
                "FGBWOKBSMR",
                "YDIUQSPPAA",
                "LAESLMZWIY"
            ),
            words = listOf(
                "DOLPHIN",
                "SHARK",
                "OCTOPUS",
                "WHALE",
                "CORAL",
                "REEF",
                "SEAL",
                "CRAB",
                "SQUID",
                "RAY"
            )
        ),
        WordPuzzle(
            id = 3,
            theme = "Snack Attack",
            title = "Tasty Treat Trail",
            difficulty = PuzzleDifficulty.EASY,
            rewardStickerId = 5,
            grid = listOf(
                "LGNGRAPEKM",
                "LXCARROTRM",
                "SIQJQCPGYT",
                "EKWAEDBHRD",
                "SJMELONCRJ",
                "EDAERBTAEQ",
                "ELPPACHEBI",
                "HBANANAPGK",
                "COLIVEIMJZ",
                "GZTPIIXPDK"
            ),
            words = listOf(
                "CARROT",
                "APPLE",
                "BREAD",
                "CHEESE",
                "GRAPE",
                "PEACH",
                "BANANA",
                "BERRY",
                "OLIVE",
                "MELON"
            )
        )
    )

    fun getAllPuzzles(): List<WordPuzzle> = puzzles

    fun getDailyPuzzle(): WordPuzzle = puzzles.first()
}
