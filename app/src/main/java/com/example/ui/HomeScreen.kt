package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DylanbloxRepository
import com.example.data.PreloadedData
import com.example.model.GameLevel
import com.example.model.PlayerRank

@Composable
fun HomeScreen(
    repository: DylanbloxRepository,
    onPlayLevel: (GameLevel) -> Unit,
    onCreateLevel: () -> Unit,
    onTriggerCopyrightAttempt: () -> Unit
) {
    val levels by repository.levels.collectAsState()
    val rank by repository.rank.collectAsState()
    val keys by repository.keys.collectAsState()
    val broadcast by repository.serverBroadcast.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Dylan's Official", "Obby", "Parkour", "Lava Escape", "Maze", "Speedrun")

    val filteredLevels = levels.filter { level ->
        val matchesSearch = level.title.contains(searchQuery, ignoreCase = true) ||
                level.author.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Dylan's Official" -> level.isOfficial
            else -> level.category.equals(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1E))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Server Broadcast Alert if active
            if (broadcast != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFF1744).copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📢 $broadcast",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { repository.clearBroadcast() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Hero Banner Showcase
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dylanblox_hero_banner),
                        contentDescription = "Dylanblox Universe Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF0F0C1E))
                                )
                            )
                    )
                    // Floating Copyright Anti-Copy Button for testing Easter Egg
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clickable { onTriggerCopyrightAttempt() },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Game", tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Game Code", color = Color(0xFF00E5FF), fontSize = 11.sp)
                        }
                    }
                }
            }

            // Profile Card: @dylaniscool67 YouTube Channel Spotlight
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E173A)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD700))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFFFFD700), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_dylanblox_icon),
                                contentDescription = "Dylan Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Dylan",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified Creator",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFF1744)
                                ) {
                                    Text(
                                        "OWNER",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "YouTube: ${PreloadedData.DYLAN_YOUTUBE_HANDLE}",
                                color = Color(0xFFFF80AB),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Official Dylanblox universe developer & creator",
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PreloadedData.DYLAN_YOUTUBE_URL))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("YouTube", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Search Bar & Discover Header
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_levels_input"),
                        placeholder = { Text("Search community obbies & games...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E173A),
                            unfocusedContainerColor = Color(0xFF18122E),
                            focusedBorderColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color(0xFF2E2652),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filter Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (cat in categories) {
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF1E173A),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF332958)
                                ),
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.White else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Community Levels List
            items(filteredLevels) { level ->
                LevelItemCard(
                    level = level,
                    onPlay = { onPlayLevel(level) }
                )
            }
        }

        // Floating Action Button to Create Level
        FloatingActionButton(
            onClick = onCreateLevel,
            containerColor = Color(0xFFFFD700),
            contentColor = Color.Black,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 84.dp, end = 20.dp)
                .testTag("fab_create_level")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Level")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (rank == PlayerRank.PREMIUM || rank == PlayerRank.GAME_OWNER) "Create (FREE)" else "Create (67 🔑)",
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun LevelItemCard(
    level: GameLevel,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onPlay() }
            .testTag("level_card_${level.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1635)),
        border = androidx.compose.foundation.BorderStroke(
            width = if (level.isOfficial) 1.5.dp else 1.dp,
            color = if (level.isOfficial) Color(0xFFFFD700) else Color(0xFF2C234E)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = level.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (level.isOfficial) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Verified, contentDescription = "Official", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getDifficultyColor(level.difficulty).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, getDifficultyColor(level.difficulty))
                ) {
                    Text(
                        text = level.difficulty,
                        color = getDifficultyColor(level.difficulty),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "by ${level.author} • Category: ${level.category}",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = level.description,
                color = Color.LightGray,
                fontSize = 12.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Social stats & Play button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${level.rating}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "Likes", tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${level.likes}", color = Color.White, fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = "Plays", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${level.plays}", color = Color.LightGray, fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PLAY (+450 🔑)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun getDifficultyColor(diff: String): Color {
    return when (diff.lowercase()) {
        "easy" -> Color(0xFF00E676)
        "medium" -> Color(0xFFFFC107)
        "hard" -> Color(0xFFFF5722)
        else -> Color(0xFFFF1744)
    }
}
