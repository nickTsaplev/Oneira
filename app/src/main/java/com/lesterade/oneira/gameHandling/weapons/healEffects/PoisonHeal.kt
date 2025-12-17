package com.lesterade.oneira.gameHandling.weapons.healEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

class PoisonHeal: HealEffect {
    override fun calculate(from: Creature, to: Creature, located: Biome, heal: Float): Float {
        return heal + to.poison
    }
}