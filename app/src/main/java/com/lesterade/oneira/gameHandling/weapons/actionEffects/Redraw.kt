package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.player

class Redraw : ActionEffect {
    override fun effect(from: creature, to: creature, located: biome) {
        if(from is player) {
            from.hand.forEach{
                from.discard(it)
            }
            from.hand = mutableListOf()
            from.redraw()
        }
    }
}