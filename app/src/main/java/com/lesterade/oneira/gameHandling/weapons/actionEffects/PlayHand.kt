package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.Player

class PlayHand: ActionEffect {
    override fun effect(
        from: Creature,
        to: Creature,
        located: Biome
    ) {
        if (from !is Player)
            return

        from.hand.forEach {
            val toAdd = it.attack(from, to, located)

            from.discard(toAdd)
        }

        from.hand = mutableListOf()
        from.redraw()
    }
}