package com.lesterade.oneira.ui.gameHandling

import android.content.Context
import org.json.JSONObject

import com.lesterade.oneira.ui.dashboard.TripView

import com.lesterade.oneira.ui.dashboard.DashboardFragment
import com.lesterade.oneira.ui.toolDisplayLayout.dispTool
import kotlinx.serialization.Serializable
import org.json.JSONArray
import kotlin.random.Random

@Serializable
open class creature(val maxhp : Float,
                    val element: Element,
                    var name: String = "",
                    val header: String) {
    var hp = maxhp

    var innerFire = 3

    val percent_hp
        get() = hp/maxhp

    var dead = false

    constructor(other: creature): this(other.maxhp, other.element, other.name, other.header)

    fun hit(dmg: Float) {
        hp -= dmg
        if (hp <= 0) {
            hp = 0f
            dead = true
        }
    }

    fun heal(dmg: Float) {
        if(dead)
            return

        hp += dmg
        if(hp > maxhp)
            hp = maxhp
    }

    open val damageBoost = 0f
}

open class actor(cr: creature): creature(cr) {
    var deck: MutableList<instrument> = mutableListOf(unknownWeapon(), unknownWeapon(), unknownWeapon())

    constructor(maxhp : Float,
                element: Element,
                name: String = "",
                header: String): this(creature(maxhp, element, name, header)) {}

    fun draw(): instrument {
        val index = Random.nextInt(0, deck.size)
        val tmp = deck[index]
        deck.removeAt(index)
        return tmp
    }
}

class player(cr: creature) : actor(cr) {
    var hand: MutableList<instrument> = mutableListOf()

    fun redraw() {
        while(hand.size < 3) {
            hand.add(draw())
        }
    }

    override val damageBoost: Float
        get() {
            var ans = 0f
            hand.forEach {
                if(it is boostingWeapon)
                    ans += it.damageBoost
            }
            return ans
        }
}

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
}

@Serializable
class simpleBiome(override val element: Element, override var name: String = "", var depth: Int, override val header: String, override val desc: String): biome {
    var deck: MutableList<String> = mutableListOf("costable")
    var next: MutableList<String> = mutableListOf()

    override val dispHeader
        get() = header + " (" + (depth + 1).toString() + " steps left)"

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        val index = Random.nextInt(0, deck.size)
        val tmp = deck[index]
        depth -= 1
        return ActorFactory.getByName(tmp)
    }

    override fun advance(): biome? {
        if(depth <= 0) {
            val index = Random.nextInt(0, next.size)
            return LocationFactory.getByName(next[index])
        }
        return null
    }
}

@Serializable
class shop(override val element: Element,
           override var name: String = "",
           override val header: String,
           override val desc: String): biome {
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

        currentlySold.add(ToolFactory.getByName("nothing"))
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
}

@Serializable
class loseLoc(override val element: Element,
              override var name: String = "",
              override val header: String,
              override val desc: String): biome {
    var next: MutableList<String> = mutableListOf()

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): biome? {
        val index = Random.nextInt(0, next.size)
        return LocationFactory.getByName(next[index])
    }
}

@Serializable
class endingLoc(override val element: Element,
                override var name: String = "",
                override val header: String,
                override val desc: String): biome {
    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): biome? {
        return null
    }
}

@Serializable
class forkLoc(override val element: Element,
              override var name: String = "",
              override val header: String,
              override val desc: String): biome {
    var next: MutableList<MutableList<String>> = mutableListOf()
    var choices: MutableList<instrument> = mutableListOf()

    var select = 0

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, Element.fire, "?", "?")
    }

    override fun advance(): biome? {
        val index = Random.nextInt(0, next[select].size)
        return LocationFactory.getByName(next[select][index])
    }
}

object GameMaster {
    var scene: biome = LocationFactory.getByName("start")

