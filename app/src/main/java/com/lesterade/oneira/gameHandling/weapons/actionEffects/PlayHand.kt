package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.player

class PlayHand: ActionEffect {
    override fun effect(
        from: creature,
        to: creature,
        located: biome
    ) {
        if (from !is player)
            return

        from.hand.forEach {
            val toAdd = it.attack(from, to, located)

            from.discard(toAdd)
        }

        from.hand = mutableListOf()
        from.redraw()
    }
}