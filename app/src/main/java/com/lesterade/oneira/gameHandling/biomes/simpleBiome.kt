package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.ActorFactory
import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.actor
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.format
import com.lesterade.oneira.gameHandling.weapons.instrument
import com.lesterade.oneira.gameHandling.player
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class simpleBiome(override val element: Element, override var name: String = "", var depth: Int, override var header: String, override var desc: String):
    biome {
    var deck: MutableList<String> = mutableListOf("costable")
    var next: MutableList<String> = mutableListOf()

    var pattern = "%s (%d steps left)"

    override val dispHeader
        get() = header + pattern.format(depth + 1)
    //= header + " (" + (depth + 1).toString() + " steps left)"

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        val index = Random.nextInt(0, deck.size)
        val tmp = deck[index]
        depth -= 1
        return ActorFactory.getByName(tmp)
    }

    override fun advance(): biome? {
        if(depth <= 0) {
            val index = Random.nextInt(0, next.size)
            return LocationFactory.getByName(next[index])
        }
        return null
    }

    override fun activateTool(id: Int, us: player, enemy: actor): Boolean {
        return (enemy.dead)
    }

    override fun choices(us: player): List<instrument> = us.hand
}
