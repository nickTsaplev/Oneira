package com.lesterade.oneira.game.biomes.encounters

import com.lesterade.oneira.game.Player
import com.lesterade.oneira.game.weapons.Instrument

interface Outcome {
    fun activate(us: Player)
    val choice: Instrument
}