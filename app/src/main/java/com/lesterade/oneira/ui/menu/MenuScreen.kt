package com.lesterade.oneira.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lesterade.oneira.IFileHandler
import com.lesterade.oneira.R
import com.lesterade.oneira.gameHandling.GameHandler

@Composable
fun MenuScreen(files: IFileHandler?, handler: GameHandler, onGameRestart: () -> Unit, changeLanguage: (String) -> Unit) {
    // val context = LocalContext.current
    val buttonColors = ButtonColors(Color(0xFF704300), Color(0xFFBDBDBD), Color(0xFF363636), Color(0xFFBDBDBD))

    val loadTextVal = stringResource(R.string.load_game)
    val saveTextVal = stringResource(R.string.save_game)


    val loadText by remember { mutableStateOf(loadTextVal) }
    val saveText by remember { mutableStateOf(saveTextVal) }


    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if(files != null) {
            Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                Button(
                    {
                        handler.loadGame(files)
                        handler.update()
                    }, shape = RectangleShape, colors = buttonColors,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Image(
                                painter = painterResource(R.drawable.tape_recorder),
                                null
                            )
                            Image(
                                painter = painterResource(R.drawable.onwards),
                                null
                            )
                        }
                        Text(loadText)
                    }
                }

                Button(
                    { handler.saveGame(files) }, shape = RectangleShape, colors = buttonColors,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Image(
                                painter = painterResource(R.drawable.save),
                                null
                            )
                            Image(
                                painter = painterResource(R.drawable.backwards),
                                null
                            )
                        }
                        Text(saveText)
                    }
                }
            }
        }
        Button(onGameRestart, shape = RectangleShape, colors = buttonColors,
            contentPadding = PaddingValues(4.dp)) {
            Image(
                painter = painterResource(R.drawable.again),
                stringResource(
                    R.string.new_game)
            )

        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                {
                    handler.changeLanguage("ru")
                    changeLanguage("ru")
                },
                shape = RectangleShape,
                colors = buttonColors
            ) {
                Text(
                    text =
                        "Русский"
                )
            }

            Button(
                {
                    handler.changeLanguage("en")
                    changeLanguage("en")
                },
                shape = RectangleShape,
                colors = buttonColors
            ) {
                Text(
                    text =
                        "English"
                )
            }
        }
    }
}
