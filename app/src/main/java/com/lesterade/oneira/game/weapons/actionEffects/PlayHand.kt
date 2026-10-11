package com.lesterade.oneira.game.weapons.actionEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Player

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