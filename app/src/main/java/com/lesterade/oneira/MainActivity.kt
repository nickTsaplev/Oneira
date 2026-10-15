package com.lesterade.oneira

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.lesterade.oneira.databinding.ActivityMainBinding
import com.lesterade.oneira.game.data.ActorFactory
import com.lesterade.oneira.gameHandling.GameHandler
import com.lesterade.oneira.gameHandling.msgFormatting.msgPatterns
import com.lesterade.oneira.game.GameMaster
import com.lesterade.oneira.game.data.LocationFactory
import com.lesterade.oneira.game.data.ToolFactory
import com.lesterade.oneira.ui.MainScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    private var gameH: GameHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: GameMasterViewModel by viewModels()

        viewModel.master.value = GameMaster()

        val language = "ru"

        ToolFactory.launch(applicationContext, language)
        ActorFactory.launch(applicationContext, language)
        LocationFactory.launch(applicationContext, language)

        if (viewModel.master.value?.gameOngoing == false)
            viewModel.master.value?.startNewGame(intent.getStringExtra("com.lesterade.oneira.charname") ?: "player")

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val master = viewModel.master.value!!
        gameH = GameHandler(msgPatterns.createFromContext(this), master)

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
