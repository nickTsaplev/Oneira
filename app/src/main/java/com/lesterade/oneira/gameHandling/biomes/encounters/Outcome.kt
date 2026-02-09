package com.lesterade.oneira.gameHandling.biomes.encounters

import com.lesterade.oneira.gameHandling.Player
import com.lesterade.oneira.gameHandling.weapons.Instrument

interface Outcome {
    fun activate(us: Player)
    val choice: Instrument
}