package com.lesterade.oneira.gameHandling.weapons.damageEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

class FieryDamage(val minFire: Int): DamageEffect {
    override fun calculate(from: creature, to: creature, located: biome, damage: Float): Float {
        if(from.innerFire > minFire)
            return damage + from.innerFire.toFloat()
        return damage
    }
}