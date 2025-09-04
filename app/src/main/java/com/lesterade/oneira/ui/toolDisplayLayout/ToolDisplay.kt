package com.lesterade.oneira.ui.toolDisplayLayout

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lesterade.oneira.ui.gameHandling.GameHandler
import com.lesterade.oneira.R
import com.lesterade.oneira.ui.gameHandling.instrument

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*

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
        loadTool(tl.imageId, tl.header, tl.description)
    }
}

data class dispTool(var name: String = "none", var header: String = "", var desc: String = "")

@Composable
fun toolDisplay(disp: dispTool, clickId: Int, onClk: (Int) -> Unit) {
    val current = LocalContext.current
    val id = current.resources.getIdentifier(disp.name, "drawable", current.packageName)

    Box(Modifier.clickable{onClk(clickId)}.fillMaxWidth()) {
        Row {
            Image(
                painter = painterResource(id = id),
                disp.header
            )
            Column {
                Text(disp.header, color = Color(0xFFB66D00))
                Text(disp.desc, color = Color(0xFFB66D00))
            }
        }
    }
}