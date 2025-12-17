package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

class Poison(val poison: Float): ActionEffect {
    override fun effect(from: creature, to: creature, located: biome) {
        to.poison += poison
    }
}