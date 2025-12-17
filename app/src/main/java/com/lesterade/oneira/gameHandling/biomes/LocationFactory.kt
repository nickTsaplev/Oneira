package com.lesterade.oneira.gameHandling.biomes

import com.lesterade.oneira.R
import com.lesterade.oneira.gameHandling.BetterJsonFactory
import com.lesterade.oneira.gameHandling.Element
import com.lesterade.oneira.gameHandling.weapons.ToolFactory
import com.lesterade.oneira.gameHandling.toStringList
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object LocationFactory: BetterJsonFactory(R.raw.locations) {
    fun getByName(name: String): biome {
        val curObj = data[name]?.jsonObject ?: return simpleBiome(Element.water, "mon_garden", 1, "Unknown location: $name", "")
        val type = curObj["type"]?.jsonPrimitive?.content ?: return simpleBiome(Element.water, "mon_garden", 1, "Misformatted location: $name", "")

        when(type) {
            "simple" -> {
                val ans = json.decodeFromJsonElement<simpleBiome>(curObj)
                ans.name = name
                curObj["header_$language"]?.jsonPrimitive?.content?.let {ans.header = it}
                curObj["desc_$language"]?.jsonPrimitive?.content?.let {ans.desc = it}

                data["pattern_$language"]?.jsonPrimitive?.contentOrNull?.let {ans.pattern = it}

                ans.deck = curObj["enemies"]!!.jsonArray.toStringList()
                ans.next = curObj["next"]!!.jsonArray.toStringList()

                ans.depth = 5

                return ans
            }
            "shop" -> {
                val ans = json.decodeFromJsonElement<Shop>(curObj)
                ans.name = name
                curObj["header_$language"]?.jsonPrimitive?.content?.let {ans.header = it}
                curObj["desc_$language"]?.jsonPrimitive?.content?.let {ans.desc = it}

                val wares = curObj["wares"]!!

                val deck = if (wares is JsonArray)
                    wares
                else
                    data[wares.jsonPrimitive.content]!!.jsonArray

                for (i in 0..<deck.size) {
                    ans.sold.add(
                        ToolFactory.getByName(
                        deck[i].jsonPrimitive.content))
                }
                ans.next = curObj["next"]!!.jsonArray.toStringList()

                ans.pickWares()
                return ans
            }
            "lose" -> {
                val ans = json.decodeFromJsonElement<loseLoc>(curObj)
                ans.name = name
                curObj["header_$language"]?.jsonPrimitive?.content?.let {ans.header = it}
                curObj["desc_$language"]?.jsonPrimitive?.content?.let {ans.desc = it}

                ans.next = curObj["next"]!!.jsonArray.toStringList()

                return ans
            }
            "fork" -> {
                val ans = json.decodeFromJsonElement<forkLoc>(curObj)
                ans.name = name
                curObj["header_$language"]?.jsonPrimitive?.content?.let {ans.header = it}
                curObj["desc_$language"]?.jsonPrimitive?.content?.let {ans.desc = it}

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
                curObj["header_$language"]?.jsonPrimitive?.content?.let {ans.header = it}

                ans.name = name
                return ans
            }
        }

        return simpleBiome(Element.fire, name, 1, "", "")
    }
}