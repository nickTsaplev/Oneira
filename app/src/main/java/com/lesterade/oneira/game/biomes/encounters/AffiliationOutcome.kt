package com.lesterade.oneira.game.biomes.encounters

import com.lesterade.oneira.game.Element
import com.lesterade.oneira.game.Player
import com.lesterade.oneira.game.weapons.Instrument

class AffiliationOutcome(override val choice: Instrument, val element: Element): Outcome {
    override fun activate(us: Player) {
        us.affiliations[element] = (us.affiliations[element] ?: 0) + 1
    }
}