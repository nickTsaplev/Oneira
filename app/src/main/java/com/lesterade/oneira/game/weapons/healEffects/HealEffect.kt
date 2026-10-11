package com.lesterade.oneira.game.weapons.healEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature

interface HealEffect {
    fun calculate(from: Creature, to: Creature, located: Biome, heal: Float): Float
}