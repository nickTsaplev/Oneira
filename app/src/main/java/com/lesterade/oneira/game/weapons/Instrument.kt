package com.lesterade.oneira.game.weapons

import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Element
import com.lesterade.oneira.game.biomes.Biome

interface Instrument {
    fun attack(from : Creature, to : Creature, located : Biome): Instrument
    fun getSignature(from: Creature, to: Creature, located: Biome): Pair<Float, Float>

    val name: String
    val header: String
    val description : String

    val element: Element

    val imageId: String
    val damageBoost: Float?

    val transmutable: Boolean
        get() = false

    fun setDescription(header: String, description: String)
}