package com.example.data

import com.example.model.Block
import com.example.model.BlockType
import com.example.model.GameLevel
import com.example.model.LeaderboardEntry
import com.example.model.PlayerRank
import com.example.model.ShopCategory
import com.example.model.ShopItem

object PreloadedData {

    const val DYLAN_YOUTUBE_HANDLE = "@dylaniscool67"
    const val DYLAN_YOUTUBE_URL = "https://youtube.com/@dylaniscool67"
    const val COPYRIGHT_NOTICE = "Nice try dude! Dylanblox is copyright protected by @dylaniscool67."

    fun getInitialShopItems(): List<ShopItem> {
        return listOf(
            // Hats (12 to 2000 keys)
            ShopItem("item_hat_1", "Dylan's Golden Crown", ShopCategory.HATS, 2000, "The iconic royal crown of @dylaniscool67 with glowing diamond gems.", "@dylaniscool67", false, "#FFD700"),
            ShopItem("item_hat_2", "Cyber Neon Visor", ShopCategory.HATS, 850, "Futuristic holographic visor with dynamic scanning lines.", "CyberMaster", false, "#00E5FF"),
            ShopItem("item_hat_3", "Pixel Cap", ShopCategory.HATS, 12, "Budget-friendly starter cap for stylish new players.", "DylanbloxStyle", false, "#E91E63"),
            ShopItem("item_hat_4", "Galaxy Fedora", ShopCategory.HATS, 420, "Woven with cosmic stardust and nebulas.", "StarGazer", false, "#7C4DFF"),
            ShopItem("item_hat_5", "Dino Hat", ShopCategory.HATS, 120, "Roar through every obby with a cute green dino hood.", "BlockyRex", false, "#4CAF50"),

            // Capes & Wings
            ShopItem("item_cape_1", "Dragon Flame Wings", ShopCategory.CAPES, 1800, "Blazing crimson wings forged in Dylan's hardest lava levels.", "@dylaniscool67", false, "#FF3D00"),
            ShopItem("item_cape_2", "Golden Dylan Cape", ShopCategory.CAPES, 1500, "Pure woven gold cape commemorating the official channel launch.", "@dylaniscool67", false, "#FFC107"),
            ShopItem("item_cape_3", "Shadow Phantom Cloak", ShopCategory.CAPES, 650, "Allows mysterious ninja stealth trail while jumping.", "NinjaGhost", false, "#37474F"),
            ShopItem("item_cape_4", "Speedster Jetpack", ShopCategory.CAPES, 950, "Mini dual-thrusters mounted on your back.", "AeroTech", false, "#00B0FF"),
            ShopItem("item_cape_5", "Basic Red Scarf", ShopCategory.CAPES, 45, "Warm knit red scarf for frosty mountain courses.", "CozyCrafter", false, "#D32F2F"),

            // Trails & Effects
            ShopItem("item_trail_1", "Rainbow Stardust Trail", ShopCategory.TRAILS, 1100, "Leave an enchanting rainbow wake behind every jump.", "@dylaniscool67", false, "#E040FB"),
            ShopItem("item_trail_2", "Lava Ember Sparkles", ShopCategory.TRAILS, 550, "Fiery sparks erupt under your feet.", "Pyromancer", false, "#FF6D00"),
            ShopItem("item_trail_3", "Neon Matrix Glitch", ShopCategory.TRAILS, 700, "Binary code digits float around your avatar.", "GlitchHacker", false, "#00E676"),
            ShopItem("item_trail_4", "Pixel Hearts Trail", ShopCategory.TRAILS, 280, "Cute floating 8-bit hearts trail.", "SweetBytes", false, "#FF4081"),
            ShopItem("item_trail_5", "Dust Cloud Trail", ShopCategory.TRAILS, 25, "Classic retro cartoon sprint dust.", "RetroFan", false, "#BDBDBD"),

            // Accessories
            ShopItem("item_acc_1", "Emerald Katana", ShopCategory.ACCESSORIES, 1400, "Legendary glowing blade of the Dylanblox arena champion.", "@dylaniscool67", false, "#00E676"),
            ShopItem("item_acc_2", "Retro Boombox", ShopCategory.ACCESSORIES, 490, "Blasts blocky 8-bit chiptune beats wherever you walk.", "DJ_Block", false, "#FF9100"),
            ShopItem("item_acc_3", "Shoulder Pet Dylan Dog", ShopCategory.ACCESSORIES, 890, "Loyal blocky companion sitting on your left shoulder.", "@dylaniscool67", false, "#8D6E63"),
            ShopItem("item_acc_4", "Cool Pixel Shades", ShopCategory.ACCESSORIES, 75, "Deal with it - iconic blocky black sunglasses.", "DylanbloxStyle", false, "#212121"),
            ShopItem("item_acc_5", "Golden Key Necklace", ShopCategory.ACCESSORIES, 320, "Shiny mini key pendant around your neck.", "KeyKeeper", false, "#FFD700")
        )
    }

