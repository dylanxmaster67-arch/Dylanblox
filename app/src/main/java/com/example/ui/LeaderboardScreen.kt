package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository

@Composable
fun LeaderboardScreen(
    repository: DylanbloxRepository
) {
    val leaderboard by repository.leaderboard.collectAsState()
    val userKeys by repository.keys.collectAsState()
    val username by repository.username.collectAsState()
    val rank by repository.rank.collectAsState()

    var activeTab by remember { mutableStateOf("KEYS") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Global Hall of Fame",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(22.dp))
                    }
                    Text(
                        text = "Real-time achievements & top players",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Your Rank Ribbon Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261D4A)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF7C4DFF))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7C4DFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("YOU", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("[${rank.displayName}]", color = Color(rank.badgeColorHex), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Global Standing: Top 1%", color = Color(0xFF00E5FF), fontSize = 11.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$userKeys", color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                }
            }

            // Global Leaderboard Entries
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(leaderboard) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (entry.rank) {
                                1 -> Color(0xFF2E2406)
                                2 -> Color(0xFF242332)
                                3 -> Color(0xFF2C1E14)
                                else -> Color(0xFF1B1536)
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = when (entry.rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFE0E0E0)
                                3 -> Color(0xFFCD7F32)
                                else -> Color(0xFF2A2150)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank Number badge
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (entry.rank) {
                                            1 -> Color(0xFFFFD700)
                                            2 -> Color(0xFFB0BEC5)
                                            3 -> Color(0xFFCD7F32)
                                            else -> Color(0xFF382C62)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${entry.rank}",
                                    color = if (entry.rank in 1..3) Color.Black else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = entry.username,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = entry.badge,
                                        color = Color(0xFF00E5FF),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Rank: ${entry.playerRank.displayName}",
                                    color = Color(entry.playerRank.badgeColorHex),
                                    fontSize = 11.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatBigScore(entry.score),
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatBigScore(score: Long): String {
    return when {
        score >= 1000000000L -> "${score / 1000000000L}B"
        score >= 1000000L -> "${score / 1000000L}M"
        score >= 1000L -> "${score / 1000L}k"
        else -> "$score"
    }
}
