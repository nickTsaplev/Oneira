package com.lesterade.oneira.gameHandling.weapons.healEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

interface HealEffect {
    fun calculate(from: creature, to: creature, located: biome, heal: Float): Float
}