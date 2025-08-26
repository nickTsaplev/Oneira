package com.lesterade.oneira.ui.toolDisplayLayout

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lesterade.oneira.ui.gameHandling.GameHandler
import com.lesterade.oneira.R
import com.lesterade.oneira.ui.gameHandling.instrument

class ToolDisplay(context: Context, attrs: AttributeSet?): ConstraintLayout(context, attrs) {
    var clickId = 0
    var handler: GameHandler? = null

    init {
        setOnClickListener({
            handler?.activateTool(clickId)
        })
    }

    fun loadTool(name: String, header: String, desc: String) {
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        findViewById<ImageView>(R.id.imageView).setImageResource(id)

        findViewById<TextView>(R.id.textViewHead).text = header
        findViewById<TextView>(R.id.textViewDesc).text = desc

    }

    fun loadTool(tl: instrument) {
        loadTool(tl.imageId, tl.header, tl.desc)
    }
}