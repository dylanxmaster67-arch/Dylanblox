package com.example.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DylanbloxRepository
import com.example.model.Block
import com.example.model.BlockType
import com.example.model.GameLevel
import com.example.model.PlayerRank
import kotlinx.coroutines.delay

@Composable
fun LevelEditorScreen(
    repository: DylanbloxRepository,
    onBack: () -> Unit,
    onTestPlay: (GameLevel) -> Unit
) {
    val rank by repository.rank.collectAsState()
    val keys by repository.keys.collectAsState()
    val coopEvents by repository.coopEvents.collectAsState()

    val levelWidth = 36
    val levelHeight = 16

    val blocks = remember {
        mutableStateListOf<Block>().apply {
            // Initial spawn ground
            for (x in 0..4) add(Block(x, 13, BlockType.SOLID, "#7C4DFF"))
            add(Block(32, 10, BlockType.FINISH_PORTAL, "#FFD700"))
        }
    }

    var selectedTool by remember { mutableStateOf<BlockType?>(BlockType.SOLID) }
    var isEraser by remember { mutableStateOf(false) }
    var selectedColorHex by remember { mutableStateOf("#7C4DFF") }

    var cameraOffsetX by remember { mutableFloatStateOf(0f) }
    var isCoopModeActive by remember { mutableStateOf(true) }
    var showPublishDialog by remember { mutableStateOf(false) }
    var publishStatusMessage by remember { mutableStateOf<String?>(null) }

    // Simulated Co-Op Real-time Collaborators
    LaunchedEffect(isCoopModeActive) {
        if (!isCoopModeActive) return@LaunchedEffect
        var step = 0
        while (true) {
            delay(5000)
            step++
            val coopX = 8 + (step * 3) % 20
            val coopY = 12 - (step % 4)
            if (step % 2 == 0) {
                // Dylan or peer places a block
                blocks.removeAll { it.x == coopX && it.y == coopY }
                blocks.add(Block(coopX, coopY, BlockType.BOUNCE, "#00E5FF"))
                repository.logCoopAction("Dylan @dylaniscool67 placed Bounce Pad at ($coopX, $coopY)")
            } else {
                blocks.removeAll { it.x == coopX && it.y == coopY }
                blocks.add(Block(coopX, coopY, BlockType.SOLID, "#FFD700"))
                repository.logCoopAction("SkyCrafter_99 placed Golden Brick at ($coopX, $coopY)")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C1E))
    ) {
        // Grid Editor Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(selectedTool, isEraser, selectedColorHex) {
                    detectTapGestures { offset ->
                        val cellSize = 44.dp.toPx()
                        val gridX = ((offset.x - cameraOffsetX) / cellSize).toInt()
                        val gridY = (offset.y / cellSize).toInt()

                        if (gridX in 0 until levelWidth && gridY in 0 until levelHeight) {
                            blocks.removeAll { it.x == gridX && it.y == gridY }
                            if (!isEraser && selectedTool != null && selectedTool != BlockType.AIR) {
                                blocks.add(Block(gridX, gridY, selectedTool!!, selectedColorHex))
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        cameraOffsetX = (cameraOffsetX + dragAmount.x).coerceIn(-1200f, 100f)
                    }
                }
        ) {
            val cellSize = 44.dp.toPx()

            // Draw Background grid lines
            for (gx in 0..levelWidth) {
                val px = gx * cellSize + cameraOffsetX
                drawLine(Color(0xFF261E42), Offset(px, 0f), Offset(px, levelHeight * cellSize), strokeWidth = 1f)
            }
            for (gy in 0..levelHeight) {
                val py = gy * cellSize
                drawLine(Color(0xFF261E42), Offset(cameraOffsetX, py), Offset(levelWidth * cellSize + cameraOffsetX, py), strokeWidth = 1f)
            }

            // Draw Placed Blocks
            for (block in blocks) {
                val px = block.x * cellSize + cameraOffsetX
                val py = block.y * cellSize
                val bSize = cellSize - 2f

                when (block.type) {
                    BlockType.SOLID -> {
                        val color = parseHex(block.colorHex ?: "#7C4DFF")
                        drawRect(color, Offset(px + 1f, py + 1f), Size(bSize, bSize))
                        drawRect(Color.White.copy(alpha = 0.25f), Offset(px + 1f, py + 1f), Size(bSize, 4f))
                    }
                    BlockType.LAVA -> {
                        drawRect(Color(0xFFFF1744), Offset(px + 1f, py + 1f), Size(bSize, bSize))
                        drawRect(Color(0xFFFF9100), Offset(px + 4f, py + 4f), Size(bSize - 8f, 6f))
                    }
                    BlockType.BOUNCE -> {
                        drawRect(Color(0xFF00E5FF), Offset(px + 1f, py + 1f), Size(bSize, bSize))
                    }
                    BlockType.SPEED -> {
                        drawRect(Color(0xFFFF6D00), Offset(px + 1f, py + 1f), Size(bSize, bSize))
                    }
                    BlockType.ICE -> {
                        drawRect(Color(0xFF80D8FF), Offset(px + 1f, py + 1f), Size(bSize, bSize))
                    }
                    BlockType.KEY_PICKUP -> {
                        drawCircle(Color(0xFFFFD700), radius = bSize * 0.35f, center = Offset(px + cellSize / 2f, py + cellSize / 2f))
                    }
                    BlockType.CHECKPOINT -> {
                        drawLine(Color.White, Offset(px + 8f, py + cellSize - 4f), Offset(px + 8f, py + 4f), strokeWidth = 4f)
                        drawRect(Color(0xFF00E676), Offset(px + 10f, py + 6f), Size(18f, 12f))
                    }
                    BlockType.FINISH_PORTAL -> {
                        drawCircle(Color(0xFFFFD700), radius = bSize * 0.45f, center = Offset(px + cellSize / 2f, py + cellSize / 2f))
                        drawCircle(Color(0xFFFFF9C4), radius = bSize * 0.25f, center = Offset(px + cellSize / 2f, py + cellSize / 2f))
                    }
                    BlockType.AIR -> {}
                }
            }

            // Draw Co-Op Partner Avatars if active
            if (isCoopModeActive) {
                val dylanX = 14 * cellSize + cameraOffsetX
                val dylanY = 10 * cellSize
                drawCircle(Color(0xFFFFD700), radius = 18f, center = Offset(dylanX, dylanY))
            }
        }

        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFF221A44), CircleShape)
                        .testTag("editor_back_btn")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Visual Level Editor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCoopModeActive) Color(0xFF004D40) else Color(0xFF37474F)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Group, contentDescription = "Coop", tint = Color(0xFF00E5FF), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isCoopModeActive) "Co-Op: Dylan & 2 Online" else "Solo Build",
                                    color = Color(0xFF00E5FF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Right Action Buttons: Test Play & Publish
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val testLevel = GameLevel(
                            id = "test_run",
                            title = "Test Run (Editor)",
                            author = "You",
                            description = "Testing jumps and physics",
                            blocks = blocks.toList(),
                            width = levelWidth,
                            height = levelHeight
                        )
                        onTestPlay(testLevel)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    modifier = Modifier.testTag("test_play_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showPublishDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    modifier = Modifier.testTag("publish_level_btn")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Publish", tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publish", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Co-Op Event Banner at Top
        if (isCoopModeActive && coopEvents.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF16122C).copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
            ) {
                Text(
                    text = "📡 " + coopEvents.first(),
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Bottom Palette & Tools Tray
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFF15102A).copy(alpha = 0.95f))
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            // Palette Blocks Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Eraser tool
                PaletteItem(
                    label = "Eraser",
                    color = Color(0xFFD32F2F),
                    isSelected = isEraser,
                    onClick = {
                        isEraser = true
                        selectedTool = null
                    }
                )

                // Blocks
                val paletteList = listOf(
                    Pair(BlockType.SOLID, "Brick"),
                    Pair(BlockType.LAVA, "Lava"),
                    Pair(BlockType.BOUNCE, "Bounce"),
                    Pair(BlockType.SPEED, "Speed"),
                    Pair(BlockType.ICE, "Ice"),
                    Pair(BlockType.KEY_PICKUP, "Key 🔑"),
                    Pair(BlockType.CHECKPOINT, "Flag 🚩"),
                    Pair(BlockType.FINISH_PORTAL, "Portal 🌀")
                )

                for ((type, name) in paletteList) {
                    PaletteItem(
                        label = name,
                        color = getPaletteColor(type),
                        isSelected = !isEraser && selectedTool == type,
                        onClick = {
                            isEraser = false
                            selectedTool = type
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Colors selector & clear button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val colors = listOf("#7C4DFF", "#00E5FF", "#FFD700", "#FF1744", "#00E676", "#FF9100", "#FFFFFF")
                    for (c in colors) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(parseHex(c))
                                .border(
                                    width = if (selectedColorHex == c) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = c }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            isCoopModeActive = !isCoopModeActive
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCoopModeActive) Color(0xFF00796B) else Color(0xFF37474F)
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(if (isCoopModeActive) "Co-Op ON" else "Co-Op OFF", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = {
                            if (blocks.isNotEmpty()) blocks.removeAt(blocks.size - 1)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo", tint = Color.White)
                    }
                }
            }
        }

        // Publish Dialog Modal
        if (showPublishDialog) {
            val cost = repository.getLevelCreationCost()
            var titleInput by remember { mutableStateOf("") }
            var descInput by remember { mutableStateOf("") }
            var categoryInput by remember { mutableStateOf("Obby") }
            var difficultyInput by remember { mutableStateOf("Medium") }

            AlertDialog(
                onDismissRequest = { showPublishDialog = false },
                containerColor = Color(0xFF1E173E),
                title = {
                    Text(
                        "Publish Level to Community",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Share your game creation with the Dylanblox community! Other players will be able to play and rate it.",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )

                        // Pricing alert
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (cost == 0) Color(0xFF1B3E2B) else Color(0xFF3E2D12),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (cost == 0) Color(0xFF00E676) else Color(0xFFFFB300))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                if (cost == 0) {
                                    Text(
                                        "PREMIUM PERK: Publishing is 100% FREE! (0 Keys)",
                                        color = Color(0xFF00E676),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    Text(
                                        "Creation Fee: 67 Keys (Your balance: $keys keys)",
                                        color = Color(0xFFFFD700),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Level Name") },
                            placeholder = { Text("e.g. Dylan's Mega Parkour") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text("Description") },
                            placeholder = { Text("Jump over lava and grab keys!") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (publishStatusMessage != null) {
                            Text(
                                text = publishStatusMessage!!,
                                color = Color(0xFFFF5252),
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val (success, message) = repository.publishLevel(
                                title = titleInput,
                                description = descInput,
                                difficulty = difficultyInput,
                                category = categoryInput,
                                blocks = blocks.toList()
                            )
                            if (success) {
                                showPublishDialog = false
                                onBack()
                            } else {
                                publishStatusMessage = message
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Text("Confirm & Publish ($cost Keys)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPublishDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
private fun PaletteItem(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.3f) else Color(0xFF231C42),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else Color.Transparent
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun getPaletteColor(type: BlockType): Color {
    return when (type) {
        BlockType.SOLID -> Color(0xFF7C4DFF)
        BlockType.LAVA -> Color(0xFFFF1744)
        BlockType.BOUNCE -> Color(0xFF00E5FF)
        BlockType.SPEED -> Color(0xFFFF6D00)
        BlockType.ICE -> Color(0xFF80D8FF)
        BlockType.KEY_PICKUP -> Color(0xFFFFD700)
        BlockType.CHECKPOINT -> Color(0xFF00E676)
        BlockType.FINISH_PORTAL -> Color(0xFFFFD700)
        BlockType.AIR -> Color.Gray
    }
}

private fun parseHex(hex: String): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) Color(0xFF000000 or colorInt) else Color(colorInt)
    } catch (_: Exception) {
        Color(0xFF7C4DFF)
    }
}
