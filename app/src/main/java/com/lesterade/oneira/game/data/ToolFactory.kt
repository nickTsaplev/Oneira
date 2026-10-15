package com.lesterade.oneira.game.data

import com.lesterade.oneira.R
import com.lesterade.oneira.game.Element
import com.lesterade.oneira.game.weapons.Instrument
import com.lesterade.oneira.game.weapons.UnknownWeapon
import com.lesterade.oneira.game.weapons.Weapon
import com.lesterade.oneira.game.weapons.actionEffects.Bleed
import com.lesterade.oneira.game.weapons.actionEffects.FieryEffect
import com.lesterade.oneira.game.weapons.actionEffects.Flip
import com.lesterade.oneira.game.weapons.actionEffects.HandBurn
import com.lesterade.oneira.game.weapons.actionEffects.Peacemaker
import com.lesterade.oneira.game.weapons.actionEffects.PlayHand
import com.lesterade.oneira.game.weapons.actionEffects.Poison
import com.lesterade.oneira.game.weapons.actionEffects.Redraw
import com.lesterade.oneira.game.weapons.actionEffects.SelfPoison
import com.lesterade.oneira.game.weapons.damageEffects.FieryDamage
import com.lesterade.oneira.game.weapons.damageEffects.Percent
import com.lesterade.oneira.game.weapons.damageEffects.Wood
import com.lesterade.oneira.game.weapons.healEffects.PoisonHeal
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ToolFactory: BetterJsonFactory(R.raw.tools) {
    fun applyEffect(base: Weapon, type: String, currentObject: JsonObject) {
        when(type) {
            "redraw" -> base.actionEffects.add(Redraw())
            "boosted" -> {
                val names = currentObject["by"]?.jsonArray ?: return
                base.damageEffects.add(com.lesterade.oneira.game.weapons.damageEffects.Boost(names.toStringList()))
            }
            "peacemaker" -> {
                val peaceWith = currentObject["peace_with"]?.jsonArray ?: return
                base.actionEffects.add(Peacemaker(peaceWith.toStringList()))
            }
            "wood" -> base.damageEffects.add(Wood())
            "lifesteal" -> base.isLifesteal = true
            "fiery" -> {
                base.damageEffects.add(FieryDamage(-currentObject["fire_effect"]!!.jsonPrimitive.int))
                base.actionEffects.add(FieryEffect(currentObject["fire_effect"]!!.jsonPrimitive.int, currentObject["min_fire"]?.jsonPrimitive?.int))
            }
            "handBurn" -> base.actionEffects.add(HandBurn(currentObject["fire_effect"]!!.jsonPrimitive.int))
            "percent" -> base.damageEffects.add(Percent())
            "flip" -> base.actionEffects.add(Flip())
            "poison" -> base.actionEffects.add(Poison(currentObject["poison"]!!.jsonPrimitive.float))
            "poisonHeal" -> base.healEffects.add(PoisonHeal())
            "playHand" -> base.actionEffects.add(PlayHand())
            "selfPoison" -> base.actionEffects.add(SelfPoison(currentObject["poison"]!!.jsonPrimitive.float))
            "bleed" -> base.actionEffects.add(Bleed(currentObject["bleed"]!!.jsonPrimitive.float))
        }
    }

    fun getToolElement(name: String): Element {
        return json.decodeFromJsonElement<Element>(data[name]?.jsonObject!!["element"]!!);
    }

    fun getTransmutation(from: Instrument, by: Element): Instrument {
        data[data[from.name]!!.jsonObject["cycle"]!!.jsonPrimitive.content]!!.jsonArray.forEach {
            if (getToolElement(it.jsonPrimitive.content) == by)
                return getByName(it.jsonPrimitive.content)
        }
        return from
    }

    fun applyEffects(base: Weapon, array: JsonArray) {
        array.forEach {
            val currentObject = it.jsonObject
            val type = currentObject["type"]?.jsonPrimitive?.content ?: return
            applyEffect(base, type, currentObject)
        }
    }

    fun headerColumn(): String {
        if (language == "en")
            return "header"
        return "header_$language"
    }

    fun descriptionColumn(): String {
        if (language == "en")
            return "description"
        return "description_$language"
    }

    fun getByName(name: String): Instrument {
        val curObj = findObject(name) ?: return UnknownWeapon()

        val type = curObj["type"]?.jsonPrimitive?.content ?: "simple"

        val base = json.decodeFromJsonElement<Weapon>(curObj)
        base.name = name

        curObj[headerColumn()]?.jsonPrimitive?.contentOrNull?.let { base.header = it }
        curObj[descriptionColumn()]?.jsonPrimitive?.contentOrNull?.let {base.description = it}

        curObj["effects"]?.jsonArray?.also {
            applyEffects(base, it)
        }

        type.split("+").forEach {
            applyEffect(base, it, curObj)
        }

        return base
    }

    fun reloadDescription(ins: Instrument) {
        val curObj = findObject(ins.name) ?: return

        val header = curObj[headerColumn()]?.jsonPrimitive?.content ?: return
        val description = curObj[descriptionColumn()]?.jsonPrimitive?.content ?: return

        ins.setDescription(
            header,
            description
        )
    }
}