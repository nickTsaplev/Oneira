package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Actor
import com.lesterade.oneira.gameHandling.ActorFactory
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Player
import com.lesterade.oneira.gameHandling.biomes.encounters.Encounter
import com.lesterade.oneira.gameHandling.format
import com.lesterade.oneira.gameHandling.weapons.Instrument
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class VaryingLoc(override val element: Element,
                  override var name: String = "",
                  var depth: Int,
                  override var header: String,
                  override var desc: String,
                  val encounterProbability: Float): Biome {
    var enemies: MutableList<String> = mutableListOf("costable")
    var encounters: MutableList<Encounter> = mutableListOf()

    var next: MutableList<String> = mutableListOf()

    var pattern = "%s (%d steps left)"

    var inEncounter: Boolean = false
        private set

    private var currentEncounter: Encounter? = null

    override val dispHeader
        get() = header + pattern.format(depth + 1)

    override fun affect(a: Creature) { }

    override fun getEnemy(): Actor {
        if (encounters.isNotEmpty() && Random.nextFloat() < encounterProbability)
        {
            val index = Random.nextInt(0, encounters.size)
            depth -= 1
            currentEncounter = encounters.removeAt(index)

            inEncounter = true

            return Actor(Creature(currentEncounter!!.enemyName))
        }
        val index = Random.nextInt(0, enemies.size)
        val tmp = enemies[index]
        depth -= 1

        inEncounter = false

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
        if (inEncounter) {
            if(currentEncounter?.outcomes?.get(id)?.choice?.name == "none")
                return false

            currentEncounter?.outcomes?.get(id)?.activate(us)
            return true
        }
        return (enemy.dead)
    }

    override fun choices(us: Player): List<Instrument> = if(inEncounter) currentEncounter!!.outcomes.map { it.choice } else us.hand
}