package com.lesterade.oneira.gameHandling.weapons.damageEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

class FieryDamage(val minFire: Int): DamageEffect {
    override fun calculate(from: Creature, to: Creature, located: Biome, damage: Float): Float {
        if(from.innerFire > minFire)
            return damage + from.innerFire.toFloat()
        return damage
    }
}