package com.lesterade.oneira.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.lesterade.oneira.IFileHandler
import com.lesterade.oneira.R
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.ui.cardsDisplay.CardsDisplayScreen
import com.lesterade.oneira.ui.game.GameScreen
import com.lesterade.oneira.ui.menu.MenuScreen
import kotlinx.serialization.Serializable

@Composable
fun MainScreen(files: IFileHandler?, handler: GameHandler, language: String = "en") {
    @Serializable
    class Game() {}

    @Serializable
    class Cards() {}

    @Serializable
    class Menu() {}

    @Serializable
    data class Ending(val imageName: String, val desc: String) {}

    val buttonColors = ButtonColors(Color(0xFF704300), Color(0xFFBDBDBD), Color(0xFF363636), Color(0xFFBDBDBD))

    val navController = rememberNavController()
    Column(modifier = Modifier
        .fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button({ navController.navigate(route = Game()) },  shape = RectangleShape, colors = buttonColors) { Text(text = stringResource(
                R.string.title_game)
            ) }
            Button({ navController.navigate(route = Cards()) }, shape = RectangleShape, colors = buttonColors) { Text(text = stringResource(
                R.string.title_cards)
            ) }
            Button({ navController.navigate(route = Menu()) },  shape = RectangleShape, colors = buttonColors) { Text(text = stringResource(
                R.string.title_menu)
            ) }
        }

        NavHost(
            navController,
            startDestination = Game(),
            modifier = Modifier
                .fillMaxSize()
        ) {
            composable<Game>() { GameScreen(handler, { s: String, s1: String -> navController.navigate(route = Ending(s, s1))}, language) }
            composable<Cards>() { CardsDisplayScreen(handler) }
            composable<Menu>() { MenuScreen(files, handler) { handler.startGame()
                handler.update()
                navController.popBackStack()
                navController.navigate(route = Game())}
            }
            composable<Ending> { backStackEntry ->
                val ending: Ending = backStackEntry.toRoute()
                EndingScreen(ending.imageName, ending.desc) { handler.startGame()
                    handler.update()
                    navController.popBackStack()
                    navController.navigate(route = Game())}}
        }
    }
}