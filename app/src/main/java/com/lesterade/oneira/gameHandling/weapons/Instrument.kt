package com.lesterade.oneira.gameHandling.weapons

import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.biomes.Biome

interface Instrument {
    fun attack(from : Creature, to : Creature, located : Biome): Instrument
    fun getSignature(from: Creature, to: Creature, located: Biome): Pair<Float, Float>

    val name: String
    val header: String
    val description : String

    val imageId: String
    val damageBoost: Float?

    val transmutable: Boolean
        get() = false
}