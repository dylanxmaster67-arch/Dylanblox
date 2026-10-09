package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DylanbloxRepository
import com.example.data.PreloadedData
import com.example.game.PhysicsEngine
import com.example.model.Block
import com.example.model.BlockType
import com.example.model.GameLevel
import com.example.model.PlayerRank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DylanbloxUnitTest {

    private lateinit var repository: DylanbloxRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = DylanbloxRepository(context)
    }

    @Test
    fun testAwardGameWinKeys() {
        val initialKeys = repository.keys.value
        repository.awardGameWinKeys()
        assertEquals(initialKeys + 450L, repository.keys.value)
    }

    @Test
    fun testRedeemCodeDylanGivesPremium() {
        val (success, _) = repository.redeemCode("Dylan")
        assertTrue(success)
        assertEquals(PlayerRank.PREMIUM, repository.rank.value)
        assertEquals(0, repository.getLevelCreationCost()) // Free game creation!
    }

    @Test
    fun testRedeemCodeKeysGivesHugeKeys() {
        val (success, _) = repository.redeemCode("keys")
        assertTrue(success)
        assertTrue(repository.keys.value >= 100000000000L)
    }

    @Test
    fun testItemPricingAndPremiumDiscount() {
        val item = repository.shopItems.value.first { it.basePrice == 2000 }
        repository.setPlayerRank(PlayerRank.MEMBER)
        assertEquals(2000, repository.getItemPrice(item))

        // Premium gets 80% discount
        repository.setPlayerRank(PlayerRank.PREMIUM)
        val discountedPrice = repository.getItemPrice(item)
        assertEquals(400, discountedPrice) // 20% of 2000 = 400
    }

    @Test
    fun testOwnerSale100PercentDiscount() {
        val item = repository.shopItems.value.first()
        repository.executeAdminCommand("/sale 100")
        assertEquals(0, repository.getItemPrice(item))
    }

    @Test
    fun testAdminFlyAndNoclipCommands() {
        repository.executeAdminCommand("/fly")
        assertTrue(repository.isFlyEnabled.value)

        repository.executeAdminCommand("/noclip")
        assertTrue(repository.isNoclipEnabled.value)
    }

    @Test
    fun testLevelCreationCostLogic() {
        repository.setPlayerRank(PlayerRank.MEMBER)
        assertEquals(67, repository.getLevelCreationCost())

        repository.setPlayerRank(PlayerRank.PREMIUM)
        assertEquals(0, repository.getLevelCreationCost())
    }

    @Test
    fun testPhysicsEngineMovementAndPortalWin() {
        val level = GameLevel(
            id = "test_lvl",
            title = "Test Level",
            author = "Tester",
            description = "Test",
            blocks = listOf(
                Block(1, 14, BlockType.SOLID),
                Block(2, 14, BlockType.FINISH_PORTAL)
            )
        )
        val physics = PhysicsEngine(level)
        physics.state.x = 2.0f
        physics.state.y = 14.0f
        physics.update(0f, false)
        assertTrue(physics.state.hasWon)
    }
}
