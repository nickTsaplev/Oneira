package com.lesterade.oneira.gameHandling.biomes.encounters

import com.lesterade.oneira.gameHandling.Player
import com.lesterade.oneira.gameHandling.weapons.Instrument

class HealOutcome(override val choice: Instrument): Outcome {
    override fun activate(us: Player) {
        us.hp = us.maxhp
    }
}