    fun getInitialLevels(): List<GameLevel> {
        return listOf(
            createDylanMasterLevel(),
            createRainbowObbyLevel(),
            createLavaEscapeLevel(),
            createSpeedrunGalaxyLevel(),
            createPuzzleKeyMaze()
        )
    }

    private fun createDylanMasterLevel(): GameLevel {
        val blocks = mutableListOf<Block>()
        // Ground start
        for (x in 0..6) {
            blocks.add(Block(x, 15, BlockType.SOLID, "#7C4DFF"))
        }
        // First jump over lava pit
        for (x in 7..9) {
            blocks.add(Block(x, 16, BlockType.LAVA, "#FF3D00"))
        }
        // Stepping stones
        blocks.add(Block(10, 14, BlockType.SOLID, "#FFD700"))
        blocks.add(Block(13, 13, BlockType.BOUNCE, "#00E5FF")) // Bounce pad
        blocks.add(Block(16, 9, BlockType.SOLID, "#7C4DFF"))
        blocks.add(Block(17, 9, BlockType.CHECKPOINT, "#00E676")) // Checkpoint

        // High altitude jump & key
        blocks.add(Block(20, 8, BlockType.KEY_PICKUP, "#FFD700"))
        blocks.add(Block(20, 11, BlockType.LAVA, "#FF1744"))
        blocks.add(Block(23, 10, BlockType.SOLID, "#7C4DFF"))
        blocks.add(Block(26, 9, BlockType.SPEED, "#FF9100")) // Speed boost
        blocks.add(Block(30, 8, BlockType.SOLID, "#7C4DFF"))
        blocks.add(Block(33, 7, BlockType.SOLID, "#7C4DFF"))
        blocks.add(Block(36, 6, BlockType.FINISH_PORTAL, "#FFD700")) // Portal

        return GameLevel(
            id = "level_dylan_master",
            title = "Dylan's Impossible Tower [OFFICIAL]",
            author = "@dylaniscool67",
            description = "The ultimate test created by Dylan! Precise jumps, bounce mechanics, and victory portal at the apex.",
            width = 40,
            height = 18,
            blocks = blocks,
            likes = 3490,
            rating = 5.0f,
            plays = 14200,
            difficulty = "Dylan's Impossible",
            category = "Obby",
            isOfficial = true
        )
    }

    private fun createRainbowObbyLevel(): GameLevel {
        val blocks = mutableListOf<Block>()
        // Rainbow stair sequence
        val colors = listOf("#F44336", "#FF9800", "#FFEB3B", "#4CAF50", "#2196F3", "#9C27B0")
        for (i in 0..12) {
            val color = colors[i % colors.size]
            blocks.add(Block(i * 2 + 1, 15 - (i % 5), BlockType.SOLID, color))
            if (i == 4) {
                blocks.add(Block(i * 2 + 1, 14 - (i % 5), BlockType.CHECKPOINT, "#FFFFFF"))
            }
            if (i == 7) {
                blocks.add(Block(i * 2 + 2, 13 - (i % 5), BlockType.KEY_PICKUP, "#FFD700"))
            }
        }
        // Lava floor below
        for (x in 2..28) {
            blocks.add(Block(x, 17, BlockType.LAVA, "#FF1744"))
        }
        // Goal
        blocks.add(Block(30, 10, BlockType.SOLID, "#9C27B0"))
        blocks.add(Block(31, 9, BlockType.FINISH_PORTAL, "#FFD700"))

        return GameLevel(
            id = "level_rainbow_obby",
            title = "Rainbow Parkour Adventure",
            author = "SkyCrafter_99",
            description = "Vibrant rainbow stepped obstacles! Great for practicing double jumps and rhythm.",
            width = 35,
            height = 18,
            blocks = blocks,
            likes = 1840,
            rating = 4.8f,
            plays = 8900,
            difficulty = "Medium",
            category = "Parkour",
            isOfficial = false
        )
    }

