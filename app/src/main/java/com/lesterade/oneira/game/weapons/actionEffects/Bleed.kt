package com.lesterade.oneira.game.weapons.actionEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature

class Bleed(val bleed: Float): ActionEffect {
    override fun effect(
        from: Creature,
        to: Creature,
        located: Biome
    ) {
        to.bleed += bleed
    }
}