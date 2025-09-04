package com.lesterade.oneira.ui.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.AttributeSet
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lesterade.oneira.databinding.FragmentDashboardBinding
import com.lesterade.oneira.ui.EndingActivity
import com.lesterade.oneira.ui.gameHandling.GameHandler
import com.lesterade.oneira.ui.gameHandling.GameMaster
import com.lesterade.oneira.ui.gameHandling.sceneInfo
import com.lesterade.oneira.ui.toolDisplayLayout.dispTool
import com.lesterade.oneira.ui.toolDisplayLayout.toolDisplay

class TripView @JvmOverloads constructor(context: Context,
                                           attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {


    private var textHeight = 10.0f // Obtained from style attributes.

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x101010
        textSize = textHeight
    }

    private val hpPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x246D49
        alpha = 0xff
        textSize = textHeight
    }

    private val piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = textHeight
    }

    private val shadowPaint = Paint(0).apply {
        color = 0x101010
    }

    private var coordx = 0f;

    var scene = "what"
    var creatureName: String? = "costable"

    var curState = 0

    var left_bar = 0.75f
    var leftRect = Rect()

    var right_bar = 0.5f
    var rightRect = Rect()

    // Called when the view should render its content.
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // In my honest opinion, this is CLEARLY a better option,
        // considering the necessity of external configs and
        // that view redraw occurs not that often
        val id = context.resources.getIdentifier(scene, "drawable", context.packageName)

        val myImage: Drawable =
            ResourcesCompat.getDrawable(context.resources, id, null)
                ?: return

        val left = (width - myImage.intrinsicWidth) / 2
        val top = height - myImage.intrinsicHeight
        myImage.setBounds(left, top, left + myImage.intrinsicWidth, top + myImage.intrinsicHeight)
        myImage.draw(canvas)

        if(creatureName != null) {
            val id = context.resources.getIdentifier(creatureName, "drawable", context.packageName)

            val myImage: Drawable =
                ResourcesCompat.getDrawable(context.resources, id, null)
                    ?: return

            val left = (width - myImage.intrinsicWidth) / 2
            val top = height - myImage.intrinsicHeight
            myImage.setBounds(left, top, left + myImage.intrinsicWidth, top + myImage.intrinsicHeight)
            myImage.draw(canvas)
        }

        leftRect.set(0, (height.toFloat() * (1f - left_bar)).toInt(), 10, height)
        canvas.drawRect(leftRect, hpPaint)

        rightRect.set(width - 10, (height.toFloat() * (1f - right_bar)).toInt(), width, height)
        canvas.drawRect(rightRect, hpPaint)

        
        // DRAW STUFF HERE
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        if (widthMeasureSpec < heightMeasureSpec) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
            return
        }

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)


        setMeasuredDimension(w, (w * 3) / 4);
    }
}

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //private val tripView get() = binding.canvasDashboard

    private var gameH: GameHandler? = GameHandler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val buttonOne = binding.tripButton1
        //buttonOne.setOnClickListener({v: View -> tripView.curState = 1
        //tripView.invalidate()})

        gameH = GameHandler()
        //gameH?.tools = mutableListOf(binding.td1.main, binding.td2.main, binding.td3.main)
        /*gameH?.tripV = binding.canvasDashboard
        gameH?.frag = this

        binding.td1.main.handler = gameH
        binding.td1.main.clickId = 0

        binding.td2.main.handler = gameH
        binding.td2.main.clickId = 1

        binding.td3.main.handler = gameH
        binding.td3.main.clickId = 2

        gameH?.update()*/
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

    fun setMessage(msg: String) {
        //binding.textLog.text = msg
    }

    fun updateScene() {
        //binding.textHeader.text = gameH!!.sceneHead
        //binding.textDesc.text = gameH!!.sceneDesc
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //gameH = null
        _binding = null
    }
}

@Composable
fun SceneCanvas(name: String, creatureName: String?, left_bar: Float, right_bar: Float) {
    val current = LocalContext.current

    val id = current.resources.getIdentifier(name, "drawable", current.packageName)
    val background = ImageBitmap.imageResource(id)

    val creatura: ImageBitmap? = if(creatureName != null) {
        val id = current.resources.getIdentifier(creatureName, "drawable", current.packageName)

        ImageBitmap.imageResource(id)
    } else
        null

    Canvas(Modifier.aspectRatio(1.34f)
                    .clipToBounds(), onDraw = {

        val left = (size.width - background.width) / 2
        val top = size.height - background.height

        drawImage(background, Offset(left, top))

        if(creatura != null) {
            val left = (size.width - creatura.width) / 2
            val top = size.height - creatura.height

            drawImage(creatura, Offset(left, top))
        }

        drawRect(Color(0xFF246D49), Offset(0f, size.height * (1f - left_bar)), Size( 10f, size.height * (left_bar)))

        drawRect(Color(0xFF246D49), Offset(size.width - 10f, size.height * (1f - right_bar)), Size(10f, size.height * (right_bar),))
    })
}

@Composable
fun GameScreen(handler: GameHandler, onGameEnd: (String, String) -> Unit) {
    val textColor = Color(0xFFB66D00)

    var tools by remember {mutableStateOf(handler.tools)}

    var msg by remember{ mutableStateOf("") }

    var lb by remember{ mutableFloatStateOf(handler.left_bar) }
    var rb by remember{ mutableFloatStateOf(handler.left_bar) }

    var head by remember{ mutableStateOf(handler.sceneHead) }
    var name by remember{ mutableStateOf(handler.sceneName) }
    var desc by remember{ mutableStateOf(handler.sceneDesc) }
    var creat by remember { mutableStateOf(handler.creatureName) }

    Column {
        Text(head, color = textColor)
        SceneCanvas(name, creat, lb, rb)
        Text(desc, color = textColor)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0..2) {
                toolDisplay(tools[i], i) {
                    handler.activateTool(it)
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

    //if(step.value == 0)

}