package com.lesterade.oneira.gameHandling.biomes.encounters

import com.lesterade.oneira.gameHandling.Player
import com.lesterade.oneira.gameHandling.weapons.Instrument

class NothingOutcome(override val choice: Instrument): Outcome {
    override fun activate(us: Player) { }
}