    var us = ActorFactory.getByName("player") as player
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
            return us.deck + us.hand
        }

    fun save(): JSONObject {
        val ans = JSONObject()
        val handSave = JSONArray()
        us.hand.forEach {
            handSave.put(it.name)
        }
        ans.put("hand", handSave)

        val deckSave = JSONArray()
        us.deck.forEach {
            deckSave.put(it.name)
        }

        ans.put("deck", handSave)

        ans.put("name", us.name)
        ans.put("hp", us.hp)
        ans.put("scene", scene.name)

        return ans
    }

    fun load(from: JSONObject) {
        us.hand = mutableListOf()

        for (i in 0..<from.getJSONArray("hand").length()) {
            us.hand.add(ToolFactory.getByName(from.getJSONArray("hand").getString(i)))
        }

        us = ActorFactory.getByName(from.getString("name")) as player

        us.deck = mutableListOf()
        for (i in 0..<from.getJSONArray("deck").length()) {
            us.deck.add(ToolFactory.getByName(from.getJSONArray("deck").getString(i)))
        }

        us.hp = from.getDouble("hp").toFloat()

        scene = LocationFactory.getByName(from.getString("scene"))
    }

    val display: List<instrument>
        get() {
            when(scene) {
                is shop -> return (scene as shop).currentlySold
                is simpleBiome -> return us.hand
                is loseLoc -> return us.hand
                is forkLoc -> return  (scene as forkLoc).choices
            }
            return us.hand
        }

    fun activateTool(id: Int) {
        msg = ""
        if(scene is shop) {
            us.hp = us.maxhp // Shops restore hp

            val tmp = scene as shop

            val toAdd = tmp.currentlySold[id]
            if(toAdd.name != "nothing")
                us.deck.add(toAdd)

            scene = scene.advance()!!
            enemy = scene.getEnemy()
            return
        }
        if(scene is loseLoc) {
            us.hand.removeAt(id)
            us.hand.add(us.draw())

            scene = scene.advance()!!
            enemy = scene.getEnemy()
            return
        }
        if(scene is forkLoc) {
            if(display[id].header != "") {
                (scene as forkLoc).select = id
                scene = scene.advance()!!
                enemy = scene.getEnemy()
            }
            return
        }
        if(scene is simpleBiome) {
            var toAdd = us.hand[id]
            us.hand.removeAt(id)

            val (hit, heal) = toAdd.getSignature(us, enemy, scene)
            toAdd = toAdd.attack(us, enemy, scene)

            if (enemy.dead) {
                val tmp = scene.advance()
                if (tmp != null)
                    scene = tmp
                enemy = scene.getEnemy()
            }

            us.deck.add(toAdd)
            us.hand.add(us.draw())

            var tmp = enemy.draw()

            val (hit_en, heal_en) = tmp.getSignature(enemy, us, scene)
            tmp = tmp.attack(enemy, us, scene)

            enemy.deck.add(tmp)

            if(us.dead) {
                scene = LocationFactory.getByName("ending_over")
            }


            msg = ""
            if(hit != 0f)
                msg += "You hit for %.2f. ".format(hit)
            if(heal != 0f)
                msg += "You heal for %.2f.".format(heal)

            msg += "\n"
            if(hit_en != 0f) {
                msg += enemy.header + " hits for %.2f".format(hit_en)
                if (heal_en != 0f)
                    msg += " and heals for %.2f".format(heal_en)
            } else if (heal_en != 0f)
                msg += enemy.header + " heals for %.2f".format(heal_en)

            return
        }
    }
}

fun loadGame(context: Context) {
    if(!context.fileList().contains("save.json"))
        return

    val stream = context.openFileInput("save.json")

    var text = String()
    while(stream.available() != 0)
        text += String(byteArrayOf(stream.read().toByte()))
    val data = JSONObject(text)

    GameMaster.load(data)
}

fun saveGame(context: Context) {
    if(context.fileList().contains("save.json"))
        context.deleteFile("save.json")
    val stream = context.openFileOutput("save.json", 0)

    val obj = GameMaster.save()
    stream.write(obj.toString().toByteArray())
    stream.close()
}


data class sceneInfo(val name: String = "none",
                     val creatureName: String? = null,
                     val sceneHead: String = "",
                     val sceneDesc: String = "",
                     val left_bar: Float = 0f,
                     val right_bar: Float = 0f,
                     val msg: String = "")

class GameHandler {
    var tools = buildList { GameMaster.display.forEach{add(dispTool(it.imageId, it.header, it.description))} }
    var msg = ""

    val sceneHead
        get() = GameMaster.scene.dispHeader

    val sceneDesc
        get() = GameMaster.scene.desc

    var tripV: TripView? = null
    var frag: DashboardFragment? = null

    fun startGame(playName: String = "player") {
        GameMaster.startNewGame(playName)
    }

    val creatureName
        get() = if(GameMaster.scene is simpleBiome)
            GameMaster.enemy.name
        else
            null

    val sceneName
        get() = GameMaster.scene.name

    val ended
        get() = GameMaster.ended

    val left_bar
        get() = GameMaster.us.percent_hp

    val right_bar
        get() = GameMaster.enemy.percent_hp

    val getScene
        get() = sceneInfo(sceneName,
            creatureName,
            sceneHead,
            sceneDesc,
            left_bar,
            right_bar,
            msg)

    fun update() {
        tools = buildList { GameMaster.display.forEach{add(dispTool(it.imageId, it.header, it.description))} }
        msg = GameMaster.msg
        /*

        tripV?.scene = GameMaster.scene.name

        if(GameMaster.scene is simpleBiome)
            tripV?.creatureName = GameMaster.enemy.name
        else
            tripV?.creatureName = null

        tripV?.left_bar = GameMaster.us.percent_hp
        tripV?.right_bar = GameMaster.enemy.percent_hp

        tripV?.invalidate()
        frag?.setMessage(GameMaster.msg)
        frag?.updateScene()*/

        // tools.forEachIndexed{ index, toolDisplay -> toolDisplay.loadTool(GameMaster.display[index]) }
    }

    fun activateTool(id: Int) {
        GameMaster.activateTool(id)

        update()
    }
}