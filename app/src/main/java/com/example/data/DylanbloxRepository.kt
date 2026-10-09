package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AvatarConfig
import com.example.model.Block
import com.example.model.ChatMessage
import com.example.model.GameLevel
import com.example.model.LeaderboardEntry
import com.example.model.PlayerRank
import com.example.model.ReportTicket
import com.example.model.ServerSettings
import com.example.model.ShopCategory
import com.example.model.ShopItem
import com.example.model.VoiceParticipant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class DylanbloxRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("dylanblox_prefs", Context.MODE_PRIVATE)

    // Current Player Profile
    private val _username = MutableStateFlow(prefs.getString("username", "Gamer_Dylan") ?: "Gamer_Dylan")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _keys = MutableStateFlow(prefs.getLong("keys", 500L)) // Starting welcome bonus
    val keys: StateFlow<Long> = _keys.asStateFlow()

    private val _rank = MutableStateFlow(
        try {
            PlayerRank.valueOf(prefs.getString("rank", PlayerRank.MEMBER.name) ?: PlayerRank.MEMBER.name)
        } catch (_: Exception) {
            PlayerRank.MEMBER
        }
    )
    val rank: StateFlow<PlayerRank> = _rank.asStateFlow()

    private val _avatarConfig = MutableStateFlow(AvatarConfig())
    val avatarConfig: StateFlow<AvatarConfig> = _avatarConfig.asStateFlow()

    private val _equippedItems = MutableStateFlow<Set<String>>(setOf("item_hat_1"))
    val equippedItems: StateFlow<Set<String>> = _equippedItems.asStateFlow()

    private val _unlockedItems = MutableStateFlow<Set<String>>(setOf("item_hat_1", "item_hat_3", "item_acc_4"))
    val unlockedItems: StateFlow<Set<String>> = _unlockedItems.asStateFlow()

    // Level Management
    private val _levels = MutableStateFlow<List<GameLevel>>(PreloadedData.getInitialLevels())
    val levels: StateFlow<List<GameLevel>> = _levels.asStateFlow()

    private val _userCreatedLevels = MutableStateFlow<List<GameLevel>>(emptyList())
    val userCreatedLevels: StateFlow<List<GameLevel>> = _userCreatedLevels.asStateFlow()

    // Shop & Global Sales
    private val _shopItems = MutableStateFlow<List<ShopItem>>(PreloadedData.getInitialShopItems())
    val shopItems: StateFlow<List<ShopItem>> = _shopItems.asStateFlow()

    // Owner Sale Override (0..100) -> 0 means standard, 80 means 80% off, 100 means 100% free!
    private val _ownerSaleDiscountPercent = MutableStateFlow(0)
    val ownerSaleDiscountPercent: StateFlow<Int> = _ownerSaleDiscountPercent.asStateFlow()

    // Admin Toggles
    private val _isFlyEnabled = MutableStateFlow(false)
    val isFlyEnabled: StateFlow<Boolean> = _isFlyEnabled.asStateFlow()

    private val _isNoclipEnabled = MutableStateFlow(false)
    val isNoclipEnabled: StateFlow<Boolean> = _isNoclipEnabled.asStateFlow()

    private val _bannedUsers = MutableStateFlow<Set<String>>(setOf("Troll_Hacker99"))
    val bannedUsers: StateFlow<Set<String>> = _bannedUsers.asStateFlow()

    private val _serverBroadcast = MutableStateFlow<String?>(null)
    val serverBroadcast: StateFlow<String?> = _serverBroadcast.asStateFlow()

    // Chat System
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("m1", "Dylan (Owner)", PlayerRank.GAME_OWNER, "Welcome to Dylanblox! Check out my official tower level!", "GLOBAL"),
            ChatMessage("m2", "ObbyMaster99", PlayerRank.PREMIUM, "Redeemed code 'Dylan' and got Premium instantly!", "GLOBAL"),
            ChatMessage("m3", "NeonKnight", PlayerRank.ADMIN, "Anti-cheat engine active. Fair play monitored on all servers.", "GLOBAL")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Voice Chat State
    private val _isVoiceMicMuted = MutableStateFlow(true)
    val isVoiceMicMuted: StateFlow<Boolean> = _isVoiceMicMuted.asStateFlow()

    private val _voiceParticipants = MutableStateFlow(
        listOf(
            VoiceParticipant("Dylan (Owner)", PlayerRank.GAME_OWNER, isMuted = false, isSpeaking = true, volume = 0.9f),
            VoiceParticipant("SkyCrafter_99", PlayerRank.PREMIUM, isMuted = false, isSpeaking = false, volume = 0.8f),
            VoiceParticipant("ProGamer_X", PlayerRank.PREMIUM, isMuted = true, isSpeaking = false, volume = 0.7f),
            VoiceParticipant("You", PlayerRank.MEMBER, isMuted = true, isSpeaking = false, volume = 1.0f)
        )
    )
    val voiceParticipants: StateFlow<List<VoiceParticipant>> = _voiceParticipants.asStateFlow()

    // Leaderboards
    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(PreloadedData.getInitialLeaderboard())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    // Moderation Reports
    private val _reports = MutableStateFlow<List<ReportTicket>>(
        listOf(
            ReportTicket("r1", "PLAYER", "SpeedHacker_07", "ModerationBot", "Suspicious movement speed in Lava Obby", "Exploiting/Cheating")
        )
    )
    val reports: StateFlow<List<ReportTicket>> = _reports.asStateFlow()

    // Server Preferences
    private val _serverSettings = MutableStateFlow(ServerSettings())
    val serverSettings: StateFlow<ServerSettings> = _serverSettings.asStateFlow()

    // Co-Op Live Building Event Log
    private val _coopEvents = MutableStateFlow<List<String>>(
        listOf(
            "Dylan connected to Co-Op room #88",
            "Dylan placed Bounce Pad at (14, 10)",
            "System: Netcode synchronized at 60Hz"
        )
    )
    val coopEvents: StateFlow<List<String>> = _coopEvents.asStateFlow()

    // Redeem Codes
    private val redeemedCodes = mutableSetOf<String>()

    fun awardGameWinKeys() {
        // Winning a game awards 450 keys!
        val newKeys = _keys.value + 450L
        setKeys(newKeys)
    }

    fun setKeys(amount: Long) {
        val clamped = if (amount < 0) 0L else amount
        _keys.value = clamped
        prefs.edit().putLong("keys", clamped).apply()
    }

    fun addKeys(amount: Long) {
        setKeys(_keys.value + amount)
    }

    fun setPlayerRank(newRank: PlayerRank) {
        _rank.value = newRank
        prefs.edit().putString("rank", newRank.name).apply()
    }

    fun getItemPrice(item: ShopItem): Int {
        val ownerDiscount = _ownerSaleDiscountPercent.value
        if (ownerDiscount >= 100) {
            return 0 // 100% discount owner sale!
        }
        if (ownerDiscount > 0) {
            val discounted = (item.basePrice * (100 - ownerDiscount)) / 100
            return discounted.coerceAtLeast(0)
        }
        // Premium rank gives 80% off!
        if (_rank.value == PlayerRank.PREMIUM || _rank.value == PlayerRank.GAME_OWNER || _rank.value == PlayerRank.ADMIN) {
            val discounted = (item.basePrice * 20) / 100 // 80% off = 20% of base price
            return discounted.coerceAtLeast(1)
        }
        return item.basePrice
    }

    fun buyItem(item: ShopItem): Boolean {
        if (_unlockedItems.value.contains(item.id)) {
            return true
        }
        val price = getItemPrice(item)
        if (_keys.value >= price) {
            setKeys(_keys.value - price)
            _unlockedItems.value = _unlockedItems.value + item.id
            equipItem(item.id)
            return true
        }
        return false
    }

    fun equipItem(itemId: String) {
        _equippedItems.value = _equippedItems.value + itemId
    }

    fun unequipItem(itemId: String) {
        _equippedItems.value = _equippedItems.value - itemId
    }

    fun getLevelCreationCost(): Int {
        // Creating your own game: 67 keys. Premium rank can create their own game for FREE!
        return if (_rank.value == PlayerRank.PREMIUM || _rank.value == PlayerRank.GAME_OWNER || _rank.value == PlayerRank.ADMIN) {
            0
        } else {
            67
        }
    }

    fun publishLevel(
        title: String,
        description: String,
        difficulty: String,
        category: String,
        blocks: List<Block>
    ): Pair<Boolean, String> {
        val cost = getLevelCreationCost()
        if (_keys.value < cost) {
            return Pair(false, "You need $cost keys to publish! (Premium users can publish for FREE)")
        }

        if (blocks.none { it.type == com.example.model.BlockType.FINISH_PORTAL }) {
            return Pair(false, "Your level must contain at least one Victory Portal to be playable!")
        }

        if (cost > 0) {
            setKeys(_keys.value - cost)
        }

        val newLevel = GameLevel(
            id = "custom_" + UUID.randomUUID().toString().take(8),
            title = title.ifBlank { "Custom Obby by ${_username.value}" },
            author = _username.value,
            description = description.ifBlank { "Awesome player-made level! Jump and conquer." },
            blocks = blocks,
            difficulty = difficulty,
            category = category,
            isOfficial = false,
            likes = 1,
            rating = 5.0f,
            plays = 0
        )

        _levels.value = listOf(newLevel) + _levels.value
        _userCreatedLevels.value = listOf(newLevel) + _userCreatedLevels.value

        // Log broadcast in coop/global
        addChatMessage("I just published a new level: '${newLevel.title}'!", "GLOBAL")

        return Pair(true, "Level published successfully! Cost: $cost keys.")
    }

    fun createCustomUgcItem(name: String, category: ShopCategory, colorHex: String, description: String) {
        val newItem = ShopItem(
            id = "ugc_" + UUID.randomUUID().toString().take(8),
            name = name,
            category = category,
            basePrice = 150,
            description = description,
            creator = _username.value,
            isCustom = true,
            colorHex = colorHex
        )
        _shopItems.value = listOf(newItem) + _shopItems.value
        _unlockedItems.value = _unlockedItems.value + newItem.id
        equipItem(newItem.id)
    }

    fun redeemCode(inputCode: String): Pair<Boolean, String> {
        val code = inputCode.trim()
        val normalized = code.lowercase()

        if (redeemedCodes.contains(normalized)) {
            return Pair(false, "Code '$code' has already been redeemed!")
        }

        when (normalized) {
            "dylan" -> {
                redeemedCodes.add(normalized)
                setPlayerRank(PlayerRank.PREMIUM)
                addChatMessage("System: ${_username.value} redeemed code 'Dylan' and unlocked [PREMIUM] Rank!", "GLOBAL")
                return Pair(true, "SUCCESS! You unlocked PREMIUM rank! (80% OFF all items & FREE game creation!)")
            }
            "keys" -> {
                redeemedCodes.add(normalized)
                // 100000M keys = 100,000,000,000 keys!
                val hugeKeys = 100000000000L
                addKeys(hugeKeys)
                addChatMessage("System: ${_username.value} redeemed code 'keys' and received 100,000M Keys!", "GLOBAL")
                return Pair(true, "JACKPOT! Added 100,000M (100 Billion) Keys to your balance!")
            }
            "dylaniscool67" -> {
                redeemedCodes.add(normalized)
                addKeys(67000L)
                _unlockedItems.value = _unlockedItems.value + "item_cape_2"
                return Pair(true, "Unlocked Golden Dylan Cape + 67,000 Bonus Keys!")
            }
            "nice try dude" -> {
                return Pair(false, "Nice try dude! That's the copyright anti-theft trigger, not a code ;)")
            }
            else -> {
                return Pair(false, "Invalid code! Try 'Dylan' or 'keys'.")
            }
        }
    }

    fun executeAdminCommand(input: String): String {
        val trimmed = input.trim()
        if (!trimmed.startsWith("/")) {
            return "Commands must start with '/'. Type /help for assistance."
        }

        val parts = trimmed.substring(1).split(" ")
        val cmd = parts[0].lowercase()
        val arg = parts.getOrNull(1)

        when (cmd) {
            "help" -> {
                return "Admin Commands:\n/fly - Toggle flying\n/noclip - Walk through walls\n/ban <user> - Ban a player\n/givekeys <amount> - Give keys (Owner)\n/setrank <rank> - Set player rank\n/sale <percent> - Set universal shop discount\n/broadcast <msg> - Send server announcement"
            }
            "fly" -> {
                _isFlyEnabled.value = !_isFlyEnabled.value
                return "Fly mode is now ${_isFlyEnabled.value.toString().uppercase()}"
            }
            "noclip" -> {
                _isNoclipEnabled.value = !_isNoclipEnabled.value
                return "Noclip is now ${_isNoclipEnabled.value.toString().uppercase()}"
            }
            "ban" -> {
                val target = arg ?: "UnknownPlayer"
                _bannedUsers.value = _bannedUsers.value + target
                addChatMessage("System [Admin Action]: Player '$target' was banned from Dylanblox servers.", "GLOBAL")
                return "Player '$target' has been banned."
            }
            "givekeys" -> {
                val amt = arg?.toLongOrNull() ?: 100000000000L
                addKeys(amt)
                return "Gave $amt keys to ${_username.value}!"
            }
            "setrank" -> {
                val newRank = when (arg?.lowercase()) {
                    "owner", "game_owner" -> PlayerRank.GAME_OWNER
                    "admin" -> PlayerRank.ADMIN
                    "premium" -> PlayerRank.PREMIUM
                    else -> PlayerRank.MEMBER
                }
                setPlayerRank(newRank)
                return "Rank updated to ${newRank.displayName}"
            }
            "sale" -> {
                val percent = arg?.toIntOrNull() ?: 100
                _ownerSaleDiscountPercent.value = percent.coerceIn(0, 100)
                val alert = "🚨 GAME OWNER UPDATE: Store sale is now ${_ownerSaleDiscountPercent.value}% OFF for all items!"
                broadcastAnnouncement(alert)
                return "Owner sale set to ${_ownerSaleDiscountPercent.value}% OFF!"
            }
            "broadcast" -> {
                val msg = parts.drop(1).joinToString(" ")
                broadcastAnnouncement(msg)
                return "Broadcast sent: $msg"
            }
            else -> {
                return "Unknown command '/$cmd'. Type /help for list."
            }
        }
    }

    fun broadcastAnnouncement(message: String) {
        _serverBroadcast.value = message
        addChatMessage("📢 [OWNER BROADCAST] $message", "GLOBAL", true)
    }

    fun clearBroadcast() {
        _serverBroadcast.value = null
    }

    fun addChatMessage(content: String, channel: String = "GLOBAL", isSystem: Boolean = false, recipient: String? = null) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = if (isSystem) "System" else _username.value,
            senderRank = if (isSystem) PlayerRank.GAME_OWNER else _rank.value,
            content = content,
            channel = channel,
            recipient = recipient,
            isSystem = isSystem
        )
        _chatMessages.value = _chatMessages.value + msg
    }

    fun toggleVoiceMic() {
        _isVoiceMicMuted.value = !_isVoiceMicMuted.value
        _voiceParticipants.value = _voiceParticipants.value.map {
            if (it.username == "You") it.copy(isMuted = _isVoiceMicMuted.value, isSpeaking = !_isVoiceMicMuted.value) else it
        }
    }

    fun updateServerSettings(settings: ServerSettings) {
        _serverSettings.value = settings
    }

    fun reportTarget(targetType: String, targetName: String, category: String, reason: String) {
        val ticket = ReportTicket(
            id = "rep_" + UUID.randomUUID().toString().take(8),
            targetType = targetType,
            targetName = targetName,
            reportedBy = _username.value,
            reason = reason,
            category = category
        )
        _reports.value = listOf(ticket) + _reports.value
    }

    fun resolveReport(ticketId: String, actionBan: Boolean) {
        _reports.value = _reports.value.map {
            if (it.id == ticketId) {
                if (actionBan) {
                    _bannedUsers.value = _bannedUsers.value + it.targetName
                }
                it.copy(status = if (actionBan) "BANNED" else "RESOLVED")
            } else it
        }
    }

    fun rateLevel(levelId: String, liked: Boolean) {
        _levels.value = _levels.value.map {
            if (it.id == levelId) {
                val newLikes = if (liked) it.likes + 1 else it.likes
                it.copy(likes = newLikes)
            } else it
        }
    }

    fun logCoopAction(action: String) {
        _coopEvents.value = listOf(action) + _coopEvents.value.take(15)
    }
}
