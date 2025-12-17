package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Actor
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.gameHandling.Player
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class LoseLoc(override val element: Element,
              override var name: String = "",
              override var header: String,
              override var desc: String): Biome {
    var next: MutableList<String> = mutableListOf()

    override fun affect(a: Creature) { }

    override fun getEnemy(): Actor {
        return Actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): Biome {
        val index = Random.nextInt(0, next.size)
        return LocationFactory.getByName(next[index])
    }

    override fun activateTool(id: Int, us: Player, enemy: Actor): Boolean {
        us.hand.removeAt(id)
        us.hand.add(us.draw())

        return true
    }

    override fun choices(us: Player): List<Instrument> = us.hand
}
