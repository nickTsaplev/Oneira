package com.lesterade.oneira.gameHandling.msgFormatting

import android.content.Context
import com.lesterade.oneira.R
import kotlinx.serialization.Serializable

@Serializable
class msgPatterns(val self_name: String,

                  val and_name: String,

                  val hit_pattern: String,
                  val bleed_pattern: String,
                  val suffer_pattern: String,
                  val heal_pattern: String,

                  val hits_pattern: String,
                  val bleeds_pattern: String,
                  val suffers_pattern: String,
                  val heals_pattern: String,
                  val do_pattern: String,
                  val does_pattern: String) {

    companion object {
        fun createFromContext(context : Context): msgPatterns {
            return msgPatterns(
                self_name = context.getString(R.string.self_name),

                and_name = context.getString(R.string.and_name),

                hit_pattern = context.getString(R.string.hit_pattern),
                bleed_pattern = context.getString(R.string.bleed_pattern),
                suffer_pattern = context.getString(R.string.suffer_pattern),
                heal_pattern = context.getString(R.string.heal_pattern),

                hits_pattern = context.getString(R.string.hits_pattern),
                bleeds_pattern = context.getString(R.string.bleeds_pattern),
                suffers_pattern = context.getString(R.string.suffers_pattern),
                heals_pattern = context.getString(R.string.heals_pattern),

                do_pattern = context.getString(R.string.do_pattern),
                does_pattern = context.getString(R.string.does_pattern),
            )
        }

    }
}
