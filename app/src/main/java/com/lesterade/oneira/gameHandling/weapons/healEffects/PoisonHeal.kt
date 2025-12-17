package com.lesterade.oneira.gameHandling.weapons.healEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

class PoisonHeal: HealEffect {
    override fun calculate(from: creature, to: creature, located: biome, heal: Float): Float {
        return heal + to.poison
    }
}