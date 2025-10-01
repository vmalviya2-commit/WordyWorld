package com.education.wordyworld.model

enum class PuzzleDifficulty {
    EASY,
    MEDIUM,
    HARD
}

data class WordPuzzle(
    val id: Int,
    val theme: String,
    val title: String,
    val solution: String,
    val hint: String,
    val difficulty: PuzzleDifficulty,
    val rewardStickerId: Int,
    val scrambledLetters: List<Char>
)

data class Sticker(
    val id: Int,
    val name: String,
    val description: String,
    val emoji: String
)

data class RewardMilestone(
    val id: Int,
    val title: String,
    val description: String,
    val targetPuzzles: Int
)

enum class ShopItemType {
    HINT_PACK,
    STICKER_PACK,
    AD_FREE
}

data class ShopItem(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    val type: ShopItemType
)
