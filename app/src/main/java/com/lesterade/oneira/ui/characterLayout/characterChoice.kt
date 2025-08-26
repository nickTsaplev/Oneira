package com.lesterade.oneira.ui.characterLayout

import android.content.Intent
import com.lesterade.oneira.MainActivity
import com.lesterade.oneira.ui.EndingActivity
import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lesterade.oneira.ui.gameHandling.GameHandler
import com.lesterade.oneira.R
import com.lesterade.oneira.ui.StartingActivity
import com.lesterade.oneira.ui.gameHandling.instrument

class CharacterChoice(context: Context, attrs: AttributeSet?): ConstraintLayout(context, attrs) {
    var clickId = 0
    var name = ""

    fun loadChar(nameSet: String, desc: String) {
        name = nameSet
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        findViewById<ImageView>(R.id.imageView).setImageResource(id)

        findViewById<TextView>(R.id.textViewHead).text = desc

        findViewById<Button>(R.id.button).setOnClickListener{
            val int = Intent("android.intent.action.VIEW")
            int.setClass(context, MainActivity::class.java)
            int.putExtra("com.lesterade.oneira.charname", name)

            (context as StartingActivity).finish()
            context.startActivity(int)
        }
    }
}