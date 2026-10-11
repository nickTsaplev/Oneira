package com.lesterade.oneira.game.weapons.damageEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature

interface DamageEffect {
    fun calculate(from: Creature, to: Creature, located: Biome, damage: Float): Float
}