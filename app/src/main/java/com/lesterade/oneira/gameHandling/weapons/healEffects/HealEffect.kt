package com.lesterade.oneira.gameHandling.weapons.healEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

interface HealEffect {
    fun calculate(from: Creature, to: Creature, located: Biome, heal: Float): Float
}