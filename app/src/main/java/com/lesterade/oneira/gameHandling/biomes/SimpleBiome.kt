package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.ActorFactory
import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Actor
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.format
import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.gameHandling.Player
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class SimpleBiome(override val element: Element,
                  override var name: String = "",
                  var depth: Int,
                  override var header: String,
                  override var desc: String): Biome {
    var deck: MutableList<String> = mutableListOf("costable")
    var next: MutableList<String> = mutableListOf()

    var pattern = "%s (%d steps left)"

    override val dispHeader
        get() = header + pattern.format(depth + 1)

    override fun affect(a: Creature) { }

    override fun getEnemy(): Actor {
        val index = Random.nextInt(0, deck.size)
        val tmp = deck[index]
        depth -= 1
        return ActorFactory.getByName(tmp)
    }

    override fun advance(): Biome? {
        if(depth <= 0) {
            val index = Random.nextInt(0, next.size)
            return LocationFactory.getByName(next[index])
        }
        return null
    }

    override fun activateTool(id: Int, us: Player, enemy: Actor): Boolean {
        return (enemy.dead)
    }

    override fun choices(us: Player): List<Instrument> = us.hand
}
