package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository
import com.example.editor.LevelEditorScreen
import com.example.game.GamePlayScreen
import com.example.model.GameLevel
import com.example.model.PlayerRank
import com.example.ui.AdminConsoleScreen
import com.example.ui.AvatarScreen
import com.example.ui.CopyrightProtectionDialog
import com.example.ui.HomeScreen
import com.example.ui.LeaderboardScreen
import com.example.ui.RedeemCodeDialog
import com.example.ui.ReportDialog
import com.example.ui.SettingsScreen
import com.example.ui.ShopScreen
import com.example.ui.SocialChatScreen
import com.example.ui.theme.DylanbloxTheme

enum class MainNavTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Home", Icons.Default.Home),
    SHOP("Shop", Icons.Default.ShoppingBag),
    AVATAR("Avatar", Icons.Default.Person),
    SOCIAL("Chat & Voice", Icons.Default.Chat),
    LEADERBOARD("Ranks", Icons.Default.EmojiEvents),
    ADMIN("Admin", Icons.Default.Terminal),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: DylanbloxRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository = DylanbloxRepository(applicationContext)

        setContent {
            DylanbloxTheme {
                DylanbloxApp(repository = repository)
            }
        }
    }
}

@Composable
fun DylanbloxApp(repository: DylanbloxRepository) {
    var currentTab by remember { mutableStateOf(MainNavTab.HOME) }
    var activePlayingLevel by remember { mutableStateOf<GameLevel?>(null) }
    var isEditingLevel by remember { mutableStateOf(false) }

    var showRedeemDialog by remember { mutableStateOf(false) }
    var showCopyrightDialog by remember { mutableStateOf(false) }
    var reportTargetInfo by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair(Type, Name)

    val keys by repository.keys.collectAsState()
    val rank by repository.rank.collectAsState()

    // Handle back button presses gracefully
    BackHandler(enabled = activePlayingLevel != null || isEditingLevel || currentTab != MainNavTab.HOME) {
        when {
            activePlayingLevel != null -> activePlayingLevel = null
            isEditingLevel -> isEditingLevel = false
            currentTab != MainNavTab.HOME -> currentTab = MainNavTab.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0F0C1E),
        topBar = {
            if (activePlayingLevel == null && !isEditingLevel) {
                DylanbloxTopBar(
                    keys = keys,
                    rank = rank,
                    onOpenRedeem = { showRedeemDialog = true }
                )
            }
        },
        bottomBar = {
            if (activePlayingLevel == null && !isEditingLevel) {
                DylanbloxBottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                activePlayingLevel != null -> {
                    GamePlayScreen(
                        level = activePlayingLevel!!,
                        repository = repository,
                        onBack = { activePlayingLevel = null },
                        onReportLevel = { lvl ->
                            reportTargetInfo = Pair("LEVEL", lvl.title)
                        }
                    )
                }

                isEditingLevel -> {
                    LevelEditorScreen(
                        repository = repository,
                        onBack = { isEditingLevel = false },
                        onTestPlay = { testLvl ->
                            activePlayingLevel = testLvl
                        }
                    )
                }

                else -> {
                    when (currentTab) {
                        MainNavTab.HOME -> HomeScreen(
                            repository = repository,
                            onPlayLevel = { lvl -> activePlayingLevel = lvl },
                            onCreateLevel = { isEditingLevel = true },
                            onTriggerCopyrightAttempt = { showCopyrightDialog = true }
                        )

                        MainNavTab.SHOP -> ShopScreen(repository = repository)

                        MainNavTab.AVATAR -> AvatarScreen(
                            repository = repository,
                            onNavigateToShop = { currentTab = MainNavTab.SHOP }
                        )

                        MainNavTab.SOCIAL -> SocialChatScreen(repository = repository)

                        MainNavTab.LEADERBOARD -> LeaderboardScreen(repository = repository)

                        MainNavTab.ADMIN -> AdminConsoleScreen(repository = repository)

                        MainNavTab.SETTINGS -> SettingsScreen(
                            repository = repository,
                            onTriggerCopyrightAttempt = { showCopyrightDialog = true }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showRedeemDialog) {
        RedeemCodeDialog(
            repository = repository,
            onDismiss = { showRedeemDialog = false }
        )
    }

    if (showCopyrightDialog) {
        CopyrightProtectionDialog(
            onDismiss = { showCopyrightDialog = false }
        )
    }

    if (reportTargetInfo != null) {
        ReportDialog(
            targetType = reportTargetInfo!!.first,
            targetName = reportTargetInfo!!.second,
            repository = repository,
            onDismiss = { reportTargetInfo = null }
        )
    }
}

@Composable
fun DylanbloxTopBar(
    keys: Long,
    rank: PlayerRank,
    onOpenRedeem: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF140E29),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF281C50))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding & Crown
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF7C4DFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DYLANBLOX",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Official Sandbox",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Keys Counter & Redeem Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rank Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(rank.badgeColorHex).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(rank.badgeColorHex))
                ) {
                    Text(
                        text = rank.displayName,
                        color = Color(rank.badgeColorHex),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // Keys Balance Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2B2005),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VpnKey,
                            contentDescription = "Keys",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatKeyNumber(keys),
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                // Redeem Codes Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF00E5FF),
                    modifier = Modifier
                        .clickable { onOpenRedeem() }
                        .testTag("redeem_code_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = "Redeem",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Codes",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DylanbloxBottomNavBar(
    currentTab: MainNavTab,
    onTabSelected: (MainNavTab) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF130E26),
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(64.dp)
    ) {
        for (tab in MainNavTab.values()) {
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFD700),
                    selectedTextColor = Color(0xFFFFD700),
                    indicatorColor = Color(0xFF2C1E55),
                    unselectedIconColor = Color(0xFFA5A0CB),
                    unselectedTextColor = Color(0xFFA5A0CB)
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

private fun formatKeyNumber(amount: Long): String {
    return when {
        amount >= 1000000000L -> "${amount / 1000000000L}B"
        amount >= 1000000L -> "${amount / 1000000L}M"
        amount >= 1000L -> "${amount / 1000L}k"
        else -> "$amount"
    }
}
