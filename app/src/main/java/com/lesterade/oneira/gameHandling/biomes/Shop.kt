package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.actor
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.weapons.instrument
import com.lesterade.oneira.gameHandling.player
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
enum class AddType {Hand, Inventory}

@Serializable
class Shop(override val element: Element,
           override var name: String = "",
           override var header: String,
           override var desc: String,
           val addType: AddType = AddType.Hand): biome {
    var next: MutableList<String> = mutableListOf()
    var sold: MutableList<instrument> = mutableListOf()
    var currentlySold: MutableList<instrument> = mutableListOf()

    fun pickWares() {
        val index = Random.nextInt(0, sold.size)

        currentlySold.add(sold[index])


        var index2 = Random.nextInt(0, sold.size)
        while(index2 == index)
            index2 = Random.nextInt(0, sold.size)

        currentlySold.add(sold[index2])

        var index3 = Random.nextInt(0, sold.size)
        while(index3 == index || index3 == index2)
            index3 = Random.nextInt(0, sold.size)

        currentlySold.add(sold[index3])

        //currentlySold.add(ToolFactory.getByName("nothing"))
    }

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): biome? {
        val index = Random.nextInt(0, next.size)
        return LocationFactory.getByName(next[index])
    }

    override fun activateTool(id: Int, us: player, enemy: actor): Boolean {
        us.hp = us.maxhp // Shops restore hp
        us.poison = 0f
        us.bleed = 0f

        val toAdd = currentlySold[id]
        if(toAdd.name != "nothing")
            when(addType) {
                AddType.Hand -> us.discard(toAdd)
                AddType.Inventory -> us.inventory.add(toAdd)
            }

        return true
    }

    override fun choices(us: player): List<instrument> = currentlySold
}
