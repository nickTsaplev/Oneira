package com.lesterade.oneira.game.weapons.damageEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature

class Wood: DamageEffect {
    override fun calculate(from: Creature, to: Creature, located: Biome, damage: Float): Float {
        if(damage - from.innerFire.toFloat() > 0f)
            return damage - from.innerFire.toFloat()
        return 0f
    }
}