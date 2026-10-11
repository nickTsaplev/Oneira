package com.lesterade.oneira.game.weapons.damageEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature

class Percent : DamageEffect {
    override fun calculate(from: Creature, to: Creature, located: Biome, damage: Float): Float {
        return (damage * to.hp)
    }
}