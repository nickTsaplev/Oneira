package com.lesterade.oneira.gameHandling

import android.content.Context
import com.lesterade.oneira.gameHandling.biomes.LocationFactory
import com.lesterade.oneira.gameHandling.biomes.Biome
import com.lesterade.oneira.gameHandling.biomes.EndingLoc
import com.lesterade.oneira.gameHandling.biomes.SimpleBiome
import com.lesterade.oneira.gameHandling.msgFormatting.MsgFormatter
import com.lesterade.oneira.gameHandling.msgFormatting.msgPatterns
import com.lesterade.oneira.gameHandling.weapons.ToolFactory
import com.lesterade.oneira.gameHandling.weapons.Weapon
import com.lesterade.oneira.gameHandling.weapons.Instrument
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

class GameMaster(private val patterns: msgPatterns) {
    var scene: Biome = SimpleBiome(Element.fire, "", 0, "", "")

    var us = Player(Creature(0f, Element.fire, "", ""))
    var enemy = scene.getEnemy()

    var msg = ""

    val ended
        get() = (scene is EndingLoc)

    init {
        us.hand = mutableListOf()
        us.redraw()
    }

    fun startNewGame(playName: String = "player") {
        scene = LocationFactory.getByName("start")
        us = ActorFactory.getByName(playName) as Player
        enemy = scene.getEnemy()

        us.hand = mutableListOf()
        us.redraw()
    }

    val cards: List<Instrument>
        get() {
            return us.cards + us.hand
        }

    fun save(): JsonObject {
        val handSave = JsonArray(us.hand.map{ JsonPrimitive(it.name) })
        val deckSave = JsonArray(us.cards.map {JsonPrimitive(it.name)})

        val ans = JsonObject(mapOf("hand" to handSave,
            "deck" to deckSave,
            "player" to Json.encodeToJsonElement(us as Creature),
            "enemy" to Json.encodeToJsonElement(enemy as Creature),
            "scene" to JsonPrimitive(scene.name)
            ))

        return ans
    }

    fun load(from: JsonObject) {
        us = Player(Json.decodeFromJsonElement<Creature>(from["player"]!!))

        us.hand = mutableListOf()

        for (i in 0..<from["hand"]!!.jsonArray.size) {
            us.hand.add(ToolFactory.getByName(from["hand"]!!.jsonArray[i].jsonPrimitive.content))
        }

        val tmp = mutableListOf<Instrument>()
        for (i in 0..<from["deck"]!!.jsonArray.size) {
           tmp.add(ToolFactory.getByName(from["deck"]!!.jsonArray[i].jsonPrimitive.content))
        }
        us.loadCards(tmp)

        scene = LocationFactory.getByName(from["scene"]!!.jsonPrimitive.content)
        enemy = Actor(Json.decodeFromJsonElement<Creature>(from["enemy"]!!))
        enemy.takeCards(ActorFactory.getByName(enemy.name))
    }

    val display: List<Instrument>
        get() = scene.choices(us)


    private fun enemyTurn(): String {
        if(scene !is SimpleBiome)
            return ""

        var tmp = enemy.draw()

        val (hit_en, heal_en) = tmp.getSignature(enemy, us, scene)
        tmp = tmp.attack(enemy, us, scene)

        enemy.discard(tmp)

        if(us.dead) {
            scene = LocationFactory.getByName("ending_over")
        }

        val enemyMsg = MsgFormatter(enemy.header, patterns.and_name, patterns.do_pattern)
        enemyMsg.addEffect(patterns.hits_pattern, hit_en)
        enemyMsg.addEffect(patterns.suffers_pattern, if (heal_en != 0f) enemy.poison else 0f)
        enemyMsg.addEffect(patterns.heals_pattern, heal_en)

        return enemyMsg.format()
    }

    fun activateTool(id: Int) {
        msg = ""
        if(scene is SimpleBiome) {
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

            val enemyMsg = MsgFormatter(enemy.header, patterns.and_name, patterns.does_pattern)
            enemyMsg.addEffect(patterns.hits_pattern, hit_en)
            enemyMsg.addEffect(patterns.bleeds_pattern, if (hit != 0f) enemy.bleed else 0f)
            enemyMsg.addEffect(patterns.suffers_pattern, if (heal_en != 0f) enemy.poison else 0f)
            enemyMsg.addEffect(patterns.heals_pattern, heal_en)

            val usMsg = MsgFormatter(patterns.self_name, patterns.and_name, patterns.do_pattern)
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
        get() = if(master.scene is SimpleBiome)
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

    val transmutation_left
        get() = master.us.directions[0].main to master.us.directions[0].dir

    val transmutation_right
        get() = master.us.directions[1].main to master.us.directions[1].dir


    fun update() {
        tools = buildList { master.display.forEach{add(dispTool(it.imageId, it.header, it.description, it.transmutable && master.scene is SimpleBiome))} }
        inventory = buildList { master.us.inventory.forEach{add(dispTool(it.imageId, it.header, it.description, it.transmutable && master.scene is SimpleBiome))} }
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