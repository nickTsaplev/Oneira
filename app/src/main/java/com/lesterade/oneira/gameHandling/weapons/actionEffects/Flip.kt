package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

class Flip : ActionEffect {
    override fun effect(from: creature, to: creature, located: biome) {
        val tmp = to.hp
        to.hp = from.hp * (to.maxhp / from.maxhp)
        from.hp = tmp * (from.maxhp / to.maxhp)
    }
}