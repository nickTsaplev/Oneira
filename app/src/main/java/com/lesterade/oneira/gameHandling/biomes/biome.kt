package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.actor
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.weapons.instrument
import com.lesterade.oneira.gameHandling.player

interface biome {
    val element: Element
    val name: String
    val header: String
    val desc: String

    val dispHeader
        get() = header

    fun affect(a : creature)
    fun getEnemy(): actor
    fun advance(): biome?

    fun activateTool(id: Int, us: player, enemy: actor): Boolean
    fun choices(us: player): List<instrument>
}
