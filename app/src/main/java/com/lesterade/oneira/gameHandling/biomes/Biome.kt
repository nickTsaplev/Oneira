package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.Actor
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.gameHandling.Player

interface Biome {
    val element: Element
    val name: String
    val header: String
    val desc: String

    val dispHeader
        get() = header

    fun affect(a : Creature)
    fun getEnemy(): Actor
    fun advance(): Biome?

    fun activateTool(id: Int, us: Player, enemy: Actor): Boolean
    fun choices(us: Player): List<Instrument>
}
