package com.lesterade.oneira.gameHandling.msgFormatting

import android.content.Context
import com.lesterade.oneira.R

class msgPatterns(context : Context) {
    val self_name = context.getString(R.string.self_name)

    val and_name = context.getString(R.string.and_name)

    val hit_pattern = context.getString(R.string.hit_pattern)
    val bleed_pattern = context.getString(R.string.bleed_pattern)
    val suffer_pattern = context.getString(R.string.suffer_pattern)
    val heal_pattern = context.getString(R.string.heal_pattern)

    val hits_pattern = context.getString(R.string.hits_pattern)
    val bleeds_pattern = context.getString(R.string.bleeds_pattern)
    val suffers_pattern = context.getString(R.string.suffers_pattern)
    val heals_pattern = context.getString(R.string.heals_pattern)

    val do_pattern = context.getString(R.string.do_pattern)
    val does_pattern = context.getString(R.string.does_pattern)
}
