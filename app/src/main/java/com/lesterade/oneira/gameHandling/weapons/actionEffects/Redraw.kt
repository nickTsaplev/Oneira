package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.Player

class Redraw : ActionEffect {
    override fun effect(from: Creature, to: Creature, located: Biome) {
        if(from is Player) {
            from.hand.forEach{
                from.discard(it)
            }
            from.hand = mutableListOf()
            from.redraw()
        }
    }
}