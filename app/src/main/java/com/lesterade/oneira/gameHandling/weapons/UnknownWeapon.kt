package com.lesterade.oneira.gameHandling.weapons

import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.biomes.Biome

class UnknownWeapon : Instrument {
    override fun attack(from: Creature, to: Creature, located: Biome): Instrument {
        return this
    }

    override fun getSignature(from: Creature, to: Creature, located: Biome): Pair<Float, Float> {
        return Pair(0f, 0f)
    }

    override val damageBoost: Float? = null

    override val header
        get() = "???"

    override val name
        get() = ""

    override val description
        get() = "???"

    override val imageId = "unknown"
}
