package com.lesterade.oneira.gameHandling.weapons

import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.creature
import com.lesterade.oneira.gameHandling.weapons.actionEffects.ActionEffect
import com.lesterade.oneira.gameHandling.weapons.damageEffects.DamageEffect
import com.lesterade.oneira.gameHandling.weapons.healEffects.HealEffect
import kotlinx.serialization.Serializable

interface instrument {
    fun attack(from : creature, to : creature, located : biome): instrument
    fun getSignature(from: creature, to: creature, located: biome): Pair<Float, Float>

    val name: String
    val header: String
    val description : String

    val imageId: String
    val damageBoost: Float?
    
    val transmutable: Boolean
        get() = false
}

@Serializable
open class Weapon(val damage: Float? = null,
                  val element: Element = Element.fire,
                  override var name : String = "",
                  override var header: String,
                  override var description : String,
                  val heal: Float = 0f,
                  val turnsInto: String? = null,
                  override val damageBoost: Float? = null,
                  val transmutesInto: List<String>? = null): instrument {
    var isLifesteal = false

    override val transmutable
        get() = (transmutesInto != null)

    var actionEffects: MutableList<ActionEffect> = mutableListOf()
    var damageEffects: MutableList<DamageEffect> = mutableListOf()
    var healEffects: MutableList<HealEffect> = mutableListOf()

    open fun getDamage(from: creature, to: creature, located: biome): Float {
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

    open fun getHeal(from: creature, to: creature, located: biome): Float {
        if(isLifesteal)
            return getDamage(from, to, located)

        var healTmp = heal
        healEffects.forEach {
            healTmp = it.calculate(from, to, located, healTmp)
        }
        return healTmp
    }

    constructor(other: Weapon) : this(other.damage,
        other.element, other.name, other.header, other.description, other.heal, other.turnsInto, other.damageBoost) {
        isLifesteal = other.isLifesteal
        actionEffects = other.actionEffects
        damageEffects = other.damageEffects
    }

    override fun attack(from: creature, to: creature, located: biome): instrument {
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

    override fun getSignature(from: creature, to: creature, located: biome): Pair<Float, Float> {
        var hitDmg = getDamage(from, to, located)
        hitDmg += hitDmg * 0.2f * located.element.effect(element)
        hitDmg += hitDmg * 0.2f * element.effect(to.element)

        return Pair(hitDmg, getHeal(from, to, located))
    }

    override val imageId
        get() = name

    open fun otherEffects(from: creature, to: creature, located: biome) {
        actionEffects.forEach{ it.effect(from, to, located) }
    }

    fun transmute(dir: Int): instrument? {
        if (transmutesInto == null)
            return null

        println(transmutesInto)

        return ToolFactory.getByName(transmutesInto[(dir + 1) shr 1])
    }
}

class unknownWeapon(): instrument {
    override fun attack(from: creature, to: creature, located: biome): instrument {
        return this
    }

    override fun getSignature(from: creature, to: creature, located: biome): Pair<Float, Float> {
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
