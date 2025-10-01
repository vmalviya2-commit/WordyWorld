package com.education.wordyworld.data

import androidx.annotation.DrawableRes
import com.education.wordyworld.R

data class ProjectPlan(
    val info: ProjectInfo,
    val goal: ProjectGoal,
    val phases: List<ProjectPhase>,
    val currentFocus: CurrentFocus
)

data class ProjectPhase(
    val title: String,
    val timeframe: String,
    val tasks: List<String>,
    val deliverables: List<String>
)

data class ActionItem(
    val description: String,
    val owner: String,
    val due: String
)

data class CurrentFocus(
    val phase: ProjectPhase,
    val statusNote: String,
    val actionItems: List<ActionItem>
)

data class ProjectGoal(
    val headline: String,
    val description: String,
    @DrawableRes val icon: Int
)

data class ProjectInfo(
    val project: String,
    val version: String,
    val preparedBy: String,
    val date: String
)

private val WordPuzzleKidsPhases = listOf(
    ProjectPhase(
        title = "Phase 1 – Planning & Research",
        timeframe = "Week 1",
        tasks = listOf(
            "Perform market research on trending puzzle apps like Wordscapes and Word Connect",
            "Define the kid audience (ages 6–14) and parent decision makers",
            "Confirm key features: daily puzzles, streaks, hints, stickers, and safe ads",
            "Draft and review the Product Requirements Document"
        ),
        deliverables = listOf(
            "Approved PRD",
            "Feature list and monetization plan"
        )
    ),
    ProjectPhase(
        title = "Phase 2 – Game Design & UI/UX",
        timeframe = "Weeks 2–3",
        tasks = listOf(
            "Design a friendly mascot character",
            "Create wireframes and user flows for key journeys",
            "Design puzzle layouts including grid, letter connect, and crossword variations",
            "Outline the sticker and badge reward system",
            "Produce UI mockups for all major screens"
        ),
        deliverables = listOf(
            "Mascot character design",
            "Wireframes for Home, Puzzle, Reward, Shop, Stickers, and Parent Dashboard",
            "UI style guide covering colors, fonts, icons, and buttons"
        )
    ),
    ProjectPhase(
        title = "Phase 3 – Technical Setup",
        timeframe = "Week 4",
        tasks = listOf(
            "Select the engine (Unity with C# or Flutter with Flame)",
            "Initialize the repository and configure CI/CD",
            "Integrate Firebase for authentication, cloud saves, and analytics",
            "Prepare the puzzle data store (Firestore or JSON)",
            "Connect AdMob with family-safe ad settings"
        ),
        deliverables = listOf(
            "Project initialized in the chosen engine",
            "Firebase and database connectivity established",
            "Repository available with automated build pipeline"
        )
    ),
    ProjectPhase(
        title = "Phase 4 – Core Development",
        timeframe = "Weeks 5–7",
        tasks = listOf(
            "Implement the core puzzle engine including word search and word connect",
            "Add hint mechanics like reveal letter, reveal word, and shuffle",
            "Build streak tracking and daily puzzle rotation",
            "Animate mascot reactions and provide feedback cues",
            "Persist puzzle content and progress in the database"
        ),
        deliverables = listOf(
            "Playable core puzzles",
            "Daily puzzle system",
            "Functional hint system",
            "Mascot animations integrated"
        )
    ),
    ProjectPhase(
        title = "Phase 5 – UI Development & Integration",
        timeframe = "Weeks 8–9",
        tasks = listOf(
            "Build UI screens for Home, Puzzle Play, Rewards, Shop, Sticker Album, and Parent Dashboard",
            "Connect interface elements to the puzzle engine",
            "Implement animations, sound effects, and feedback loops"
        ),
        deliverables = listOf(
            "Fully working UI connected to backend systems",
            "Parental dashboard with necessary controls",
            "Reward system linked to gameplay outcomes"
        )
    ),
    ProjectPhase(
        title = "Phase 6 – Monetization & Rewards",
        timeframe = "Week 10",
        tasks = listOf(
            "Integrate AdMob rewarded ads for hints with family-safe filters",
            "Implement in-app purchases for hint packs, sticker packs, and ad removal",
            "Add COPPA-compliant parental confirmations for purchases",
            "Expand the sticker and badge collection flow"
        ),
        deliverables = listOf(
            "Family-safe ads integrated",
            "IAP flows implemented and tested",
            "Sticker album unlockable through rewards"
        )
    ),
    ProjectPhase(
        title = "Phase 7 – Testing & Quality Assurance",
        timeframe = "Week 11",
        tasks = listOf(
            "Validate puzzle logic with unit tests",
            "Conduct UI/UX and kid usability testing",
            "Verify parental controls and purchase gates",
            "Audit ad inventory to ensure only safe ads are shown",
            "Optimize for lower-end devices"
        ),
        deliverables = listOf(
            "QA test reports",
            "Bug fixes and performance optimizations",
            "Feedback from kids and parents incorporated"
        )
    ),
    ProjectPhase(
        title = "Phase 8 – Launch Preparation & Release",
        timeframe = "Week 12",
        tasks = listOf(
            "Create app store listings with icons, screenshots, and a preview video",
            "Write a parent-focused, safety-first description",
            "Run a soft launch in select regions and collect feedback",
            "Address issues prior to global rollout"
        ),
        deliverables = listOf(
            "App published in the Kids section of both stores",
            "Initial downloads and reviews gathered"
        )
    ),
    ProjectPhase(
        title = "Phase 9 – Post-Launch Growth & Updates",
        timeframe = "Ongoing",
        tasks = listOf(
            "Release new puzzles each week",
            "Add seasonal events such as Christmas and Halloween themes",
            "Introduce multiplayer challenge modes",
            "Expand sticker collections and mascot customization",
            "Monitor analytics for retention and monetization insights"
        ),
        deliverables = listOf(
            "Continuous content updates",
            "Seasonal event rollouts",
            "Improved retention and monetization metrics"
        )
    )
)

val WordPuzzleKidsPlan = ProjectPlan(
    info = ProjectInfo(
        project = "Word Puzzle Kids",
        version = "1.0",
        preparedBy = "Product & UX Team",
        date = "October 1, 2025"
    ),
    goal = ProjectGoal(
        headline = "Design, develop, and launch a kid-friendly word puzzle app",
        description = "Create a playful experience with engaging puzzles, cartoon mascots, sticker rewards, parental controls, " +
            "and monetization via family-safe ads and in-app purchases for the Kids sections of the Google Play Store and Apple " +
            "App Store.",
        icon = R.drawable.ic_goal
    ),
    phases = WordPuzzleKidsPhases,
    currentFocus = CurrentFocus(
        phase = WordPuzzleKidsPhases.first(),
        statusNote = "Kick off Week 1 by validating the feature set, capturing market insights, and drafting the PRD for sign-off",
        actionItems = listOf(
            ActionItem(
                description = "Synthesize competitive review of Wordscapes, Word Connect, and other top Kids puzzles",
                owner = "Product Research",
                due = "Oct 2"
            ),
            ActionItem(
                description = "Interviews with 3 parents to validate feature priorities and monetization comfort",
                owner = "UX Research",
                due = "Oct 3"
            ),
            ActionItem(
                description = "Draft PRD sections covering gameplay, rewards, parental controls, and monetization",
                owner = "Product Management",
                due = "Oct 4"
            ),
            ActionItem(
                description = "Review PRD with Engineering & Design for approval",
                owner = "Cross-functional Team",
                due = "Oct 5"
            )
        )
    )
)
