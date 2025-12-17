package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

class SelfPoison(val poison: Float): ActionEffect {
    override fun effect(
        from: Creature,
        to: Creature,
        located: Biome
    ) {
        from.poison += poison
    }
}