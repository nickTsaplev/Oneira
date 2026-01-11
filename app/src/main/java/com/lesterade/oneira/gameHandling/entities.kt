package com.lesterade.oneira.gameHandling

import com.lesterade.oneira.gameHandling.weapons.Instrument
import com.lesterade.oneira.gameHandling.weapons.UnknownWeapon
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
open class Creature(val maxhp : Float,
                    val element: Element,
                    var name: String = "",
                    var header: String,
                    var hp : Float = maxhp,

                    var bleed : Float = 0f,
                    var poison : Float = 0f,

                    var innerFire : Int = 3) {



    val percent_hp
        get() = hp/maxhp

    val dead
        get() = (hp <= 0f)

    constructor(other: Creature): this(other.maxhp, other.element, other.name, other.header, other.hp, other.bleed, other.poison, other.innerFire)

    fun hit(dmg: Float) {
        hp -= dmg
        hp -= bleed
        if (hp <= 0) {
            hp = 0f
        }
    }

    fun heal(dmg: Float) {
        if(dead)
            return

        hp += dmg
        hp -= poison
        if(hp > maxhp)
            hp = maxhp
    }

    open val damageBoost = 0f
}

open class Actor(cr: Creature): Creature(cr) {
    protected var deck: MutableList<Instrument> = mutableListOf(UnknownWeapon(), UnknownWeapon(), UnknownWeapon())
    protected var discarded: MutableList<Instrument> = mutableListOf()

    val cards
        get() = deck

    constructor(maxhp : Float,
                element: Element,
                name: String = "",
                header: String): this(Creature(maxhp, element, name, header))

    fun draw(): Instrument {
        if(deck.size == 0)
            deck = discarded

        val index = Random.nextInt(0, deck.size)
        val tmp = deck[index]
        deck.removeAt(index)
        return tmp
    }

    // Redraw policy - use a discard pile or not
    fun discard(card: Instrument) {
        discarded.add(card)
    }

    fun loadCards(cards: MutableList<Instrument>) {
        deck = cards
    }

    fun takeCards(other: Actor)
    {
        loadCards(other.deck)
    }
}

@Serializable
class TransmutationDirection(val main: Boolean, val dir: Boolean) {
    fun apply(element: Element): Element {
        return element.transmute(main, dir)
    }
}

class Player(cr: Creature) : Actor(cr) {
    var hand: MutableList<Instrument> = mutableListOf()

    var inventory: MutableList<Instrument> = mutableListOf()

    var directions: List<TransmutationDirection> = listOf()

    fun redraw() {
        while(hand.size < 3 && ((deck.size + discarded.size) > 0)) {
            hand.add(draw())
        }
    }

    override val damageBoost: Float
        get() {
            var ans = 0f
            hand.forEach {
                it.damageBoost?.also { ans += it }
            }
            return ans
        }
}
