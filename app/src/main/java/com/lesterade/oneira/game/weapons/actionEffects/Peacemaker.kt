package com.lesterade.oneira.game.weapons.actionEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature
import kotlin.random.Random

class Peacemaker(val peaceWith: MutableList<String>): ActionEffect {
    override fun effect(from: Creature, to: Creature, located: Biome) {
        if(to.name in peaceWith)
            if(Random.nextInt(0, 2) == 0)
                to.hp = 0f
    }
}