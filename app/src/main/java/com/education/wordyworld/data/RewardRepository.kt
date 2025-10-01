package com.education.wordyworld.data

import com.education.wordyworld.model.RewardMilestone

class RewardRepository {
    private val milestones = listOf(
        RewardMilestone(
            id = 1,
            title = "First Steps",
            description = "Solve 1 puzzle to earn your first badge.",
            targetPuzzles = 1
        ),
        RewardMilestone(
            id = 2,
            title = "Weekly Wizard",
            description = "Keep your 5-day streak alive!",
            targetPuzzles = 5
        ),
        RewardMilestone(
            id = 3,
            title = "Sticker Superfan",
            description = "Collect 4 stickers to unlock a surprise.",
            targetPuzzles = 4
        ),
        RewardMilestone(
            id = 4,
            title = "Galaxy Brain",
            description = "Complete all daily puzzles this week.",
            targetPuzzles = 7
        )
    )

    fun getRewardMilestones(): List<RewardMilestone> = milestones
}
