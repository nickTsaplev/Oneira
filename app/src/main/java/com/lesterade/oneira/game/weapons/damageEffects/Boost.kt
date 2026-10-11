package com.lesterade.oneira.game.weapons.damageEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Player

class Boost(val counted: List<String>) :
    com.lesterade.oneira.game.weapons.damageEffects.DamageEffect {
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