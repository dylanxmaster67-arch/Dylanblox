package com.example.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository
import com.example.model.AvatarConfig
import com.example.model.Block
import com.example.model.BlockType
import com.example.model.GameLevel
import com.example.model.PlayerRank
import kotlinx.coroutines.delay

@Composable
fun GamePlayScreen(
    level: GameLevel,
    repository: DylanbloxRepository,
    onBack: () -> Unit,
    onReportLevel: (GameLevel) -> Unit
) {
    val rank by repository.rank.collectAsState()
    val isGlobalFly by repository.isFlyEnabled.collectAsState()
    val isGlobalNoclip by repository.isNoclipEnabled.collectAsState()
    val avatarConfig by repository.avatarConfig.collectAsState()

    var localFly by remember { mutableStateOf(isGlobalFly) }
    var localNoclip by remember { mutableStateOf(isGlobalNoclip) }

    val physicsEngine = remember(level) {
        PhysicsEngine(level, isFlyEnabled = localFly, isNoclipEnabled = localNoclip)
    }

    var moveInput by remember { mutableFloatStateOf(0f) }
    var jumpInput by remember { mutableStateOf(false) }
    var flyUpInput by remember { mutableStateOf(false) }
    var flyDownInput by remember { mutableStateOf(false) }

    var elapsedTimeMs by remember { mutableLongStateOf(0L) }
    var isVictoryClaimed by remember { mutableStateOf(false) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var hasLikedCurrentLevel by remember { mutableStateOf(false) }
    var deathFlash by remember { mutableStateOf(false) }

    // Game Loop (Targeting smooth 60 FPS update rate: ~16ms tick)
    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            physicsEngine.isFlyEnabled = localFly
            physicsEngine.isNoclipEnabled = localNoclip

            physicsEngine.update(
                moveX = moveInput,
                jumpPressed = jumpInput,
                flyUp = flyUpInput,
                flyDown = flyDownInput
            )

            if (!physicsEngine.state.hasWon) {
                elapsedTimeMs = System.currentTimeMillis() - startTime
            } else if (!isVictoryClaimed) {
                isVictoryClaimed = true
                repository.awardGameWinKeys() // Award 450 keys!
                showVictoryDialog = true
            }

            if (physicsEngine.state.isDead) {
                deathFlash = true
                delay(300)
                physicsEngine.respawnAtCheckpoint()
                deathFlash = false
            }

            delay(16) // ~60fps
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1B))
    ) {
        // Main Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            jumpInput = true
                        }
                    )
                }
        ) {
            val tileSize = 42.dp.toPx()
            val canvasW = size.width
            val canvasH = size.height

            // Camera offset centering on player
            val playerPixelX = physicsEngine.state.x * tileSize
            val playerPixelY = physicsEngine.state.y * tileSize

            val camX = canvasW / 2f - playerPixelX
            val camY = canvasH / 2f - playerPixelY

            // Draw Background Grid / Sky
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1235), Color(0xFF090616))
                )
            )

            // Draw Blocks
            for (block in physicsEngine.currentBlocks) {
                val bx = block.x * tileSize + camX
                val by = block.y * tileSize + camY

                // Viewport culling
                if (bx + tileSize < 0 || bx > canvasW || by + tileSize < 0 || by > canvasH) continue

                drawBlock(block, bx, by, tileSize)
            }

            // Draw Player Avatar
            val px = playerPixelX + camX
            val py = playerPixelY + camY
            drawPlayerAvatar(px, py, tileSize, avatarConfig, localFly, physicsEngine.state.onGround)
        }

        // Death Flash Overlay
        if (deathFlash) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = 0.4f))
            )
        }

        // Top HUD Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back & Level Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF1E1938).copy(alpha = 0.85f), CircleShape)
                        .testTag("game_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = level.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "by ${level.author} • ${level.category}",
                        color = Color(0xFF00E5FF),
                        fontSize = 12.sp
                    )
                }
            }

            // Timer & Collected Keys HUD
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1938).copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ ${formatTimer(elapsedTimeMs)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF332A00).copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VpnKey,
                            contentDescription = "Keys",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${physicsEngine.state.collectedKeys}",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                IconButton(
                    onClick = { onReportLevel(level) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF381926).copy(alpha = 0.85f), CircleShape)
                        .testTag("report_level_button")
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Report", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                }
            }
        }

        // Admin Quick-Action Toolbar (if Admin/Owner or rank elevated)
        if (rank == PlayerRank.ADMIN || rank == PlayerRank.GAME_OWNER || localFly || localNoclip) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF18132F).copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "ADMIN TOOLS:",
                        color = Color(0xFF00E5FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { localFly = !localFly },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (localFly) Color(0xFF00E676) else Color(0xFF2C254D)
                        ),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(if (localFly) "FLY: ON" else "FLY: OFF", fontSize = 10.sp)
                    }
                    Button(
                        onClick = { localNoclip = !localNoclip },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (localNoclip) Color(0xFFFF9100) else Color(0xFF2C254D)
                        ),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(if (localNoclip) "NOCLIP: ON" else "NOCLIP: OFF", fontSize = 10.sp)
                    }
                    IconButton(
                        onClick = { physicsEngine.resetToStart() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // On-Screen Touch Controls (Mobile Platformer Pad)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            // Directional Left / Right buttons
            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Move Left
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF241C48).copy(alpha = 0.85f))
                        .border(2.dp, Color(0xFF7C4DFF), CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    moveInput = -1f
                                    tryAwaitRelease()
                                    moveInput = 0f
                                }
                            )
                        }
                        .testTag("btn_move_left"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Left",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Move Right
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF241C48).copy(alpha = 0.85f))
                        .border(2.dp, Color(0xFF7C4DFF), CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    moveInput = 1f
                                    tryAwaitRelease()
                                    moveInput = 0f
                                }
                            )
                        }
                        .testTag("btn_move_right"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Right",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Right Action Buttons (Jump / Fly Up / Down)
            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (localFly) {
                    // Fly Down Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2848).copy(alpha = 0.85f))
                            .border(2.dp, Color(0xFF00E5FF), CircleShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        flyDownInput = true
                                        tryAwaitRelease()
                                        flyDownInput = false
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Fly Down", tint = Color(0xFF00E5FF))
                    }

                    // Fly Up Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2848).copy(alpha = 0.85f))
                            .border(2.dp, Color(0xFF00E5FF), CircleShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        flyUpInput = true
                                        tryAwaitRelease()
                                        flyUpInput = false
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Fly Up", tint = Color(0xFF00E5FF))
                    }
                }

                // Big Jump Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                            )
                        )
                        .border(3.dp, Color.White, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    jumpInput = true
                                    tryAwaitRelease()
                                    jumpInput = false
                                }
                            )
                        }
                        .testTag("btn_jump"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = "Jump",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "JUMP",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Victory Dialog / Modal
        AnimatedVisibility(
            visible = showVictoryDialog,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1333)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 LEVEL CLEARED! 🏆",
                        color = Color(0xFFFFD700),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Congratulations! You conquered ${level.title}",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Keys Won Showcase
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2E2405),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFC107)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.VpnKey,
                                contentDescription = "Keys",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "+450 KEYS WON!",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Added directly to your balance 🔑",
                                    color = Color(0xFFFFECB3),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Time: ${formatTimer(elapsedTimeMs)} • Deaths: ${physicsEngine.state.deathCount}",
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rating button
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (!hasLikedCurrentLevel) {
                                    hasLikedCurrentLevel = true
                                    repository.rateLevel(level.id, true)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasLikedCurrentLevel) Color(0xFF00E676) else Color(0xFF322857)
                            )
                        ) {
                            Icon(Icons.Default.ThumbUp, contentDescription = "Like", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (hasLikedCurrentLevel) "Liked! 👍" else "Like Level 👍")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                showVictoryDialog = false
                                isVictoryClaimed = false
                                physicsEngine.resetToStart()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF45347B)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Play Again")
                        }

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back to Hub", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Drawing helper functions
private fun DrawScope.drawBlock(block: Block, x: Float, y: Float, size: Float) {
    val margin = 1f
    val bSize = size - margin * 2

    when (block.type) {
        BlockType.SOLID -> {
            val baseColor = block.colorHex?.let { parseHexColor(it) } ?: Color(0xFF7C4DFF)
            // Top highlight for 3D blocky look
            drawRect(baseColor, Offset(x + margin, y + margin), Size(bSize, bSize))
            drawRect(Color.White.copy(alpha = 0.25f), Offset(x + margin, y + margin), Size(bSize, 4f))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(x + margin, y + bSize - 4f), Size(bSize, 4f))
        }
        BlockType.LAVA -> {
            drawRect(Color(0xFFFF1744), Offset(x + margin, y + margin), Size(bSize, bSize))
            // Animated magma crack lines
            drawRect(Color(0xFFFF9100), Offset(x + 4f, y + 4f), Size(bSize - 8f, 6f))
            drawRect(Color(0xFFFFEA00), Offset(x + 8f, y + 14f), Size(bSize - 16f, 4f))
        }
        BlockType.BOUNCE -> {
            drawRect(Color(0xFF00E5FF), Offset(x + margin, y + margin), Size(bSize, bSize))
            // Draw upward arrows
            val path = Path().apply {
                moveTo(x + size / 2f, y + 6f)
                lineTo(x + size / 2f - 10f, y + size - 10f)
                lineTo(x + size / 2f + 10f, y + size - 10f)
                close()
            }
            drawPath(path, Color.White)
        }
        BlockType.SPEED -> {
            drawRect(Color(0xFFFF6D00), Offset(x + margin, y + margin), Size(bSize, bSize))
            // Speed chevrons
            val path = Path().apply {
                moveTo(x + size - 8f, y + size / 2f)
                lineTo(x + 10f, y + 8f)
                lineTo(x + 10f, y + size - 8f)
                close()
            }
            drawPath(path, Color.White)
        }
        BlockType.ICE -> {
            drawRect(Color(0xFF80D8FF).copy(alpha = 0.85f), Offset(x + margin, y + margin), Size(bSize, bSize))
            drawRect(Color.White.copy(alpha = 0.5f), Offset(x + margin, y + margin), Size(bSize, 3f))
        }
        BlockType.KEY_PICKUP -> {
            // Golden Key
            drawCircle(Color(0xFFFFD700), radius = bSize * 0.28f, center = Offset(x + size / 2f, y + size * 0.35f))
            drawCircle(Color(0xFF2E2405), radius = bSize * 0.12f, center = Offset(x + size / 2f, y + size * 0.35f))
            drawRect(Color(0xFFFFD700), Offset(x + size / 2f - 3f, y + size * 0.45f), Size(6f, bSize * 0.45f))
            drawRect(Color(0xFFFFD700), Offset(x + size / 2f + 3f, y + size * 0.7f), Size(6f, 5f))
        }
        BlockType.CHECKPOINT -> {
            // Checkpoint Flag
            drawLine(Color.White, Offset(x + 8f, y + size - 4f), Offset(x + 8f, y + 6f), strokeWidth = 4f)
            val flagPath = Path().apply {
                moveTo(x + 10f, y + 6f)
                lineTo(x + size - 8f, y + 14f)
                lineTo(x + 10f, y + 22f)
                close()
            }
            drawPath(flagPath, Color(0xFF00E676))
        }
        BlockType.FINISH_PORTAL -> {
            // Radiant Golden Victory Portal
            drawCircle(Color(0xFFFFD700).copy(alpha = 0.35f), radius = bSize * 0.6f, center = Offset(x + size / 2f, y + size / 2f))
            drawCircle(Color(0xFFFFC107), radius = bSize * 0.45f, center = Offset(x + size / 2f, y + size / 2f))
            drawCircle(Color(0xFFFFF8E1), radius = bSize * 0.25f, center = Offset(x + size / 2f, y + size / 2f))
        }
        BlockType.AIR -> {}
    }
}

