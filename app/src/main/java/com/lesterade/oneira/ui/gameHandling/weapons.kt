package com.lesterade.oneira.ui.gameHandling

import kotlinx.serialization.Serializable
import kotlin.random.Random

interface instrument {
    fun attack(from : creature, to : creature, located : biome): instrument
    fun getSignature(from: creature, to: creature, located: biome): Pair<Float, Float>

    val name: String
    val header: String
    val description : String

    val imageId: String
}

@Serializable
open class simpleWeapon(val damage: Float, val element: Element, override var name : String = "", override val header: String, override val description : String,
                        val heal: Float = 0f,
                        val turnsInto: String? = null): instrument {
    var isLifesteal = false

    open fun getDamage(from: creature, to: creature, located: biome): Float {
        return damage + from.damageBoost
    }
    open fun getHeal(from: creature, to: creature, located: biome): Float {
        if(isLifesteal)
            return getDamage(from, to, located)
        return heal
    }

    constructor(other: simpleWeapon) : this(other.damage,
        other.element, other.name, other.header, other.description, other.heal, other.turnsInto)

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

    }
}

class boostingWeapon(base: simpleWeapon, val damageBoost: Float): simpleWeapon(base) {

}

class peacemakingWeapon(base: simpleWeapon): simpleWeapon(base) {
    var peaceWith = mutableListOf<String>()

    override fun otherEffects(from: creature, to: creature, located: biome) {
        if(to.name in peaceWith)
            if(Random.nextInt(0, 2) == 0)
                to.hp = 0f
    }
}

class boostedWeapon(base: simpleWeapon, val counted: List<String>): simpleWeapon(base) {
    override fun getDamage(from: creature, to: creature, located: biome): Float {
        if(from is player) {
            var hitDmg = 0f
            from.deck.forEach { if(it.name in counted) hitDmg += 1f}
            from.hand.forEach { if(it.name in counted) hitDmg += 1f}
            hitDmg *= damage

            return hitDmg + from.damageBoost
        }
        return damage
    }
}

class fieryWeapon(base: simpleWeapon, val fireEff: Int): simpleWeapon(base) {
    override fun getDamage(from: creature, to: creature, located: biome): Float {
        if(damage == 0f || (from.innerFire + fireEff < 0))
            return from.innerFire.toFloat() + from.damageBoost
        return damage
    }

    override fun otherEffects(from: creature, to: creature, located: biome) {
        if(fireEff == 0)
            from.innerFire = 0
        else {
            if(from.innerFire + fireEff < 0)
                return
            from.innerFire += fireEff
        }
    }
}

class handBurner(base: simpleWeapon, val fireEff: Int): simpleWeapon(base) {
    override fun otherEffects(from: creature, to: creature, located: biome) {
        if(fireEff == 0)
            from.innerFire = 0
        else
            from.innerFire += fireEff

        if(from is player) {
            if(from.hand.size + from.deck.size < 4) {
                from.hp = 0f
                return
            }

            from.hand.removeAt(Random.nextInt(0, from.hand.size))
            from.redraw()
        }
    }
}

class redrawingWeapon(base: simpleWeapon): simpleWeapon(base) {
    override fun otherEffects(from: creature, to: creature, located: biome) {
        if(from is player) {
            from.deck.addAll(from.hand)
            from.hand = mutableListOf()
            from.redraw()
        }
    }
}

class woodenWeapon(base: simpleWeapon): simpleWeapon(base) {
    override fun getDamage(from: creature, to: creature, located: biome): Float {
        if(damage - from.innerFire.toFloat() > 0f)
            return damage + from.damageBoost - from.innerFire.toFloat()
        return 0f
    }
}

class unknownWeapon(): instrument {
    override fun attack(from: creature, to: creature, located: biome): instrument {
        return this
    }

    override fun getSignature(from: creature, to: creature, located: biome): Pair<Float, Float> {
        return Pair(0f, 0f)
    }

    override val header
        get() = "???"

    override val name
        get() = ""

    override val description
        get() = "???"

    override val imageId = "unknown"
}
