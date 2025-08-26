package com.lesterade.oneira.ui.gameHandling

import android.content.Context
import com.lesterade.oneira.R
import org.json.JSONObject

abstract class JsonFactory(val id: Int) {
    var data = JSONObject()

    fun launch(context: Context) {
        val stream = context.resources.openRawResource(id)

        var text = String()
        while(stream.available() != 0)
            text += String(byteArrayOf(stream.read().toByte()))
        data = JSONObject(text)
    }
}

object ToolFactory: JsonFactory(R.raw.tools) {
    fun getByName(name: String): instrument {
        val curObj = data.optJSONObject(name) ?: return unknownWeapon()
        val type = curObj.optString("type")

        val damage = curObj.getDouble("damage").toFloat()
        val elem = element.fromString(curObj.getString("element"))

        var heal = 0f
        if(!curObj.optDouble("heals").isNaN())
            heal = curObj.optDouble("heals").toFloat()

        var t_into: String? = curObj.optString("turns_into")
        if(t_into == "")
            t_into = null

        val base = simpleWeapon(
            damage,
            elem,
            name,
            curObj.getString("header"),
            curObj.getString("description"),
            heal,
            t_into
        )

        when(type) {
            "simple" -> return base
            "reloading" -> return base
            "redraw" -> return redrawingWeapon(
                base
            )
            "boosted" -> return boostedWeapon(
                base,
                listOf("cloud", "droplet")
            )
            "wooden" -> return woodenWeapon(
                base
            )
            "lifesteal" -> {
                base.isLifesteal = true
                return base
            }
            "fiery" -> return fieryWeapon(
                base,
                curObj.getInt("fire_effect")
            )
            "handBurn" -> return handBurner(
                base,
                curObj.getInt("fire_effect")
            )
        }
        return unknownWeapon()
    }
}

object ActorFactory: JsonFactory(R.raw.actors) {
    fun getByName(name: String): actor {
        val curObj = data.optJSONObject(name) ?: return actor(0f, element.fire, "?")

        val hp = curObj.getDouble("hp").toFloat()

        val isP = curObj.optString("player")



        val ans = if(isP == "" || isP == "false")
            actor(hp, element.fromString(curObj.getString("element")), name)
        else
            player(hp, element.fromString(curObj.getString("element")), name)


        val tools = curObj.getJSONArray("deck")
        ans.deck = mutableListOf()
        for(i in 0..<tools.length())
            ans.deck.add(ToolFactory.getByName(tools.getString(i)))

        return ans
    }
}

object LocationFactory: JsonFactory(R.raw.locations) {
    fun getByName(name: String): biome {
        val curObj = data.optJSONObject(name) ?: return simpleBiome(element.water, "mon_garden", 1, "", "")
        val type = curObj.optString("type")

        val header = curObj.getString("header")
        val desc = curObj.getString("desc")
        when(type) {
            "simple" -> {
                val depth = curObj.getInt("depth")

                val ans =
                    simpleBiome(element.fromString(curObj.getString("element")), name, depth, header, desc)

                val deck = curObj.getJSONArray("enemies")

                ans.deck = mutableListOf()

                for (i in 0..<deck.length())
                    ans.deck.add(deck.getString(i))

                val next = curObj.getJSONArray("next")

                for (i in 0..<next.length())
                    ans.next.add(next.getString(i))

                return ans
            }
            "shop" -> {
                val ans = shop(element.fromString(curObj.getString("element")), name, header, desc)

                val deck = curObj.getJSONArray("wares")
                for (i in 0..<deck.length()) {
                    ans.sold.add(ToolFactory.getByName(
                        deck.getString(i)))
                }
                val next = curObj.getJSONArray("next")

                for (i in 0..<next.length())
                    ans.next.add(next.getString(i))

                ans.pickWares()
                return ans
            }
            "lose" -> {
                val ans = loseLoc(element.fromString(curObj.getString("element")), name, header, desc)

                val next = curObj.getJSONArray("next")

                for (i in 0..<next.length())
                    ans.next.add(next.getString(i))

                return ans
            }
            "fork" -> {
                val ans = forkLoc(element.fromString(curObj.getString("element")), name, header, desc)

                val nextArr = curObj.getJSONArray("next")

                for (i in 0..<3) {
                    ans.next.add(mutableListOf())

                    if(i >= nextArr.length()) {
                        ans.choices.add(ToolFactory.getByName("none"))
                        continue
                    }

                    val next = nextArr.getJSONArray(i)
                    ans.choices.add(ToolFactory.getByName(next.getString(0)))
                    for (j in 1..<next.length())
                        ans.next[i].add(next.getString(j))
                }

                return ans
            }
            "ending" -> {
                return endingLoc(element.fromString(curObj.getString("element")), name, header, desc)
            }
        }
        return simpleBiome(element.fromString(curObj.getString("element")), name, 1, "", "")
    }
}