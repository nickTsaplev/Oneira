package com.lesterade.oneira.gameHandling.weapons.damageEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

class Percent : DamageEffect {
    override fun calculate(from: Creature, to: Creature, located: Biome, damage: Float): Float {
        return (damage * to.hp)
    }
}