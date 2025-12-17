package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Actor
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.gameHandling.Player
import kotlinx.serialization.Serializable

@Serializable
class EndingLoc(override val element: Element,
                override var name: String = "",
                override var header: String,
                override val desc: String): Biome {
    override fun affect(a: Creature) { }

    override fun getEnemy(): Actor {
        return Actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): Biome? {
        return null
    }

    override fun activateTool(id: Int, us: Player, enemy: Actor): Boolean {
        return false
    }

    override fun choices(us: Player): List<Instrument> = us.hand
}