private fun DrawScope.drawPlayerAvatar(
    x: Float,
    y: Float,
    tileSize: Float,
    avatar: AvatarConfig,
    isFlying: Boolean,
    onGround: Boolean
) {
    val skinColor = parseHexColor(avatar.skinColor)
    val torsoColor = parseHexColor(avatar.torsoColor)
    val legsColor = parseHexColor(avatar.legsColor)

    val avatarW = tileSize * 0.7f
    val avatarH = tileSize * 0.95f

    // Cape / Wings behind avatar if equipped
    if (avatar.cape != null) {
        val capePath = Path().apply {
            moveTo(x + avatarW * 0.2f, y + avatarH * 0.35f)
            lineTo(x - 12f, y + avatarH * 0.9f)
            lineTo(x + avatarW * 0.5f, y + avatarH * 0.8f)
            close()
        }
        drawPath(capePath, Color(0xFFFFD700))
    }

    // Legs
    drawRect(legsColor, Offset(x + 2f, y + avatarH * 0.65f), Size(avatarW * 0.42f, avatarH * 0.35f))
    drawRect(legsColor, Offset(x + avatarW * 0.52f, y + avatarH * 0.65f), Size(avatarW * 0.42f, avatarH * 0.35f))

    // Torso (Blocky)
    drawRect(torsoColor, Offset(x, y + avatarH * 0.32f), Size(avatarW, avatarH * 0.35f))
    drawRect(Color.White.copy(alpha = 0.15f), Offset(x, y + avatarH * 0.32f), Size(avatarW, 3f))

    // Head (Cube)
    val headSize = avatarW * 0.9f
    val headX = x + (avatarW - headSize) / 2f
    val headY = y
    drawRect(skinColor, Offset(headX, headY), Size(headSize, headSize * 0.85f))

    // Eyes & Smile
    drawRect(Color(0xFF1E88E5), Offset(headX + 4f, headY + 8f), Size(5f, 7f))
    drawRect(Color(0xFF1E88E5), Offset(headX + headSize - 9f, headY + 8f), Size(5f, 7f))
    drawRect(Color(0xFF212121), Offset(headX + headSize * 0.35f, headY + headSize * 0.55f), Size(headSize * 0.3f, 3f))

    // Hat / Crown on top
    if (avatar.hat.contains("Crown", ignoreCase = true)) {
        val crownPath = Path().apply {
            moveTo(headX - 2f, headY + 2f)
            lineTo(headX - 2f, headY - 10f)
            lineTo(headX + headSize * 0.25f, headY - 4f)
            lineTo(headX + headSize * 0.5f, headY - 12f)
            lineTo(headX + headSize * 0.75f, headY - 4f)
            lineTo(headX + headSize + 2f, headY - 10f)
            lineTo(headX + headSize + 2f, headY + 2f)
            close()
        }
        drawPath(crownPath, Color(0xFFFFD700))
        drawCircle(Color(0xFF00E5FF), radius = 2f, center = Offset(headX + headSize * 0.5f, headY - 9f))
    }

    // Flight Glow
    if (isFlying) {
        drawCircle(Color(0xFF00E5FF).copy(alpha = 0.3f), radius = avatarW * 0.8f, center = Offset(x + avatarW / 2f, y + avatarH / 2f))
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorInt)
        } else {
            Color(colorInt)
        }
    } catch (_: Exception) {
        Color(0xFFFFD700)
    }
}

private fun formatTimer(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (ms % 1000) / 100
    return String.format("%02d:%02d.%d", minutes, seconds, tenths)
}
