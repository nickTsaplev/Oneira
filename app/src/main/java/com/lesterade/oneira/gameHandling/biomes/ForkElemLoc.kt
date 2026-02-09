package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Actor
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Player
import com.lesterade.oneira.gameHandling.weapons.Instrument
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class ForkElemLoc(override val element: Element,
              override var name: String = "",
              override var header: String,
              override var desc: String): Biome {


    @Serializable
    class Route(val name: String) {
        @Serializable
        class Option(val element: Element, val next: String)

        val next: MutableList<Option> = mutableListOf()

        fun leadOn(affiliations: Map<Element, Int>): String {
            if (affiliations[next[0].element]!! > affiliations[next[1].element]!!)
                return next[0].next
            if (affiliations[next[0].element]!! < affiliations[next[1].element]!!)
                return next[1].next
            return next[Random.nextInt(0,2)].next
        }
    }

    var next: MutableList<Route> = mutableListOf()
    var choices: MutableList<Instrument> = mutableListOf()

    var affiliations: Map<Element, Int> = mapOf()

    var select = 0

    override fun affect(a: Creature) { }

    override fun getEnemy(): Actor {
        return Actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): Biome {
        return LocationFactory.getByName(next[select].leadOn(affiliations))
    }

    override fun activateTool(id: Int, us: Player, enemy: Actor): Boolean {
        if(choices[id].header == "")
            return false

        affiliations = us.affiliations

        select = id
        return true
    }

    override fun choices(us: Player): List<Instrument> = choices
}
