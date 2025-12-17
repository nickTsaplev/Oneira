package com.lesterade.oneira.gameHandling.weapons.damageEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.Player

class Boost(val counted: List<String>) : DamageEffect {
    override fun calculate(from: Creature, to: Creature, located: Biome, damage: Float): Float {
        if(from is Player) {
            var hitDmg = 0f
            from.cards.forEach { if(it.name in counted) hitDmg += 1f}
            from.hand.forEach { if(it.name in counted) hitDmg += 1f}
            hitDmg *= damage

            return hitDmg
        }
        return damage
    }
}