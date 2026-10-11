package com.lesterade.oneira.game

import com.lesterade.oneira.game.biomes.Biome
import com.lesterade.oneira.game.biomes.EndingLoc
import com.lesterade.oneira.game.biomes.SimpleBiome
import com.lesterade.oneira.game.biomes.VaryingLoc
import com.lesterade.oneira.game.weapons.Instrument
import com.lesterade.oneira.game.weapons.Weapon
import com.lesterade.oneira.game.data.ActorFactory
import com.lesterade.oneira.game.data.LocationFactory
import com.lesterade.oneira.game.data.ToolFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class GameMaster {
    var scene: Biome = SimpleBiome(Element.fire, "", 0, "", "")

    var us = Player(Creature("", "", 0f, Element.fire,))
    var enemy = scene.getEnemy()

    var gameOngoing = false

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

        gameOngoing = true
    }

    fun save(): JsonObject {
        val handSave = JsonArray(us.hand.map { JsonPrimitive(it.name) })
        val deckSave = JsonArray(us.cards.map { JsonPrimitive(it.name) })

        val ans = JsonObject(
            mapOf(
                "hand" to handSave,
                "deck" to deckSave,
                "player" to Json.encodeToJsonElement(us as Creature),
                "enemy" to Json.encodeToJsonElement(enemy as Creature),
                "scene" to JsonPrimitive(scene.name)
            )
        )

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


    private fun enemyTurn(): TurnReport? {
        if(scene !is SimpleBiome)
            return null

        var tmp = enemy.draw()

        val (hit_en, heal_en) = tmp.getSignature(enemy, us, scene)
        tmp = tmp.attack(enemy, us, scene)

        enemy.discard(tmp)

        if(us.dead) {
            scene = LocationFactory.getByName("ending_over")
        }

        return TurnReport(hit_en, 0f, if (heal_en != 0f) enemy.poison else 0f, heal_en)
    }

    fun activateTool(id: Int): Pair<TurnReport?, TurnReport?> {
        if(scene is SimpleBiome || ((scene is VaryingLoc) && (!(scene as VaryingLoc).inEncounter))) {
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
                return null to TurnReport(hit, 0f, if (heal != 0f) us.poison else 0f, heal)
            }

            var tmp = enemy.draw()

            val (hit_en, heal_en) = tmp.getSignature(enemy, us, scene)
            tmp = tmp.attack(enemy, us, scene)

            enemy.discard(tmp)

            if(us.dead) {
                gameOngoing = false
                scene = LocationFactory.getByName("ending_over")
            }

            return (TurnReport(hit_en, if (hit != 0f) enemy.bleed else 0f, if (heal_en != 0f) enemy.poison else 0f, heal_en) to
                TurnReport(hit, if (hit_en != 0f) us.bleed else 0f, if (heal != 0f) us.poison else 0f, heal))
        }

        if(scene.activateTool(id, us, enemy)) {
            val tmp = scene.advance()
            if (tmp != null)
                scene = tmp
            enemy = scene.getEnemy()
        }
        return null to null
    }

    fun useCollectible(id: Int) {
        us.inventory[id].attack(us, enemy, scene)
        us.inventory.removeAt(id)
    }

    fun transmuteTool(id: Int, dir: Int): TurnReport? {
        (us.hand[id] as? Weapon)?.transmute(dir)?.let {
            us.hand[id] = it
            return enemyTurn()
        }
        return null
    }
}