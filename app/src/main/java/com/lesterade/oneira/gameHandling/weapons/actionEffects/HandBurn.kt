package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.player
import kotlin.random.Random

class HandBurn(val fireEff: Int) : ActionEffect {
    override fun effect(from: creature, to: creature, located: biome) {
        if(fireEff == 0)
            from.innerFire = 0
        else
            from.innerFire += fireEff

        if(from is player) {
            if(from.hand.size + from.cards.size < 4) {
                from.hp = 0f
                return
            }

            from.hand.removeAt(Random.nextInt(0, from.hand.size))
            from.redraw()
        }
    }
}