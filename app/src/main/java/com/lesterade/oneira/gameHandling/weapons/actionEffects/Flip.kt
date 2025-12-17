package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

class Flip : ActionEffect {
    override fun effect(from: Creature, to: Creature, located: Biome) {
        val tmp = to.hp
        to.hp = from.hp * (to.maxhp / from.maxhp)
        from.hp = tmp * (from.maxhp / to.maxhp)
    }
}