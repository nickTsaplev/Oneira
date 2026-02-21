package com.lesterade.oneira.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.lesterade.oneira.R

@Composable
fun EndingScreen(imageName: String, desc: String, onGameStart: () -> Unit) {
    val context = LocalContext.current

    val buttonColors = ButtonColors(Color(0xFF704300), Color(0xFFBDBDBD), Color(0xFF363636), Color(0xFFBDBDBD))
    val textColor = Color(0xFFB66D00)


    val id = context.resources.getIdentifier(imageName, "drawable", context.packageName)

     Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = id),
                imageName)
            Text(text = desc, color = textColor)
            Button(onGameStart, shape = RectangleShape, colors = buttonColors) {Text(text = stringResource(R.string.start_again) )}
        }
    }

}
