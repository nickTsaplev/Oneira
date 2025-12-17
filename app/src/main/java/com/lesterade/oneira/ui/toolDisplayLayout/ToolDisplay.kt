package com.lesterade.oneira.ui.toolDisplayLayout

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.R
import com.lesterade.oneira.gameHandling.weapons.Instrument

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.sp
import com.lesterade.oneira.gameHandling.dispTool

@Composable
fun ToolDisplay(disp: dispTool, clickId: Int, onClk: (Int) -> Unit, scale: Float? = null) {
    val current = LocalContext.current
    var id = current.resources.getIdentifier(disp.name, "drawable", current.packageName)

    if(id == 0)
        id = R.drawable.onwards

    Surface (
        color = Color(0xFF151111)
    ) {
        Box(Modifier.clickable{onClk(clickId)}.fillMaxWidth()) {
        Row {
            Image(
                painter = painterResource(id = id),
                disp.header,
                contentScale = if (scale == null) ContentScale.Fit else FixedScale(scale)
            )
            Column {
                Text(disp.header, color = Color(0xFFB66D00))
                Text(disp.desc, color = Color(0xFFB66D00), fontSize = 14.sp)
            }
        }
    }}
}

@Composable
fun TransmutableToolDisplay(disp: dispTool, clickId: Int, onClk: (Int) -> Unit, onTrms: (Int, Int) -> Unit,  scale: Float? = null) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd)
                onTrms(clickId, 1)
            else if (it == SwipeToDismissBoxValue.EndToStart)
                onTrms(clickId, -1)
            // Reset item when toggling done status
            false
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = {
            when(swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Image(
                    painter = painterResource(R.drawable.transmute_main),
                    "transmute",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.CenterStart),
                    contentScale = if (scale == null) ContentScale.Fit else FixedScale(scale))
                SwipeToDismissBoxValue.EndToStart -> Image(
                    painter = painterResource(R.drawable.transmute_side),
                    "transmute",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.CenterEnd),
                    contentScale = if (scale == null) ContentScale.Fit else FixedScale(scale))
                SwipeToDismissBoxValue.Settled -> {}
            }
        }
    ) {
        ToolDisplay(disp, clickId, onClk)
    }
}