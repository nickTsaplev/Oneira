package com.lesterade.oneira.gameHandling.weapons

import com.lesterade.oneira.R
import com.lesterade.oneira.gameHandling.BetterJsonFactory
import com.lesterade.oneira.gameHandling.toStringList
import com.lesterade.oneira.gameHandling.weapons.actionEffects.Bleed
import com.lesterade.oneira.gameHandling.weapons.actionEffects.FieryEffect
import com.lesterade.oneira.gameHandling.weapons.actionEffects.Flip
import com.lesterade.oneira.gameHandling.weapons.actionEffects.HandBurn
import com.lesterade.oneira.gameHandling.weapons.actionEffects.Peacemaker
import com.lesterade.oneira.gameHandling.weapons.actionEffects.PlayHand
import com.lesterade.oneira.gameHandling.weapons.actionEffects.Poison
import com.lesterade.oneira.gameHandling.weapons.actionEffects.Redraw
import com.lesterade.oneira.gameHandling.weapons.actionEffects.SelfPoison
import com.lesterade.oneira.gameHandling.weapons.damageEffects.Boost
import com.lesterade.oneira.gameHandling.weapons.damageEffects.FieryDamage
import com.lesterade.oneira.gameHandling.weapons.damageEffects.Percent
import com.lesterade.oneira.gameHandling.weapons.damageEffects.Wood
import com.lesterade.oneira.gameHandling.weapons.healEffects.PoisonHeal
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
                base.damageEffects.add(Boost(names.toStringList()))
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

    fun applyEffects(base: Weapon, array: JsonArray) {
        array.forEach {
            val currentObject = it.jsonObject
            val type = currentObject["type"]?.jsonPrimitive?.content ?: return
            applyEffect(base, type, currentObject)
        }
    }

    fun getByName(name: String): Instrument {
        val curObj = data[name]?.jsonObject ?: return UnknownWeapon()

        val type = curObj["type"]?.jsonPrimitive?.content ?: "simple"

        val base = json.decodeFromJsonElement<Weapon>(curObj)
        base.name = name

        curObj["header_$language"]?.jsonPrimitive?.contentOrNull?.let { base.header = it }
        curObj["description_$language"]?.jsonPrimitive?.contentOrNull?.let {base.description = it}

        curObj["effects"]?.jsonArray?.also {
            applyEffects(base, it)
        }

        type.split("+").forEach {
            applyEffect(base, it, curObj)
        }

        return base
    }
}