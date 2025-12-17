package com.lesterade.oneira.gameHandling


import android.content.Context
import com.lesterade.oneira.gameHandling.weapons.ToolFactory
import com.lesterade.oneira.gameHandling.weapons.instrument

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedReader
import java.io.InputStreamReader
import com.lesterade.oneira.R

fun JsonArray.toStringList(): MutableList<String> {
    val ans = mutableListOf<String>()

    for(i in 0..<size)
        ans.add(get(i).jsonPrimitive.content)

    return ans
}

abstract class BetterJsonFactory(val id: Int) {
    protected val json = Json { ignoreUnknownKeys = true }

    var finished = false

    var language = "en"

    var text = ""
    var data = JsonObject(mapOf())

    fun launch(context: Context, _language: String) {
        language = _language

        val stream = context.resources.openRawResource(id)
        val buffer = BufferedReader(InputStreamReader(stream))

        val text = (buffer.readLines()).joinToString("")

        data = parseToJsonElement((text)).jsonObject

        buffer.close()
        stream.close()
    }
}
object ActorFactory: BetterJsonFactory(R.raw.actors) {
    fun getByName(name: String): actor {
        val curObj = data[name]?.jsonObject ?: return actor(0f, Element.fire, "?", "?")

        val isP = curObj["player"]?.jsonPrimitive?.content

        val base = json.decodeFromJsonElement<creature>(curObj)
        curObj["header_$language"]?.jsonPrimitive?.contentOrNull?.let { base.header = it }
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

