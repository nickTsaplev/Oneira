package com.lesterade.oneira.gameHandling.weapons

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.Creature
import com.lesterade.oneira.gameHandling.weapons.actionEffects.ActionEffect
import com.lesterade.oneira.gameHandling.weapons.damageEffects.DamageEffect
import com.lesterade.oneira.gameHandling.weapons.healEffects.HealEffect
import kotlinx.serialization.Serializable

interface Instrument {
    fun attack(from : Creature, to : Creature, located : Biome): Instrument
    fun getSignature(from: Creature, to: Creature, located: Biome): Pair<Float, Float>

    val name: String
    val header: String
    val description : String

    val imageId: String
    val damageBoost: Float?
    
    val transmutable: Boolean
        get() = false
}

@Serializable
open class Weapon(private val damage: Float? = null,
                  val element: Element = Element.fire,
                  override var name : String = "",
                  override var header: String,
                  override var description : String,
                  private val heal: Float = 0f,
                  private val turnsInto: String? = null,
                  override val damageBoost: Float? = null,
                  private val transmutesInto: List<String>? = null): Instrument {
    var isLifesteal = false

    override val transmutable
        get() = (transmutesInto != null)

    var actionEffects: MutableList<ActionEffect> = mutableListOf()
    var damageEffects: MutableList<DamageEffect> = mutableListOf()
    var healEffects: MutableList<HealEffect> = mutableListOf()

    open fun getDamage(from: Creature, to: Creature, located: Biome): Float {
        if (damage != null) {
            var dmg: Float = damage
            damageEffects.forEach {
                dmg = it.calculate(from, to, located, dmg)
            }
            if(dmg != 0f)
                return dmg + from.damageBoost
        }
        return 0f
    }

    open fun getHeal(from: Creature, to: Creature, located: Biome): Float {
        if(isLifesteal)
            return getDamage(from, to, located)

        var healTmp = heal
        healEffects.forEach {
            healTmp = it.calculate(from, to, located, healTmp)
        }
        return healTmp
    }

    override fun attack(from: Creature, to: Creature, located: Biome): Instrument {
        var hitDmg = getDamage(from, to, located)
        hitDmg += hitDmg * 0.2f * located.element.effect(element)
        hitDmg += hitDmg * 0.2f * element.effect(to.element)
        to.hit(hitDmg)

        from.heal(getHeal(from, to, located))

        otherEffects(from, to, located)

        if(turnsInto == null)
            return this
        return ToolFactory.getByName(turnsInto)
    }

    override fun getSignature(from: Creature, to: Creature, located: Biome): Pair<Float, Float> {
        var hitDmg = getDamage(from, to, located)
        hitDmg += hitDmg * 0.2f * located.element.effect(element)
        hitDmg += hitDmg * 0.2f * element.effect(to.element)

        return Pair(hitDmg, getHeal(from, to, located))
    }

    override val imageId
        get() = name

    open fun otherEffects(from: Creature, to: Creature, located: Biome) {
        actionEffects.forEach{ it.effect(from, to, located) }
    }

    fun transmute(dir: Int): Instrument? {
        if (transmutesInto == null)
            return null

        println(transmutesInto)

        return ToolFactory.getByName(transmutesInto[(dir + 1) shr 1])
    }
}

class UnknownWeapon : Instrument {
    override fun attack(from: Creature, to: Creature, located: Biome): Instrument {
        return this
    }

    override fun getSignature(from: Creature, to: Creature, located: Biome): Pair<Float, Float> {
        return Pair(0f, 0f)
    }

    override val damageBoost: Float? = null

    override val header
        get() = "???"

    override val name
        get() = ""

    override val description
        get() = "???"

    override val imageId = "unknown"
}
