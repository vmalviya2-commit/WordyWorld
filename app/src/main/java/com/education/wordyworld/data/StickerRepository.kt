package com.education.wordyworld.data

import com.education.wordyworld.model.Sticker

class StickerRepository {
    private val stickers = listOf(
        Sticker(
            id = STARTER_STICKER_ID,
            name = "Bright Beginner",
            description = "You unlocked the adventure!",
            emoji = "🌟"
        ),
        Sticker(
            id = 1,
            name = "Lion Leader",
            description = "Solved the Safari Buddies puzzle.",
            emoji = "🦁"
        ),
        Sticker(
            id = 2,
            name = "Ocean Pal",
            description = "Finished the Ocean Wonders challenge.",
            emoji = "🐬"
        ),
        Sticker(
            id = 3,
            name = "Night Spark",
            description = "Glow with the Garden Party puzzle win!",
            emoji = "✨"
        ),
        Sticker(
            id = 4,
            name = "Space Hopper",
            description = "Bounced through the Space Explorers puzzle.",
            emoji = "🧑‍🚀"
        ),
        Sticker(
            id = 5,
            name = "Snack Champ",
            description = "Crunch time success!",
            emoji = "🥕"
        )
    )

    fun getStickers(): List<Sticker> = stickers

    companion object {
        const val STARTER_STICKER_ID = 0
    }
}
