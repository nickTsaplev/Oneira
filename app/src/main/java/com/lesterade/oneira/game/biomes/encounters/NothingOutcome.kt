package com.lesterade.oneira.game.biomes.encounters

import com.lesterade.oneira.game.Player
import com.lesterade.oneira.game.weapons.Instrument

class NothingOutcome(override val choice: Instrument): Outcome {
    override fun activate(us: Player) { }
}