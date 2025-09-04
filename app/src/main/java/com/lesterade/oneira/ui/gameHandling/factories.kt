package com.lesterade.oneira.ui.gameHandling

import android.content.Context
import com.lesterade.oneira.R
import org.json.JSONObject

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun JsonArray.toStringList(): MutableList<String> {
    val ans = mutableListOf<String>()

    for(i in 0..<size)
        ans.add(get(i).jsonPrimitive.content)

    return ans
}

abstract class BetterJsonFactory(val id: Int) {
    protected val json = Json { ignoreUnknownKeys = true }

    var data = JsonObject(mapOf())

    fun launch(context: Context) {
        val stream = context.resources.openRawResource(id)

        var text = String()
        while(stream.available() != 0)
            text += String(byteArrayOf(stream.read().toByte()))
        data = parseToJsonElement(text).jsonObject
    }
}

object ToolFactory: BetterJsonFactory(R.raw.tools) {

    fun getByName(name: String): instrument {
        val curObj = data[name]?.jsonObject ?: return unknownWeapon()
        val type = curObj["type"]?.jsonPrimitive?.content ?: return unknownWeapon()

        //val damage = curObj.getDouble("damage").toFloat()
        //val elem = element.fromString(curObj.getString("element"))

        //var heal = 0f
        //if(!curObj.optDouble("heals").isNaN())
        //    heal = curObj.optDouble("heals").toFloat()

        //var t_into: String? = curObj.optString("turns_into")
        //if(t_into == "")
        //    t_into = null

        val base = json.decodeFromJsonElement<simpleWeapon>(curObj)
        base.name = name

        when(type) {
            "simple" -> return base
            "reloading" -> return base
            "redraw" -> return redrawingWeapon(
                base
            )
            "boosting" -> return boostingWeapon(
                base,
                curObj["dmg_boost"]!!.jsonPrimitive.float
            )
            "boosted" -> {
                val ans = mutableListOf<String>()
                val names = curObj["by"]?.jsonArray ?: return unknownWeapon()

                return boostedWeapon(
                    base, names.toStringList()
                )
            }
            "peacemaking" -> {
                val ans = peacemakingWeapon(base)
                val p = curObj["peace_with"]?.jsonArray ?: return unknownWeapon()

                ans.peaceWith = p.toStringList()

                return ans
            }
            "wooden" -> return woodenWeapon(
                base
            )
            "lifesteal" -> {
                base.isLifesteal = true
                return base
            }
            "fiery" -> return fieryWeapon(
                base,
                curObj["fire_effect"]!!.jsonPrimitive.int
            )
            "handBurn" -> return handBurner(
                base,
                curObj["fire_effect"]!!.jsonPrimitive.int
            )
        }
        return unknownWeapon()
    }
}

object ActorFactory: BetterJsonFactory(R.raw.actors) {
    fun getByName(name: String): actor {
        val curObj = data[name]?.jsonObject ?: return actor(0f, Element.fire, "?", "?")

        val isP = curObj["player"]?.jsonPrimitive?.content

        val base = json.decodeFromJsonElement<creature>(curObj)
        base.name = name

        val ans = if(isP == "" || isP == "false")
            actor(base)
        else
            player(base)


        val tools = curObj["deck"]!!.jsonArray

        val tmp = mutableListOf<instrument>()
        for(i in 0..<tools.size)
            tmp.add(ToolFactory.getByName(tools[i].jsonPrimitive.content))

        ans.loadCards(tmp)

        return ans
    }
}

object LocationFactory: BetterJsonFactory(R.raw.locations) {
    fun getByName(name: String): biome {
        val curObj = data[name]?.jsonObject ?: return simpleBiome(Element.water, "mon_garden", 1, "Unknown location: $name", "")
        val type = curObj["type"]?.jsonPrimitive?.content ?: return simpleBiome(Element.water, "mon_garden", 1, "Misformatted location: $name", "")

        when(type) {
            "simple" -> {
                val ans = json.decodeFromJsonElement<simpleBiome>(curObj)
                ans.name = name

                ans.deck = curObj["enemies"]!!.jsonArray.toStringList()
                ans.next = curObj["next"]!!.jsonArray.toStringList()

                return ans
            }
            "shop" -> {
                val ans = json.decodeFromJsonElement<shop>(curObj)
                ans.name = name

                val deck = curObj["wares"]!!.jsonArray
                for (i in 0..<deck.size) {
                    ans.sold.add(ToolFactory.getByName(
                        deck[i].jsonPrimitive.content))
                }
                ans.next = curObj["next"]!!.jsonArray.toStringList()

                ans.pickWares()
                return ans
            }
            "lose" -> {
                val ans = json.decodeFromJsonElement<loseLoc>(curObj)
                ans.name = name

                ans.next = curObj["next"]!!.jsonArray.toStringList()

                return ans
            }
            "fork" -> {
                val ans = json.decodeFromJsonElement<forkLoc>(curObj)
                ans.name = name

                val nextArr = curObj["next"]!!.jsonArray

                ans.next = mutableListOf()

                for (i in 0..<3) {
                    ans.next.add(mutableListOf())

                    if(i >= nextArr.size) {
                        ans.choices.add(ToolFactory.getByName("none"))
                        continue
                    }

                    val next = nextArr[i].jsonArray
                    ans.choices.add(ToolFactory.getByName(next[0].jsonPrimitive.content))
                    for (j in 1..<next.size)
                        ans.next[i].add(next[j].jsonPrimitive.content)
                }

                return ans
            }
            "ending" -> {
                val ans = json.decodeFromJsonElement<endingLoc>(curObj)
                ans.name = name
                return ans
            }
        }
        return simpleBiome(Element.fire, name, 1, "", "")
    }
}