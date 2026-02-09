package com.lesterade.oneira

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.lesterade.oneira.databinding.ActivityMainBinding
import com.lesterade.oneira.gameHandling.weapons.ToolFactory
import com.lesterade.oneira.gameHandling.ActorFactory
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.gameHandling.msgFormatting.msgPatterns
import com.lesterade.oneira.gameHandling.GameMaster
import com.lesterade.oneira.gameHandling.biomes.LocationFactory
import com.lesterade.oneira.ui.MainScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val language = "ru"

    private var gameH: GameHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: GameMasterViewModel by viewModels()

        viewModel.master.value = GameMaster(msgPatterns(this))

        ToolFactory.launch(applicationContext, language)
        ActorFactory.launch(applicationContext, language)
        LocationFactory.launch(applicationContext, language)

        viewModel.master.value?.startNewGame(intent.getStringExtra("com.lesterade.oneira.charname") ?: "player")

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val master = viewModel.master.value!!
        gameH = GameHandler(master)

        gameH?.update()
        binding.composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        binding.composeView.setContent { MainScreen(AndroidFileHandler(this), gameH!!, language) }

        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameH = null
    }
}
