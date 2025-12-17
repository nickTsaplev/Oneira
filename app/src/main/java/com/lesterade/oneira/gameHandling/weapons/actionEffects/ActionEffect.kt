package com.lesterade.oneira.gameHandling.weapons.actionEffects

import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature

interface ActionEffect {
    fun effect(from: creature, to: creature, located: biome)
}