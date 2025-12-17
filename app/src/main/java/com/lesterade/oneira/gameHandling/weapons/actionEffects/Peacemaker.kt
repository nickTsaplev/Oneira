package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature
import kotlin.random.Random

class Peacemaker(val peaceWith: MutableList<String>): ActionEffect {
    override fun effect(from: Creature, to: Creature, located: Biome) {
        if(to.name in peaceWith)
            if(Random.nextInt(0, 2) == 0)
                to.hp = 0f
    }
}