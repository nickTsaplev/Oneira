package com.lesterade.oneira.game.weapons.actionEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Player

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