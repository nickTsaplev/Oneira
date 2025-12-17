package com.lesterade.oneira.gameHandling.weapons.damageEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

interface DamageEffect {
    fun calculate(from: creature, to: creature, located: biome, damage: Float): Float
}