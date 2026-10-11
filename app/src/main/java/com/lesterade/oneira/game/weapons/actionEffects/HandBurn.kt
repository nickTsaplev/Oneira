package com.lesterade.oneira.game.weapons.actionEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Player
import kotlin.random.Random

class HandBurn(val fireEff: Int) : ActionEffect {
    override fun effect(from: Creature, to: Creature, located: Biome) {
        if(fireEff == 0)
            from.innerFire = 0
        else
            from.innerFire += fireEff

        if(from is Player) {
            if(from.hand.size + from.cards.size < 4) {
                from.hp = 0f
                return
            }

            from.hand.removeAt(Random.nextInt(0, from.hand.size))
            from.redraw()
        }
    }
}