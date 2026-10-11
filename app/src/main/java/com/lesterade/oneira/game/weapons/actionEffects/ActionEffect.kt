package com.lesterade.oneira.game.weapons.actionEffects

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.Creature

interface ActionEffect {
    fun effect(from: Creature, to: Creature, located: Biome)
}