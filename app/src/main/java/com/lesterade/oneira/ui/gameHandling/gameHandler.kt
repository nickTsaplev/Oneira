package com.lesterade.oneira.ui.gameHandling

import android.content.Context
import android.content.Intent
import kotlinx.serialization.*
import kotlinx.serialization.modules.*
import org.json.JSONObject
import android.content.res.*

import com.lesterade.oneira.ui.dashboard.TripView
import com.lesterade.oneira.ui.toolDisplayLayout.ToolDisplay

import com.lesterade.oneira.R
import com.lesterade.oneira.ui.EndingActivity
import com.lesterade.oneira.ui.dashboard.DashboardFragment
import org.json.JSONArray
import kotlin.random.Random

open class creature(val maxhp : Float, val elem: element, val name: String, val header: String) {
    var hp = maxhp

    var innerFire = 3

    val percent_hp
        get() = hp.toFloat()/maxhp.toFloat()

    var dead = false

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

}

open class actor(maxhp : Float, elem: element, name: String, header: String = name): creature(maxhp, elem, name, header) {
    var deck: MutableList<instrument> = mutableListOf(unknownWeapon(), unknownWeapon(), unknownWeapon())

    fun draw(): instrument {
        val index = Random.nextInt(0, deck.size)
        val tmp = deck[index]
        deck.removeAt(index)
        return tmp
    }
}

class player(maxhp : Float, elem: element, name: String) : actor(maxhp, elem, name) {
    var hand: MutableList<instrument> = mutableListOf()

    fun redraw() {
        while(hand.size < 3) {
            hand.add(draw())
        }
    }
}

abstract class biome(val elem: element, val name: String, open val header: String, val desc: String) {
    abstract fun affect(a : creature)
    abstract fun getEnemy(): actor
    abstract fun advance(): biome?
}

class simpleBiome(elem: element, name: String, var depth: Int, val inner_header: String, desc: String): biome(elem, name, inner_header, desc) {
    var deck: MutableList<String> = mutableListOf("costable")
    var next: MutableList<String> = mutableListOf()

    override val header
        get() = inner_header + " (" + (depth + 1).toString() + " steps left)"

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

class shop(elem: element, name: String, header: String, desc: String): biome(elem, name, header, desc) {
    var next: MutableList<String> = mutableListOf()
    var sold: MutableList<instrument> = mutableListOf()
    var currentlySold: MutableList<instrument> = mutableListOf()

    fun pickWares() {
        val index = Random.nextInt(0, sold.size)

        currentlySold.add(sold[index])


        val index2 = Random.nextInt(0, sold.size)

        currentlySold.add(sold[index2])

        currentlySold.add(ToolFactory.getByName("nothing"))
    }

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, element.fire, "?")
    }

    override fun advance(): biome? {
        val index = Random.nextInt(0, next.size)
        return LocationFactory.getByName(next[index])
    }
}

class loseLoc(elem: element, name: String, header: String, desc: String): biome(elem, name, header, desc) {
    var next: MutableList<String> = mutableListOf()

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, element.fire, "?")
    }

    override fun advance(): biome? {
        val index = Random.nextInt(0, next.size)
        return LocationFactory.getByName(next[index])
    }
}

class endingLoc(elem: element, name: String, header: String, desc: String): biome(elem, name, header, desc) {
    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, element.fire, "?")
    }

    override fun advance(): biome? {
        return null
    }
}

class forkLoc(elem: element, name: String, header: String, desc: String): biome(elem, name, header, desc) {
    var next: MutableList<MutableList<String>> = mutableListOf()
    var choices: MutableList<instrument> = mutableListOf()

    var select = 0

    override fun affect(a: creature) {

    }

    override fun getEnemy(): actor {
        return actor(1f, element.fire, "?")
    }

    override fun advance(): biome? {
        val index = Random.nextInt(0, next[select].size)
        return LocationFactory.getByName(next[select][index])
    }
}

interface instrument {
    fun attack(from : creature, to : creature, located : biome): instrument
    fun getSignature(from: creature, to: creature, located: biome): Pair<Float, Float>

    val name: String
    val header: String
    val desc : String

    val imageId: String
}

