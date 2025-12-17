package com.lesterade.oneira.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lesterade.oneira.GameMasterViewModel
import com.lesterade.oneira.databinding.FragmentGameBinding
import com.lesterade.oneira.ui.EndingActivity
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.ui.toolDisplayLayout.ToolDisplay
import com.lesterade.oneira.ui.toolDisplayLayout.TransmutableToolDisplay
import kotlin.math.ceil

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private var gameH: GameHandler? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val viewModel: GameMasterViewModel by activityViewModels<GameMasterViewModel>()
        val master = viewModel.master.value!!
        gameH = GameHandler(master)

        gameH?.update()
        binding.composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding.composeView.setContent { GameScreen(gameH!!) {
            name, header ->
            val int = Intent("android.intent.action.VIEW")
            int.setClass(requireContext(), EndingActivity::class.java)
            int.putExtra("com.lesterade.oneira.ending", header)
            int.putExtra("com.lesterade.oneira.ending_img", name)
            activity?.finish()
            startActivity(int)
        } }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
fun SceneCanvas(name: String, creatureName: String?, leftBar: Float, rightBar: Float, scale : Float? = null) {
    val current = LocalContext.current

    val id = current.resources.getIdentifier(name, "drawable", current.packageName)
    val background = ImageBitmap.imageResource(id)

    val creatura: ImageBitmap? = if(creatureName != null) {
        val id = current.resources.getIdentifier(creatureName, "drawable", current.packageName)

        ImageBitmap.imageResource(id)
    } else
        null

    fun DrawScope.drawInterface() {
        val left = (size.width - background.width) / 2
        val top = size.height - background.height

        drawImage(background, Offset(left, top))

        if(creatura != null) {
            val left = (size.width - creatura.width) / 2
            val top = size.height - creatura.height

            drawImage(creatura, Offset(left, top))
        }

        drawRect(Color(0xFF246D49), Offset(0f, size.height * (1f - leftBar)), Size( 10f, size.height * (leftBar)))

        drawRect(Color(0xFF246D49), Offset(size.width - 10f, size.height * (1f - rightBar)), Size(10f, size.height * (rightBar),))
    }

    Canvas(Modifier.aspectRatio(1.34f)
                    .clipToBounds(), onDraw = {

        if (scale != null)
            scale(scaleX = scale, scaleY = scale, block = DrawScope::drawInterface)
        else
            drawInterface()
    })
}

@Composable
fun GameScreen(handler: GameHandler, onGameEnd: (String, String) -> Unit) {
    val textColor = Color(0xFFB66D00)

    var tools by remember {mutableStateOf(handler.tools)}

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
        Text(head, color = textColor)
        SceneCanvas(name, creat, lb, rb, scale)
        Text(desc, color = textColor)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0..2) {
                if(tools[i].transmutable)
                    TransmutableToolDisplay(tools[i], i, {
                        handler.activateTool(it)
                        update()
                    }, {a: Int, b: Int ->
                        handler.transmuteTool(a, b)
                        update()
                    }, scale)
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

@Composable
fun MainScreen(handler: GameHandler) {
    var step = remember { mutableStateOf(2)}

    if(step.value == 2)
        GameScreen(handler, {name, header -> step.value = 0})
}