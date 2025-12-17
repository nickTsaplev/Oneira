package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature
import kotlin.random.Random

class Peacemaker(val peaceWith: MutableList<String>): ActionEffect {
    override fun effect(from: creature, to: creature, located: biome) {
        if(to.name in peaceWith)
            if(Random.nextInt(0, 2) == 0)
                to.hp = 0f
    }
}