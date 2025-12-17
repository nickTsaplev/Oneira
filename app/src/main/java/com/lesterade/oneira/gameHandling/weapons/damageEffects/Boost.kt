package com.lesterade.oneira.gameHandling.weapons.damageEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.player

class Boost(val counted: List<String>) : DamageEffect {
    override fun calculate(from: creature, to: creature, located: biome, damage: Float): Float {
        if(from is player) {
            var hitDmg = 0f
            from.cards.forEach { if(it.name in counted) hitDmg += 1f}
            from.hand.forEach { if(it.name in counted) hitDmg += 1f}
            hitDmg *= damage

            return hitDmg
        }
        return damage
    }
}