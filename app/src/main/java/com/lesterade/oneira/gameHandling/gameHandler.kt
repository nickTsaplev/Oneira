package com.lesterade.oneira.gameHandling

import android.content.Context
import com.lesterade.oneira.gameHandling.biomes.LocationFactory
import com.lesterade.oneira.gameHandling.biomes.biome
import com.lesterade.oneira.gameHandling.biomes.endingLoc
import com.lesterade.oneira.gameHandling.biomes.simpleBiome
import com.lesterade.oneira.gameHandling.msgFormatting.msgFormatter
import com.lesterade.oneira.gameHandling.msgFormatting.msgPatterns
import com.lesterade.oneira.gameHandling.weapons.ToolFactory
import com.lesterade.oneira.gameHandling.weapons.Weapon
import com.lesterade.oneira.gameHandling.weapons.instrument
import com.lesterade.oneira.gameHandling.weapons.unknownWeapon
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.random.Random

@Serializable
open class creature(val maxhp : Float,
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

    constructor(other: creature): this(other.maxhp, other.element, other.name, other.header, other.hp, other.bleed, other.poison, other.innerFire)

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

open class actor(cr: creature): creature(cr) {
    protected var deck: MutableList<instrument> = mutableListOf(unknownWeapon(), unknownWeapon(), unknownWeapon())
    protected var discarded: MutableList<instrument> = mutableListOf()

    val cards
        get() = deck

    constructor(maxhp : Float,
                element: Element,
                name: String = "",
                header: String): this(creature(maxhp, element, name, header)) {}

    fun draw(): instrument {
        if(deck.size == 0)
            deck = discarded

        val index = Random.nextInt(0, deck.size)
        val tmp = deck[index]
        deck.removeAt(index)
        return tmp
    }

    // Redraw policy - use a discard pile or not
    fun discard(card: instrument) {
        discarded.add(card)
    }

    fun loadCards(cards: MutableList<instrument>) {
        deck = cards
    }

    fun takeCards(other: actor)
    {
        loadCards(other.deck)
    }
}

class player(cr: creature) : actor(cr) {
    var hand: MutableList<instrument> = mutableListOf()

    var inventory: MutableList<instrument> = mutableListOf()

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

class GameMaster(val patterns: msgPatterns) {
    var scene: biome = simpleBiome(Element.fire, "", 0, "", "")

    var us = player(creature(0f, Element.fire, "", ""))
    var enemy = scene.getEnemy()

    var msg = ""

    val ended
        get() = (scene is endingLoc)

    init {
        us.hand = mutableListOf()
        us.redraw()
    }

    fun startNewGame(playName: String = "player") {
        scene = LocationFactory.getByName("start")
        us = ActorFactory.getByName(playName) as player
        enemy = scene.getEnemy()

        us.hand = mutableListOf()
        us.redraw()
    }

    val cards: List<instrument>
        get() {
            return us.cards + us.hand
        }

    fun save(): JsonObject {
        val handSave = JsonArray(us.hand.map{ JsonPrimitive(it.name) })
        val deckSave = JsonArray(us.cards.map {JsonPrimitive(it.name)})

        val ans = JsonObject(mapOf("hand" to handSave,
            "deck" to deckSave,
            "player" to Json.encodeToJsonElement(us as creature),
            "enemy" to Json.encodeToJsonElement(enemy as creature),
            "scene" to JsonPrimitive(scene.name)
            ))

        return ans
    }

    fun load(from: JsonObject) {
        us = player(Json.decodeFromJsonElement<creature>(from["player"]!!))

        us.hand = mutableListOf()

        for (i in 0..<from["hand"]!!.jsonArray.size) {
            us.hand.add(ToolFactory.getByName(from["hand"]!!.jsonArray[i].jsonPrimitive.content))
        }

        val tmp = mutableListOf<instrument>()
        for (i in 0..<from["deck"]!!.jsonArray.size) {
           tmp.add(ToolFactory.getByName(from["deck"]!!.jsonArray[i].jsonPrimitive.content))
        }
        us.loadCards(tmp)

        scene = LocationFactory.getByName(from["scene"]!!.jsonPrimitive.content)
        enemy = actor(Json.decodeFromJsonElement<creature>(from["enemy"]!!))
        enemy.takeCards(ActorFactory.getByName(enemy.name))
    }

    val display: List<instrument>
        get() = scene.choices(us)


    fun enemyTurn(): String {
        if(scene !is simpleBiome)
            return ""

        var tmp = enemy.draw()

        val (hit_en, heal_en) = tmp.getSignature(enemy, us, scene)
        tmp = tmp.attack(enemy, us, scene)

        enemy.discard(tmp)

        if(us.dead) {
            scene = LocationFactory.getByName("ending_over")
        }

        val enemyMsg = msgFormatter(enemy.header, patterns.and_name, patterns.do_pattern)
        enemyMsg.addEffect(patterns.hits_pattern, hit_en)
        enemyMsg.addEffect(patterns.suffers_pattern, if (heal_en != 0f) enemy.poison else 0f)
        enemyMsg.addEffect(patterns.heals_pattern, heal_en)

        return enemyMsg.format()
    }

    fun activateTool(id: Int) {
        msg = ""
        if(scene is simpleBiome) {
            var toAdd = us.hand[id]
            us.hand.removeAt(id)

            val (hit, heal) = toAdd.getSignature(us, enemy, scene)
            toAdd = toAdd.attack(us, enemy, scene)

            us.discard(toAdd)
            us.hand.add(us.draw())

            if (enemy.dead) {
                val tmp = scene.advance()
                if (tmp != null)
                    scene = tmp
                enemy = scene.getEnemy()
                return
            }

            var tmp = enemy.draw()

            val (hit_en, heal_en) = tmp.getSignature(enemy, us, scene)
            tmp = tmp.attack(enemy, us, scene)

            enemy.discard(tmp)

            if(us.dead) {
                scene = LocationFactory.getByName("ending_over")
            }

            val enemyMsg = msgFormatter(enemy.header, patterns.and_name, patterns.does_pattern)
            enemyMsg.addEffect(patterns.hits_pattern, hit_en)
            enemyMsg.addEffect(patterns.bleeds_pattern, if (hit != 0f) enemy.bleed else 0f)
            enemyMsg.addEffect(patterns.suffers_pattern, if (heal_en != 0f) enemy.poison else 0f)
            enemyMsg.addEffect(patterns.heals_pattern, heal_en)

            val usMsg = msgFormatter(patterns.self_name, patterns.and_name, patterns.do_pattern)
            usMsg.addEffect(patterns.hit_pattern, hit)
            usMsg.addEffect(patterns.bleed_pattern, if (hit_en != 0f) us.bleed else 0f)
            usMsg.addEffect(patterns.suffer_pattern, if (heal != 0f) us.poison else 0f)
            usMsg.addEffect(patterns.heal_pattern, heal)

            msg = usMsg.format() + "\n" + enemyMsg.format()

            return
        }

        if(scene.activateTool(id, us, enemy)) {
            val tmp = scene.advance()
            if (tmp != null)
                scene = tmp
            enemy = scene.getEnemy()
        }
    }

    fun useCollectible(id: Int) {
        us.inventory[id].attack(us, enemy, scene)
        us.inventory.removeAt(id)
    }

    fun transmuteTool(id: Int, dir: Int) {
        (us.hand[id] as? Weapon)?.transmute(dir)?.let {
            us.hand[id] = it
            msg = enemyTurn()
        }
    }
}

data class dispTool(var name: String = "none",
                    var header: String = "",
                    var desc: String = "",
                    var transmutable: Boolean = false)

class GameHandler(val master : GameMaster) {
    var tools = buildList { master.display.forEach{add(dispTool(it.imageId, it.header, it.description))} }
    var inventory = listOf<dispTool>()
    var msg = ""

    val sceneHead
        get() = master.scene.dispHeader

    val sceneDesc
        get() = master.scene.desc

    fun startGame(playName: String = "player") {
        master.startNewGame(playName)
    }

    val creatureName
        get() = if(master.scene is simpleBiome)
            master.enemy.name
        else
            null

    val sceneName
        get() = master.scene.name

    val ended
        get() = master.ended

    val left_bar
        get() = master.us.percent_hp

    val right_bar
        get() = master.enemy.percent_hp

    fun update() {
        tools = buildList { master.display.forEach{add(dispTool(it.imageId, it.header, it.description, it.transmutable && master.scene is simpleBiome))} }
        inventory = buildList { master.us.inventory.forEach{add(dispTool(it.imageId, it.header, it.description, it.transmutable && master.scene is simpleBiome))} }
        msg = master.msg
    }

    fun activateTool(id: Int) {
        master.activateTool(id)

        update()
    }

    fun transmuteTool(id: Int, dir: Int) {
        master.transmuteTool(id, dir)

        update()
    }

    fun useItem(id: Int) {
        master.useCollectible(id)

        update()
    }
}

fun loadGame(context: Context, master: GameMaster) {
    if(!context.fileList().contains("save.json"))
        return

    val stream = context.openFileInput("save.json")

    var text = String()
    while(stream.available() != 0)
        text += String(byteArrayOf(stream.read().toByte()))
    val data = parseToJsonElement(text).jsonObject

    master.load(data)
}

fun saveGame(context: Context, master: GameMaster) {
    if(context.fileList().contains("save.json"))
        context.deleteFile("save.json")
    val stream = context.openFileOutput("save.json", 0)

    val obj = master.save()
    stream.write(obj.toString().toByteArray())
    stream.close()
}