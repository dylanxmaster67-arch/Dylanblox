package com.example.model

enum class BlockType(val displayName: String, val isSolid: Boolean, val isDeadly: Boolean) {
    AIR("Air", false, false),
    SOLID("Solid Brick", true, false),
    LAVA("Lava / Hazard", false, true),
    BOUNCE("Bounce Pad", true, false),
    SPEED("Speed Booster", true, false),
    ICE("Ice Block", true, false),
    KEY_PICKUP("Key Item", false, false),
    CHECKPOINT("Checkpoint Flag", false, false),
    FINISH_PORTAL("Victory Portal", false, false)
}

data class Block(
    val x: Int,
    val y: Int,
    val type: BlockType,
    val colorHex: String? = null
)

data class GameLevel(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val width: Int = 40,
    val height: Int = 18,
    val blocks: List<Block>,
    val likes: Int = 120,
    val rating: Float = 4.8f,
    val plays: Int = 1350,
    val difficulty: String = "Medium",
    val category: String = "Obby",
    val isOfficial: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PlayerRank(val displayName: String, val badgeColorHex: Long) {
    MEMBER("Player", 0xFF9E9E9E),
    PREMIUM("Premium", 0xFFFFD700),
    ADMIN("Admin", 0xFF00E5FF),
    GAME_OWNER("Game Owner", 0xFFFF1744)
}

data class AvatarConfig(
    val skinColor: String = "#FFE0BD",
    val hat: String = "Crown of Dylan",
    val face: String = "Cool Gamer Smile",
    val torsoColor: String = "#7C4DFF",
    val legsColor: String = "#212121",
    val cape: String = "Golden Wings",
    val trailEffect: String = "Star Sparkles"
)

enum class ShopCategory(val title: String) {
    HATS("Hats & Crowns"),
    CAPES("Capes & Wings"),
    TRAILS("Effects & Trails"),
    ACCESSORIES("Accessories"),
    CUSTOM_UGC("Player UGC Creations")
}

data class ShopItem(
    val id: String,
    val name: String,
    val category: ShopCategory,
    val basePrice: Int, // between 12 and 2000 keys
    val description: String,
    val creator: String = "@dylaniscool67",
    val isCustom: Boolean = false,
    val colorHex: String = "#FFD700"
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val senderRank: PlayerRank,
    val content: String,
    val channel: String = "GLOBAL", // GLOBAL, COOP, ADMIN, DM
    val recipient: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isSystem: Boolean = false
)

data class VoiceParticipant(
    val username: String,
    val rank: PlayerRank,
    val isMuted: Boolean = false,
    val isSpeaking: Boolean = false,
    val volume: Float = 0.8f
)

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val score: Long,
    val badge: String,
    val playerRank: PlayerRank
)

data class ReportTicket(
    val id: String,
    val targetType: String, // "LEVEL" or "PLAYER"
    val targetName: String,
    val reportedBy: String,
    val reason: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    var status: String = "PENDING"
)

data class ServerSettings(
    val region: String = "North America (US-East)",
    val targetFps: Int = 60, // 1 to 60 FPS range
    val inputMode: String = "Touch / Mobile", // Touch, Console/Gamepad, PC WASD
    val noiseSuppressionEnabled: Boolean = true,
    val lowLatencyNetcode: Boolean = true,
    val hapticFeedback: Boolean = true,
    val themeName: String = "Neon Cyber",
    val antiCheatActive: Boolean = true
)
