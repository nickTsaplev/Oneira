package com.lesterade.oneira.gameHandling.msgFormatting

import com.lesterade.oneira.gameHandling.format

class msgFormatter(val name: String, val and: String, val nothing: String)
{
    private class effect(val template: String, val value: Float)
    {
        fun format(): String = template.format(value)
            // template.format(value)
    }
    private var effects: MutableList<effect> = mutableListOf()

    fun addEffect(template: String, value: Float)
    {
        if(value != 0f)
            effects.add(effect(template, value))
    }

    fun format(): String
    {
        var msg = name

        if(effects.size == 0)
            return "$msg $nothing"

        for(i in 0..<effects.size)
        {
            if(i == 0)
                msg += (" " + effects[i].format())
            else if(i == effects.size - 1)
                msg += (" $and " + effects[i].format())
            else
                msg += ", " + effects[i].format()
        }

        return msg
    }
}