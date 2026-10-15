package com.lesterade.oneira.game.weapons

import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Element
import com.lesterade.oneira.game.biomes.Biome

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

    override val element: Element
        get() = Element.fire

    override val imageId = "unknown"

    override fun setDescription(header: String, description: String) {
        
    }
}
