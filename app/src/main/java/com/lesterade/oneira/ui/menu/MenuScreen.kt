package com.lesterade.oneira.ui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.lesterade.oneira.IFileHandler
import com.lesterade.oneira.R
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.gameHandling.loadGame
import com.lesterade.oneira.gameHandling.saveGame

@Composable
fun MenuScreen(files: IFileHandler?, handler: GameHandler, onGameRestart: () -> Unit) {
    // val context = LocalContext.current
    val buttonColors = ButtonColors(Color(0xFF704300), Color(0xFFBDBDBD), Color(0xFF363636), Color(0xFFBDBDBD))

    Column() {
        if(files != null) {
            Button({
                loadGame(files, handler)
                handler.update()
            }, shape = RectangleShape, colors = buttonColors) {
                Text(
                    text = stringResource(
                        R.string.load_game
                    )
                )
            }
            Button({ saveGame(files, handler) }, shape = RectangleShape, colors = buttonColors) {
                Text(
                    text = stringResource(
                        R.string.save_game
                    )
                )
            }
        }
        Button(onGameRestart, shape = RectangleShape, colors = buttonColors) {
            Text(text = stringResource(
                R.string.new_game)
            )
        }
    }
}
