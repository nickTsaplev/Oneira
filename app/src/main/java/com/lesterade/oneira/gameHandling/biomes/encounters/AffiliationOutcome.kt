package com.lesterade.oneira.gameHandling.biomes.encounters

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Player
import com.lesterade.oneira.gameHandling.weapons.Instrument

class AffiliationOutcome(override val choice: Instrument, val element: Element): Outcome {
    override fun activate(us: Player) {
        us.affiliations[element] = (us.affiliations[element] ?: 0) + 1
    }
}