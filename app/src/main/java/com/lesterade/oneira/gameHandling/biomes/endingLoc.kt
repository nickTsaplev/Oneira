package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.actor
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.weapons.instrument
import com.lesterade.oneira.gameHandling.player
import kotlinx.serialization.Serializable

@Serializable
class endingLoc(override val element: Element,
                override var name: String = "",
                override var header: String,
                override val desc: String): biome {
    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): biome? {
        return null
    }

    override fun activateTool(id: Int, us: player, enemy: actor): Boolean {
        return false
    }

    override fun choices(us: player): List<instrument> = us.hand
}