// @Serializable
// @SerialName("simple")
open class simpleWeapon(val dmg: Float, val elem: element, val modelName : String, override val header: String, val description : String,
                        val hl: Float = 0f,
                        val turnsInto: String? = null): instrument {
    var isLifesteal = false

    open fun getDamage(from: creature, to: creature, located: biome): Float {
        return dmg
    }
    open fun getHeal(from: creature, to: creature, located: biome): Float {
        if(isLifesteal)
            return getDamage(from, to, located)
        return hl
    }

    constructor(other: simpleWeapon) : this(other.dmg,
        other.elem, other.modelName, other.header, other.description, other.hl, other.turnsInto)

    override fun attack(from: creature, to: creature, located: biome): instrument {
        var hitDmg = getDamage(from, to, located)
        hitDmg += hitDmg * 0.2f * located.elem.effect(elem)
        hitDmg += hitDmg * 0.2f * elem.effect(to.elem)
        to.hit(hitDmg)

        from.heal(getHeal(from, to, located))

        otherEffects(from, to, located)

        if(turnsInto == null)
            return this
        return ToolFactory.getByName(turnsInto)
    }

    override fun getSignature(from: creature, to: creature, located: biome): Pair<Float, Float> {
        var hitDmg = getDamage(from, to, located)
        hitDmg += hitDmg * 0.2f * located.elem.effect(elem)
        hitDmg += hitDmg * 0.2f * elem.effect(to.elem)

        return Pair(hitDmg, getHeal(from, to, located))
    }

    override val name
        get() = modelName // + " (" + elem.tochar + ")"

    override val desc
        get() = description

    override val imageId
        get() = modelName

    open fun otherEffects(from: creature, to: creature, located: biome) {

    }
}

class boostedWeapon(base: simpleWeapon, val counted: List<String>): simpleWeapon(base) {
    override fun getDamage(from: creature, to: creature, located: biome): Float {
        if(from is player) {
            var hitDmg = 0f
            from.deck.forEach { if(it.name in counted) hitDmg += 1f}
            from.hand.forEach { if(it.name in counted) hitDmg += 1f}
            hitDmg *= dmg

            return hitDmg
        }
        return dmg
    }
}

class fieryWeapon(base: simpleWeapon, val fireEff: Int): simpleWeapon(base) {
    override fun getDamage(from: creature, to: creature, located: biome): Float {
        if(dmg == 0f || (from.innerFire + fireEff < 0))
            return from.innerFire.toFloat()
        return dmg
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
            if(from.hand.size + from.deck.size < 7) {
                from.hp = 0f
                return
            }

            from.hand = mutableListOf()
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
        if(dmg - from.innerFire.toFloat() > 0f)
            return dmg - from.innerFire.toFloat()
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

    override val desc
        get() = "???"

    override val imageId = "unknown"
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
            tmp = tmp.attack(enemy, us, scene)
            val (hit_en, heal_en) = tmp.getSignature(enemy, us, scene)

            enemy.deck.add(tmp)

            if(us.dead) {
                scene = LocationFactory.getByName("ending_over")
            }


            msg = ""
            if(hit != 0f)
                msg += "You hit for %.2f.".format(hit)
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

class GameHandler {
    var tools: MutableList<ToolDisplay> = mutableListOf()
    var tripV: TripView? = null
    var frag: DashboardFragment? = null

    val sceneHead
        get() = GameMaster.scene.header

    val sceneDesc
        get() = GameMaster.scene.desc

    fun startGame(playName: String = "player") {
        GameMaster.startNewGame(playName)
    }

    fun update() {
        if(GameMaster.ended) {
            val int = Intent("android.intent.action.VIEW")
            int.setClass(frag!!.requireContext(), EndingActivity::class.java)
            int.putExtra("com.lesterade.oneira.ending", GameMaster.scene.header)
            int.putExtra("com.lesterade.oneira.ending_img", GameMaster.scene.name)
            frag?.activity?.finish()
            frag?.startActivity(int)
        }

        tripV?.scene = GameMaster.scene.name

        if(GameMaster.scene is simpleBiome)
            tripV?.creatureName = GameMaster.enemy.name
        else
            tripV?.creatureName = null

        tripV?.left_bar = GameMaster.us.percent_hp
        tripV?.right_bar = GameMaster.enemy.percent_hp

        tripV?.invalidate()
        frag?.setMessage(GameMaster.msg)
        frag?.updateScene()

        tools.forEachIndexed{ index, toolDisplay -> toolDisplay.loadTool(GameMaster.display[index]) }
    }

    fun activateTool(id: Int) {
        GameMaster.activateTool(id)

        update()
    }
}