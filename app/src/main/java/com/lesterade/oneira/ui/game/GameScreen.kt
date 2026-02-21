package com.lesterade.oneira.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.ui.toolDisplayLayout.ToolDisplay
import com.lesterade.oneira.ui.toolDisplayLayout.TransmutableToolDisplay
import kotlin.math.ceil

@Composable
fun SceneCanvas(name: String, creatureName: String?, leftBar: Float, rightBar: Float, scale : Float? = null, language: String = "en") {
    val current = LocalContext.current

    var id = current.resources.getIdentifier(name, "drawable", current.packageName)

    if (name.endsWith("_")) {
        val newId = current.resources.getIdentifier(name + language, "drawable", current.packageName)
        if (newId != 0)
            id = newId
    }

    val background = ImageBitmap.imageResource(id)

    val creatura: ImageBitmap? = if(creatureName != null) {
        val id = current.resources.getIdentifier(creatureName, "drawable", current.packageName)

        ImageBitmap.imageResource(id)
    } else
        null

    fun DrawScope.drawInterface() {
        val scaleF = scale ?: 1f

        val left = (size.width - background.width) / 2
        val top = size.height - background.height

        drawImage(background, Offset(left, top))

        if(creatura != null) {
            val left = (size.width - creatura.width) / 2
            val top = size.height - creatura.height

            drawImage(creatura, Offset(left, top))
        }

        drawRect(Color(0xFF246D49), Offset(0f, size.height * (1f - leftBar)), Size( 10f * scaleF, size.height * (leftBar)))

        drawRect(Color(0xFF246D49), Offset(size.width - 10f, size.height * (1f - rightBar)), Size(10f * scaleF, size.height * (rightBar),))
    }

    Canvas(
        Modifier.aspectRatio(1.34f)
        .clipToBounds(), onDraw = {

        if (scale != null)
            scale(scaleX = scale, scaleY = scale, block = DrawScope::drawInterface)
        else
            drawInterface()
    })
}

@Composable
fun GameScreen(handler: GameHandler,
               onGameEnd: (String, String) -> Unit,
               language: String = "en") {
    val textColor = Color(0xFFB66D00)

    var tools by remember { mutableStateOf(handler.tools) }

    var msg by remember{ mutableStateOf("") }

    var lb by remember{ mutableFloatStateOf(handler.left_bar) }
    var rb by remember{ mutableFloatStateOf(handler.right_bar) }

    var head by remember{ mutableStateOf(handler.sceneHead) }
    var name by remember{ mutableStateOf(handler.sceneName) }
    var desc by remember{ mutableStateOf(handler.sceneDesc) }
    var creat by remember { mutableStateOf(handler.creatureName) }

    val configuration = LocalConfiguration.current
    val scaleVal = configuration.densityDpi / 240f

    val scale = if (scaleVal > 2f) ceil(scaleVal) else null

    val update = {
        tools = handler.tools
        msg = handler.msg

        lb = handler.left_bar
        rb = handler.right_bar

        head = handler.sceneHead
        name = handler.sceneName
        desc = handler.sceneDesc
        creat = handler.creatureName

        if(handler.ended) {
            onGameEnd(handler.sceneName, handler.sceneHead)
        }
    }
    Column {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Text(head, color = textColor)
            SceneCanvas(name, creat, lb, rb, scale, language)
            Text(desc, color = textColor)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0..2) {
                    if (tools[i].transmutable)
                        TransmutableToolDisplay(
                            tools[i], i,
                            {
                                handler.activateTool(it)
                                update()
                            }, { a: Int, b: Int ->
                                handler.transmuteTool(a, b)
                                update()
                            }, scale
                        )
                    else
                        ToolDisplay(tools[i], i, {
                            handler.activateTool(it)
                            update()
                        }, scale)
                }
            }
            Text(msg, color = textColor)
        }
    }
}
