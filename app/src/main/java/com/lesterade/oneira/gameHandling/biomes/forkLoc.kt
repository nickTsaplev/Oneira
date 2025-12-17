package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.actor
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.weapons.instrument
import com.lesterade.oneira.gameHandling.player
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class forkLoc(override val element: Element,
              override var name: String = "",
              override var header: String,
              override var desc: String): biome {
    var next: MutableList<MutableList<String>> = mutableListOf()
    var choices: MutableList<instrument> = mutableListOf()

    var select = 0

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): biome? {
        val index = Random.nextInt(0, next[select].size)
        return LocationFactory.getByName(next[select][index])
    }

    override fun activateTool(id: Int, us: player, enemy: actor): Boolean {
        if(choices[id].header == "")
            return false

        select = id
        return true
    }

    override fun choices(us: player): List<instrument> = choices
}
