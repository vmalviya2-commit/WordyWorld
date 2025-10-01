package com.education.wordyworld.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.education.wordyworld.data.RewardRepository
import com.education.wordyworld.data.ShopRepository
import com.education.wordyworld.data.StickerRepository
import com.education.wordyworld.model.RewardMilestone
import com.education.wordyworld.model.ShopItem
import com.education.wordyworld.model.Sticker
import com.education.wordyworld.ui.home.HomeScreen
import com.education.wordyworld.ui.parents.ParentDashboardScreen
import com.education.wordyworld.ui.puzzle.PuzzleScreen
import com.education.wordyworld.ui.puzzle.PuzzleViewModel
import com.education.wordyworld.ui.rewards.RewardsScreen
import com.education.wordyworld.ui.shop.ShopScreen
import com.education.wordyworld.ui.stickers.StickerAlbumScreen

@Composable
fun WordyWorldApp() {
    val navController = rememberNavController()
    val puzzleViewModel: PuzzleViewModel = viewModel()
    val puzzleState = puzzleViewModel.uiState

    val stickers = remember { StickerRepository().getStickers() }
    val rewardMilestones = remember { RewardRepository().getRewardMilestones() }
    val shopItems = remember { ShopRepository().getShopItems() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = WordyWorldDestination.fromRoute(navBackStackEntry?.destination) ?: WordyWorldDestination.Home

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WordyWorldTopBar(
                currentDestination = currentDestination,
                solvedCount = puzzleState.solvedCount,
                totalPuzzles = puzzleState.totalPuzzles
            )
        },
        bottomBar = {
            WordyWorldBottomBar(
                currentDestination = currentDestination,
                onNavigate = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        WordyWorldNavHost(
            navController = navController,
            padding = innerPadding,
            stickers = stickers,
            rewardMilestones = rewardMilestones,
            shopItems = shopItems,
            puzzleViewModel = puzzleViewModel
        )
    }
}

@Composable
private fun WordyWorldNavHost(
    navController: NavHostController,
    padding: PaddingValues,
    stickers: List<Sticker>,
    rewardMilestones: List<RewardMilestone>,
    shopItems: List<ShopItem>,
    puzzleViewModel: PuzzleViewModel
) {
    val puzzleState = puzzleViewModel.uiState
    NavHost(
        navController = navController,
        startDestination = WordyWorldDestination.Home.route,
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        composable(WordyWorldDestination.Home.route) {
            HomeScreen(
                state = puzzleState,
                stickers = stickers,
                onStartPuzzle = { navController.navigate(WordyWorldDestination.Puzzle.route) },
                onOpenRewards = { navController.navigate(WordyWorldDestination.Rewards.route) },
                onOpenStickerAlbum = { navController.navigate(WordyWorldDestination.Stickers.route) },
                onOpenShop = { navController.navigate(WordyWorldDestination.Shop.route) }
            )
        }
        composable(WordyWorldDestination.Puzzle.route) {
            PuzzleScreen(
                state = puzzleState,
                stickers = stickers,
                onStartSelection = puzzleViewModel::startSelection,
                onContinueSelection = puzzleViewModel::continueSelection,
                onEndSelection = puzzleViewModel::endSelection,
                onRevealHint = puzzleViewModel::revealHint,
                onTogglePause = puzzleViewModel::togglePause,
                onNextPuzzle = puzzleViewModel::goToNextPuzzle,
                onResetPuzzle = puzzleViewModel::resetCurrentPuzzle
            )
        }
        composable(WordyWorldDestination.Rewards.route) {
            RewardsScreen(
                state = puzzleState,
                milestones = rewardMilestones
            )
        }
        composable(WordyWorldDestination.Stickers.route) {
            StickerAlbumScreen(
                stickers = stickers,
                unlockedStickerIds = puzzleState.unlockedStickerIds
            )
        }
        composable(WordyWorldDestination.Shop.route) {
            ShopScreen(items = shopItems)
        }
        composable(WordyWorldDestination.Parents.route) {
            ParentDashboardScreen(streakCount = puzzleState.streakCount)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordyWorldTopBar(
    currentDestination: WordyWorldDestination,
    solvedCount: Int,
    totalPuzzles: Int
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = {
            Text(text = "Word Puzzle Kids • ${currentDestination.label}")
        },
        actions = {
            Text(
                text = "$solvedCount/$totalPuzzles solved",
                style = MaterialTheme.typography.labelMedium
            )
        }
    )
}

@Composable
private fun WordyWorldBottomBar(
    currentDestination: WordyWorldDestination,
    onNavigate: (WordyWorldDestination) -> Unit
) {
    NavigationBar {
        WordyWorldDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (currentDestination == destination) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(text = destination.label) }
            )
        }
    }
}

private enum class WordyWorldDestination(
    val route: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Home(
        route = "home",
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    Puzzle(
        route = "puzzle",
        label = "Puzzle",
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    ),
    Rewards(
        route = "rewards",
        label = "Rewards",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents
    ),
    Stickers(
        route = "stickers",
        label = "Stickers",
        selectedIcon = Icons.Filled.Collections,
        unselectedIcon = Icons.Outlined.Collections
    ),
    Shop(
        route = "shop",
        label = "Shop",
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront
    ),
    Parents(
        route = "parents",
        label = "Parents",
        selectedIcon = Icons.Filled.VerifiedUser,
        unselectedIcon = Icons.Outlined.VerifiedUser
    );

    companion object {
        fun fromRoute(destination: NavDestination?): WordyWorldDestination? {
            val route = destination?.route ?: return null
            return entries.firstOrNull { it.route == route }
        }
    }
}