    private fun createLavaEscapeLevel(): GameLevel {
        val blocks = mutableListOf<Block>()
        for (x in 0..4) blocks.add(Block(x, 15, BlockType.SOLID, "#37474F"))
        for (x in 5..35) blocks.add(Block(x, 16, BlockType.LAVA, "#FF3D00"))

        blocks.add(Block(7, 13, BlockType.BOUNCE, "#00E5FF"))
        blocks.add(Block(12, 10, BlockType.ICE, "#80D8FF"))
        blocks.add(Block(16, 9, BlockType.SOLID, "#37474F"))
        blocks.add(Block(17, 8, BlockType.CHECKPOINT, "#00E676"))
        blocks.add(Block(21, 9, BlockType.SPEED, "#FF6D00"))
        blocks.add(Block(26, 8, BlockType.SOLID, "#37474F"))
        blocks.add(Block(27, 7, BlockType.KEY_PICKUP, "#FFD700"))
        blocks.add(Block(31, 7, BlockType.SOLID, "#37474F"))
        blocks.add(Block(34, 6, BlockType.FINISH_PORTAL, "#FFD700"))

        return GameLevel(
            id = "level_lava_rush",
            title = "Molten Core: Lava Escape",
            author = "MagmaMaster",
            description = "One wrong step and you're toast! Use bounce pads and ice glide to reach the bunker portal.",
            width = 38,
            height = 18,
            blocks = blocks,
            likes = 2100,
            rating = 4.9f,
            plays = 9600,
            difficulty = "Hard",
            category = "Lava Escape",
            isOfficial = false
        )
    }

    private fun createSpeedrunGalaxyLevel(): GameLevel {
        val blocks = mutableListOf<Block>()
        for (x in 0..3) blocks.add(Block(x, 15, BlockType.SOLID, "#311B92"))
        for (x in 5..32 step 4) {
            blocks.add(Block(x, 14 - ((x / 4) % 3), BlockType.SPEED, "#00E5FF"))
            blocks.add(Block(x + 1, 14 - ((x / 4) % 3), BlockType.SOLID, "#311B92"))
        }
        blocks.add(Block(17, 12, BlockType.CHECKPOINT, "#00E676"))
        blocks.add(Block(25, 11, BlockType.KEY_PICKUP, "#FFD700"))
        blocks.add(Block(35, 12, BlockType.FINISH_PORTAL, "#FFD700"))

        return GameLevel(
            id = "level_speedrun_galaxy",
            title = "Speedrun Hyperway 60FPS",
            author = "SonicPixel",
            description = "High momentum boost pads lined up in zero gravity. How fast can you clear it?",
            width = 38,
            height = 18,
            blocks = blocks,
            likes = 980,
            rating = 4.7f,
            plays = 4300,
            difficulty = "Medium",
            category = "Speedrun",
            isOfficial = false
        )
    }

    private fun createPuzzleKeyMaze(): GameLevel {
        val blocks = mutableListOf<Block>()
        // Multi-level maze with keys needed
        for (x in 0..30) {
            blocks.add(Block(x, 16, BlockType.SOLID, "#455A64"))
            if (x in 10..20) blocks.add(Block(x, 11, BlockType.SOLID, "#455A64"))
            if (x in 15..28) blocks.add(Block(x, 6, BlockType.SOLID, "#455A64"))
        }
        blocks.add(Block(8, 14, BlockType.BOUNCE, "#00E5FF"))
        blocks.add(Block(12, 10, BlockType.KEY_PICKUP, "#FFD700"))
        blocks.add(Block(18, 9, BlockType.BOUNCE, "#00E5FF"))
        blocks.add(Block(22, 5, BlockType.KEY_PICKUP, "#FFD700"))
        blocks.add(Block(27, 5, BlockType.FINISH_PORTAL, "#FFD700"))

        return GameLevel(
            id = "level_puzzle_maze",
            title = "The Ancient Key Crypt",
            author = "BlockArchitect",
            description = "Navigate vertical corridors, grab the golden keys, and unlock the exit tomb.",
            width = 35,
            height = 18,
            blocks = blocks,
            likes = 1420,
            rating = 4.8f,
            plays = 6100,
            difficulty = "Easy",
            category = "Maze",
            isOfficial = false
        )
    }

    fun getInitialLeaderboard(): List<LeaderboardEntry> {
        return listOf(
            LeaderboardEntry(1, "Dylan (Owner)", 100000000000L, "👑 FOUNDER", PlayerRank.GAME_OWNER),
            LeaderboardEntry(2, "ProGamer_X", 4850000L, "💎 TOP BUILDER", PlayerRank.PREMIUM),
            LeaderboardEntry(3, "ObbyMaster99", 2940000L, "⚡ SPEEDRUN GOD", PlayerRank.PREMIUM),
            LeaderboardEntry(4, "NeonKnight", 1850000L, "🛡️ MODERATOR", PlayerRank.ADMIN),
            LeaderboardEntry(5, "PixelHero67", 940000L, "⭐ STAR CREATOR", PlayerRank.MEMBER),
            LeaderboardEntry(6, "BlockQueen", 650000L, "🎨 UGC ARTIST", PlayerRank.MEMBER),
            LeaderboardEntry(7, "NinjaDash", 420000L, "🔥 LAVA SURVIVOR", PlayerRank.MEMBER)
        )
    }
}
