package com.lesterade.oneira.gameHandling.weapons.damageEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

class Wood: DamageEffect {
    override fun calculate(from: creature, to: creature, located: biome, damage: Float): Float {
        if(damage - from.innerFire.toFloat() > 0f)
            return damage - from.innerFire.toFloat()
        return 0f
    }
}