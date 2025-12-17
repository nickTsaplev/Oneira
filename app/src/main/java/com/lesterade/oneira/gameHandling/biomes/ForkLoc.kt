package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Actor
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.gameHandling.Player
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class ForkLoc(override val element: Element,
              override var name: String = "",
              override var header: String,
              override var desc: String): Biome {
    var next: MutableList<MutableList<String>> = mutableListOf()
    var choices: MutableList<Instrument> = mutableListOf()

    var select = 0

    override fun affect(a: Creature) { }

    override fun getEnemy(): Actor {
        return Actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): Biome {
        val index = Random.nextInt(0, next[select].size)
        return LocationFactory.getByName(next[select][index])
    }

    override fun activateTool(id: Int, us: Player, enemy: Actor): Boolean {
        if(choices[id].header == "")
            return false

        select = id
        return true
    }

    override fun choices(us: Player): List<Instrument> = choices
}
