package com.lesterade.oneira.game.biomes

import com.lesterade.oneira.game.Element
import com.lesterade.oneira.game.Actor
import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Player
import com.lesterade.oneira.game.weapons.Instrument
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
