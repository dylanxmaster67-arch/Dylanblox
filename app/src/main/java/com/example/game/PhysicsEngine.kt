package com.example.game

import com.example.model.Block
import com.example.model.BlockType
import com.example.model.GameLevel

data class PlayerPhysicsState(
    var x: Float = 1.0f,
    var y: Float = 14.0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var onGround: Boolean = false,
    var checkpointX: Float = 1.0f,
    var checkpointY: Float = 14.0f,
    var hasWon: Boolean = false,
    var isDead: Boolean = false,
    var collectedKeys: Int = 0,
    var deathCount: Int = 0,
    var jumpRequested: Boolean = false
)

class PhysicsEngine(
    val level: GameLevel,
    var isFlyEnabled: Boolean = false,
    var isNoclipEnabled: Boolean = false
) {
    var state = PlayerPhysicsState()
    private val activeBlocks = level.blocks.toMutableList()

    val currentBlocks: List<Block>
        get() = activeBlocks

    init {
        resetToStart()
    }

    fun resetToStart() {
        val startY = 14f.coerceAtMost((level.height - 3).toFloat())
        state = PlayerPhysicsState(
            x = 1.0f,
            y = startY,
            checkpointX = 1.0f,
            checkpointY = startY
        )
    }

    fun respawnAtCheckpoint() {
        state.x = state.checkpointX
        state.y = state.checkpointY
        state.vx = 0f
        state.vy = 0f
        state.isDead = false
        state.onGround = true
    }

    fun update(
        moveX: Float, // -1f .. 1f
        jumpPressed: Boolean,
        flyUp: Boolean = false,
        flyDown: Boolean = false
    ) {
        if (state.hasWon || state.isDead) return

        if (isFlyEnabled) {
            // Flying Mode: freeform movement
            state.vx = moveX * 7f
            state.vy = when {
                flyUp -> -7f
                flyDown -> 7f
                jumpPressed -> -7f
                else -> 0f
            }
            state.x += state.vx * 0.1f
            state.y += state.vy * 0.1f
            checkTriggersOnly()
            return
        }

        // Standard Platformer Physics
        val acceleration = 0.8f
        val friction = 0.82f
        val gravity = 0.75f
        val terminalVelocity = 15f
        val jumpImpulse = -12.5f

        // Horizontal movement
        state.vx += moveX * acceleration
        state.vx *= friction
        if (kotlin.math.abs(state.vx) < 0.05f) state.vx = 0f

        // Vertical movement / Gravity
        state.vy += gravity
        if (state.vy > terminalVelocity) state.vy = terminalVelocity

        // Jump execution
        if (jumpPressed && (state.onGround || isNoclipEnabled)) {
            state.vy = jumpImpulse
            state.onGround = false
        }

        val stepX = state.vx * 0.16f
        val stepY = state.vy * 0.16f

        if (isNoclipEnabled) {
            // Noclip allows passing through walls & ignoring hazard death
            state.x += stepX
            state.y += stepY
            checkTriggersOnly()
        } else {
            // Full collision handling
            applyHorizontalMovement(stepX)
            applyVerticalMovement(stepY)
            checkInteractions()
        }

        // Check pit fall
        if (state.y > level.height + 2) {
            triggerDeath()
        }
    }

    private fun applyHorizontalMovement(stepX: Float) {
        state.x += stepX
        val playerBox = getBoundingBox(state.x, state.y)

        for (block in activeBlocks) {
            if (block.type.isSolid) {
                if (overlaps(playerBox, block.x.toFloat(), block.y.toFloat(), 1f, 1f)) {
                    if (stepX > 0) {
                        state.x = block.x - 0.75f
                    } else if (stepX < 0) {
                        state.x = block.x + 1f
                    }
                    state.vx = 0f
                    break
                }
            }
        }
    }

    private fun applyVerticalMovement(stepY: Float) {
        state.y += stepY
        state.onGround = false
        val playerBox = getBoundingBox(state.x, state.y)

        for (block in activeBlocks) {
            if (block.type.isSolid) {
                if (overlaps(playerBox, block.x.toFloat(), block.y.toFloat(), 1f, 1f)) {
                    if (stepY > 0) {
                        // Landing on ground
                        state.y = block.y - 0.95f
                        state.vy = 0f
                        state.onGround = true

                        // Check bounce pad
                        if (block.type == BlockType.BOUNCE) {
                            state.vy = -17f
                            state.onGround = false
                        } else if (block.type == BlockType.SPEED) {
                            state.vx = 8f
                        }
                    } else if (stepY < 0) {
                        // Hitting ceiling
                        state.y = block.y + 1f
                        state.vy = 0f
                    }
                    break
                }
            }
        }
    }

    private fun checkInteractions() {
        val playerBox = getBoundingBox(state.x, state.y)
        val iterator = activeBlocks.iterator()

        while (iterator.hasNext()) {
            val block = iterator.next()
            if (overlaps(playerBox, block.x.toFloat(), block.y.toFloat(), 1f, 1f)) {
                when (block.type) {
                    BlockType.LAVA -> {
                        triggerDeath()
                        return
                    }
                    BlockType.CHECKPOINT -> {
                        state.checkpointX = block.x.toFloat()
                        state.checkpointY = (block.y - 1).toFloat()
                    }
                    BlockType.KEY_PICKUP -> {
                        state.collectedKeys += 1
                        iterator.remove()
                    }
                    BlockType.FINISH_PORTAL -> {
                        state.hasWon = true
                    }
                    else -> {}
                }
            }
        }
    }

    private fun checkTriggersOnly() {
        val playerBox = getBoundingBox(state.x, state.y)
        val iterator = activeBlocks.iterator()
        while (iterator.hasNext()) {
            val block = iterator.next()
            if (overlaps(playerBox, block.x.toFloat(), block.y.toFloat(), 1f, 1f)) {
                when (block.type) {
                    BlockType.KEY_PICKUP -> {
                        state.collectedKeys += 1
                        iterator.remove()
                    }
                    BlockType.FINISH_PORTAL -> {
                        state.hasWon = true
                    }
                    BlockType.CHECKPOINT -> {
                        state.checkpointX = block.x.toFloat()
                        state.checkpointY = (block.y - 1).toFloat()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun triggerDeath() {
        state.isDead = true
        state.deathCount += 1
    }

    private fun getBoundingBox(x: Float, y: Float): FloatArray {
        // [left, top, right, bottom]
        return floatArrayOf(x + 0.1f, y + 0.05f, x + 0.7f, y + 0.95f)
    }

    private fun overlaps(box: FloatArray, bx: Float, by: Float, bw: Float, bh: Float): Boolean {
        return box[0] < bx + bw && box[2] > bx && box[1] < by + bh && box[3] > by
    }
}
