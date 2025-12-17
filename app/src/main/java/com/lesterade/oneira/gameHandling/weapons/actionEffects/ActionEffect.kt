package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature

interface ActionEffect {
    fun effect(from: Creature, to: Creature, located: Biome)
}