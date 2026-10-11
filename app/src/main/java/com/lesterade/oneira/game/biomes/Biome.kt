package com.lesterade.oneira.game.biomes

import com.lesterade.oneira.game.Element
import com.lesterade.oneira.game.Actor
import com.lesterade.oneira.game.Creature
import com.lesterade.oneira.game.Player
import com.lesterade.oneira.game.weapons.Instrument

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
